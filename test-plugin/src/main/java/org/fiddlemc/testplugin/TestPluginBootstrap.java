package org.fiddlemc.testplugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.bukkit.Instrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Slab;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.fiddlemc.fiddle.api.FiddleEvents;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.nms.BlockRegistryEntryBuilderNMS;
import org.fiddlemc.fiddle.api.moredatadriven.paper.registry.type.nms.BlockEntityTypeRegistryEntryBuilderNMS;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingUtilities;
import org.fiddlemc.testplugin.data.PluginBlockTypes;
import org.fiddlemc.testplugin.data.PluginItemTypes;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class TestPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        loadIncludedDataPack(context);
        registerBlockEntities(context);
        setBlockMappings(context);
        setItemMappings(context);
        setItemSourceTooltipMapping(context);
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
                event.registrar().discoverPack(Objects.requireNonNull(this.getClass().getResource("/data_pack")).toURI(), "provided");
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Lightable getLitRedstoneLampState() {
        Lightable state = BlockType.REDSTONE_LAMP.createBlockData();
        state.setLit(true);
        return state;
    }

    private static NoteBlock getLitPaperLampNoteBlockState() {
        NoteBlock state = BlockType.NOTE_BLOCK.createBlockData();
        state.setInstrument(Instrument.BELL);
        state.setNote(Note.natural(1, Note.Tone.A));
        return state;
    }

    private static NoteBlock getPaperLampNoteBlockState() {
        NoteBlock state = BlockType.NOTE_BLOCK.createBlockData();
        state.setInstrument(Instrument.BELL);
        state.setNote(Note.sharp(1, Note.Tone.A));
        return state;
    }

    private static Leaves getNonWaterloggedYellowMapleLeavesState() {
        Leaves state = BlockType.CHERRY_LEAVES.createBlockData();
        state.setDistance(2);
        return state;
    }

    private static Leaves getWaterloggedYellowMapleLeavesState() {
        Leaves state = BlockType.CHERRY_LEAVES.createBlockData();
        state.setDistance(2);
        state.setWaterlogged(true);
        return state;
    }

    private static NoteBlock getAzaleaPlanksNoteBlockState() {
        NoteBlock state = BlockType.NOTE_BLOCK.createBlockData();
        state.setInstrument(Instrument.BELL);
        state.setNote(Note.sharp(1, Note.Tone.G));
        return state;
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
     * Register custom block entities.
     */
    private void registerBlockEntities(@NotNull BootstrapContext context) {

        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK, event -> {
            System.out.println("FiddleEvents.BLOCK");
            event.registry().register(
                TypedKey.create(RegistryKey.BLOCK, Key.key("example:block_entity")),
                builder -> {
                    var builderNMS = (BlockRegistryEntryBuilderNMS) builder;
                    builderNMS.factoryNMS(TestBlock::new);
                }
            );
        });

        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM, event -> {
            System.out.println("FiddleEvents.ITEM");
            event.registry().register(
                TypedKey.create(RegistryKey.ITEM, Key.key("example:block_entity")),
                builder -> {
                    builder.inheritsFromBlock(new NamespacedKey("example", "block_entity"));
                }
            );
        });

        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK_ENTITY_TYPE, event -> {
            System.out.println("FiddleEvents.BLOCK_ENTITY_TYPE");
            event.registry().register(
                TypedKey.create(RegistryKey.BLOCK_ENTITY_TYPE, Key.key("example:block_entity")),
                builder -> {
                    var builderNMS = (BlockEntityTypeRegistryEntryBuilderNMS) builder;
                    builderNMS.factorNMS(TestBlockEntity::new);
                    builderNMS.validBlocksNMS(BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("example", "block_entity")).get().value());
                }
            );
        });

        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM_MAPPING, event -> {
            event.register(builder -> {
                builder.everyAwarenessLevel();
                builder.from(PluginItemTypes.EXAMPLE_BLOCK_ENTITY.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.DIAMOND_BLOCK);
                });
            });
        });

        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK_MAPPING, event -> {
            event.register(builder -> {
                builder.everyAwarenessLevel();
                builder.fromEveryStateOf(PluginBlockTypes.EXAMPLE_BLOCK_ENTITY.get());
                builder.to(BlockType.DIAMOND_BLOCK.createBlockData());
            });
        });
    }

    /**
     * Configures the server-to-client mappings for blocks.
     */
    private void setBlockMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.BLOCK_MAPPING, event -> {

            // Lit paper lamp
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.fromEveryStateOf(PluginBlockTypes.LIT_PAPER_LAMP.get());
                builder.to(getLitRedstoneLampState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.LIT_PAPER_LAMP.get());
                builder.to(getLitPaperLampNoteBlockState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getLitPaperLampNoteBlockState());
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });

            // Paper lamp
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.fromEveryStateOf(PluginBlockTypes.PAPER_LAMP.get());
                builder.to(BlockType.REDSTONE_LAMP.createBlockData());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.PAPER_LAMP.get());
                builder.to(getPaperLampNoteBlockState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getPaperLampNoteBlockState());
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });

            // Yellow maple leaves
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.YELLOW_MAPLE_LEAVES.get(),
                BlockType.ACACIA_LEAVES
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginBlockTypes.YELLOW_MAPLE_LEAVES.get().createBlockDataStates().stream().filter(data -> !((Leaves) data).isWaterlogged()).toList());
                builder.to(getNonWaterloggedYellowMapleLeavesState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getNonWaterloggedYellowMapleLeavesState());
                builder.to(BlockType.CHERRY_LEAVES.createBlockData());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginBlockTypes.YELLOW_MAPLE_LEAVES.get().createBlockDataStates().stream().filter(data -> ((Leaves) data).isWaterlogged()).toList());
                builder.to(getWaterloggedYellowMapleLeavesState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getWaterloggedYellowMapleLeavesState());
                Leaves waterloggedDefaultBirchLeaves = BlockType.CHERRY_LEAVES.createBlockData();
                waterloggedDefaultBirchLeaves.setWaterlogged(true);
                builder.to(waterloggedDefaultBirchLeaves);
            });

            // Snowed stone bricks
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.SNOWED_STONE_BRICKS.get(),
                BlockType.STONE_BRICKS
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.SNOWED_STONE_BRICKS.get());
                builder.to(BlockType.INFESTED_STONE_BRICKS.createBlockData());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(BlockType.INFESTED_STONE_BRICKS.createBlockData());
                builder.to(BlockType.STONE_BRICKS.createBlockData());
            });

            // Dirt slab
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIRT_SLAB.get(),
                BlockType.MUD_BRICK_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                PluginBlockTypes.DIRT_SLAB.get(),
                BlockType.WAXED_EXPOSED_CUT_COPPER_SLAB
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                BlockType.WAXED_EXPOSED_CUT_COPPER_SLAB,
                BlockType.EXPOSED_CUT_COPPER_SLAB
            );

            // Diorite brick stairs
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.DIRT_STAIRS.get(),
                BlockType.MUD_BRICK_STAIRS
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                PluginBlockTypes.DIRT_STAIRS.get(),
                BlockType.WAXED_EXPOSED_CUT_COPPER_STAIRS
            );
            event.registerStateToState(
                ClientView.AwarenessLevel.RESOURCE_PACK,
                BlockType.WAXED_EXPOSED_CUT_COPPER_STAIRS,
                BlockType.EXPOSED_CUT_COPPER_STAIRS
            );

            // Glass slab
            event.registerStateToState(
                ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(),
                PluginBlockTypes.GLASS_SLAB.get().createBlockDataStates().stream().filter(data -> ((Slab) data).getType() != Slab.Type.DOUBLE).toList(),
                BlockType.QUARTZ_SLAB
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks());
                builder.from(PluginBlockTypes.GLASS_SLAB.get().createBlockDataStates().stream().filter(data -> ((Slab) data).getType() == Slab.Type.DOUBLE).toList());
                builder.toDefaultStateOf(BlockType.GLASS);
            });

            // Glass stairs
            event.registerStateToState(
                ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(),
                PluginBlockTypes.GLASS_STAIRS.get(),
                BlockType.QUARTZ_STAIRS
            );

            // Grass slab
            event.registerStateToState(
                ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks(),
                PluginBlockTypes.GRASS_SLAB.get(),
                BlockType.MOSSY_COBBLESTONE_SLAB
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks());
                builder.from(PluginBlockTypes.GRASS_SLAB.get().createBlockDataStates().stream().filter(data -> ((Slab) data).getType() == Slab.Type.DOUBLE).toList());
                builder.toDefaultStateOf(BlockType.GRASS_BLOCK);
            });

            // Stone brick bevel
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks());
                builder.fromEveryStateOf(PluginBlockTypes.STONE_BRICK_BEVEL.get());
                builder.toDefaultStateOf(BlockType.BARRIER);
            });

            // Azalea planks
            event.registerStateToState(
                ClientView.AwarenessLevel.VANILLA,
                PluginBlockTypes.AZALEA_PLANKS.get(),
                BlockType.WARPED_PLANKS
            );
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.fromEveryStateOf(PluginBlockTypes.AZALEA_PLANKS.get());
                builder.to(getAzaleaPlanksNoteBlockState());
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(getAzaleaPlanksNoteBlockState());
                builder.to(BlockType.NOTE_BLOCK.createBlockData());
            });

            // Birch bookshelf
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

            // Diorite brick slab
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

            // Diorite brick stairs
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

            // Diorite bricks
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

        });
    }

    /**
     * Configures the server-to-client mappings for items.
     */
    private void setItemMappings(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM_MAPPING, event -> {

            // Lit paper lamp
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.LIT_PAPER_LAMP.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.REDSTONE_LAMP);
                    handle.getMutable().editMeta(meta -> {
                        ((BlockDataMeta) meta).setBlockData(getLitRedstoneLampState());
                    });
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.LIT_PAPER_LAMP.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.NOTE_BLOCK);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("chinese_mythology_mashup:lit_paper_lamp"));
                        ((BlockDataMeta) meta).setBlockData(getLitPaperLampNoteBlockState());
                    });
                });
            });

            // Paper lamp
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.PAPER_LAMP.get());
                builder.to(ItemType.REDSTONE_LAMP);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.PAPER_LAMP.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.NOTE_BLOCK);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("chinese_mythology_mashup:paper_lamp"));
                        ((BlockDataMeta) meta).setBlockData(getPaperLampNoteBlockState());
                    });
                });
            });

            // Yellow maple leaves
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.YELLOW_MAPLE_LEAVES.get());
                builder.to(ItemType.ACACIA_LEAVES);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.YELLOW_MAPLE_LEAVES.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.CHERRY_LEAVES);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("maple_delight:yellow_maple_leaves"));
                        ((BlockDataMeta) meta).setBlockData(getNonWaterloggedYellowMapleLeavesState());
                    });
                });
            });

            // Snowed stone bricks
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.SNOWED_STONE_BRICKS.get());
                builder.to(ItemType.STONE_BRICKS);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.SNOWED_STONE_BRICKS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.INFESTED_STONE_BRICKS);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("minecraft_dungeons:snowed_stone_bricks"));
                        ((BlockDataMeta) meta).setBlockData(BlockType.INFESTED_STONE_BRICKS.createBlockData());
                    });
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(ItemType.INFESTED_STONE_BRICKS);
                builder.to(ItemType.STONE_BRICKS);
            });

            // Dirt slab
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIRT_SLAB.get());
                builder.to(ItemType.MUD_BRICK_SLAB);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIRT_SLAB.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.WAXED_EXPOSED_CUT_COPPER_SLAB);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("more_vanilla_shapes:dirt_slab")));
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(ItemType.WAXED_EXPOSED_CUT_COPPER_SLAB);
                builder.to(ItemType.EXPOSED_CUT_COPPER_SLAB);
            });

            // Dirt stairs
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.DIRT_STAIRS.get());
                builder.to(ItemType.MUD_BRICK_STAIRS);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.DIRT_STAIRS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.WAXED_EXPOSED_CUT_COPPER_STAIRS);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("more_vanilla_shapes:dirt_stairs")));
                });
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(ItemType.WAXED_EXPOSED_CUT_COPPER_STAIRS);
                builder.to(ItemType.EXPOSED_CUT_COPPER_STAIRS);
            });

            // Glass slab
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.GLASS_SLAB.get());
                builder.to(ItemType.QUARTZ_SLAB);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.GLASS_SLAB.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.QUARTZ_SLAB);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("more_vanilla_shapes:glass_slab")));
                });
            });

            // Glass stairs
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.GLASS_STAIRS.get());
                builder.to(ItemType.QUARTZ_STAIRS);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.GLASS_STAIRS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.QUARTZ_STAIRS);
                    handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("more_vanilla_shapes:glass_stairs")));
                });
            });

            // Grass slab
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.GRASS_SLAB.get());
                builder.to(ItemType.MOSSY_COBBLESTONE_SLAB);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.GRASS_SLAB.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.MOSSY_COBBLESTONE_SLAB);
                    // handle.getMutable().editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("more_vanilla_shapes:grass_slab")));
                });
            });

            // Stone brick bevel
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.STONE_BRICK_BEVEL.get());
                builder.to(ItemType.BARRIER);
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

            // Azalea planks
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                builder.from(PluginItemTypes.AZALEA_PLANKS.get());
                builder.to(ItemType.WARPED_PLANKS);
            });
            event.register(builder -> {
                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                builder.from(PluginItemTypes.AZALEA_PLANKS.get());
                builder.to(handle -> {
                    ItemMappingUtilities.get().setItemTypeWhilePreservingRest(handle, ItemType.NOTE_BLOCK);
                    handle.getMutable().editMeta(meta -> {
                        meta.setItemModel(NamespacedKey.fromString("quark:azalea_planks"));
                        ((BlockDataMeta) meta).setBlockData(getAzaleaPlanksNoteBlockState());
                    });
                });
            });

            // Birch bookshelf
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

            // Diorite brick slab
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

            // Diorite brick stairs
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

            // Diorite bricks
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
     * Adds a tooltip to custom items, letting the player know the source to which the block plugins.
     */
    private void setItemSourceTooltipMapping(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.ITEM_MAPPING, event -> {
            event.register(builder -> {
                builder.everyAwarenessLevel();
                builder.from(Registry.ITEM.stream().filter(item -> !item.isVanilla()).toList());
                builder.to(handle -> {
                    String source = switch (handle.getOriginal().getType().key().namespace()) {
                        case "chinese_mythology_mashup" -> "Chinese Mythology Mash-up";
                        case "maple_delight" -> "Maple Delight";
                        case "minecraft_dungeons" -> "Minecraft Dungeons";
                        case "more_vanilla_shapes" -> "More Vanilla Shapes";
                        case "quark" -> "Quark";
                        case "minecraft", "fiddle" -> null;
                        case "example" -> "Example";
                        default ->
                            throw new IllegalStateException("Unexpected value: " + handle.getOriginal().getType().key().namespace());
                    };
                    if (source == null) return;
                    ItemStack itemStack = handle.getMutable();
                    @Nullable List<Component> lore = itemStack.lore();
                    if (lore == null) {
                        lore = new ArrayList<>(1);
                    }
                    lore.add(Component.text(source, Style.empty().color(NamedTextColor.BLUE)));
                    itemStack.lore(lore);
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
            event.register(PluginBlockTypes.GRASS_SLAB.get().translationKey(), "Grass Slab");
            event.register(PluginBlockTypes.GRASS_SLAB.get().translationKey(), "草のハーフブロック", "ja_jp", ServerSideTranslations.FallbackScope.LANGUAGE_GROUP);
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

    private void configureResourcePack(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(FiddleEvents.RESOURCE_PACK_CONSTRUCT, event -> {
            try {
                event.copyPluginResources(context, List.of(ClientView.AwarenessLevel.RESOURCE_PACK, ClientView.AwarenessLevel.CLIENT_MOD), "resource_pack_direct", "");
                event.asset(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", BlockType.CHERRY_LEAVES.getKey(), "json").asJsonObject().setParsedFromString("""
                    {
                      "variants": {
                           "distance=2,persistent=false": { "model": "maple_delight:block/yellow_maple_leaves" }
                         }
                    }
                    """);
                event.asset(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", BlockType.NOTE_BLOCK.getKey(), "json").asJsonObject().setParsedFromString("""
                    {
                      "variants": {
                           "instrument=bell,note=15,powered=false": { "model": "chinese_mythology_mashup:block/lit_paper_lamp" },
                           "instrument=bell,note=16,powered=false": { "model": "chinese_mythology_mashup:block/paper_lamp" },
                           "instrument=bell,note=14,powered=false": { "model": "quark:block/azalea_planks" },
                           "instrument=bell,note=13,powered=false": { "model": "quark:block/birch_bookshelf" },
                           "instrument=bell,note=1,powered=false": { "model": "quark:block/diorite_bricks" }
                         }
                    }
                    """);
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/minecraft_dungeons/blockstates/snowed_stone_bricks.json", "assets/minecraft/blockstates/infested_stone_bricks.json");
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/more_vanilla_shapes/blockstates/dirt_slab.json", "assets/minecraft/blockstates/waxed_exposed_cut_copper_slab.json");
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/more_vanilla_shapes/blockstates/dirt_stairs.json", "assets/minecraft/blockstates/waxed_exposed_cut_copper_stairs.json");
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/quark/blockstates/diorite_brick_slab.json", "assets/minecraft/blockstates/waxed_cut_copper_slab.json");
                event.copyPluginResource(this, ClientView.AwarenessLevel.RESOURCE_PACK, "resource_pack_indirect/assets/quark/blockstates/diorite_brick_stairs.json", "assets/minecraft/blockstates/waxed_cut_copper_stairs.json");
                event.copyPluginResources(context, ClientView.AwarenessLevel.CLIENT_MOD, "resource_pack_indirect", "");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
