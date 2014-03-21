package org.entityapi.api.mind.behaviour.goals;

import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.EntityWolf;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.entityapi.api.ControllableEntity;
import org.entityapi.api.mind.behaviour.Behaviour;
import org.entityapi.api.mind.behaviour.BehaviourType;
import org.entityapi.nms.NMSEntityUtil;

public class BehaviourBeg extends Behaviour {

    private Material[] materialsToBegFor;
    private EntityHuman player;
    private float range;
    private int ticks;

    public BehaviourBeg(ControllableEntity controllableEntity, Material[] materialsToBegFor) {
        this(controllableEntity, 8.0F, materialsToBegFor);
    }

    public BehaviourBeg(ControllableEntity controllableEntity, float range, Material[] materialsToBegFor) {
        super(controllableEntity);
        this.materialsToBegFor = materialsToBegFor;
        this.range = range;
    }

    @Override
    public BehaviourType getType() {
        return BehaviourType.TWO;
    }

    @Override
    public String getDefaultKey() {
        return "Beg";
    }

    @Override
    public boolean shouldStart() {
        this.player = this.getHandle().world.findNearbyPlayer(this.getHandle(), (double) this.range);
        return this.player == null ? false : this.isHoldingItem(this.player);
    }

    @Override
    public boolean shouldContinue() {
        if (!player.isAlive()) {
            return false;
        } else if (this.getHandle().e(this.player) > (double) (this.range * this.range)) {
            return false;
        }
        return this.ticks > 0 && this.isHoldingItem(this.player);
    }

    @Override
    public void start() {
        if (this.getHandle() instanceof EntityWolf) {
            ((EntityWolf) this.getHandle()).m(true);
        }
        this.ticks = 40 + this.getHandle().aI().nextInt(40);
    }

    @Override
    public void finish() {
        if (this.getHandle() instanceof EntityWolf) {
            ((EntityWolf) this.getHandle()).m(false);
        }
        this.player = null;
    }

    @Override
    public void tick() {
        NMSEntityUtil.getControllerLook(this.getHandle()).a(this.player.locX, this.player.locY + (double) this.player.getHeadHeight(), this.player.locZ, 10.0F, (float) NMSEntityUtil.getMaxHeadRotation(this.getHandle()));
        --this.ticks;
    }

    private boolean isHoldingItem(EntityHuman human) {
        ItemStack held = human.getBukkitEntity().getItemInHand();
        if (held == null) {
            return false;
        }

        for (Material m : this.materialsToBegFor) {
            if (held.getType() == m) {
                return true;
            }
        }
        return false;
    }
}