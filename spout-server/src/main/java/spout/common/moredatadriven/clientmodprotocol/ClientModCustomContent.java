package spout.common.moredatadriven.clientmodprotocol;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import spout.common.moredatadriven.minecraft.type.ItemTypes;
import java.util.List;

/**
 * Contains all custom content sent by a Spout server.
 */
public class ClientModCustomContent {

    /**
     * The block JSONs.
     */
    final List<JsonElement> blockJSONs;

    /**
     * The item JSONs.
     */
    final List<JsonElement> itemJSONs;

    private ClientModCustomContent(List<JsonElement> blockJSONs, List<JsonElement> itemJSONs) {
        this.blockJSONs = blockJSONs;
        this.itemJSONs = itemJSONs;
    }

    static ClientModCustomContent create(List<Block> blocks, List<Item> items) {
        return new ClientModCustomContent(
            blocks.stream().map(block -> BlockTypes.CODEC.codec().encodeStart(JsonOps.INSTANCE, block).getOrThrow()).toList(),
            items.stream().map(item -> ItemTypes.CODEC.codec().encodeStart(JsonOps.INSTANCE, item).getOrThrow()).toList()
        );
    }

}
