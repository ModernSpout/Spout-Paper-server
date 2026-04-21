package spout.common.branding;

/**
 * Holder for {@link #BRAND}.
 */
public final class SpoutBrandName {

    private SpoutBrandName() {
        throw new UnsupportedOperationException();
    }

    /**
     * The name of the Spout brand (used to identify the server to the client).
     *
     * <p>
     * Similar to {@code io.papermc.paper.ServerBuildInfoImpl.BRAND_PAPER_NAME}.
     * </p>
     */
    public static final String BRAND = "Spout";

}
