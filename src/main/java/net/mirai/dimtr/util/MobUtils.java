package net.mirai.dimtr.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.warden.Warden;

/**
 * Utilidades centralizadas para classificação de mobs
 */
public class MobUtils {
    
    /**
     * Verifica se a entidade é considerada um mob hostil para aplicação de multiplicadores
     * Esta é a versão consolidada que inclui todos os mobs relevantes
     */
    public static boolean isHostileMob(LivingEntity entity) {
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
                entity instanceof Piglin ||
                entity instanceof Hoglin ||
                entity instanceof Zoglin ||
                entity instanceof Ghast ||
                entity instanceof WitherBoss ||
                entity instanceof Warden ||
                entity instanceof Bogged ||
                entity instanceof Breeze;
    }
    
    /**
     * Versão simplificada para XP multipliers (apenas mobs básicos)
     * Mantida para preservar comportamento específico do XP system
     */
    public static boolean isBasicHostileMob(LivingEntity entity) {
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
                entity instanceof Bogged ||
                entity instanceof Breeze;
    }
}
