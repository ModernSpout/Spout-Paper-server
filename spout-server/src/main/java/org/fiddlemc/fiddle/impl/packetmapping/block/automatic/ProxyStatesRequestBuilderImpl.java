package org.fiddlemc.fiddle.impl.packetmapping.block.automatic;

import org.fiddlemc.fiddle.api.packetmapping.block.automatic.ProxyStatesRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.UsedStates;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A base implementation of {@link ProxyStatesRequestBuilder}.
 */
public abstract class ProxyStatesRequestBuilderImpl<US extends UsedStates> implements ProxyStatesRequestBuilder<US> {

    public boolean createFromToProxyMapping;
    public boolean createProxyToVisualDuplicateMapping;
    public boolean createItemMappings;
    public boolean createVanillaMappings;
    public boolean createResourcePackBlockstatesEntries;
    public @Nullable List<Consumer<US>> resultConsumers;

    public ProxyStatesRequestBuilderImpl() {
        this.createFromToProxyMapping = true;
        this.createProxyToVisualDuplicateMapping = true;
        this.createItemMappings = true;
        this.createVanillaMappings = true;
        this.createResourcePackBlockstatesEntries = true;
        this.resultConsumers = null;
    }

    @Override
    public void createFromToProxyMapping(boolean createFromToProxyMapping) {
        this.createFromToProxyMapping = createFromToProxyMapping;
    }

    @Override
    public boolean createFromToProxyMapping() {
        return this.createFromToProxyMapping;
    }

    @Override
    public void createProxyToVisualDuplicateMapping(boolean createProxyToVisualDuplicateMapping) {
        this.createProxyToVisualDuplicateMapping = createProxyToVisualDuplicateMapping;
    }

    @Override
    public boolean createProxyToVisualDuplicateMapping() {
        return this.createProxyToVisualDuplicateMapping;
    }

    @Override
    public void createItemMappings(boolean createItemMappings) {
        this.createItemMappings = createItemMappings;
    }

    @Override
    public boolean createItemMappings() {
        return this.createItemMappings;
    }

    @Override
    public void createVanillaMappings(boolean createVanillaMappings) {
        this.createVanillaMappings = createVanillaMappings;
    }

    @Override
    public boolean createVanillaMappings() {
        return this.createVanillaMappings;
    }

    @Override
    public void createResourcePackBlockstatesEntries(boolean createResourcePackBlockstatesEntries) {
        this.createResourcePackBlockstatesEntries = createResourcePackBlockstatesEntries;
    }

    @Override
    public boolean createResourcePackBlockstatesEntries() {
        return this.createResourcePackBlockstatesEntries;
    }

    @Override
    public void useResult(Consumer<US> resultConsumer) {
        if (this.resultConsumers == null) {
            this.resultConsumers = new ArrayList<>(1);
        }
        this.resultConsumers.add(resultConsumer);
    }

    public void validateArguments() {
    }

}
