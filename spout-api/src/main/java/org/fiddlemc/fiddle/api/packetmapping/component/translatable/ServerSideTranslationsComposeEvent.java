package org.fiddlemc.fiddle.api.packetmapping.component.translatable;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;

/**
 * Provides functionality to register server-side translations to the {@link ServerSideTranslations}.
 */
public interface ServerSideTranslationsComposeEvent extends LifecycleEvent {

    /**
     * The same as {@link #register(String, String, String, ServerSideTranslations.FallbackScope)},
     * but with:
     * <ul>
     *     <li>{@code locale} = {@code en_us}</li>
     *     <li>{@code fallbackScope} = {@link ServerSideTranslations.FallbackScope#ALL}</li>
     * </ul>
     */
    default void register(String key, String translation) {
        this.register(key, translation, "en_us", ServerSideTranslations.FallbackScope.ALL);
    }

    /**
     * The same as {@link #register(String, String, String, ServerSideTranslations.FallbackScope, boolean)},
     * but with {@code overrideClientSide} = {@code true}.
     */
    default void register(String key, String translation, String locale, ServerSideTranslations.FallbackScope fallbackScope) {
        this.register(key, translation, locale, fallbackScope, true);
    }

    /**
     * Register a new translation.
     *
     * <p>
     * Translations that are registered later
     * replace translations that are registered with the same parameters before.
     * </p>
     *
     * <p>
     * Translations can also replace Minecraft vanilla translations,
     * such as for the key "{@code block.minecraft.bookshelf}".
     * </p>
     *
     * @param key                The lower-case key, for example "{@code item.example.ash}".
     * @param translation        The desired translation, for example "{@code 灰}".
     * @param locale             A lower-case locale that exists in Minecraft,
     *                           for example "{@code ja_jp}" for Japanese.
     * @param fallbackScope      The extent to which the translation can serve as a fallback.
     * @param overrideClientSide Whether this translation also overrides translations already registered
     *                           on the client (such as vanilla names for Minecraft blocks and items).
     */
    void register(String key, String translation, String locale, ServerSideTranslations.FallbackScope fallbackScope, boolean overrideClientSide);

}
