package org.fiddlemc.fiddle.impl.resourcepack.plugin.discover;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.Identifier;
import org.fiddlemc.fiddle.api.resourcepack.content.Blockstates;
import org.fiddlemc.fiddle.api.resourcepack.content.Lang;
import org.fiddlemc.fiddle.api.resourcepack.plugin.discover.FiddlePluginResourcePackDiscoverEvent;
import org.fiddlemc.fiddle.api.resourcepack.plugin.discover.FiddlePluginResourcePackDiscovery;
import org.fiddlemc.fiddle.impl.packetmapping.component.translatable.MinecraftLocaleUtil;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The implementation for {@link FiddlePluginResourcePackDiscovery}.
 */
public final class FiddlePluginResourcePackDiscoveryImpl extends ComposableImpl<FiddlePluginResourcePackDiscoverEvent, FiddlePluginResourcePackDiscoverEventImpl> implements FiddlePluginResourcePackDiscovery {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<FiddlePluginResourcePackDiscovery, FiddlePluginResourcePackDiscoveryImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(FiddlePluginResourcePackDiscoveryImpl.class);
        }

    }

    public static FiddlePluginResourcePackDiscoveryImpl get() {
        return (FiddlePluginResourcePackDiscoveryImpl) FiddlePluginResourcePackDiscovery.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_plugin_resource_pack_discovery";
    }

    /**
     * A map from each {@link PluginBootstrap} to a pair of
     * the {@linkplain BootstrapContext#getPluginSource() plugin source}
     * and the path inside the JAR.
     */
    Map<PluginBootstrap, Pair<Path, String>> resourcePackPathsByPlugin = new LinkedHashMap<>();

    /**
     * A map from each relative path (to the root of the resource pack) to a list of pairs of the
     * {@link PluginBootstrap} that includes that file, and the path within the plugin JAR of its resource pack files.
     */
    Map<String, List<Pair<PluginBootstrap, String>>> providingPluginsByResourcePackFileRelativePath = new LinkedHashMap<>();

    private final Map<Identifier, Blockstates> cachedBlockstates = new HashMap<>();

    private FiddlePluginResourcePackDiscoveryImpl() {
    }

    @Override
    protected FiddlePluginResourcePackDiscoverEventImpl createComposeEvent() {
        return new FiddlePluginResourcePackDiscoverEventImpl();
    }

    public @Nullable String getIncludedResourcePackPath(PluginBootstrap pluginBootstrap) {
        return this.resourcePackPathsByPlugin.get(pluginBootstrap).right();
    }

    public List<Pair<PluginBootstrap, Pair<Path, String>>> getIncludedResourcePackPathsByPlugin() {
        return this.resourcePackPathsByPlugin.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList();
    }

    public List<Pair<String, List<Pair<PluginBootstrap, String>>>> getProvidingPluginsByResourcePackFileRelativePath() {
        return this.providingPluginsByResourcePackFileRelativePath.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList();
    }

    public Blockstates getResourcePackBlockstates(Identifier blockIdentifier) {
        return this.cachedBlockstates.computeIfAbsent(blockIdentifier, identifier -> {
            String relativePath = "assets/" + identifier.getNamespace() + "/blockstates/" + identifier.getPath() + ".json";
            List<Pair<PluginBootstrap, String>> pluginBootstrapAndResourcePackPaths = this.providingPluginsByResourcePackFileRelativePath.get(relativePath);
            if (pluginBootstrapAndResourcePackPaths == null || pluginBootstrapAndResourcePackPaths.isEmpty()) {
                throw new IllegalStateException("Missing blockstates file from resource pack: " + relativePath);
            }
            Pair<PluginBootstrap, String> pluginBootstrapAndResourcePackPath = pluginBootstrapAndResourcePackPaths.getLast();
            String path = pluginBootstrapAndResourcePackPath.right() + "/" + relativePath;
            try {
                byte[] bytes = pluginBootstrapAndResourcePackPath.left().getClass().getClassLoader().getResourceAsStream(path).readAllBytes();
                String string = new String(bytes, StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(string);
                return Blockstates.ofMutable(jsonElement.getAsJsonObject());
            } catch (Exception e) {
                throw new RuntimeException("Missing or invalid blockstates file in plugin (" + pluginBootstrapAndResourcePackPath.left().getClass().getName() + ") resource pack: " + path, e);
            }
        });
    }

    public List<Pair<PluginBootstrap, List<Pair<MinecraftLocaleUtil.KnownLocale, Lang>>>> getResourcePackLangs() {
        Map<PluginBootstrap, List<Pair<MinecraftLocaleUtil.KnownLocale, Lang>>> langs = new LinkedHashMap<>();
        this.providingPluginsByResourcePackFileRelativePath.forEach((pathInResourcePack, providingPlugins) -> {
            if (providingPlugins.isEmpty()) return;
            Matcher langMatcher = LANG_FILE_PATTERN.matcher(pathInResourcePack);
            if (!langMatcher.find()) {
                return;
            }
            try {
                String locale = langMatcher.group(1);
                MinecraftLocaleUtil.KnownLocale knownLocale = MinecraftLocaleUtil.getKnownLocale(locale);
                if (knownLocale == null) {
                    // Skip unknown locales
                    return;
                }
                for (Pair<PluginBootstrap, String> providingPlugin : providingPlugins) {
                    try {
                        InputStream stream = providingPlugin.left().getClass().getClassLoader().getResourceAsStream((providingPlugin.right().isEmpty() ? "" : providingPlugin.right() + "/") + pathInResourcePack);
                        if (stream != null) {
                            Reader reader = new InputStreamReader(stream);
                            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                            Lang lang = Lang.wrap(json);
                            langs.computeIfAbsent(providingPlugin.left(), $ -> new ArrayList<>(1)).add(Pair.of(knownLocale, lang));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Exception while including resource pack translations from plugin " + providingPlugin.left().getClass().getName() + " resource pack file " + pathInResourcePack, e);
                    }
                }
            } catch (Exception ignored) {
                // Something is weird, let's assume we shouldn't include it
            }
        });
        return langs.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).toList();
    }

    private static final Pattern LANG_FILE_PATTERN = Pattern.compile("assets/minecraft/lang/([^/]+)\\.json");

}
