package org.fiddlemc.testplugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Instrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.fiddlemc.fiddle.api.FiddleEvents;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.ItemRegistryEntry;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingUtilities;
import org.fiddlemc.testplugin.data.PluginBlockTypes;
import org.fiddlemc.testplugin.data.PluginItemTypes;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

            event.registry().register(TypedKey.create(RegistryKey.BLOCK, Key.key("quark:diorite_brick_slab")), builder -> {
                builder
                    .inheritsFromSlab()
                    .mapColor(BlockType.DIORITE)
                    .instrument(Instrument.BASS_DRUM)
                    .requiresCorrectToolForDrops()
                    .destroyTime(1.5f)
                    .explosionResistance(6.0f);
            });

            event.registry().register(TypedKey.create(RegistryKey.BLOCK, Key.key("quark:diorite_brick_stairs")), builder -> {
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

            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:glass_shard")), builder -> {
            });
            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:birch_bookshelf")), ItemRegistryEntry.Builder::inheritsFromBlock);
            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:diorite_bricks")), ItemRegistryEntry.Builder::inheritsFromBlock);
            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:diorite_brick_slab")), ItemRegistryEntry.Builder::inheritsFromBlock);
            event.registry().register(TypedKey.create(RegistryKey.ITEM, Key.key("quark:diorite_brick_stairs")), ItemRegistryEntry.Builder::inheritsFromBlock);

        });
    }

    private static NoteBlock getBirchBookshelfNoteBlockState() {
        NoteBlock state = BlockType.NOTE_BLOCK.createBlockData();
        state.setInstrument(Instrument.BELL);
        state.setNote(Note.natural(1, Note.Tone.G));
        return state;
    }

    private static NoteBlock getDioriteBricksNoteBlockState() {
        NoteBlock state = BlockType.NOTE_BLOCK.createBlockData();
        state.setInstrument(Instrument.BELL);
        state.setNote(Note.natural(0, Note.Tone.G));
        return state;
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
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.BIRCH_BOOKSHELF.get());
                builder.to(getBirchBookshelfNoteBlockState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getBirchBookshelfNoteBlockState());
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICKS.get(),
                BlockType.POLISHED_DIORITE
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.DIORITE_BRICKS.get());
                builder.to(getDioriteBricksNoteBlockState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getDioriteBricksNoteBlockState());
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICK_SLAB.get(),
                BlockType.POLISHED_DIORITE_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                PluginBlockTypes.DIORITE_BRICK_SLAB.get(),
                BlockType.WAXED_CUT_COPPER_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                BlockType.WAXED_CUT_COPPER_SLAB,
                BlockType.CUT_COPPER_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIORITE_BRICK_STAIRS.get(),
                BlockType.POLISHED_DIORITE_STAIRS
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                PluginBlockTypes.DIORITE_BRICK_STAIRS.get(),
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
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.BIRCH_BOOKSHELF.get());
                builder.to(ItemType.BOOKSHELF);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.BIRCH_BOOKSHELF.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.NOTE_BLOCK);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("quark:birch_bookshelf"));
                        ((BlockDataMeta) meta).setBlockData(getBirchBookshelfNoteBlockState());
                    });
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIORITE_BRICKS.get());
                builder.to(ItemType.POLISHED_DIORITE);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIORITE_BRICKS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.NOTE_BLOCK);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("quark:diorite_bricks"));
                        ((BlockDataMeta) meta).setBlockData(getDioriteBricksNoteBlockState());
                    });
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIORITE_BRICK_SLAB.get());
                builder.to(ItemType.POLISHED_DIORITE_SLAB);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIORITE_BRICK_SLAB.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.WAXED_CUT_COPPER_SLAB);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("quark:diorite_brick_slab")));
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(ItemType.WAXED_CUT_COPPER_SLAB);
                builder.to(ItemType.CUT_COPPER_SLAB);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIORITE_BRICK_STAIRS.get());
                builder.to(ItemType.POLISHED_DIORITE_STAIRS);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIORITE_BRICK_STAIRS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.WAXED_CUT_COPPER_STAIRS);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("quark:diorite_brick_stairs")));
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(ItemType.WAXED_CUT_COPPER_STAIRS);
                builder.to(ItemType.CUT_COPPER_STAIRS);
            });

        });
    }

    /**
     * Configures the translations of names of blocks and items.
     */
    private void setTranslations(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.SERVER_SIDE_TRANSLATION, event -> {

            event.register(PluginItemTypes.GLASS_SHARD.get().translationKey(), "Glass Shard");
            event.register(PluginItemTypes.GLASS_SHARD.get().translationKey(), "ガラスの破片", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.BIRCH_BOOKSHELF.get().translationKey(), "Birch Bookshelf");
            event.register(PluginBlockTypes.BIRCH_BOOKSHELF.get().translationKey(), "シラカバの本棚", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICKS.get().translationKey(), "Diorite Bricks");
            event.register(PluginBlockTypes.DIORITE_BRICKS.get().translationKey(), "閃緑岩レンガ", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICK_SLAB.get().translationKey(), "Diorite Brick Slab");
            event.register(PluginBlockTypes.DIORITE_BRICK_SLAB.get().translationKey(), "閃緑岩レンガのハーフブロック", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
            event.register(PluginBlockTypes.DIORITE_BRICK_STAIRS.get().translationKey(), "Diorite Brick Stairs");
            event.register(PluginBlockTypes.DIORITE_BRICK_STAIRS.get().translationKey(), "閃緑岩レンガの階段", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

            event.register(BlockType.BOOKSHELF.translationKey(), "Oak Bookshelf");
            event.register(BlockType.BOOKSHELF.translationKey(), "オークの本棚", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);

        });
    }

    private void configureResourcePack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.RESOURCE_PACK_CONSTRUCT, event -> {
            try {
                event.copyPluginResources(context, List.of(ClientView.AwarenessLevel.RESOURCE_PACK, ClientView.AwarenessLevel.CLIENT_MOD), "resource_pack_direct", "");
                event.asset(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", BlockType.NOTE_BLOCK.getKey(), "json").setJsonParsedFromString("""
                    {
                      "variants": {
                           "instrument=bell,note=13,powered=false": { "model": "quark:block/birch_bookshelf" },
                           "instrument=bell,note=1,powered=false": { "model": "quark:block/diorite_bricks" }
                         }
                    }
                    """);
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/quark/blockstates/diorite_brick_slab.json", "assets/minecraft/blockstates/waxed_cut_copper_slab.json");
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/quark/blockstates/diorite_brick_stairs.json", "assets/minecraft/blockstates/waxed_cut_copper_stairs.json");
                event.copyPluginResources(context, ClientView.AwarenessLevel.CLIENT_MOD, "resource_pack_indirect", "");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        context.getLifecycleManager().registerEventHandler(FiddleEvents.RESOURCE_PACK_CONSTRUCT_FINISH, event -> {
            try {
                Files.write(Path.of("resource_pack.zip"), event.get(ClientView.AwarenessLevel.RESOURCE_PACK).getBytes());
                Files.write(Path.of("client_mod.zip"), event.get(ClientView.AwarenessLevel.CLIENT_MOD).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
