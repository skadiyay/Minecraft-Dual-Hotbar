package com.dualhotbar.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection helpers used by mixins that need to reach members declared in a
 * <em>superclass</em> of the mixin target class.
 *
 * <p>Mixin 0.8.7's {@code @Shadow} only resolves members declared directly in
 * the mixin target class (see {@code TargetClassContext.findAliasedMethod});
 * members inherited from a parent class are not found, and in NeoForge 1.21's
 * official-mappings runtime there is no remapper to fall back to, so a
 * {@code @Shadow} of a superclass member throws {@code InvalidMixinException}.
 * Since the runtime uses Mojang (official) names, reflection resolves them
 * directly and is a safe replacement.</p>
 */
public final class MixinReflect {
    private MixinReflect() {
    }

    /** Finds a method declared anywhere in the given class hierarchy. */
    public static Method method(Class<?> owner, String name, Class<?>... params) {
        Class<?> c = owner;
        while (c != null) {
            try {
                Method m = c.getDeclaredMethod(name, params);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ignored) {
                c = c.getSuperclass();
            }
        }
        throw new RuntimeException("Reflection: method " + name + " not found in hierarchy of " + owner.getName());
    }

    /** Finds a field declared anywhere in the given class hierarchy. */
    public static Field field(Class<?> owner, String name) {
        Class<?> c = owner;
        while (c != null) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            }
        }
        throw new RuntimeException("Reflection: field " + name + " not found in hierarchy of " + owner.getName());
    }
}
