package org.fiddlemc.fiddle.impl.packetmapping.component.translatable;

import net.minecraft.locale.Language;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides some functionality for Minecraft locales.
 */
public final class MinecraftLocaleUtil {

    private MinecraftLocaleUtil() {
        throw new UnsupportedOperationException();
    }

    public static class KnownLocale {

        public final String lowerCaseLocale;
        public final @Nullable String languageGroup;
        public boolean isLanguageGroupDefault;

        private KnownLocale(String lowerCaseLocale, @Nullable String languageGroup, boolean isLanguageGroupDefault) {
            this.lowerCaseLocale = lowerCaseLocale;
            this.languageGroup = languageGroup != null ? languageGroup : computeLanguageGroup(lowerCaseLocale);
            this.isLanguageGroupDefault = isLanguageGroupDefault && languageGroup != null;
        }

        private KnownLocale(String lowerCaseLocale, @Nullable String languageGroup) {
            this(lowerCaseLocale, languageGroup, false);
        }

        private KnownLocale(String lowerCaseLocale, boolean isLanguageGroupDefault) {
            this(lowerCaseLocale, null, isLanguageGroupDefault);
        }

        private KnownLocale(String lowerCaseLocale) {
            this(lowerCaseLocale, null);
        }

    }

    /**
     * Based on the <a href="https://minecraft.wiki/w/Language">wiki page</a>.
     *
     * <p>
     * Some languages have a hard-coded language group.
     * This is merely an attempt at auto-completing missing translations,
     * with the understanding that some of the resulting translations
     * may be confusing, inaccurate or objectionable.
     * </p>
     */
    private static final KnownLocale[] knownLocales = {
        new KnownLocale("af_za", true),
        new KnownLocale("ar_sa", true),
        new KnownLocale("ast_es", true),
        new KnownLocale("az_az", true),
        new KnownLocale("ba_ru", true),
        new KnownLocale("bar", "de"),
        new KnownLocale("be_by", true),
        new KnownLocale("be_latn"),
        new KnownLocale("bg_bg", true),
        new KnownLocale("br_fr", true),
        new KnownLocale("brb", "nl"),
        new KnownLocale("bs_ba", true),
        new KnownLocale("ca_es", true),
        new KnownLocale("cs_cz", true),
        new KnownLocale("cy_gb", true),
        new KnownLocale("da_dk", true),
        new KnownLocale("de_at"),
        new KnownLocale("de_ch"),
        new KnownLocale("de_de", true),
        new KnownLocale("el_gr", true),
        new KnownLocale("en_au"),
        new KnownLocale("en_ca"),
        new KnownLocale("en_gb"),
        new KnownLocale("en_nz"),
        new KnownLocale("en_pt"),
        new KnownLocale("en_ud"),
        new KnownLocale("en_us", true),
        new KnownLocale("enp", "en"),
        new KnownLocale("enws", "en"),
        new KnownLocale("eo_uy", true),
        new KnownLocale("es_ar"),
        new KnownLocale("es_cl"),
        new KnownLocale("es_ec"),
        new KnownLocale("es_es", true),
        new KnownLocale("es_mx"),
        new KnownLocale("es_uy"),
        new KnownLocale("es_ve"),
        new KnownLocale("esan", "es"),
        new KnownLocale("et_ee", true),
        new KnownLocale("eu_es", true),
        new KnownLocale("fa_ir", true),
        new KnownLocale("fi_fi", true),
        new KnownLocale("fil_ph", true),
        new KnownLocale("fo_fo", true),
        new KnownLocale("fr_ca"),
        new KnownLocale("fr_fr", true),
        new KnownLocale("fra_de", true),
        new KnownLocale("fur_it", true),
        new KnownLocale("fy_nl", true),
        new KnownLocale("ga_ie", true),
        new KnownLocale("gd_gb", true),
        new KnownLocale("gl_es", true),
        new KnownLocale("hal_ua", true),
        new KnownLocale("haw_us", true),
        new KnownLocale("he_il", true),
        new KnownLocale("hi_in", true),
        new KnownLocale("hn_no", true),
        new KnownLocale("hr_hr", true),
        new KnownLocale("hu_hu", true),
        new KnownLocale("hy_am", true),
        new KnownLocale("id_id", true),
        new KnownLocale("ig_ng", true),
        new KnownLocale("io_en", true),
        new KnownLocale("is_is", true),
        new KnownLocale("isv", "sk"),
        new KnownLocale("it_it", true),
        new KnownLocale("ja_jp", true),
        new KnownLocale("jbo_en", true),
        new KnownLocale("ka_ge", true),
        new KnownLocale("kk_kz", true),
        new KnownLocale("kn_in", true),
        new KnownLocale("ko_kr", true),
        new KnownLocale("ksh", "de"),
        new KnownLocale("kw_gb", true),
        new KnownLocale("ky_kg", true),
        new KnownLocale("la_la", true),
        new KnownLocale("lb_lu", true),
        new KnownLocale("li_li", true),
        new KnownLocale("lmo", "it"),
        new KnownLocale("lo_la", true),
        new KnownLocale("lol_us", "en"),
        new KnownLocale("lt_lt", true),
        new KnownLocale("lv_lv", true),
        new KnownLocale("lzh", "zh"),
        new KnownLocale("mk_mk", true),
        new KnownLocale("mn_mn", true),
        new KnownLocale("ms_my", true),
        new KnownLocale("mt_mt", true),
        new KnownLocale("nah", "es"),
        new KnownLocale("nds_de", true),
        new KnownLocale("nl_be"),
        new KnownLocale("nl_nl", true),
        new KnownLocale("nn_no", true),
        new KnownLocale("no_no", true),
        new KnownLocale("nb_no", "no"),
        new KnownLocale("oc_fr", true),
        new KnownLocale("ovd", "sv"),
        new KnownLocale("pl_pl", true),
        new KnownLocale("pls", "en"),
        new KnownLocale("pt_br"),
        new KnownLocale("pt_pt", true),
        new KnownLocale("qcb_es", true),
        new KnownLocale("qid", "id"),
        new KnownLocale("qya_aa", true),
        new KnownLocale("ro_ro", true),
        new KnownLocale("rpr", "ru"),
        new KnownLocale("ru_ru", true),
        new KnownLocale("ry_ua", true),
        new KnownLocale("sah_sah", true),
        new KnownLocale("se_no", true),
        new KnownLocale("sk_sk", true),
        new KnownLocale("sl_si" ,true),
        new KnownLocale("so_so", true),
        new KnownLocale("sq_al", true),
        new KnownLocale("sr_cs"),
        new KnownLocale("sr_sp", true),
        new KnownLocale("sv_se", true),
        new KnownLocale("sxu", "de"),
        new KnownLocale("szl", "pl"),
        new KnownLocale("ta_in", true),
        new KnownLocale("th_th", true),
        new KnownLocale("tl_ph", true),
        new KnownLocale("tlh_aa", true),
        new KnownLocale("tok", true),
        new KnownLocale("tr_tr", true),
        new KnownLocale("tt_ru", true),
        new KnownLocale("tzo_mx", true),
        new KnownLocale("uk_ua", true),
        new KnownLocale("val_es", true),
        new KnownLocale("vec_it", true),
        new KnownLocale("vi_vn", true),
        new KnownLocale("vp_vl", true),
        new KnownLocale("yi_de", true),
        new KnownLocale("yo_ng", true),
        new KnownLocale("zh_cn", true),
        new KnownLocale("zh_hk"),
        new KnownLocale("zh_tw"),
        new KnownLocale("zlm_arab", true)
    };
    private static final Map<String, KnownLocale> knownLocaleByLowerCaseLocale = Arrays.stream(knownLocales).collect(Collectors.toMap(locale -> locale.lowerCaseLocale, Function.identity()));
    private static final Map<String, List<KnownLocale>> knownLocalesByLanguageGroup = Arrays.stream(knownLocales).filter(locale -> locale.languageGroup != null).collect(Collectors.groupingBy(locale -> locale.languageGroup));
    private static final Map<String, KnownLocale> defaultKnownLocaleByLanguageGroup = Arrays.stream(knownLocales).filter(locale -> locale.isLanguageGroupDefault).collect(Collectors.toMap(locale -> locale.languageGroup, Function.identity()));

    public static KnownLocale[] getKnownLocales() {
        return knownLocales;
    }

    /**
     * @return The known Minecraft locale for the given string,
     * or null if no locale is known.
     */
    public static @Nullable KnownLocale getKnownLocale(String lowerCaseLocale) {
        return knownLocaleByLowerCaseLocale.get(lowerCaseLocale);
    }

    public static List<KnownLocale> getKnownLocalesForLanguageGroup(String languageGroup) {
        return knownLocalesByLanguageGroup.getOrDefault(languageGroup, Collections.emptyList());
    }

    public static Collection<String> getLanguageGroups() {
        return knownLocalesByLanguageGroup.keySet();
    }

    public static @Nullable KnownLocale getDefaultKnownLocaleForLanguageGroup(String languageGroup) {
        return defaultKnownLocaleByLanguageGroup.get(languageGroup);
    }

    public static KnownLocale getDefault() {
        return getKnownLocale(Language.DEFAULT);
    }

    /**
     * @return The language group for a Minecraft locale,
     * or null if no language group is known.
     */
    public static @Nullable String getLanguageGroup(String lowerCaseLocale) {
        @Nullable KnownLocale knownLocale = getKnownLocale(lowerCaseLocale);
        return knownLocale != null ? knownLocale.languageGroup : computeLanguageGroup(lowerCaseLocale);
    }

    private static @Nullable String computeLanguageGroup(String lowerCaseLocale) {
        int underscoreIndex = lowerCaseLocale.indexOf('_');
        if (underscoreIndex > 0) {
            return lowerCaseLocale.substring(0, underscoreIndex);
        }
        return null;
    }

}
