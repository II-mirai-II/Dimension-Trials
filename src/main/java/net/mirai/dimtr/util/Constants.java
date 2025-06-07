package net.mirai.dimtr.util;

public class Constants {
    public static final String MODID = "dimtr";

    // CORREÇÃO: Nome da chave de dados
    public static final String PROGRESSION_DATA_NAME = "dimtr_progression";

    // Chaves de tradução principais
    public static final String HUD_TITLE = "hud.dimtr.title";
    public static final String PROGRESSION_BOOK_TITLE = "gui.dimtr.progression_book.title";

    // Mensagens do sistema
    public static final String MSG_PHASE1_UNLOCKED_GLOBAL = "message.dimtr.phase1_unlocked_global";
    public static final String MSG_PHASE1_UNLOCKED_GLOBAL_CONFIG_DISABLED = "message.dimtr.phase1_unlocked_global_config_disabled";
    public static final String MSG_PHASE2_UNLOCKED_GLOBAL = "message.dimtr.phase2_unlocked_global";
    public static final String MSG_PHASE2_UNLOCKED_GLOBAL_CONFIG_DISABLED = "message.dimtr.phase2_unlocked_global_config_disabled";

    // CORREÇÃO: Mensagens de bloqueio de portal
    public static final String MSG_NETHER_LOCKED = "message.dimtr.nether_locked";
    public static final String MSG_END_LOCKED = "message.dimtr.end_locked";

    // NOVO: Mensagens de teleporte para spawn
    public static final String MSG_NETHER_LOCKED_TELEPORT = "message.dimtr.nether_locked_teleport";
    public static final String MSG_END_LOCKED_TELEPORT = "message.dimtr.end_locked_teleport";

    // Títulos das fases
    public static final String HUD_PHASE1_TITLE = "hud.dimtr.phase1_title";
    public static final String HUD_PHASE2_TITLE = "hud.dimtr.phase2_title";

    // Objetivos especiais - COM EMOJIS
    public static final String HUD_ELDER_GUARDIAN = "hud.dimtr.elder_guardian";
    public static final String HUD_TOOLTIP_ELDER_GUARDIAN = "hud.dimtr.tooltip.elder_guardian";
    public static final String HUD_RAID_WON = "hud.dimtr.raid_won";
    public static final String HUD_TOOLTIP_RAID_WON = "hud.dimtr.tooltip.raid_won";
    public static final String HUD_TRIAL_VAULT_ADV = "hud.dimtr.trial_vault_adv";
    public static final String HUD_TOOLTIP_TRIAL_VAULT_ADV = "hud.dimtr.tooltip.trial_vault_adv";
    public static final String HUD_WITHER_KILLED = "hud.dimtr.wither_killed";
    public static final String HUD_TOOLTIP_WITHER_KILLED = "hud.dimtr.tooltip.wither_killed";
    public static final String HUD_WARDEN_KILLED = "hud.dimtr.warden_killed";
    public static final String HUD_TOOLTIP_WARDEN_KILLED = "hud.dimtr.tooltip.warden_killed";

    // Seções do HUD - COM EMOJIS
    public static final String HUD_SECTION_SPECIAL_OBJECTIVES = "hud.dimtr.section.special_objectives";
    public static final String HUD_SECTION_MOB_ELIMINATION = "hud.dimtr.section.mob_elimination";
    public static final String HUD_SECTION_NETHER_MOBS = "hud.dimtr.section.nether_mobs";
    public static final String HUD_SECTION_REPEAT_OVERWORLD = "hud.dimtr.section.repeat_overworld";

    // Mob Names - COM EMOJIS
    public static final String HUD_MOB_ZOMBIE = "hud.dimtr.mob.zombie";
    public static final String HUD_MOB_ZOMBIE_VILLAGER = "hud.dimtr.mob.zombie_villager";
    public static final String HUD_MOB_SKELETON = "hud.dimtr.mob.skeleton";
    public static final String HUD_MOB_STRAY = "hud.dimtr.mob.stray";
    public static final String HUD_MOB_HUSK = "hud.dimtr.mob.husk";
    public static final String HUD_MOB_SPIDER = "hud.dimtr.mob.spider";
    public static final String HUD_MOB_CREEPER = "hud.dimtr.mob.creeper";
    public static final String HUD_MOB_DROWNED = "hud.dimtr.mob.drowned";
    public static final String HUD_MOB_ENDERMAN = "hud.dimtr.mob.enderman";
    public static final String HUD_MOB_WITCH = "hud.dimtr.mob.witch";
    public static final String HUD_MOB_PILLAGER = "hud.dimtr.mob.pillager";
    public static final String HUD_MOB_CAPTAIN = "hud.dimtr.mob.captain";
    public static final String HUD_MOB_VINDICATOR = "hud.dimtr.mob.vindicator";
    public static final String HUD_MOB_BOGGED = "hud.dimtr.mob.bogged";
    public static final String HUD_MOB_BREEZE = "hud.dimtr.mob.breeze";

    // NOVO: Mob Names para Ravager e Evoker como Goal Kills
    public static final String HUD_MOB_RAVAGER = "hud.dimtr.mob.ravager";
    public static final String HUD_MOB_EVOKER = "hud.dimtr.mob.evoker";

    // Fase 2 Mobs - COM EMOJIS
    public static final String HUD_MOB_BLAZE = "hud.dimtr.mob.blaze";
    public static final String HUD_MOB_WITHER_SKELETON = "hud.dimtr.mob.wither_skeleton";
    public static final String HUD_MOB_PIGLIN_BRUTE = "hud.dimtr.mob.piglin_brute";
    public static final String HUD_MOB_HOGLIN = "hud.dimtr.mob.hoglin";
    public static final String HUD_MOB_ZOGLIN = "hud.dimtr.mob.zoglin";
    public static final String HUD_MOB_GHAST = "hud.dimtr.mob.ghast";
    public static final String HUD_MOB_ENDERMITE = "hud.dimtr.mob.endermite";
    public static final String HUD_MOB_PIGLIN = "hud.dimtr.mob.piglin";

    // Comandos
    public static final String CMD_RESET_SUCCESS = "cmd.dimtr.reset_success";
    public static final String CMD_PHASE1_COMPLETE = "cmd.dimtr.phase1_complete";
    public static final String CMD_PHASE2_COMPLETE = "cmd.dimtr.phase2_complete";
    public static final String CMD_GOAL_SET = "cmd.dimtr.goal_set";
    public static final String CMD_INVALID_GOAL = "cmd.dimtr.invalid_goal";
    public static final String CMD_MOB_KILLS_SET = "cmd.dimtr.mob_kills_set";
    public static final String CMD_INVALID_MOB = "cmd.dimtr.invalid_mob";
    public static final String CMD_INVALID_BOOLEAN = "cmd.dimtr.invalid_boolean";

    // NOVO: Window System Constants
    public static final String GUI_WINDOW_INSTRUCTIONS = "gui.dimtr.hud.window_instructions";
    public static final String GUI_PAGE_INSTRUCTIONS = "gui.dimtr.hud.page_instructions";
    public static final String GUI_CLOSE_INSTRUCTIONS = "gui.dimtr.hud.close_instructions";
    public static final String GUI_WINDOW_TOGGLE = "gui.dimtr.hud.window_toggle";

    // NOVO: Window Titles
    public static final String WINDOW_PHASE1_MAIN_TITLE = "gui.dimtr.window.phase1_main.title";
    public static final String WINDOW_PHASE1_GOALS_TITLE = "gui.dimtr.window.phase1_goals.title";
    public static final String WINDOW_PHASE2_MAIN_TITLE = "gui.dimtr.window.phase2_main.title";
    public static final String WINDOW_PHASE2_GOALS_TITLE = "gui.dimtr.window.phase2_goals.title";
}