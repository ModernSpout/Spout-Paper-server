package org.fiddlemc.fiddle.impl.packetmapping.block.breakspeed;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import spout.common.branding.SpoutNamespace;
import org.jspecify.annotations.Nullable;
import java.util.Collection;

/**
 * A utility class for the modification of {@link Attributes#BLOCK_BREAK_SPEED}.
 */
public final class BlockBreakSpeedAttributeModification {

    private BlockBreakSpeedAttributeModification() {
        throw new UnsupportedOperationException();
    }

    /**
     * The {@link Identifier} for {@link AttributeModifier}s that serve to change the
     * {@link Attributes#BLOCK_BREAK_SPEED} to let the client calculate the same value as the server.
     */
    public static final Identifier SERVER_TO_CLIENT_MODIFIER_IDENTIFIER = Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, "server_to_client");

    public static Collection<AttributeModifier> withServerToClientModifierIfNecessary(AttributeInstance attributeInstance, Collection<AttributeModifier> modifiers, @Nullable ServerPlayer selfPlayer) {
        if (attributeInstance.getAttribute() == Attributes.BLOCK_BREAK_SPEED) {
            if (selfPlayer != null) {
                float serverToClientSideBlockBreakFactor = selfPlayer.serverToClientSideBlockBreakSpeedFactor;
                if (serverToClientSideBlockBreakFactor != 1) {
                    modifiers = new java.util.ArrayList<>(modifiers);
                    modifiers.add(new AttributeModifier(SERVER_TO_CLIENT_MODIFIER_IDENTIFIER, serverToClientSideBlockBreakFactor - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            }
        }
        return modifiers;
    }

}
