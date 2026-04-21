package org.fiddlemc.fiddle.impl.packetmapping.block;

/**
 * A {@link BlockMappingsStep} that is defined by a function.
 */
public non-sealed interface FunctionBlockMappingsStep extends BlockMappingsStep {

    boolean requiresCoordinates();

}
