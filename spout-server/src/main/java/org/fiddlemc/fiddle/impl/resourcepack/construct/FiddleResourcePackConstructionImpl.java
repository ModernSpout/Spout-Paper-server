package org.fiddlemc.fiddle.impl.resourcepack.construct;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventRunner;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.PrioritizableLifecycleEventType;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructFinishEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstruction;
import spout.server.paper.impl.configuration.SpoutGlobalConfiguration;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockRegistry;
import org.fiddlemc.fiddle.impl.resourcepack.plugin.discover.FiddlePluginResourcePackDiscoveryImpl;
import org.fiddlemc.fiddle.impl.resourcepack.send.FiddleResourcePackSending;
import org.fiddlemc.fiddle.impl.resourcepack.serve.FiddleResourcePackServing;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The implementation for {@link FiddleResourcePackConstruction}.
 */
public final class FiddleResourcePackConstructionImpl extends ComposableImpl<FiddleResourcePackConstructEvent, FiddleResourcePackConstructEventImpl> implements FiddleResourcePackConstruction {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<FiddleResourcePackConstruction, FiddleResourcePackConstructionImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(FiddleResourcePackConstructionImpl.class);
        }

    }

    public static FiddleResourcePackConstructionImpl get() {
        return (FiddleResourcePackConstructionImpl) FiddleResourcePackConstruction.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_resource_pack_construction";
    }

    private FiddleResourcePackConstructionImpl() {
    }

    @Override
    protected FiddleResourcePackConstructEventImpl createComposeEvent() {
        // Create the event
        FiddleResourcePackConstructEventImpl event = new FiddleResourcePackConstructEventImpl();
        // Add default contents
        for (String path : DEFAULT_RESOURCE_PACK_CONTENTS_PATHS) {
            try {
                event.copyPluginResource((Class) this.getClass(), Arrays.stream(ClientView.AwarenessLevel.getAll()).filter(FiddleResourcePackConstructionImpl::generateForAwarenessLevel).toList(), "default_resource_pack_contents/" + path, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Add included plugin contents
        FiddlePluginResourcePackDiscoveryImpl.get().getProvidingPluginsByResourcePackFileRelativePath().forEach(pair -> {
            List<Pair<PluginBootstrap, String>> providingPlugins = pair.right();
            if (providingPlugins.isEmpty()) {
                return;
            }
            Pair<PluginBootstrap, String> providingPlugin = providingPlugins.getLast();
            String pathInResourcePack = pair.left();
            // Filter out language files
            if (isLangFile(pathInResourcePack)) {
                return;
            }
            // Filter out non-vanilla blockstates for vanilla clients
            boolean isNonVanillaBlockstates;
            Matcher blockstatesMatcher = BLOCKSTATES_FILE_PATTERN.matcher(pathInResourcePack);
            if (blockstatesMatcher.find()) {
                try {
                    String namespace = blockstatesMatcher.group(1);
                    String path = blockstatesMatcher.group(2);
                    Identifier identifier = Identifier.fromNamespaceAndPath(namespace, path);
                    Optional<Block> optionalBlock = BlockRegistry.get().getOptional(identifier);
                    if (optionalBlock.isEmpty()) {
                        // The block doesn't exist, don't include it
                    }
                    Block block = optionalBlock.get();
                    isNonVanillaBlockstates = !block.isVanilla();
                } catch (Exception ignored) {
                    // Something is weird, let's assume we shouldn't include it
                    isNonVanillaBlockstates = true;
                }
            } else {
                // Not a blockstates file
                isNonVanillaBlockstates = false;
            }
            ClientView.AwarenessLevel[] awarenessLevels = isNonVanillaBlockstates ? new ClientView.AwarenessLevel[]{ClientView.AwarenessLevel.CLIENT_MOD} : new ClientView.AwarenessLevel[]{ClientView.AwarenessLevel.RESOURCE_PACK, ClientView.AwarenessLevel.CLIENT_MOD};
            try {
                event.copyPluginResource(providingPlugin.left(), awarenessLevels, (providingPlugin.right().isEmpty() ? "" : providingPlugin.right() + "/") + pathInResourcePack, pathInResourcePack);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Return the event
        return event;
    }

    @Override
    protected void copyInformationFromEvent(final FiddleResourcePackConstructEventImpl event) {
        // Build the pack contents
        Map<ClientView.AwarenessLevel, byte[]> packBytes;
        try {
            packBytes = event.buildPacks();
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while constructing the server resource pack", e);
        }
        // Create pack instances
        Map<ClientView.AwarenessLevel, FiddleConstructedResourcePackImpl> packs = new EnumMap<>(ClientView.AwarenessLevel.class);
        for (Map.Entry<ClientView.AwarenessLevel, byte[]> entry : packBytes.entrySet()) {
            packs.put(entry.getKey(), new FiddleConstructedResourcePackImpl(entry.getKey(), entry.getValue()));
        }
        // Call plugins
        LifecycleEventRunner.INSTANCE.callEvent(this.finish(), new FiddleResourcePackConstructFinishEventImpl(packs));
        // Get the packs to pass to built-in output use cases
        FiddleConstructedResourcePackImpl vanillaPack = packs.get(ClientView.AwarenessLevel.RESOURCE_PACK);
        FiddleConstructedResourcePackImpl clientModPack = packs.get(ClientView.AwarenessLevel.CLIENT_MOD);
        // TODO save to file if enabled
        // Set up the HTTP serving
        if (FiddleResourcePackServing.isEnabled()) {
            FiddleResourcePackServing.start(vanillaPack, clientModPack);
        }
        // Initialize the packet sending
        FiddleResourcePackSending.initialize(vanillaPack, clientModPack);
    }

    /**
     * Whether constructing the resource pack is enabled.
     */
    public boolean isEnabled() {
        return SpoutGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.enabled;
    }

    private static class FiddleResourcePackConstructFinishEventType extends PrioritizableLifecycleEventType.Simple<BootstrapContext, FiddleResourcePackConstructFinishEvent> {

        public FiddleResourcePackConstructFinishEventType() {
            super("fiddle_resource_pack_construction_finish", BootstrapContext.class);
        }

    }

    /**
     * The cached return value of {@link #finish()},
     * or null if not cached yet.
     */
    private @Nullable FiddleResourcePackConstructFinishEventType finishEventType;

    @Override
    public LifecycleEventType<BootstrapContext, FiddleResourcePackConstructFinishEvent, PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext>> finish() {
        if (this.finishEventType == null) {
            this.finishEventType = new FiddleResourcePackConstructFinishEventType();
        }
        return this.finishEventType;
    }

    public static boolean generateForAwarenessLevel(ClientView.AwarenessLevel awarenessLevel) {
        return awarenessLevel != ClientView.AwarenessLevel.VANILLA;
    }

    private static final String[] DEFAULT_RESOURCE_PACK_CONTENTS_PATHS = {
        "pack.mcmeta",
        "assets/fiddle/models/block/bevel_middle.json",
        "assets/fiddle/models/block/bevel_nnnnnnnn.json",
        "assets/fiddle/models/block/bevel_nnnynnyn.json",
        "assets/fiddle/models/block/bevel_nnnyynnn.json",
        "assets/fiddle/models/block/bevel_nnynnnnn.json",
        "assets/fiddle/models/block/bevel_nnyynnnn.json",
        "assets/fiddle/models/block/bevel_nnyynnyn.json",
        "assets/fiddle/models/block/bevel_nnyynnyy.json",
        "assets/fiddle/models/block/bevel_nnyynynn.json",
        "assets/fiddle/models/block/bevel_nnyynyyn.json",
        "assets/fiddle/models/block/bevel_nnyyynnn.json",
        "assets/fiddle/models/block/bevel_nnyyynyn.json",
        "assets/fiddle/models/block/bevel_nnyyyynn.json",
        "assets/fiddle/models/block/bevel_nnyyyyyn.json",
        "assets/fiddle/models/block/bevel_nynnynnn.json",
        "assets/fiddle/models/block/bevel_nynynnyn.json",
        "assets/fiddle/models/block/bevel_nynyynnn.json",
        "assets/fiddle/models/block/bevel_nynyynyn.json",
        "assets/fiddle/models/block/bevel_nyynnnnn.json",
        "assets/fiddle/models/block/bevel_nyynnynn.json",
        "assets/fiddle/models/block/bevel_nyynynnn.json",
        "assets/fiddle/models/block/bevel_nyynyynn.json",
        "assets/fiddle/models/block/bevel_nyyynnnn.json",
        "assets/fiddle/models/block/bevel_nyyynnyn.json",
        "assets/fiddle/models/block/bevel_nyyynynn.json",
        "assets/fiddle/models/block/bevel_nyyynyyn.json",
        "assets/fiddle/models/block/bevel_nyyyynnn.json",
        "assets/fiddle/models/block/bevel_nyyyynyn.json",
        "assets/fiddle/models/block/bevel_nyyyynyy.json",
        "assets/fiddle/models/block/bevel_nyyyyynn.json",
        "assets/fiddle/models/block/bevel_nyyyyyyn.json",
        "assets/fiddle/models/block/bevel_ynnnnnnn.json",
        "assets/fiddle/models/block/bevel_ynnynnnn.json",
        "assets/fiddle/models/block/bevel_ynnynnyn.json",
        "assets/fiddle/models/block/bevel_ynnynyyn.json",
        "assets/fiddle/models/block/bevel_ynnyynnn.json",
        "assets/fiddle/models/block/bevel_ynnyynyn.json",
        "assets/fiddle/models/block/bevel_ynynnnnn.json",
        "assets/fiddle/models/block/bevel_ynyynnnn.json",
        "assets/fiddle/models/block/bevel_ynyynnyn.json",
        "assets/fiddle/models/block/bevel_ynyynnyy.json",
        "assets/fiddle/models/block/bevel_ynyynynn.json",
        "assets/fiddle/models/block/bevel_ynyynyyn.json",
        "assets/fiddle/models/block/bevel_ynyyynnn.json",
        "assets/fiddle/models/block/bevel_ynyyynyn.json",
        "assets/fiddle/models/block/bevel_ynyyyynn.json",
        "assets/fiddle/models/block/bevel_ynyyyyyn.json",
        "assets/fiddle/models/block/bevel_yynnnnnn.json",
        "assets/fiddle/models/block/bevel_yynnynnn.json",
        "assets/fiddle/models/block/bevel_yynnyynn.json",
        "assets/fiddle/models/block/bevel_yynynnnn.json",
        "assets/fiddle/models/block/bevel_yynynnyn.json",
        "assets/fiddle/models/block/bevel_yynynyyn.json",
        "assets/fiddle/models/block/bevel_yynyynnn.json",
        "assets/fiddle/models/block/bevel_yynyynyn.json",
        "assets/fiddle/models/block/bevel_yynyyyyn.json",
        "assets/fiddle/models/block/bevel_yyynnnnn.json",
        "assets/fiddle/models/block/bevel_yyynnynn.json",
        "assets/fiddle/models/block/bevel_yyynynnn.json",
        "assets/fiddle/models/block/bevel_yyynyynn.json",
        "assets/fiddle/models/block/bevel_yyyynnnn.json",
        "assets/fiddle/models/block/bevel_yyyynnyn.json",
        "assets/fiddle/models/block/bevel_yyyynnyy.json",
        "assets/fiddle/models/block/bevel_yyyynynn.json",
        "assets/fiddle/models/block/bevel_yyyynyyn.json",
        "assets/fiddle/models/block/bevel_yyyyynnn.json",
        "assets/fiddle/models/block/bevel_yyyyynyn.json",
        "assets/fiddle/models/block/bevel_yyyyynyy.json",
        "assets/fiddle/models/block/bevel_yyyyyynn.json",
        "assets/fiddle/models/block/bevel_yyyyyyyn.json",
        "assets/fiddle/models/block/bevel_yyyyyyyy.json",
        "assets/fiddle/models/block/top_half_texture_bottom_slab.json",
    };

    private static boolean isLangFile(String pathInResourcePack) {
        return pathInResourcePack.matches("assets/minecraft/lang/[^/]+\\.json");
    }

    private static final Pattern BLOCKSTATES_FILE_PATTERN = Pattern.compile("assets/([^/]+)/blockstates/([^/]+)\\.json");

}
