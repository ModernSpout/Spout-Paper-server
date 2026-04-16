package org.fiddlemc.testplugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Lightable;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.FiddleEvents;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.AutomaticBlockMappings;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingUtilities;
import org.fiddlemc.testplugin.data.PluginBlockTypes;
import org.fiddlemc.testplugin.data.PluginItemTypes;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

@SuppressWarnings("unused")
public class TestPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        loadIncludedDataPack(context);
        loadIncludedResourcePack(context);
        setBlockMappings(context);
        setItemMappings(context);
        setTranslations(context);
    }

    /**
     * Makes sure the included data pack is loaded.
     * It contains drop tables, crafting recipes and more for the custom blocks and items we add.
     */
    private void loadIncludedDataPack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, event -> {
            try {
                event.registrar().discoverPack(Objects.requireNonNull(this.getClass().getResource("/data_pack")).toURI(), "provided");
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Makes sure the included resource pack is loaded.
     * It contains textures, models and more for the custom blocks and items we add.
     */
    private void loadIncludedResourcePack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.PLUGIN_RESOURCE_PACK_DISCOVERY, event -> {
            event.register(this, context);
        });
    }

    /**
     * Configures the server-to-client mappings for blocks.
     */
    private void setBlockMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK_MAPPING, event -> {
            AutomaticBlockMappings automaticMappings = event.automaticMappings(this);

            // Lit paper lamp
            automaticMappings.fullBlock(builder -> {
                builder.fromDefaultStateOf(PluginBlockTypes.LIT_PAPER_LAMP.get());
                Lightable litRedstoneLampState = BlockType.REDSTONE_LAMP.createBlockData();
                litRedstoneLampState.setLit(true);
                builder.fallback(litRedstoneLampState);
            });

            // Paper lamp
            automaticMappings.fullBlock(builder -> {
                builder.fromDefaultStateOf(PluginBlockTypes.PAPER_LAMP.get());
                builder.fallbackDefaultStateOf(BlockType.REDSTONE_LAMP);
            });

            // Yellow maple leaves
            automaticMappings.leaves(builder -> {
                builder.from(PluginBlockTypes.YELLOW_MAPLE_LEAVES.get());
                builder.fallback(BlockType.AZALEA_LEAVES);
            });

            // Snowed stone bricks
            automaticMappings.fullBlock(builder -> {
                builder.fromDefaultStateOf(PluginBlockTypes.SNOWED_STONE_BRICKS.get());
                builder.fallbackDefaultStateOf(BlockType.STONE_BRICKS);
            });

            // Dirt slab
            automaticMappings.slab(builder -> {
                builder.from(PluginBlockTypes.DIRT_SLAB.get());
                builder.fallback(BlockType.MUD_BRICK_SLAB);
                builder.fullBlockFallbackDefaultStateOf(BlockType.DIRT);
            });

            // Dirt stairs
            automaticMappings.stairs(builder -> {
                builder.from(PluginBlockTypes.DIRT_STAIRS.get());
                builder.fallback(BlockType.MUD_BRICK_STAIRS);
            });

            // Glass slab
            automaticMappings.slab(builder -> {
                builder.from(PluginBlockTypes.GLASS_SLAB.get());
                builder.fallback(BlockType.QUARTZ_SLAB);
                builder.fullBlockFallbackDefaultStateOf(BlockType.GLASS);
            });

            // Glass stairs
            automaticMappings.stairs(builder -> {
                builder.from(PluginBlockTypes.GLASS_STAIRS.get());
                builder.fallback(BlockType.QUARTZ_STAIRS);
            });

            // Stone brick bevel
            event.manualMappings().register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks());
                builder.fromEveryStateOf(PluginBlockTypes.STONE_BRICK_BEVEL.get());
                builder.toDefaultStateOf(BlockType.BARRIER);
            });

            // Azalea planks
            automaticMappings.fullBlock(builder -> {
                builder.fromDefaultStateOf(PluginBlockTypes.AZALEA_PLANKS.get());
                builder.fallbackDefaultStateOf(BlockType.WARPED_PLANKS);
            });

            // Birch bookshelf
            automaticMappings.fullBlock(builder -> {
                builder.fromDefaultStateOf(PluginBlockTypes.BIRCH_BOOKSHELF.get());
                builder.fallbackDefaultStateOf(BlockType.BOOKSHELF);
            });

            // Diorite brick slab
            automaticMappings.slab(builder -> {
                builder.from(PluginBlockTypes.DIORITE_BRICK_SLAB.get());
                builder.fallback(BlockType.POLISHED_DIORITE_SLAB);
            });

            // Diorite brick stairs
            automaticMappings.stairs(builder -> {
                builder.from(PluginBlockTypes.DIORITE_BRICK_STAIRS.get());
                builder.fallback(BlockType.POLISHED_DIORITE_STAIRS);
            });

            // Diorite bricks
            automaticMappings.fullBlock(builder -> {
                builder.fromDefaultStateOf(PluginBlockTypes.DIORITE_BRICKS.get());
                builder.fallbackDefaultStateOf(BlockType.POLISHED_DIORITE);
            });

        });
    }

    /**
     * Configures the server-to-client mappings for items.
     */
    private void setItemMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM_MAPPING, event -> {

            // Stone brick bevel
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.STONE_BRICK_BEVEL.get());
                builder.to(ItemType.STONE);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.STONE_BRICK_BEVEL.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.STONE_BUTTON);
                    // ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.BARRIER);
                    // handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("more_vanilla_shapes:stone_brick_bevel")));
                });
            });

            // Glass shard
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.GLASS_SHARD.get());
                builder.to(ItemType.PRISMARINE_SHARD);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.GLASS_SHARD.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.PRISMARINE_SHARD);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("quark:glass_shard")));
                });
            });

        });
    }

    /**
     * Configures the translations of names of blocks and items.
     */
    private void setTranslations(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.SERVER_SIDE_TRANSLATION, event -> {

            event.register(PluginBlockTypes.LIT_PAPER_LAMP.get().translationKey(), "Lit Paper Lamp");
            event.register(PluginBlockTypes.LIT_PAPER_LAMP.get().translationKey(), "点灯した紙ランプ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.PAPER_LAMP.get().translationKey(), "Paper Lamp");
            event.register(PluginBlockTypes.PAPER_LAMP.get().translationKey(), "紙ランプ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.YELLOW_MAPLE_LEAVES.get().translationKey(), "Yellow Maple Leaves");
            event.register(PluginBlockTypes.YELLOW_MAPLE_LEAVES.get().translationKey(), "黄色いカエデの葉", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.SNOWED_STONE_BRICKS.get().translationKey(), "Snowed Stone Bricks");
            event.register(PluginBlockTypes.SNOWED_STONE_BRICKS.get().translationKey(), "雪化粧の石レンガ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIRT_SLAB.get().translationKey(), "Dirt Slab");
            event.register(PluginBlockTypes.DIRT_SLAB.get().translationKey(), "土のハーフブロック", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIRT_STAIRS.get().translationKey(), "Dirt Stairs");
            event.register(PluginBlockTypes.DIRT_STAIRS.get().translationKey(), "土の階段", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.GLASS_SLAB.get().translationKey(), "Glass Slab");
            event.register(PluginBlockTypes.GLASS_SLAB.get().translationKey(), "ガラスのハーフブロック", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.GLASS_STAIRS.get().translationKey(), "Glass Stairs");
            event.register(PluginBlockTypes.GLASS_STAIRS.get().translationKey(), "ガラスの階段", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.STONE_BRICK_BEVEL.get().translationKey(), "Stone Brick Piece");
            event.register(PluginBlockTypes.STONE_BRICK_BEVEL.get().translationKey(), "石レンガのミニ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.AZALEA_PLANKS.get().translationKey(), "Azalea Planks");
            event.register(PluginBlockTypes.AZALEA_PLANKS.get().translationKey(), "ツツジの板材", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.BIRCH_BOOKSHELF.get().translationKey(), "Birch Bookshelf");
            event.register(PluginBlockTypes.BIRCH_BOOKSHELF.get().translationKey(), "シラカバの本棚", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICK_SLAB.get().translationKey(), "Diorite Brick Slab");
            event.register(PluginBlockTypes.DIORITE_BRICK_SLAB.get().translationKey(), "閃緑岩レンガのハーフブロック", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICK_STAIRS.get().translationKey(), "Diorite Brick Stairs");
            event.register(PluginBlockTypes.DIORITE_BRICK_STAIRS.get().translationKey(), "閃緑岩レンガの階段", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICKS.get().translationKey(), "Diorite Bricks");
            event.register(PluginBlockTypes.DIORITE_BRICKS.get().translationKey(), "閃緑岩レンガ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginItemTypes.GLASS_SHARD.get().translationKey(), "Glass Shard");
            event.register(PluginItemTypes.GLASS_SHARD.get().translationKey(), "ガラスの破片", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

            event.register(BlockType.BOOKSHELF.translationKey(), "Oak Bookshelf");
            event.register(BlockType.BOOKSHELF.translationKey(), "オークの本棚", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

        });
    }

}
