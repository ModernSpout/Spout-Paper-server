package org.fiddlemc.fiddle.impl.packetmapping.component.translatable;

import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslationsComposeEvent;
import java.util.HashMap;
import java.util.Locale;

/**
 * The implementation of {@link ServerSideTranslationsComposeEvent}.
 */
public final class ServerSideTranslationsComposeEventImpl implements PaperLifecycleEvent, ServerSideTranslationsComposeEvent {

    private final ServerSideTranslationsImpl registrar;

    ServerSideTranslationsComposeEventImpl(ServerSideTranslationsImpl registrar) {
        this.registrar = registrar;
    }

    @Override
    public void register(final String key, final String translation, final String locale, final ServerSideTranslations.FallbackScope fallbackScope, boolean overrideClientSide) {

        // Do some basic checks for the arguments
        String lowerCaseKey = key.toLowerCase(Locale.ROOT);
        if (!key.equals(lowerCaseKey)) {
            throw new IllegalArgumentException("The given key (" + key + ") is not lower-case");
        }
        String trimmedKey = key.trim();
        if (!key.equals(trimmedKey)) {
            throw new IllegalArgumentException("The given key (" + key + ") includes whitespace");
        }
        String lowerCaseLocale = locale.toLowerCase(Locale.ROOT);
        if (!locale.equals(lowerCaseLocale)) {
            throw new IllegalArgumentException("The given locale (" + locale + ") is not lower-case");
        }
        String trimmedLocale = locale.trim();
        if (!locale.equals(trimmedLocale)) {
            throw new IllegalArgumentException("The given locale (" + locale + ") includes whitespace");
        }

        // Add the translations
        ServerSideTranslationsImpl.RegisteredTranslationsForKey translationsForKey = this.registrar.registeredTranslations.computeIfAbsent(key, $ -> new ServerSideTranslationsImpl.RegisteredTranslationsForKey());
        ServerSideTranslations.ServerSideTranslation translationToRegister = new ServerSideTranslations.ServerSideTranslation(translation, overrideClientSide);
        if (fallbackScope.equals(ServerSideTranslations.FallbackScope.ALL)) {
            // Add as generic fallback
            if (translationsForKey.genericTranslation == null || overrideClientSide || !translationsForKey.genericTranslation.overrideClientSide()) {
                translationsForKey.genericTranslation = translationToRegister;
            }
        }
        if (!fallbackScope.equals(ServerSideTranslations.FallbackScope.NONE)) {
            // Add as group fallback
            int underscoreIndex = locale.indexOf('_');
            if (underscoreIndex > 0) {
                String group = locale.substring(0, underscoreIndex);
                if (translationsForKey.languageGroupTranslations == null) {
                    translationsForKey.languageGroupTranslations = new HashMap<>(2);
                    translationsForKey.languageGroupTranslations.put(group, translationToRegister);
                } else {
                    translationsForKey.languageGroupTranslations.compute(group, ($, existingTranslation) -> {
                        if (existingTranslation == null || overrideClientSide || !existingTranslation.overrideClientSide()) {
                            return translationToRegister;
                        }
                        return existingTranslation;
                    });
                }
            }
        }
        translationsForKey.localeTranslations.compute(locale, ($, existingTranslation) -> {
            if (existingTranslation == null || overrideClientSide || !existingTranslation.overrideClientSide()) {
                return translationToRegister;
            }
            return existingTranslation;
        });

    }

}
