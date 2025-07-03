package net.mirai.dimtr.event;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.data.ProgressionManager;
import net.mirai.dimtr.util.MobUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
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
        if (!MobUtils.isBasicHostileMob(entity)) {
            return;
        }

        // 🎯 MUDANÇA PRINCIPAL: Usar sistema de proximidade
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        double multiplier = progressionManager.calculateAverageMultiplierNearPosition(
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                serverLevel
        );

        if (multiplier > 1.0) {
            int originalXp = event.getDroppedExperience();
            int newXp = (int) Math.ceil(originalXp * multiplier);
            event.setDroppedExperience(newXp);
        }
    }
}