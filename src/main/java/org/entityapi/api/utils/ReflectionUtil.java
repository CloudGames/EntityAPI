/*
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

/*
 * This file is part of EchoPet.
 *
 * EchoPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EchoPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EchoPet.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.entityapi.api.utils;

import org.bukkit.Bukkit;
import org.entityapi.api.plugin.EntityAPI;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {

    public static String NMS_PATH = getNMSPackageName();
    public static String CBC_PATH = getCBCPackageName();
    public static String COMPAT_NMS_PATH = "org.entityapi.nms." + getServerVersion();

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static String getNMSPackageName() {
        return "net.minecraft.server." + getServerVersion();
    }

    public static String getCBCPackageName() {
        return "org.bukkit.craftbukkit." + getServerVersion();
    }

    /**
     * Class stuff
     */

    public static Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            EntityAPI.LOGGER_REFLECTION.warning("Could not find class: " + name + "!");
            return null;
        }
    }

    public static Class getVersionedClass(String classPath) {
        return getClass(COMPAT_NMS_PATH + "." + classPath);
    }

    public static Class getControllableEntityClass(String classIdentifier, boolean nms) {
        return getVersionedClass("entity.Controllable" + classIdentifier + (nms ? "Entity" : "Base"));
    }

    public static Class getNMSClass(String className) {
        return getClass(NMS_PATH + "." + className);
    }

    public static Class getCBCClass(String classPath) {
        return getClass(CBC_PATH + "." + classPath);
    }

    /**
     * Field stuff
     */

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            return field;
        } catch (NoSuchFieldException e) {
            EntityAPI.LOGGER_REFLECTION.warning("No such field: " + fieldName + "!");
            return null;
        }
    }

    public static <T> T getField(Class<?> clazz, String fieldName, Object instance) {
        try {
            return (T) getField(clazz, fieldName).get(instance);
        } catch (IllegalAccessException e) {
            EntityAPI.LOGGER_REFLECTION.warning("Failed to access field: " + fieldName + "!");
            return null;
        }
    }

    public static void setField(Class<?> clazz, String fieldName, Object instance, Object value) {
        try {
            getField(clazz, fieldName).set(instance, value);
        } catch (IllegalAccessException e) {
            EntityAPI.LOGGER_REFLECTION.warning("Could not set new field value for: " + fieldName);
        }
    }

    public static <T> T getField(Field field, Object instance) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            EntityAPI.LOGGER_REFLECTION.warning("Failed to retrieve field: " + field.getName());
            return null;
        }
    }

    /**
     * Method stuff
     */

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        try {
            return clazz.getDeclaredMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            EntityAPI.LOGGER_REFLECTION.warning("No such method: " + methodName + "!");
            return null;
        }
    }

    public static <T> T invokeMethod(Method method, Object instance, Object... args) {
        try {
            return (T) method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            EntityAPI.LOGGER_REFLECTION.warning("Failed to access method: " + method.getName() + "!");
            return null;
        } catch (InvocationTargetException e) {
            EntityAPI.LOGGER_REFLECTION.warning("Failed to invoke method: " + method.getName() + "!");
            e.printStackTrace();
            return null;
        }
    }
}
