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

package org.entityapi.nms.v1_7_R1.entity.mind.behaviour.goals;

import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.Vec3D;
import org.entityapi.api.ControllableEntity;
import org.entityapi.api.mind.behaviour.BehaviourType;
import org.entityapi.nms.v1_7_R1.NMSEntityUtil;
import org.entityapi.nms.v1_7_R1.RandomPositionGenerator;
import org.entityapi.nms.v1_7_R1.entity.mind.behaviour.BehaviourBase;

public class BehaviourPanic extends BehaviourBase {

    private double panicX;
    private double panicY;
    private double panicZ;

    public BehaviourPanic(ControllableEntity controllableEntity) {
        super(controllableEntity);
    }

    @Override
    public BehaviourType getType() {
        return BehaviourType.ONE;
    }

    @Override
    public String getDefaultKey() {
        return "Panic";
    }

    @Override
    public boolean shouldStart() {
        if (this.getHandle().getLastDamager() == null && !this.getHandle().isBurning()) {
            return false;
        } else {
            Vec3D vec3d = RandomPositionGenerator.a(this.getHandle(), 5, 4);

            if (vec3d == null) {
                return false;
            } else {
                this.panicX = vec3d.c;
                this.panicY = vec3d.d;
                this.panicZ = vec3d.e;
                return true;
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        // CraftBukkit start - introduce a temporary timeout hack until this is fixed properly
        if ((this.getHandle().ticksLived - this.getHandle().aK()) > 100) {
            this.getHandle().b((EntityLiving) null);
            return false;
        }
        // CraftBukkit end
        return !NMSEntityUtil.getNavigation(this.getHandle()).g();
    }

    @Override
    public void start() {
        NMSEntityUtil.getNavigation(this.getHandle()).a(this.panicX, this.panicY, this.panicZ, this.getControllableEntity().getSpeed());
    }

    @Override
    public void tick() {

    }
}