/*
 * Copyright (C) EntityAPI Team
 *
 * This file is part of EntityAPI.
 *
 * EntityAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EntityAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EntityAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.entityapi.api.utils;

import com.captainbern.minecraft.reflection.MinecraftReflection;
import com.captainbern.reflection.ClassTemplate;
import com.captainbern.reflection.Reflection;
import com.captainbern.reflection.SafeField;
import com.captainbern.reflection.SafeMethod;
import com.captainbern.reflection.accessor.FieldAccessor;
import com.captainbern.reflection.accessor.MethodAccessor;
import org.bukkit.entity.Entity;
import org.entityapi.api.entity.type.bukkit.InsentientEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.captainbern.reflection.matcher.Matchers.withArguments;
import static com.captainbern.reflection.matcher.Matchers.withType;

public class EntityUtil {

    protected static FieldAccessor<List> GOALS;
    protected static FieldAccessor<List> ACTIVE_GOALS;

    protected static MethodAccessor<Void> ADD_GOAL;

    protected static FieldAccessor<Object> GOAL_SELECTOR;

    /**
     * Util classes shouldn't have a public or protected constructor
     */
    private EntityUtil() {
    }

    public static void addGoal(Object nmsEntityHandle, Object nmsGoalHandle, int priority) {
        if (ADD_GOAL == null) {
            initializeFields();
        }

        ADD_GOAL.invoke(nmsEntityHandle, nmsGoalHandle, priority);
    }

    public static List getGoals(Object nmsEntityHandle) {
        if (GOALS == null || GOAL_SELECTOR == null) {
            initializeFields();
        }

        return GOALS.get(GOAL_SELECTOR.get(nmsEntityHandle));
    }

    public static List getActiveGoals(Object nmsEntityHandle) {
        if (ACTIVE_GOALS == null || GOAL_SELECTOR == null) {
            initializeFields();
        }

        return ACTIVE_GOALS.get(GOAL_SELECTOR.get(nmsEntityHandle));
    }

    public static void clearGoals(Object nmsEntityHandle) {
        if (GOALS == null || ACTIVE_GOALS == null || GOAL_SELECTOR == null) {
            initializeFields();
        }

        GOALS.get(GOAL_SELECTOR.get(nmsEntityHandle)).clear();
        ACTIVE_GOALS.get(GOAL_SELECTOR.get(nmsEntityHandle)).clear();
    }

    protected static void initializeFields() {
        try {

            ClassTemplate goalTemplate = new Reflection().reflect(MinecraftReflection.getMinecraftClass("PathfinderGoalSelector"));

            List<SafeMethod<Void>> methodCandidates = goalTemplate.getSafeMethods(withArguments(int.class, MinecraftReflection.getMinecraftClass("PathfinderGoal")));
            if (methodCandidates.size() > 0) {
                ADD_GOAL = methodCandidates.get(0).getAccessor();
            } else {
                throw new RuntimeException("Failed to get the addGoal method!");
            }

            List<SafeField<List>> fieldCandidates = goalTemplate.getSafeFields(withType(List.class));
            if (fieldCandidates.size() > 1) {
                GOALS = fieldCandidates.get(0).getAccessor();
                ACTIVE_GOALS = fieldCandidates.get(0).getAccessor();
            } else {
                throw new RuntimeException("Failed to initialize the goal-lists!");
            }

            // The GoalSelector
            ClassTemplate entityTemplate = new Reflection().reflect(MinecraftReflection.getMinecraftClass("EntityInsentient"));
            List<SafeField<Object>> candidates = entityTemplate.getSafeFields(withType(goalTemplate.getReflectedClass()));

            if (candidates.size() > 0) {
                GOAL_SELECTOR = candidates.get(0).getAccessor(); // the normal selector is the first one
            } else {
                throw new RuntimeException("Failed to initialize the GoalSelector field for the entities");
            }

        } catch (Exception ಠ_ಠ) {
            throw new RuntimeException("Failed to initialize the goal-related fields!", ಠ_ಠ);
        }
    }

    // Entity conversion

    private static Map<Class<? extends Entity>, Class<?>> converterMap = new HashMap<>();

    static {
        converterMap.put(InsentientEntity.class, MinecraftReflection.getMinecraftClass("EntityInsentient"));
    }

    public static Object getNmsClassFor(Class<? extends Entity> bukkitHandle) {
        if (bukkitHandle == null) {
            return null;
        }

        try {

            return getNms(bukkitHandle);

        } catch (Exception ಥ_ಥ) {
            throw new RuntimeException("Failed to get the NMS version for: " + bukkitHandle.getCanonicalName(), ಥ_ಥ);
        }
    }

    private static Object getNms(Class<? extends Entity> type) {
        try {
            Class<?> nms = converterMap.get(type);

            if (nms == null) {

                try {

                    nms = getNmsFromMethod(type);
                } catch (Exception e) { // We failed, let's see if we can fix it with fields
                    nms = getNmsFromField(type);
                }

                converterMap.put(type, nms);
            }

            return nms;
        } catch (Exception ノಠ益ಠノ) {
            throw new RuntimeException("Failed to get the NMS class for: " + type.getCanonicalName(), ノಠ益ಠノ);
        }
    }

    protected static Class<?> getNmsFromField(Class<?> type) {
        final FieldAccessor<Object> accessor = new Reflection().reflect(type).getSafeFieldByName("handle").getAccessor();
        return accessor.getField().member().getType();
    }

    protected static Class<?> getNmsFromMethod(Class<?> type) {
        final MethodAccessor<Object> accessor = new Reflection().reflect(type).getSafeMethod("getHandle").getAccessor();
        return accessor.getMethod().member().getReturnType();
    }
}
