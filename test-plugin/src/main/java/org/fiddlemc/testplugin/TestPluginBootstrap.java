package org.fiddlemc.testplugin;

import com.google.gson.JsonParser;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.FiddleEvents;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.ItemRegistryEntry;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.testplugin.data.PluginBlockTypes;
import org.fiddlemc.testplugin.data.PluginItemTypes;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;

@SuppressWarnings("unused")
public class TestPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        loadIncludedDataPack(context);
        addCustomBlocks(context);
        addCustomItems(context);
        setBlockMappings(context);
        setItemMappings(context);
        setTranslations(context);
        configureResourcePack(context);
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
     */
    private void addCustomBlocks(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK, event -> {

            event.registry().register(TypedKey.create(RegistryKey.BLOCK, Key.key("quark:birch_bookshelf")), builder -> {
                builder
                    .mapColor(BlockType.BIRCH_LEAVES)
                    .instrument(Instrument.BASS_GUITAR)
                    .destroyTime(1.5f)
                    .explosionResistance(1.5f)
                    .sound(BlockType.OAK_PLANKS)
                    .ignitedByLava();
            });

            event.registry().register(TypedKey.create(RegistryKey.BLOCK, Key.key("quark:diorite_bricks")), builder -> {
                builder
                    .mapColor(BlockType.DIORITE)
                    .instrument(Instrument.BASS_DRUM)
                    .requiresCorrectToolForDrops()
                    .destroyTime(1.5f)
                    .explosionResistance(6.0f);
            });

            event.registry().register(TypedKey.create(RegistryKey.BLOCK, Key.key("quark:diorite_bricks_slab")), builder -> {
                builder
                    .inheritsFromSlab()
                    .mapColor(BlockType.DIORITE)
                    .instrument(Instrument.BASS_DRUM)
                    .requiresCorrectToolForDrops()
                    .destroyTime(1.5f)
                    .explosionResistance(6.0f);
            });

            event.registry().register(TypedKey.create(RegistryKey.BLOCK, Key.key("quark:diorite_bricks_stairs")), builder -> {
                builder
                    .inheritsFromStairs(PluginBlockTypes.DIORITE_BRICKS.get()) // It's a stair block
                    .mapColor(BlockType.DIORITE)
                    .instrument(Instrument.BASS_DRUM)
                    .requiresCorrectToolForDrops()
                    .destroyTime(1.5f)
                    .explosionResistance(6.0f);
            });

        });
    }

    /**
     * Adds our custom items.
     */
    private void addCustomItems(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM, event -> {

            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:clear_shard")), builder -> {
            });
            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:birch_bookshelf")), ItemRegistryEntry.Builder::inheritsFromBlock);
            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:diorite_bricks")), ItemRegistryEntry.Builder::inheritsFromBlock);
            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:diorite_bricks_slab")), ItemRegistryEntry.Builder::inheritsFromBlock);
            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:diorite_bricks_stairs")), ItemRegistryEntry.Builder::inheritsFromBlock);

        });
    }

    /**
     * Configures the server-to-client mappings for blocks.
     */
    private void setBlockMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK_MAPPING, event -> {

            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.BIRCH_BOOKSHELF.get(),
                BlockType.BOOKSHELF
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICKS.get(),
                BlockType.POLISHED_DIORITE
            );
            NoteBlock dioriteBricksNoteBlockState = BlockType.NOTE_BLOCK.createBlockData();
            dioriteBricksNoteBlockState.setInstrument(Instrument.BELL);
            dioriteBricksNoteBlockState.setNote(Note.natural(0, Note.Tone.G));
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.DIORITE_BRICKS.get());
                builder.to(dioriteBricksNoteBlockState);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(dioriteBricksNoteBlockState);
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICKS_SLAB.get(),
                BlockType.POLISHED_DIORITE_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                PluginBlockTypes.DIORITE_BRICKS_SLAB.get(),
                BlockType.WAXED_CUT_COPPER_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                BlockType.WAXED_CUT_COPPER_SLAB,
                BlockType.CUT_COPPER_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICKS_STAIRS.get(),
                BlockType.POLISHED_DIORITE_STAIRS
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                PluginBlockTypes.DIORITE_BRICKS_STAIRS.get(),
                BlockType.WAXED_CUT_COPPER_STAIRS
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                BlockType.WAXED_CUT_COPPER_STAIRS,
                BlockType.CUT_COPPER_STAIRS
            );

        });
    }

    /**
     * Configures the server-to-client mappings for items.
     */
    private void setItemMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM_MAPPING, event -> {

            event.register(builder -> {
                builder.from(PluginItemTypes.CLEAR_SHARD.get());
                builder.to(ItemType.PRISMARINE_SHARD);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.BIRCH_BOOKSHELF.get());
                builder.to(ItemType.BOOKSHELF);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.BIRCH_BOOKSHELF.get());
                builder.to(ItemType.BARRIER);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIORITE_BRICKS.get());
                builder.to(ItemType.POLISHED_DIORITE);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIORITE_BRICKS.get());
                builder.to(ItemType.BARRIER);
            });
            event.register(builder -> {
                builder.from(PluginItemTypes.DIORITE_BRICKS_SLAB.get());
                builder.to(ItemType.POLISHED_DIORITE_SLAB);
            });
            event.register(builder -> {
                builder.from(PluginItemTypes.DIORITE_BRICKS_STAIRS.get());
                builder.to(ItemType.POLISHED_DIORITE_STAIRS);
            });

        });
    }

    /**
     * Configures the translations of names of blocks and items.
     */
    private void setTranslations(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.SERVER_SIDE_TRANSLATION, event -> {

            event.register(PluginItemTypes.CLEAR_SHARD.get().translationKey(), "Glass Shard");
            event.register(PluginItemTypes.CLEAR_SHARD.get().translationKey(), "ガラスの破片", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.BIRCH_BOOKSHELF.get().translationKey(), "Birch Bookshelf");
            event.register(PluginBlockTypes.BIRCH_BOOKSHELF.get().translationKey(), "シラカバの本棚", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICKS.get().translationKey(), "Diorite Bricks");
            event.register(PluginBlockTypes.DIORITE_BRICKS.get().translationKey(), "閃緑岩レンガ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICKS_SLAB.get().translationKey(), "Diorite Brick Slab");
            event.register(PluginBlockTypes.DIORITE_BRICKS_SLAB.get().translationKey(), "閃緑岩レンガのハーフブロック", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICKS_STAIRS.get().translationKey(), "Diorite Brick Stairs");
            event.register(PluginBlockTypes.DIORITE_BRICKS_STAIRS.get().translationKey(), "閃緑岩レンガの階段", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

            event.register(BlockType.BOOKSHELF.translationKey(), "Oak Bookshelf");
            event.register(BlockType.BOOKSHELF.translationKey(), "オークの本棚", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

        });
    }

    private void configureResourcePack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.RESOURCE_PACK_CONSTRUCT, event -> {
            event.getAssetPath(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", BlockType.NOTE_BLOCK.getKey(), "json").setJsonObjectMutable(JsonParser.parseString("""
                {
                  "variants": {
                       "instrument=bell,note=1,powered=false": { "model": "quark:block/diorite_bricks" },
                       "instrument=bell,note=1,powered=true": { "model": "quark:block/diorite_bricks" }
                     }
                }
                """).getAsJsonObject());
            event.getAssetPath(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", BlockType.WAXED_CUT_COPPER_SLAB.getKey(), "json").setJsonObjectMutable(JsonParser.parseString("""
                {
                  "variants": {
                    "type=bottom": {
                      "model": "minecraft:block/tuff_brick_slab"
                    },
                    "type=double": {
                      "model": "minecraft:block/tuff_bricks"
                    },
                    "type=top": {
                      "model": "minecraft:block/tuff_brick_slab_top"
                    }
                  }
                }
                """).getAsJsonObject());
            event.getAssetPath(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", BlockType.WAXED_CUT_COPPER_STAIRS.getKey(), "json").setJsonObjectMutable(JsonParser.parseString("""
                {
                  "variants": {
                    "facing=east,half=bottom,shape=inner_left": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "y": 270
                    },
                    "facing=east,half=bottom,shape=inner_right": {
                      "model": "minecraft:block/tuff_brick_stairs_inner"
                    },
                    "facing=east,half=bottom,shape=outer_left": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "y": 270
                    },
                    "facing=east,half=bottom,shape=outer_right": {
                      "model": "minecraft:block/tuff_brick_stairs_outer"
                    },
                    "facing=east,half=bottom,shape=straight": {
                      "model": "minecraft:block/tuff_brick_stairs"
                    },
                    "facing=east,half=top,shape=inner_left": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "x": 180
                    },
                    "facing=east,half=top,shape=inner_right": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "x": 180,
                      "y": 90
                    },
                    "facing=east,half=top,shape=outer_left": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "x": 180
                    },
                    "facing=east,half=top,shape=outer_right": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "x": 180,
                      "y": 90
                    },
                    "facing=east,half=top,shape=straight": {
                      "model": "minecraft:block/tuff_brick_stairs",
                      "uvlock": true,
                      "x": 180
                    },
                    "facing=north,half=bottom,shape=inner_left": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "y": 180
                    },
                    "facing=north,half=bottom,shape=inner_right": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "y": 270
                    },
                    "facing=north,half=bottom,shape=outer_left": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "y": 180
                    },
                    "facing=north,half=bottom,shape=outer_right": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "y": 270
                    },
                    "facing=north,half=bottom,shape=straight": {
                      "model": "minecraft:block/tuff_brick_stairs",
                      "uvlock": true,
                      "y": 270
                    },
                    "facing=north,half=top,shape=inner_left": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "x": 180,
                      "y": 270
                    },
                    "facing=north,half=top,shape=inner_right": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "x": 180
                    },
                    "facing=north,half=top,shape=outer_left": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "x": 180,
                      "y": 270
                    },
                    "facing=north,half=top,shape=outer_right": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "x": 180
                    },
                    "facing=north,half=top,shape=straight": {
                      "model": "minecraft:block/tuff_brick_stairs",
                      "uvlock": true,
                      "x": 180,
                      "y": 270
                    },
                    "facing=south,half=bottom,shape=inner_left": {
                      "model": "minecraft:block/tuff_brick_stairs_inner"
                    },
                    "facing=south,half=bottom,shape=inner_right": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "y": 90
                    },
                    "facing=south,half=bottom,shape=outer_left": {
                      "model": "minecraft:block/tuff_brick_stairs_outer"
                    },
                    "facing=south,half=bottom,shape=outer_right": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "y": 90
                    },
                    "facing=south,half=bottom,shape=straight": {
                      "model": "minecraft:block/tuff_brick_stairs",
                      "uvlock": true,
                      "y": 90
                    },
                    "facing=south,half=top,shape=inner_left": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "x": 180,
                      "y": 90
                    },
                    "facing=south,half=top,shape=inner_right": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "x": 180,
                      "y": 180
                    },
                    "facing=south,half=top,shape=outer_left": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "x": 180,
                      "y": 90
                    },
                    "facing=south,half=top,shape=outer_right": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "x": 180,
                      "y": 180
                    },
                    "facing=south,half=top,shape=straight": {
                      "model": "minecraft:block/tuff_brick_stairs",
                      "uvlock": true,
                      "x": 180,
                      "y": 90
                    },
                    "facing=west,half=bottom,shape=inner_left": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "y": 90
                    },
                    "facing=west,half=bottom,shape=inner_right": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "y": 180
                    },
                    "facing=west,half=bottom,shape=outer_left": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "y": 90
                    },
                    "facing=west,half=bottom,shape=outer_right": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "y": 180
                    },
                    "facing=west,half=bottom,shape=straight": {
                      "model": "minecraft:block/tuff_brick_stairs",
                      "uvlock": true,
                      "y": 180
                    },
                    "facing=west,half=top,shape=inner_left": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "x": 180,
                      "y": 180
                    },
                    "facing=west,half=top,shape=inner_right": {
                      "model": "minecraft:block/tuff_brick_stairs_inner",
                      "uvlock": true,
                      "x": 180,
                      "y": 270
                    },
                    "facing=west,half=top,shape=outer_left": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "x": 180,
                      "y": 180
                    },
                    "facing=west,half=top,shape=outer_right": {
                      "model": "minecraft:block/tuff_brick_stairs_outer",
                      "uvlock": true,
                      "x": 180,
                      "y": 270
                    },
                    "facing=west,half=top,shape=straight": {
                      "model": "minecraft:block/tuff_brick_stairs",
                      "uvlock": true,
                      "x": 180,
                      "y": 180
                    }
                  }
                }
                """).getAsJsonObject());
            event.getAssetPath(ClientView.AwarenessLevel.RESOURCE_PACK, "models/block", PluginBlockTypes.DIORITE_BRICKS.get().getKey(), "json").setJsonObjectMutable(JsonParser.parseString("""
                {
                  "parent": "minecraft:block/cube_all",
                  "textures": {
                    "all": "minecraft:block/purple_wool"
                  }
                }
                """).getAsJsonObject());
        });
    }

}
