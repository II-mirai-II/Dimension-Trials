package net.mirai.dimtr.event;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.data.ProgressionData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class MobMultiplierHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // Só aplicar no servidor
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Verificar se os multiplicadores estão habilitados
        if (!DimTrConfig.SERVER.enableMultipliers.get()) {
            return;
        }

        // Só aplicar em mobs hostis específicos
        if (!(event.getEntity() instanceof LivingEntity livingEntity) || !isHostileMob(livingEntity)) {
            return;
        }

        ProgressionData progressionData = ProgressionData.get(serverLevel);
        double multiplier = calculateMultiplier(progressionData);

        if (multiplier > 1.0) {
            applyMultiplier(livingEntity, multiplier);
        }
    }

    private static boolean isHostileMob(LivingEntity entity) {
        return entity instanceof Zombie ||
                entity instanceof ZombieVillager ||
                entity instanceof Skeleton ||
                entity instanceof Stray ||
                entity instanceof Husk ||
                entity instanceof Spider ||
                entity instanceof Creeper ||
                entity instanceof Drowned ||
                entity instanceof EnderMan ||  // Correto: EnderMan (não Enderman)
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
                entity instanceof WitherBoss ||  // Correto: WitherBoss
                entity instanceof Warden ||
                entity instanceof EnderDragon;  // Correto: EnderDragon
    }

    private static double calculateMultiplier(ProgressionData progressionData) {
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

    private static void applyMultiplier(LivingEntity entity, double multiplier) {
        try {
            // Aplicar multiplicador de vida
            if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
                double originalHealth = entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                double newHealth = originalHealth * multiplier;
                entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
                entity.setHealth((float) newHealth); // Setar vida atual para o máximo
            }

            // Aplicar multiplicador de dano de ataque
            if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                double originalDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                double newDamage = originalDamage * multiplier;
                entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newDamage);
            }

            DimTrMod.LOGGER.debug("Applied {}x multiplier to {} (HP: {}, DMG: {})",
                    multiplier,
                    entity.getType().getDescriptionId(),
                    entity.getAttribute(Attributes.MAX_HEALTH) != null ?
                            entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() : "N/A",
                    entity.getAttribute(Attributes.ATTACK_DAMAGE) != null ?
                            entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() : "N/A"
            );

        } catch (Exception e) {
            DimTrMod.LOGGER.warn("Failed to apply multiplier to {}: {}",
                    entity.getType().getDescriptionId(), e.getMessage());
        }
    }
}