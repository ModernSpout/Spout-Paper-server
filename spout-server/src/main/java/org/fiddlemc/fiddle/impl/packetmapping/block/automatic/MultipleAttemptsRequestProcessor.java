package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} that tries multiple attempts
 * (each of which tries something different).
 */
public abstract class MultipleAttemptsRequestProcessor<R extends ProxyStatesRequestBuilderImpl<?>, S> extends RequestProcessor<R> {

    protected MultipleAttemptsRequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    /**
     * Performs the next attempt.
     *
     * <p>
     * This method should (eventually) call either {@link #processAttemptFailure}, {@link #processFailure},
     * {@link #processAttemptSuccess} or {@link #processSuccess}, exactly once.
     * </p>
     */
    protected abstract void attemptNext();

    /**
     * @return Whether more attempts can be made.
     */
    protected abstract boolean hasMoreAttempts();

    protected void processAttemptSuccess(S result) {
        this.processSuccess(result);
    }

    protected void processAttemptFailure() {
        this.tryAttemptNext();
    }

    protected void tryAttemptNext() {
        if (this.hasMoreAttempts()) {
            this.attemptNext();
        } else {
            this.processFailure();
        }
    }

    protected abstract void processSuccess(S result);

    protected abstract void processFailure();

    @Override
    protected void processAfterValidateArguments() {
        this.tryAttemptNext();
    }

}
