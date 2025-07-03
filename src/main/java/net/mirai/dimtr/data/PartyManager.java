package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.network.UpdatePartyToClientPayload;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerenciador centralizado para parties/grupos
 */
public class PartyManager extends SavedData {

    private static final String DATA_NAME = "dimtr_party_manager";

    // Dados das parties
    private final Map<UUID, PartyData> parties = new HashMap<>();
    private final Map<UUID, UUID> playerToParty = new HashMap<>(); // PlayerID -> PartyID

    // Contexto do servidor
    private MinecraftServer serverForContext;

    public PartyManager() {
    }

    public PartyManager(MinecraftServer server) {
        this.serverForContext = server;
    }

    public static PartyManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        () -> new PartyManager(level.getServer()),
                        (tag, registries) -> load(tag, registries, level.getServer())
                ),
                DATA_NAME
        );
    }

    private static PartyManager load(CompoundTag tag, HolderLookup.Provider registries, MinecraftServer server) {
        PartyManager manager = new PartyManager(server);
        manager.loadData(tag, registries);
        return manager;
    }

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        // Salvar parties
        ListTag partiesList = new ListTag();
        for (PartyData party : parties.values()) {
            partiesList.add(party.save(registries));
        }
        tag.put("parties", partiesList);

        // Salvar mapeamento jogador -> party
        CompoundTag playerMappingTag = new CompoundTag();
        for (Map.Entry<UUID, UUID> entry : playerToParty.entrySet()) {
            playerMappingTag.putUUID(entry.getKey().toString(), entry.getValue());
        }
        tag.put("playerToParty", playerMappingTag);

        return tag;
    }

    private void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        parties.clear();
        playerToParty.clear();

        // Carregar parties
        if (tag.contains("parties", Tag.TAG_LIST)) {
            ListTag partiesList = tag.getList("parties", Tag.TAG_COMPOUND);
            for (Tag partyTag : partiesList) {
                if (partyTag instanceof CompoundTag partyCompound) {
                    PartyData partyData = PartyData.load(partyCompound, registries);
                    parties.put(partyData.getPartyId(), partyData);
                }
            }
        }

        // Carregar mapeamento jogador -> party
        if (tag.contains("playerToParty", Tag.TAG_COMPOUND)) {
            CompoundTag playerMappingTag = tag.getCompound("playerToParty");
            for (String playerIdStr : playerMappingTag.getAllKeys()) {
                UUID playerId = UUID.fromString(playerIdStr);
                UUID partyId = playerMappingTag.getUUID(playerIdStr);
                playerToParty.put(playerId, partyId);
            }
        }
    }

    // ============================================================================
    // 🎯 MÉTODOS PRINCIPAIS DE GERENCIAMENTO DE PARTIES
    // ============================================================================

    /**
     * Criar nova party
     */
    public CreatePartyResult createParty(UUID leaderId, String partyName, String password) {
        // Verificar se jogador já está em uma party
        if (isPlayerInParty(leaderId)) {
            return CreatePartyResult.ALREADY_IN_PARTY;
        }

        // Validar nome da party
        if (partyName == null || partyName.trim().isEmpty() || partyName.length() > 20) {
            return CreatePartyResult.INVALID_NAME;
        }

        // Verificar se nome já existe
        boolean nameExists = parties.values().stream()
                .anyMatch(party -> party.getName().equalsIgnoreCase(partyName.trim()));
        if (nameExists) {
            return CreatePartyResult.NAME_TAKEN;
        }

        // Criar party
        UUID partyId = UUID.randomUUID();
        PartyData newParty = new PartyData(partyId, partyName.trim(), password, leaderId);

        // 🎯 CORREÇÃO: Transferir progresso individual do líder para a party
        if (serverForContext != null) {
            ServerLevel overworldLevel = serverForContext.getLevel(Level.OVERWORLD);
            if (overworldLevel != null) {
                ProgressionManager progressionManager = ProgressionManager.get(overworldLevel);
                if (progressionManager != null) {
                    // Obter progresso individual do líder
                    Map<String, Integer> leaderProgress = progressionManager.getPlayerMobKills(leaderId);
                    PlayerProgressionData leaderData = progressionManager.getPlayerData(leaderId);
                    
                    // Transferir progresso de mobs
                    newParty.transferIndividualProgress(leaderId, leaderProgress);
                    
                    // Transferir objetivos especiais
                    transferSpecialObjectives(newParty, leaderData);
                }
            }
        }

        parties.put(partyId, newParty);
        playerToParty.put(leaderId, partyId);

        setDirty();
        syncPartyToMembers(partyId);

        return CreatePartyResult.SUCCESS;
    }

    /**
     * Entrar em party existente
     */
    public JoinPartyResult joinParty(UUID playerId, String partyName, String password) {
        // Verificar se jogador já está em uma party
        if (isPlayerInParty(playerId)) {
            return JoinPartyResult.ALREADY_IN_PARTY;
        }

        // Encontrar party por nome
        PartyData targetParty = parties.values().stream()
                .filter(party -> party.getName().equalsIgnoreCase(partyName))
                .findFirst()
                .orElse(null);

        if (targetParty == null) {
            return JoinPartyResult.PARTY_NOT_FOUND;
        }

        // Verificar senha
        if (!targetParty.checkPassword(password)) {
            return JoinPartyResult.WRONG_PASSWORD;
        }

        // Verificar se party está cheia
        if (!targetParty.addMember(playerId)) {
            return JoinPartyResult.PARTY_FULL;
        }

        playerToParty.put(playerId, targetParty.getPartyId());
        
        // 🔧 CORREÇÃO CRÍTICA: Usar ProgressTransferService para transferência robusta
        if (serverForContext != null) {
            ServerLevel overworldLevel = serverForContext.getLevel(Level.OVERWORLD);
            if (overworldLevel != null) {
                try {
                    // Transferir progresso individual para party usando o serviço especializado
                    boolean transferSuccess = net.mirai.dimtr.system.ProgressTransferService.transferFromIndividualToParty(
                        playerId, 
                        net.mirai.dimtr.system.ProgressTransferService.TransferConfig.getDefault(),
                        "Player joined party: " + targetParty.getName());
                    
                    if (transferSuccess) {
                        DimTrMod.LOGGER.info("✅ Successfully transferred individual progress to party for player {}", playerId);
                    } else {
                        DimTrMod.LOGGER.warn("⚠️ Failed to transfer progress to party for player {} (player may not have been in individual mode)", playerId);
                    }
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("❌ Exception during progress transfer to party for player {}: {}", playerId, e.getMessage());
                    // Continuar mesmo se a transferência falhar - o jogador ainda pode entrar na party
                }
                
                // 🔧 CORREÇÃO CRÍTICA: Sincronizar todos os membros da party após entrada
                for (UUID memberId : targetParty.getMembers()) {
                    ServerPlayer member = serverForContext.getPlayerList().getPlayer(memberId);
                    if (member != null) {
                        // ✅ CORREÇÃO: Enviar dados de progressão DA PARTY
                        sendPartyProgressionToClient(member);
                    }
                }
            }
        }

        setDirty();
        syncPartyToMembers(targetParty.getPartyId());

        return JoinPartyResult.SUCCESS;
    }

    /**
     * Sair da party
     */
    public LeavePartyResult leaveParty(UUID playerId) {
        UUID partyId = playerToParty.get(playerId);
        if (partyId == null) {
            return LeavePartyResult.NOT_IN_PARTY;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return LeavePartyResult.NOT_IN_PARTY;
        }

        // 🔧 CORREÇÃO CRÍTICA: Usar ProgressTransferService para transferência robusta de volta ao individual
        if (serverForContext != null) {
            try {
                boolean transferSuccess = net.mirai.dimtr.system.ProgressTransferService.transferFromPartyToIndividual(
                    playerId,
                    net.mirai.dimtr.system.ProgressTransferService.TransferConfig.getDefault(),
                    "Player left party: " + party.getName());
                
                if (transferSuccess) {
                    DimTrMod.LOGGER.info("✅ Successfully transferred party progress to individual for player {}", playerId);
                } else {
                    DimTrMod.LOGGER.warn("⚠️ Failed to transfer progress from party to individual for player {} (player may not have been in party mode)", playerId);
                }
            } catch (Exception e) {
                DimTrMod.LOGGER.error("❌ Exception during progress transfer from party for player {}: {}", playerId, e.getMessage());
                // Continuar mesmo se a transferência falhar - o jogador ainda pode sair da party
            }
            
            // 🔧 CORREÇÃO CRÍTICA: Enviar dados vazios para limpar o HUD do cliente
            ServerPlayer leavingPlayer = serverForContext.getPlayerList().getPlayer(playerId);
            if (leavingPlayer != null) {
                sendEmptyPartyDataToClient(leavingPlayer);
                
                // ✅ CORREÇÃO: Enviar dados de progressão INDIVIDUAL atualizados
                ServerLevel overworldLevel = serverForContext.getLevel(Level.OVERWORLD);
                if (overworldLevel != null) {
                    ProgressionManager progressionManager = ProgressionManager.get(overworldLevel);
                    progressionManager.sendToClient(leavingPlayer); // OK aqui, pois saiu da party
                }
            }
        }

        // Remover jogador da party
        party.removeMember(playerId);
        playerToParty.remove(playerId);

        // Se a party ficou vazia, deletar
        if (party.getMemberCount() == 0) {
            parties.remove(partyId);
        } else {
            // 🔧 CORREÇÃO CRÍTICA: Sincronizar com os membros restantes da party após saída
            if (serverForContext != null) {
                // ✅ CORREÇÃO: Sincronizar membros restantes com dados DA PARTY
                for (UUID memberId : party.getMembers()) {
                    ServerPlayer member = serverForContext.getPlayerList().getPlayer(memberId);
                    if (member != null) {
                        sendPartyProgressionToClient(member);
                    }
                }
            }
            syncPartyToMembers(partyId);
        }

        setDirty();

        return LeavePartyResult.SUCCESS;
    }

    /**
     * Listar parties públicas
     */
    public List<PartyInfo> getPublicParties() {
        return parties.values().stream()
                .filter(PartyData::isPublic)
                .map(party -> new PartyInfo(
                        party.getName(),
                        party.getMemberCount(),
                        10, // máximo de membros
                        party.isPublic()
                ))
                .collect(Collectors.toList());
    }

    // ============================================================================
    // 🎯 MÉTODOS DE INTEGRAÇÃO COM PROGRESSÃO
    // ============================================================================

    /**
     * Processar kill de mob para party (se aplicável)
     */
    public boolean processPartyMobKill(UUID playerId, String mobType) {
        UUID partyId = playerToParty.get(playerId);
        if (partyId == null) {
            return false;
        }

        PartyData party = parties.get(partyId);
        if (party == null) {
            return false;
        }

        // 🎯 NOVO: Incrementar kill compartilhado e registrar contribuição individual
        boolean updated = party.incrementSharedMobKillWithContribution(mobType, playerId);

        if (updated) {
            setDirty();
            
            // 🎆 NOVO: Verificar se completou alguma fase e lançar fogos de artifício
            boolean wasPhase1Complete = party.isPhase1SharedCompleted();
            boolean wasPhase2Complete = party.isPhase2SharedCompleted();
            
            // Verificar se Phase 1 foi completada pela primeira vez
            if (!wasPhase1Complete && party.isPhase1Complete()) {
                party.setPhase1SharedCompleted(true);
                
                // 🎊 Lançar celebração de party épica para todos os membros
                if (serverForContext != null) {
                    java.util.List<ServerPlayer> onlineMembers = new java.util.ArrayList<>();
                    for (UUID memberId : party.getMembers()) {
                        ServerPlayer member = serverForContext.getPlayerList().getPlayer(memberId);
                        if (member != null && member.level() != null) {
                            onlineMembers.add(member);
                        }
                    }
                    
                    if (!onlineMembers.isEmpty()) {
                        net.mirai.dimtr.util.NotificationHelper.launchPartyCelebrationFireworks(onlineMembers, 1);
                        DimTrMod.LOGGER.info("🎉 [PARTY] Phase 1 completed! Launched party celebration for {} members", 
                            onlineMembers.size());
                    }
                }
            }
            
            // Verificar se Phase 2 foi completada pela primeira vez
            if (!wasPhase2Complete && party.isPhase2Complete()) {
                party.setPhase2SharedCompleted(true);
                
                // 🎊 Lançar celebração de party épica para todos os membros
                if (serverForContext != null) {
                    java.util.List<ServerPlayer> onlineMembers = new java.util.ArrayList<>();
                    for (UUID memberId : party.getMembers()) {
                        ServerPlayer member = serverForContext.getPlayerList().getPlayer(memberId);
                        if (member != null && member.level() != null) {
                            onlineMembers.add(member);
                        }
                    }
                    
                    if (!onlineMembers.isEmpty()) {
                        net.mirai.dimtr.util.NotificationHelper.launchPartyCelebrationFireworks(onlineMembers, 2);
                        DimTrMod.LOGGER.info("🎉 [PARTY] Phase 2 completed! Launched party celebration for {} members", 
                            onlineMembers.size());
                    }
                }
            }
            
            syncPartyToMembers(partyId);
        }

        return updated;
    }

    /**
     * Processar objetivo especial para party
     */
    public boolean processPartySpecialObjective(UUID playerId, String objectiveType) {
        UUID partyId = playerToParty.get(playerId);
        if (partyId == null) return false;

        PartyData party = parties.get(partyId);
        if (party == null) return false;

        boolean updated = false;

        switch (objectiveType.toLowerCase()) {
            case "elder_guardian" -> {
                if (!party.isSharedElderGuardianKilled()) {
                    party.setSharedElderGuardianKilled(true);
                    updated = true;
                }
            }
            case "raid" -> {
                if (!party.isSharedRaidWon()) {
                    party.setSharedRaidWon(true);
                    updated = true;
                }
            }
            case "trial_vault" -> {
                if (!party.isSharedTrialVaultAdvancementEarned()) {
                    party.setSharedTrialVaultAdvancementEarned(true);
                    updated = true;
                }
            }
            case "voluntary_exile" -> {
                if (!party.isSharedVoluntaireExileAdvancementEarned()) {
                    party.setSharedVoluntaireExileAdvancementEarned(true);
                    updated = true;
                }
            }
            case "wither" -> {
                if (!party.isSharedWitherKilled()) {
                    party.setSharedWitherKilled(true);
                    updated = true;
                }
            }
            case "warden" -> {
                if (!party.isSharedWardenKilled()) {
                    party.setSharedWardenKilled(true);
                    updated = true;
                }
            }
        }

        if (updated) {
            setDirty();
            syncPartyToMembers(partyId);

            // Atualizar progressão individual de todos os membros
            ProgressionManager progressionManager = ProgressionManager.get(
                    (ServerLevel) serverForContext.overworld());

            for (UUID memberId : party.getMembers()) {
                switch (objectiveType.toLowerCase()) {
                    case "elder_guardian" -> progressionManager.updateElderGuardianKilled(memberId);
                    case "raid" -> progressionManager.updateRaidWon(memberId);
                    case "trial_vault" -> progressionManager.updateTrialVaultAdvancementEarned(memberId);
                    case "voluntary_exile" -> progressionManager.updateVoluntaireExileAdvancementEarned(memberId);
                    case "wither" -> progressionManager.updateWitherKilled(memberId);
                    case "warden" -> progressionManager.updateWardenKilled(memberId);
                }
            }
        }

        return updated;
    }

    // ============================================================================
    // 🎯 MÉTODOS AUXILIARES
    // ============================================================================

    public boolean isPlayerInParty(UUID playerId) {
        return playerToParty.containsKey(playerId);
    }

    public PartyData getPlayerParty(UUID playerId) {
        UUID partyId = playerToParty.get(playerId);
        return partyId != null ? parties.get(partyId) : null;
    }

    public int getRequiredMobKills(UUID playerId, String mobType, int baseRequirement) {
        PartyData party = getPlayerParty(playerId);
        if (party == null) return baseRequirement;

        double multiplier = party.getRequirementMultiplier();
        return (int) Math.ceil(baseRequirement * multiplier);
    }

    private void syncPartyToMembers(UUID partyId) {
        PartyData party = parties.get(partyId);
        if (party == null || serverForContext == null) return;

        // Criar payload com dados da party
        UpdatePartyToClientPayload payload = createPartyPayload(party);

        // Enviar para todos os membros da party
        for (UUID memberId : party.getMembers()) {
            ServerPlayer player = serverForContext.getPlayerList().getPlayer(memberId);
            if (player != null) {
                PacketDistributor.sendToPlayer(player, payload);
            }
        }
    }

    /**
     * Enviar dados da party para um jogador específico
     * @param player O jogador para receber os dados da party
     */
    public void sendPartyToClient(ServerPlayer player) {
        if (player == null || serverForContext == null) return;
        
        UUID playerId = player.getUUID();
        if (isPlayerInParty(playerId)) {
            UUID partyId = playerToParty.get(playerId);
            PartyData party = parties.get(partyId);
            
            if (party != null) {
                // Criar payload com dados da party
                UpdatePartyToClientPayload payload = createPartyPayload(party);
                PacketDistributor.sendToPlayer(player, payload);
            }
        } else {
            // Se o jogador não está em uma party, enviar dados vazios para limpar o cliente
            sendEmptyPartyDataToClient(player);
        }
    }

    /**
     * 🔧 CORREÇÃO CRÍTICA: Enviar dados de progressão da party para o cliente
     * Este método deve ser usado em vez de ProgressionManager.sendToClient() quando o jogador está em party
     */
    public void sendPartyProgressionToClient(ServerPlayer player) {
        if (player == null || serverForContext == null) return;
        
        UUID playerId = player.getUUID();
        if (isPlayerInParty(playerId)) {
            UUID partyId = playerToParty.get(playerId);
            PartyData party = parties.get(partyId);
            
            if (party != null) {
                // Criar payload com dados de progressão DA PARTY
                UpdateProgressionToClientPayload partyProgressPayload = createPartyProgressionPayload(party);
                PacketDistributor.sendToPlayer(player, partyProgressPayload);
                
                DimTrMod.LOGGER.debug("📡 Sent party progression data to client: {} (party: {})", 
                    player.getGameProfile().getName(), party.getName());
            }
        }
    }

    /**
     * Criar payload de progressão a partir dos dados da party
     */
    private UpdateProgressionToClientPayload createPartyProgressionPayload(PartyData party) {
        // Usar os dados COMPARTILHADOS da party ao invés dos dados individuais
        return new UpdateProgressionToClientPayload(
            // Objetivos da party
            party.isSharedElderGuardianKilled(),
            party.isSharedRaidWon(),
            false, // ravagerKilled - não usado
            false, // evokerKilled - não usado
            party.isSharedTrialVaultAdvancementEarned(),
            party.isSharedVoluntaireExileAdvancementEarned(),
            party.isPhase1SharedCompleted(),
            party.isSharedWitherKilled(),
            party.isSharedWardenKilled(),
            party.isPhase2SharedCompleted(),
            
            // Contadores de mobs da party - Fase 1
            party.getSharedMobKills().getOrDefault("zombie", 0),
            0, // zombieVillagerKills - sempre 0
            party.getSharedMobKills().getOrDefault("skeleton", 0),
            party.getSharedMobKills().getOrDefault("stray", 0),
            party.getSharedMobKills().getOrDefault("husk", 0),
            party.getSharedMobKills().getOrDefault("spider", 0),
            party.getSharedMobKills().getOrDefault("creeper", 0),
            party.getSharedMobKills().getOrDefault("drowned", 0),
            party.getSharedMobKills().getOrDefault("enderman", 0),
            party.getSharedMobKills().getOrDefault("witch", 0),
            party.getSharedMobKills().getOrDefault("pillager", 0),
            party.getSharedMobKills().getOrDefault("captain", 0),
            party.getSharedMobKills().getOrDefault("vindicator", 0),
            party.getSharedMobKills().getOrDefault("bogged", 0),
            party.getSharedMobKills().getOrDefault("breeze", 0),
            party.getSharedMobKills().getOrDefault("ravager", 0),
            party.getSharedMobKills().getOrDefault("evoker", 0),
            
            // Contadores de mobs da party - Fase 2
            party.getSharedMobKills().getOrDefault("blaze", 0),
            party.getSharedMobKills().getOrDefault("wither_skeleton", 0),
            party.getSharedMobKills().getOrDefault("piglin_brute", 0),
            party.getSharedMobKills().getOrDefault("hoglin", 0),
            party.getSharedMobKills().getOrDefault("zoglin", 0),
            party.getSharedMobKills().getOrDefault("ghast", 0),
            0, // endermiteKills - sempre 0
            party.getSharedMobKills().getOrDefault("piglin", 0),
            
            // Requisitos de mobs (mesmo valores para todos)
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqZombieKills.get(),
            0, // reqZombieVillagerKills - sempre 0
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqSkeletonKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqStrayKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqHuskKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqSpiderKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqCreeperKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqDrownedKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqEndermanKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqWitchKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqPillagerKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqCaptainKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqVindicatorKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqBoggedKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqBreezeKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqRavagerKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqEvokerKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqBlazeKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqWitherSkeletonKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqPiglinBruteKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqHoglinKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqZoglinKills.get(),
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqGhastKills.get(),
            0, // reqEndermiteKills - sempre 0
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqPiglinKills.get(),
            
            // Configurações
            net.mirai.dimtr.config.DimTrConfig.SERVER.reqVoluntaryExile.get(),
            
            // Dados de custom phases da party
            party.getSharedCustomPhaseCompletion(),
            party.getSharedCustomMobKills(),
            party.getSharedCustomObjectiveCompletion()
        );
    }

    private UpdatePartyToClientPayload createPartyPayload(PartyData party) {
        return new UpdatePartyToClientPayload(
                party.getPartyId(),
                party.getName(),
                party.isPublic(), // 🎯 ADICIONADO: isPublic field
                party.getLeaderId(),
                new ArrayList<>(party.getMembers()),
                party.getRequirementMultiplier(),
                // 🔧 ADICIONADO: Campos que estavam faltando
                party.getMemberCount(),
                party.getSharedMobKills(),
                party.isSharedElderGuardianKilled(),
                party.isSharedRaidWon(),
                party.isSharedTrialVaultAdvancementEarned(),
                party.isSharedVoluntaireExileAdvancementEarned(),
                party.isSharedWitherKilled(),
                party.isSharedWardenKilled(),
                party.isPhase1SharedCompleted(),
                party.isPhase2SharedCompleted(),
                // 🎯 NOVO: Custom Phases data
                party.getSharedCustomPhaseCompletion(),
                party.getSharedCustomMobKills(),
                party.getSharedCustomObjectiveCompletion()
        );
    }

    // ✅ NOVO: Método para enviar dados vazios para limpar party do cliente
    private void sendEmptyPartyDataToClient(ServerPlayer player) {
        UpdatePartyToClientPayload emptyPayload = new UpdatePartyToClientPayload(
                null, // partyId
                "", // partyName  
                true, // isPublic (default)
                null, // leaderId
                new ArrayList<>(), // members (vazio)
                1.0, // progressionMultiplier (default)
                0, // memberCount
                new HashMap<>(), // sharedMobKills (vazio)
                false, false, false, false, false, false, false, false, // todos objetivos false
                // 🎯 NOVO: Custom Phases data vazios
                new HashMap<>(), // sharedCustomPhaseCompletion (vazio)
                new HashMap<>(), // sharedCustomMobKills (vazio)
                new HashMap<>() // sharedCustomObjectiveCompletion (vazio)
        );
        
        PacketDistributor.sendToPlayer(player, emptyPayload);
    }

    // ============================================================================
    // 🎯 MÉTODOS AUXILIARES PARA TRANSFERÊNCIA DE PROGRESSO
    // ============================================================================

    /**
     * Transferir objetivos especiais do jogador para a party
     */
    private void transferSpecialObjectives(PartyData party, PlayerProgressionData playerData) {
        // Fase 1 objectives
        if (playerData.elderGuardianKilled) {
            party.setSharedElderGuardianKilled(true);
        }
        if (playerData.raidWon) {
            party.setSharedRaidWon(true);
        }
        if (playerData.trialVaultAdvancementEarned) {
            party.setSharedTrialVaultAdvancementEarned(true);
        }
        if (playerData.voluntaireExileAdvancementEarned) {
            party.setSharedVoluntaireExileAdvancementEarned(true);
        }
        
        // Fase 2 objectives
        if (playerData.witherKilled) {
            party.setSharedWitherKilled(true);
        }
        if (playerData.wardenKilled) {
            party.setSharedWardenKilled(true);
        }
        
        // Verificar se as fases devem ser marcadas como completas
        if (playerData.phase1Completed) {
            party.setPhase1SharedCompleted(true);
        }
        if (playerData.phase2Completed) {
            party.setPhase2SharedCompleted(true);
        }
        
        // 🎯 NOVO: Transferir Custom Phases
        party.transferCustomProgressFromPlayer(playerData);
    }

    /**
     * 🎯 NOVO: Transferir objetivos especiais de volta ao jogador que sai
     */
    private void restoreSpecialObjectivesToPlayer(UUID playerId, PartyData party) {
        if (serverForContext != null) {
            ServerLevel overworldLevel = serverForContext.getLevel(Level.OVERWORLD);
            if (overworldLevel != null) {
                ProgressionManager progressionManager = ProgressionManager.get(overworldLevel);
                if (progressionManager != null) {
                    PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
                    
                    // Restaurar objetivos especiais usando métodos públicos
                    // LÓGICA: Se a party tem o objetivo, todos os membros devem tê-lo ao sair
                    if (party.isSharedElderGuardianKilled()) {
                        progressionManager.updateElderGuardianKilled(playerId);
                    }
                    if (party.isSharedRaidWon()) {
                        progressionManager.updateRaidWon(playerId);
                    }
                    if (party.isSharedTrialVaultAdvancementEarned()) {
                        progressionManager.updateTrialVaultAdvancementEarned(playerId);
                    }
                    if (party.isSharedVoluntaireExileAdvancementEarned()) {
                        progressionManager.updateVoluntaireExileAdvancementEarned(playerId);
                    }
                    if (party.isSharedWitherKilled()) {
                        progressionManager.updateWitherKilled(playerId);
                    }
                    if (party.isSharedWardenKilled()) {
                        progressionManager.updateWardenKilled(playerId);
                    }
                    
                    // 🎯 NOVO: Restaurar Custom Phases
                    party.restoreCustomProgressToPlayer(playerData);
                }
            }
        }
    }

    // ============================================================================
    // 🎯 ENUMS DE RESULTADO
    // ============================================================================

    public enum CreatePartyResult {
        SUCCESS,
        ALREADY_IN_PARTY,
        INVALID_NAME,
        NAME_TAKEN
    }

    public enum JoinPartyResult {
        SUCCESS,
        ALREADY_IN_PARTY,
        PARTY_NOT_FOUND,
        WRONG_PASSWORD,
        PARTY_FULL
    }

    public enum LeavePartyResult {
        SUCCESS,
        NOT_IN_PARTY
    }

    /**
     * Info pública de uma party
     */
    public static class PartyInfo {
        public final String name;
        public final int currentMembers;
        public final int maxMembers;
        public final boolean isPublic;

        public PartyInfo(String name, int currentMembers, int maxMembers, boolean isPublic) {
            this.name = name;
            this.currentMembers = currentMembers;
            this.maxMembers = maxMembers;
            this.isPublic = isPublic;
        }
    }

    /**
     * Serializa os dados de party para um backup
     * @return CompoundTag contendo todos os dados serializados
     */
    public CompoundTag serializeForBackup() {
        CompoundTag root = new CompoundTag();
        
        // Serializar dados de todas as parties
        CompoundTag partiesTag = new CompoundTag();
        for (Map.Entry<UUID, PartyData> entry : parties.entrySet()) {
            UUID partyId = entry.getKey();
            PartyData data = entry.getValue();
            
            CompoundTag partyTag = new CompoundTag();
            // Usar o método save com um HolderLookup nulo (para backup não precisamos)
            partyTag = data.save(null);
            partiesTag.put(partyId.toString(), partyTag);
        }
        
        root.put("parties", partiesTag);
        
        // Serializar mapeamento jogador -> party
        CompoundTag playerMappingTag = new CompoundTag();
        for (Map.Entry<UUID, UUID> entry : playerToParty.entrySet()) {
            playerMappingTag.putUUID(entry.getKey().toString(), entry.getValue());
        }
        root.put("playerToParty", playerMappingTag);
        
        root.putLong("backupTimestamp", System.currentTimeMillis());
        
        return root;
    }
    
    /**
     * Restaura os dados de parties a partir de um backup
     * @param backupTag Tag contendo os dados do backup
     */
    public void deserializeFromBackup(CompoundTag backupTag) {
        if (!backupTag.contains("parties") || !backupTag.contains("playerToParty")) {
            net.mirai.dimtr.DimTrMod.LOGGER.error("Dados de backup de parties inválidos: tags necessárias não encontradas");
            return;
        }
        
        // Restaurar parties
        CompoundTag partiesTag = backupTag.getCompound("parties");
        Map<UUID, PartyData> restoredParties = new HashMap<>();
        
        for (String partyIdStr : partiesTag.getAllKeys()) {
            try {
                UUID partyId = UUID.fromString(partyIdStr);
                CompoundTag partyTag = partiesTag.getCompound(partyIdStr);
                
                // Criar party vazia e preencher com dados do backup
                PartyData data = new PartyData(partyId, "", "", null);
                // Corrigir para usar o método correto com um HolderLookup nulo
                data = PartyData.load(partyTag, null);
                
                restoredParties.put(partyId, data);
            } catch (IllegalArgumentException e) {
                net.mirai.dimtr.DimTrMod.LOGGER.warn("UUID de party inválido no backup: {}", partyIdStr);
            }
        }
        
        // Restaurar mapeamento jogador -> party
        CompoundTag playerMappingTag = backupTag.getCompound("playerToParty");
        Map<UUID, UUID> restoredMapping = new HashMap<>();
        
        for (String playerIdStr : playerMappingTag.getAllKeys()) {
            try {
                UUID playerId = UUID.fromString(playerIdStr);
                UUID partyId = playerMappingTag.getUUID(playerIdStr);
                
                // Só adicionar se a party existir
                if (restoredParties.containsKey(partyId)) {
                    restoredMapping.put(playerId, partyId);
                }
            } catch (IllegalArgumentException e) {
                net.mirai.dimtr.DimTrMod.LOGGER.warn("UUID de jogador inválido no backup: {}", playerIdStr);
            }
        }
        
        // Substituir dados atuais pelos restaurados
        this.parties.clear();
        this.parties.putAll(restoredParties);
        
        this.playerToParty.clear();
        this.playerToParty.putAll(restoredMapping);
        
        // Marcar como alterado para salvar
        this.setDirty();
        
        net.mirai.dimtr.DimTrMod.LOGGER.info("Dados de parties restaurados: {} parties e {} jogadores", 
            restoredParties.size(), restoredMapping.size());
    }
}