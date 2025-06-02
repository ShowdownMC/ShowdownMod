package io.github.pixityomg.showdownmod;

import io.github.pixityomg.showdownmod.goals.AscendingExplodingIronGolemGoal;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = Showdownmod.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide()) {
            Component welcomeMessage = Component.literal("§6Welcome to ShowdownSMP Modded.");
            player.displayClientMessage(welcomeMessage, true);
        }
    }

    /**
     * Event listener for when an entity joins the level.
     * Used to inject custom AI goals into Iron Golems.
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (Config.enableAscendingExplodingGolems && event.getEntity() instanceof IronGolem golem) {

            Set<WrappedGoal> goalsToRemove = new HashSet<>(golem.goalSelector.getAvailableGoals());

            for (WrappedGoal wrappedGoal : goalsToRemove) {
                golem.goalSelector.removeGoal(wrappedGoal.getGoal());
            }

            golem.goalSelector.addGoal(1, new AscendingExplodingIronGolemGoal(
                    golem,
                    Config.golemAscentSpeed,
                    Config.golemExplosionHeight,
                    Config.golemExplosionPower,
                    Config.golemIronDropAmount
            ));

            golem.setNoGravity(true);
        }
    }
}
