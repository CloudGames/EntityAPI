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

package org.entityapi.api.plugin;

import com.captainbern.minecraft.reflection.MinecraftReflection;
import org.bukkit.plugin.Plugin;
import org.entityapi.api.EntityManager;

public class EntityAPI {

    /**
     * 'key' used to identify entities as an EntityAPI "NPC" without using the API.
     */
    public static final String ENTITY_METADATA_MARKER = "EntityAPI";

    /**
     * Some NMS paths for internals
     */
    public static final String INTERNAL_NMS_PATH = "org.entityapi.nms." + MinecraftReflection.getVersionTag();

    private static IEntityAPICore CORE;

    public static void setCore(IEntityAPICore core) {
        if (CORE != null) {
            throw new RuntimeException("Core already set!");
        }
        CORE = core;
    }

    public static IEntityAPICore getCore() {
        return CORE;
    }

    /**
     * Creates an EntityManager for the given Plugin.
     *
     * @param owningPlugin The plugin the EntityManager should be assigned to.
     *
     * @return The EntityManager object for the given plugin.
     */
    public static EntityManager createManager(Plugin owningPlugin) {
        return CORE.createManager(owningPlugin);
    }

    /**
     * Creates an EntityManager for the given Plugin.
     *
     * @param owningPlugin The plugin the EntityManager should be assigned to.
     * @param keepInMemory Whether or not the EntityManager should keep every entity in memory. Even when the entity
     *                     died.
     *
     * @return The EntityManager for the given plugin.
     */
    public static EntityManager createManager(Plugin owningPlugin, boolean keepInMemory) {
        return CORE.createManager(owningPlugin, keepInMemory);
    }

    /**
     * Registers an EntityManager with the given name.
     *
     * @param name    The name the EntityManager will be registered with.
     * @param manager The EntityManager.
     */
    public static void registerManager(String name, EntityManager manager) {
        CORE.registerManager(name, manager);
    }

    /**
     * Returns true if the given plugin has an EntityManager.
     *
     * @param plugin The plugin that should be checked.
     *
     * @return Whether or not the plugin has an EntityManager.
     */
    public static boolean hasEntityManager(Plugin plugin) {
        return CORE.hasEntityManager(plugin);
    }

    public static boolean hasEntityManager(String pluginName) {
        return CORE.hasEntityManager(pluginName);
    }

    public static EntityManager getManagerFor(Plugin plugin) {
        return CORE.getManagerFor(plugin);
    }

    public static EntityManager getManagerFor(String pluginName) {
        return CORE.getManagerFor(pluginName);
    }
}
