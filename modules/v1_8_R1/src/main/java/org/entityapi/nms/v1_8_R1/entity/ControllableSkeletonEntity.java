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

package org.entityapi.nms.v1_8_R1.entity;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.entityapi.api.entity.DespawnReason;
import org.entityapi.api.entity.EntitySound;
import org.entityapi.api.entity.mind.attribute.*;
import org.entityapi.api.entity.mind.behaviour.goals.BehaviourMeleeAttack;
import org.entityapi.api.entity.mind.behaviour.goals.BehaviourRangedAttack;
import org.entityapi.api.entity.type.ControllableSkeleton;
import org.entityapi.api.entity.type.nms.ControllableSkeletonHandle;
import org.entityapi.api.events.Action;
import org.entityapi.api.utils.EntityUtil;

import java.util.Calendar;

public class ControllableSkeletonEntity extends EntitySkeleton implements ControllableSkeletonHandle {

    private final ControllableSkeleton controllableEntity;

    public ControllableSkeletonEntity(World world, ControllableSkeleton controllableEntity) {
        super(world);
        this.controllableEntity = controllableEntity;
        EntityUtil.clearGoals(this);
    }

    @Override
    public ControllableSkeleton getControllableEntity() {
        return this.controllableEntity;
    }

    // EntityInsentient - Most importantly stops NMS goal selectors from ticking
    @Override
    protected void bn() {
        ++this.aV;

        this.w();

        this.getEntitySenses().a();

        //this.targetSelector.a();
        //this.goalSelector.a();

        this.getNavigation().f();

        this.bp();

        this.getControllerMove().c();
        this.getControllerLook().a();
        this.getControllerJump().b();
    }

    @Override
    public void move(double d0, double d1, double d2) {
        if (this.controllableEntity != null && this.controllableEntity.isStationary()) {
            return;
        }
        super.move(d0, d1, d2);
    }

    @Override
    public void h() {
        super.h();
        if (this.controllableEntity != null) {
            this.controllableEntity.getMind().getAttribute(TickAttribute.class).call(this.controllableEntity);
            this.controllableEntity.getMind().tick();
        }
    }

    @Override
    public void collide(Entity entity) {
        if (this.controllableEntity == null) {
            super.collide(entity);
            return;
        }

        if (!this.controllableEntity.getMind().getAttribute(CollideAttribute.class).call(this.controllableEntity, entity.getBukkitEntity()).isCancelled()) {
            super.collide(entity);
        }
    }

    @Override
    public boolean a(EntityHuman entity) {
        if (this.controllableEntity == null || !(entity.getBukkitEntity() instanceof Player)) {
            return super.c(entity);
        }

        return !this.controllableEntity.getMind().getAttribute(InteractAttribute.class).call(this.controllableEntity, entity.getBukkitEntity(), Action.RIGHT_CLICK).isCancelled();
    }

    @Override
    public boolean damageEntity(DamageSource damageSource, float v) {
        if (this.controllableEntity != null && damageSource.getEntity() != null && damageSource.getEntity().getBukkitEntity() instanceof Player) {
            return !this.controllableEntity.getMind().getAttribute(InteractAttribute.class).call(this.controllableEntity, damageSource.getEntity().getBukkitEntity(), Action.LEFT_CLICK).isCancelled();
        }
        return super.damageEntity(damageSource, v);
    }

    @Override
    public void e(float xMotion, float zMotion) {
        float[] motion = new float[]{xMotion, (float) this.motY, zMotion};
        if (this.controllableEntity != null) {
            ControlledRidingAttribute controlledRidingAttribute = this.controllableEntity.getMind().getAttribute(ControlledRidingAttribute.class);
            if (controlledRidingAttribute != null) {
                controlledRidingAttribute.onRide(motion);
            }
        }
        this.motY = motion[1];
        super.e(motion[0], motion[2]);
    }

    @Override
    public void g(double x, double y, double z) {
        if (this.controllableEntity != null) {
            Vector velocity = this.controllableEntity.getMind().getAttribute(PushAttribute.class).call(this.controllableEntity, new Vector(x, y, z)).getPushVelocity();
            x = velocity.getX();
            y = velocity.getY();
            z = velocity.getZ();
        }
        super.g(x, y, z);
    }

    @Override
    public void die(DamageSource damagesource) {
        if (this.controllableEntity != null) {
            this.controllableEntity.getEntityManager().despawn(this.controllableEntity, DespawnReason.DEATH);
        }
        super.die(damagesource);
    }

    @Override
    protected String t() {
        return this.controllableEntity == null ? "mob.skeleton.say" : this.controllableEntity.getSound(EntitySound.IDLE);
    }

    @Override
    protected String aT() {
        return this.controllableEntity == null ? "mob.skeleton.say" : this.controllableEntity.getSound(EntitySound.HURT);
    }

    @Override
    protected String aU() {
        return this.controllableEntity == null ? "mob.skeleton.death" : this.controllableEntity.getSound(EntitySound.DEATH);
    }

    @Override
    protected void a(int i, int j, int k, Block block) {
        this.makeSound(this.controllableEntity == null ? "mob.skeleton.step" : this.controllableEntity.getSound(EntitySound.STEP), 0.15F, 1.0F);
    }

    @Override
    public GroupDataEntity a(GroupDataEntity groupdataentity) {
        groupdataentity = super.a(groupdataentity);
        if (this.world.worldProvider instanceof WorldProviderHell && this.aI().nextInt(5) > 0) {
            if (this.controllableEntity != null) {
                this.controllableEntity.getMind().getMovementBehaviourSelector().addBehaviour(new BehaviourMeleeAttack(this.controllableEntity, HumanEntity.class, false, 1.2D), 4);
            }
            this.setSkeletonType(1);
            this.setEquipment(0, new ItemStack(Items.STONE_SWORD));
            this.getAttributeInstance(GenericAttributes.e).setValue(4.0D);
        } else {
            this.controllableEntity.getMind().getMovementBehaviourSelector().addBehaviour(new BehaviourRangedAttack(this.controllableEntity, 60, 15.0F, 1.0D), 4);
            this.bA();
            this.bB();
        }

        this.h(this.random.nextFloat() < 0.55F * this.world.b(this.locX, this.locY, this.locZ));
        if (this.getEquipment(4) == null) {
            Calendar calendar = this.world.V();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
                this.setEquipment(4, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
                this.dropChances[4] = 0.0F;
            }
        }

        return groupdataentity;
    }
}