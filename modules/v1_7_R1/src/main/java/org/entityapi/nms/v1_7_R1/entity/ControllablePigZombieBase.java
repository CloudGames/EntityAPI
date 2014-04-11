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

import org.bukkit.entity.PigZombie;
import org.entityapi.api.ControllableEntityType;
import org.entityapi.api.EntityManager;
import org.entityapi.api.EntitySound;
import org.entityapi.api.entity.ControllablePigZombie;

public class ControllablePigZombieBase extends ControllableBaseEntity<PigZombie, ControllablePigZombieEntity> implements ControllablePigZombie {

    public ControllablePigZombieBase(int id, EntityManager manager) {
        super(id, ControllableEntityType.PIG_ZOMBIE, manager);
    }

    public ControllablePigZombieBase(int id, ControllablePigZombieEntity entityHandle, EntityManager manager) {
        this(id, manager);
        this.handle = entityHandle;
        this.loot = entityHandle.getDefaultMaterialLoot();
    }

    @Override
    public void initSounds() {
        this.setSound(EntitySound.IDLE, "mob.zombiepig.zpig");
        this.setSound(EntitySound.HURT, "mob.zombiepig.zpighurt");
        this.setSound(EntitySound.DEATH, "mob.zombiepig.zpigdeath");
        this.setSound(EntitySound.STEP, "mob.zombie.step");
        this.setSound(EntitySound.ANGRY, "mob.zombiepig.zpigangry");
    }
}