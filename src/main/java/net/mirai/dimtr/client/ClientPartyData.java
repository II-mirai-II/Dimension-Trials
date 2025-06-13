package net.mirai.dimtr.client;

import net.mirai.dimtr.network.UpdatePartyToClientPayload;

import java.util.*;

/**
 * Dados de party no lado cliente
 */
public class ClientPartyData {

    public static final ClientPartyData INSTANCE = new ClientPartyData();

    // Dados da party atual
    private UUID partyId = null;
    private String partyName = "";
    private UUID leaderId = null;
    private List<UUID> members = new ArrayList<>();
    private int memberCount = 0;
    private double requirementMultiplier = 1.0;
    private Map<String, Integer> sharedMobKills = new HashMap<>();

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

    // 🔧 CORRIGIDO: Método updateData com campos corretos
    public void updateData(UpdatePartyToClientPayload payload) {
        this.partyId = payload.partyId();
        this.partyName = payload.partyName();
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
    }

    public void clearData() {
        this.partyId = null;
        this.partyName = "";
        this.leaderId = null;
        this.members.clear();
        this.memberCount = 0;
        this.requirementMultiplier = 1.0;
        this.sharedMobKills.clear();

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
}