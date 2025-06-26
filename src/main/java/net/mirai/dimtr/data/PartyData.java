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

    // 🎯 NOVO: Multiplicador dinâmico baseado no número de membros
    private static final double BASE_MULTIPLIER_PER_MEMBER = 0.75; // 75% por membro

    public PartyData(UUID partyId, String name, String password, UUID leaderId) {
        this.partyId = partyId;
        this.name = name;
        this.password = password;
        this.isPublic = (password == null); // Público se não tem senha
        this.leaderId = leaderId;
        this.members = new HashSet<>();
        this.sharedMobKills = new HashMap<>();
        this.individualContributions = new HashMap<>();
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
     * 75% adicional por membro além do primeiro
     */
    public double getRequirementMultiplier() {
        return 1.0 + (members.size() - 1) * 0.75;
    }

    /**
     * 🎯 NOVO: Calcular multiplicador de metas baseado no número de membros
     * Cada membro adiciona 75% aos requisitos
     */
    public double getPartyMultiplier() {
        return 1.0 + (members.size() - 1) * BASE_MULTIPLIER_PER_MEMBER;
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
            sharedMobKills.put(mobType, sharedMobKills.get(mobType) + 1);
            
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
}