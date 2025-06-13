package net.mirai.dimtr.event;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.data.ProgressionManager;
import net.minecraft.nbt.CompoundTag;
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

    // Tag NBT para marcar que o mob já teve multiplicador aplicado
    private static final String MULTIPLIER_APPLIED_TAG = "dimtr_multiplier_applied";
    private static final String MULTIPLIER_VALUE_TAG = "dimtr_multiplier_value";

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

        // Evitar aplicar multiplicador múltiplas vezes
        CompoundTag persistentData = livingEntity.getPersistentData();
        if (persistentData.getBoolean(MULTIPLIER_APPLIED_TAG)) {
            return;
        }

        // Calcular multiplicador baseado em players próximos
        double multiplier = calculateMultiplierForPosition(livingEntity, serverLevel);

        if (multiplier > 1.0) {
            applyMultiplier(livingEntity, multiplier);

            // Marcar como processado
            persistentData.putBoolean(MULTIPLIER_APPLIED_TAG, true);
            persistentData.putDouble(MULTIPLIER_VALUE_TAG, multiplier);

            DimTrMod.LOGGER.debug("Applied {}x multiplier to {} at position ({}, {}, {}) based on nearby players",
                    String.format("%.2f", multiplier),
                    livingEntity.getType().getDescriptionId(),
                    (int)livingEntity.getX(),
                    (int)livingEntity.getY(),
                    (int)livingEntity.getZ());
        }
    }

    /**
     * Calcula o multiplicador baseado na média dos players próximos
     */
    private static double calculateMultiplierForPosition(LivingEntity entity, ServerLevel level) {
        ProgressionManager manager = ProgressionManager.get(level);

        return manager.calculateAverageMultiplierNearPosition(
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                level
        );
    }

    private static boolean isHostileMob(LivingEntity entity) {
        return entity instanceof Zombie ||
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

    private static void applyMultiplier(LivingEntity entity, double multiplier) {
        try {
            // Aplicar multiplicador de vida
            if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
                double originalHealth = entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                double newHealth = originalHealth * multiplier;
                entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
                entity.setHealth((float) newHealth);
            }

            // Aplicar multiplicador de dano
            if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                double originalDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                double newDamage = originalDamage * multiplier;
                entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newDamage);
            }

        } catch (Exception e) {
            DimTrMod.LOGGER.warn("Failed to apply multiplier to {}: {}",
                    entity.getType().getDescriptionId(), e.getMessage());
        }
    }
}