package net.mirai.dimtr.util;

import net.mirai.dimtr.DimTrMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;

/**
 * Utility class for sending rich notifications to players.
 * Provides consistent visual and audio feedback for important events.
 */
public class NotificationHelper {

    /**
     * Notification types with different visual styles and sounds.
     */
    public enum NotificationType {
        SUCCESS(ChatFormatting.GREEN, "✅", SoundEvents.EXPERIENCE_ORB_PICKUP),
        INFO(ChatFormatting.AQUA, "ℹ️", SoundEvents.NOTE_BLOCK_BELL),
        WARNING(ChatFormatting.YELLOW, "⚠️", SoundEvents.NOTE_BLOCK_SNARE),
        ERROR(ChatFormatting.RED, "❌", SoundEvents.NOTE_BLOCK_BASS),
        ACHIEVEMENT(ChatFormatting.GOLD, "🏆", SoundEvents.UI_TOAST_CHALLENGE_COMPLETE),
        PARTY(ChatFormatting.LIGHT_PURPLE, "👥", SoundEvents.NOTE_BLOCK_CHIME);

        public final ChatFormatting color;
        public final String icon;
        public final SoundEvent sound;

        NotificationType(ChatFormatting color, String icon, Holder.Reference<SoundEvent> soundHolder) {
            this.color = color;
            this.icon = icon;
            this.sound = soundHolder.value();
        }
        
        NotificationType(ChatFormatting color, String icon, SoundEvent sound) {
            this.color = color;
            this.icon = icon;
            this.sound = sound;
        }
    }

    /**
     * Sends a notification to a player with visual and audio feedback.
     * @param player The player to send the notification to
     * @param type The type of notification
     * @param title The notification title
     * @param message The notification message (optional)
     * @param playSound Whether to play the associated sound
     */
    public static void sendNotification(ServerPlayer player, NotificationType type, String title, 
                                       String message, boolean playSound) {
        // Send title message
        Component titleComponent = Component.literal(type.icon + " " + title)
                .withStyle(type.color);
        player.sendSystemMessage(titleComponent);
        
        // Send message if provided
        if (message != null && !message.isEmpty()) {
            Component messageComponent = Component.literal(message)
                    .withStyle(ChatFormatting.GRAY);
            player.sendSystemMessage(messageComponent);
        }
        
        // Play sound if requested
        if (playSound && type.sound != null) {
            player.playNotifySound(type.sound, SoundSource.MASTER, 1.0f, 1.0f);
        }
    }

    /**
     * Sends a notification with default sound enabled.
     */
    public static void sendNotification(ServerPlayer player, NotificationType type, String title, String message) {
        sendNotification(player, type, title, message, true);
    }

    /**
     * Sends a notification with only a title.
     */
    public static void sendNotification(ServerPlayer player, NotificationType type, String title) {
        sendNotification(player, type, title, null, true);
    }

    /**
     * Sends a success notification.
     */
    public static void sendSuccess(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.SUCCESS, title, message);
    }

    /**
     * Sends a success notification with only a title.
     */
    public static void sendSuccess(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.SUCCESS, title);
    }

    /**
     * Sends an info notification.
     */
    public static void sendInfo(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.INFO, title, message);
    }

    /**
     * Sends an info notification with only a title.
     */
    public static void sendInfo(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.INFO, title);
    }

    /**
     * Sends a warning notification.
     */
    public static void sendWarning(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.WARNING, title, message);
    }

    /**
     * Sends a warning notification with only a title.
     */
    public static void sendWarning(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.WARNING, title);
    }

    /**
     * Sends an error notification.
     */
    public static void sendError(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.ERROR, title, message);
    }

    /**
     * Sends an error notification with only a title.
     */
    public static void sendError(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.ERROR, title);
    }

    /**
     * Sends an achievement notification.
     */
    public static void sendAchievement(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.ACHIEVEMENT, title, message);
    }

    /**
     * Sends an achievement notification with only a title.
     */
    public static void sendAchievement(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.ACHIEVEMENT, title);
    }

    /**
     * Sends a party-related notification.
     */
    public static void sendParty(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.PARTY, title, message);
    }

    /**
     * Sends a party-related notification with only a title.
     */
    public static void sendParty(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.PARTY, title);
    }

    /**
     * Sends a progress update notification.
     * @param player The player
     * @param objectiveName The name of the objective
     * @param current Current progress
     * @param required Required progress
     * @param isCompleted Whether the objective is now completed
     */
    public static void sendProgressUpdate(ServerPlayer player, String objectiveName, int current, 
                                         int required, boolean isCompleted) {
        if (isCompleted) {
            sendAchievement(player, 
                Component.translatable(Constants.NOTIFICATION_PROGRESS_COMPLETE, objectiveName).getString(),
                Component.translatable(Constants.NOTIFICATION_PROGRESS_COMPLETE_DESC).getString());
        } else {
            String progressText = Component.translatable(Constants.NOTIFICATION_PROGRESS_PREFIX, current, required).getString();
            sendInfo(player, objectiveName, progressText);
        }
    }

    /**
     * Sends a phase completion notification with celebration effects.
     * @param player The player
     * @param phaseName The name of the completed phase
     * @param unlockedContent What was unlocked
     */
    public static void sendPhaseCompletion(ServerPlayer player, String phaseName, String unlockedContent) {
        // Main completion message
        sendAchievement(player, 
                Component.translatable(Constants.NOTIFICATION_PHASE_COMPLETE, phaseName).getString(),
                Component.translatable(Constants.NOTIFICATION_PHASE_COMPLETE_CONGRATS, phaseName).getString());
        
        // Unlocked content message
        if (unlockedContent != null && !unlockedContent.isEmpty()) {
            sendSuccess(player, 
                    Component.translatable(Constants.NOTIFICATION_CONTENT_UNLOCKED).getString(),
                    Component.translatable(Constants.NOTIFICATION_CONTENT_ACCESSIBLE, unlockedContent).getString());
        }
        
        // Additional celebration sound
        player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1.0f, 1.2f);
    }

    /**
     * Sends a party join notification to all party members.
     * @param newMember The player who joined
     * @param partyMembers Array of all party members to notify
     * @param partyName The name of the party
     */
    public static void sendPartyJoinNotification(ServerPlayer newMember, ServerPlayer[] partyMembers, String partyName) {
        String joinMessage = Component.translatable(Constants.NOTIFICATION_PARTY_JOIN, newMember.getName().getString()).getString();
        
        for (ServerPlayer member : partyMembers) {
            if (member != newMember) {
                sendParty(member, Component.translatable(Constants.NOTIFICATION_PARTY_UPDATE).getString(), joinMessage);
            }
        }
        
        // Welcome message for the new member
        sendParty(newMember, 
                Component.translatable(Constants.NOTIFICATION_PARTY_WELCOME, partyName).getString(),
                Component.translatable(Constants.NOTIFICATION_PARTY_SHARE_PROGRESS).getString());
    }

    /**
     * Sends a multiplier notification when a player gains a multiplier.
     * @param player The player
     * @param multiplier The new multiplier value
     * @param reason The reason for gaining the multiplier
     */
    public static void sendMultiplierGained(ServerPlayer player, float multiplier, String reason) {
        String title = Component.translatable(Constants.NOTIFICATION_MULTIPLIER_GAINED).getString();
        String message = Component.translatable(Constants.NOTIFICATION_MULTIPLIER_FROM, 
                String.format("%.1fx", multiplier), reason).getString();
        sendSuccess(player, title, message);
    }

    /**
     * Launches fireworks at the player's location to celebrate phase completion
     * @param player The player who completed a phase
     * @param phaseNumber The phase that was completed (for different firework colors)
     */
    public static void launchCelebrationFireworks(ServerPlayer player, int phaseNumber) {
        if (player == null || player.level() == null) return;
        
        net.minecraft.world.level.Level level = player.level();
        net.minecraft.core.BlockPos playerPos = player.blockPosition();
        
        try {
            // Sons de celebração principais
            level.playSound(null, playerPos, 
                net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 
                net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                
            level.playSound(null, playerPos, 
                net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, 
                net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 0.75F);
            
            // Abordagem simples: criar fogos de artifício básicos e lançá-los
            for (int i = 0; i < 8; i++) {
                final int index = i;
                level.getServer().tell(new net.minecraft.server.TickTask(level.getServer().getTickCount() + index * 5, () -> {
                    try {
                        // Calcular posição aleatória ao redor do jogador
                        double offsetX = (Math.random() - 0.5) * 8;
                        double offsetZ = (Math.random() - 0.5) * 8;
                        
                        // Criar um foguete com explosão colorida
                        net.minecraft.world.item.ItemStack rocket = new net.minecraft.world.item.ItemStack(
                            net.minecraft.world.item.Items.FIREWORK_ROCKET);
                            
                        // Configurar o NBT do foguete
                        net.minecraft.nbt.CompoundTag rocketTag = new net.minecraft.nbt.CompoundTag();
                        net.minecraft.nbt.CompoundTag fireworksTag = new net.minecraft.nbt.CompoundTag();
                        net.minecraft.nbt.ListTag explosionsTag = new net.minecraft.nbt.ListTag();
                        net.minecraft.nbt.CompoundTag explosionTag = new net.minecraft.nbt.CompoundTag();
                        
                        // Configurar o tipo de explosão (0-4) e efeitos
                        explosionTag.putByte("Type", (byte)(Math.random() * 5));
                        explosionTag.putBoolean("Flicker", true);
                        explosionTag.putBoolean("Trail", true);
                        
                        // Configurar as cores com base na fase
                        int[] colors;
                        int[] fadeColors;
                        
                        switch (phaseNumber) {
                            case 1: // Fase 1 - Overworld (azul/ciano)
                                colors = new int[]{43775, 16777215}; // Azul e branco
                                fadeColors = new int[]{65535}; // Ciano
                                break;
                            case 2: // Fase 2 - Nether (vermelho/laranja)
                                colors = new int[]{16711680, 16733440}; // Vermelho e laranja
                                fadeColors = new int[]{16755200}; // Amarelo
                                break;
                            default: // Fase 3 - End (roxo/rosa)
                                colors = new int[]{11141290, 16711935}; // Roxo e magenta
                                fadeColors = new int[]{8388736}; // Violeta
                                break;
                        }
                        
                        // Adicionar arrays de cores
                        explosionTag.putIntArray("Colors", colors);
                        explosionTag.putIntArray("FadeColors", fadeColors);
                        
                        // Construir o NBT completo
                        explosionsTag.add(explosionTag);
                        fireworksTag.put("Explosions", explosionsTag);
                        fireworksTag.putByte("Flight", (byte)(1 + Math.random() * 2)); // Altura do vôo
                        
                        rocketTag.put("Fireworks", fireworksTag);
                        rocket.setTag(rocketTag);
                        
                        // Criar a entidade do foguete
                        net.minecraft.world.entity.projectile.FireworkRocketEntity firework = 
                            new net.minecraft.world.entity.projectile.FireworkRocketEntity(
                                level, 
                                player.getX() + offsetX, 
                                player.getY() + 1, 
                                player.getZ() + offsetZ, 
                                rocket);
                        
                        // Não podemos acessar o campo lifetime diretamente, 
                        // mas o foguete terá um tempo de vida padrão
                        
                        // Adicionar a entidade ao mundo
                        level.addFreshEntity(firework);
                        
                        // Som de lançamento
                        level.playSound(null, 
                            player.getX() + offsetX, 
                            player.getY() + 1, 
                            player.getZ() + offsetZ, 
                            net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_LAUNCH, 
                            net.minecraft.sounds.SoundSource.AMBIENT, 
                            1.0F, 0.8F + (float)(Math.random() * 0.4));
                        
                        // Efeito visual simples com partículas adicionais
                        for (int j = 0; j < 20; j++) {
                            double px = player.getX() + offsetX + (Math.random() - 0.5) * 2;
                            double py = player.getY() + 1 + Math.random() * 2;
                            double pz = player.getZ() + offsetZ + (Math.random() - 0.5) * 2;
                            
                            if (phaseNumber == 1) {
                                level.addParticle(
                                    net.minecraft.core.particles.ParticleTypes.END_ROD,
                                    px, py, pz,
                                    (Math.random() - 0.5) * 0.1,
                                    Math.random() * 0.2,
                                    (Math.random() - 0.5) * 0.1);
                            } else if (phaseNumber == 2) {
                                level.addParticle(
                                    net.minecraft.core.particles.ParticleTypes.FLAME,
                                    px, py, pz,
                                    (Math.random() - 0.5) * 0.1,
                                    Math.random() * 0.2,
                                    (Math.random() - 0.5) * 0.1);
                            } else {
                                level.addParticle(
                                    net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH,
                                    px, py, pz,
                                    (Math.random() - 0.5) * 0.1,
                                    Math.random() * 0.2,
                                    (Math.random() - 0.5) * 0.1);
                            }
                        }
                        
                    } catch (Exception e) {
                        DimTrMod.LOGGER.error("Erro ao lançar fogos de artifício: {}", e.getMessage());
                    }
                }));
            }
            
            // Adicionar mais partículas para melhorar o efeito visual
            for (int i = 0; i < 200; i++) {
                final int index = i;
                level.getServer().tell(new net.minecraft.server.TickTask(level.getServer().getTickCount() + index / 10, () -> {
                    try {
                        // Partículas mais próximas do jogador
                        double offsetX = (Math.random() - 0.5) * 10;
                        double offsetY = Math.random() * 5;
                        double offsetZ = (Math.random() - 0.5) * 10;
                        
                        // Partículas temáticas conforme a fase
                        switch (phaseNumber) {
                            case 1: // Fase 1 - Overworld (azul/ciano)
                                level.addParticle(
                                    Math.random() > 0.5 ? 
                                        net.minecraft.core.particles.ParticleTypes.END_ROD :
                                        net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                                    player.getX() + offsetX,
                                    player.getY() + offsetY,
                                    player.getZ() + offsetZ,
                                    (Math.random() - 0.5) * 0.05,
                                    Math.random() * 0.05,
                                    (Math.random() - 0.5) * 0.05);
                                break;
                            case 2: // Fase 2 - Nether (vermelho/laranja)
                                level.addParticle(
                                    Math.random() > 0.5 ? 
                                        net.minecraft.core.particles.ParticleTypes.FLAME :
                                        net.minecraft.core.particles.ParticleTypes.LAVA,
                                    player.getX() + offsetX,
                                    player.getY() + offsetY,
                                    player.getZ() + offsetZ,
                                    (Math.random() - 0.5) * 0.05,
                                    Math.random() * 0.05,
                                    (Math.random() - 0.5) * 0.05);
                                break;
                            default: // Fase 3 - End (roxo/rosa)
                                level.addParticle(
                                    Math.random() > 0.5 ? 
                                        net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH :
                                        net.minecraft.core.particles.ParticleTypes.PORTAL,
                                    player.getX() + offsetX,
                                    player.getY() + offsetY,
                                    player.getZ() + offsetZ,
                                    (Math.random() - 0.5) * 0.05,
                                    Math.random() * 0.05,
                                    (Math.random() - 0.5) * 0.05);
                                break;
                        }
                        
                        // Adicionar alguns efeitos sonoros esparsos para melhorar o ambiente
                        if (index % 40 == 0) {
                            level.playSound(null, 
                                player.getX() + offsetX, 
                                player.getY() + offsetY, 
                                player.getZ() + offsetZ,
                                Math.random() > 0.5 ? 
                                    net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_BLAST : 
                                    net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_TWINKLE, 
                                net.minecraft.sounds.SoundSource.AMBIENT, 
                                0.5F, 0.8F + (float)(Math.random() * 0.4));
                        }
                        
                    } catch (Exception e) {
                        // Silenciar exceções de partículas
                    }
                }));
            }
                
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Falha ao iniciar celebração: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
