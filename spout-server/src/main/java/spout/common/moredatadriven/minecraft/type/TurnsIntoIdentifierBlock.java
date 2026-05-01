package spout.common.moredatadriven.minecraft.type;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public interface TurnsIntoIdentifierBlock {

    @Nullable Identifier spout$getTurnsIntoIdentifier();

    void spout$setTurnsIntoIdentifier(@Nullable Identifier turnsIntoIdentifier);

}
