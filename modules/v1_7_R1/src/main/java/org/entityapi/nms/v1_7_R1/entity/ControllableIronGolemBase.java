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

package org.entityapi.nms.v1_7_R1.entity;

import org.bukkit.entity.IronGolem;
import org.entityapi.api.ControllableEntityType;
import org.entityapi.api.EntityManager;
import org.entityapi.api.EntitySound;
import org.entityapi.api.entity.ControllableIronGolem;

public class ControllableIronGolemBase extends ControllableBaseEntity<IronGolem, ControllableIronGolemEntity> implements ControllableIronGolem {


    public ControllableIronGolemBase(int id, EntityManager manager) {
        super(id, ControllableEntityType.IRON_GOLEM, manager);
    }

    public ControllableIronGolemBase(int id, ControllableIronGolemEntity entityHandle, EntityManager manager) {
        this(id, manager);
        this.handle = entityHandle;
        this.loot = entityHandle.getDefaultMaterialLoot();
    }

    @Override
    public void initSounds() {
        this.setSound(EntitySound.HURT, "mob.irongolem.hit");
        this.setSound(EntitySound.DEATH, "mob.irongolem.death");
        this.setSound(EntitySound.THROW, "mob.irongolem.throw");
    }
}