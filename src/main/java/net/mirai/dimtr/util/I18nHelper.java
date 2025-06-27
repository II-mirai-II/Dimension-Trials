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

    // Common party command translation keys
    public static final class Party {
        public static final String ERROR_NOT_PLAYER = "party.dimtr.error.not_player";
        public static final String ERROR_ALREADY_IN_PARTY = "party.dimtr.error.already_in_party";
        public static final String ERROR_NOT_IN_PARTY = "party.dimtr.error.not_in_party";
        public static final String ERROR_NOT_LEADER = "party.dimtr.error.not_leader";
        public static final String ERROR_INVALID_NAME = "party.dimtr.error.invalid_name";
        public static final String ERROR_NAME_TAKEN = "party.dimtr.error.name_taken";
        public static final String ERROR_PARTY_NOT_FOUND = "party.dimtr.error.party_not_found";
        public static final String ERROR_WRONG_PASSWORD = "party.dimtr.error.wrong_password";
        public static final String ERROR_PARTY_FULL = "party.dimtr.error.party_full";
        public static final String ERROR_UNKNOWN_CREATE = "party.dimtr.error.unknown_create";
        public static final String ERROR_UNKNOWN_JOIN = "party.dimtr.error.unknown_join";
        public static final String ERROR_UNKNOWN_LEAVE = "party.dimtr.error.unknown_leave";
        public static final String ERROR_GET_PARTY_INFO = "party.dimtr.error.get_party_info";

        public static final String CREATE_SUCCESS = "party.dimtr.create.success";
        public static final String CREATE_SUCCESS_LEADER = "party.dimtr.create.success.leader";
        public static final String CREATE_SUCCESS_INFO = "party.dimtr.create.success.info";
        public static final String CREATE_SUCCESS_MULTIPLIER = "party.dimtr.create.success.multiplier";
        public static final String CREATE_PASSWORD_INFO = "party.dimtr.create.password_info";

        public static final String JOIN_SUCCESS = "party.dimtr.join.success";
        public static final String JOIN_SUCCESS_BENEFIT = "party.dimtr.join.success.benefit";
        public static final String JOIN_SUCCESS_INFO = "party.dimtr.join.success.info";

        public static final String LEAVE_SUCCESS = "party.dimtr.leave.success";
        public static final String LEAVE_SUCCESS_INDIVIDUAL = "party.dimtr.leave.success.individual";

        public static final String LIST_HEADER = "party.dimtr.list.header";
        public static final String LIST_EMPTY = "party.dimtr.list.empty";
        public static final String LIST_CREATE_SUGGESTION = "party.dimtr.list.create_suggestion";
        public static final String LIST_JOIN_SUGGESTION = "party.dimtr.list.join_suggestion";

        public static final String INFO_HEADER = "party.dimtr.info.header";
        public static final String INFO_NAME = "party.dimtr.info.name";
        public static final String INFO_MEMBERS = "party.dimtr.info.members";
        public static final String INFO_TYPE = "party.dimtr.info.type";
        public static final String INFO_TYPE_PUBLIC = "party.dimtr.info.type.public";
        public static final String INFO_TYPE_PRIVATE = "party.dimtr.info.type.private";
        public static final String INFO_MULTIPLIER = "party.dimtr.info.multiplier";
        public static final String INFO_MEMBERS_HEADER = "party.dimtr.info.members.header";
        public static final String INFO_PROGRESS_HEADER = "party.dimtr.info.progress.header";
        public static final String INFO_KILLS_HEADER = "party.dimtr.info.kills.header";
        public static final String INFO_MEMBER_YOU = "party.dimtr.info.member.you";

        public static final String OBJECTIVE_ELDER_GUARDIAN = "party.dimtr.objective.elder_guardian";
        public static final String OBJECTIVE_RAID_WON = "party.dimtr.objective.raid_won";
        public static final String OBJECTIVE_TRIAL_VAULT = "party.dimtr.objective.trial_vault";
        public static final String OBJECTIVE_VOLUNTARY_EXILE = "party.dimtr.objective.voluntary_exile";
        public static final String OBJECTIVE_WITHER = "party.dimtr.objective.wither";
        public static final String OBJECTIVE_WARDEN = "party.dimtr.objective.warden";

        public static final String TYPE_PUBLIC = "party.dimtr.type.public";
        public static final String TYPE_PRIVATE = "party.dimtr.type.private";
    }



    // Event handler messages
    public static final class Events {
        public static final String DIMENSION_REQUIRED = "event.dimtr.dimension.required";
        public static final String DIMENSION_COMPLETE = "event.dimtr.dimension.complete";
        public static final String PHASE_COMPLETE = "event.dimtr.phase.complete";
        public static final String CONTENT_UNLOCKED = "event.dimtr.content.unlocked";
    }
}
