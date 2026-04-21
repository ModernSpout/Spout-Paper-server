package org.fiddlemc.fiddle.impl.packetmapping.block.claim;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.fiddlemc.fiddle.api.packetmapping.block.claim.ClaimRequestPriority;
import org.fiddlemc.fiddle.api.packetmapping.block.claim.ClaimRequestPriorityComparator;
import org.fiddlemc.fiddle.api.packetmapping.block.claim.ResourcePackBlockStateClaims;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

/**
 * The implementation for {@link ResourcePackBlockStateClaims}.
 *
 * <p>
 * For most states, claims are governed by their {@linkplain VisualDuplicatesImpl visual duplicates}.
 * For such states, this class tracks whether they have already been claimed,
 * and keeps track of how many states in a visual duplicates group have already been claimed,
 * to make sure that at least one state remains unclaimed.
 * That state is then also used as the visual duplicate to map claimed block states to.
 * </p>
 *
 * <p>
 * Some states have a unique visual representation, but are never used in vanilla.
 * This includes redstone dust that only points in 1 direction, and waterlogged double slabs.
 * These can also be claimed. If it then turns out that they do actually occur on the server,
 * they will be mapped to the most appropriate vanilla block (such as a non-waterlogged double slab).
 * </p>
 *
 * <p>
 * Other states, i.e. those that do not fit either of the above claiming strategies,
 * are not claimable.
 * </p>
 *
 * <p>
 * Note that claim requests to some block states that are normally claimable
 * may be blocked by the server's configuration. TODO Implement configuration for this
 * </p>
 */
public final class ResourcePackBlockStateClaimsImpl implements ResourcePackBlockStateClaims {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<ResourcePackBlockStateClaims, ResourcePackBlockStateClaimsImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(ResourcePackBlockStateClaimsImpl.class);
        }

    }

    public static ResourcePackBlockStateClaimsImpl get() {
        return (ResourcePackBlockStateClaimsImpl) ResourcePackBlockStateClaims.get();
    }

    private int nextClaimId;
    private final TreeSet<ClaimRequest> requests;

    public ResourcePackBlockStateClaimsImpl() {
        Comparator<ClaimRequestPriority> requestPriorityComparator = new ClaimRequestPriorityComparator(Object2DoubleMaps.emptyMap()); /* TODO fill with useful values based on how often block states occur */
        this.requests = new TreeSet<>(Comparator.comparing(ClaimRequest::priority, requestPriorityComparator).thenComparingInt(request -> -request.id));
    }

    public void processRequests() {
        new AllRequestsProcessingState().process();
    }

    public void claim(int[] states, ClaimRequestPriority priority, @Nullable Consumer<int @Nullable []> resultConsumer, @Nullable Consumer<int[]> visualDuplicatesConsumer, boolean orSimilar, boolean usingVanillaLook) {
        if (Arrays.stream(states).distinct().count() != states.length) {
            throw new IllegalArgumentException("states cannot contain duplicates");
        }
        this.requests.add(new ClaimRequest(this.nextClaimId++, states, priority, resultConsumer, visualDuplicatesConsumer, orSimilar, usingVanillaLook));
    }

    public void claim(List<BlockData> states, ClaimRequestPriority priority, @Nullable Consumer<@Nullable List<? extends BlockData>> resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer, boolean orSimilar, boolean usingVanillaLook) {
        this.claim(
            states.stream().mapToInt(state -> ((CraftBlockData) state).getState().indexInVanillaOnlyBlockStateRegistry).toArray(),
            priority,
            resultConsumer == null ? null : result -> resultConsumer.accept(result != null ? Arrays.stream(result).mapToObj(id -> VanillaOnlyBlockStateRegistry.get().byId(id).createCraftBlockData()).toList() : null),
            visualDuplicatesConsumer == null ? null : visualDuplicates -> visualDuplicatesConsumer.accept(Arrays.stream(visualDuplicates).mapToObj(id -> VanillaOnlyBlockStateRegistry.get().byId(id).createCraftBlockData()).toList()),
            orSimilar,
            usingVanillaLook
        );
    }

    public void claim(BlockData state, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer, @Nullable Consumer<BlockData> visualDuplicateConsumer, boolean usingVanillaLook) {
        this.claimAll(
            List.of(state),
            priority,
            resultConsumer,
            visualDuplicateConsumer == null ? null : visualDuplicates -> visualDuplicateConsumer.accept(visualDuplicates.getFirst()),
            usingVanillaLook
        );
    }

    public void claimAll(List<BlockData> states, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer, boolean usingVanillaLook) {
        this.claim(
            states,
            priority,
            resultConsumer == null ? null : result -> resultConsumer.accept(result != null),
            visualDuplicatesConsumer,
            false,
            usingVanillaLook
        );
    }

    public void claimOrSimilar(BlockData state, ClaimRequestPriority priority, @Nullable Consumer<@Nullable BlockData> resultConsumer, @Nullable Consumer<BlockData> visualDuplicatesConsumer, boolean usingVanillaLook) {
        this.claimAllOrSimilar(
            List.of(state),
            priority,
            resultConsumer == null ? null : result -> resultConsumer.accept(result != null ? result.getFirst() : null),
            visualDuplicatesConsumer == null ? null : visualDuplicates -> visualDuplicatesConsumer.accept(visualDuplicates.getFirst()),
            usingVanillaLook
        );
    }

    public void claimAllOrSimilar(List<BlockData> states, ClaimRequestPriority priority, @Nullable Consumer<@Nullable List<? extends BlockData>> resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer, boolean usingVanillaLook) {
        this.claim(
            states,
            priority,
            resultConsumer,
            visualDuplicatesConsumer,
            true,
            usingVanillaLook
        );
    }

    @Override
    public void claim(BlockData state, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer, @Nullable Consumer<BlockData> visualDuplicateConsumer) {
        this.claim(state, priority, resultConsumer, visualDuplicateConsumer, false);
    }

    @Override
    public void claimAll(List<BlockData> states, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer) {
        this.claimAll(states, priority, resultConsumer, visualDuplicatesConsumer, false);
    }

    @Override
    public void claimOrSimilar(BlockData state, ClaimRequestPriority priority, @Nullable Consumer<@Nullable BlockData> resultConsumer, @Nullable Consumer<BlockData> visualDuplicatesConsumer) {
        this.claimOrSimilar(state, priority, resultConsumer, visualDuplicatesConsumer, false);
    }

    @Override
    public void claimAllOrSimilar(List<BlockData> states, ClaimRequestPriority priority, @Nullable Consumer<@Nullable List<? extends BlockData>> resultConsumer, @Nullable Consumer<List<? extends BlockData>> visualDuplicatesConsumer) {
        this.claimAllOrSimilar(states, priority, resultConsumer, visualDuplicatesConsumer, false);
    }

    @Override
    public void claimUsingVanillaLook(BlockData state, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer) {
        this.claim(state, priority, resultConsumer, null, true);
    }

    @Override
    public void claimAllUsingVanillaLook(List<BlockData> states, ClaimRequestPriority priority, @Nullable BooleanConsumer resultConsumer) {
        this.claimAll(states, priority, resultConsumer, null, true);
    }

    @Override
    public void claimOrSimilarUsingVanillaLook(BlockData state, ClaimRequestPriority priority, @Nullable Consumer<@Nullable BlockData> resultConsumer) {
        this.claimOrSimilar(state, priority, resultConsumer, null, true);
    }

    @Override
    public void claimAllOrSimilarUsingVanillaLook(List<BlockData> states, ClaimRequestPriority priority, @Nullable Consumer<@Nullable List<? extends BlockData>> resultConsumer) {
        this.claimAllOrSimilar(states, priority, resultConsumer, null, true);
    }

    private static boolean isWaterloggedDoubleSlab(BlockState state) {
        return state.getBlock() instanceof SlabBlock && state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE && state.getValue(BlockStateProperties.WATERLOGGED);
    }

    private static boolean isSingleDirectionRedstoneWire(BlockState state) {
        return state.getBlock() == Blocks.REDSTONE_WIRE && Stream.of(BlockStateProperties.NORTH_REDSTONE, BlockStateProperties.EAST_REDSTONE, BlockStateProperties.SOUTH_REDSTONE, BlockStateProperties.WEST_REDSTONE).filter(property -> state.getValue(property) == RedstoneSide.NONE).count() == 3;
    }

    /**
     * Utility class that represents a claim request submitted to one of the
     * {@link ResourcePackBlockStateClaims} methods.
     */
    private record ClaimRequest(
        int id,
        int[] states,
        ClaimRequestPriority priority,
        @Nullable Consumer<@Nullable int[]> resultConsumer,
        @Nullable Consumer<int[]> visualDuplicatesConsumer,
        boolean orSimilar,
        boolean usingVanillaLook
    ) {
    }

    /**
     * Utility class that holds the state of processing during
     * {@link ResourcePackBlockStateClaimsImpl#processRequests()}.
     */
    private final class AllRequestsProcessingState {

        /**
         * The cached {@link VanillaOnlyBlockStateRegistry}.
         */
        final VanillaOnlyBlockStateRegistry registry;

        /**
         * The cached {@link VanillaOnlyBlockStateRegistry#size()}.
         */
        final int registrySize;

        /**
         * Which block states have been claimed,
         * by their {@link BlockState#indexInVanillaOnlyBlockStateRegistry}.
         */
        final boolean[] claimed;

        /**
         * The same as {@link #claimed},
         * but only if the claim is a non-vanilla-look claim.
         */
        final boolean[] claimedNonVanilla;

        /**
         * Which block states must be mapped to a visual duplicate,
         * indexed by their {@link BlockState#indexInVanillaOnlyBlockStateRegistry}.
         *
         * <p>
         * The states that do require a visual duplicate will have a non-null {@link IntConsumer},
         * to which the {@link BlockState#indexInVanillaOnlyBlockStateRegistry} of the visual duplicate
         * must be passed.
         * </p>
         */
        final @Nullable IntConsumer[] visualDuplicateListeners;

        /**
         * The number of non-vanilla-look claims on a visual duplicate group returned by
         * {@link VisualDuplicatesImpl#getVisualDuplicates},
         * indexed by {@link VisualDuplicatesImpl.VisualDuplicateGroupImpl#getId()}.
         */
        final Int2IntMap visualDuplicateGroupNonVanillaClaimCount;

        /**
         * The same as {@link #visualDuplicateGroupNonVanillaClaimCount},
         * but for vanilla-look claims.
         */
        final Int2IntMap visualDuplicateGroupVanillaClaimCount;

        AllRequestsProcessingState() {
            this.registry = VanillaOnlyBlockStateRegistry.get();
            this.registrySize = registry.size();
            this.claimed = new boolean[registrySize];
            this.claimedNonVanilla = new boolean[registrySize];
            this.visualDuplicateListeners = new IntConsumer[registrySize];
            this.visualDuplicateGroupVanillaClaimCount = new Int2IntArrayMap();
            this.visualDuplicateGroupNonVanillaClaimCount = new Int2IntArrayMap();
        }

        boolean isClaimed(int state) {
            return this.claimed[state];
        }

        boolean isClaimedNonVanilla(int state) {
            return this.claimedNonVanilla[state];
        }

        boolean isClaimedVanilla(int state) {
            return this.isClaimed(state) && !this.isClaimedNonVanilla(state);
        }

        int getFirstUnclaimedOrVanillaClaimed(int[] states) {
            for (int state : states) {
                if (!this.claimedNonVanilla[state]) {
                    return state;
                }
            }
            throw new IllegalStateException("No unclaimed or vanilla-look claimed state in given states");
        }

        int getFirstUnclaimedOrVanillaClaimed(VisualDuplicatesImpl.VisualDuplicateGroupImpl group) {
            return this.getFirstUnclaimedOrVanillaClaimed(group.stateIndices);
        }

        /**
         * Calls any {@link ClaimRequest#visualDuplicatesConsumer}s.
         */
        void callVisualDuplicatesConsumers() {
            Int2IntMap visualDuplicateGroupMappingTarget = new Int2IntArrayMap();
            for (int state = 0; state < this.visualDuplicateListeners.length; state++) {
                @Nullable IntConsumer listener = this.visualDuplicateListeners[state];
                if (listener == null) continue;

                VisualDuplicatesImpl.@Nullable VisualDuplicateGroupImpl group = VisualDuplicatesImpl.get().getVisualDuplicates(state);
                int target;
                if (group != null) {

                    if (visualDuplicateGroupMappingTarget.containsKey(group.getId())) {
                        // Use the already chosen target for the visual duplicate group
                        target = visualDuplicateGroupMappingTarget.get(group.getId());
                    } else {
                        // Choose the best target from the visual duplicate group
                        target = this.getFirstUnclaimedOrVanillaClaimed(group);
                        visualDuplicateGroupMappingTarget.put(group.getId(), target);
                    }

                } else {

                    // The block state should not be mapped to a visual duplicate from a group, but from unused to used block state
                    BlockState blockState = VanillaOnlyBlockStateRegistry.get().byId(state);
                    if (isWaterloggedDoubleSlab(blockState)) {
                        target = blockState.setValue(BlockStateProperties.WATERLOGGED, false).indexInVanillaOnlyBlockStateRegistry;
                    } else if (isSingleDirectionRedstoneWire(blockState)) {
                        if (blockState.getValue(BlockStateProperties.SOUTH_REDSTONE) != RedstoneSide.NONE) {
                            target = blockState.setValue(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE).indexInVanillaOnlyBlockStateRegistry;
                        } else if (blockState.getValue(BlockStateProperties.NORTH_REDSTONE) != RedstoneSide.NONE) {
                            target = blockState.setValue(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE).indexInVanillaOnlyBlockStateRegistry;
                        } else if (blockState.getValue(BlockStateProperties.WEST_REDSTONE) != RedstoneSide.NONE) {
                            target = blockState.setValue(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE).indexInVanillaOnlyBlockStateRegistry;
                        } else if (blockState.getValue(BlockStateProperties.EAST_REDSTONE) != RedstoneSide.NONE) {
                            target = blockState.setValue(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE).indexInVanillaOnlyBlockStateRegistry;
                        } else {
                            throw new IllegalStateException("Invalid single direction redstone wire: " + blockState);
                        }
                    } else {
                        throw new IllegalStateException("State without known visual duplicate mapping: " + blockState);
                    }

                }

                listener.accept(target);
            }
        }

        void process() {

            // Process the claims
            while (true) {
                @Nullable ClaimRequest highestPriorityRequest = ResourcePackBlockStateClaimsImpl.this.requests.pollLast();
                if (highestPriorityRequest == null) {
                    break;
                }
                new SingleRequestProcessingState(highestPriorityRequest).process();
            }

            // Apply the visual duplicate mappings
            this.callVisualDuplicatesConsumers();

        }

        /**
         * Utility class that holds the state of processing for each request during
         * {@link AllRequestsProcessingState#process()}.
         */
        private final class SingleRequestProcessingState {

            /**
             * The {@link ClaimRequest}.
             */
            final ClaimRequest request;

            /**
             * The visual duplicate group used, for each of the requested states,
             * or null at an index where no visual duplicate group is used.
             */
            final VisualDuplicatesImpl.@Nullable VisualDuplicateGroupImpl[] visualDuplicateGroupPerRequestedState;

            /**
             * The number of claims that will be made to a visual duplicate group,
             * indexed by the {@link VisualDuplicatesImpl.VisualDuplicateGroupImpl#getId()}.
             */
            final Int2IntMap neededNewClaimCountPerVisualDuplicateGroup;

            /**
             * The actual state to claim, for each of the request states,
             * or -1 if not known yet.
             */
            final int[] definitiveStateToClaimPerRequestedState;

            SingleRequestProcessingState(ClaimRequest request) {
                this.request = request;
                this.visualDuplicateGroupPerRequestedState = new VisualDuplicatesImpl.VisualDuplicateGroupImpl[request.states.length];
                this.neededNewClaimCountPerVisualDuplicateGroup = new Int2IntArrayMap();
                this.definitiveStateToClaimPerRequestedState = new int[request.states.length];
                Arrays.fill(this.definitiveStateToClaimPerRequestedState, -1);
            }

            private @Nullable Boolean tryPreprocessRequestStateUsingVisualDuplicateGroup(int requestedStateI, int requestedState) {

                VisualDuplicatesImpl.@Nullable VisualDuplicateGroupImpl visualDuplicateGroup = VisualDuplicatesImpl.get().getVisualDuplicates(requestedState);
                if (visualDuplicateGroup == null) {
                    // Not in a visual duplicate group
                    return null;
                }

                this.visualDuplicateGroupPerRequestedState[requestedStateI] = visualDuplicateGroup;
                // Save that we need a claim for this visual duplicate group
                this.neededNewClaimCountPerVisualDuplicateGroup.compute(visualDuplicateGroup.getId(), ($, value) -> value != null ? value + 1 : 1);
                return true;

            }

            private @Nullable Boolean tryPreprocessRequestStateFromUnusedState(int requestedStateI, int requestedState) {
                BlockState blockState = VanillaOnlyBlockStateRegistry.get().byId(requestedState);

                if (isWaterloggedDoubleSlab(blockState) || isSingleDirectionRedstoneWire(blockState)) {

                    // There are no identical alternatives for any such state, so it must not be claimed
                    if (this.request.usingVanillaLook ? AllRequestsProcessingState.this.isClaimedNonVanilla(requestedState) : AllRequestsProcessingState.this.isClaimed(requestedState)) {
                        return false;
                    }
                    // Can claim the unused state
                    this.definitiveStateToClaimPerRequestedState[requestedStateI] = requestedState;
                    return true;

                }

                // Not an unused state
                return null;
            }

            private boolean preprocessState(int requestedStateI) {

                // Get the state
                int requestedState = this.request.states[requestedStateI];

                // Check if the state is already claimed as vanilla
                if (this.request.usingVanillaLook) {
                    if (AllRequestsProcessingState.this.isClaimedVanilla(requestedState)) {
                        // The block state is already claimed as vanilla
                        this.definitiveStateToClaimPerRequestedState[requestedStateI] = requestedState;
                        return true;
                    }
                }
                if (!this.request.orSimilar && AllRequestsProcessingState.this.isClaimed(requestedState)) {
                    // The block state is already claimed
                    return false;
                }

                // Try claiming it using visual duplicates
                @Nullable Boolean result = this.tryPreprocessRequestStateUsingVisualDuplicateGroup(requestedStateI, requestedState);
                if (result != null) {
                    return result;
                }
                // Check if the requested state is unused
                result = this.tryPreprocessRequestStateFromUnusedState(requestedStateI, requestedState);
                if (result != null) {
                    return result;
                }
                // Try claiming the block state directly if we desire the vanilla look
                if (this.request.usingVanillaLook) {
                    if (AllRequestsProcessingState.this.isClaimed(requestedState)) {
                        // The block state is already claimed as non-vanilla
                        return false;
                    }
                    // Claim it directly
                    this.definitiveStateToClaimPerRequestedState[requestedStateI] = requestedState;
                    return true;
                }

                // The block state is not claimable at all
                return false;

            }

            /**
             * Checks if enough claims available for each visual duplicate group from which we need claims.
             */
            private boolean checkEnoughClaimsAvailable() {
                if (!this.request.usingVanillaLook) {
                    for (Int2IntMap.Entry entry : this.neededNewClaimCountPerVisualDuplicateGroup.int2IntEntrySet()) {
                        int alreadyClaimedVanilla = AllRequestsProcessingState.this.visualDuplicateGroupVanillaClaimCount.get(entry.getIntKey());
                        int alreadyClaimed = AllRequestsProcessingState.this.visualDuplicateGroupNonVanillaClaimCount.get(entry.getIntKey()) + alreadyClaimedVanilla;
                        if (alreadyClaimed + entry.getIntValue() >= VisualDuplicatesImpl.get().getVisualDuplicates(entry.getIntKey()).stateIndices.length - (alreadyClaimedVanilla > 0 ? 1 : 0)) {
                            // Too many claims on a visual duplicate group
                            return false;
                        }
                    }
                }
                return true;
            }

            private void storeSuccessfulClaim() {

                // Initialize the visual duplicate container if we need it for visual duplicates consumers
                class VisualDuplicatesContainer {

                    int[] visualDuplicates;
                    int setCount;
                    Consumer<int[]> visualDuplicatesConsumer;

                    VisualDuplicatesContainer(int size, Consumer<int[]> visualDuplicatesConsumer) {
                        this.visualDuplicates = new int[size];
                        this.visualDuplicatesConsumer = visualDuplicatesConsumer;
                    }

                    void set(int i, int value) {
                        this.visualDuplicates[i] = value;
                        if (++this.setCount == this.visualDuplicates.length) {
                            this.visualDuplicatesConsumer.accept(this.visualDuplicates);
                        }
                    }

                }
                @Nullable VisualDuplicatesContainer visualDuplicateContainer = this.request.visualDuplicatesConsumer == null ? null : new VisualDuplicatesContainer(this.request.states.length, this.request.visualDuplicatesConsumer);

                for (int requestedStateI = 0; requestedStateI < this.request.states.length; requestedStateI++) {
                    int finalRequestedStateI = requestedStateI;
                    int stateToClaim = this.definitiveStateToClaimPerRequestedState[requestedStateI];

                    // Determine the state to claim if not known yet: only needed in the case of a state in a visual duplicate group
                    if (stateToClaim == -1) {

                        int state = this.request.states[requestedStateI];
                        VisualDuplicatesImpl.@Nullable VisualDuplicateGroupImpl group = this.visualDuplicateGroupPerRequestedState[requestedStateI];
                        if (group == null) {
                            throw new IllegalStateException("No visual duplicate group for requested state without an already determined state to claim");
                        }

                        if (this.request.usingVanillaLook) {

                            if (!this.request.orSimilar) {
                                // We simply claim the state itself
                                stateToClaim = this.request.states[requestedStateI];
                            } else {
                                // We must choose another state from the visual duplicate group
                                // First try an already claimed vanilla state
                                OptionalInt existingClaimed = Arrays.stream(VisualDuplicatesImpl.get().getVisualDuplicates(state).stateIndices).filter(alternative -> AllRequestsProcessingState.this.isClaimedVanilla(alternative)).findFirst();
                                if (existingClaimed.isPresent()) {
                                    stateToClaim = existingClaimed.getAsInt();
                                } else {
                                    // Make a new claim
                                    stateToClaim = Arrays.stream(VisualDuplicatesImpl.get().getVisualDuplicates(state).stateIndices).filter(alternative -> !AllRequestsProcessingState.this.isClaimed(alternative)).findFirst().getAsInt();
                                }
                            }
                            if (!AllRequestsProcessingState.this.isClaimedVanilla(stateToClaim)) {
                                // Increment the used claims count of the group
                                AllRequestsProcessingState.this.visualDuplicateGroupVanillaClaimCount.compute(group.getId(), ($, value) -> value != null ? value + 1 : 1);
                            }

                        } else {

                            if (!AllRequestsProcessingState.this.isClaimed(state) || !this.request.orSimilar) {
                                // We simply claim the state itself
                                stateToClaim = this.request.states[requestedStateI];
                            } else {
                                // We must choose another state from the visual duplicate group
                                stateToClaim = Arrays.stream(VisualDuplicatesImpl.get().getVisualDuplicates(state).stateIndices).filter(alternative -> !AllRequestsProcessingState.this.isClaimed(alternative) && Arrays.stream(this.request.states).noneMatch(given -> given == alternative)).findFirst().getAsInt();
                            }
                            // Increment the used claims count of the group
                            AllRequestsProcessingState.this.visualDuplicateGroupNonVanillaClaimCount.compute(group.getId(), ($, value) -> value != null ? value + 1 : 1);

                        }

                        // Save the state to claim
                        this.definitiveStateToClaimPerRequestedState[requestedStateI] = stateToClaim;

                    }

                    // Store the claim
                    AllRequestsProcessingState.this.claimed[stateToClaim] = true;
                    if (!this.request.usingVanillaLook) {
                        AllRequestsProcessingState.this.claimedNonVanilla[stateToClaim] = true;
                    }

                    // Mark if we need a mapping to a visual duplicate
                    if (visualDuplicateContainer != null) {
                        AllRequestsProcessingState.this.visualDuplicateListeners[stateToClaim] = visualDuplicate -> {
                            visualDuplicateContainer.set(finalRequestedStateI, visualDuplicate);
                        };
                    }

                }
            }

            void process() {

                // Check for each state what we need to do, and keep track of it
                for (int requestedStateI = 0; requestedStateI < this.request.states.length; requestedStateI++) {
                    boolean success = this.preprocessState(requestedStateI);
                    if (!success) {
                        if (this.request.resultConsumer != null) this.request.resultConsumer.accept(null);
                        return;
                    }
                }

                // Check if enough claims are available for visual duplicate groups
                if (!this.checkEnoughClaimsAvailable()) {
                    if (this.request.resultConsumer != null) this.request.resultConsumer.accept(null);
                    return;
                }

                // We will be successful, make the claim definite
                storeSuccessfulClaim();

                // Run the result consumer
                if (this.request.resultConsumer != null)
                    this.request.resultConsumer.accept(this.definitiveStateToClaimPerRequestedState);

            }

        }

    }

}
