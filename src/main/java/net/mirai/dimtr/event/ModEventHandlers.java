package net.mirai.dimtr.event;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.command.DimTrCommands;
import net.mirai.dimtr.data.ProgressionData;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload;
import net.mirai.dimtr.util.Constants;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.ChatFormatting;
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
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEventHandlers {

    public static final String NBT_MOD_SUBTAG_KEY = DimTrMod.MODID;
    public static final String NBT_FLAG_RECEIVED_BOOK = "received_progression_book";

    private static final int PORTAL_SOUND_COOLDOWN_TICKS = 40;
    private static final int TELEPORT_COOLDOWN_TICKS = 100; // 5 segundos de cooldown para teleporte
    private static final Map<UUID, Long> netherPortalSoundCooldowns = new HashMap<>();
    private static final Map<UUID, Long> endPortalSoundCooldowns = new HashMap<>();
    private static final Map<UUID, Long> teleportCooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DimTrCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Verificar se foi morto por um jogador
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // MANTIDO: Objetivos especiais (bosses únicos)
        if (entity.getType() == EntityType.ELDER_GUARDIAN) {
            if (progressionData.updateElderGuardianKilled(true)) {
                DimTrMod.LOGGER.info("Elder Guardian killed! Phase 1 progress updated.");
            }
        } else if (entity.getType() == EntityType.RAVAGER) {
            if (progressionData.updateRavagerKilled(true)) {
                DimTrMod.LOGGER.info("Ravager killed! Phase 1 progress updated.");
            }
        } else if (entity.getType() == EntityType.EVOKER) {
            if (progressionData.updateEvokerKilled(true)) {
                DimTrMod.LOGGER.info("Evoker killed! Phase 1 progress updated.");
            }
        } else if (entity.getType() == EntityType.WITHER) {
            if (progressionData.updateWitherKilled(true)) {
                DimTrMod.LOGGER.info("Wither killed! Phase 2 progress updated.");
            }
        } else if (entity.getType() == EntityType.WARDEN) {
            if (progressionData.updateWardenKilled(true)) {
                DimTrMod.LOGGER.info("Warden killed! Phase 2 progress updated.");
            }
        }

        // CORRIGIDO: Sistema de contagem de mobs comuns
        String mobType = getMobType(entity);
        if (mobType != null) {
            boolean updated = progressionData.incrementMobKill(mobType);
            if (updated) {
                DimTrMod.LOGGER.debug("Player {} killed a {} (count incremented)",
                        player.getDisplayName().getString(), mobType);
            }
        }
    }

    // CORRIGIDO: Método para mapear entidades para strings usadas no sistema
    private static String getMobType(LivingEntity entity) {
        // Fase 1 - Mobs Comuns do Overworld
        if (entity instanceof Zombie && !(entity instanceof ZombieVillager) && !(entity instanceof Husk)) {
            return "zombie";
        } else if (entity instanceof ZombieVillager) {
            return "zombie_villager";
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
        } else if (entity instanceof Drowned) {
            return "drowned";
        } else if (entity instanceof EnderMan) {
            return "enderman";
        } else if (entity instanceof Witch) {
            return "witch";
        } else if (entity instanceof Pillager pillager) {
            // IMPORTANTE: Verificar Captain PRIMEIRO
            if (pillager.canBeLeader()) {
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
        } else if (entity instanceof Endermite) {
            return "endermite";
        } else if (entity instanceof Piglin piglin) {
            // CORRIGIDO: Usar apenas getTarget() para verificar se Piglin é hostil
            // Um Piglin é considerado hostil se ele tem um alvo de ataque
            if (piglin.getTarget() != null) {
                return "piglin";
            }
            // ALTERNATIVA: Verificar se está em modo de ataque
            // Também podemos verificar se não está "calmo" (isAdult e não tem item de ouro)
            if (piglin.isAdult() && !piglin.isHolding(Items.GOLD_INGOT)) {
                // Considera como hostil se é adulto e não está segurando ouro
                return "piglin";
            }
        }

        // ADICIONADO: Mobs mais novos que podem não estar disponíveis em todas as versões
        String entityName = entity.getType().toString().toLowerCase();
        if (entityName.contains("bogged")) {
            return "bogged";
        } else if (entityName.contains("breeze")) {
            return "breeze";
        }

        return null;
    }

    @SubscribeEvent
    public static void onPlayerGetAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ProgressionData progressionData = ProgressionData.get(serverLevel);
        AdvancementHolder advancement = event.getAdvancement();
        ResourceLocation advancementId = advancement.id();

        if (advancementId.equals(ResourceLocation.withDefaultNamespace("adventure/hero_of_the_village"))) {
            if (progressionData.updateRaidWon(true)) {
                DimTrMod.LOGGER.info("Hero of the Village advancement earned! Phase 1 progress updated.");
            }
        } else if (advancementId.equals(ResourceLocation.withDefaultNamespace("adventure/under_lock_and_key"))) {
            if (progressionData.updateTrialVaultAdvancementEarned(true)) {
                DimTrMod.LOGGER.info("Under Lock and Key advancement earned! Phase 1 progress updated.");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel serverLevel = player.serverLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);
        UpdateProgressionToClientPayload payload = progressionData.createPayload();
        PacketDistributor.sendToPlayer(player, payload);
    }

    // ============================================================================
    // SISTEMA COMPLETO DE BLOQUEIO DE PORTAIS COM TELEPORTE PARA WORLDSPAWN
    // ============================================================================

    // 1. BLOQUEAR VIAGEM DIMENSIONAL (caso o portal já esteja ativo) + TELEPORTE PARA WORLDSPAWN
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) player.level();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // CORREÇÃO: Log detalhado para debug
        DimTrMod.LOGGER.debug("Player {} attempting to travel to dimension: {}",
                player.getName().getString(), event.getDimension().location());

        // Verificar acesso ao Nether
        if (event.getDimension() == Level.NETHER) {
            boolean isLocked = progressionData.isPhase1EffectivelyLocked();
            DimTrMod.LOGGER.debug("Nether access check - Phase 1 locked: {}", isLocked);

            if (isLocked) {
                event.setCanceled(true);

                // NOVO: Teleportar para WorldSpawn
                teleportToWorldSpawn(player, serverLevel, "nether");

                DimTrMod.LOGGER.info("Blocked {} from entering Nether - Phase 1 incomplete. Teleported to spawn.",
                        player.getName().getString());
                return;
            }
        }

        // Verificar acesso ao End
        if (event.getDimension() == Level.END) {
            boolean isLocked = progressionData.isPhase2EffectivelyLocked();
            DimTrMod.LOGGER.debug("End access check - Phase 2 locked: {}", isLocked);

            if (isLocked) {
                event.setCanceled(true);

                // NOVO: Teleportar para WorldSpawn
                teleportToWorldSpawn(player, serverLevel, "end");

                DimTrMod.LOGGER.info("Blocked {} from entering End - Phase 2 incomplete. Teleported to spawn.",
                        player.getName().getString());
                return;
            }
        }

        DimTrMod.LOGGER.debug("Allowing {} to travel to {}",
                player.getName().getString(), event.getDimension().location());
    }

    // 2. BLOQUEAR ATIVAÇÃO/INTERAÇÃO COM PORTAIS
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ProgressionData progressionData = ProgressionData.get(serverLevel);
        BlockPos clickedPos = event.getPos();
        BlockState clickedState = serverLevel.getBlockState(clickedPos);
        ItemStack heldItem = event.getItemStack();

        // ====== BLOQUEAR ATIVAÇÃO DE PORTAL DO NETHER ======
        if ((heldItem.getItem() instanceof FlintAndSteelItem || heldItem.getItem() == Items.FIRE_CHARGE) &&
                clickedState.is(Blocks.OBSIDIAN)) {

            if (progressionData.isPhase1EffectivelyLocked()) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_NETHER_LOCKED)
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, "nether");

                // Partículas extras na posição clicada
                BlockPos firePos = clickedPos.relative(event.getFace());
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        firePos.getX() + 0.5, firePos.getY() + 0.5, firePos.getZ() + 0.5,
                        15, 0.1, 0.1, 0.1, 0.02);

                DimTrMod.LOGGER.info("Blocked {} from lighting Nether portal - Phase 1 incomplete",
                        player.getName().getString());
                return;
            }
        }

        // ====== BLOQUEAR COLOCAÇÃO DE ENDER EYE ======
        if (heldItem.getItem() == Items.ENDER_EYE && clickedState.is(Blocks.END_PORTAL_FRAME)) {
            if (progressionData.isPhase2EffectivelyLocked()) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_END_LOCKED)
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, "end");

                // Partículas extras na posição clicada
                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.8, clickedPos.getZ() + 0.5,
                        30, 0.2, 0.2, 0.2, 0.05);

                DimTrMod.LOGGER.info("Blocked {} from placing Ender Eye - Phase 2 incomplete",
                        player.getName().getString());
                return;
            }
        }

        // ====== BLOQUEAR ACENDIMENTO DIRETO DO PORTAL DO END ======
        if ((heldItem.getItem() instanceof FlintAndSteelItem || heldItem.getItem() == Items.FIRE_CHARGE) &&
                (clickedState.is(Blocks.END_PORTAL_FRAME) || clickedState.is(Blocks.END_PORTAL))) {

            if (progressionData.isPhase2EffectivelyLocked()) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_END_LOCKED)
                        .withStyle(ChatFormatting.RED));

                playDenialEffects(serverLevel, player, "end");

                DimTrMod.LOGGER.info("Blocked {} from lighting End portal - Phase 2 incomplete",
                        player.getName().getString());
                return;
            }
        }
    }

    // 3. DETECTAR COLISÃO COM PORTAL DO END (caso o portal já esteja completo) + TELEPORTE PARA WORLDSPAWN
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // CORREÇÃO: Usar PlayerTickEvent.Post em vez de TickEvent.PlayerTickEvent
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel serverLevel = player.serverLevel();
        ProgressionData progressionData = ProgressionData.get(serverLevel);

        // Verificar se Phase 2 está bloqueada
        if (!progressionData.isPhase2EffectivelyLocked()) {
            return; // End liberado, não bloquear
        }

        BlockPos playerPos = player.blockPosition();

        // Verificar múltiplas posições ao redor do jogador
        BlockPos[] positionsToCheck = {
                playerPos,                    // Posição atual
                playerPos.below(),            // Abaixo
                playerPos.above(),            // Acima
                playerPos.north(),            // Norte
                playerPos.south(),            // Sul
                playerPos.east(),             // Leste
                playerPos.west()              // Oeste
        };

        boolean isInEndPortal = false;
        BlockPos portalPos = null;

        for (BlockPos checkPos : positionsToCheck) {
            BlockState state = serverLevel.getBlockState(checkPos);
            if (state.is(Blocks.END_PORTAL)) {
                isInEndPortal = true;
                portalPos = checkPos;
                break;
            }
        }

        if (isInEndPortal) {
            // NOVO: Teleportar para WorldSpawn em vez de só empurrar para cima
            teleportToWorldSpawn(player, serverLevel, "end");

            // Partículas adicionais no portal
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    portalPos.getX() + 0.5, portalPos.getY() + 1, portalPos.getZ() + 0.5,
                    50, 0.5, 0.3, 0.5, 0.1);

            DimTrMod.LOGGER.info("Blocked {} from using End portal - Phase 2 incomplete. Teleported to spawn.",
                    player.getName().getString());
        }
    }

    // ============================================================================
    // MÉTODOS AUXILIARES
    // ============================================================================

    // NOVO: Método para teleportar para WorldSpawn com cooldown
    private static void teleportToWorldSpawn(ServerPlayer player, ServerLevel serverLevel, String portalType) {
        long currentTime = serverLevel.getGameTime();
        UUID playerId = player.getUUID();

        // Verificar cooldown de teleporte para evitar spam
        long lastTeleport = teleportCooldowns.getOrDefault(playerId, 0L);
        if (currentTime <= lastTeleport + TELEPORT_COOLDOWN_TICKS) {
            return; // Ainda no cooldown, não teleportar novamente
        }

        // Obter posição do WorldSpawn
        BlockPos spawnPos = serverLevel.getSharedSpawnPos();
        double spawnX = spawnPos.getX() + 0.5;
        double spawnY = spawnPos.getY();
        double spawnZ = spawnPos.getZ() + 0.5;

        // Verificar se é uma posição segura (não dentro de blocos)
        BlockPos safePos = findSafeSpawnPosition(serverLevel, spawnPos);
        if (safePos != null) {
            spawnX = safePos.getX() + 0.5;
            spawnY = safePos.getY();
            spawnZ = safePos.getZ() + 0.5;
        }

        // Teleportar o jogador
        player.teleportTo(spawnX, spawnY, spawnZ);

        // Enviar mensagem personalizada baseada no tipo de portal
        Component message;
        if ("nether".equals(portalType)) {
            message = Component.translatable(Constants.MSG_NETHER_LOCKED_TELEPORT)
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
        } else if ("end".equals(portalType)) {
            message = Component.translatable(Constants.MSG_END_LOCKED_TELEPORT)
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
        } else {
            message = Component.literal("§c§l[DimTr] Você foi teleportado para o spawn por tentar acessar uma dimensão bloqueada!");
        }

        player.sendSystemMessage(message);

        // Som e partículas de teleporte
        playTeleportEffects(serverLevel, player, portalType);

        // Definir cooldown
        teleportCooldowns.put(playerId, currentTime);

        DimTrMod.LOGGER.info("Teleported {} to world spawn due to {} portal violation",
                player.getName().getString(), portalType);
    }

    // NOVO: Encontrar posição segura próxima ao spawn
    private static BlockPos findSafeSpawnPosition(ServerLevel serverLevel, BlockPos originalSpawn) {
        // Verificar posição original primeiro
        if (isSafePosition(serverLevel, originalSpawn)) {
            return originalSpawn;
        }

        // Procurar em um raio de 5 blocos ao redor do spawn
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = -3; y <= 10; y++) {
                    BlockPos testPos = originalSpawn.offset(x, y, z);
                    if (isSafePosition(serverLevel, testPos)) {
                        return testPos;
                    }
                }
            }
        }

        // Se não encontrar, usar a posição original mesmo
        return originalSpawn;
    }

    // NOVO: Verificar se uma posição é segura para teleporte
    private static boolean isSafePosition(ServerLevel serverLevel, BlockPos pos) {
        BlockState groundState = serverLevel.getBlockState(pos.below());
        BlockState posState = serverLevel.getBlockState(pos);
        BlockState aboveState = serverLevel.getBlockState(pos.above());

        // Verificar se há chão sólido e espaço suficiente acima
        return groundState.isSolid() &&
                !posState.isSolid() &&
                !aboveState.isSolid() &&
                !groundState.is(Blocks.LAVA) &&
                !posState.is(Blocks.LAVA) &&
                !aboveState.is(Blocks.LAVA);
    }

    // NOVO: Efeitos de teleporte
    private static void playTeleportEffects(ServerLevel serverLevel, ServerPlayer player, String portalType) {
        // Som de teleporte
        serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 1.0F, 1.2F);

        // Partículas de teleporte
        if ("nether".equals(portalType)) {
            // Partículas vermelhas para Nether
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 1.0, 0.5, 0.1);
        } else if ("end".equals(portalType)) {
            // Partículas roxas para End
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 1.0, 0.5, 0.1);
        }

        // Partículas adicionais de teleporte
        serverLevel.sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + 0.5, player.getZ(),
                20, 0.3, 0.3, 0.3, 0.05);
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

            // Partículas de fumaça
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

            // Partículas do portal reverso
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    20, 0.3, 0.3, 0.3, 0.1);
        }
    }
}