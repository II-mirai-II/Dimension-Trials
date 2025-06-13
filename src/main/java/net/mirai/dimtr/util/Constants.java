package net.mirai.dimtr.util;

public class Constants {
    // CORREÇÃO: MOD_ID em vez de MODID para consistência
    public static final String MOD_ID = "dimtr";
    public static final String MODID = "dimtr"; // Manter para compatibilidade

    // CORREÇÃO: Nome da chave de dados
    public static final String PROGRESSION_DATA_NAME = "dimtr_progression";

    // Chaves de tradução principais
    public static final String HUD_TITLE = "hud.dimtr.title";
    public static final String PROGRESSION_BOOK_TITLE = "gui.dimtr.progression_book.title";

    // 🎯 NOVO: Mensagens do sistema individual
    public static final String MSG_PHASE1_COMPLETE_INDIVIDUAL = "message.dimtr.phase1_complete_individual";
    public static final String MSG_PHASE2_COMPLETE_INDIVIDUAL = "message.dimtr.phase2_complete_individual";

    // Mensagens globais (mantidas para compatibilidade)
    public static final String MSG_PHASE1_UNLOCKED_GLOBAL = "message.dimtr.phase1_unlocked_global";
    public static final String MSG_PHASE1_UNLOCKED_GLOBAL_CONFIG_DISABLED = "message.dimtr.phase1_unlocked_global_config_disabled";
    public static final String MSG_PHASE2_UNLOCKED_GLOBAL = "message.dimtr.phase2_unlocked_global";
    public static final String MSG_PHASE2_UNLOCKED_GLOBAL_CONFIG_DISABLED = "message.dimtr.phase2_unlocked_global_config_disabled";

    // CORREÇÃO: Mensagens de bloqueio de portal
    public static final String MSG_NETHER_LOCKED = "message.dimtr.nether_locked";
    public static final String MSG_END_LOCKED = "message.dimtr.end_locked";
    public static final String MSG_NETHER_PORTAL_BLOCKED = "message.dimtr.nether_portal_blocked";
    public static final String MSG_END_PORTAL_BLOCKED = "message.dimtr.end_portal_blocked";

    // NOVO: Mensagens de teleporte para spawn
    public static final String MSG_NETHER_LOCKED_TELEPORT = "message.dimtr.nether_locked_teleport";
    public static final String MSG_END_LOCKED_TELEPORT = "message.dimtr.end_locked_teleport";

    // 🎯 NOVO: Mensagens administrativas para comandos individuais
    public static final String MSG_ADMIN_PLAYER_PHASE1_COMPLETE = "message.dimtr.admin.player_phase1_complete";
    public static final String MSG_ADMIN_PLAYER_PHASE2_COMPLETE = "message.dimtr.admin.player_phase2_complete";
    public static final String MSG_ADMIN_PLAYER_PROGRESS_RESET = "message.dimtr.admin.player_progress_reset";
    public static final String MSG_ADMIN_GOAL_SET = "message.dimtr.admin.goal_set";
    public static final String MSG_ADMIN_MOB_KILL_SET = "message.dimtr.admin.mob_kill_set";

    // 🎯 NOVO: Mensagens de notificação para jogadores
    public static final String MSG_PLAYER_PROGRESS_RESET_BY_ADMIN = "message.dimtr.player.progress_reset_by_admin";
    public static final String MSG_PLAYER_PHASE1_COMPLETED_BY_ADMIN = "message.dimtr.player.phase1_completed_by_admin";
    public static final String MSG_PLAYER_PHASE2_COMPLETED_BY_ADMIN = "message.dimtr.player.phase2_completed_by_admin";

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

    // NOVO: Conquista Voluntaire Exile
    public static final String HUD_VOLUNTAIRE_EXILE = "hud.dimtr.voluntaire_exile";
    public static final String HUD_TOOLTIP_VOLUNTAIRE_EXILE = "hud.dimtr.tooltip.voluntaire_exile";

    public static final String HUD_WITHER_KILLED = "hud.dimtr.wither_killed";
    public static final String HUD_TOOLTIP_WITHER_KILLED = "hud.dimtr.tooltip.wither_killed";
    public static final String HUD_WARDEN_KILLED = "hud.dimtr.warden_killed";
    public static final String HUD_TOOLTIP_WARDEN_KILLED = "hud.dimtr.tooltip.warden_killed";

    // Seções do HUD - COM EMOJIS
    public static final String HUD_SECTION_SPECIAL_OBJECTIVES = "hud.dimtr.section.special_objectives";
    public static final String HUD_SECTION_MOB_ELIMINATION = "hud.dimtr.section.mob_elimination";
    public static final String HUD_SECTION_NETHER_MOBS = "hud.dimtr.section.nether_mobs";
    public static final String HUD_SECTION_REPEAT_OVERWORLD = "hud.dimtr.section.repeat_overworld";
    public static final String HUD_SECTION_SPECIAL_MOBS = "hud.dimtr.section.special.mobs";

    // 🎯 NOVO: Indicadores de sistema individual
    public static final String HUD_INDIVIDUAL_PROGRESS = "hud.dimtr.individual_progress";
    public static final String HUD_YOUR_MULTIPLIER = "hud.dimtr.your_multiplier";
    public static final String HUD_NEARBY_AVERAGE_MULTIPLIER = "hud.dimtr.nearby_average_multiplier";

    // Mob Names - COM EMOJIS - Fase 1 (Overworld)
    public static final String HUD_MOB_ZOMBIE = "hud.dimtr.mob.zombie";
    // REMOVIDO: HUD_MOB_ZOMBIE_VILLAGER - não é mais usado
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

    // Goal Kills - Ravager e Evoker
    public static final String HUD_MOB_RAVAGER = "hud.dimtr.mob.ravager";
    public static final String HUD_MOB_EVOKER = "hud.dimtr.mob.evoker";

    // Fase 2 Mobs - COM EMOJIS (Nether/End)
    public static final String HUD_MOB_BLAZE = "hud.dimtr.mob.blaze";
    public static final String HUD_MOB_WITHER_SKELETON = "hud.dimtr.mob.wither_skeleton";
    public static final String HUD_MOB_PIGLIN_BRUTE = "hud.dimtr.mob.piglin_brute";
    public static final String HUD_MOB_HOGLIN = "hud.dimtr.mob.hoglin";
    public static final String HUD_MOB_ZOGLIN = "hud.dimtr.mob.zoglin";
    public static final String HUD_MOB_GHAST = "hud.dimtr.mob.ghast";
    public static final String HUD_MOB_ENDERMITE = "hud.dimtr.mob.endermite"; // Mantido para compatibilidade
    public static final String HUD_MOB_PIGLIN = "hud.dimtr.mob.piglin";

    // 🎯 NOVO: Comandos individuais
    public static final String CMD_INDIVIDUAL_RESET_SUCCESS = "cmd.dimtr.individual_reset_success";
    public static final String CMD_INDIVIDUAL_PHASE1_COMPLETE = "cmd.dimtr.individual_phase1_complete";
    public static final String CMD_INDIVIDUAL_PHASE2_COMPLETE = "cmd.dimtr.individual_phase2_complete";
    public static final String CMD_INDIVIDUAL_GOAL_SET = "cmd.dimtr.individual_goal_set";
    public static final String CMD_INDIVIDUAL_MOB_KILLS_SET = "cmd.dimtr.individual_mob_kills_set";
    public static final String CMD_GLOBAL_STATUS = "cmd.dimtr.global_status";
    public static final String CMD_PLAYER_NOT_FOUND = "cmd.dimtr.player_not_found";
    public static final String CMD_MUST_BE_PLAYER = "cmd.dimtr.must_be_player";

    // Comandos originais (mantidos)
    public static final String CMD_RESET_SUCCESS = "cmd.dimtr.reset_success";
    public static final String CMD_PHASE1_COMPLETE = "cmd.dimtr.phase1_complete";
    public static final String CMD_PHASE2_COMPLETE = "cmd.dimtr.phase2_complete";
    public static final String CMD_GOAL_SET = "cmd.dimtr.goal_set";
    public static final String CMD_INVALID_GOAL = "cmd.dimtr.invalid_goal";
    public static final String CMD_MOB_KILLS_SET = "cmd.dimtr.mob_kills_set";
    public static final String CMD_INVALID_MOB = "cmd.dimtr.invalid_mob";
    public static final String CMD_INVALID_BOOLEAN = "cmd.dimtr.invalid_boolean";

    // Window System Constants
    public static final String GUI_WINDOW_INSTRUCTIONS = "gui.dimtr.hud.window_instructions";
    public static final String GUI_PAGE_INSTRUCTIONS = "gui.dimtr.hud.page_instructions";
    public static final String GUI_CLOSE_INSTRUCTIONS = "gui.dimtr.hud.close_instructions";
    public static final String GUI_WINDOW_TOGGLE = "gui.dimtr.hud.window_toggle";

    // Window Titles
    public static final String WINDOW_PHASE1_MAIN_TITLE = "gui.dimtr.window.phase1_main.title";
    public static final String WINDOW_PHASE1_GOALS_TITLE = "gui.dimtr.window.phase1_goals.title";
    public static final String WINDOW_PHASE2_MAIN_TITLE = "gui.dimtr.window.phase2_main.title";
    public static final String WINDOW_PHASE2_GOALS_TITLE = "gui.dimtr.window.phase2_goals.title";

    // Chaves para o HUD traduzível
    public static final String GUI_NO_CONTENT = "gui.dimtr.no.content";
    public static final String GUI_PAGE_INDICATOR = "gui.dimtr.page.indicator";
    public static final String GUI_PHASE1_DISABLED = "gui.dimtr.phase1.disabled";
    public static final String GUI_PHASE2_DISABLED = "gui.dimtr.phase2.disabled";
    public static final String GUI_PHASE_COMPLETE = "gui.dimtr.phase.complete";
    public static final String GUI_NETHER_UNLOCKED = "gui.dimtr.nether.unlocked";
    public static final String GUI_END_UNLOCKED = "gui.dimtr.end.unlocked";
    public static final String GUI_COMPLETE_OBJECTIVES = "gui.dimtr.complete.objectives";
    public static final String GUI_UNLOCK_NETHER = "gui.dimtr.unlock.nether";
    public static final String GUI_UNLOCK_END = "gui.dimtr.unlock.end";
    public static final String GUI_MOB_PROGRESS = "gui.dimtr.mob.progress";
    public static final String GUI_MOB_ELIMINATION_DISABLED = "gui.dimtr.mob.elimination.disabled";
    public static final String GUI_COMPLETE_PHASE1_FIRST = "gui.dimtr.complete.phase1.first";
    public static final String GUI_SECTION_COMMON_MOBS = "gui.dimtr.section.common.mobs";
    public static final String GUI_SECTION_SPECIAL_MOBS = "gui.dimtr.section.special.mobs";
    public static final String GUI_SECTION_GOAL_KILLS = "gui.dimtr.section.goal.kills";
    public static final String GUI_SECTION_GOAL_KILLS_RESET = "gui.dimtr.section.goal.kills.reset";
    public static final String GUI_SUMMARY = "gui.dimtr.summary";
    public static final String GUI_UNIQUE_CHALLENGES = "gui.dimtr.unique.challenges";
    public static final String GUI_REQUIREMENTS_INCREASED = "gui.dimtr.requirements.increased";
    public static final String GUI_MOBS_COMPLETED = "gui.dimtr.mobs.completed";
    public static final String GUI_TOTAL_KILLS = "gui.dimtr.total.kills";
    public static final String GUI_TYPES_COMPLETED = "gui.dimtr.types.completed";
    public static final String GUI_NETHER_PROGRESS = "gui.dimtr.nether.progress";
    public static final String GUI_OVERWORLD_PROGRESS = "gui.dimtr.overworld.progress";
    public static final String GUI_PHASE2_LOCKED_LINE1 = "gui.dimtr.phase2.locked.line1";
    public static final String GUI_PHASE2_LOCKED_LINE2 = "gui.dimtr.phase2.locked.line2";
    public static final String GUI_PHASE2_LOCKED_LINE3 = "gui.dimtr.phase2.locked.line3";
    public static final String GUI_CHALLENGE_WITHER = "gui.dimtr.challenge.wither";
    public static final String GUI_CHALLENGE_WARDEN = "gui.dimtr.challenge.warden";
    public static final String GUI_CHALLENGE_NETHER = "gui.dimtr.challenge.nether";
    public static final String GUI_CHALLENGE_NEW_MOBS = "gui.dimtr.challenge.new.mobs";

    // Teclas de atalho
    public static final String KEY_OPEN_HUD = "key.dimtr.open_hud";
    public static final String KEY_CATEGORY = "key.categories.dimtr";

    // Item de progressão
    public static final String ITEM_PROGRESSION_BOOK = "item.dimtr.progression_book";

    // Comandos detalhados
    public static final String COMMAND_DIMTR = "commands.dimtr.description";
    public static final String COMMAND_DIMTR_STATUS = "commands.dimtr.status.description";
    public static final String COMMAND_DIMTR_COMPLETE = "commands.dimtr.complete.description";
    public static final String COMMAND_DIMTR_RESET = "commands.dimtr.reset.description";

    // Avancements
    public static final String ADVANCEMENT_PHASE1_COMPLETE = "advancement.dimtr.phase1_complete";
    public static final String ADVANCEMENT_PHASE2_COMPLETE = "advancement.dimtr.phase2_complete";

    // Creative Tab
    public static final String CREATIVE_TAB_TITLE = "itemGroup.dimtr.main";

    // NOVO: Configurações (adicionar constantes que faltavam)
    public static final String CONFIG_ENABLE_PHASE1 = "config.dimtr.server.enablePhase1";
    public static final String CONFIG_ENABLE_PHASE2 = "config.dimtr.server.enablePhase2";
    public static final String CONFIG_ENABLE_MOB_KILLS_PHASE1 = "config.dimtr.server.enableMobKillsPhase1";
    public static final String CONFIG_ENABLE_MOB_KILLS_PHASE2 = "config.dimtr.server.enableMobKillsPhase2";
    public static final String CONFIG_REQ_VOLUNTARY_EXILE = "config.dimtr.server.reqVoluntaryExile";
    public static final String CONFIG_ENABLE_XP_MULTIPLIER = "config.dimtr.server.enableXpMultiplier";
    public static final String CONFIG_ENABLE_MULTIPLIERS = "config.dimtr.server.enableMultipliers";

    // 🎯 NOVO: Configurações de sistema individual
    public static final String CONFIG_PROXIMITY_RADIUS = "config.dimtr.server.proximityRadius";
    public static final String CONFIG_INDIVIDUAL_PROGRESSION = "config.dimtr.server.individualProgression";

    // Tooltips
    public static final String TOOLTIP_PROGRESSION_BOOK = "tooltip.dimtr.progression_book";
    public static final String TOOLTIP_PHASE_LOCKED = "tooltip.dimtr.phase_locked";
    public static final String TOOLTIP_PHASE_UNLOCKED = "tooltip.dimtr.phase_unlocked";

    // Informações de progresso
    public static final String PROGRESS_PHASE1_INCOMPLETE = "progress.dimtr.phase1_incomplete";
    public static final String PROGRESS_PHASE1_COMPLETE = "progress.dimtr.phase1_complete";
    public static final String PROGRESS_PHASE2_INCOMPLETE = "progress.dimtr.phase2_incomplete";
    public static final String PROGRESS_PHASE2_COMPLETE = "progress.dimtr.phase2_complete";

    // Status de portais
    public static final String PORTAL_STATUS_LOCKED = "portal.dimtr.status.locked";
    public static final String PORTAL_STATUS_UNLOCKED = "portal.dimtr.status.unlocked";

    // Validação
    public static final String VALIDATION_INVALID_MOB_TYPE = "validation.dimtr.invalid_mob_type";
    public static final String VALIDATION_INVALID_PHASE = "validation.dimtr.invalid_phase";

    // Constantes específicas para seções especializadas
    public static final String SECTION_GOAL_KILLS = "gui.dimtr.section.goal.kills";
    public static final String SECTION_SPECIAL_MOBS = "gui.dimtr.section.special.mobs";
    public static final String SECTION_COMMON_MOBS = "gui.dimtr.section.common.mobs";
    public static final String SECTION_NETHER_MOBS = "gui.dimtr.section.nether.mobs";

    // NOVO: Constantes do Sistema de Sumário
    public static final String GUI_SUMMARY_TITLE = "gui.dimtr.summary.title";
    public static final String GUI_SUMMARY_SUBTITLE = "gui.dimtr.summary.subtitle";
    public static final String GUI_SUMMARY_GENERAL_STATS = "gui.dimtr.summary.general_stats";
    public static final String GUI_SUMMARY_PHASE1_STATUS = "gui.dimtr.summary.phase1_status";
    public static final String GUI_SUMMARY_PHASE2_STATUS = "gui.dimtr.summary.phase2_status";

    // Descrições das seções
    public static final String GUI_SUMMARY_PHASE1_MAIN_DESC = "gui.dimtr.summary.phase1_main.desc";
    public static final String GUI_SUMMARY_PHASE1_GOALS_DESC = "gui.dimtr.summary.phase1_goals.desc";
    public static final String GUI_SUMMARY_PHASE2_MAIN_DESC = "gui.dimtr.summary.phase2_main.desc";
    public static final String GUI_SUMMARY_PHASE2_GOALS_DESC = "gui.dimtr.summary.phase2_goals.desc";

    // Instruções de navegação
    public static final String GUI_SUMMARY_INSTRUCTIONS_CLICK = "gui.dimtr.summary.instructions.click";
    public static final String GUI_SUMMARY_INSTRUCTIONS_CLOSE = "gui.dimtr.summary.instructions.close";
    public static final String GUI_SECTION_INSTRUCTIONS_NAVIGATE = "gui.dimtr.section.instructions.navigate";
    public static final String GUI_SECTION_INSTRUCTIONS_BACK = "gui.dimtr.section.instructions.back";
    public static final String GUI_BACK_TO_SUMMARY = "gui.dimtr.back_to_summary";

    // 🎯 NOVO: Sistema de proximidade
    public static final String MSG_PROXIMITY_INFO = "message.dimtr.proximity_info";
    public static final String MSG_MULTIPLIER_APPLIED = "message.dimtr.multiplier_applied";
    public static final String GUI_PROXIMITY_PLAYERS = "gui.dimtr.proximity_players";
    public static final String GUI_AVERAGE_MULTIPLIER = "gui.dimtr.average_multiplier";

    // ============================================================================
    // 🎯 SISTEMA DE PARTIES - TRANSLATION KEYS
    // ============================================================================

    // Window title para parties
    public static final String WINDOW_PARTIES_TITLE = "gui.dimtr.window.parties.title";

    // Summary description
    public static final String GUI_SUMMARY_PARTIES_DESC = "gui.dimtr.summary.parties.desc";

    // Parties section - Main content
    public static final String GUI_PARTIES_WELCOME = "gui.dimtr.parties.welcome";
    public static final String GUI_PARTIES_CURRENT_STATUS = "gui.dimtr.parties.current.status";
    public static final String GUI_PARTIES_IN_PARTY = "gui.dimtr.parties.in.party";
    public static final String GUI_PARTIES_NO_PARTY = "gui.dimtr.parties.no.party";

    // Parties section - Members
    public static final String GUI_PARTIES_MEMBERS = "gui.dimtr.parties.members";

    // Parties section - Progress
    public static final String GUI_PARTIES_SHARED_PROGRESS = "gui.dimtr.parties.shared.progress";
    public static final String GUI_PARTIES_SHARED_MOBS = "gui.dimtr.parties.shared.mobs";

    // Parties section - Actions
    public static final String GUI_PARTIES_ACTIONS = "gui.dimtr.parties.actions";
    public static final String GUI_PARTIES_ACTION_CREATE = "gui.dimtr.parties.action.create";
    public static final String GUI_PARTIES_ACTION_JOIN = "gui.dimtr.parties.action.join";
    public static final String GUI_PARTIES_ACTION_LIST = "gui.dimtr.parties.action.list";

    // Parties section - Benefits
    public static final String GUI_PARTIES_BENEFITS = "gui.dimtr.parties.benefits";
    public static final String GUI_PARTIES_BENEFIT_SHARED = "gui.dimtr.parties.benefit.shared";
    public static final String GUI_PARTIES_BENEFIT_MULTIPLIER = "gui.dimtr.parties.benefit.multiplier";

    // Parties section - Commands
    public static final String GUI_PARTIES_COMMANDS = "gui.dimtr.parties.commands";
}