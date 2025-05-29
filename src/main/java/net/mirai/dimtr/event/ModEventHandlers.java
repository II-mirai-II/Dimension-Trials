package net.mirai.dimtr.event;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.command.DimTrCommands;
import net.mirai.dimtr.data.ProgressionData;
import net.mirai.dimtr.init.ModItems;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload; // IMPORT ADICIONADO
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor; // IMPORT ADICIONADO

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEventHandlers {

    public static final String NBT_MOD_SUBTAG_KEY = DimTrMod.MODID;
    public static final String NBT_FLAG_RECEIVED_BOOK = "received_progression_book";

    private static final int PORTAL_SOUND_COOLDOWN_TICKS = 40;
    private static final Map<UUID, Long> netherPortalSoundCooldowns = new HashMap<>();
    private static final Map<UUID, Long> endPortalSoundCooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DimTrCommands.register(event.getDispatcher());
        DimTrMod.LOGGER.info("DimTr Commands registered.");
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel) {
            ProgressionData progressionData = ProgressionData.get(serverLevel);
            LivingEntity deadEntity = event.getEntity();

            if (deadEntity.getType() == EntityType.ELDER_GUARDIAN) {
                if (progressionData.updateElderGuardianKilled(true)) {
                    DimTrMod.LOGGER.info("Elder Guardian killed. Progress updated.");
                }
            } else if (deadEntity.getType() == EntityType.RAVAGER) {
                if (progressionData.updateRavagerKilled(true)) {
                    DimTrMod.LOGGER.info("Ravager killed. Progress updated.");
                }
            } else if (deadEntity.getType() == EntityType.EVOKER) {
                if (progressionData.updateEvokerKilled(true)) {
                    DimTrMod.LOGGER.info("Evoker killed. Progress updated.");
                }
            } else if (deadEntity.getType() == EntityType.WITHER) {
                if (progressionData.updateWitherKilled(true)) {
                    DimTrMod.LOGGER.info("Wither killed. Progress updated.");
                }
            } else if (deadEntity.getType() == EntityType.WARDEN) {
                if (progressionData.updateWardenKilled(true)) {
                    DimTrMod.LOGGER.info("Warden killed. Progress updated.");
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
                        DimTrMod.LOGGER.info("Hero of the Village advancement ({}) earned by {}. Progress updated.", advancement.id(), player.getName().getString());
                    }
                } else if (advancement.id().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/under_lock_and_key"))) {
                    if(progressionData.updateTrialVaultAdvancementEarned(true)) {
                        DimTrMod.LOGGER.info("Under Lock and Key (Trial Vault) advancement ({}) earned by {}. Progress updated.", advancement.id(), player.getName().getString());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ProgressionData progressionData = ProgressionData.get(serverLevel);
        BlockPos clickedPos = event.getPos();
        BlockState clickedState = serverLevel.getBlockState(clickedPos);
        BlockPos firePos = clickedPos.relative(event.getFace());
        BlockState firePosState = serverLevel.getBlockState(firePos);
        long currentTime = serverLevel.getGameTime();

        if (event.getItemStack().getItem() instanceof FlintAndSteelItem &&
                clickedState.is(Blocks.OBSIDIAN) &&
                firePosState.canBeReplaced()) {
            if (progressionData.isPhase1EffectivelyLocked()) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_NETHER_LOCKED));

                long lastPlayed = netherPortalSoundCooldowns.getOrDefault(player.getUUID(), 0L);
                if (currentTime > lastPlayed + PORTAL_SOUND_COOLDOWN_TICKS) {
                    DimTrMod.LOGGER.debug("Player {} denied Nether access. Playing PORTAL_TRIGGER sound.", player.getName().getString());
                    serverLevel.playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.7F, 0.8F + serverLevel.random.nextFloat() * 0.4F);
                    netherPortalSoundCooldowns.put(player.getUUID(), currentTime);
                }

                serverLevel.sendParticles(ParticleTypes.SMOKE, firePos.getX() + 0.5, firePos.getY() + 0.5, firePos.getZ() + 0.5, 15, 0.1, 0.1, 0.1, 0.02);
                return;
            }
        }

        if (event.getItemStack().getItem() == Items.ENDER_EYE &&
                clickedState.is(Blocks.END_PORTAL_FRAME)) {
            if (progressionData.isPhase2EffectivelyLocked()) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.translatable(Constants.MSG_END_LOCKED));

                long lastPlayed = endPortalSoundCooldowns.getOrDefault(player.getUUID(), 0L);
                if (currentTime > lastPlayed + PORTAL_SOUND_COOLDOWN_TICKS) {
                    DimTrMod.LOGGER.debug("Player {} denied End portal access. Playing ENDERMAN_TELEPORT sound.", player.getName().getString());
                    serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);
                    endPortalSoundCooldowns.put(player.getUUID(), currentTime);
                }

                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, clickedPos.getX() + 0.5, clickedPos.getY() + 0.8, clickedPos.getZ() + 0.5, 30, 0.2, 0.2, 0.2, 0.05);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Lógica para dar o livro com NBT
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag modData;
            if (persistentData.contains(NBT_MOD_SUBTAG_KEY, CompoundTag.TAG_COMPOUND)) {
                modData = persistentData.getCompound(NBT_MOD_SUBTAG_KEY);
            } else {
                modData = new CompoundTag();
            }

            if (!modData.getBoolean(NBT_FLAG_RECEIVED_BOOK)) {
                ItemStack bookItemStack = new ItemStack(ModItems.PROGRESSION_BOOK.get());
                boolean addedToInventory = player.getInventory().add(bookItemStack);
                if (!addedToInventory) {
                    if (player.getInventory().offhand.get(0).isEmpty()) {
                        player.getInventory().offhand.set(0, bookItemStack);
                        addedToInventory = true;
                        DimTrMod.LOGGER.info("Progression Book given to player {} (in offhand).", player.getName().getString());
                    }
                }

                if (!addedToInventory) {
                    player.drop(bookItemStack, false);
                    DimTrMod.LOGGER.info("Progression Book given to player {} (dropped as inventory was full).", player.getName().getString());
                } else if (player.getInventory().contains(bookItemStack)) {
                    DimTrMod.LOGGER.info("Progression Book given to player {} (in main inventory).", player.getName().getString());
                }

                modData.putBoolean(NBT_FLAG_RECEIVED_BOOK, true);
                persistentData.put(NBT_MOD_SUBTAG_KEY, modData);
            }

            // Enviar o estado de progressão atual para o jogador que logou
            ProgressionData worldProgression = ProgressionData.get(player.serverLevel());
            UpdateProgressionToClientPayload currentProgressionPayload = new UpdateProgressionToClientPayload(worldProgression);
            PacketDistributor.sendToPlayer(player, currentProgressionPayload);
            DimTrMod.LOGGER.info("Sent current progression state to player {}.", player.getName().getString());
        }
    }
}