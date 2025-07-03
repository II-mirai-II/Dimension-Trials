package net.mirai.dimtr.util;

import net.mirai.dimtr.DimTrMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.particles.ParticleTypes;

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
        
        BlockPos playerPos = player.blockPosition();
        
        try {
            // Sons de celebração principais
            player.level().playSound(null, playerPos, 
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 
                SoundSource.MASTER, 1.0F, 1.0F);
                
            player.level().playSound(null, playerPos, 
                SoundEvents.PLAYER_LEVELUP, 
                SoundSource.MASTER, 1.0F, 0.75F);
            
            // Criar comandos para gerar os fogos de artifício (usando /summon)
            // Isso garante que teremos os fogos visíveis com as cores certas
            CommandSourceStack source = 
                player.createCommandSourceStack().withPermission(4).withSuppressedOutput();
                
            MinecraftServer server = player.level().getServer();
            if (server == null) return;
            
            // Gerar fogos aleatórios ao redor do jogador
            for (int i = 0; i < 10; i++) {
                final int index = i;
                // Atrasar cada foguete por alguns ticks para criar um efeito sequencial
                server.tell(new net.minecraft.server.TickTask(server.getTickCount() + index * 4, () -> {
                    try {
                        // Posição aleatória ao redor do jogador
                        double offsetX = (Math.random() - 0.5) * 10;
                        double offsetZ = (Math.random() - 0.5) * 10;
                        double posX = player.getX() + offsetX;
                        double posY = player.getY() + 1;
                        double posZ = player.getZ() + offsetZ;
                        
                        // Comando base para criar foguetes
                        String command = "";
                        
                        // Configurar cores com base na fase
                        switch (phaseNumber) {
                            case 1: // Fase 1 - Overworld (azul/ciano)
                                if (Math.random() < 0.5) {
                                    // Tipo de foguete de pequena explosão com cores da fase 1
                                    command = String.format(
                                        "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                        "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:1,Explosions:[" +
                                        "{Type:0,Flicker:1,Trail:1,Colors:[I;2651799,2437522],FadeColors:[I;4312372]}]}}}}",
                                        posX, posY, posZ);
                                } else {
                                    // Tipo de foguete de explosão em formato de estrela com cores da fase 1
                                    command = String.format(
                                        "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                        "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:1,Explosions:[" +
                                        "{Type:1,Flicker:1,Trail:1,Colors:[I;3949738,4159204],FadeColors:[I;43775]}]}}}}",
                                        posX, posY, posZ);
                                }
                                break;
                                
                            case 2: // Fase 2 - Nether (vermelho/laranja)
                                if (Math.random() < 0.5) {
                                    // Tipo de foguete com cores da fase 2
                                    command = String.format(
                                        "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                        "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:1,Explosions:[" +
                                        "{Type:2,Flicker:1,Trail:1,Colors:[I;11743532,14586514],FadeColors:[I;16733525]}]}}}}",
                                        posX, posY, posZ);
                                } else {
                                    // Variação de foguete com cores da fase 2
                                    command = String.format(
                                        "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                        "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:1,Explosions:[" +
                                        "{Type:4,Flicker:1,Trail:1,Colors:[I;16711680,16755200],FadeColors:[I;16733440]}]}}}}",
                                        posX, posY, posZ);
                                }
                                break;
                                
                            default: // Fase 3 - End (roxo/rosa)
                                if (Math.random() < 0.5) {
                                    // Tipo de foguete com cores da fase 3
                                    command = String.format(
                                        "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                        "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:1,Explosions:[" +
                                        "{Type:3,Flicker:1,Trail:1,Colors:[I;8991416,13061821],FadeColors:[I;8388736]}]}}}}",
                                        posX, posY, posZ);
                                } else {
                                    // Variação de foguete com cores da fase 3
                                    command = String.format(
                                        "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                        "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:1,Explosions:[" +
                                        "{Type:4,Flicker:1,Trail:1,Colors:[I;11141290,16711935],FadeColors:[I;8323327]}]}}}}",
                                        posX, posY, posZ);
                                }
                                break;
                        }
                        
                        // Executar o comando para criar o foguete
                        server.getCommands().performPrefixedCommand(source, command);
                        
                        // Som de lançamento
                        player.level().playSound(null, posX, posY, posZ,
                            SoundEvents.FIREWORK_ROCKET_LAUNCH,
                            SoundSource.MASTER,
                            0.8F, 0.8F + (float)(Math.random() * 0.4F));
                            
                        // Adicionar partículas extras
                        for (int j = 0; j < 15; j++) {
                            double px = posX + (Math.random() - 0.5) * 2;
                            double py = posY + Math.random() * 2;
                            double pz = posZ + (Math.random() - 0.5) * 2;
                            
                            // Partículas de acordo com a fase
                            if (phaseNumber == 1) {
                                player.level().addParticle(
                                    ParticleTypes.END_ROD,
                                    px, py, pz,
                                    (Math.random() - 0.5) * 0.1,
                                    Math.random() * 0.2,
                                    (Math.random() - 0.5) * 0.1);
                            } else if (phaseNumber == 2) {
                                player.level().addParticle(
                                    ParticleTypes.FLAME,
                                    px, py, pz,
                                    (Math.random() - 0.5) * 0.1,
                                    Math.random() * 0.2,
                                    (Math.random() - 0.5) * 0.1);
                            } else {
                                player.level().addParticle(
                                    ParticleTypes.DRAGON_BREATH,
                                    px, py, pz,
                                    (Math.random() - 0.5) * 0.1,
                                    Math.random() * 0.2,
                                    (Math.random() - 0.5) * 0.1);
                            }
                        }
                    } catch (Exception e) {
                        DimTrMod.LOGGER.error("Erro ao lançar foguete: {}", e.getMessage());
                    }
                }));
            }
            
            // Lançar um foguete grande especial no centro (após pequeno atraso)
            server.tell(new net.minecraft.server.TickTask(server.getTickCount() + 25, () -> {
                try {
                    String specialCommand = "";
                    
                    // Foguete grande com múltiplas explosões baseado na fase
                    switch (phaseNumber) {
                        case 1: // Fase 1 - Grande foguete azul/ciano
                            specialCommand = String.format(
                                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:2,Explosions:[" +
                                "{Type:1,Flicker:1,Trail:1,Colors:[I;3949738,4159204],FadeColors:[I;43775]}," +
                                "{Type:0,Flicker:1,Trail:1,Colors:[I;2651799,2437522],FadeColors:[I;4312372]}," +
                                "{Type:4,Flicker:1,Trail:1,Colors:[I;16777215],FadeColors:[I;43690]}]}}}}",
                                player.getX(), player.getY() + 1, player.getZ());
                            break;
                        case 2: // Fase 2 - Grande foguete vermelho/laranja
                            specialCommand = String.format(
                                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:2,Explosions:[" +
                                "{Type:1,Flicker:1,Trail:1,Colors:[I;11743532,16733525],FadeColors:[I;15566613]}," +
                                "{Type:2,Flicker:1,Trail:1,Colors:[I;16711680,16755200],FadeColors:[I;16733440]}," +
                                "{Type:3,Flicker:1,Trail:1,Colors:[I;16747520,16762880],FadeColors:[I;16737095]}]}}}}",
                                player.getX(), player.getY() + 1, player.getZ());
                            break;
                        default: // Fase 3 - Grande foguete roxo/rosa
                            specialCommand = String.format(
                                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                                "{FireworksItem:{id:\"minecraft:firework_rocket\",Count:1,tag:{Fireworks:{Flight:2,Explosions:[" +
                                "{Type:1,Flicker:1,Trail:1,Colors:[I;8991416,13061821],FadeColors:[I;8388736]}," +
                                "{Type:3,Flicker:1,Trail:1,Colors:[I;11141290,16711935],FadeColors:[I;8323327]}," +
                                "{Type:4,Flicker:1,Trail:1,Colors:[I;12801229,13369599],FadeColors:[I;8339378]}]}}}}",
                                player.getX(), player.getY() + 1, player.getZ());
                            break;
                    }
                    
                    // Executar comando para o foguete especial
                    server.getCommands().performPrefixedCommand(source, specialCommand);
                    
                    // Som de lançamento especial
                    player.level().playSound(null, 
                        player.getX(), player.getY() + 1, player.getZ(),
                        SoundEvents.FIREWORK_ROCKET_LARGE_BLAST,
                        SoundSource.MASTER,
                        1.2F, 0.7F);
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("Erro ao lançar foguete especial: {}", e.getMessage());
                }
            }));
                
        } catch (Exception e) {
            DimTrMod.LOGGER.error("Falha ao iniciar celebração: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
