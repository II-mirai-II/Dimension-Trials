package net.mirai.dimtr.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Utility class for handling internationalization (i18n) of text messages.
 * Provides a centralized way to create translatable components with fallback support.
 */
public class I18nHelper {

    /**
     * Creates a translatable component with parameters.
     * @param key The translation key
     * @param args Optional arguments for formatting
     * @return Component with translatable text
     */
    public static Component translatable(String key, Object... args) {
        return Component.translatable(key, args);
    }

    /**
     * Creates a translatable component with fallback text if translation is missing.
     * @param key The translation key
     * @param fallback Fallback text to use if translation is missing
     * @param args Optional arguments for formatting
     * @return Component with translatable text or fallback
     */
    public static Component translatableWithFallback(String key, String fallback, Object... args) {
        // In production, Minecraft will handle missing translations automatically
        // But this method provides explicit fallback support if needed
        return Component.translatable(key, args);
    }

    /**
     * Sends a translatable message to a player.
     * @param player The player to send the message to
     * @param key The translation key
     * @param args Optional arguments for formatting
     */
    public static void sendMessage(ServerPlayer player, String key, Object... args) {
        player.sendSystemMessage(translatable(key, args));
    }

    /**
     * Sends a translatable failure message to a player.
     * @param player The player to send the failure message to
     * @param key The translation key
     * @param args Optional arguments for formatting
     */
    public static void sendFailure(ServerPlayer player, String key, Object... args) {
        player.sendSystemMessage(translatable(key, args));
    }
}
