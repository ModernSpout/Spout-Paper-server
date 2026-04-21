package org.fiddlemc.fiddle.api.packetmapping;

import org.fiddlemc.fiddle.api.clientview.ClientView;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A builder to define a mapping that targets one or more particular {@link ClientView.AwarenessLevel}s.
 */
public interface AwarenessLevelMappingBuilder {

    /**
     * Sets the {@link ClientView.AwarenessLevel} to which this mapping will be applied.
     *
     * <p>
     * This replaces any previous value set with {@link #awarenessLevel}.
     * </p>
     */
    default void awarenessLevel(ClientView.AwarenessLevel awarenessLevel) {
        this.awarenessLevel(List.of(awarenessLevel));
    }

    /**
     * @see #awarenessLevel(ClientView.AwarenessLevel)
     */
    default void awarenessLevel(ClientView.AwarenessLevel[] awarenessLevels) {
        this.awarenessLevel(Arrays.asList(awarenessLevels));
    }

    /**
     * @see #awarenessLevel(ClientView.AwarenessLevel)
     */
    void awarenessLevel(Collection<ClientView.AwarenessLevel> awarenessLevels);

    /**
     * This mapping will be applied to every {@link ClientView.AwarenessLevel}.
     */
    default void everyAwarenessLevel() {
        this.awarenessLevel(ClientView.AwarenessLevel.getAll());
    }

    /**
     * This mapping will be applied to every {@link ClientView.AwarenessLevel}
     * in {@link ClientView.AwarenessLevel#getThatDoNotAlwaysUnderstandsAllServerSideTranslatables()}.
     */
    default void everyAwarenessLevelThatDoesNotAlwaysUnderstandAllServerSideTranslatables() {
        this.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideTranslatables());
    }

    /**
     * This mapping will be applied to every {@link ClientView.AwarenessLevel}
     * in {@link ClientView.AwarenessLevel#getThatDoNotAlwaysUnderstandsAllServerSideBlocks()}.
     */
    default void everyAwarenessLevelThatDoesNotAlwaysUnderstandAllServerSideBlocks() {
        this.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks());
    }

    /**
     * This mapping will be applied to every {@link ClientView.AwarenessLevel}
     * in {@link ClientView.AwarenessLevel#getThatDoNotAlwaysUnderstandsAllServerSideItems()}.
     */
    default void everyAwarenessLevelThatDoesNotAlwaysUnderstandAllServerSideItems() {
        this.awarenessLevel(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideItems());
    }

    /**
     * Adds a {@link ClientView.AwarenessLevel} to which this mapping will be applied.
     */
    void addAwarenessLevel(ClientView.AwarenessLevel awarenessLevel);

    /**
     * @see #addAwarenessLevel(ClientView.AwarenessLevel)
     */
    default void addAwarenessLevel(ClientView.AwarenessLevel[] awarenessLevels) {
        for (ClientView.AwarenessLevel value : awarenessLevels) {
            this.addAwarenessLevel(value);
        }
    }

    /**
     * @see #addAwarenessLevel(ClientView.AwarenessLevel)
     */
    default void addAwarenessLevel(Collection<ClientView.AwarenessLevel> awarenessLevels) {
        for (ClientView.AwarenessLevel value : awarenessLevels) {
            this.addAwarenessLevel(value);
        }
    }

}
