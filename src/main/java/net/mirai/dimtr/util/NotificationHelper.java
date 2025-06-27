package net.mirai.dimtr.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

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
}
