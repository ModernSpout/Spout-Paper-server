package org.fiddlemc.fiddle.impl.bukkit.enuminjection.material;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.bukkit.enuminjection.material.MaterialEnumNames;
import org.fiddlemc.fiddle.impl.bukkit.enuminjection.BukkitEnumNameMappingPipelineImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;

/**
 * The implementation of {@link MaterialEnumNames}.
 */
public final class MaterialEnumNameMappingPipelineImpl extends BukkitEnumNameMappingPipelineImpl<Triple<NamespacedKey, @Nullable BlockType, @Nullable ItemType>> implements MaterialEnumNames {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<MaterialEnumNames, MaterialEnumNameMappingPipelineImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(MaterialEnumNameMappingPipelineImpl.class);
        }

    }

    public static MaterialEnumNameMappingPipelineImpl get() {
        return (MaterialEnumNameMappingPipelineImpl) MaterialEnumNames.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_material_enum_name_mapping_pipeline";
    }

}
