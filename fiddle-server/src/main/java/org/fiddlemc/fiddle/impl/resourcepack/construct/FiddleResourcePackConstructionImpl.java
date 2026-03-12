package org.fiddlemc.fiddle.impl.resourcepack.construct;

import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstructEvent;
import org.fiddlemc.fiddle.api.resourcepack.construct.FiddleResourcePackConstruction;
import org.fiddlemc.fiddle.impl.configuration.FiddleGlobalConfiguration;
import org.fiddlemc.fiddle.impl.resourcepack.send.FiddleResourcePackSending;
import org.fiddlemc.fiddle.impl.resourcepack.serve.FiddleResourcePackServing;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import java.util.Map;

/**
 * The implementation for {@link FiddleResourcePackConstruction}.
 */
public final class FiddleResourcePackConstructionImpl extends ComposableImpl<FiddleResourcePackConstructEvent, FiddleResourcePackConstructEventImpl> implements FiddleResourcePackConstruction {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<FiddleResourcePackConstruction, FiddleResourcePackConstructionImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(FiddleResourcePackConstructionImpl.class);
        }

    }

    public static FiddleResourcePackConstructionImpl get() {
        return (FiddleResourcePackConstructionImpl) FiddleResourcePackConstruction.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_resource_pack_construction";
    }

    private FiddleResourcePackConstructionImpl() {
    }

    @Override
    protected FiddleResourcePackConstructEventImpl createComposeEvent() {
        return new FiddleResourcePackConstructEventImpl();
    }

    @Override
    protected void copyInformationFromEvent(final FiddleResourcePackConstructEventImpl event) {
        Map<ClientView.AwarenessLevel, byte[]> packs;
        try {
            packs = event.buildPacks();
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while constructing the server resource pack", e);
        }
        byte[] vanillaPack = packs.get(ClientView.AwarenessLevel.RESOURCE_PACK);
        byte[] clientModPack = packs.get(ClientView.AwarenessLevel.CLIENT_MOD);
        // Initialize the packet sending
        FiddleResourcePackSending.initialize(vanillaPack, clientModPack);
        // Use the pack output as configured in the configuration
        // TODO save to file if enabled
        if (FiddleResourcePackServing.isEnabled()) {
            // Initialize the packet serving
            FiddleResourcePackServing.start(vanillaPack, clientModPack);
        }
    }

    /**
     * Whether constructing the resource pack is enabled.
     */
    public boolean isEnabled() {
        return FiddleGlobalConfiguration.get().generatedResourcePack.output.serveOverHttp.enabled;
    }

}
