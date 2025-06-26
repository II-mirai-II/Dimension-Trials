package net.mirai.dimtr.event;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.command.DimTrCommands;
import net.mirai.dimtr.command.PartyCommands;
import net.mirai.dimtr.data.PartyManager;
import net.mirai.dimtr.data.ProgressionManager;
import net.mirai.dimtr.data.ProgressionCoordinator;
import net.mirai.dimtr.util.Constants;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.advancements.AdvancementHolder;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gerenciador central de eventos do mod - VERSÃO COMPLETA E CORRIGIDA
 *
 * ✅ Sistema de progressão individual
 * ✅ Sistema de parties colaborativas
 * ✅ Integração completa entre sistemas
 * ✅ Bloqueio individual de portais
 * ✅ Detecção avançada de mobs
 * ✅ Correções de compilação
 */
@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEventHandlers {

    public static final String NBT_MOD_SUBTAG_KEY = DimTrMod.MODID;
    public static final String NBT_FLAG_RECEIVED_BOOK = "received_progression_book";

    // CORREÇÃO: Usar constantes ao invés de valores hardcoded
    private static final int PORTAL_SOUND_COOLDOWN_TICKS = Constants.DEFAULT_PORTAL_SOUND_COOLDOWN_TICKS;
    private static final Map<UUID, Long> netherPortalSoundCooldowns = new HashMap<>();
    private static final Map<UUID, Long> endPortalSoundCooldowns = new HashMap<>();

    // ============================================================================
    // 🎯 REGISTRO DE COMANDOS
    // ============================================================================

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        // 🎯 REGISTRO COMPLETO DE COMANDOS
        DimTrCommands.register(event.getDispatcher());
        PartyCommands.register(event.getDispatcher()); // ✅ CORRIGIDO

        DimTrMod.LOGGER.info("✅ Registered all DimTr commands:");
        DimTrMod.LOGGER.info("   • /dimtr (Administrative & individual commands - OP required)");
        DimTrMod.LOGGER.info("   • /party (Party management commands - No OP required)");
    }

    // ============================================================================
    // 🎯 SISTEMA DE MORTE DE ENTIDADES (VERSÃO UNIFICADA - CORRIGIDO)
    // ============================================================================

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        // Verificar se foi um jogador que matou
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity killedEntity)) {
            return;
        }

        if (!(killedEntity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        UUID playerId = player.getUUID();
        
        // CORREÇÃO: Usar sistema coordenado para evitar double-counting
        // 🎯 OBJETIVOS ESPECIAIS (Elder Guardian, Wither, Warden)
        if (killedEntity.getType() == EntityType.ELDER_GUARDIAN) {
            ProgressionCoordinator.processSpecialObjective(playerId, "elder_guardian", serverLevel);
        } else if (killedEntity.getType() == EntityType.WITHER) {
            ProgressionCoordinator.processSpecialObjective(playerId, "wither", serverLevel);
        } else if (killedEntity.getType() == EntityType.WARDEN) {
            ProgressionCoordinator.processSpecialObjective(playerId, "warden", serverLevel);
        }

        // 🎯 CONTADORES DE MOBS (SISTEMA COORDENADO)
        String mobType = getMobType(killedEntity);
        if (mobType != null) {
            ProgressionCoordinator.processMobKill(playerId, mobType, serverLevel);
        }

        // 🎯 NOVO: PROCESSAR MOBS CUSTOMIZADOS
        processCustomMobKill(playerId, killedEntity, serverLevel);
    }

    /**
     * 🎯 MÉTODO UNIFICADO: Detecção robusta de tipos de mobs
     * CORREÇÃO: Removido método duplicado getMobTypeFromEntity()
     */
    private static String getMobType(LivingEntity entity) {
        // CORREÇÃO 1: DROWNED DEVE VIR ANTES DE ZOMBIE
        if (entity instanceof Drowned) {
            return "drowned";
        }

        // Fase 1 - Mobs Comuns do Overworld
        if (entity instanceof Zombie && !(entity instanceof ZombieVillager) && !(entity instanceof Husk)) {
            return "zombie";
        } else if (entity instanceof ZombieVillager) {
            return "zombie_villager"; // Ainda retorna mas não é mais usado
        } else if (entity instanceof Skeleton && !(entity instanceof Stray) && !(entity instanceof WitherSkeleton)) {
            return "skeleton";
        } else if (entity instanceof Stray) {
            return "stray";
        } else if (entity instanceof Husk) {
            return "husk";
        } else if (entity instanceof Spider) {
            return "spider";
        } else if (entity instanceof Creeper) {
            return "creeper";
        } else if (entity instanceof EnderMan) {
            return "enderman";
        } else if (entity instanceof Witch) {
            return "witch";
        } else if (entity instanceof Pillager pillager) {
            // CORREÇÃO: MELHOR IDENTIFICAÇÃO DE CAPTAIN PILLAGER
            ItemStack mainHand = pillager.getMainHandItem();
            ItemStack offHand = pillager.getOffhandItem();

            boolean isCarryingBanner = mainHand.getItem() == Items.WHITE_BANNER ||
                    mainHand.getItem() == Items.BLACK_BANNER ||
                    mainHand.getItem() == Items.BLUE_BANNER ||
                    mainHand.getItem() == Items.BROWN_BANNER ||
                    mainHand.getItem() == Items.CYAN_BANNER ||
                    mainHand.getItem() == Items.GRAY_BANNER ||
                    mainHand.getItem() == Items.GREEN_BANNER ||
                    mainHand.getItem() == Items.LIGHT_BLUE_BANNER ||
                    mainHand.getItem() == Items.LIGHT_GRAY_BANNER ||
                    mainHand.getItem() == Items.LIME_BANNER ||
                    mainHand.getItem() == Items.MAGENTA_BANNER ||
                    mainHand.getItem() == Items.ORANGE_BANNER ||
                    mainHand.getItem() == Items.PINK_BANNER ||
                    mainHand.getItem() == Items.PURPLE_BANNER ||
                    mainHand.getItem() == Items.RED_BANNER ||
                    mainHand.getItem() == Items.YELLOW_BANNER ||
                    offHand.getItem() == Items.WHITE_BANNER ||
                    offHand.getItem() == Items.BLACK_BANNER ||
                    offHand.getItem() == Items.BLUE_BANNER ||
                    offHand.getItem() == Items.BROWN_BANNER ||
                    offHand.getItem() == Items.CYAN_BANNER ||
                    offHand.getItem() == Items.GRAY_BANNER ||
                    offHand.getItem() == Items.GREEN_BANNER ||
                    offHand.getItem() == Items.LIGHT_BLUE_BANNER ||
                    offHand.getItem() == Items.LIGHT_GRAY_BANNER ||
                    offHand.getItem() == Items.LIME_BANNER ||
                    offHand.getItem() == Items.MAGENTA_BANNER ||
                    offHand.getItem() == Items.ORANGE_BANNER ||
                    offHand.getItem() == Items.PINK_BANNER ||
                    offHand.getItem() == Items.PURPLE_BANNER ||
                    offHand.getItem() == Items.RED_BANNER ||
                    offHand.getItem() == Items.YELLOW_BANNER;

            boolean hasBadOmen = pillager.hasEffect(net.minecraft.world.effect.MobEffects.BAD_OMEN);

            if (isCarryingBanner || hasBadOmen) {
                return "captain";
            }
            return "pillager";
        } else if (entity instanceof Vindicator) {
            return "vindicator";
        } else if (entity instanceof Evoker) {
            return "evoker";
        } else if (entity instanceof Ravager) {
            return "ravager";
        }

        // Fase 2 - Mobs do Nether/End
        else if (entity instanceof Blaze) {
            return "blaze";
        } else if (entity instanceof WitherSkeleton) {
            return "wither_skeleton";
        } else if (entity instanceof PiglinBrute) {
            return "piglin_brute";
        } else if (entity instanceof Hoglin) {
            return "hoglin";
        } else if (entity instanceof Zoglin) {
            return "zoglin";
        } else if (entity instanceof Ghast) {
            return "ghast";
        }
        // ✅ ENDERMITE REMOVIDO COMPLETAMENTE
        else if (entity instanceof Piglin piglin) {
            if (piglin.getTarget() != null) {
                return "piglin";
            }
            if (piglin.isAdult() && !piglin.isHolding(Items.GOLD_INGOT)) {
                return "piglin";
            }
        }

        // ADICIONADO: Mobs mais novos
        String entityName = entity.getType().toString().toLowerCase();
        if (entityName.contains("bogged")) {
            return "bogged";
        } else if (entityName.contains("breeze")) {
            return "breeze";
        }

        return null;
    }

    // ============================================================================
    // 🎯 SISTEMA DE ADVANCEMENTS (INTEGRAÇÃO COMPLETA)
    // ============================================================================

    @SubscribeEvent
    public static void onAdvancementEvent(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 🔧 CORRIGIDO: AdvancementHolder.id() em vez de getId()
            String advancementId = event.getAdvancement().id().toString();
            ServerLevel serverLevel = (ServerLevel) player.level();

            // Mapear advancement para objetivo especial
            String objectiveType = mapAdvancementToObjective(advancementId);
            if (objectiveType != null) {
                // 🎯 PRIMEIRO: Processar para party (se aplicável)
                PartyManager partyManager = PartyManager.get(serverLevel);
                boolean processedByParty = partyManager.processPartySpecialObjective(player.getUUID(), objectiveType);

                // 🎯 SEGUNDO: Se não foi processado por party, processar individualmente
                if (!processedByParty) {
                    ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
                    processIndividualObjective(progressionManager, player.getUUID(), objectiveType);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerGetAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // 🎯 MUDANÇA PRINCIPAL: Progressão individual por jogador
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        PartyManager partyManager = PartyManager.get(serverLevel);
        UUID playerId = event.getEntity().getUUID();
        AdvancementHolder advancement = event.getAdvancement();
        ResourceLocation advancementId = advancement.id();

        if (advancementId.equals(ResourceLocation.withDefaultNamespace("adventure/hero_of_the_village"))) {
            // 🎯 PRIMEIRO: Processar para party (se aplicável)
            boolean processedByParty = partyManager.processPartySpecialObjective(playerId, "raid");

            // 🎯 SEGUNDO: Se não foi processado por party, processar individualmente
            if (!processedByParty) {
                progressionManager.updateRaidWon(playerId);
            }
        } else if (advancementId.equals(ResourceLocation.withDefaultNamespace("adventure/under_lock_and_key"))) {
            // 🎯 PRIMEIRO: Processar para party (se aplicável)
            boolean processedByParty = partyManager.processPartySpecialObjective(playerId, "trial_vault");

            // 🎯 SEGUNDO: Se não foi processado por party, processar individualmente
            if (!processedByParty) {
                progressionManager.updateTrialVaultAdvancementEarned(playerId);
            }
        } else if (advancementId.equals(ResourceLocation.withDefaultNamespace("adventure/voluntary_exile"))) {
            // 🎯 PRIMEIRO: Processar para party (se aplicável)
            boolean processedByParty = partyManager.processPartySpecialObjective(playerId, "voluntary_exile");

            // 🎯 SEGUNDO: Se não foi processado por party, processar individualmente
            if (!processedByParty) {
                progressionManager.updateVoluntaireExileAdvancementEarned(playerId);
            }
        }
    }

    /**
     * Mapear ID do advancement para tipo de objetivo
     */
    private static String mapAdvancementToObjective(String advancementId) {
        return switch (advancementId) {
            case "minecraft:adventure/kill_a_mob" -> null; // Muito genérico
            case "minecraft:adventure/voluntary_exile" -> "voluntary_exile";
            case "minecraft:adventure/hero_of_the_village" -> "raid";
            case "minecraft:adventure/under_lock_and_key" -> "trial_vault";
            // TODO: Adicionar outros mappings conforme necessário
            default -> null;
        };
    }

    /**
     * Processar objetivo individual
     */
    private static void processIndividualObjective(ProgressionManager manager, UUID playerId, String objectiveType) {
        switch (objectiveType.toLowerCase()) {
            case "elder_guardian" -> manager.updateElderGuardianKilled(playerId);
            case "raid" -> manager.updateRaidWon(playerId);
            case "trial_vault" -> manager.updateTrialVaultAdvancementEarned(playerId);
            case "voluntary_exile" -> manager.updateVoluntaireExileAdvancementEarned(playerId);
            case "wither" -> manager.updateWitherKilled(playerId);
            case "warden" -> manager.updateWardenKilled(playerId);
        }
    }

    // ============================================================================
    // 🎯 SISTEMA DE LOGIN (ATUALIZADO)
    // ============================================================================

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // 🎯 MUDANÇA PRINCIPAL: Enviar dados individuais do jogador
        ServerLevel serverLevel = player.serverLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);

        // Enviar dados específicos do jogador
        progressionManager.sendToClient(player);
    }

    // ============================================================================
    // 🎯 SISTEMA DE BLOQUEIO INDIVIDUAL DE PORTAIS
    // ============================================================================

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) player.level();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        UUID playerId = player.getUUID();

        // 🎯 VERIFICAR ACESSO PADRÃO (Nether/End)
        if (event.getDimension() == Level.NETHER) {
            if (!progressionManager.canPlayerAccessNether(playerId)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable("message.dimtr.nether_locked")
                        .withStyle(ChatFormatting.RED));
                playDenialEffects(serverLevel, player, "nether");
                return;
            }
        }

        if (event.getDimension() == Level.END) {
            if (!progressionManager.canPlayerAccessEnd(playerId)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable("message.dimtr.end_locked")
                        .withStyle(ChatFormatting.RED));
                playDenialEffects(serverLevel, player, "end");
                return;
            }
        }

        // 🎯 NOVO: VERIFICAR ACESSO A DIMENSÕES CUSTOMIZADAS
        String dimensionString = event.getDimension().location().toString();
        if (!net.mirai.dimtr.config.CustomRequirements.canAccessCustomDimension(playerId, event.getDimension().location())) {
            // Encontrar qual fase customizada bloqueia esta dimensão
            String blockingPhase = findBlockingCustomPhase(playerId, dimensionString, serverLevel);
            if (blockingPhase != null) {
                event.setCanceled(true);
                var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(blockingPhase);
                String phaseName = customPhase != null ? customPhase.name : blockingPhase;
                
                player.sendSystemMessage(Component.literal("🔒 " + phaseName + " Required")
                        .withStyle(ChatFormatting.RED));
                player.sendSystemMessage(Component.literal("Complete " + phaseName + " to access this dimension")
                        .withStyle(ChatFormatting.GRAY));
                        
                playDenialEffects(serverLevel, player, "custom");
                return;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // 🎯 MUDANÇA: Verificação individual
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        UUID playerId = player.getUUID();
        BlockPos clickedPos = event.getPos();
        BlockState clickedState = serverLevel.getBlockState(clickedPos);
        ItemStack heldItem = event.getItemStack();

        // ====== BLOQUEAR ATIVAÇÃO DE PORTAL DO NETHER ======
        if ((heldItem.getItem() instanceof FlintAndSteelItem || heldItem.getItem() == Items.FIRE_CHARGE) &&
                clickedState.is(Blocks.OBSIDIAN)) {

            if (!progressionManager.canPlayerAccessNether(playerId)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable("message.dimtr.nether_locked")
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, "nether");

                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.8, clickedPos.getZ() + 0.5,
                        30, 0.2, 0.2, 0.2, 0.05);
                return;
            }
        }

        // ====== BLOQUEAR COLOCAÇÃO DE ENDER EYE ======
        if (heldItem.getItem() == Items.ENDER_EYE && clickedState.is(Blocks.END_PORTAL_FRAME)) {
            if (!progressionManager.canPlayerAccessEnd(playerId)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable("message.dimtr.end_locked")
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, "end");

                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.8, clickedPos.getZ() + 0.5,
                        30, 0.2, 0.2, 0.2, 0.05);
                return;
            }
        }

        // ====== BLOQUEAR ACENDIMENTO DIRETO DO PORTAL DO END ======
        if ((heldItem.getItem() instanceof FlintAndSteelItem || heldItem.getItem() == Items.FIRE_CHARGE) &&
                (clickedState.is(Blocks.END_PORTAL_FRAME) || clickedState.is(Blocks.END_PORTAL))) {

            if (!progressionManager.canPlayerAccessEnd(playerId)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable("message.dimtr.end_locked")
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, "end");

                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.8, clickedPos.getZ() + 0.5,
                        20, 0.3, 0.3, 0.3, 0.05);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel serverLevel = player.serverLevel();
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        UUID playerId = player.getUUID();

        // 🎯 MUDANÇA: Verificação individual
        if (progressionManager.canPlayerAccessEnd(playerId)) {
            return; // End liberado para este jogador
        }

        BlockPos playerPos = player.blockPosition();

        BlockPos[] positionsToCheck = {
                playerPos, playerPos.below(), playerPos.above(),
                playerPos.north(), playerPos.south(), playerPos.east(), playerPos.west()
        };

        boolean isInEndPortal = false;
        BlockPos portalPos = null;

        for (BlockPos pos : positionsToCheck) {
            BlockState blockState = serverLevel.getBlockState(pos);
            if (blockState.is(Blocks.END_PORTAL)) {
                isInEndPortal = true;
                portalPos = pos;
                break;
            }
        }

        if (isInEndPortal && portalPos != null) {
            // CORREÇÃO: Não teleportar mais - apenas impedir permanência no portal
            // Empurrar jogador suavemente para fora
            double pushX = (player.getX() - (portalPos.getX() + 0.5)) * 0.3;
            double pushZ = (player.getZ() - (portalPos.getZ() + 0.5)) * 0.3;
            
            // Aplicar pequeno impulso para fora
            player.push(pushX, 0.1, pushZ);
            
            player.sendSystemMessage(Component.translatable("message.dimtr.end_locked")
                    .withStyle(ChatFormatting.RED));

            playDenialEffects(serverLevel, player, "end");
        }
    }

    // ============================================================================
    // 🎯 MÉTODOS AUXILIARES
    // ============================================================================

    /**
     * 🎯 NOVO: Encontra qual fase customizada está bloqueando o acesso a uma dimensão
     */
    private static String findBlockingCustomPhase(UUID playerId, String dimensionString, ServerLevel serverLevel) {
        var customPhases = net.mirai.dimtr.config.CustomRequirements.getAllCustomPhases();
        if (customPhases.isEmpty()) {
            return null;
        }

        var progressionManager = ProgressionManager.get(serverLevel);
        var playerData = progressionManager.getPlayerData(playerId);

        for (var entry : customPhases.entrySet()) {
            String phaseId = entry.getKey();
            var phase = entry.getValue();
            
            // Check if this phase blocks the dimension
            if (phase.dimensionAccess != null && phase.dimensionAccess.contains(dimensionString)) {
                // Check if phase is completed
                if (!playerData.isCustomPhaseComplete(phaseId)) {
                    return phaseId;
                }
            }
        }

        return null;
    }

    private static void playDenialEffects(ServerLevel serverLevel, Player player, String portalType) {
        long currentTime = serverLevel.getGameTime();
        UUID playerId = player.getUUID();

        if ("nether".equals(portalType)) {
            long lastPlayed = netherPortalSoundCooldowns.getOrDefault(playerId, 0L);
            if (currentTime > lastPlayed + PORTAL_SOUND_COOLDOWN_TICKS) {
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRIGGER,
                        SoundSource.PLAYERS, 0.7F, 0.5F);
                netherPortalSoundCooldowns.put(playerId, currentTime);
            }

            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 1, player.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05);

        } else if ("end".equals(portalType)) {
            long lastPlayed = endPortalSoundCooldowns.getOrDefault(playerId, 0L);
            if (currentTime > lastPlayed + PORTAL_SOUND_COOLDOWN_TICKS) {
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                        SoundSource.PLAYERS, 1.0F, 0.5F);
                endPortalSoundCooldowns.put(playerId, currentTime);
            }

            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.1);
        } else if ("custom".equals(portalType)) {
            // Custom portal denial effects
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE,
                    SoundSource.PLAYERS, 0.8F, 0.8F);

            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                    player.getX(), player.getY() + 1, player.getZ(),
                    15, 0.4, 0.4, 0.4, 0.1);
        }
    }

    /**
     * 🎯 NOVO: Processar mobs customizados
     */
    private static void processCustomMobKill(UUID playerId, LivingEntity killedEntity, ServerLevel serverLevel) {
        String entityType = killedEntity.getType().toString();
        String entityId = entityType.toLowerCase();
        
        // Check if any custom phases require this mob
        var customPhases = net.mirai.dimtr.config.CustomRequirements.getAllCustomPhases();
        if (customPhases.isEmpty()) {
            return;
        }
        
        var progressionManager = ProgressionManager.get(serverLevel);
        var playerData = progressionManager.getPlayerData(playerId);
        
        for (var entry : customPhases.entrySet()) {
            String phaseId = entry.getKey();
            var phase = entry.getValue();
            
            // Skip if phase is already completed
            if (playerData.isCustomPhaseComplete(phaseId)) {
                continue;
            }
            
            // Check if this phase requires this mob type
            if (phase.mobRequirements != null && phase.mobRequirements.containsKey(entityId)) {
                int required = phase.mobRequirements.get(entityId);
                int current = playerData.getCustomMobKills(phaseId, entityId);
                
                if (current < required) {
                    playerData.incrementCustomMobKill(phaseId, entityId);
                    
                    // Check if phase is now complete
                    checkCustomPhaseCompletion(playerId, phaseId, serverLevel);
                }
            }
        }
    }
    
    /**
     * 🎯 NOVO: Verificar se uma fase customizada foi completada
     */
    private static void checkCustomPhaseCompletion(UUID playerId, String phaseId, ServerLevel serverLevel) {
        var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(phaseId);
        if (customPhase == null) {
            return;
        }
        
        var progressionManager = ProgressionManager.get(serverLevel);
        var playerData = progressionManager.getPlayerData(playerId);
        
        // Check if all mob requirements are met
        boolean allMobsComplete = true;
        if (customPhase.mobRequirements != null) {
            for (var entry : customPhase.mobRequirements.entrySet()) {
                String mobType = entry.getKey();
                int required = entry.getValue();
                int current = playerData.getCustomMobKills(phaseId, mobType);
                
                if (current < required) {
                    allMobsComplete = false;
                    break;
                }
            }
        }
        
        // Check if all special objectives are met
        boolean allObjectivesComplete = true;
        if (customPhase.specialObjectives != null) {
            for (var entry : customPhase.specialObjectives.entrySet()) {
                String objectiveId = entry.getKey();
                var objective = entry.getValue();
                
                if (objective.required && !playerData.isCustomObjectiveComplete(phaseId, objectiveId)) {
                    allObjectivesComplete = false;
                    break;
                }
            }
        }
        
        // Complete the phase if all requirements are met
        if (allMobsComplete && allObjectivesComplete) {
            playerData.setCustomPhaseComplete(phaseId, true);
            
            // Notify player
            var player = serverLevel.getPlayerByUUID(playerId);
            if (player != null) {
                player.sendSystemMessage(Component.literal("🎉 " + customPhase.name + " Complete!")
                        .withStyle(ChatFormatting.GOLD));
                player.sendSystemMessage(Component.literal("You have unlocked new content!")
                        .withStyle(ChatFormatting.YELLOW));
            }
            
            progressionManager.setDirty(); // Mark for saving
        }
    }
}