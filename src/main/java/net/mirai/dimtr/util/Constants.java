package net.mirai.dimtr.util;

public class Constants {
    // CORREÇÃO: MOD_ID em vez de MODID para consistência
    public static final String MOD_ID = "dimtr";
    public static final String MODID = "dimtr"; // Manter para compatibilidade

    // CORREÇÃO: Nome da chave de dados
    public static final String PROGRESSION_DATA_NAME = "dimtr_progression";

    // Chaves de tradução principais
    public static final String HUD_TITLE = "hud.dimtr.title";

    // ============================================================================
    // 🎯 HUD SYSTEM - TRANSLATION KEYS
    // ============================================================================
    
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

    // 🎯 NOVO: Mensagens do sistema individual
    public static final String MSG_PHASE1_COMPLETE_INDIVIDUAL = "message.dimtr.phase1_complete_individual";
    public static final String MSG_PHASE2_COMPLETE_INDIVIDUAL = "message.dimtr.phase2_complete_individual";

    // Mensagens globais (mantidas para compatibilidade)
    public static final String MSG_PHASE1_UNLOCKED_GLOBAL = "message.dimtr.phase1_unlocked_global";
    public static final String MSG_PHASE1_UNLOCKED_GLOBAL_CONFIG_DISABLED = "message.dimtr.phase1_unlocked_global_config_disabled";
    public static final String MSG_PHASE2_UNLOCKED_GLOBAL = "message.dimtr.phase2_unlocked_global";
    public static final String MSG_PHASE2_UNLOCKED_GLOBAL_CONFIG_DISABLED = "message.dimtr.phase2_unlocked_global_config_disabled";

    // 🎯 NOVO: Constantes de configuração
    public static final double DEFAULT_PROXIMITY_RADIUS = 48.0;
    public static final int DEFAULT_PORTAL_SOUND_COOLDOWN_TICKS = 40;
    public static final double DEFAULT_PHASE2_OVERWORLD_MULTIPLIER = 1.25;
    
    // 🎯 NOVO: Constantes de sincronização
    public static final int DEFAULT_SYNC_INTERVAL_TICKS = 100;
    public static final int DEFAULT_MAX_PARTY_SIZE = 4;
    public static final double DEFAULT_PARTY_PROGRESSION_MULTIPLIER = 0.75;

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

    // ============================================================================
    // 🎯 DIMTR COMMANDS - TRANSLATION KEYS
    // ============================================================================
    
    // Admin command success messages
    public static final String CMD_ADMIN_PHASE1_COMPLETE = "cmd.dimtr.admin.phase1.complete";
    public static final String CMD_ADMIN_PHASE2_COMPLETE = "cmd.dimtr.admin.phase2.complete";
    public static final String CMD_ADMIN_RESET_SUCCESS = "cmd.dimtr.admin.reset.success";
    public static final String CMD_ADMIN_GOAL_SET = "cmd.dimtr.admin.goal.set";
    public static final String CMD_ADMIN_MOB_KILLS_SET = "cmd.dimtr.admin.mob.kills.set";
    
    // Admin command error messages
    public static final String CMD_ADMIN_INVALID_GOAL = "cmd.dimtr.admin.invalid.goal";
    public static final String CMD_ADMIN_INVALID_MOB = "cmd.dimtr.admin.invalid.mob";
    public static final String CMD_ADMIN_PHASE1_REQUIRED = "cmd.dimtr.admin.phase1.required";
    
    // Player status display
    public static final String CMD_PLAYER_STATUS_HEADER = "cmd.dimtr.player.status.header";
    public static final String CMD_PLAYER_PHASE1_STATUS = "cmd.dimtr.player.phase1.status";
    public static final String CMD_PLAYER_PHASE2_STATUS = "cmd.dimtr.player.phase2.status";
    public static final String CMD_PLAYER_MULTIPLIER = "cmd.dimtr.player.multiplier";
    public static final String CMD_PLAYER_OBJECTIVES_HEADER = "cmd.dimtr.player.objectives.header";
    public static final String CMD_PLAYER_COUNTERS_HEADER = "cmd.dimtr.player.counters.header";
    public static final String CMD_PLAYER_MOB_KILL_ENTRY = "cmd.dimtr.player.mob.kill.entry";
    
    // Sync command messages
    public static final String CMD_SYNC_SUCCESS = "cmd.dimtr.sync.success";
    public static final String CMD_SYNC_INFO = "cmd.dimtr.sync.info";
    public static final String CMD_SYNC_FAILURE = "cmd.dimtr.sync.failure";
    
    // Debug command messages
    public static final String CMD_DEBUG_PAYLOAD_HEADER = "cmd.dimtr.debug.payload.header";
    public static final String CMD_DEBUG_REQUIREMENTS = "cmd.dimtr.debug.requirements";
    public static final String CMD_DEBUG_PHASE_STATUS = "cmd.dimtr.debug.phase.status";

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

    // 🎯 CONSTANTES PARA TEXTOS DINÂMICOS NA SEÇÃO PARTIES
    public static final String GUI_PARTIES_PARTY_TYPE = "gui.dimtr.parties.party.type";
    public static final String GUI_PARTIES_TYPE_PUBLIC = "gui.dimtr.parties.type.public";
    public static final String GUI_PARTIES_TYPE_PRIVATE = "gui.dimtr.parties.type.private";
    public static final String GUI_PARTIES_MEMBER_COUNT = "gui.dimtr.parties.member.count";
    public static final String GUI_PARTIES_MULTIPLIER = "gui.dimtr.parties.multiplier";
    public static final String GUI_PARTIES_REQUIREMENT_REDUCTION = "gui.dimtr.parties.requirement.reduction";
    public static final String GUI_PARTIES_YOU_INDICATOR = "gui.dimtr.parties.you.indicator";
    
    // Progresso compartilhado específico
    public static final String GUI_PARTIES_SHARED_ELDER_GUARDIAN = "gui.dimtr.parties.shared.elder_guardian";
    public static final String GUI_PARTIES_SHARED_RAID = "gui.dimtr.parties.shared.raid";
    public static final String GUI_PARTIES_SHARED_WITHER = "gui.dimtr.parties.shared.wither";
    public static final String GUI_PARTIES_SHARED_WARDEN = "gui.dimtr.parties.shared.warden";
    
    // Informações sobre transferência de progresso
    public static final String GUI_PARTIES_MAX_MEMBERS = "gui.dimtr.parties.max.members";
    public static final String GUI_PARTIES_PROGRESS_TRANSFERRED_IN = "gui.dimtr.parties.progress.transferred.in";
    public static final String GUI_PARTIES_PROGRESS_PRESERVED_OUT = "gui.dimtr.parties.progress.preserved.out";
    public static final String GUI_PARTIES_PROGRESS_WILL_TRANSFER = "gui.dimtr.parties.progress.will.transfer";
    public static final String GUI_PARTIES_PROGRESS_WILL_PRESERVE = "gui.dimtr.parties.progress.will.preserve";
    
    // Comandos específicos
    public static final String GUI_PARTIES_CMD_LEAVE = "gui.dimtr.parties.cmd.leave";
    public static final String GUI_PARTIES_CMD_INFO = "gui.dimtr.parties.cmd.info";
    public static final String GUI_PARTIES_CMD_KICK = "gui.dimtr.parties.cmd.kick";
    public static final String GUI_PARTIES_CMD_PROMOTE = "gui.dimtr.parties.cmd.promote";

    // ============================================================================
    // 🎯 PARTY COMMANDS - TRANSLATION KEYS (FALTAVAM)
    // ============================================================================

    // Party command messages - Success
    public static final String PARTY_CREATE_SUCCESS = "party.dimtr.create.success";
    public static final String PARTY_CREATE_SUCCESS_LEADER = "party.dimtr.create.success.leader";
    public static final String PARTY_CREATE_SUCCESS_INFO = "party.dimtr.create.success.info";
    public static final String PARTY_CREATE_SUCCESS_MULTIPLIER = "party.dimtr.create.success.multiplier";
    public static final String PARTY_JOIN_SUCCESS = "party.dimtr.join.success";
    public static final String PARTY_JOIN_SUCCESS_BENEFIT = "party.dimtr.join.success.benefit";
    public static final String PARTY_JOIN_SUCCESS_INFO = "party.dimtr.join.success.info";
    public static final String PARTY_LEAVE_SUCCESS = "party.dimtr.leave.success";
    public static final String PARTY_LEAVE_SUCCESS_INDIVIDUAL = "party.dimtr.leave.success.individual";
    public static final String PARTY_KICK_SUCCESS = "party.dimtr.kick.success";
    public static final String PARTY_KICK_NOTIFICATION = "party.dimtr.kick.notification";
    public static final String PARTY_PROMOTE_SUCCESS = "party.dimtr.promote.success";
    public static final String PARTY_PROMOTE_NEW_LEADER = "party.dimtr.promote.new_leader";
    public static final String PARTY_PROMOTE_OLD_LEADER = "party.dimtr.promote.old_leader";
    public static final String PARTY_PROMOTE_NOTIFICATION = "party.dimtr.promote.notification";
    public static final String PARTY_DISBAND_SUCCESS = "party.dimtr.disband.success";
    public static final String PARTY_DISBAND_NOTIFICATION = "party.dimtr.disband.notification";
    public static final String PARTY_INVITE_SUCCESS = "party.dimtr.invite.success";
    public static final String PARTY_INVITE_NOTIFICATION = "party.dimtr.invite.notification";
    public static final String PARTY_INVITE_NOTIFICATION_PUBLIC = "party.dimtr.invite.notification.public";
    public static final String PARTY_INVITE_NOTIFICATION_PRIVATE = "party.dimtr.invite.notification.private";

    // Party command messages - Errors
    public static final String PARTY_ERROR_NOT_PLAYER = "party.dimtr.error.not_player";
    public static final String PARTY_ERROR_ALREADY_IN_PARTY = "party.dimtr.error.already_in_party";
    public static final String PARTY_ERROR_NOT_IN_PARTY = "party.dimtr.error.not_in_party";
    public static final String PARTY_ERROR_NOT_LEADER = "party.dimtr.error.not_leader";
    public static final String PARTY_ERROR_INVALID_NAME = "party.dimtr.error.invalid_name";
    public static final String PARTY_ERROR_NAME_TAKEN = "party.dimtr.error.name_taken";
    public static final String PARTY_ERROR_PARTY_NOT_FOUND = "party.dimtr.error.party_not_found";
    public static final String PARTY_ERROR_WRONG_PASSWORD = "party.dimtr.error.wrong_password";
    public static final String PARTY_ERROR_PARTY_FULL = "party.dimtr.error.party_full";
    public static final String PARTY_ERROR_PLAYER_NOT_IN_PARTY = "party.dimtr.error.player_not_in_party";
    public static final String PARTY_ERROR_CANNOT_KICK_SELF = "party.dimtr.error.cannot_kick_self";
    public static final String PARTY_ERROR_ALREADY_LEADER = "party.dimtr.error.already_leader";
    public static final String PARTY_ERROR_CANNOT_PROMOTE_SELF = "party.dimtr.error.cannot_promote_self";
    public static final String PARTY_ERROR_PARTY_FULL_INVITE = "party.dimtr.error.party_full_invite";
    public static final String PARTY_ERROR_PLAYER_ALREADY_IN_PARTY = "party.dimtr.error.player_already_in_party";
    public static final String PARTY_ERROR_GET_PARTY_INFO = "party.dimtr.error.get_party_info";
    public static final String PARTY_ERROR_UNKNOWN_CREATE = "party.dimtr.error.unknown_create";
    public static final String PARTY_ERROR_UNKNOWN_JOIN = "party.dimtr.error.unknown_join";
    public static final String PARTY_ERROR_UNKNOWN_LEAVE = "party.dimtr.error.unknown_leave";

    // Party info display
    public static final String PARTY_INFO_HEADER = "party.dimtr.info.header";
    public static final String PARTY_INFO_NAME = "party.dimtr.info.name";
    public static final String PARTY_INFO_MEMBERS = "party.dimtr.info.members";
    public static final String PARTY_INFO_TYPE = "party.dimtr.info.type";
    public static final String PARTY_INFO_TYPE_PUBLIC = "party.dimtr.info.type.public";
    public static final String PARTY_INFO_TYPE_PRIVATE = "party.dimtr.info.type.private";
    public static final String PARTY_INFO_MULTIPLIER = "party.dimtr.info.multiplier";
    public static final String PARTY_INFO_MEMBERS_HEADER = "party.dimtr.info.members.header";
    public static final String PARTY_INFO_PROGRESS_HEADER = "party.dimtr.info.progress.header";
    public static final String PARTY_INFO_KILLS_HEADER = "party.dimtr.info.kills.header";
    public static final String PARTY_INFO_KILL_ENTRY = "party.dimtr.info.kill.entry";
    public static final String PARTY_INFO_COMMANDS_HEADER = "party.dimtr.info.commands.header";
    public static final String PARTY_INFO_LEADER_COMMANDS = "party.dimtr.info.leader.commands";
    public static final String PARTY_INFO_LEADER_KICK = "party.dimtr.info.leader.kick";
    public static final String PARTY_INFO_LEADER_PROMOTE = "party.dimtr.info.leader.promote";
    public static final String PARTY_INFO_LEADER_DISBAND = "party.dimtr.info.leader.disband";
    public static final String PARTY_INFO_MEMBER_COMMANDS = "party.dimtr.info.member.commands";
    public static final String PARTY_INFO_MEMBER_LEAVE = "party.dimtr.info.member.leave";
    public static final String PARTY_INFO_MEMBER_YOU = "party.dimtr.info.member.you";

    // Additional party constants for missing messages
    public static final String PARTY_LIST_EMPTY = "party.dimtr.list.empty";
    public static final String PARTY_LIST_EMPTY_TIP = "party.dimtr.list.empty.tip";
    public static final String PARTY_LIST_HEADER = "party.dimtr.list.header";
    public static final String PARTY_LIST_JOIN_TIP = "party.dimtr.list.join.tip";
    public static final String PARTY_LIST_ENTRY = "party.dimtr.list.entry";
    
    public static final String PARTY_INVITE_RECEIVED = "party.dimtr.invite.received";
    public static final String PARTY_INVITE_JOIN_PUBLIC = "party.dimtr.invite.join.public";
    public static final String PARTY_INVITE_JOIN_PRIVATE = "party.dimtr.invite.join.private";
    
    public static final String PARTY_PROGRESS_ELDER_GUARDIAN = "party.dimtr.progress.elder_guardian";
    public static final String PARTY_PROGRESS_RAID_WON = "party.dimtr.progress.raid_won";
    public static final String PARTY_PROGRESS_TRIAL_VAULT = "party.dimtr.progress.trial_vault";
    public static final String PARTY_PROGRESS_VOLUNTARY_EXILE = "party.dimtr.progress.voluntary_exile";
    public static final String PARTY_PROGRESS_WITHER_KILLED = "party.dimtr.progress.wither_killed";
    public static final String PARTY_PROGRESS_WARDEN_KILLED = "party.dimtr.progress.warden_killed";
    
    public static final String PARTY_JOIN_INFO_COMMAND_TIP = "party.dimtr.join.info.command.tip";

    // Error constants with specific formatting
    public static final String PARTY_ERROR_ALREADY_IN_PARTY_LEAVE_FIRST = "party.dimtr.error.already_in_party_leave_first";
    public static final String PARTY_ERROR_PARTY_NOT_FOUND_FORMAT = "party.dimtr.error.party_not_found_format";
    
    // Member display constants
    public static final String PARTY_MEMBER_OFFLINE = "party.dimtr.member.offline";
    public static final String PARTY_MEMBER_YOU_SUFFIX = "party.dimtr.member.you.suffix";
    public static final String PARTY_MEMBER_LEADER_PREFIX = "party.dimtr.member.leader.prefix";
    public static final String PARTY_MEMBER_REGULAR_PREFIX = "party.dimtr.member.regular.prefix";

    // Event handler constants
    public static final String EVENT_DIMENSION_REQUIRED = "event.dimtr.dimension.required";
    public static final String EVENT_DIMENSION_COMPLETE = "event.dimtr.dimension.complete";
    public static final String EVENT_PHASE_COMPLETE = "event.dimtr.phase.complete";
    public static final String EVENT_CONTENT_UNLOCKED = "event.dimtr.content.unlocked";

    // ============================================================================
    // 🎯 CONFIGURATION FILES
    // ============================================================================
    public static final String CONFIG_SERVER_FILE_SUFFIX = "-server.toml";
    public static final String CONFIG_CLIENT_FILE_SUFFIX = "-client.toml";

    // ============================================================================
    // 🎯 TOOLTIP HELPER STRINGS
    // ============================================================================
    public static final String TOOLTIP_STATUS_PREFIX = "Status: ";
    public static final String TOOLTIP_PROGRESS_PREFIX = "Progress: ";
    public static final String TOOLTIP_REMAINING_PREFIX = "Remaining: ";
    public static final String TOOLTIP_MEMBERS_PREFIX = "Members: ";
    public static final String TOOLTIP_TYPE_PREFIX = "Type: ";
    public static final String TOOLTIP_MULTIPLIER_PREFIX = "Multiplier: ";
    public static final String TOOLTIP_BENEFITS_HEADER = "Benefits:";
    public static final String TOOLTIP_SHARED_PROGRESSION = "• Shared progression";
    public static final String TOOLTIP_SOCIAL_GAMEPLAY = "• Social gameplay";
    public static final String TOOLTIP_EXAMPLE_HEADER = "Example:";
    public static final String TOOLTIP_SEPARATOR = "━━━━━━━━━━━━━━━━━━━━";
    
    // Status icons and prefixes
    public static final String ICON_COMPLETED = "✅ ";
    public static final String ICON_PENDING = "⏳ ";
    public static final String ICON_COMBAT = "⚔ ";
    public static final String ICON_TARGET = "🎯 ";
    public static final String ICON_PARTY = "👥 ";
    public static final String ICON_HELP = "💡 ";
    public static final String ICON_ERROR = "❌ ";
    public static final String ICON_INFO = "📋 ";
    public static final String ICON_SETTINGS = "⚙ ";
    public static final String ICON_MULTIPLIER = "⚡ ";
    public static final String ICON_LOCK = "🔒 ";
    public static final String ICON_NAME = "📛 ";

    // ============================================================================
    // 🎯 COMMAND MESSAGES
    // ============================================================================
    // Error messages
    public static final String CMD_ERROR_NOT_PLAYER = "cmd.dimtr.error.not_player";
    public static final String CMD_ERROR_NO_PARTY = "cmd.dimtr.error.no_party";
    public static final String CMD_ERROR_PARTY_INFO = "cmd.dimtr.error.party_info";
    
    // Party info messages
    public static final String CMD_PARTY_INFO_HEADER = "cmd.dimtr.party.info.header";
    public static final String CMD_PARTY_INFO_NAME = "cmd.dimtr.party.info.name";
    public static final String CMD_PARTY_INFO_MEMBERS = "cmd.dimtr.party.info.members";
    public static final String CMD_PARTY_INFO_TYPE = "cmd.dimtr.party.info.type";
    public static final String CMD_PARTY_INFO_TYPE_PUBLIC = "cmd.dimtr.party.info.type.public";
    public static final String CMD_PARTY_INFO_TYPE_PRIVATE = "cmd.dimtr.party.info.type.private";
    public static final String CMD_PARTY_INFO_MULTIPLIER = "cmd.dimtr.party.info.multiplier";
    public static final String CMD_PARTY_INFO_MEMBERS_HEADER = "cmd.dimtr.party.info.members.header";
    public static final String CMD_PARTY_INFO_SHARED_PROGRESS = "cmd.dimtr.party.info.shared.progress";
    
    // Party list messages  
    public static final String CMD_PARTY_LIST_EMPTY = "cmd.dimtr.party.list.empty";
    public static final String CMD_PARTY_LIST_CREATE_HINT = "cmd.dimtr.party.list.create.hint";
    public static final String CMD_PARTY_LIST_HEADER = "cmd.dimtr.party.list.header";
    public static final String CMD_PARTY_LIST_JOIN_HINT = "cmd.dimtr.party.list.join.hint";
    
    // Status messages
    public static final String CMD_STATUS_PHASES = "cmd.dimtr.status.phases";
    public static final String CMD_STATUS_GLOBAL_HEADER = "cmd.dimtr.status.global.header";
    
    // Objectives status
    public static final String CMD_OBJECTIVE_ELDER_GUARDIAN = "cmd.dimtr.objective.elder_guardian";
    public static final String CMD_OBJECTIVE_RAID_WON = "cmd.dimtr.objective.raid_won";
    public static final String CMD_OBJECTIVE_TRIAL_VAULT = "cmd.dimtr.objective.trial_vault";
    public static final String CMD_OBJECTIVE_VOLUNTARY_EXILE = "cmd.dimtr.objective.voluntary_exile";
    public static final String CMD_OBJECTIVE_WITHER = "cmd.dimtr.objective.wither";
    public static final String CMD_OBJECTIVE_WARDEN = "cmd.dimtr.objective.warden";

    // ============================================================================
    // 🎯 GUI SECTION STRINGS
    // ============================================================================
    public static final String GUI_CUSTOM_PHASES_WELCOME = "gui.dimtr.custom_phases.welcome";
    public static final String GUI_CUSTOM_PHASES_NO_PHASES = "gui.dimtr.custom_phases.no_phases";
    public static final String GUI_CUSTOM_PHASES_AVAILABLE = "gui.dimtr.custom_phases.available";
    
    // Party display strings
    public static final String GUI_PARTY_NAME_PREFIX = "gui.dimtr.party.name.prefix";
    public static final String GUI_CUSTOM_PHASES_SHARED = "gui.dimtr.custom_phases.shared";
    
    // Custom phases section strings
    public static final String GUI_CUSTOM_PHASES_TITLE = "gui.dimtr.custom_phases.title";
    public static final String GUI_CUSTOM_PHASES_DESCRIPTION = "gui.dimtr.custom_phases.description";
    public static final String GUI_CUSTOM_PHASES_CONFIG_INFO = "gui.dimtr.custom_phases.config_info";
    public static final String GUI_CUSTOM_PHASES_CONFIG_PATH = "gui.dimtr.custom_phases.config_path";
    public static final String GUI_CUSTOM_PHASES_MOB_REQUIREMENTS = "gui.dimtr.custom_phases.mob_requirements";
    public static final String GUI_CUSTOM_PHASES_OBJECTIVE_REQUIREMENTS = "gui.dimtr.custom_phases.objective_requirements";
    public static final String GUI_CUSTOM_PHASES_ADVANCEMENT_REQUIREMENTS = "gui.dimtr.custom_phases.advancement_requirements";
    public static final String GUI_CUSTOM_PHASES_ITEM_REQUIREMENTS = "gui.dimtr.custom_phases.item_requirements";

    // ============================================================================
    // 🎯 PARTY COMMAND EXAMPLES - TRANSLATION KEYS
    // ============================================================================
    
    // Party command examples for GUI
    public static final String GUI_PARTIES_CMD_CREATE_EXAMPLE = "gui.dimtr.parties.cmd.create.example";
    public static final String GUI_PARTIES_CMD_JOIN_EXAMPLE = "gui.dimtr.parties.cmd.join.example";
    public static final String GUI_PARTIES_CMD_LIST_EXAMPLE = "gui.dimtr.parties.cmd.list.example";
    
    // Party requirement system
    public static final String GUI_PARTIES_REQUIREMENT_MULTIPLIER = "gui.dimtr.parties.requirement.multiplier";

    // ============================================================================
    // 🎯 TOOLTIP STATUS STRINGS - TRANSLATION KEYS
    // ============================================================================
    
    // Status indicators for tooltips
    public static final String TOOLTIP_STATUS_COMPLETED = "tooltip.dimtr.status.completed";
    public static final String TOOLTIP_STATUS_IN_PROGRESS = "tooltip.dimtr.status.in_progress";
    public static final String TOOLTIP_TYPE_PUBLIC = "tooltip.dimtr.type.public";
    public static final String TOOLTIP_TYPE_PRIVATE = "tooltip.dimtr.type.private";
    public static final String TOOLTIP_PHASE_COMPLETE = "tooltip.dimtr.phase.complete";

    // ============================================================================
    // 🎯 NOTIFICATION HELPER - TRANSLATION KEYS
    // ============================================================================
    
    // Progress update notifications
    public static final String NOTIFICATION_PROGRESS_COMPLETE = "notification.dimtr.progress.complete";
    public static final String NOTIFICATION_PROGRESS_COMPLETE_DESC = "notification.dimtr.progress.complete.desc";
    public static final String NOTIFICATION_PROGRESS_PREFIX = "notification.dimtr.progress.prefix";
    
    // Phase completion notifications
    public static final String NOTIFICATION_PHASE_COMPLETE = "notification.dimtr.phase.complete";
    public static final String NOTIFICATION_PHASE_COMPLETE_CONGRATS = "notification.dimtr.phase.complete.congrats";
    public static final String NOTIFICATION_CONTENT_UNLOCKED = "notification.dimtr.content.unlocked";
    public static final String NOTIFICATION_CONTENT_ACCESSIBLE = "notification.dimtr.content.accessible";
    
    // Party notifications
    public static final String NOTIFICATION_PARTY_JOIN = "notification.dimtr.party.join";
    public static final String NOTIFICATION_PARTY_UPDATE = "notification.dimtr.party.update";
    public static final String NOTIFICATION_PARTY_WELCOME = "notification.dimtr.party.welcome";
    public static final String NOTIFICATION_PARTY_SHARE_PROGRESS = "notification.dimtr.party.share.progress";
    
    // Multiplier notifications
    public static final String NOTIFICATION_MULTIPLIER_GAINED = "notification.dimtr.multiplier.gained";
    public static final String NOTIFICATION_MULTIPLIER_FROM = "notification.dimtr.multiplier.from";

    // Debug commands
    // ============================================================================
    // 🎯 PARTY SYSTEM - TRANSLATION KEYS
    // ============================================================================

    // ============================================================================
    // 🎯 LOGGING SYSTEM - MESSAGES
    // ============================================================================
    
    // Initialization logs
    public static final String LOG_INITIALIZING_MOD = "🚀 Initializing Dimension Trials Mod...";
    public static final String LOG_CONFIG_REGISTERED = "✅ Registered server and client configurations";
    public static final String LOG_NETWORKING_REGISTERED = "✅ Registered networking system with party support";
    public static final String LOG_CUSTOM_REQUIREMENTS_INITIALIZED = "✅ Initialized custom requirements system";
    public static final String LOG_INITIALIZATION_COMPLETE = "🎯 Dimension Trials Mod initialization complete!";
    public static final String LOG_FEATURES_AVAILABLE = "📋 Features available:";
    public static final String LOG_FEATURE_PHASE_PROGRESSION = "   • Phase-based progression system";
    public static final String LOG_FEATURE_PARTY_SYSTEM = "   • Collaborative party system ✅";
    public static final String LOG_FEATURE_HUD_INTERFACE = "   • Modular HUD interface";
    public static final String LOG_FEATURE_ADMIN_COMMANDS = "   • Administrative commands (/dimtr)";
    public static final String LOG_FEATURE_PARTY_COMMANDS = "   • Party management (/party) ✅";
    public static final String LOG_FEATURE_INDIVIDUAL_TRACKING = "   • Individual progression tracking ✅";
    public static final String LOG_FEATURE_PROXIMITY_MULTIPLIERS = "   • Proximity-based multipliers ✅";
    public static final String LOG_FEATURE_CUSTOM_REQUIREMENTS = "   • Custom requirements system 🎯 NEW";
    
    // Command registration logs
    public static final String LOG_COMMANDS_REGISTERED = "✅ Registered all DimTr commands:";
    public static final String LOG_COMMANDS_DIMTR = "   • /dimtr (Administrative & individual commands - OP required)";
    public static final String LOG_COMMANDS_PARTY = "   • /party (Party management commands - No OP required)";
    
    // Networking logs
    public static final String LOG_NETWORK_PAYLOADS_REGISTERED = "✅ Network payloads registered successfully:";
    public static final String LOG_NETWORK_PROGRESSION_PAYLOAD = "   • UpdateProgressionToClientPayload (Individual progression sync)";
    public static final String LOG_NETWORK_PARTY_PAYLOAD = "   • UpdatePartyToClientPayload (Party system sync)";
    public static final String LOG_NETWORK_OPERATIONAL = "🎯 All networking systems are operational!";
    
    // Debug and processing logs
    public static final String LOG_MOB_KILL_PARTY = "🎯 Mob kill processed by PARTY system: {} -> {}";
    public static final String LOG_MOB_KILL_INDIVIDUAL = "🎯 Mob kill processed by INDIVIDUAL system: {} -> {}";
    public static final String LOG_OBJECTIVE_PARTY = "🎯 Special objective processed by PARTY system: {} -> {}";
    public static final String LOG_OBJECTIVE_INDIVIDUAL = "🎯 Special objective processed by INDIVIDUAL system: {} -> {}";
    public static final String LOG_PARTY_OBJECTIVE_COMPLETED = "✅ Party objective completed: {} - {} by {}";
    public static final String LOG_INDIVIDUAL_OBJECTIVE_COMPLETED = "✅ Individual custom objective completed: {} - {} by {}";
    public static final String LOG_PARTY_PHASE_COMPLETED = "✅ Party completed custom phase: {} ({})";
    public static final String LOG_PLAYER_PHASE_COMPLETED = "✅ Player completed custom phase: {} ({})";
    public static final String LOG_PLAYER_NEAR_MOB = "Player {} near mob at ({}, {}, {}) - Phase1: {}, Phase2: {}, Multiplier: {}";
    public static final String LOG_PARTY_DATA_UPDATED = "✅ Party data updated on client: {} with {} members";
    
    // Custom requirements logs
    public static final String LOG_CUSTOM_REQUIREMENTS_LOADED = "Loaded {} custom requirement sets";
    public static final String LOG_CUSTOM_REQUIREMENTS_EXAMPLE_CREATED = "Created example custom requirements file: {}";
    public static final String LOG_CUSTOM_REQUIREMENTS_FILE_LOADED = "Loaded custom requirements: {} ({})";
    public static final String LOG_DATA_GENERATION_COMPLETE = "Data generation setup completed for {}";

    // ============================================================================
    // 🎯 MOB TYPES - CONSTANTS
    // ============================================================================
    
    // Phase 1 mob types
    public static final String MOB_TYPE_DROWNED = "drowned";
    public static final String MOB_TYPE_ZOMBIE = "zombie";
    // ✅ REMOVIDO: MOB_TYPE_ZOMBIE_VILLAGER - funcionalidade descontinuada
    public static final String MOB_TYPE_SKELETON = "skeleton";
    public static final String MOB_TYPE_STRAY = "stray";
    public static final String MOB_TYPE_HUSK = "husk";
    public static final String MOB_TYPE_SPIDER = "spider";
    public static final String MOB_TYPE_CREEPER = "creeper";
    public static final String MOB_TYPE_ENDERMAN = "enderman";
    public static final String MOB_TYPE_WITCH = "witch";
    public static final String MOB_TYPE_CAPTAIN = "captain";
    public static final String MOB_TYPE_PILLAGER = "pillager";
    public static final String MOB_TYPE_VINDICATOR = "vindicator";
    public static final String MOB_TYPE_EVOKER = "evoker";
    public static final String MOB_TYPE_RAVAGER = "ravager";
    public static final String MOB_TYPE_BOGGED = "bogged";
    public static final String MOB_TYPE_BREEZE = "breeze";
    
    // Phase 2 mob types
    public static final String MOB_TYPE_BLAZE = "blaze";
    public static final String MOB_TYPE_WITHER_SKELETON = "wither_skeleton";
    public static final String MOB_TYPE_PIGLIN_BRUTE = "piglin_brute";
    public static final String MOB_TYPE_HOGLIN = "hoglin";
    public static final String MOB_TYPE_ZOGLIN = "zoglin";
    public static final String MOB_TYPE_GHAST = "ghast";
    public static final String MOB_TYPE_PIGLIN = "piglin";

    // ============================================================================
    // 🎯 OBJECTIVE TYPES - CONSTANTS
    // ============================================================================
    
    public static final String OBJECTIVE_TYPE_ELDER_GUARDIAN = "elder_guardian";
    public static final String OBJECTIVE_TYPE_WITHER = "wither";
    public static final String OBJECTIVE_TYPE_WARDEN = "warden";
    public static final String OBJECTIVE_TYPE_RAID = "raid";
    public static final String OBJECTIVE_TYPE_TRIAL_VAULT = "trial_vault";
    public static final String OBJECTIVE_TYPE_VOLUNTARY_EXILE = "voluntary_exile";

    // ============================================================================
    // 🎯 DIMENSION TYPES - CONSTANTS
    // ============================================================================
    
    public static final String DIMENSION_TYPE_NETHER = "nether";
    public static final String DIMENSION_TYPE_END = "end";
    public static final String DIMENSION_TYPE_CUSTOM = "custom";

    // ============================================================================
    // 🎯 ADVANCEMENT IDS - CONSTANTS
    // ============================================================================
    
    public static final String ADVANCEMENT_KILL_A_MOB = "minecraft:adventure/kill_a_mob";
    public static final String ADVANCEMENT_VOLUNTARY_EXILE = "minecraft:adventure/voluntary_exile";
    public static final String ADVANCEMENT_HERO_OF_VILLAGE = "minecraft:adventure/hero_of_the_village";
    public static final String ADVANCEMENT_UNDER_LOCK_AND_KEY = "minecraft:adventure/under_lock_and_key";

    // ============================================================================
    // 🎯 DIMENSION LOCKS - TRANSLATION KEYS (FALTAVAM)
    // ============================================================================
    
    public static final String MSG_NETHER_LOCKED = "message.dimtr.nether_locked";
    public static final String MSG_END_LOCKED = "message.dimtr.end_locked";

    // ============================================================================
    // 🎯 UI FORMATTING - CONSTANTS
    // ============================================================================
    
    // Progress separators
    public static final String PROGRESS_SEPARATOR = "/";
    public static final String LABEL_VALUE_SEPARATOR = ": ";
    public static final String SPACE = " ";
    public static final String DOUBLE_SPACE = "  ";
    public static final String EMPTY_STRING = "";
    
    // Progress bar elements
    public static final String PROGRESS_BAR_START = "[";
    public static final String PROGRESS_BAR_FILLED = "█";
    public static final String PROGRESS_BAR_EMPTY = "░";
    public static final String PROGRESS_BAR_END = "] ";
    public static final String PERCENTAGE_SYMBOL = "%";
    
    // Multiplier formatting
    public static final String MULTIPLIER_FORMAT = "%.1fx";
    
    // UI icons
    public static final String UI_ICON_LOCK = "🔒";
    public static final String UI_ICON_ARROW = "➤";
    
    // Notification type icons (from enum)
    public static final String NOTIFICATION_ICON_SUCCESS = "✅";
    public static final String NOTIFICATION_ICON_INFO = "ℹ";
    public static final String NOTIFICATION_ICON_WARNING = "⚠";
    public static final String NOTIFICATION_ICON_ERROR = "❌";
    public static final String NOTIFICATION_ICON_ACHIEVEMENT = "🏆";
    public static final String NOTIFICATION_ICON_PARTY = "👥";

    // ============================================================================
    // 🎯 COMMAND HELP SYSTEM - TRANSLATION KEYS
    // ============================================================================
    
    public static final String HELP_HEADER = "cmd.dimtr.help.header";
    public static final String HELP_SUBTITLE = "cmd.dimtr.help.subtitle";
    public static final String HELP_GETTING_STARTED = "cmd.dimtr.help.getting_started";
    public static final String HELP_PRESS_J = "cmd.dimtr.help.press_j";
    public static final String HELP_COMPLETE_PHASE1 = "cmd.dimtr.help.complete_phase1";
    public static final String HELP_COMPLETE_PHASE2 = "cmd.dimtr.help.complete_phase2";
    public static final String HELP_PLAYER_COMMANDS = "cmd.dimtr.help.player_commands";
    public static final String HELP_PARTY_CREATE = "cmd.dimtr.help.party_create";
    public static final String HELP_PARTY_JOIN = "cmd.dimtr.help.party_join";
    public static final String HELP_PARTY_LEAVE = "cmd.dimtr.help.party_leave";
    public static final String HELP_PARTY_LIST = "cmd.dimtr.help.party_list";
    public static final String HELP_PARTY_INFO = "cmd.dimtr.help.party_info";
    public static final String HELP_ADMIN_COMMANDS = "cmd.dimtr.help.admin_commands";
    public static final String HELP_ADMIN_COMPLETE = "cmd.dimtr.help.admin_complete";
    public static final String HELP_ADMIN_RESET = "cmd.dimtr.help.admin_reset";
    public static final String HELP_ADMIN_STATUS = "cmd.dimtr.help.admin_status";
    public static final String HELP_ADMIN_SYNC = "cmd.dimtr.help.admin_sync";
    public static final String HELP_TROUBLESHOOTING = "cmd.dimtr.help.troubleshooting";
    public static final String HELP_PROGRESS_NOT_UPDATING = "cmd.dimtr.help.progress_not_updating";
    public static final String HELP_HUD_NOT_SHOWING = "cmd.dimtr.help.hud_not_showing";
    public static final String HELP_PARTY_ISSUES = "cmd.dimtr.help.party_issues";
    public static final String HELP_CONTACT_ADMINS = "cmd.dimtr.help.contact_admins";

    // ============================================================================
    // 🎯 DEBUG SYSTEM - TRANSLATION KEYS
    // ============================================================================
    
    public static final String DEBUG_SYSTEM_INDIVIDUAL = "cmd.dimtr.debug.system_individual";
    public static final String DEBUG_CONFIG_ACTIVE = "cmd.dimtr.debug.config_active";
    public static final String DEBUG_PHASE1_STATUS = "cmd.dimtr.debug.phase1_status";
    public static final String DEBUG_PHASE2_STATUS = "cmd.dimtr.debug.phase2_status";
    public static final String DEBUG_VOLUNTARY_EXILE_STATUS = "cmd.dimtr.debug.voluntary_exile_status";
    public static final String DEBUG_PHASE1_ENABLED = "cmd.dimtr.debug.phase1_enabled";
    public static final String DEBUG_PHASE1_DISABLED = "cmd.dimtr.debug.phase1_disabled";
    public static final String DEBUG_PHASE2_ENABLED = "cmd.dimtr.debug.phase2_enabled";
    public static final String DEBUG_PHASE2_DISABLED = "cmd.dimtr.debug.phase2_disabled";
    public static final String DEBUG_VOLUNTARY_EXILE_REQUIRED = "cmd.dimtr.debug.voluntary_exile_required";
    public static final String DEBUG_VOLUNTARY_EXILE_OPTIONAL = "cmd.dimtr.debug.voluntary_exile_optional";
    public static final String DEBUG_PLAYERS_ONLINE_HEADER = "cmd.dimtr.debug.players_online_header";
    public static final String DEBUG_MULTIPLIERS_HEADER = "cmd.dimtr.debug.multipliers_header";
    public static final String DEBUG_PLAYER_STATUS_FORMAT = "cmd.dimtr.debug.player_status_format";
    public static final String DEBUG_PLAYER_INDIVIDUAL = "cmd.dimtr.debug.player_individual";
    public static final String DEBUG_PLAYER_PROXIMITY = "cmd.dimtr.debug.player_proximity";
    public static final String DEBUG_PHASE2_STATUS_FORMAT = "cmd.dimtr.debug.phase2_status_format";

    // ============================================================================
    // 🎯 HELP CONTENT - HARDCODED STRINGS TO MIGRATE TO TRANSLATION KEYS
    // ============================================================================
    
    // Help system content (temporary until creating translation keys)
    public static final String HELP_HEADER_CONTENT = "=== DIMENSION TRIALS HELP ===";
    public static final String HELP_SUBTITLE_CONTENT = "🌟 Your comprehensive guide to conquering dimensions";
    public static final String HELP_GETTING_STARTED_CONTENT = "🚀 Getting Started:";
    public static final String HELP_PRESS_J_CONTENT = "• Press 'J' to open your progression HUD";
    public static final String HELP_COMPLETE_PHASE1_CONTENT = "• Complete Phase 1 objectives to unlock the Nether";
    public static final String HELP_COMPLETE_PHASE2_CONTENT = "• Complete Phase 2 objectives to unlock The End";
    public static final String HELP_PLAYER_COMMANDS_CONTENT = "⚙ Player Commands:";
    public static final String HELP_PARTY_CREATE_CONTENT = "• /party create <n> [password] - Create a party";
    public static final String HELP_PARTY_JOIN_CONTENT = "• /party join <n> [password] - Join a party";
    public static final String HELP_PARTY_LEAVE_CONTENT = "• /party leave - Leave your party";
    public static final String HELP_PARTY_LIST_CONTENT = "• /party list - List public parties";
    public static final String HELP_PARTY_INFO_CONTENT = "• /party info - View party information";
    public static final String HELP_ADMIN_COMMANDS_CONTENT = "👑 Admin Commands:";
    public static final String HELP_ADMIN_COMPLETE_CONTENT = "• /dimtr player <player> complete phase1|phase2";
    public static final String HELP_ADMIN_RESET_CONTENT = "• /dimtr player <player> reset all|phase1|phase2";
    public static final String HELP_ADMIN_STATUS_CONTENT = "• /dimtr player <player> status - View player progress";
    public static final String HELP_ADMIN_SYNC_CONTENT = "• /dimtr player <player> sync - Force client sync";
    public static final String HELP_TROUBLESHOOTING_CONTENT = "🔧 Troubleshooting:";
    public static final String HELP_PROGRESS_NOT_UPDATING_CONTENT = "• Progress not updating? Contact an admin for sync";
    public static final String HELP_HUD_NOT_SHOWING_CONTENT = "• HUD not showing? Press 'J' or check keybindings";
    public static final String HELP_PARTY_ISSUES_CONTENT = "• Party issues? Try leaving and rejoining";
    public static final String HELP_CONTACT_ADMINS_CONTENT = "💡 For more help, contact server administrators";

    // Debug system content (English)
    public static final String DEBUG_SYSTEM_INDIVIDUAL_CONTENT = "🌍 Dimension Trials - Individual System";
    public static final String DEBUG_CONFIG_ACTIVE_CONTENT = "📋 Active Configurations:";
    public static final String DEBUG_PHASE1_ENABLED_CONTENT = "Enabled";
    public static final String DEBUG_PHASE1_DISABLED_CONTENT = "Disabled";
    public static final String DEBUG_PHASE2_ENABLED_CONTENT = "Enabled";
    public static final String DEBUG_PHASE2_DISABLED_CONTENT = "Disabled";
    public static final String DEBUG_VOLUNTARY_EXILE_REQUIRED_CONTENT = "Required";
    public static final String DEBUG_VOLUNTARY_EXILE_OPTIONAL_CONTENT = "Optional";
    public static final String DEBUG_PLAYERS_ONLINE_HEADER_CONTENT = "=== ONLINE PLAYERS ===";
    public static final String DEBUG_MULTIPLIERS_HEADER_CONTENT = "=== MULTIPLIERS BY PLAYER ===";
    public static final String DEBUG_PHASE1_PREFIX = "  Phase 1: ";
    public static final String DEBUG_PHASE2_PREFIX = "  Phase 2: ";
    public static final String DEBUG_VOLUNTARY_EXILE_PREFIX = "  Voluntary Exile: ";
    public static final String DEBUG_PLAYER_PREFIX = "👤 ";
    public static final String DEBUG_INDIVIDUAL_PREFIX = "  Individual: ";
    public static final String DEBUG_PROXIMITY_PREFIX = "  Nearby: ";
    public static final String DEBUG_PHASE2_STATUS_PREFIX = "  Phase 2: ";
    
    // ============================================================================
    // 🎯 COMMANDS - DIMTR COMMANDS (CONVERTED TO ENGLISH)
    // ============================================================================
    
    // Success messages
    public static final String PHASE1_COMPLETE_SUCCESS = "✅ Phase 1 completed for %s!";
    public static final String PHASE2_COMPLETE_SUCCESS = "✅ Phase 2 completed for %s!";
    public static final String PROGRESS_RESET_SUCCESS = "✅ %s reset for %s!";
    public static final String GOAL_SET_SUCCESS = "✅ Goal '%s' set to %s for %s";
    public static final String MOB_COUNT_SET_SUCCESS = "✅ Count of '%s' set to %d for %s";
    public static final String SYNC_SUCCESS = "✅ Synchronization sent to %s!";
    
    // Error messages
    public static final String INVALID_GOAL_ERROR = "❌ Invalid goal: %s";
    public static final String INVALID_MOB_TYPE_ERROR = "❌ Invalid mob type: %s";
    public static final String SYNC_FAILED_ERROR = "❌ Synchronization failed: %s";
    
    // Status display headers
    public static final String STATUS_HEADER = "=== %s PROGRESSION ===";
    public static final String PHASE_STATUS_TEMPLATE = "🏆 %s: %s";
    public static final String MULTIPLIER_DISPLAY = "⚡ Multiplier: %.1fx";
    public static final String SPECIAL_OBJECTIVES_HEADER = "--- SPECIAL OBJECTIVES ---";
    public static final String MAIN_COUNTERS_HEADER = "--- MAIN COUNTERS ---";
    public static final String MOB_KILL_DISPLAY = "⚔ %s: %d";
    
    // Phase status values
    public static final String PHASE_COMPLETE = "COMPLETE";
    public static final String PHASE_INCOMPLETE = "INCOMPLETE";
    public static final String PHASE_1_LABEL = "Phase 1";
    public static final String PHASE_2_LABEL = "Phase 2";
    
    // Reset type descriptions
    public static final String RESET_ALL_DESC = "complete progression";
    public static final String RESET_PHASE1_DESC = "Phase 1 progression";
    public static final String RESET_PHASE2_DESC = "Phase 2 progression";
    public static final String RESET_MOB_KILLS_DESC = "mob counters";
    public static final String RESET_DEFAULT_DESC = "data";
    
    // Debug messages
    public static final String DEBUG_PAYLOAD_HEADER = "=== DEBUG PAYLOAD: %s ===";
    public static final String DEBUG_REQUIREMENTS_HEADER = "📊 Requirements vs Current:";
    public static final String DEBUG_PHASES_HEADER = "🔧 Phase Status:";
    public static final String DEBUG_PHASE_STATUS = "  %s: %s";
    public static final String SYNC_HELP_MESSAGE = "💡 Ask the player to open the HUD (J) to check the values";

    // ============================================================================
    // 🎯 COMMAND LITERALS - CONSTANTS
    // ============================================================================
    
    // Party command literals
    public static final String CMD_PARTY_LITERAL = "party";
    public static final String CMD_CREATE_LITERAL = "create";
    public static final String CMD_JOIN_LITERAL = "join";
    public static final String CMD_LEAVE_LITERAL = "leave";
    public static final String CMD_LIST_LITERAL = "list";
    public static final String CMD_INFO_LITERAL = "info";
    public static final String CMD_DISBAND_LITERAL = "disband";
    public static final String CMD_KICK_LITERAL = "kick";
    public static final String CMD_PROMOTE_LITERAL = "promote";
    public static final String CMD_INVITE_LITERAL = "invite";

    // ============================================================================
    // 🎯 HARDCODED MESSAGES TO MIGRATE TO TRANSLATION KEYS
    // ============================================================================
    
    // Party command error messages (Portuguese - should be translation keys)
    public static final String PARTY_ERROR_ALREADY_IN_PARTY_LEAVE = "party.dimtr.error.already_in_party_leave";
    public static final String PARTY_ERROR_INVALID_NAME_LENGTH = "party.dimtr.error.invalid_name_length";
    public static final String PARTY_ERROR_NAME_EXISTS = "party.dimtr.error.name_exists";
    public static final String PARTY_ERROR_UNKNOWN_CREATE_PARTY = "party.dimtr.error.unknown_create_party";
    public static final String PARTY_ERROR_MUST_BE_PLAYER = "party.dimtr.error.must_be_player";
    public static final String PARTY_ERROR_PARTY_FULL_DISPLAY = "party.dimtr.error.party_full_display";
    public static final String PARTY_ERROR_NOT_IN_ANY_PARTY = "party.dimtr.error.not_in_any_party";
    public static final String PARTY_ERROR_UNKNOWN_LEAVE_PARTY = "party.dimtr.error.unknown_leave_party";
    public static final String PARTY_ERROR_GET_PARTY_INFO_FAILED = "party.dimtr.error.get_party_info_failed";
    
    // Party command success messages (Portuguese - should be translation keys)
    public static final String PARTY_JOIN_SUCCESS_MESSAGE = "party.dimtr.join.success.message";
    public static final String PARTY_JOIN_SUCCESS_BENEFIT_SHARE = "party.dimtr.join.success.benefit.share";
    public static final String PARTY_JOIN_SUCCESS_INFO_HINT = "party.dimtr.join.success.info.hint";
    public static final String PARTY_LEAVE_SUCCESS_MESSAGE = "party.dimtr.leave.success.message";
    public static final String PARTY_LEAVE_SUCCESS_INDIVIDUAL_NEW = "party.dimtr.leave.success.individual.new";
    public static final String PARTY_LIST_NO_PUBLIC_PARTIES = "party.dimtr.list.no_public_parties";
    public static final String PARTY_LIST_CREATE_HINT = "party.dimtr.list.create.hint";
    public static final String PARTY_LIST_PUBLIC_HEADER = "party.dimtr.list.public.header";
    public static final String PARTY_LIST_JOIN_HINT = "party.dimtr.list.join.hint";
    
    // Party info display (Portuguese - should be translation keys)
    public static final String PARTY_INFO_HEADER_DISPLAY = "party.dimtr.info.header.display";
    public static final String PARTY_INFO_NAME_DISPLAY = "party.dimtr.info.name.display";
    public static final String PARTY_INFO_MEMBERS_DISPLAY = "party.dimtr.info.members.display";
    public static final String PARTY_INFO_TYPE_DISPLAY = "party.dimtr.info.type.display";
    public static final String PARTY_INFO_TYPE_PUBLIC_DISPLAY = "party.dimtr.info.type.public.display";
    public static final String PARTY_INFO_TYPE_PRIVATE_DISPLAY = "party.dimtr.info.type.private.display";
    public static final String PARTY_INFO_MULTIPLIER_DISPLAY = "party.dimtr.info.multiplier.display";
    public static final String PARTY_INFO_MEMBERS_SECTION = "party.dimtr.info.members.section";
    public static final String PARTY_INFO_SHARED_PROGRESS_SECTION = "party.dimtr.info.shared.progress.section";
    public static final String PARTY_INFO_ELDER_GUARDIAN_KILLED = "party.dimtr.info.elder.guardian.killed";
    public static final String PARTY_INFO_RAID_WON = "party.dimtr.info.raid.won";
    public static final String PARTY_INFO_MEMBER_YOU_SUFFIX = "party.dimtr.info.member.you.suffix";
    
    // DimTr command success messages (Portuguese - should be translation keys)
    public static final String DIMTR_PHASE1_COMPLETE_SUCCESS = "cmd.dimtr.phase1.complete.success";
    public static final String DIMTR_PHASE2_COMPLETE_SUCCESS = "cmd.dimtr.phase2.complete.success";
    public static final String DIMTR_RESET_SUCCESS_MESSAGE = "cmd.dimtr.reset.success.message";
    public static final String DIMTR_GOAL_SET_SUCCESS = "cmd.dimtr.goal.set.success";
    public static final String DIMTR_MOB_COUNT_SET_SUCCESS = "cmd.dimtr.mob.count.set.success";
    public static final String DIMTR_SYNC_SUCCESS_MESSAGE = "cmd.dimtr.sync.success.message";
    public static final String DIMTR_SYNC_HELP_MESSAGE = "cmd.dimtr.sync.help.message";
    
    // DimTr command error messages (Portuguese - should be translation keys)
    public static final String DIMTR_INVALID_GOAL_ERROR = "cmd.dimtr.error.invalid.goal";
    public static final String DIMTR_INVALID_MOB_TYPE_ERROR = "cmd.dimtr.error.invalid.mob.type";
    public static final String DIMTR_SYNC_FAILED_ERROR = "cmd.dimtr.error.sync.failed";
    public static final String DIMTR_DEBUG_PAYLOAD_ERROR = "cmd.dimtr.error.debug.payload";
    
    // DimTr status display headers (Portuguese - should be translation keys)
    public static final String DIMTR_STATUS_PLAYER_HEADER = "cmd.dimtr.status.player.header";
    public static final String DIMTR_STATUS_PHASE1_DISPLAY = "cmd.dimtr.status.phase1.display";
    public static final String DIMTR_STATUS_PHASE2_DISPLAY = "cmd.dimtr.status.phase2.display";
    public static final String DIMTR_STATUS_MULTIPLIER_DISPLAY = "cmd.dimtr.status.multiplier.display";
    public static final String DIMTR_STATUS_SPECIAL_OBJECTIVES_HEADER = "cmd.dimtr.status.special.objectives.header";
    public static final String DIMTR_STATUS_MAIN_COUNTERS_HEADER = "cmd.dimtr.status.main.counters.header";
    public static final String DIMTR_STATUS_MOB_KILL_DISPLAY = "cmd.dimtr.status.mob.kill.display";
    public static final String DIMTR_STATUS_GLOBAL_HEADER = "cmd.dimtr.status.global.header";
    public static final String DIMTR_DEBUG_PAYLOAD_HEADER = "cmd.dimtr.debug.payload.header";
    public static final String DIMTR_DEBUG_REQUIREMENTS_HEADER = "cmd.dimtr.debug.requirements.header";
    public static final String DIMTR_DEBUG_PHASES_HEADER = "cmd.dimtr.debug.phases.header";
    public static final String DIMTR_DEBUG_INDIVIDUAL_SYSTEM = "cmd.dimtr.debug.individual.system";
    
    // Status values (Portuguese - should be translation keys)
    public static final String STATUS_COMPLETE = "status.dimtr.complete";
    public static final String STATUS_INCOMPLETE = "status.dimtr.incomplete";
    public static final String PHASE_1_LABEL_NEW = "phase.dimtr.phase1.label";
    public static final String PHASE_2_LABEL_NEW = "phase.dimtr.phase2.label";
    
    // Reset descriptions (Portuguese - should be translation keys)
    public static final String RESET_ALL_DESCRIPTION = "reset.dimtr.all.description";
    public static final String RESET_PHASE1_DESCRIPTION = "reset.dimtr.phase1.description";
    public static final String RESET_PHASE2_DESCRIPTION = "reset.dimtr.phase2.description";
    public static final String RESET_MOB_KILLS_DESCRIPTION = "reset.dimtr.mob.kills.description";
    public static final String RESET_DEFAULT_DESCRIPTION = "reset.dimtr.default.description";

    // ============================================================================
    // 🎯 HARDCODED STRING CONSTANTS - FINAL CLEANUP
    // ============================================================================
    
    // Debug system constants (avoid duplicates)
    public static final String CMD_DEBUG_PAYLOAD_ERROR_ALT = "cmd.dimtr.debug.payload.error.alt";
    
    // Global status system constants
    public static final String CMD_SYSTEM_TITLE = "cmd.dimtr.system.title";
    public static final String CMD_ACTIVE_CONFIGURATIONS = "cmd.dimtr.active.configurations";
    public static final String CMD_PHASE1_STATUS_CONFIG = "cmd.dimtr.phase1.status.config";
    public static final String CMD_PHASE2_STATUS_CONFIG = "cmd.dimtr.phase2.status.config";
    public static final String CMD_VOLUNTARY_EXILE_STATUS_CONFIG = "cmd.dimtr.voluntary.exile.status.config";
    public static final String CMD_STATUS_ENABLED = "cmd.dimtr.status.enabled";
    public static final String CMD_STATUS_DISABLED = "cmd.dimtr.status.disabled";
    public static final String CMD_STATUS_REQUIRED = "cmd.dimtr.status.required";
    public static final String CMD_STATUS_OPTIONAL = "cmd.dimtr.status.optional";
    public static final String CMD_ONLINE_PLAYERS_SECTION = "cmd.dimtr.online.players.section";
    public static final String CMD_NO_ONLINE_PLAYERS = "cmd.dimtr.no.online.players";
    public static final String CMD_MULTIPLIERS_BY_PLAYER = "cmd.dimtr.multipliers.by.player";
    public static final String CMD_INDIVIDUAL_MULT = "cmd.dimtr.individual.mult";
    public static final String CMD_NEARBY_MULT = "cmd.dimtr.nearby.mult";
    public static final String CMD_COMMAND_PLAYER_ONLY = "cmd.dimtr.command.player.only";
    
    // Party creation constants (complete set)
    public static final String PARTY_CREATE_SUCCESS_FULL_FORMAT = "party.dimtr.create.success.full.format";
    public static final String PARTY_CREATE_LEADER_MESSAGE = "party.dimtr.create.leader.message";
    public static final String PARTY_CREATE_JOIN_PUBLIC_INFO = "party.dimtr.create.join.public.info";
    public static final String PARTY_CREATE_PASSWORD_SHARE = "party.dimtr.create.password.share";
    public static final String PARTY_CREATE_MULTIPLIER_CURRENT = "party.dimtr.create.multiplier.current";
    public static final String PARTY_ERROR_NAME_ALREADY_EXISTS = "party.dimtr.error.name.already.exists";
    public static final String PARTY_ERROR_UNKNOWN_CREATION = "party.dimtr.error.unknown.creation";
    public static final String PARTY_ERROR_COMMAND_MUST_BE_PLAYER = "party.dimtr.error.command.must.be.player";
    
    // Party joining constants (unique names to avoid duplicates)
    public static final String PARTY_JOIN_SUCCESS_FULL_FORMAT = "party.dimtr.join.success.full.format";
    public static final String PARTY_JOIN_PROGRESS_SHARING = "party.dimtr.join.progress.sharing";
    public static final String PARTY_JOIN_INFO_TIP = "party.dimtr.join.info.tip";
    public static final String PARTY_ERROR_ALREADY_IN_PARTY_DETAILED = "party.dimtr.error.already.in.party.detailed";
    public static final String PARTY_ERROR_PARTY_NOT_FOUND_DETAILED = "party.dimtr.error.party.not.found.detailed";
    public static final String PARTY_ERROR_PARTY_IS_FULL = "party.dimtr.error.party.is.full";
    public static final String PARTY_ERROR_UNKNOWN_JOIN_ERROR = "party.dimtr.error.unknown.join.error";
    
    // Party leaving constants (unique names)
    public static final String PARTY_LEAVE_SUCCESS_MESSAGE_DETAILED = "party.dimtr.leave.success.message.detailed";
    public static final String PARTY_LEAVE_INDIVIDUAL_MODE = "party.dimtr.leave.individual.mode";
    
    // Party listing constants (unique names)
    public static final String PARTY_LIST_NO_PUBLIC = "party.dimtr.list.no.public";
    public static final String PARTY_LIST_CREATE_SUGGESTION_ALT = "party.dimtr.list.create.suggestion.alt";
    public static final String PARTY_LIST_PUBLIC_PARTIES = "party.dimtr.list.public.parties";
    public static final String PARTY_LIST_JOIN_SUGGESTION_ALT = "party.dimtr.list.join.suggestion.alt";
    
    // Party info constants (unique names)
    public static final String PARTY_INFO_PARTY_HEADER = "party.dimtr.info.party.header";
    public static final String PARTY_INFO_PARTY_NAME = "party.dimtr.info.party.name";
    public static final String PARTY_INFO_MEMBER_COUNT = "party.dimtr.info.member.count";
    public static final String PARTY_INFO_PARTY_TYPE = "party.dimtr.info.party.type";
    public static final String PARTY_INFO_TYPE_PUBLIC_DISPLAY_ALT = "party.dimtr.info.type.public.display.alt";
    public static final String PARTY_INFO_TYPE_PRIVATE_DISPLAY_ALT = "party.dimtr.info.type.private.display.alt";
    public static final String PARTY_INFO_PARTY_MULTIPLIER = "party.dimtr.info.party.multiplier";
    public static final String PARTY_INFO_MEMBERS_LIST = "party.dimtr.info.members.list";
    public static final String PARTY_INFO_SHARED_PROGRESS_DISPLAY = "party.dimtr.info.shared.progress.display";
    public static final String PARTY_INFO_ELDER_GUARDIAN_STATUS = "party.dimtr.info.elder.guardian.status";
    public static final String PARTY_INFO_RAID_WON_STATUS = "party.dimtr.info.raid.won.status";
    public static final String PARTY_INFO_TRIAL_VAULT_STATUS = "party.dimtr.info.trial.vault.status";
    public static final String PARTY_INFO_VOLUNTARY_EXILE_STATUS = "party.dimtr.info.voluntary.exile.status";
    public static final String PARTY_INFO_WITHER_STATUS = "party.dimtr.info.wither.status";
    public static final String PARTY_INFO_WARDEN_STATUS = "party.dimtr.info.warden.status";
    public static final String PARTY_INFO_YOU_MARKER = "party.dimtr.info.you.marker";
    
    // Party management constants (kick, promote, disband, invite)
    public static final String PARTY_KICK_SUCCESS_DETAILED = "party.dimtr.kick.success.detailed";
    public static final String PARTY_KICK_NOTIFICATION_TO_PLAYER = "party.dimtr.kick.notification.to.player";
    public static final String PARTY_PROMOTE_SUCCESS_TO_NEW_LEADER = "party.dimtr.promote.success.to.new.leader";
    public static final String PARTY_PROMOTE_SUCCESS_TO_OLD_LEADER = "party.dimtr.promote.success.to.old.leader";
    public static final String PARTY_PROMOTE_NOTIFICATION_TO_PARTY = "party.dimtr.promote.notification.to.party";
    public static final String PARTY_DISBAND_SUCCESS_TO_LEADER = "party.dimtr.disband.success.to.leader";
    public static final String PARTY_DISBAND_NOTIFICATION_TO_MEMBERS = "party.dimtr.disband.notification.to.members";
    public static final String PARTY_INVITE_SUCCESS_TO_LEADER = "party.dimtr.invite.success.to.leader";
    public static final String PARTY_INVITE_NOTIFICATION_TO_PLAYER = "party.dimtr.invite.notification.to.player";
    public static final String PARTY_INVITE_PUBLIC_JOIN_INFO = "party.dimtr.invite.public.join.info";
    public static final String PARTY_INVITE_PRIVATE_JOIN_INFO = "party.dimtr.invite.private.join.info";
    
    // Additional error constants
    public static final String PARTY_ERROR_CANNOT_KICK_YOURSELF = "party.dimtr.error.cannot.kick.yourself";
    public static final String PARTY_ERROR_CANNOT_PROMOTE_YOURSELF = "party.dimtr.error.cannot.promote.yourself";
    public static final String PARTY_ERROR_ONLY_LEADER_CAN_INVITE = "party.dimtr.error.only.leader.can.invite";
          public static final String PARTY_ERROR_PLAYER_ALREADY_IN_PARTY_FOR_INVITE = "party.dimtr.error.player.already.in.party.for.invite";
    public static final String PARTY_ERROR_PARTY_FULL_FOR_INVITE = "party.dimtr.error.party.full.for.invite";

    // ============================================================================
    // 🎯 HARDCODED STRINGS TO MIGRATE - DIMTR COMMANDS
    // ============================================================================
    
    // DimTr status display hardcoded strings
    public static final String CMD_STATUS_PROGRESSION_HEADER = "cmd.dimtr.status.progression.header";
    public static final String CMD_STATUS_ELDER_GUARDIAN = "cmd.dimtr.status.elder.guardian";
    public static final String CMD_STATUS_RAID_WON = "cmd.dimtr.status.raid.won";
    public static final String CMD_STATUS_TRIAL_VAULT = "cmd.dimtr.status.trial.vault";
    public static final String CMD_STATUS_VOLUNTARY_EXILE = "cmd.dimtr.status.voluntary.exile";
    public static final String CMD_STATUS_WITHER_KILLED = "cmd.dimtr.status.wither.killed";
    public static final String CMD_STATUS_WARDEN_KILLED = "cmd.dimtr.status.warden.killed";
    public static final String CMD_STATUS_MOB_KILL_FORMAT = "cmd.dimtr.status.mob.kill.format";
    
    // DimTr debug system hardcoded strings
    public static final String CMD_DEBUG_PHASE_STATUS_FORMAT = "cmd.dimtr.debug.phase.status.format";
    public static final String CMD_DEBUG_ERROR_CREATING_PAYLOAD = "cmd.dimtr.debug.error.creating.payload";
    public static final String CMD_DEBUG_SYSTEM_TITLE = "cmd.dimtr.debug.system.title";
    public static final String CMD_DEBUG_CONFIGURATIONS_ACTIVE = "cmd.dimtr.debug.configurations.active";
    public static final String CMD_DEBUG_PHASE1_STATUS = "cmd.dimtr.debug.phase1.status";
    public static final String CMD_DEBUG_PHASE2_STATUS = "cmd.dimtr.debug.phase2.status";
    public static final String CMD_DEBUG_VOLUNTARY_EXILE_STATUS = "cmd.dimtr.debug.voluntary.exile.status";
    public static final String CMD_DEBUG_ONLINE_PLAYERS_HEADER = "cmd.dimtr.debug.online.players.header";
    public static final String CMD_DEBUG_NO_ONLINE_PLAYERS = "cmd.dimtr.debug.no.online.players";
    public static final String CMD_DEBUG_PLAYER_FORMAT = "cmd.dimtr.debug.player.format";
    public static final String CMD_DEBUG_MULTIPLIERS_HEADER = "cmd.dimtr.debug.multipliers.header";
    public static final String CMD_DEBUG_PLAYER_MULTIPLIER_INDIVIDUAL = "cmd.dimtr.debug.player.multiplier.individual";
    public static final String CMD_DEBUG_PLAYER_MULTIPLIER_NEARBY = "cmd.dimtr.debug.player.multiplier.nearby";
    public static final String CMD_DEBUG_PLAYER_MULTIPLIER_HEADER = "cmd.dimtr.debug.player.multiplier.header";
    
    // Status values constants
    public static final String STATUS_ENABLED = "status.dimtr.enabled";
    public static final String STATUS_DISABLED = "status.dimtr.disabled";
    public static final String STATUS_REQUIRED = "status.dimtr.required";
    public static final String STATUS_OPTIONAL = "status.dimtr.optional";
    
    // Goal status emojis and messages
    public static final String GOAL_STATUS_ELDER_GUARDIAN = "goal.dimtr.elder.guardian";
    public static final String GOAL_STATUS_RAID_WON = "goal.dimtr.raid.won";
    public static final String GOAL_STATUS_TRIAL_VAULT = "goal.dimtr.trial.vault";
    public static final String GOAL_STATUS_VOLUNTARY_EXILE = "goal.dimtr.voluntary.exile";
    public static final String GOAL_STATUS_WITHER_KILLED = "goal.dimtr.wither.killed";
    public static final String GOAL_STATUS_WARDEN_KILLED = "goal.dimtr.warden.killed";
    
    // Reset type descriptions
    public static final String RESET_TYPE_ALL = "reset.dimtr.type.all";
    public static final String RESET_TYPE_PHASE1 = "reset.dimtr.type.phase1";
    public static final String RESET_TYPE_PHASE2 = "reset.dimtr.type.phase2";
    public static final String RESET_TYPE_MOB_KILLS = "reset.dimtr.type.mob.kills";
    
    // Self-target pronouns (Portuguese)
    public static final String PRONOUN_YOU = "pronoun.dimtr.you";
    public static final String PRONOUN_YOUR_MASC = "pronoun.dimtr.your.masc";
    public static final String PRONOUN_YOUR_FEM = "pronoun.dimtr.your.fem";
    public static final String PRONOUN_YOUR_PLURAL = "pronoun.dimtr.your.plural";
    
    // ============================================================================
    // 🎯 HARDCODED STRINGS TO MIGRATE - PARTY COMMANDS
    // ============================================================================
    
    // Party creation messages (unique names to avoid duplicates)
    public static final String PARTY_CREATE_SUCCESS_FORMAT_NEW = "party.dimtr.create.success.format.new";
    public static final String PARTY_CREATE_TYPE_PUBLIC = "party.dimtr.create.type.public";
    public static final String PARTY_CREATE_TYPE_PRIVATE = "party.dimtr.create.type.private";
    public static final String PARTY_CREATE_LEADER_NOTIFICATION = "party.dimtr.create.leader.notification";
    public static final String PARTY_CREATE_JOIN_INFO_PUBLIC = "party.dimtr.create.join.info.public";
    public static final String PARTY_CREATE_PASSWORD_INFO_NEW = "party.dimtr.create.password.info.new";
    public static final String PARTY_CREATE_MULTIPLIER_INFO = "party.dimtr.create.multiplier.info";
    
    // Party join messages
    public static final String PARTY_JOIN_SUCCESS_FORMAT = "party.dimtr.join.success.format";
    public static final String PARTY_JOIN_PROGRESS_SHARING_INFO = "party.dimtr.join.progress.sharing.info";
    // Party error messages (migrating hardcoded ones)
    public static final String PARTY_ERROR_COMMAND_PLAYER_ONLY = "party.dimtr.error.command.player.only";
    public static final String PARTY_ERROR_INVALID_NAME_LENGTH_RANGE = "party.dimtr.error.invalid.name.length.range";
    public static final String PARTY_ERROR_NAME_ALREADY_EXISTS_SIMPLE = "party.dimtr.error.name.already.exists.simple";
    public static final String PARTY_ERROR_UNKNOWN_CREATE_ERROR = "party.dimtr.error.unknown.create.error";
    
    // Additional missing constants for hardcoded strings in DimTrCommands
    public static final String CMD_INVALID_MOB_FORMAT = "cmd.dimtr.invalid.mob.format";
    public static final String CMD_GOAL_SET_SUCCESS = "cmd.dimtr.goal.set.success";
    public static final String CMD_MOB_COUNT_SET_SUCCESS = "cmd.dimtr.mob.count.set.success";
    
    // Reset type descriptions in English (for reset functionality)
    public static final String RESET_TYPE_ALL_ENG = "reset.dimtr.type.all.eng";
    public static final String RESET_TYPE_PHASE1_ENG = "reset.dimtr.type.phase1.eng";
    public static final String RESET_TYPE_PHASE2_ENG = "reset.dimtr.type.phase2.eng";
    public static final String RESET_TYPE_MOB_KILLS_ENG = "reset.dimtr.type.mob.kills.eng";
    public static final String RESET_TYPE_DEFAULT_ENG = "reset.dimtr.type.default.eng";
}