package org.fiddlemc.fiddle.impl.packetmapping.component;

import io.papermc.paper.adventure.AdventureComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.ObjectContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.contents.data.BlockDataSource;
import net.minecraft.network.chat.contents.data.DataSource;
import net.minecraft.network.chat.contents.data.EntityDataSource;
import net.minecraft.network.chat.contents.data.StorageDataSource;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.network.chat.contents.objects.ObjectInfo;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import org.fiddlemc.fiddle.api.packetmapping.component.ComponentTarget;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for getting {@link ComponentTarget}.
 */
public final class ComponentTargetUtil {

    private ComponentTargetUtil() {
        throw new UnsupportedOperationException();
    }

    private static final ComponentTarget[] VALUES = ComponentTarget.values();

    public static ComponentTarget getMostSpecificTarget(Component component) {
        if (component instanceof MutableComponent mutableComponent) {
            ComponentContents contents = mutableComponent.getContents();
            if (contents instanceof PlainTextContents) {
                return ComponentTarget.TEXT;
            } else if (contents instanceof TranslatableContents translatableContents) {
                if (translatableContents.getFallback() == null) {
                    return ComponentTarget.TRANSLATABLE_WITHOUT_FALLBACK;
                }
                return ComponentTarget.TRANSLATABLE;
            } else if (contents instanceof ScoreContents) {
                return ComponentTarget.SCORE;
            } else if (contents instanceof SelectorContents) {
                return ComponentTarget.SELECTOR;
            } else if (contents instanceof KeybindContents) {
                return ComponentTarget.KEYBIND;
            } else if (contents instanceof NbtContents nbtContents) {
                DataSource dataSource = nbtContents.getDataSource();
                if (dataSource instanceof BlockDataSource) {
                    return ComponentTarget.NBT_BLOCK;
                } else if (dataSource instanceof EntityDataSource) {
                    return ComponentTarget.NBT_ENTITY;
                } else if (dataSource instanceof StorageDataSource) {
                    return ComponentTarget.NBT_STORAGE;
                }
                return ComponentTarget.NBT;
            } else if (contents instanceof ObjectContents(ObjectInfo objectInfo)) {
                if (objectInfo instanceof AtlasSprite) {
                    return ComponentTarget.OBJECT_ATLAS;
                } else if (objectInfo instanceof PlayerSprite) {
                    return ComponentTarget.OBJECT_PLAYER;
                }
                return ComponentTarget.OBJECT;
            }
        } else if (component instanceof AdventureComponent adventureComponentHolder) {
            net.kyori.adventure.text.Component adventureComponent = adventureComponentHolder.adventure$component();
            if (adventureComponent instanceof net.kyori.adventure.text.TextComponent) {
                return ComponentTarget.TEXT;
            } else if (adventureComponent instanceof net.kyori.adventure.text.TranslatableComponent translatableComponent) {
                if (translatableComponent.fallback() == null) {
                    return ComponentTarget.TRANSLATABLE_WITHOUT_FALLBACK;
                }
                return ComponentTarget.TRANSLATABLE;
            } else if (adventureComponent instanceof net.kyori.adventure.text.ScoreComponent) {
                return ComponentTarget.SCORE;
            } else if (adventureComponent instanceof net.kyori.adventure.text.SelectorComponent) {
                return ComponentTarget.SELECTOR;
            } else if (adventureComponent instanceof net.kyori.adventure.text.KeybindComponent) {
                return ComponentTarget.KEYBIND;
            } else if (adventureComponent instanceof net.kyori.adventure.text.NBTComponent<?, ?>) {
                if (adventureComponent instanceof net.kyori.adventure.text.BlockNBTComponent) {
                    return ComponentTarget.NBT_BLOCK;
                } else if (adventureComponent instanceof net.kyori.adventure.text.EntityNBTComponent) {
                    return ComponentTarget.NBT_ENTITY;
                } else if (adventureComponent instanceof net.kyori.adventure.text.StorageNBTComponent) {
                    return ComponentTarget.NBT_STORAGE;
                }
                return ComponentTarget.NBT;
            } else if (adventureComponent instanceof net.kyori.adventure.text.ObjectComponent objectComponent) {
                net.kyori.adventure.text.object.ObjectContents objectContents = objectComponent.contents();
                if (objectContents instanceof net.kyori.adventure.text.object.SpriteObjectContents) {
                    return ComponentTarget.OBJECT_ATLAS;
                } else if (objectContents instanceof net.kyori.adventure.text.object.PlayerHeadObjectContents) {
                    return ComponentTarget.OBJECT_PLAYER;
                }
                return ComponentTarget.OBJECT;
            }
        }
        return ComponentTarget.ALL;
    }

    /**
     * @param a A {@link ComponentTarget}.
     * @param b A {@link ComponentTarget}.
     * @return Whether target {@code a} implies target {@code b}.
     */
    public static boolean implies(ComponentTarget a, ComponentTarget b) {
        if (a == b) {
            return true;
        } else if (a == ComponentTarget.ALL) {
            return true;
        } else if (a == ComponentTarget.TRANSLATABLE) {
            return b == ComponentTarget.TRANSLATABLE_WITHOUT_FALLBACK;
        } else if (a == ComponentTarget.NBT) {
            return b == ComponentTarget.NBT_BLOCK || b == ComponentTarget.NBT_ENTITY || b == ComponentTarget.NBT_STORAGE;
        } else if (a == ComponentTarget.OBJECT) {
            return b == ComponentTarget.OBJECT_ATLAS || b == ComponentTarget.OBJECT_PLAYER;
        }
        return false;
    }

    public static List<ComponentTarget> expandTargets(Collection<ComponentTarget> targets) {
        return Arrays.stream(ComponentTarget.values()).filter(potentialTarget -> targets.stream().anyMatch(target -> implies(target, potentialTarget))).toList();
    }

    public static ComponentTarget getByOrdinal(int ordinal) {
        return VALUES[ordinal];
    }

}
