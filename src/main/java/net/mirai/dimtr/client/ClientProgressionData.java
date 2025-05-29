package net.mirai.dimtr.client; // Pacote atualizado

import net.mirai.dimtr.config.DimTrConfig; // Pacote e nome da classe de config atualizados
import net.mirai.dimtr.network.UpdateProgressionToClientPayload; // Pacote atualizado
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProgressionData {
    public static final ClientProgressionData INSTANCE = new ClientProgressionData();

    private boolean elderGuardianKilled = false;
    private boolean raidWon = false;
    private boolean ravagerKilled = false;
    private boolean evokerKilled = false;
    private boolean trialVaultAdvancementEarned = false;
    private boolean phase1Completed = false;

    private boolean witherKilled = false;
    private boolean wardenKilled = false;
    private boolean phase2Completed = false;

    // Campos para espelhar a config do servidor (são sincronizados)
    private boolean serverEnablePhase1 = true;
    private boolean serverReqElderGuardian = true;
    private boolean serverReqRaidAndRavager = true;
    private boolean serverReqEvoker = true;
    private boolean serverReqTrialVaultAdv = true;
    private boolean serverEnablePhase2 = true;
    private boolean serverReqWither = true;
    private boolean serverReqWarden = true;


    private ClientProgressionData() {}

    public void updateData(UpdateProgressionToClientPayload payload) {
        this.elderGuardianKilled = payload.elderGuardianKilled();
        this.raidWon = payload.raidWon();
        this.ravagerKilled = payload.ravagerKilled();
        this.evokerKilled = payload.evokerKilled();
        this.trialVaultAdvancementEarned = payload.trialVaultAdvancementEarned();
        this.phase1Completed = payload.phase1Completed();
        this.witherKilled = payload.witherKilled();
        this.wardenKilled = payload.wardenKilled();
        this.phase2Completed = payload.phase2Completed();

        // Atualiza o estado local das configs do servidor
        // Isso é importante porque DimTrConfig.SERVER.X.get() no cliente
        // LERÁ a config sincronizada do servidor.
        this.serverEnablePhase1 = DimTrConfig.SERVER.enablePhase1.get(); // Referência de config atualizada
        this.serverReqElderGuardian = DimTrConfig.SERVER.reqElderGuardian.get(); // Referência de config atualizada
        this.serverReqRaidAndRavager = DimTrConfig.SERVER.reqRaidAndRavager.get(); // Referência de config atualizada
        this.serverReqEvoker = DimTrConfig.SERVER.reqEvoker.get(); // Referência de config atualizada
        this.serverReqTrialVaultAdv = DimTrConfig.SERVER.reqTrialVaultAdv.get(); // Referência de config atualizada
        this.serverEnablePhase2 = DimTrConfig.SERVER.enablePhase2.get(); // Referência de config atualizada
        this.serverReqWither = DimTrConfig.SERVER.reqWither.get(); // Referência de config atualizada
        this.serverReqWarden = DimTrConfig.SERVER.reqWarden.get(); // Referência de config atualizada
    }

    // Getters para progresso
    public boolean isElderGuardianKilled() { return elderGuardianKilled; }
    public boolean isRaidWon() { return raidWon; }
    public boolean isRavagerKilled() { return ravagerKilled; }
    public boolean isEvokerKilled() { return evokerKilled; }
    public boolean isTrialVaultAdvancementEarned() { return trialVaultAdvancementEarned; }
    public boolean isPhase1Completed() { return phase1Completed; }
    public boolean isPhase2Completed() { return phase2Completed; }
    public boolean isWitherKilled() { return witherKilled; }
    public boolean isWardenKilled() { return wardenKilled; }

    // Getters para estado da config do servidor (para a HUD saber o que mostrar)
    public boolean isServerEnablePhase1() { return serverEnablePhase1; }
    public boolean isServerReqElderGuardian() { return serverReqElderGuardian; }
    public boolean isServerReqRaidAndRavager() { return serverReqRaidAndRavager; }
    public boolean isServerReqEvoker() { return serverReqEvoker; }
    public boolean isServerReqTrialVaultAdv() { return serverReqTrialVaultAdv; }
    public boolean isServerEnablePhase2() { return serverEnablePhase2; }
    public boolean isServerReqWither() { return serverReqWither; }
    public boolean isServerReqWarden() { return serverReqWarden; }

    // Helper para HUD: Fase 1 está efetivamente completa (ou por progresso ou por config)
    public boolean isPhase1EffectivelyComplete() {
        return this.phase1Completed || !this.serverEnablePhase1;
    }
}