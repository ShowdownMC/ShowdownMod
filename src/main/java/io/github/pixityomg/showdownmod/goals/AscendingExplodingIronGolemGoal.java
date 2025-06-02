package io.github.pixityomg.showdownmod.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.EnumSet;

/**
 * A custom AI Goal for Iron Golems that makes them ascend into the sky,
 * explode at a certain height, and drop iron ingots.
 */
public class AscendingExplodingIronGolemGoal extends Goal {
    private final IronGolem golem;
    private final double ascentSpeed;
    private final double explosionHeight;
    private final double explosionPower;
    private final int ironDropAmount;
    private boolean exploded = false;

    public AscendingExplodingIronGolemGoal(IronGolem golem, double ascentSpeed, double explosionHeight, double explosionPower, int ironDropAmount) {
        this.golem = golem;
        this.ascentSpeed = ascentSpeed;
        this.explosionHeight = explosionHeight;
        this.explosionPower = explosionPower;
        this.ironDropAmount = ironDropAmount;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * This goal should always be able to use if not yet exploded.
     * It's intended to be the primary behavior.
     */
    @Override
    public boolean canUse() {
        return !exploded;
    }

    /**
     * This goal should continue as long as the golem hasn't exploded yet.
     */
    @Override
    public boolean canContinueToUse() {
        return !exploded && this.golem.isAlive();
    }

    /**
     * Called when the goal starts.
     * Ensures the golem is set to no gravity so it can float upwards.
     */
    @Override
    public void start() {
        this.golem.setNoGravity(true);
    }

    /**
     * Called every tick while the goal is active.
     * Handles ascending and triggering the explosion.
     */
    @Override
    public void tick() {
        if (exploded) return;

        this.golem.setDeltaMovement(0, ascentSpeed, 0);

        if (this.golem.getY() >= explosionHeight) {
            triggerExplosion();
            exploded = true;
            this.golem.discard();
        }
    }

    /**
     * Triggers the explosion and drops iron ingots.
     */
    private void triggerExplosion() {
        this.golem.level().explode(
                this.golem,
                this.golem.getX(),
                this.golem.getY(),
                this.golem.getZ(),
                (float) explosionPower,
                ExplosionInteraction.BLOCK
        );

        for (int i = 0; i < ironDropAmount; i++) {
            ItemEntity itemEntity = new ItemEntity(this.golem.level(),
                    this.golem.getX(),
                    this.golem.getY(),
                    this.golem.getZ(),
                    new ItemStack(Items.IRON_INGOT));
            this.golem.level().addFreshEntity(itemEntity);
        }
    }

    /**
     * Resets the goal. Not strictly necessary if the golem discards itself,
     * but good practice.
     */
    @Override
    public void stop() {
        // No specific cleanup needed here as the golem discards itself
    }
}
