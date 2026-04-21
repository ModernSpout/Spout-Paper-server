package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.fiddlemc.fiddle.api.packetmapping.block.claim.ClaimRequestPriority;
import org.fiddlemc.fiddle.api.packetmapping.item.nms.ItemMappingUtilitiesNMS;
import org.fiddlemc.fiddle.api.resourcepack.construct.BlockstatesFiddleResourcePackPath;
import org.fiddlemc.fiddle.api.resourcepack.content.Blockstates;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.fiddlemc.fiddle.impl.packetmapping.block.claim.ResourcePackBlockStateClaimsImpl;
import org.fiddlemc.fiddle.impl.packetmapping.item.ItemMappingsImpl;
import org.fiddlemc.fiddle.impl.resourcepack.construct.FiddleResourcePackConstructionImpl;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;

/**
 * A {@link MultipleAttemptsRequestProcessor} where each attempt means claiming some {@link BlockState}s.
 */
public abstract class BlockStateClaimAttemptsRequestProcessor<US extends UsedStates, R extends ProxyStatesRequestBuilderImpl<US> & FromToBlockStatesRequestBuilder> extends MultipleAttemptsRequestProcessor<R, BlockState[]> {

    private @Nullable ClaimRequestPriority priority;

    protected BlockStateClaimAttemptsRequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    protected abstract BlockState[] nextCandidates();

    @Override
    protected void attemptNext() {

        BlockState[] candidates = this.nextCandidates();
        int[] candidateIndices = new int[candidates.length];
        for (int i = 0; i < candidates.length; i++) {
            candidateIndices[i] = candidates[i].indexInVanillaOnlyBlockStateRegistry;
        }

        if (this.priority == null) {
            this.priority = ClaimRequestPriority.forBlockStates(Arrays.stream(this.request.fromStates()).map(state -> (BlockData) state.createCraftBlockData()).toArray(BlockData[]::new));
        }

        ResourcePackBlockStateClaimsImpl.get().claim(
            candidateIndices,
            this.priority,
            result -> {
                if (result != null) {
                    this.processAttemptSuccess(Arrays.stream(result).mapToObj(VanillaOnlyBlockStateRegistry.get()::byId).toArray(BlockState[]::new));
                } else {
                    this.processAttemptFailure();
                }
            },
            this.request.createProxyToVisualDuplicateMapping ? visualDuplicates -> {
                for (int i = 0; i < visualDuplicates.length; i++) {
                    BlockState visualDuplicate = VanillaOnlyBlockStateRegistry.get().byId(visualDuplicates[i]);
                    this.event.registerNMS(
                        ClientView.AwarenessLevel.RESOURCE_PACK,
                        candidates[i],
                        visualDuplicate
                    );
                    if (this.request.createItemMappings && this.request instanceof FromToItemRequestBuilderImpl<?> && candidates[i] == candidates[i].getBlock().defaultBlockState()) {
                        @Nullable Item candidateItem = FromToItemRequestBuilderImpl.inferItem(candidates[i].getBlock());
                        @Nullable Item visualDuplicateItem = FromToItemRequestBuilderImpl.inferItem(visualDuplicate.getBlock());
                        if (candidateItem != null && visualDuplicateItem != null) {
                            ItemMappingsImpl.get().addEventInitializer(event -> {
                                @Nullable BlockItemStateProperties blockItemStateProperties = visualDuplicate == visualDuplicate.getBlock().defaultBlockState() ? null : new BlockItemStateProperties(visualDuplicate.createCraftBlockData().toStates(false));
                                @Nullable Identifier itemModel = candidateItem == visualDuplicateItem ? null : visualDuplicateItem.getDefaultInstance().getOrDefault(DataComponents.ITEM_MODEL, visualDuplicateItem.keyInItemRegistry);
                                if (candidateItem != visualDuplicateItem || itemModel != null || blockItemStateProperties != null) {
                                    event.registerNMS(builder -> {
                                        builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                                        builder.from(candidateItem);
                                        builder.to(handle -> {
                                            if (candidateItem != visualDuplicateItem) {
                                                ItemMappingUtilitiesNMS.get().setItemWhilePreservingRest(handle, visualDuplicateItem);
                                            }
                                            ItemStack itemStack = handle.getMutable();
                                            if (itemModel != null) {
                                                itemStack.set(DataComponents.ITEM_MODEL, itemModel);
                                            }
                                            if (blockItemStateProperties != null) {
                                                itemStack.set(DataComponents.BLOCK_STATE, blockItemStateProperties);
                                            }
                                        });
                                    });
                                }
                            });
                        }
                    }
                }
            } : null,
            true,
            false
        );

    }

    @Override
    protected void processSuccess(BlockState[] result) {
        this.processResult(result);
    }

    @Override
    protected void processFailure() {
        this.processResult(null);
    }

    protected abstract US createUsedStates(BlockState @Nullable [] result);

    protected int mapFromStatesIndexToProxyStatesIndex(int fromStatesIndex, BlockState[] proxyStates) {
        return fromStatesIndex;
    }

    protected int mapProxyStatesIndexToFromStatesIndex(int proxyStatesIndex, BlockState[] proxyStates) {
        return proxyStatesIndex;
    }

    protected int mapFromStatesIndexToFallbackStatesIndex(int fromStatesIndex) {
        return fromStatesIndex;
    }

    protected void processResult(BlockState @Nullable [] proxyStates) {
        if (this.request.createFromToProxyMapping) {
            for (int fromStatesIndex = 0; fromStatesIndex < this.request.fromStates().length; fromStatesIndex++) {
                int finalFromStatesIndex = fromStatesIndex;
                this.event.manualMappings().registerNMS(builder -> {
                    builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                    builder.from(this.request.fromStates()[finalFromStatesIndex]);
                    builder.to(proxyStates != null ? proxyStates[this.mapFromStatesIndexToProxyStatesIndex(finalFromStatesIndex, proxyStates)] : this.request.fallbackStates()[this.mapFromStatesIndexToFallbackStatesIndex(finalFromStatesIndex)]);
                });
            }
        }
        if (this.request.createVanillaMappings) {
            for (int fromStatesIndex = 0; fromStatesIndex < this.request.fromStates().length; fromStatesIndex++) {
                int finalFromStatesIndex = fromStatesIndex;
                this.event.manualMappings().registerNMS(builder -> {
                    builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                    builder.from(this.request.fromStates()[finalFromStatesIndex]);
                    builder.to(this.request.fallbackStates()[this.mapFromStatesIndexToFallbackStatesIndex(finalFromStatesIndex)]);
                });
            }
        }
        if (proxyStates != null && this.request.createResourcePackBlockstatesEntries) {
            FiddleResourcePackConstructionImpl.get().addEventInitializer(event -> {
                for (int proxyIndex = 0; proxyIndex < proxyStates.length; proxyIndex++) {
                    int finalProxyIndex = proxyIndex;
                    BlockstatesFiddleResourcePackPath path = event.asset(ClientView.AwarenessLevel.RESOURCE_PACK, "blockstates", proxyStates[proxyIndex].getBlock().keyInBlockRegistry, "json").asBlockstates();
                    path.update(blockstates -> {
                        if (blockstates == null) {
                            blockstates = Blockstates.create();
                        }
                        blockstates.setVariant(proxyStates[finalProxyIndex].createCraftBlockData(), this.request.getBlockstatesVariant(this.mapProxyStatesIndexToFromStatesIndex(finalProxyIndex, proxyStates)));
                        return blockstates;
                    });
                }
            });
        }
        if (this.request.createItemMappings && this.request instanceof FromToItemRequestBuilderImpl<?> fromToItemRequest) {
            @Nullable Item fromItem = fromToItemRequest.fromItemMinecraft();
            if (fromItem != null) {
                Block fromItemBlock = fromItem instanceof BlockItem blockItem ? blockItem.getBlock() : ((FromToItemRequestBuilderImpl<?>) this.request).getBlockToInferFromItem();
                int fromItemBlockStateIndex = fromItemBlock == null ? -1 : Arrays.asList(this.request.fromStates()).indexOf(fromItemBlock.defaultBlockState());
                if (fromItemBlockStateIndex == -1) {
                    fromItemBlockStateIndex = Arrays.asList(this.request.fromStates()).indexOf(this.request.fromStates()[0].getBlock().defaultBlockState());
                    if (fromItemBlockStateIndex == -1) {
                        fromItemBlockStateIndex = 0;
                    }
                }
                int finalFromItemBlockStateIndex = fromItemBlockStateIndex;
                boolean proxyItemIsFallback = proxyStates == null;
                @Nullable Item proxyItem = proxyItemIsFallback ? fromToItemRequest.fallbackItemMinecraft() : FromToItemRequestBuilderImpl.inferItem(proxyStates[this.mapFromStatesIndexToProxyStatesIndex(fromItemBlockStateIndex, proxyStates)].getBlock());
                @Nullable Item fallbackItem = this.request.createVanillaMappings ? fromToItemRequest.fallbackItemMinecraft() : null;
                if (proxyItem != null || fallbackItem != null) {
                    ItemMappingsImpl.get().addEventInitializer(event -> {
                        if (proxyItem != null) {
                            BlockState itemBlockState = proxyItemIsFallback ? this.request.fallbackStates()[this.mapFromStatesIndexToFallbackStatesIndex(finalFromItemBlockStateIndex)] : proxyStates[this.mapFromStatesIndexToProxyStatesIndex(finalFromItemBlockStateIndex, proxyStates)];
                            @Nullable BlockItemStateProperties blockItemStateProperties = itemBlockState == itemBlockState.getBlock().defaultBlockState() ? null : new BlockItemStateProperties(itemBlockState.createCraftBlockData().toStates(false));
                            @Nullable Identifier itemModel = fromToItemRequest.getFromItemModel();
                            event.registerNMS(builder -> {
                                builder.awarenessLevel(ClientView.AwarenessLevel.RESOURCE_PACK);
                                builder.from(fromItem);
                                builder.to(handle -> {
                                    ItemMappingUtilitiesNMS.get().setItemWhilePreservingRest(handle, proxyItem);
                                    ItemStack itemStack = handle.getMutable();
                                    if (itemModel != null) {
                                        itemStack.set(DataComponents.ITEM_MODEL, itemModel);
                                    }
                                    if (blockItemStateProperties != null) {
                                        itemStack.set(DataComponents.BLOCK_STATE, blockItemStateProperties);
                                    }
                                });
                            });
                        }
                        if (fallbackItem != null) {
                            BlockState itemBlockState = this.request.fallbackStates()[this.mapFromStatesIndexToFallbackStatesIndex(finalFromItemBlockStateIndex)];
                            @Nullable BlockItemStateProperties blockItemStateProperties = itemBlockState == itemBlockState.getBlock().defaultBlockState() ? null : new BlockItemStateProperties(this.request.fallbackStates()[0].createCraftBlockData().toStates(false));
                            event.registerNMS(builder -> {
                                builder.awarenessLevel(ClientView.AwarenessLevel.VANILLA);
                                builder.from(fromItem);
                                builder.to(handle -> {
                                    ItemMappingUtilitiesNMS.get().setItemWhilePreservingRest(handle, fallbackItem);
                                    if (blockItemStateProperties != null) {
                                        handle.getMutable().set(DataComponents.BLOCK_STATE, blockItemStateProperties);
                                    }
                                });
                            });
                        }
                    });
                }
            }
        }
        if (this.request.resultConsumers != null) {
            US usedStates = this.createUsedStates(proxyStates);
            this.request.resultConsumers.forEach(consumer -> consumer.accept(usedStates));
        }
    }

}
