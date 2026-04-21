package org.fiddlemc.fiddle.api.packetmapping.component;

import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;

/**
 * The different types of component that can be targeted by a {@link ComponentMappingBuilder}.
 */
public enum ComponentTarget {

    /**
     * Any component.
     */
    ALL,

    /**
     * Any component of type {@code "text"}.
     *
     * @see TextComponent
     */
    TEXT,

    /**
     * Any component of type {@code "translatable"}.
     *
     * @see TranslatableComponent
     */
    TRANSLATABLE,

    /**
     * Any {@link #TRANSLATABLE} component that does not have a {@link TranslatableComponent#fallback()}.
     */
    TRANSLATABLE_WITHOUT_FALLBACK,

    /**
     * Any component of type {@code "scoreboard"}.
     *
     * @see ScoreComponent
     */
    SCORE,

    /**
     * Any component of type {@code "selector"}.
     *
     * @see SelectorComponent
     */
    SELECTOR,

    /**
     * Any component of type {@code "keybind"}.
     *
     * @see KeybindComponent
     */
    KEYBIND,

    /**
     * Any component of type {@code "nbt"}.
     *
     * @see NBTComponent
     */
    NBT,

    /**
     * Any {@link #NBT} component with source {@code "block"}.
     *
     * @see BlockNBTComponent
     */
    NBT_BLOCK,

    /**
     * Any {@link #NBT} component with source {@code "entity"}.
     *
     * @see EntityNBTComponent
     */
    NBT_ENTITY,

    /**
     * Any {@link #NBT} component with source {@code "storage"}.
     *
     * @see StorageNBTComponent
     */
    NBT_STORAGE,

    /**
     * Any component of type {@code "object"}.
     *
     * @see ObjectComponent
     */
    OBJECT,

    /**
     * Any {@link #OBJECT} component with object {@code "atlas"}.
     *
     * @see SpriteObjectContents
     */
    OBJECT_ATLAS,

    /**
     * Any {@link #OBJECT} component with object {@code "player"}.
     *
     * @see PlayerHeadObjectContents
     */
    OBJECT_PLAYER

}
