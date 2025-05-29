package net.mirai.dimtr.event; // Pacote atualizado

import net.mirai.dimtr.DimTrMod; // Pacote e classe principal atualizados
import net.mirai.dimtr.command.DimTrCommands; // Pacote de command corrigido e importado
import net.mirai.dimtr.data.ProgressionData; // Pacote atualizado
import net.mirai.dimtr.util.Constants; // Pacote atualizado
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.GAME) // MODID atualizado
public class ModEventHandlers {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DimTrCommands.register(event.getDispatcher()); // Classe de comando atualizada
        DimTrMod.LOGGER.info("DimTr Commands registered."); // Logger e mensagem atualizados
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel) {
            // ProgressionData será da classe no novo pacote net.mirai.dimtr.data
            ProgressionData progressionData = ProgressionData.get(serverLevel);
            LivingEntity deadEntity = event.getEntity();

            if (deadEntity.getType() == EntityType.ELDER_GUARDIAN) {
                if (progressionData.updateElderGuardianKilled(true)) {
                    DimTrMod.LOGGER.info("Elder Guardian killed. Progress updated."); // Logger atualizado
                }
            } else if (deadEntity.getType() == EntityType.RAVAGER) {
                if (progressionData.updateRavagerKilled(true)) {
                    DimTrMod.LOGGER.info("Ravager killed. Progress updated."); // Logger atualizado
                }
            } else if (deadEntity.getType() == EntityType.EVOKER) {
                if (progressionData.updateEvokerKilled(true)) {
                    DimTrMod.LOGGER.info("Evoker killed. Progress updated."); // Logger atualizado
                }
            } else if (deadEntity.getType() == EntityType.WITHER) {
                if (progressionData.updateWitherKilled(true)) {
                    DimTrMod.LOGGER.info("Wither killed. Progress updated."); // Logger atualizado
                }
            } else if (deadEntity.getType() == EntityType.WARDEN) {
                if (progressionData.updateWardenKilled(true)) {
                    DimTrMod.LOGGER.info("Warden killed. Progress updated."); // Logger atualizado
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerGetAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.level() instanceof ServerLevel serverLevel) {
                ProgressionData progressionData = ProgressionData.get(serverLevel);
                AdvancementHolder advancement = event.getAdvancement();

                if (advancement.id().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/hero_of_the_village"))) {
                    if (progressionData.updateRaidWon(true)) {
                        DimTrMod.LOGGER.info("Hero of the Village advancement ({}) earned by {}. Progress updated.", advancement.id(), player.getName().getString()); // Logger atualizado
                    }
                } else if (advancement.id().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/under_lock_and_key"))) {
                    if(progressionData.updateTrialVaultAdvancementEarned(true)) {
                        DimTrMod.LOGGER.info("Under Lock and Key (Trial Vault) advancement ({}) earned by {}. Progress updated.", advancement.id(), player.getName().getString()); // Logger atualizado
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player.level() instanceof ServerLevel serverLevel) {
            ProgressionData progressionData = ProgressionData.get(serverLevel);
            BlockPos clickedPos = event.getPos();
            BlockState clickedState = serverLevel.getBlockState(clickedPos);
            BlockPos firePos = clickedPos.relative(event.getFace());
            BlockState firePosState = serverLevel.getBlockState(firePos);

            if (event.getItemStack().getItem() instanceof FlintAndSteelItem &&
                    clickedState.is(Blocks.OBSIDIAN) &&
                    firePosState.canBeReplaced()
            ) {
                if (progressionData.isPhase1EffectivelyLocked()) {
                    event.setCanceled(true);
                    // Constants será da classe no novo pacote net.mirai.dimtr.util
                    player.sendSystemMessage(Component.translatable(Constants.MSG_NETHER_LOCKED));
                    serverLevel.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.7F, 1.0F);
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            firePos.getX() + 0.5, firePos.getY() + 0.5, firePos.getZ() + 0.5,
                            15, 0.1, 0.1, 0.1, 0.02);
                    DimTrMod.LOGGER.debug("Player {} tried to light Nether portal. Phase 1 locked.", player.getName().getString()); // Logger atualizado
                    return;
                }
            }

            if (event.getItemStack().getItem() == Items.ENDER_EYE &&
                    clickedState.is(Blocks.END_PORTAL_FRAME)) {
                if (progressionData.isPhase2EffectivelyLocked()) {
                    event.setCanceled(true);
                    player.sendSystemMessage(Component.translatable(Constants.MSG_END_LOCKED));
                    serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);
                    serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                            clickedPos.getX() + 0.5, clickedPos.getY() + 0.8, clickedPos.getZ() + 0.5,
                            30, 0.2, 0.2, 0.2, 0.05);
                    DimTrMod.LOGGER.debug("Player {} tried to activate End portal. Phase 2 locked.", player.getName().getString()); // Logger atualizado
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        // Nenhuma lógica de progressão aqui por enquanto
    }
}