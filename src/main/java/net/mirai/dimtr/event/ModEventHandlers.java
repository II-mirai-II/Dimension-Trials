package net.mirai.dimtr.event;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.command.DimTrCommands;
import net.mirai.dimtr.command.PartyCommands;
import net.mirai.dimtr.data.PartyManager;
import net.mirai.dimtr.data.ProgressionManager;
import net.mirai.dimtr.data.ProgressionCoordinator;
import net.mirai.dimtr.system.CustomPhaseSystem;
import net.mirai.dimtr.system.BossKillValidator;
import net.mirai.dimtr.system.ProgressTransferService;
import net.mirai.dimtr.integration.ExternalModIntegration;
import net.mirai.dimtr.util.Constants;
import net.mirai.dimtr.util.BlockPosPool;
import net.mirai.dimtr.util.I18nHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.minecraft.advancements.AdvancementHolder;

import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    
    // 🎯 PERFORMANCE: Cache estático de banners para evitar comparações múltiplas
    private static final Set<Item> BANNER_ITEMS = Set.of(
        Items.WHITE_BANNER, Items.BLACK_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER,
        Items.CYAN_BANNER, Items.GRAY_BANNER, Items.GREEN_BANNER, Items.LIGHT_BLUE_BANNER,
        Items.LIGHT_GRAY_BANNER, Items.LIME_BANNER, Items.MAGENTA_BANNER, Items.ORANGE_BANNER,
        Items.PINK_BANNER, Items.PURPLE_BANNER, Items.RED_BANNER, Items.YELLOW_BANNER
    );
    
    // 🎯 PERFORMANCE: Cache para tipos de entidades especiais
    private static final Set<EntityType<?>> SPECIAL_OBJECTIVE_TYPES = Set.of(
        EntityType.ELDER_GUARDIAN,
        EntityType.WITHER,
        EntityType.WARDEN
    );
    
    // 🎯 PERFORMANCE: Cache para mapeamento de dimensões customizadas
    private static final Map<String, Set<String>> DIMENSION_TO_PHASES_CACHE = new ConcurrentHashMap<>();
    private static volatile long lastCacheRefresh = 0;
    private static final long CACHE_REFRESH_INTERVAL = 30000; // 30 segundos

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

        DimTrMod.LOGGER.info(Constants.LOG_COMMANDS_REGISTERED);
        DimTrMod.LOGGER.info(Constants.LOG_COMMANDS_DIMTR);
        DimTrMod.LOGGER.info(Constants.LOG_COMMANDS_PARTY);
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
        
        // 🔒 NOVA VALIDAÇÃO: Verificar legitimidade de boss kills
        if (isBossEntity(killedEntity)) {
            String bossId = getBossId(killedEntity);
            if (!BossKillValidator.validateKill(playerId, bossId, event.getSource())) {
                DimTrMod.LOGGER.warn("❌ Boss kill inválido rejeitado: {} -> {}", player.getName().getString(), bossId);
                return; // Interromper processamento se kill for inválido
            }
            DimTrMod.LOGGER.info("✅ Boss kill validado: {} -> {}", player.getName().getString(), bossId);
        }
        
        // 🎯 NOVO: Processar custom phases para o kill
        CustomPhaseSystem.processMobKill(player, killedEntity, event.getSource());
        
        // 🎯 PERFORMANCE: Usar cache para verificação rápida de objetivos especiais
        EntityType<?> entityType = killedEntity.getType();
        if (SPECIAL_OBJECTIVE_TYPES.contains(entityType)) {
            if (entityType == EntityType.ELDER_GUARDIAN) {
                ProgressionCoordinator.processSpecialObjective(playerId, Constants.OBJECTIVE_TYPE_ELDER_GUARDIAN, serverLevel);
            } else if (entityType == EntityType.WITHER) {
                ProgressionCoordinator.processSpecialObjective(playerId, Constants.OBJECTIVE_TYPE_WITHER, serverLevel);
            } else if (entityType == EntityType.WARDEN) {
                ProgressionCoordinator.processSpecialObjective(playerId, Constants.OBJECTIVE_TYPE_WARDEN, serverLevel);
            }
        }

        // 🎯 CONTADORES DE MOBS (SISTEMA COORDENADO)
        String mobType = getMobType(killedEntity);
        if (mobType != null) {
            ProgressionCoordinator.processMobKill(playerId, mobType, serverLevel);
        }

        // 🎯 NOVO: PROCESSAR MOBS CUSTOMIZADOS
        processCustomMobKill(playerId, killedEntity, serverLevel);
        
        // 🎯 NOVO: INTEGRAÇÃO AUTOMÁTICA COM MODS EXTERNOS
        processExternalModBossKill(playerId, killedEntity, serverLevel);
    }

    /**
     * 🎯 MÉTODO UNIFICADO: Detecção robusta de tipos de mobs
     * CORREÇÃO: Removido método duplicado getMobTypeFromEntity()
     */
    private static String getMobType(LivingEntity entity) {
        // CORREÇÃO 1: DROWNED DEVE VIR ANTES DE ZOMBIE
        if (entity instanceof Drowned) {
            return Constants.MOB_TYPE_DROWNED;
        }

        // Fase 1 - Mobs Comuns do Overworld
        if (entity instanceof Zombie && !(entity instanceof ZombieVillager) && !(entity instanceof Husk)) {
            return Constants.MOB_TYPE_ZOMBIE;
        } else        if (entity instanceof ZombieVillager) {
            // ✅ REMOVIDO: Zombie villager não é mais contado como progresso
            return null;
        }else if (entity instanceof Skeleton && !(entity instanceof Stray) && !(entity instanceof WitherSkeleton)) {
            return Constants.MOB_TYPE_SKELETON;
        } else if (entity instanceof Stray) {
            return Constants.MOB_TYPE_STRAY;
        } else if (entity instanceof Husk) {
            return Constants.MOB_TYPE_HUSK;
        } else if (entity instanceof Spider) {
            return Constants.MOB_TYPE_SPIDER;
        } else if (entity instanceof Creeper) {
            return Constants.MOB_TYPE_CREEPER;
        } else if (entity instanceof EnderMan) {
            return Constants.MOB_TYPE_ENDERMAN;
        } else if (entity instanceof Witch) {
            return Constants.MOB_TYPE_WITCH;
        } else if (entity instanceof Pillager pillager) {
            // 🎯 PERFORMANCE: Otimizado usando Set para verificação de banners
            ItemStack mainHand = pillager.getMainHandItem();
            ItemStack offHand = pillager.getOffhandItem();

            boolean isCarryingBanner = BANNER_ITEMS.contains(mainHand.getItem()) || 
                                     BANNER_ITEMS.contains(offHand.getItem());

            boolean hasBadOmen = pillager.hasEffect(net.minecraft.world.effect.MobEffects.BAD_OMEN);

            if (isCarryingBanner || hasBadOmen) {
                return Constants.MOB_TYPE_CAPTAIN;
            }
            return Constants.MOB_TYPE_PILLAGER;
        } else if (entity instanceof Vindicator) {
            return Constants.MOB_TYPE_VINDICATOR;
        } else if (entity instanceof Evoker) {
            return Constants.MOB_TYPE_EVOKER;
        } else if (entity instanceof Ravager) {
            return Constants.MOB_TYPE_RAVAGER;
        }

        // Fase 2 - Mobs do Nether/End
        else if (entity instanceof Blaze) {
            return Constants.MOB_TYPE_BLAZE;
        } else if (entity instanceof WitherSkeleton) {
            return Constants.MOB_TYPE_WITHER_SKELETON;
        } else if (entity instanceof PiglinBrute) {
            return Constants.MOB_TYPE_PIGLIN_BRUTE;
        } else if (entity instanceof Hoglin) {
            return Constants.MOB_TYPE_HOGLIN;
        } else if (entity instanceof Zoglin) {
            return Constants.MOB_TYPE_ZOGLIN;
        } else if (entity instanceof Ghast) {
            return Constants.MOB_TYPE_GHAST;
        }
        // ✅ ENDERMITE REMOVIDO COMPLETAMENTE
        else if (entity instanceof Piglin piglin) {
            if (piglin.getTarget() != null) {
                return Constants.MOB_TYPE_PIGLIN;
            }
            if (piglin.isAdult() && !piglin.isHolding(Items.GOLD_INGOT)) {
                return Constants.MOB_TYPE_PIGLIN;
            }
        }

        // ADICIONADO: Mobs mais novos
        String entityName = entity.getType().toString().toLowerCase();
        if (entityName.contains(Constants.MOB_TYPE_BOGGED)) {
            return Constants.MOB_TYPE_BOGGED;
        } else if (entityName.contains(Constants.MOB_TYPE_BREEZE)) {
            return Constants.MOB_TYPE_BREEZE;
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

            // 🎯 NOVO: Processar custom phases com advancement
            CustomPhaseSystem.processAdvancementEarned(player, event.getAdvancement());

            // Mapear advancement para objetivo especial
            String objectiveType = mapAdvancementToObjective(advancementId);
            if (objectiveType != null) {
                // 🎯 PRIMEIRO: Processar para party (se aplicável)
                PartyManager partyManager = PartyManager.get(serverLevel);
                boolean processedByParty = partyManager.processPartySpecialObjective(player.getUUID(), objectiveType);

                // 🎯 SEGUNDO: Se não foi processado por party, processar individualmente
                if (!processedByParty) {
                    ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
                    processIndividualObjective(progressionManager, player.getUUID(), objectiveType, serverLevel);
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
            boolean processedByParty = partyManager.processPartySpecialObjective(playerId, Constants.OBJECTIVE_TYPE_RAID);

            // 🎯 SEGUNDO: Se não foi processado por party, processar individualmente
            if (!processedByParty) {
                progressionManager.updateRaidWon(playerId);
            }
        } else if (advancementId.equals(ResourceLocation.withDefaultNamespace("adventure/under_lock_and_key"))) {
            // 🎯 PRIMEIRO: Processar para party (se aplicável)
            boolean processedByParty = partyManager.processPartySpecialObjective(playerId, Constants.OBJECTIVE_TYPE_TRIAL_VAULT);

            // 🎯 SEGUNDO: Se não foi processado por party, processar individualmente
            if (!processedByParty) {
                progressionManager.updateTrialVaultAdvancementEarned(playerId);
            }
        } else if (advancementId.equals(ResourceLocation.withDefaultNamespace("adventure/voluntary_exile"))) {
            // 🎯 PRIMEIRO: Processar para party (se aplicável)
            boolean processedByParty = partyManager.processPartySpecialObjective(playerId, Constants.OBJECTIVE_TYPE_VOLUNTARY_EXILE);

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
            case Constants.ADVANCEMENT_KILL_A_MOB -> null; // Muito genérico
            case Constants.ADVANCEMENT_VOLUNTARY_EXILE -> Constants.OBJECTIVE_TYPE_VOLUNTARY_EXILE;
            case Constants.ADVANCEMENT_HERO_OF_VILLAGE -> Constants.OBJECTIVE_TYPE_RAID;
            case Constants.ADVANCEMENT_UNDER_LOCK_AND_KEY -> Constants.OBJECTIVE_TYPE_TRIAL_VAULT;
            // 🎯 NOVO: Mapear advancements para custom phases
            default -> mapCustomAdvancementToObjective(advancementId);
        };
    }
    
    /**
     * 🎯 NOVO: Mapear advancements customizados para objetivos de custom phases
     */
    private static String mapCustomAdvancementToObjective(String advancementId) {
        // Verificar se alguma fase customizada tem objetivos que correspondem a este advancement
        var customPhases = net.mirai.dimtr.config.CustomRequirements.getAllCustomPhases();
        
        for (var phaseEntry : customPhases.entrySet()) {
            String phaseId = phaseEntry.getKey();
            var phase = phaseEntry.getValue();
            
            if (phase.specialObjectives != null) {
                for (var objEntry : phase.specialObjectives.entrySet()) {
                    String objectiveId = objEntry.getKey();
                    // Verificar se o objectiveId corresponde ao advancementId
                    // (isso pode ser configurado de forma mais sofisticada futuramente)
                    if (advancementId.contains(objectiveId) || objectiveId.contains(advancementId)) {
                        return phaseId + ":" + objectiveId; // Formato especial para custom objectives
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * Processar objetivo individual
     */
    private static void processIndividualObjective(ProgressionManager manager, UUID playerId, String objectiveType, ServerLevel serverLevel) {
        // 🎯 NOVO: Verificar se é um objetivo customizado
        if (objectiveType != null && objectiveType.contains(":")) {
            // Formato especial para custom objectives: "phaseId:objectiveId"
            String[] parts = objectiveType.split(":", 2);
            if (parts.length == 2) {
                String phaseId = parts[0];
                String objectiveId = parts[1];
                ProgressionCoordinator.processCustomObjective(playerId, phaseId, objectiveId, serverLevel);
                return;
            }
        }
        
        // Objetivos padrão
        if (objectiveType != null) {
            switch (objectiveType.toLowerCase()) {
                case Constants.OBJECTIVE_TYPE_ELDER_GUARDIAN -> manager.updateElderGuardianKilled(playerId);
                case Constants.OBJECTIVE_TYPE_RAID -> manager.updateRaidWon(playerId);
                case Constants.OBJECTIVE_TYPE_TRIAL_VAULT -> manager.updateTrialVaultAdvancementEarned(playerId);
                case Constants.OBJECTIVE_TYPE_VOLUNTARY_EXILE -> manager.updateVoluntaireExileAdvancementEarned(playerId);
                case Constants.OBJECTIVE_TYPE_WITHER -> manager.updateWitherKilled(playerId);
                case Constants.OBJECTIVE_TYPE_WARDEN -> manager.updateWardenKilled(playerId);
            }
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
        UUID playerId = player.getUUID();

        // 🎯 CORREÇÃO: Usar ProgressionCoordinator para verificação unificada
        if (event.getDimension() == Level.NETHER) {
            if (!ProgressionCoordinator.canPlayerAccessDimension(playerId, Constants.DIMENSION_TYPE_NETHER, serverLevel)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_NETHER_LOCKED)
                        .withStyle(ChatFormatting.RED));
                playDenialEffects(serverLevel, player, Constants.DIMENSION_TYPE_NETHER);
                return;
            }
        }

        if (event.getDimension() == Level.END) {
            if (!ProgressionCoordinator.canPlayerAccessDimension(playerId, Constants.DIMENSION_TYPE_END, serverLevel)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_END_LOCKED)
                        .withStyle(ChatFormatting.RED));
                playDenialEffects(serverLevel, player, Constants.DIMENSION_TYPE_END);
                return;
            }
        }

        // 🎯 NOVO: VERIFICAR ACESSO A DIMENSÕES CUSTOMIZADAS
        String dimensionString = event.getDimension().location().toString();
        if (!ProgressionCoordinator.canPlayerAccessCustomDimension(playerId, dimensionString, serverLevel)) {
            // Encontrar qual fase customizada bloqueia esta dimensão
            String blockingPhase = net.mirai.dimtr.config.CustomRequirements.findBlockingPhaseForDimension(dimensionString);
            if (blockingPhase != null) {
                event.setCanceled(true);
                var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(blockingPhase);
                String phaseName = customPhase != null ? customPhase.name : blockingPhase;
                
                if (player instanceof ServerPlayer serverPlayer) {
                    I18nHelper.sendMessage(serverPlayer, Constants.EVENT_DIMENSION_REQUIRED, phaseName);
                    I18nHelper.sendMessage(serverPlayer, Constants.EVENT_DIMENSION_COMPLETE, phaseName);
                }
                        
                playDenialEffects(serverLevel, player, Constants.DIMENSION_TYPE_CUSTOM);
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

        // 🎯 CORREÇÃO: Verificação unificada via ProgressionCoordinator
        UUID playerId = player.getUUID();
        BlockPos clickedPos = event.getPos();
        BlockState clickedState = serverLevel.getBlockState(clickedPos);
        ItemStack heldItem = event.getItemStack();

        // ====== BLOQUEAR ATIVAÇÃO DE PORTAL DO NETHER ======
        if ((heldItem.getItem() instanceof FlintAndSteelItem || heldItem.getItem() == Items.FIRE_CHARGE) &&
                clickedState.is(Blocks.OBSIDIAN)) {

            if (!ProgressionCoordinator.canPlayerAccessDimension(playerId, Constants.DIMENSION_TYPE_NETHER, serverLevel)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_NETHER_LOCKED)
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, Constants.DIMENSION_TYPE_NETHER);

                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.8, clickedPos.getZ() + 0.5,
                        30, 0.2, 0.2, 0.2, 0.05);
                return;
            }
        }

        // ====== BLOQUEAR COLOCAÇÃO DE ENDER EYE ======
        if (heldItem.getItem() == Items.ENDER_EYE && clickedState.is(Blocks.END_PORTAL_FRAME)) {
            if (!ProgressionCoordinator.canPlayerAccessDimension(playerId, Constants.DIMENSION_TYPE_END, serverLevel)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_END_LOCKED)
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, Constants.DIMENSION_TYPE_END);

                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.8, clickedPos.getZ() + 0.5,
                        30, 0.2, 0.2, 0.2, 0.05);
                return;
            }
        }

        // ====== BLOQUEAR ACENDIMENTO DIRETO DO PORTAL DO END ======
        if ((heldItem.getItem() instanceof FlintAndSteelItem || heldItem.getItem() == Items.FIRE_CHARGE) &&
                (clickedState.is(Blocks.END_PORTAL_FRAME) || clickedState.is(Blocks.END_PORTAL))) {

            if (!ProgressionCoordinator.canPlayerAccessDimension(playerId, Constants.DIMENSION_TYPE_END, serverLevel)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_END_LOCKED)
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, Constants.DIMENSION_TYPE_END);

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
        UUID playerId = player.getUUID();

        // 🎯 CORREÇÃO: Usar ProgressionCoordinator para verificação unificada
        if (ProgressionCoordinator.canPlayerAccessDimension(playerId, Constants.DIMENSION_TYPE_END, serverLevel)) {
            return; // End liberado para este jogador
        }

        BlockPos playerPos = player.blockPosition();

        // 🎯 PERFORMANCE: Usar pool de BlockPos para reduzir GC overhead
        BlockPos.MutableBlockPos[] positionsToCheck = {
                BlockPosPool.acquire(playerPos),
                BlockPosPool.acquire(playerPos.getX(), playerPos.getY() - 1, playerPos.getZ()),
                BlockPosPool.acquire(playerPos.getX(), playerPos.getY() + 1, playerPos.getZ()),
                BlockPosPool.acquire(playerPos.getX(), playerPos.getY(), playerPos.getZ() - 1),
                BlockPosPool.acquire(playerPos.getX(), playerPos.getY(), playerPos.getZ() + 1),
                BlockPosPool.acquire(playerPos.getX() + 1, playerPos.getY(), playerPos.getZ()),
                BlockPosPool.acquire(playerPos.getX() - 1, playerPos.getY(), playerPos.getZ())
        };

        boolean isInEndPortal = false;
        BlockPos portalPos = null;

        try {
            for (BlockPos.MutableBlockPos pos : positionsToCheck) {
                BlockState blockState = serverLevel.getBlockState(pos);
                if (blockState.is(Blocks.END_PORTAL)) {
                    isInEndPortal = true;
                    portalPos = pos.immutable(); // Criar cópia imutável para uso posterior
                    break;
                }
            }
        } finally {
            // 🎯 PERFORMANCE: Retornar todas as posições ao pool
            for (BlockPos.MutableBlockPos pos : positionsToCheck) {
                BlockPosPool.release(pos);
            }
        }

        if (isInEndPortal && portalPos != null) {
            // CORREÇÃO: Não teleportar mais - apenas impedir permanência no portal
            // Empurrar jogador suavemente para fora
            double pushX = (player.getX() - (portalPos.getX() + 0.5)) * 0.3;
            double pushZ = (player.getZ() - (portalPos.getZ() + 0.5)) * 0.3;
            
            // Aplicar pequeno impulso para fora
            player.push(pushX, 0.1, pushZ);
            
            player.sendSystemMessage(Component.translatable(Constants.MSG_END_LOCKED)
                    .withStyle(ChatFormatting.RED));

            playDenialEffects(serverLevel, player, Constants.DIMENSION_TYPE_END);
        }
    }

    // ============================================================================
    // 🎯 MÉTODOS AUXILIARES
    // ============================================================================

    /**
     * 🎯 NOVO: Encontra qual fase customizada está bloqueando o acesso a uma dimensão
     */
    private static void refreshDimensionCache() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheRefresh < CACHE_REFRESH_INTERVAL) {
            return; // Cache ainda válido
        }
        
        DIMENSION_TO_PHASES_CACHE.clear();
        var customPhases = net.mirai.dimtr.config.CustomRequirements.getAllCustomPhases();
        
        for (var entry : customPhases.entrySet()) {
            String phaseId = entry.getKey();
            var phase = entry.getValue();
            
            if (phase.dimensionAccess != null) {
                for (String dimension : phase.dimensionAccess) {
                    DIMENSION_TO_PHASES_CACHE.computeIfAbsent(dimension, k -> ConcurrentHashMap.newKeySet())
                        .add(phaseId);
                }
            }
        }
        
        lastCacheRefresh = currentTime;
    }
    
    /**
     * 🎯 PERFORMANCE: Método otimizado para encontrar fase bloqueante
     * TODO: Remover se não for utilizado no futuro
     */
    @SuppressWarnings("unused")
    private static String findBlockingCustomPhase(UUID playerId, String dimensionString, ServerLevel serverLevel) {
        refreshDimensionCache();
        
        Set<String> phasesForDimension = DIMENSION_TO_PHASES_CACHE.get(dimensionString);
        if (phasesForDimension == null || phasesForDimension.isEmpty()) {
            return null; // Nenhuma fase bloqueia esta dimensão
        }
        
        var progressionManager = ProgressionManager.get(serverLevel);
        var playerData = progressionManager.getPlayerData(playerId);
        
        // Verificar apenas as fases que realmente afetam esta dimensão
        for (String phaseId : phasesForDimension) {
            if (!playerData.isCustomPhaseComplete(phaseId)) {
                return phaseId;
            }
        }
        
        return null;
    }

    private static void playDenialEffects(ServerLevel serverLevel, Player player, String portalType) {
        long currentTime = serverLevel.getGameTime();
        UUID playerId = player.getUUID();

        if (Constants.DIMENSION_TYPE_NETHER.equals(portalType)) {
            long lastPlayed = netherPortalSoundCooldowns.getOrDefault(playerId, 0L);
            if (currentTime > lastPlayed + PORTAL_SOUND_COOLDOWN_TICKS) {
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRIGGER,
                        SoundSource.PLAYERS, 0.7F, 0.5F);
                netherPortalSoundCooldowns.put(playerId, currentTime);
            }

            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 1, player.getZ(),
                    10, 0.3, 0.3, 0.3, 0.05);

        } else if (Constants.DIMENSION_TYPE_END.equals(portalType)) {
            long lastPlayed = endPortalSoundCooldowns.getOrDefault(playerId, 0L);
            if (currentTime > lastPlayed + PORTAL_SOUND_COOLDOWN_TICKS) {
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                        SoundSource.PLAYERS, 1.0F, 0.5F);
                endPortalSoundCooldowns.put(playerId, currentTime);
            }

            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.1);
        } else if (Constants.DIMENSION_TYPE_CUSTOM.equals(portalType)) {
            // Custom portal denial effects
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE,
                    SoundSource.PLAYERS, 0.8F, 0.8F);

            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                    player.getX(), player.getY() + 1, player.getZ(),
                    15, 0.4, 0.4, 0.4, 0.1);
        }
    }

    /**
     * 🎯 NOVO: Processar mobs customizados via ProgressionCoordinator
     */
    private static void processCustomMobKill(UUID playerId, LivingEntity killedEntity, ServerLevel serverLevel) {
        String entityType = killedEntity.getType().toString();
        String entityId = entityType.toLowerCase();
        
        // Check if any custom phases require this mob
        var customPhases = net.mirai.dimtr.config.CustomRequirements.getAllCustomPhases();
        if (customPhases.isEmpty()) {
            return;
        }
        
        for (var entry : customPhases.entrySet()) {
            String phaseId = entry.getKey();
            var phase = entry.getValue();
            
            // Check if this phase requires this mob type
            if (phase.mobRequirements != null && phase.mobRequirements.containsKey(entityId)) {
                // 🎯 MUDANÇA: Usar ProgressionCoordinator ao invés de lógica individual
                ProgressionCoordinator.processCustomMobKill(playerId, phaseId, entityId, serverLevel);
            }
        }
    }

    /**
     * 🎯 NOVO: Processar morte de bosses de mods externos
     */
    private static void processExternalModBossKill(UUID playerId, LivingEntity killedEntity, ServerLevel serverLevel) {
        // 🎯 CORREÇÃO CRÍTICA: Usar BuiltInRegistries para obter ResourceLocation correta
        ResourceLocation entityTypeLocation = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(killedEntity.getType());
        String entityId = entityTypeLocation != null ? entityTypeLocation.toString() : killedEntity.getType().toString();
        
        // ✅ DEBUG: Log de todas as mortes para verificar detecção
        DimTrMod.LOGGER.debug("🔍 Entity killed: {} (ID: {}) by player {}", 
            killedEntity.getClass().getSimpleName(), entityId, playerId);
        
        // Verificar se é um boss de mod externo
        boolean isExternalBoss = ExternalModIntegration.isExternalBoss(entityId);
        
        if (!isExternalBoss) {
            return;
        }
        
        // Get boss phase assignment
        int phase = ExternalModIntegration.getBossPhase(entityId);
        String displayName = ExternalModIntegration.getBossDisplayName(entityId);
        
        DimTrMod.LOGGER.info("🏆 External mod boss defeated: {} (ID: {}) by player {} (Phase: {})", 
            displayName, entityId, playerId, phase);
        
        // Processar através do ProgressionCoordinator como objetivo especial
        boolean processed = ProgressionCoordinator.processExternalBossObjective(playerId, entityId, phase, serverLevel);
        
        if (processed) {
            DimTrMod.LOGGER.info("✅ External boss {} processed successfully for player {}", 
                displayName, playerId);
        } else {
            DimTrMod.LOGGER.warn("❌ External boss {} was NOT processed for player {} (already completed?)", 
                displayName, playerId);
        }
    }

    // ============================================================================
    // 🆕 MÉTODOS AUXILIARES PARA INTEGRAÇÃO DOS NOVOS SISTEMAS
    // ============================================================================
    
    /**
     * 🔍 Verificar se uma entidade é considerada um boss
     */
    private static boolean isBossEntity(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        return type == EntityType.WITHER || 
               type == EntityType.ENDER_DRAGON || 
               type == EntityType.ELDER_GUARDIAN || 
               type == EntityType.WARDEN ||
               ExternalModIntegration.isExternalBoss(entity.getType().toString());
    }
    
    /**
     * 🆔 Obter ID único do boss para validação
     */
    private static String getBossId(LivingEntity entity) {
        ResourceLocation entityTypeLocation = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return entityTypeLocation != null ? entityTypeLocation.toString() : entity.getType().toString();
    }
}