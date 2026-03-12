package org.fiddlemc.testplugin;

import com.google.gson.JsonParser;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.FiddleEvents;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.testplugin.data.PluginBlockTypes;
import org.fiddlemc.testplugin.data.PluginItemTypes;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class TestPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        loadIncludedDataPack(context);
        addCustomBlocks(context);
        addCustomItems(context);
        setBasicBlockMappings(context);
        setComplexBlockMappings(context);
        setBasicItemMappings(context);
        setComplexItemMappings(context);
        setTranslations(context);
        configureResourcePack(context);
        customizeEnumNameForAnItem(context);
    }

    /**
     * Makes sure the included data pack is loaded.
     * It contains crafting recipes for the custom items we add.
     */
    private void loadIncludedDataPack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, event -> {
            try {
                event.registrar().discoverPack(this.getClass().getResource("/data_pack").toURI(), "provided");
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Adds our custom blocks.
     *
     * <p>
     * We register the following blocks:
     * <ul>
     *     <li><code>example:ash_block</code></li>
     *     <li><code>example:ash_stairs</code></li>
     * </ul>
     * We add some server-side properties to <code>example:ash_block</code> as a demo.
     * </p>
     */
    private void addCustomBlocks(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK, event -> {

            event.registry().register(TypedKey.create(RegistryKey.BLOCK, Key.key("example:ash_block")), builder -> {
                builder
                    .mapColor(BlockType.LIGHT_GRAY_WOOL) // It shows up light gray on maps
                    .strength(0.5f) // It breaks like sand
                    .pushReaction(PistonMoveReaction.BREAK) // It breaks when pushed by a piston
                    .requiresCorrectToolForDrops(); // It drops nothing unless broken with the right tool (a shovel, as defined in the included data pack)
            });

            event.registry().register(TypedKey.create(RegistryKey.BLOCK, Key.key("example:ash_stairs")), builder -> {
                builder
                    .inheritsFromStairs(PluginBlockTypes.ASH_BLOCK.get()) // It's a stair block
                    // Add the same properties as above
                    .mapColor(BlockType.LIGHT_GRAY_WOOL)
                    .strength(0.5f)
                    .pushReaction(PistonMoveReaction.BREAK)
                    .requiresCorrectToolForDrops();
            });

        });
    }

    /**
     * Adds our custom items.
     *
     * <p>
     * We register the following items:
     * <ul>
     *     <li><code>example:ash</code></li>
     *     <li><code>example:ash_block</code></li>
     *     <li><code>example:ash_stairs</code></li>
     * </ul>
     * We add some server-side properties to <code>example:ash_block</code> as a demo.
     * </p>
     */
    private void addCustomItems(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM, event -> {

            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("example:ash")), builder -> {
            });

            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("example:ash_block")), builder ->
                builder.inheritsFromBlock() // It's a block item
                    .stacksTo(32) // It stacks to 32
                    .fireResistant() // It is resistant to fire
                    .craftRemainder(PluginItemTypes.ASH.get()) // It leaves ash when used in a crafting recipe
            );

            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("example:ash_stairs")), builder ->
                builder.inheritsFromBlock() // It's a block item
            );

        });
    }

    /**
     * Configures the basic server-to-client mappings for blocks,
     * with a simple syntax.
     *
     * <p>
     * This maps our custom blocks,
     * and also maps the vanilla {@code minecraft:birch_leaves} to {@code minecraft:copper_grate},
     * to show that you can also map vanilla blocks.
     * </p>
     */
    private void setBasicBlockMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK_MAPPING, event -> {

            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.fromEveryStateOf(PluginBlockTypes.ASH_BLOCK.get());
                builder.toDefaultStateOf(BlockType.LIGHT_GRAY_CONCRETE_POWDER);
            });
            event.registerStateToState(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(), PluginBlockTypes.ASH_STAIRS.get(), BlockType.ANDESITE_STAIRS);
            event.registerStateToState(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(), PluginBlockTypes.MAPLE_LEAVES.get(), BlockType.CHERRY_LEAVES);

            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.fromEveryStateOf(BlockType.BIRCH_LEAVES);
                builder.toDefaultStateOf(BlockType.WAXED_COPPER_GRATE);
            });

            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.ASH_BLOCK.get());
                builder.toDefaultStateOf(BlockType.NOTE_BLOCK);
            });

        });
    }

    /**
     * Configures more customized server-to-client mappings for blocks,
     * as a demo that this is possible when needed.
     *
     * <p>
     * We make gras followed a tiled pattern with moss by making its mapping depend on the coordinates.
     * </p>
     */
    private void setComplexBlockMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK_MAPPING, event -> {
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(BlockType.GRASS_BLOCK.createBlockData());
                builder.to(handle -> {
                    if (!handle.getContext().isStateOfPhysicalBlockInWorld()) return;
                    int coordinatesXor = handle.getContext().getPhysicalBlockX() ^ handle.getContext().getPhysicalBlockY() ^ handle.getContext().getPhysicalBlockZ();
                    if ((coordinatesXor & 1) == 0) return;
                    handle.set(BlockType.MOSS_BLOCK.createBlockData());
                }, true);
            });
        });
    }

    /**
     * Configures the basic server-to-client mappings for items,
     * with a simple syntax.
     *
     * <p>
     * This maps our custom items,
     * and also maps the vanilla {@code minecraft:iron_axe} to {@code minecraft:echo_shard},
     * to show that you can also map vanilla items.
     * </p>
     */
    private void setBasicItemMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM_MAPPING, event -> {

            event.register(builder -> {
                builder.from(PluginItemTypes.ASH.get());
                builder.to(ItemType.GUNPOWDER);
            });
            event.register(builder -> {
                builder.from(PluginItemTypes.ASH_BLOCK.get());
                builder.to(ItemType.LIGHT_GRAY_CONCRETE_POWDER);
            });
            event.register(builder -> {
                builder.from(PluginItemTypes.ASH_STAIRS.get());
                builder.to(ItemType.ANDESITE_STAIRS);
            });

            event.register(builder -> {
                builder.from(ItemType.IRON_AXE);
                builder.to(ItemType.ECHO_SHARD);
            });

        });
    }

    /**
     * Configures more customized server-to-client mappings for items,
     * as a demo that this is possible when needed.
     *
     * <p>
     * We add the following mappings:
     * <ul>
     *     <li>We add extra lore to a vanilla item, <code>example:crafting_table</code></li>
     *     <li>We also change some random stuff of one of our custom items, <code>example:ash_stairs</code></li>
     * </ul>
     * </p>
     */
    private void setComplexItemMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM_MAPPING, event -> {

            event.register(builder -> {
                builder.everyAwarenessLevel();
                builder.from(ItemType.CRAFTING_TABLE);
                builder.to(handle -> {
                    var newLines = Stream.of(
                        Component.text("This is a very important block for beginners!"),
                        Component.text("For example, it can be used to craft ").append(Component.translatable(PluginItemTypes.ASH_BLOCK.get()))
                    ).map(line -> line.decoration(TextDecoration.ITALIC, false).color(TextColor.color(5526612))).toList();
                    var lore = handle.getImmutable().lore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }
                    lore.addAll(newLines);
                    handle.getMutable().lore(lore);
                });
            });

            event.register(builder -> {
                builder.from(PluginItemTypes.ASH_STAIRS.get());
                builder.to(handle -> {
                    handle.getMutable().editMeta(meta -> {
                        meta.itemName(meta.itemName().decorate(TextDecoration.BOLD));
                        meta.setEnchantmentGlintOverride(true); // Give it an enchantment glint
                    });
                });
            });

        });
    }

    /**
     * Configures the translations of names of blocks and items.
     *
     * <p>
     * We set a generic name for our custom blocks and items,
     * as well as a Japanese name for <code>example:ash</code> that will be visible to clients
     * that use Japanese as their display language.
     * </p>
     *
     * <p>
     * We can also use this to change existing vanilla texts.
     * To demonstrate this, we change the name of <code>minecraft:bookshelf</code>.
     * </p>
     */
    private void setTranslations(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.SERVER_SIDE_TRANSLATION, event -> {

            event.register(PluginItemTypes.ASH.get().translationKey(), "Ash");
            event.register(PluginItemTypes.ASH_BLOCK.get().translationKey(), "Ash block");
            event.register(PluginItemTypes.ASH_STAIRS.get().translationKey(), "Ash stairs");

            event.register(PluginItemTypes.ASH.get().translationKey(), "灰", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

            event.register(BlockType.BOOKSHELF.translationKey(), "Booky Bookshelf");

        });
    }

    private void configureResourcePack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.RESOURCE_PACK, event -> {
            event.getAssetPath(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", BlockType.BOOKSHELF.getKey(), "json").setJsonObjectMutable(JsonParser.parseString("""
            {
              "variants": {
                "": {
                  "model": "minecraft:block/purple_wool"
                }
              }
            }
            """).getAsJsonObject());
        });
    }

    /**
     * Sets a custom enum name <code>ASHES_TO_DUST</code> for the <code>example:ash</code> item,
     * purely as a demo.
     *
     * <p>
     * This is just intended as a demo that this is possible.
     * Please don't do this yourself unless you really know what you are doing!
     * </p>
     */
    private void customizeEnumNameForAnItem(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.MATERIAL_ENUM_NAME, event -> {
            event.register(handle -> {
                var key = handle.getSourceValue().getLeft();
                if (key.equals(NamespacedKey.fromString("example:ash"))) {
                    handle.set("ASHES_TO_DUST");
                }
            });
        });
    }

}
