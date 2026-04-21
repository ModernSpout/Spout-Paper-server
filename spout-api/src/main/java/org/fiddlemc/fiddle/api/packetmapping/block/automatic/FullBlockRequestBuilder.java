package org.fiddlemc.fiddle.api.packetmapping.block.automatic;

/**
 * A {@link ProxyStatesRequestBuilder} for {@link AutomaticBlockMappings#fullBlock}.
 */
public interface FullBlockRequestBuilder extends FromBlockStateRequestBuilder<UsedStates.Single>, ToBlockStateRequestBuilder<UsedStates.Single> {

    // /**
    //  * @param preference The ordered list of preferred choices, from most preferred to least preferred.
    //  *                The list will be auto-completed with any {@link Choice}s not present.
    //  */
    // void preference(List<Choice> preference);
    //
    // /**
    //  * @return The current ordered list of preferred targets.
    //  * This contains every {@link Choice} exactly once.
    //  */
    // List<Choice> preference();
    //
    // /**
    //  * An enum representing the different types of choices that this builder can make.
    //  */
    // enum Choice {
    //     /**
    //      * <ul>
    //      *     <li>Sound: wood</li>
    //      *     <li>Vanilla tool: axe</li>
    //      * </ul>
    //      */
    //     NOTE_BLOCK,
    //     /**
    //      * <ul>
    //      *     <li>Sound: wood</li>
    //      *     <li>Vanilla tool: axe</li>
    //      * </ul>
    //      */
    //     INFESTED_STONE,
    //     /**
    //      * <ul>
    //      *     <li>Sound: wood</li>
    //      *     <li>Vanilla tool: axe</li>
    //      * </ul>
    //      */
    //     BEENEST,
    //     /**
    //      * <ul>
    //      *     <li>Sound: wood</li>
    //      *     <li>Vanilla tool: axe</li>
    //      * </ul>
    //      */
    //     BEE_HIVE,
    // }

}
