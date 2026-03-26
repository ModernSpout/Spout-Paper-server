package org.fiddlemc.testplugin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.Objects;

public class TestBlockEntity extends BlockEntity {
    private int count;

    public TestBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("example", "block_entity"))), pos, blockState);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("count", this.count);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.count = input.getIntOr("count", 1);
    }

    public int getCount() {
        return this.count;
    }

    public void incrementCount() {
        this.count++;
        this.setChanged();
    }
}
