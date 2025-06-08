package net.mirai.dimtr.event;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.data.ProgressionData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class XpMultiplierHandler {

    @SubscribeEvent
    public static void onMobDropExperience(LivingExperienceDropEvent event) {
        LivingEntity entity = event.getEntity();

        // Só aplicar no servidor
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Verificar se os multiplicadores de XP estão habilitados
        if (!DimTrConfig.SERVER.enableXpMultiplier.get()) {
            return;
        }

        // Só aplicar em mobs hostis específicos
        if (!isHostileMob(entity)) {
            return;
        }

        ProgressionData progressionData = ProgressionData.get(serverLevel);
        double multiplier = calculateXpMultiplier(progressionData);

        if (multiplier > 1.0) {
            int originalXp = event.getDroppedExperience();
            int newXp = (int) Math.ceil(originalXp * multiplier);
            event.setDroppedExperience(newXp);

            DimTrMod.LOGGER.debug("Applied {}x XP multiplier to {} (Original: {}, New: {})",
                    multiplier,
                    entity.getType().getDescriptionId(),
                    originalXp,
                    newXp
            );
        }
    }

    private static boolean isHostileMob(LivingEntity entity) {
        return entity instanceof Zombie ||
                // REMOVIDO: entity instanceof ZombieVillager ||
                entity instanceof Skeleton ||
                entity instanceof Stray ||
                entity instanceof Husk ||
                entity instanceof Spider ||
                entity instanceof Creeper ||
                entity instanceof Drowned ||
                entity instanceof EnderMan ||
                entity instanceof Witch ||
                entity instanceof Pillager ||
                entity instanceof Vindicator ||
                entity instanceof Evoker ||
                entity instanceof Ravager ||
                entity instanceof ElderGuardian ||
                entity instanceof Guardian ||
                entity instanceof Blaze ||
                entity instanceof WitherSkeleton ||
                entity instanceof PiglinBrute ||
                entity instanceof Hoglin ||
                entity instanceof Zoglin ||
                entity instanceof Ghast ||
                entity instanceof Endermite ||
                entity instanceof Piglin ||
                entity instanceof WitherBoss ||
                entity instanceof Warden ||
                entity instanceof EnderDragon;
    }

    private static double calculateXpMultiplier(ProgressionData progressionData) {
        // Usar os mesmos multiplicadores das fases como HP/Damage
        // Fase 2 completa tem prioridade máxima
        if (progressionData.phase2Completed) {
            return DimTrConfig.SERVER.phase2Multiplier.get();
        }
        // Fase 1 completa
        else if (progressionData.phase1Completed) {
            return DimTrConfig.SERVER.phase1Multiplier.get();
        }
        // Nenhuma fase completa
        return 1.0;
    }
}