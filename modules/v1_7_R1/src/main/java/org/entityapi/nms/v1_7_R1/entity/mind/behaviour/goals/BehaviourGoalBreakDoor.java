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

package org.entityapi.nms.v1_7_R1.entity.mind.behaviour.goals;

import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.EnumDifficulty;
import net.minecraft.server.v1_7_R1.IBlockAccess;
import org.entityapi.api.entity.ControllableEntity;

public class BehaviourGoalBreakDoor extends BehaviourGoalDoorInteract {

    private int breakTicks;
    private int lastBreakTicks = -1;

    public BehaviourGoalBreakDoor(ControllableEntity controllableEntity, boolean searchForIronDoor) {
        super(controllableEntity, searchForIronDoor);
    }

    @Override
    public String getDefaultKey() {
        return "Break Door";
    }

    @Override
    public boolean shouldStart() {
        return !super.shouldStart() ? false : (!this.getHandle().world.getGameRules().getBoolean("mobGriefing") ? false : !this.door.f((IBlockAccess) this.getHandle().world, this.pointX, this.pointY, this.pointZ));
    }

    @Override
    public boolean shouldContinue() {
        double dist = this.getHandle().e((double) this.pointX, (double) this.pointY, (double) this.pointZ);

        return this.breakTicks <= 240 && !this.door.f((IBlockAccess) this.getHandle().world, this.pointX, this.pointY, this.pointZ) && dist < 4.0D;
    }

    @Override
    public void start() {
        super.start();
        this.breakTicks = 0;
    }

    @Override
    public void finish() {
        super.finish();
        this.getHandle().world.d(this.getHandle().getId(), this.pointX, this.pointY, this.pointZ, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getHandle().aI().nextInt(20) == 0) {
            this.getHandle().world.triggerEffect(1010, this.pointX, this.pointY, this.pointZ, 0);
        }

        ++this.breakTicks;
        int i = (int) ((float) this.breakTicks / 240.0F * 10.0F);

        if (i != this.lastBreakTicks) {
            this.getHandle().world.d(this.getHandle().getId(), this.pointX, this.pointY, this.pointZ, i);
            this.lastBreakTicks = i;
        }

        if (this.breakTicks == 240 && this.getHandle().world.difficulty == EnumDifficulty.HARD) {
            // CraftBukkit start
            if (org.bukkit.craftbukkit.v1_7_R1.event.CraftEventFactory.callEntityBreakDoorEvent(this.getHandle(), this.pointX, this.pointY, this.pointZ).isCancelled()) {
                this.start();
                return;
            }
            // CraftBukkit end

            this.getHandle().world.setAir(this.pointX, this.pointY, this.pointZ);
            this.getHandle().world.triggerEffect(1012, this.pointX, this.pointY, this.pointZ, 0);
            this.getHandle().world.triggerEffect(2001, this.pointX, this.pointY, this.pointZ, Block.b(this.door));
        }
    }
}