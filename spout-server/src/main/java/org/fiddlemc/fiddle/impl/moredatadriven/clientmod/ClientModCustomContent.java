package org.fiddlemc.fiddle.impl.moredatadriven.clientmod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.type.ItemTypes;
import java.util.List;

/**
 * An encodable object containing all custom content sent to the client.
 */
public record ClientModCustomContent(
    List<Block> blocks,
    List<Item> items
) {

    public static final MapCodec<ClientModCustomContent> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        BlockTypes.CODEC.codec().listOf().fieldOf("blocks").forGetter(ClientModCustomContent::blocks),
        ItemTypes.CODEC.codec().listOf().fieldOf("items").forGetter(ClientModCustomContent::items)
    ).apply(instance, ClientModCustomContent::new));

    public static final Codec<ClientModCustomContent> CODEC = MAP_CODEC.codec();

}
