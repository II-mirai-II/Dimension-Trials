package net.mirai.dimtr.client;

import net.mirai.dimtr.network.UpdatePartyToClientPayload;
import net.minecraft.client.Minecraft;

import java.util.*;

/**
 * Dados de party no lado cliente
 */
public class ClientPartyData {

    public static final ClientPartyData INSTANCE = new ClientPartyData();

    // Dados da party atual
    private UUID partyId = null;
    private String partyName = "";
    private boolean isPublic = true; // 🎯 NOVO: Se a party é pública ou privada
    private UUID leaderId = null;
    private List<UUID> members = new ArrayList<>();
    private int memberCount = 0;
    private double requirementMultiplier = 1.0;
    private Map<String, Integer> sharedMobKills = new HashMap<>();

    // ✅ NOVO: Cache persistente de nomes de membros da party
    private Map<UUID, String> memberNameCache = new HashMap<>();

    // Objetivos especiais compartilhados
    private boolean sharedElderGuardianKilled = false;
    private boolean sharedRaidWon = false;
    private boolean sharedTrialVaultAdvancementEarned = false;
    private boolean sharedVoluntaireExileAdvancementEarned = false;
    private boolean sharedWitherKilled = false;
    private boolean sharedWardenKilled = false;
    private boolean phase1SharedCompleted = false;
    private boolean phase2SharedCompleted = false;

    private ClientPartyData() {}

    // ✅ CORRIGIDO: Método updateData com detecção de dados vazios
    public void updateData(UpdatePartyToClientPayload payload) {
        // ✅ CORREÇÃO: Se partyId é null, significa que o jogador saiu da party
        if (payload.partyId() == null) {
            clearData();
            return;
        }
        
        this.partyId = payload.partyId();
        this.partyName = payload.partyName();
        this.isPublic = payload.isPublic(); // ✅ CORRIGIDO: Atualizar isPublic
        this.leaderId = payload.leaderId();
        this.members = new ArrayList<>(payload.members());
        this.memberCount = payload.memberCount();
        this.requirementMultiplier = payload.progressionMultiplier();
        this.sharedMobKills = new HashMap<>(payload.sharedMobKills());

        this.sharedElderGuardianKilled = payload.sharedElderGuardianKilled();
        this.sharedRaidWon = payload.sharedRaidWon();
        this.sharedTrialVaultAdvancementEarned = payload.sharedTrialVaultAdvancementEarned();
        this.sharedVoluntaireExileAdvancementEarned = payload.sharedVoluntaireExileAdvancementEarned();
        this.sharedWitherKilled = payload.sharedWitherKilled();
        this.sharedWardenKilled = payload.sharedWardenKilled();
        this.phase1SharedCompleted = payload.phase1SharedCompleted();
        this.phase2SharedCompleted = payload.phase2SharedCompleted();

        // ✅ NOVO: Atualizar cache de nomes para membros online
        updateMemberNameCache();
    }

    // ✅ NOVO: Método para atualizar o cache de nomes dos membros
    private void updateMemberNameCache() {
        Minecraft minecraft = Minecraft.getInstance();
        
        for (UUID memberId : members) {
            String currentName = null;
            
            // Tentar obter nome do jogador atual
            if (minecraft.player != null && memberId.equals(minecraft.player.getUUID())) {
                currentName = minecraft.player.getName().getString();
            }
            // Tentar encontrar jogador online no mundo
            else if (minecraft.level != null) {
                for (var player : minecraft.level.players()) {
                    if (player.getUUID().equals(memberId)) {
                        currentName = player.getName().getString();
                        break;
                    }
                }
            }
            // Tentar connection info (para jogadores distantes mas conectados)
            if (currentName == null) {
                var connection = minecraft.getConnection();
                if (connection != null) {
                    var playerInfo = connection.getPlayerInfo(memberId);
                    if (playerInfo != null) {
                        currentName = playerInfo.getProfile().getName();
                    }
                }
            }
            
            // Se conseguiu obter o nome, atualizar o cache
            if (currentName != null && !currentName.isEmpty()) {
                memberNameCache.put(memberId, currentName);
            }
        }
        
        // Limpar cache de jogadores que não estão mais na party
        memberNameCache.keySet().retainAll(members);
    }

    public void clearData() {
        this.partyId = null;
        this.partyName = "";
        this.isPublic = true; // ✅ CORRIGIDO: Resetar isPublic
        this.leaderId = null;
        this.members.clear();
        this.memberCount = 0;
        this.requirementMultiplier = 1.0;
        this.sharedMobKills.clear();

        // ✅ NOVO: Limpar cache de nomes ao sair da party
        this.memberNameCache.clear();

        this.sharedElderGuardianKilled = false;
        this.sharedRaidWon = false;
        this.sharedTrialVaultAdvancementEarned = false;
        this.sharedVoluntaireExileAdvancementEarned = false;
        this.sharedWitherKilled = false;
        this.sharedWardenKilled = false;
        this.phase1SharedCompleted = false;
        this.phase2SharedCompleted = false;
    }

    // ============================================================================
    // 🎯 GETTERS PÚBLICOS
    // ============================================================================

    public boolean isInParty() {
        return partyId != null;
    }

    public UUID getPartyId() { return partyId; }
    public String getPartyName() { return partyName; }
    public UUID getLeaderId() { return leaderId; }
    public List<UUID> getMembers() { return new ArrayList<>(members); }
    public int getMemberCount() { return memberCount; }
    public double getRequirementMultiplier() { return requirementMultiplier; }

    // ✅ NOVO: Método para obter nome do membro com fallback para cache
    public String getMemberName(UUID memberId) {
        // Primeiro tentar resolver o nome em tempo real
        Minecraft minecraft = Minecraft.getInstance();
        
        // Se for o próprio jogador
        if (minecraft.player != null && memberId.equals(minecraft.player.getUUID())) {
            return minecraft.player.getName().getString();
        }
        
        // Tentar encontrar jogador online no mundo
        if (minecraft.level != null) {
            for (var player : minecraft.level.players()) {
                if (player.getUUID().equals(memberId)) {
                    String name = player.getName().getString();
                    // Atualizar cache
                    memberNameCache.put(memberId, name);
                    return name;
                }
            }
        }
        
        // Tentar connection info
        var connection = minecraft.getConnection();
        if (connection != null) {
            var playerInfo = connection.getPlayerInfo(memberId);
            if (playerInfo != null) {
                String name = playerInfo.getProfile().getName();
                // Atualizar cache
                memberNameCache.put(memberId, name);
                return name;
            }
        }
        
        // Se não conseguiu resolver em tempo real, usar cache
        String cachedName = memberNameCache.get(memberId);
        if (cachedName != null && !cachedName.isEmpty()) {
            return cachedName;
        }
        
        // Último recurso: usar parte do UUID
        return "Player-" + memberId.toString().substring(0, 8);
    }

    public int getSharedMobKillCount(String mobType) {
        return sharedMobKills.getOrDefault(mobType, 0);
    }

    public Map<String, Integer> getSharedMobKills() {
        return new HashMap<>(sharedMobKills);
    }

    // Objetivos especiais
    public boolean isSharedElderGuardianKilled() { return sharedElderGuardianKilled; }
    public boolean isSharedRaidWon() { return sharedRaidWon; }
    public boolean isSharedTrialVaultAdvancementEarned() { return sharedTrialVaultAdvancementEarned; }
    public boolean isSharedVoluntaireExileAdvancementEarned() { return sharedVoluntaireExileAdvancementEarned; }
    public boolean isSharedWitherKilled() { return sharedWitherKilled; }
    public boolean isSharedWardenKilled() { return sharedWardenKilled; }
    public boolean isPhase1SharedCompleted() { return phase1SharedCompleted; }
    public boolean isPhase2SharedCompleted() { return phase2SharedCompleted; }

    public boolean isPartyPublic() { 
        return isPublic; 
    }
}