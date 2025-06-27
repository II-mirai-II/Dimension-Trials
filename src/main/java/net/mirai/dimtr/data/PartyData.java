package net.mirai.dimtr.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.*;

/**
 * Dados de uma party/grupo - VERSÃO EXPANDIDA
 */
public class PartyData {
    private final UUID partyId;
    private String name;
    private String password; // null se pública
    private boolean isPublic; // true = pública, false = privada
    private UUID leaderId;
    private final Set<UUID> members;
    private final Map<String, Integer> sharedMobKills;
    
    // 🎯 NOVO: Rastreamento de contribuições individuais para preservar progresso
    private final Map<UUID, Map<String, Integer>> individualContributions;
    
    private boolean phase1SharedCompleted;
    private boolean phase2SharedCompleted;

    // Objetivos especiais compartilhados
    private boolean sharedElderGuardianKilled;
    private boolean sharedRaidWon;
    private boolean sharedTrialVaultAdvancementEarned;
    private boolean sharedVoluntaireExileAdvancementEarned;
    private boolean sharedWitherKilled;
    private boolean sharedWardenKilled;

    // 🎯 NOVO: Dados de fases customizadas compartilhadas
    private final Map<String, Boolean> sharedCustomPhaseCompletion;
    private final Map<String, Map<String, Integer>> sharedCustomMobKills; // phaseId -> mobType -> kills
    private final Map<String, Map<String, Boolean>> sharedCustomObjectiveCompletion; // phaseId -> objectiveId -> complete

    public PartyData(UUID partyId, String name, String password, UUID leaderId) {
        this.partyId = partyId;
        this.name = name;
        this.password = password;
        this.isPublic = (password == null); // Público se não tem senha
        this.leaderId = leaderId;
        this.members = new HashSet<>();
        this.sharedMobKills = new HashMap<>();
        this.individualContributions = new HashMap<>();
        this.sharedCustomPhaseCompletion = new HashMap<>();
        this.sharedCustomMobKills = new HashMap<>();
        this.sharedCustomObjectiveCompletion = new HashMap<>();
        this.members.add(leaderId); // Líder é sempre membro

        initializeMobKills();
    }

    private void initializeMobKills() {
        // Fase 1 mobs
        String[] phase1Mobs = {"zombie", "skeleton", "stray", "husk", "spider", "creeper",
                "drowned", "enderman", "witch", "pillager", "captain",
                "vindicator", "bogged", "breeze", "ravager", "evoker"};

        // Fase 2 mobs
        String[] phase2Mobs = {"blaze", "wither_skeleton", "piglin_brute", "hoglin",
                "zoglin", "ghast", "piglin"};

        for (String mob : phase1Mobs) {
            sharedMobKills.put(mob, 0);
        }
        for (String mob : phase2Mobs) {
            sharedMobKills.put(mob, 0);
        }
    }

    public boolean isPublic() {
        return isPublic && (password == null || password.isEmpty());
    }

    public boolean checkPassword(String inputPassword) {
        if (isPublic()) return true;
        return password.equals(inputPassword);
    }

    public boolean addMember(UUID playerId) {
        if (members.size() >= 10) return false; // Máximo 10 membros
        boolean added = members.add(playerId);
        if (added) {
            // Inicializar contribuições individuais para o novo membro
            individualContributions.put(playerId, new HashMap<>());
        }
        return added;
    }

    public boolean removeMember(UUID playerId) {
        if (leaderId.equals(playerId) && members.size() > 1) {
            // Transferir liderança para outro membro
            Optional<UUID> newLeader = members.stream()
                    .filter(id -> !id.equals(playerId))
                    .findFirst();
            if (newLeader.isPresent()) {
                leaderId = newLeader.get();
            }
        }
        
        boolean removed = members.remove(playerId);
        if (removed) {
            // Remover contribuições individuais do membro que saiu
            individualContributions.remove(playerId);
        }
        return removed;
    }

    /**
     * Calcular multiplicador de requisitos baseado no tamanho da party
     * CORREÇÃO: 75% adicional por membro além do primeiro (trabalho em equipe)
     * Exemplo: 2 membros = 175%, 3 membros = 250%, 4 membros = 325%
     */
    public double getRequirementMultiplier() {
        return 1.0 + (members.size() - 1) * 0.75;
    }

    /**
     * 🎯 CORREÇÃO: Calcular multiplicador de metas baseado no número de membros
     * Cada membro reduz requisitos proporcionalmente
     */
    public double getPartyMultiplier() {
        return getRequirementMultiplier(); // Usar mesma lógica corrigida
    }

    /**
     * 🎯 NOVO: Definir se a party é pública ou privada
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
        if (isPublic) {
            this.password = null; // Remove senha se tornar pública
        }
    }

    /**
     * 🎯 NOVO: Calcular requisito ajustado por multiplicador de party
     */
    public int getAdjustedRequirement(int baseRequirement) {
        return (int) Math.ceil(baseRequirement * getPartyMultiplier());
    }

    /**
     * Incrementar kill compartilhado
     */
    public boolean incrementSharedMobKill(String mobType) {
        if (sharedMobKills.containsKey(mobType)) {
            sharedMobKills.put(mobType, sharedMobKills.get(mobType) + 1);
            return true;
        }
        return false;
    }
    
    /**
     * 🎯 NOVO: Transferir progresso individual de um jogador para a party
     */
    public void transferIndividualProgress(UUID playerId, Map<String, Integer> playerProgress) {
        // Inicializar contribuições se não existir
        individualContributions.putIfAbsent(playerId, new HashMap<>());
        Map<String, Integer> contributions = individualContributions.get(playerId);
        
        // Para cada tipo de mob no progresso do jogador
        for (Map.Entry<String, Integer> entry : playerProgress.entrySet()) {
            String mobType = entry.getKey();
            int playerKills = entry.getValue();
            
            if (sharedMobKills.containsKey(mobType) && playerKills > 0) {
                // Somar ao progresso compartilhado
                sharedMobKills.put(mobType, sharedMobKills.get(mobType) + playerKills);
                
                // Registrar contribuição individual
                contributions.put(mobType, contributions.getOrDefault(mobType, 0) + playerKills);
            }
        }
    }
    
    /**
     * 🎯 NOVO: Remover contribuições individuais quando jogador sai da party
     */
    public Map<String, Integer> removeIndividualContributions(UUID playerId) {
        Map<String, Integer> contributions = individualContributions.get(playerId);
        if (contributions == null) {
            return new HashMap<>();
        }
        
        // Subtrair contribuições do progresso compartilhado
        for (Map.Entry<String, Integer> entry : contributions.entrySet()) {
            String mobType = entry.getKey();
            int contributedKills = entry.getValue();
            
            if (sharedMobKills.containsKey(mobType)) {
                int newSharedValue = Math.max(0, sharedMobKills.get(mobType) - contributedKills);
                sharedMobKills.put(mobType, newSharedValue);
            }
        }
        
        // Retornar contribuições para preservar progresso individual
        return new HashMap<>(contributions);
    }
    
    /**
     * 🎯 NOVO: Incrementar kill e registrar contribuição individual
     */
    public boolean incrementSharedMobKillWithContribution(String mobType, UUID contributorId) {
        if (sharedMobKills.containsKey(mobType)) {
            // Incrementar progresso compartilhado
            int oldValue = sharedMobKills.get(mobType);
            sharedMobKills.put(mobType, oldValue + 1);
            
            // Registrar contribuição individual
            individualContributions.putIfAbsent(contributorId, new HashMap<>());
            Map<String, Integer> contributions = individualContributions.get(contributorId);
            contributions.put(mobType, contributions.getOrDefault(mobType, 0) + 1);
            
            return true;
        }
        return false;
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("partyId", partyId);
        tag.putString("name", name);
        if (password != null) {
            tag.putString("password", password);
        }
        tag.putBoolean("isPublic", isPublic); // 🎯 NOVO: Salvar campo isPublic
        tag.putUUID("leaderId", leaderId);

        // Salvar membros
        ListTag membersList = new ListTag();
        for (UUID member : members) {
            membersList.add(StringTag.valueOf(member.toString()));
        }
        tag.put("members", membersList);

        // Salvar mob kills compartilhados
        CompoundTag mobKillsTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : sharedMobKills.entrySet()) {
            mobKillsTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("sharedMobKills", mobKillsTag);
        
        // 🎯 NOVO: Salvar contribuições individuais
        CompoundTag contributionsTag = new CompoundTag();
        for (Map.Entry<UUID, Map<String, Integer>> playerEntry : individualContributions.entrySet()) {
            CompoundTag playerContributions = new CompoundTag();
            for (Map.Entry<String, Integer> mobEntry : playerEntry.getValue().entrySet()) {
                playerContributions.putInt(mobEntry.getKey(), mobEntry.getValue());
            }
            contributionsTag.put(playerEntry.getKey().toString(), playerContributions);
        }
        tag.put("individualContributions", contributionsTag);

        // Salvar objetivos especiais
        tag.putBoolean("phase1SharedCompleted", phase1SharedCompleted);
        tag.putBoolean("phase2SharedCompleted", phase2SharedCompleted);
        tag.putBoolean("sharedElderGuardianKilled", sharedElderGuardianKilled);
        tag.putBoolean("sharedRaidWon", sharedRaidWon);
        tag.putBoolean("sharedTrialVaultAdvancementEarned", sharedTrialVaultAdvancementEarned);
        tag.putBoolean("sharedVoluntaireExileAdvancementEarned", sharedVoluntaireExileAdvancementEarned);
        tag.putBoolean("sharedWitherKilled", sharedWitherKilled);
        tag.putBoolean("sharedWardenKilled", sharedWardenKilled);

        // 🎯 NOVO: Salvar dados de fases customizadas
        CompoundTag customPhasesTag = new CompoundTag();
        for (Map.Entry<String, Boolean> entry : sharedCustomPhaseCompletion.entrySet()) {
            customPhasesTag.putBoolean(entry.getKey(), entry.getValue());
        }
        tag.put("sharedCustomPhaseCompletion", customPhasesTag);
        
        // Salvar kills de mobs em fases customizadas
        CompoundTag customMobKillsTag = new CompoundTag();
        for (Map.Entry<String, Map<String, Integer>> phaseEntry : sharedCustomMobKills.entrySet()) {
            String phaseId = phaseEntry.getKey();
            Map<String, Integer> mobKills = phaseEntry.getValue();
            
            CompoundTag phaseMobKillsTag = new CompoundTag();
            for (Map.Entry<String, Integer> mobEntry : mobKills.entrySet()) {
                phaseMobKillsTag.putInt(mobEntry.getKey(), mobEntry.getValue());
            }
            customMobKillsTag.put(phaseId, phaseMobKillsTag);
        }
        tag.put("sharedCustomMobKills", customMobKillsTag);
        
        // Salvar conclusão de objetivos em fases customizadas
        CompoundTag customObjectivesTag = new CompoundTag();
        for (Map.Entry<String, Map<String, Boolean>> phaseEntry : sharedCustomObjectiveCompletion.entrySet()) {
            String phaseId = phaseEntry.getKey();
            Map<String, Boolean> objectives = phaseEntry.getValue();
            
            CompoundTag objectivesTag = new CompoundTag();
            for (Map.Entry<String, Boolean> objectiveEntry : objectives.entrySet()) {
                objectivesTag.putBoolean(objectiveEntry.getKey(), objectiveEntry.getValue());
            }
            customObjectivesTag.put(phaseId, objectivesTag);
        }
        tag.put("sharedCustomObjectiveCompletion", customObjectivesTag);

        return tag;
    }

    public static PartyData load(CompoundTag tag, HolderLookup.Provider registries) {
        UUID partyId = tag.getUUID("partyId");
        String name = tag.getString("name");
        String password = tag.contains("password") ? tag.getString("password") : null;
        UUID leaderId = tag.getUUID("leaderId");

        PartyData party = new PartyData(partyId, name, password, leaderId);

        // 🎯 NOVO: Carregar campo isPublic
        if (tag.contains("isPublic")) {
            party.isPublic = tag.getBoolean("isPublic");
        } else {
            // Compatibilidade: se não tem o campo, determinar pelo password
            party.isPublic = (password == null || password.isEmpty());
        }

        // Carregar membros
        party.members.clear();
        if (tag.contains("members")) {
            ListTag membersList = tag.getList("members", 8); // 8 = TAG_STRING
            for (int i = 0; i < membersList.size(); i++) {
                party.members.add(UUID.fromString(membersList.getString(i)));
            }
        }

        // Carregar mob kills compartilhados
        if (tag.contains("sharedMobKills")) {
            CompoundTag mobKillsTag = tag.getCompound("sharedMobKills");
            for (String key : mobKillsTag.getAllKeys()) {
                party.sharedMobKills.put(key, mobKillsTag.getInt(key));
            }
        }
        
        // 🎯 NOVO: Carregar contribuições individuais
        if (tag.contains("individualContributions")) {
            CompoundTag contributionsTag = tag.getCompound("individualContributions");
            for (String playerIdStr : contributionsTag.getAllKeys()) {
                try {
                    UUID playerId = UUID.fromString(playerIdStr);
                    CompoundTag playerContributions = contributionsTag.getCompound(playerIdStr);
                    Map<String, Integer> contributions = new HashMap<>();
                    
                    for (String mobType : playerContributions.getAllKeys()) {
                        contributions.put(mobType, playerContributions.getInt(mobType));
                    }
                    
                    party.individualContributions.put(playerId, contributions);
                } catch (IllegalArgumentException e) {
                    // UUID inválido, ignorar
                }
            }
        }

        // Carregar objetivos especiais
        party.phase1SharedCompleted = tag.getBoolean("phase1SharedCompleted");
        party.phase2SharedCompleted = tag.getBoolean("phase2SharedCompleted");
        party.sharedElderGuardianKilled = tag.getBoolean("sharedElderGuardianKilled");
        party.sharedRaidWon = tag.getBoolean("sharedRaidWon");
        party.sharedTrialVaultAdvancementEarned = tag.getBoolean("sharedTrialVaultAdvancementEarned");
        party.sharedVoluntaireExileAdvancementEarned = tag.getBoolean("sharedVoluntaireExileAdvancementEarned");
        party.sharedWitherKilled = tag.getBoolean("sharedWitherKilled");
        party.sharedWardenKilled = tag.getBoolean("sharedWardenKilled");

        // 🎯 NOVO: Carregar dados de fases customizadas
        if (tag.contains("sharedCustomPhaseCompletion")) {
            CompoundTag customPhasesTag = tag.getCompound("sharedCustomPhaseCompletion");
            for (String phaseId : customPhasesTag.getAllKeys()) {
                party.sharedCustomPhaseCompletion.put(phaseId, customPhasesTag.getBoolean(phaseId));
            }
        }
        
        // Carregar kills de mobs em fases customizadas
        if (tag.contains("sharedCustomMobKills")) {
            CompoundTag customMobKillsTag = tag.getCompound("sharedCustomMobKills");
            for (String phaseId : customMobKillsTag.getAllKeys()) {
                CompoundTag mobKillsTag = customMobKillsTag.getCompound(phaseId);
                Map<String, Integer> mobKills = new HashMap<>();
                
                for (String mobType : mobKillsTag.getAllKeys()) {
                    mobKills.put(mobType, mobKillsTag.getInt(mobType));
                }
                
                party.sharedCustomMobKills.put(phaseId, mobKills);
            }
        }
        
        // Carregar conclusão de objetivos em fases customizadas
        if (tag.contains("sharedCustomObjectiveCompletion")) {
            CompoundTag customObjectivesTag = tag.getCompound("sharedCustomObjectiveCompletion");
            for (String phaseId : customObjectivesTag.getAllKeys()) {
                CompoundTag objectivesTag = customObjectivesTag.getCompound(phaseId);
                Map<String, Boolean> objectives = new HashMap<>();
                
                for (String objectiveId : objectivesTag.getAllKeys()) {
                    objectives.put(objectiveId, objectivesTag.getBoolean(objectiveId));
                }
                
                party.sharedCustomObjectiveCompletion.put(phaseId, objectives);
            }
        }

        return party;
    }

    // Getters
    public UUID getPartyId() { return partyId; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public UUID getLeaderId() { return leaderId; }
    public Set<UUID> getMembers() { return new HashSet<>(members); }
    public int getMemberCount() { return members.size(); }
    public Map<String, Integer> getSharedMobKills() { return new HashMap<>(sharedMobKills); }
    public int getSharedMobKillCount(String mobType) { return sharedMobKills.getOrDefault(mobType, 0); }
    
    // 🎯 NOVO: Getter para contribuições individuais
    public Map<String, Integer> getIndividualContributions(UUID playerId) { 
        return new HashMap<>(individualContributions.getOrDefault(playerId, new HashMap<>())); 
    }

    // Objetivos especiais getters
    public boolean isPhase1SharedCompleted() { return phase1SharedCompleted; }
    public boolean isPhase2SharedCompleted() { return phase2SharedCompleted; }
    public boolean isSharedElderGuardianKilled() { return sharedElderGuardianKilled; }
    public boolean isSharedRaidWon() { return sharedRaidWon; }
    public boolean isSharedTrialVaultAdvancementEarned() { return sharedTrialVaultAdvancementEarned; }
    public boolean isSharedVoluntaireExileAdvancementEarned() { return sharedVoluntaireExileAdvancementEarned; }
    public boolean isSharedWitherKilled() { return sharedWitherKilled; }
    public boolean isSharedWardenKilled() { return sharedWardenKilled; }

    // Setters para objetivos especiais
    public void setSharedElderGuardianKilled(boolean value) { this.sharedElderGuardianKilled = value; }
    public void setSharedRaidWon(boolean value) { this.sharedRaidWon = value; }
    public void setSharedTrialVaultAdvancementEarned(boolean value) { this.sharedTrialVaultAdvancementEarned = value; }
    public void setSharedVoluntaireExileAdvancementEarned(boolean value) { this.sharedVoluntaireExileAdvancementEarned = value; }
    public void setSharedWitherKilled(boolean value) { this.sharedWitherKilled = value; }
    public void setSharedWardenKilled(boolean value) { this.sharedWardenKilled = value; }
    public void setPhase1SharedCompleted(boolean value) { this.phase1SharedCompleted = value; }
    public void setPhase2SharedCompleted(boolean value) { this.phase2SharedCompleted = value; }

    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setLeaderId(UUID leaderId) { this.leaderId = leaderId; }

    // ============================================================================
    // 🎯 MÉTODOS DE VERIFICAÇÃO DE ACESSO A DIMENSÕES
    // ============================================================================

    /**
     * Verificar se a party pode acessar o Nether (Fase 1 completa)
     */
    public boolean canAccessNether() {
        if (!net.mirai.dimtr.config.DimTrConfig.SERVER.enablePhase1.get()) {
            return true;
        }
        return isPhase1Complete();
    }

    /**
     * Verificar se a party pode acessar o End (Fase 2 completa)
     */
    public boolean canAccessEnd() {
        if (!net.mirai.dimtr.config.DimTrConfig.SERVER.enablePhase2.get()) {
            return true;
        }
        return isPhase2Complete() && canAccessNether();
    }

    /**
     * Verificar se a Fase 1 está completa para a party
     */
    public boolean isPhase1Complete() {
        if (!net.mirai.dimtr.config.DimTrConfig.SERVER.enablePhase1.get()) {
            return true;
        }

        // Verificar objetivos especiais
        boolean specialObjectivesComplete = true;
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.reqElderGuardian.get() && !sharedElderGuardianKilled) {
            specialObjectivesComplete = false;
        }
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.reqRaid.get() && !sharedRaidWon) {
            specialObjectivesComplete = false;
        }
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.reqTrialVaultAdv.get() && !sharedTrialVaultAdvancementEarned) {
            specialObjectivesComplete = false;
        }
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.reqVoluntaryExile.get() && !sharedVoluntaireExileAdvancementEarned) {
            specialObjectivesComplete = false;
        }

        // Verificar mob kills com multiplicador de party
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableMobKillsPhase1.get()) {
            double multiplier = getRequirementMultiplier();
            
            if (getSharedMobKills("zombie") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqZombieKills.get() * multiplier)) return false;
            if (getSharedMobKills("skeleton") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqSkeletonKills.get() * multiplier)) return false;
            if (getSharedMobKills("stray") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqStrayKills.get() * multiplier)) return false;
            if (getSharedMobKills("husk") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqHuskKills.get() * multiplier)) return false;
            if (getSharedMobKills("spider") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqSpiderKills.get() * multiplier)) return false;
            if (getSharedMobKills("creeper") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqCreeperKills.get() * multiplier)) return false;
            if (getSharedMobKills("drowned") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqDrownedKills.get() * multiplier)) return false;
            if (getSharedMobKills("enderman") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqEndermanKills.get() * multiplier)) return false;
            if (getSharedMobKills("witch") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqWitchKills.get() * multiplier)) return false;
            if (getSharedMobKills("pillager") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqPillagerKills.get() * multiplier)) return false;
            if (getSharedMobKills("captain") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqCaptainKills.get() * multiplier)) return false;
            if (getSharedMobKills("vindicator") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqVindicatorKills.get() * multiplier)) return false;
            if (getSharedMobKills("bogged") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqBoggedKills.get() * multiplier)) return false;
            if (getSharedMobKills("breeze") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqBreezeKills.get() * multiplier)) return false;
            if (getSharedMobKills("ravager") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqRavagerKills.get() * multiplier)) return false;
            if (getSharedMobKills("evoker") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqEvokerKills.get() * multiplier)) return false;
        }

        return specialObjectivesComplete;
    }

    /**
     * Verificar se a Fase 2 está completa para a party
     */
    public boolean isPhase2Complete() {
        if (!net.mirai.dimtr.config.DimTrConfig.SERVER.enablePhase2.get()) {
            return true;
        }

        // Deve completar Fase 1 primeiro
        if (!isPhase1Complete()) {
            return false;
        }

        // Verificar objetivos especiais da Fase 2
        boolean specialObjectivesComplete = true;
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.reqWither.get() && !sharedWitherKilled) {
            specialObjectivesComplete = false;
        }
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.reqWarden.get() && !sharedWardenKilled) {
            specialObjectivesComplete = false;
        }

        // Verificar mob kills da Fase 2 com multiplicador de party
        if (net.mirai.dimtr.config.DimTrConfig.SERVER.enableMobKillsPhase2.get()) {
            double multiplier = getRequirementMultiplier();
            
            // Nether mobs
            if (getSharedMobKills("blaze") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqBlazeKills.get() * multiplier)) return false;
            if (getSharedMobKills("wither_skeleton") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqWitherSkeletonKills.get() * multiplier)) return false;
            if (getSharedMobKills("piglin_brute") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqPiglinBruteKills.get() * multiplier)) return false;
            if (getSharedMobKills("hoglin") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqHoglinKills.get() * multiplier)) return false;
            if (getSharedMobKills("zoglin") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqZoglinKills.get() * multiplier)) return false;
            if (getSharedMobKills("ghast") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqGhastKills.get() * multiplier)) return false;
            if (getSharedMobKills("piglin") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqPiglinKills.get() * multiplier)) return false;

            // Overworld mobs repetidos (125%)
            double overworldMultiplier = multiplier * 1.25; // 125% dos requisitos originais
            if (getSharedMobKills("zombie") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqZombieKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("skeleton") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqSkeletonKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("stray") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqStrayKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("husk") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqHuskKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("spider") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqSpiderKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("creeper") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqCreeperKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("drowned") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqDrownedKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("enderman") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqEndermanKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("witch") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqWitchKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("pillager") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqPillagerKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("captain") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqCaptainKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("vindicator") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqVindicatorKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("bogged") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqBoggedKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("breeze") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqBreezeKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("ravager") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqRavagerKills.get() * overworldMultiplier)) return false;
            if (getSharedMobKills("evoker") < (int)Math.ceil(net.mirai.dimtr.config.DimTrConfig.SERVER.reqEvokerKills.get() * overworldMultiplier)) return false;
        }

        return specialObjectivesComplete;
    }

    /**
     * Método auxiliar para obter kills de mob
     */
    private int getSharedMobKills(String mobType) {
        return sharedMobKills.getOrDefault(mobType, 0);
    }
    
    // ============================================================================
    // 🎯 NOVO: MÉTODOS PARA CUSTOM PHASES
    // ============================================================================
    
    /**
     * Verificar se uma fase customizada está completa
     */
    public boolean isCustomPhaseComplete(String phaseId) {
        return sharedCustomPhaseCompletion.getOrDefault(phaseId, false);
    }
    
    /**
     * Marcar uma fase customizada como completa
     */
    public void setCustomPhaseComplete(String phaseId, boolean complete) {
        sharedCustomPhaseCompletion.put(phaseId, complete);
    }
    
    /**
     * Obter kills de mob customizado compartilhado
     */
    public int getSharedCustomMobKills(String phaseId, String mobType) {
        return sharedCustomMobKills
                .getOrDefault(phaseId, new HashMap<>())
                .getOrDefault(mobType, 0);
    }
    
    /**
     * Incrementar kill de mob customizado compartilhado
     */
    public void incrementSharedCustomMobKill(String phaseId, String mobType) {
        sharedCustomMobKills.computeIfAbsent(phaseId, k -> new HashMap<>())
                .merge(mobType, 1, Integer::sum);
    }
    
    /**
     * Adicionar kills de mob customizado (para transferência de progresso)
     */
    public void addSharedCustomMobKills(String phaseId, String mobType, int kills) {
        if (kills > 0) {
            sharedCustomMobKills.computeIfAbsent(phaseId, k -> new HashMap<>())
                    .merge(mobType, kills, Integer::sum);
        }
    }
    
    /**
     * Verificar se objetivo customizado está completo
     */
    public boolean isSharedCustomObjectiveComplete(String phaseId, String objectiveId) {
        return sharedCustomObjectiveCompletion
                .getOrDefault(phaseId, new HashMap<>())
                .getOrDefault(objectiveId, false);
    }
    
    /**
     * Marcar objetivo customizado como completo
     */
    public void setSharedCustomObjectiveComplete(String phaseId, String objectiveId, boolean complete) {
        sharedCustomObjectiveCompletion.computeIfAbsent(phaseId, k -> new HashMap<>())
                .put(objectiveId, complete);
    }
    
    /**
     * 🎯 NOVO: Transferir progresso de custom phase de um jogador para a party
     */
    public void transferCustomProgressFromPlayer(PlayerProgressionData playerData) {
        // Transferir custom phase completion
        for (var entry : playerData.getCustomPhaseCompletionMap().entrySet()) {
            String phaseId = entry.getKey();
            boolean isComplete = entry.getValue();
            if (isComplete && !isCustomPhaseComplete(phaseId)) {
                setCustomPhaseComplete(phaseId, true);
            }
        }
        
        // Transferir custom mob kills
        for (var phaseEntry : playerData.getCustomMobKillsMap().entrySet()) {
            String phaseId = phaseEntry.getKey();
            for (var mobEntry : phaseEntry.getValue().entrySet()) {
                String mobType = mobEntry.getKey();
                int kills = mobEntry.getValue();
                addSharedCustomMobKills(phaseId, mobType, kills);
            }
        }
        
        // Transferir custom objectives
        for (var phaseEntry : playerData.getCustomObjectiveCompletionMap().entrySet()) {
            String phaseId = phaseEntry.getKey();
            for (var objEntry : phaseEntry.getValue().entrySet()) {
                String objectiveId = objEntry.getKey();
                boolean isComplete = objEntry.getValue();
                if (isComplete) {
                    setSharedCustomObjectiveComplete(phaseId, objectiveId, true);
                }
            }
        }
    }
    
    /**
     * 🎯 NOVO: Restaurar progresso de custom phase da party para um jogador
     */
    public void restoreCustomProgressToPlayer(PlayerProgressionData playerData) {
        // Restaurar custom phase completion
        for (var entry : sharedCustomPhaseCompletion.entrySet()) {
            String phaseId = entry.getKey();
            boolean isComplete = entry.getValue();
            if (isComplete) {
                playerData.setCustomPhaseComplete(phaseId, true);
            }
        }
        
        // Restaurar custom objectives
        for (var phaseEntry : sharedCustomObjectiveCompletion.entrySet()) {
            String phaseId = phaseEntry.getKey();
            for (var objEntry : phaseEntry.getValue().entrySet()) {
                String objectiveId = objEntry.getKey();
                boolean isComplete = objEntry.getValue();
                if (isComplete) {
                    playerData.setCustomObjectiveComplete(phaseId, objectiveId, true);
                }
            }
        }
        
        // Para mob kills customizados, precisamos ser mais cuidadosos
        // Vamos garantir que o jogador tenha pelo menos o progresso atual da party
        for (var phaseEntry : sharedCustomMobKills.entrySet()) {
            String phaseId = phaseEntry.getKey();
            for (var mobEntry : phaseEntry.getValue().entrySet()) {
                String mobType = mobEntry.getKey();
                int partyKills = mobEntry.getValue();
                int playerKills = playerData.getCustomMobKills(phaseId, mobType);
                
                // Se a party tem mais kills que o jogador, dar o progresso para o jogador
                if (partyKills > playerKills) {
                    int killsToAdd = partyKills - playerKills;
                    for (int i = 0; i < killsToAdd; i++) {
                        playerData.incrementCustomMobKill(phaseId, mobType);
                    }
                }
            }
        }
    }
    
    // ============================================================================
    // 🎯 NOVO: Getters para Custom Phases data
    // ============================================================================
    
    /**
     * Get all shared custom phase completion data
     */
    public Map<String, Boolean> getSharedCustomPhaseCompletion() {
        return new HashMap<>(sharedCustomPhaseCompletion);
    }
    
    /**
     * Get all shared custom mob kills data
     */
    public Map<String, Map<String, Integer>> getSharedCustomMobKills() {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (var entry : sharedCustomMobKills.entrySet()) {
            result.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return result;
    }
    
    /**
     * Get all shared custom objective completion data
     */
    public Map<String, Map<String, Boolean>> getSharedCustomObjectiveCompletion() {
        Map<String, Map<String, Boolean>> result = new HashMap<>();
        for (var entry : sharedCustomObjectiveCompletion.entrySet()) {
            result.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return result;
    }
}