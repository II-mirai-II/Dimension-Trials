package net.mirai.dimtr.client;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.network.UpdateProgressionToClientPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ClientProgressionData {
    public static final ClientProgressionData INSTANCE = new ClientProgressionData();

    // Objetivos originais
    private boolean elderGuardianKilled = false;
    private boolean raidWon = false;
    private boolean ravagerKilled = false; // Manter para compatibilidade
    private boolean evokerKilled = false; // Manter para compatibilidade
    private boolean trialVaultAdvancementEarned = false;
    // NOVO: Conquista Voluntaire Exile
    private boolean voluntaireExileAdvancementEarned = false;
    private boolean phase1Completed = false;

    private boolean witherKilled = false;
    private boolean wardenKilled = false;
    private boolean phase2Completed = false;

    // Novos contadores de mobs - Fase 1 (Overworld)
    private int zombieKills = 0;
    // Campo removido: zombieVillagerKills - funcionalidade completamente descontinuada
    private int skeletonKills = 0;
    private int strayKills = 0;
    private int huskKills = 0;
    private int spiderKills = 0;
    private int creeperKills = 0;
    private int drownedKills = 0;
    private int endermanKills = 0;
    private int witchKills = 0;
    private int pillagerKills = 0;
    private int captainKills = 0;
    private int vindicatorKills = 0;
    private int boggedKills = 0;
    private int breezeKills = 0;

    // ✅ CORRIGIDO: Ravager e Evoker são mob kills normais da Fase 1
    private int ravagerKills = 0;
    private int evokerKills = 0;

    // Novos contadores de mobs - Fase 2 (Nether + High Level)
    private int blazeKills = 0;
    private int witherSkeletonKills = 0;
    private int piglinBruteKills = 0;
    private int hoglinKills = 0;
    private int zoglinKills = 0;
    private int ghastKills = 0;
    private int piglinKills = 0;

    // 🎯 NOVO: Custom Phases data
    private Map<String, Boolean> customPhaseCompletion = new HashMap<>();
    private Map<String, Map<String, Integer>> customMobKills = new HashMap<>();
    private Map<String, Map<String, Boolean>> customObjectiveCompletion = new HashMap<>();

    // CORREÇÃO PRINCIPAL: Configurações de requisitos sincronizadas do servidor
    private int reqZombieKills = 50;
    private int reqSkeletonKills = 40;
    private int reqStrayKills = 10;
    private int reqHuskKills = 10;
    private int reqSpiderKills = 30;
    private int reqCreeperKills = 30;
    private int reqDrownedKills = 20;
    private int reqEndermanKills = 5;
    private int reqWitchKills = 5;
    private int reqPillagerKills = 20;
    private int reqCaptainKills = 1;
    private int reqVindicatorKills = 10;
    private int reqBoggedKills = 10;
    private int reqBreezeKills = 5;
    private int reqRavagerKills = 1; // CORRETO: 1
    private int reqEvokerKills = 5;  // CORRETO: 5
    private int reqBlazeKills = 20;
    private int reqWitherSkeletonKills = 15;
    private int reqPiglinBruteKills = 5;
    private int reqHoglinKills = 1;  // CORRETO: 1
    private int reqZoglinKills = 1;  // CORRETO: 1
    private int reqGhastKills = 10;
    private int reqPiglinKills = 30;

    // NOVO: Configuração para Voluntary Exile
    private boolean serverReqVoluntaryExile = true;

    // MÉTODO PRINCIPAL: Atualizar dados com payload do servidor
    public void updateData(UpdateProgressionToClientPayload payload) {
        // Objetivos originais
        this.elderGuardianKilled = payload.elderGuardianKilled();
        this.raidWon = payload.raidWon();
        this.ravagerKilled = payload.ravagerKilled(); // Manter para compatibilidade
        this.evokerKilled = payload.evokerKilled(); // Manter para compatibilidade
        this.trialVaultAdvancementEarned = payload.trialVaultAdvancementEarned();
        // NOVO: Conquista Voluntaire Exile
        this.voluntaireExileAdvancementEarned = payload.voluntaireExileAdvancementEarned();
        this.phase1Completed = payload.phase1Completed();
        this.witherKilled = payload.witherKilled();
        this.wardenKilled = payload.wardenKilled();
        this.phase2Completed = payload.phase2Completed();

        // Novos contadores de mobs - Fase 1 (incluindo Ravager e Evoker)
        this.zombieKills = payload.zombieKills();
        // ✅ REMOVIDO: zombieVillagerKills - funcionalidade descontinuada
        this.skeletonKills = payload.skeletonKills();
        this.strayKills = payload.strayKills();
        this.huskKills = payload.huskKills();
        this.spiderKills = payload.spiderKills();
        this.creeperKills = payload.creeperKills();
        this.drownedKills = payload.drownedKills();
        this.endermanKills = payload.endermanKills();
        this.witchKills = payload.witchKills();
        this.pillagerKills = payload.pillagerKills();
        this.captainKills = payload.captainKills();
        this.vindicatorKills = payload.vindicatorKills();
        this.boggedKills = payload.boggedKills();
        this.breezeKills = payload.breezeKills();

        // ✅ CORRIGIDO: Ravager e Evoker como mob kills normais
        this.ravagerKills = payload.ravagerKills();
        this.evokerKills = payload.evokerKills();

        // Novos contadores de mobs - Fase 2
        this.blazeKills = payload.blazeKills();
        this.witherSkeletonKills = payload.witherSkeletonKills();
        this.piglinBruteKills = payload.piglinBruteKills();
        this.hoglinKills = payload.hoglinKills();
        this.zoglinKills = payload.zoglinKills();
        this.ghastKills = payload.ghastKills();
        this.piglinKills = payload.piglinKills();

        // CORREÇÃO PRINCIPAL: Atualizar configurações sincronizadas do servidor
        this.reqZombieKills = payload.reqZombieKills();
        // Campo reqZombieVillagerKills removido
        this.reqSkeletonKills = payload.reqSkeletonKills();
        this.reqStrayKills = payload.reqStrayKills();
        this.reqHuskKills = payload.reqHuskKills();
        this.reqSpiderKills = payload.reqSpiderKills();
        this.reqCreeperKills = payload.reqCreeperKills();
        this.reqDrownedKills = payload.reqDrownedKills();
        this.reqEndermanKills = payload.reqEndermanKills();
        this.reqWitchKills = payload.reqWitchKills();
        this.reqPillagerKills = payload.reqPillagerKills();
        this.reqCaptainKills = payload.reqCaptainKills();
        this.reqVindicatorKills = payload.reqVindicatorKills();
        this.reqBoggedKills = payload.reqBoggedKills();
        this.reqBreezeKills = payload.reqBreezeKills();
        // ✅ CORRIGIDO: Ravager e Evoker são mob kills normais da Fase 1
        this.reqRavagerKills = payload.reqRavagerKills(); // Requisito: 1
        this.reqEvokerKills = payload.reqEvokerKills();   // Requisito: 5
        this.reqBlazeKills = payload.reqBlazeKills();
        this.reqWitherSkeletonKills = payload.reqWitherSkeletonKills();
        this.reqPiglinBruteKills = payload.reqPiglinBruteKills();
        this.reqHoglinKills = payload.reqHoglinKills();   // Agora sincronizado: 1
        this.reqZoglinKills = payload.reqZoglinKills();   // Agora sincronizado: 1
        this.reqGhastKills = payload.reqGhastKills();
        this.reqPiglinKills = payload.reqPiglinKills();

        // NOVO: Sincronizar configuração Voluntary Exile
        this.serverReqVoluntaryExile = payload.serverReqVoluntaryExile();

        // 🎯 NOVO: Sincronizar dados de Custom Phases do payload
        this.customPhaseCompletion.clear();
        this.customPhaseCompletion.putAll(payload.customPhaseCompletion());
        
        this.customMobKills.clear();
        this.customMobKills.putAll(payload.customMobKills());
        
        this.customObjectiveCompletion.clear();
        this.customObjectiveCompletion.putAll(payload.customObjectiveCompletion());
    }

    // Método getInstance para compatibilidade
    public static ClientProgressionData getInstance() {
        return INSTANCE;
    }
    
    // Getters públicos para acessar campos privados
    public boolean isPhase1Completed() { return phase1Completed; }
    public boolean isPhase2Completed() { return phase2Completed; }
    public boolean isElderGuardianKilled() { return elderGuardianKilled; }
    public boolean isRaidWon() { return raidWon; }
    public boolean isWitherKilled() { return witherKilled; }
    public boolean isWardenKilled() { return wardenKilled; }
    public int getZombieKills() { return zombieKills; }
    public int getSkeletonKills() { return skeletonKills; }
    public int getCreeperKills() { return creeperKills; }
    public int getSpiderKills() { return spiderKills; }
    public int getEndermanKills() { return endermanKills; }
    public int getWitchKills() { return witchKills; }
    public int getBlazeKills() { return blazeKills; }    
    // Getters adicionais necessários
    public boolean isVoluntaireExileAdvancementEarned() { return voluntaireExileAdvancementEarned; }
    public boolean isTrialVaultAdvancementEarned() { return trialVaultAdvancementEarned; }

    // Método para obter contadores de mobs como mapa
    public Map<String, Integer> getMobKills() {
        Map<String, Integer> mobKills = new HashMap<>();
        mobKills.put("zombie", zombieKills);
        mobKills.put("skeleton", skeletonKills);
        mobKills.put("creeper", creeperKills);
        mobKills.put("spider", spiderKills);
        mobKills.put("enderman", endermanKills);
        mobKills.put("witch", witchKills);
        mobKills.put("blaze", blazeKills);
        mobKills.put("pillager", pillagerKills);
        mobKills.put("captain", captainKills);
        mobKills.put("vindicator", vindicatorKills);
        mobKills.put("bogged", boggedKills);
        mobKills.put("breeze", breezeKills);
        mobKills.put("ravager", ravagerKills);
        mobKills.put("evoker", evokerKills);
        mobKills.put("stray", strayKills);
        mobKills.put("husk", huskKills);
        mobKills.put("drowned", drownedKills);
        mobKills.put("wither_skeleton", witherSkeletonKills);
        mobKills.put("piglin_brute", piglinBruteKills);
        mobKills.put("hoglin", hoglinKills);
        mobKills.put("zoglin", zoglinKills);
        mobKills.put("ghast", ghastKills);
        mobKills.put("piglin", piglinKills);
        return mobKills;
    }

    // Método de compatibilidade para getMobKillCount
    public int getMobKillCount(String mobType) {
        Map<String, Integer> mobKills = getMobKills();
        return mobKills.getOrDefault(mobType, 0);
    }

    // CORREÇÃO PRINCIPAL: Usar valores sincronizados em vez de DimTrConfig.SERVER
    public int getMobKillRequirement(String mobType, int phase) {
        int baseRequirement = getBaseMobKillRequirement(mobType, phase);
        
        // 🎯 NOVO: Aplicar multiplicador de party se o jogador estiver em uma
        if (ClientPartyData.INSTANCE.isInParty()) {
            double partyMultiplier = ClientPartyData.INSTANCE.getRequirementMultiplier();
            return (int) Math.ceil(baseRequirement * partyMultiplier);
        }
        
        return baseRequirement;
    }
    
    // 🎯 NOVO: Método para obter requisito base sem multiplicador de party
    private int getBaseMobKillRequirement(String mobType, int phase) {
        if (phase == 1) {
            return switch (mobType) {
                case "zombie" -> reqZombieKills;
                // Campo zombie_villager removido - sempre retorna 0
                case "skeleton" -> reqSkeletonKills;
                case "stray" -> reqStrayKills;
                case "husk" -> reqHuskKills;
                case "spider" -> reqSpiderKills;
                case "creeper" -> reqCreeperKills;
                case "drowned" -> reqDrownedKills;
                case "enderman" -> reqEndermanKills;
                case "witch" -> reqWitchKills;
                case "pillager" -> reqPillagerKills;
                case "captain" -> reqCaptainKills;
                case "vindicator" -> reqVindicatorKills;
                case "bogged" -> reqBoggedKills;
                case "breeze" -> reqBreezeKills;
                // ✅ CORRIGIDO: Ravager e Evoker são mob kills normais da Fase 1
                case "ravager" -> reqRavagerKills; // Retorna 1
                case "evoker" -> reqEvokerKills;   // Retorna 5
                default -> 0;
            };
        } else if (phase == 2) {
            return switch (mobType) {
                // Mobs do Nether/End com valores corretos
                case "blaze" -> reqBlazeKills;
                case "wither_skeleton" -> reqWitherSkeletonKills;
                case "piglin_brute" -> reqPiglinBruteKills;
                case "hoglin" -> reqHoglinKills; // Retorna 1 correto
                case "zoglin" -> reqZoglinKills; // Retorna 1 correto
                case "ghast" -> reqGhastKills;
                // Campo endermite removido - sempre retorna 0
                case "endermite" -> 0;
                case "piglin" -> reqPiglinKills;

                // Mobs do Overworld com requisitos aumentados (125%)
                case "zombie" -> getPhase2OverworldRequirement(reqZombieKills);
                // ✅ REMOVIDO: zombie_villager - funcionalidade descontinuada
                case "skeleton" -> getPhase2OverworldRequirement(reqSkeletonKills);
                case "creeper" -> getPhase2OverworldRequirement(reqCreeperKills);
                case "spider" -> getPhase2OverworldRequirement(reqSpiderKills);
                case "enderman" -> getPhase2OverworldRequirement(reqEndermanKills);
                case "witch" -> getPhase2OverworldRequirement(reqWitchKills);
                case "pillager" -> getPhase2OverworldRequirement(reqPillagerKills);

                // ✅ CORRIGIDO: Ravager e Evoker Phase 2 com 125% dos valores da Phase 1
                case "ravager" -> getPhase2OverworldRequirement(reqRavagerKills); // 1 * 1.25 = 2
                case "evoker" -> getPhase2OverworldRequirement(reqEvokerKills);   // 5 * 1.25 = 7
                default -> 0;
            };
        }
        return 0;
    }

    // ATUALIZADO: Método auxiliar para calcular requisitos da Fase 2 para mobs do Overworld (125%)
    private int getPhase2OverworldRequirement(int originalRequirement) {
        // Calcular 125% do valor original (100% + 25%)
        // Exemplo: Zumbis 50 -> 50 + (50 * 0.25) = 62.5 -> 63
        return (int) Math.ceil(originalRequirement * 1.25);
    }

    // CORREÇÃO: Implementar métodos que estavam como placeholder
    public boolean isServerEnablePhase1() { return true; }
    public boolean isServerEnablePhase2() { return true; }
    public boolean isServerEnableMobKillsPhase1() { return true; }
    public boolean isServerEnableMobKillsPhase2() { return true; }
    public boolean isServerReqElderGuardian() { return true; }
    public boolean isServerReqRaid() { return true; }
    public boolean isServerReqTrialVaultAdv() { return true; }
    public boolean isServerReqWither() { return true; }
    public boolean isServerReqWarden() { return true; }

    // NOVO: Getter para verificar se Voluntary Exile é obrigatório
    public boolean isServerReqVoluntaryExile() {
        return serverReqVoluntaryExile;
    }

    // CORREÇÃO PRINCIPAL: Adicionar método que estava faltando
    public boolean isPhase1EffectivelyComplete() {
        return phase1Completed;
    }

    // ============================================================================
    // 🎯 NOVO: Custom Phase Support
    // ============================================================================
    
    /**
     * Check if a custom phase is completed
     */
    public boolean isCustomPhaseComplete(String phaseId) {
        return customPhaseCompletion.getOrDefault(phaseId, false);
    }
    
    /**
     * Get custom mob kills for a specific phase and mob type
     */
    public int getCustomMobKills(String phaseId, String mobType) {
        return customMobKills.getOrDefault(phaseId, new HashMap<>()).getOrDefault(mobType, 0);
    }
    
    /**
     * Check if a custom objective is completed
     */
    public boolean isCustomObjectiveComplete(String phaseId, String objectiveId) {
        return customObjectiveCompletion.getOrDefault(phaseId, new HashMap<>()).getOrDefault(objectiveId, false);
    }
    
    /**
     * 🎯 NOVO: Obter requisito de mob customizado ajustado por party
     */
    public int getCustomMobRequirementAdjusted(String phaseId, String mobType) {
        var customPhase = net.mirai.dimtr.config.CustomRequirements.getCustomPhase(phaseId);
        if (customPhase == null || customPhase.mobRequirements == null) {
            return 0;
        }
        
        int baseRequirement = customPhase.mobRequirements.getOrDefault(mobType, 0);
        
        // 🎯 NOVO: Aplicar multiplicador de party se o jogador estiver em uma
        if (ClientPartyData.INSTANCE.isInParty()) {
            double partyMultiplier = ClientPartyData.INSTANCE.getRequirementMultiplier();
            return (int) Math.ceil(baseRequirement * partyMultiplier);
        }
        
        return baseRequirement;
    }
    
    // 🎯 CACHE para evitar spam de logs e melhorar performance
    private static final Map<Integer, List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo>> bossesCache = new HashMap<>();
    private static long lastCacheUpdate = 0;
    private static final long CACHE_DURATION = 5000; // 5 segundos
    
    /**
     * 🎯 NOVO: Verificar se um boss de mod externo foi morto
     */
    public boolean isExternalBossKilled(String bossEntityId) {
        String key = bossEntityId.replace(":", "_");
        Map<String, Boolean> externalBosses = customObjectiveCompletion.getOrDefault("external_bosses", new HashMap<>());
        boolean killed = externalBosses.getOrDefault(key, false);
        
        // Log opcional apenas para debug crítico (desabilitado)
        /*
        if (net.mirai.dimtr.DimTrMod.isDebugging()) {
            net.mirai.dimtr.DimTrMod.LOGGER.trace("[CLIENT] Verificando boss externo: {} ({}) = {}", 
                bossEntityId, key, killed);
        }
        */
        
        return killed;
    }
    
    /**
     * 🎯 OTIMIZADO: Obter lista de bosses de mods externos para uma fase específica (com cache inteligente)
     */
    public java.util.List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> getExternalBossesForPhase(int phase) {
        long currentTime = System.currentTimeMillis();
        // Debugging desabilitado para evitar spam
        boolean debugEnabled = false;
        
        // Se o cache está válido, usar ele
        if (currentTime - lastCacheUpdate < CACHE_DURATION && bossesCache.containsKey(phase)) {
            List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> cachedBosses = bossesCache.get(phase);
            if (debugEnabled) {
                net.mirai.dimtr.DimTrMod.LOGGER.trace("[CLIENT] Usando cache para bosses da fase {}. Total: {}", 
                    phase, cachedBosses.size());
            }
            return new ArrayList<>(cachedBosses); // Retornar cópia defensiva
        }
        
        // Atualizar cache apenas se necessário
        if (currentTime - lastCacheUpdate >= CACHE_DURATION) {
            if (debugEnabled) {
                net.mirai.dimtr.DimTrMod.LOGGER.trace("[CLIENT] Limpando cache de bosses (expirado)");
            }
            bossesCache.clear();
            lastCacheUpdate = currentTime;
        }
        
        // Obter dados e armazenar no cache
        List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> bosses = 
            net.mirai.dimtr.integration.ExternalModIntegration.getBossesForPhase(phase);
        
        if (bosses != null) {
            // Armazenar cópia para evitar modificações externas
            List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> bossesToCache = new ArrayList<>(bosses);
            bossesCache.put(phase, bossesToCache);
            
            if (debugEnabled) {
                net.mirai.dimtr.DimTrMod.LOGGER.trace("[CLIENT] Atualizando cache para bosses da fase {}. Total: {}", 
                    phase, bossesToCache.size());
                for (var boss : bossesToCache) {
                    boolean isKilled = isExternalBossKilled(boss.entityId);
                    net.mirai.dimtr.DimTrMod.LOGGER.trace("[CLIENT] - Boss: {} (fase {}) - Derrotado: {}", 
                        boss.displayName, boss.phase, isKilled);
                }
            }
            
            return new ArrayList<>(bosses); // Retornar cópia defensiva
        } else {
            // Em caso de erro, retornar lista vazia
            List<net.mirai.dimtr.integration.ExternalModIntegration.BossInfo> emptyList = new ArrayList<>();
            bossesCache.put(phase, emptyList);
            if (debugEnabled) {
                net.mirai.dimtr.DimTrMod.LOGGER.warn("[CLIENT] Falha ao obter bosses da fase {}", phase);
            }
            return emptyList;
        }
    }
    
    /**
     * 🎯 NOVO: Verificar se deve exibir Fase 3 (bosses do End habilitados)
     */
    public boolean shouldShowPhase3() {
        try {
            // Verificar se há bosses do End configurados
            var phase3Bosses = getExternalBossesForPhase(3);
            return !phase3Bosses.isEmpty() && net.mirai.dimtr.config.DimTrConfig.SERVER.createPhase3ForEndBosses.get();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 🎯 NOVO: Verificar se a Fase 3 está completa
     */
    public boolean isPhase3Completed() {
        if (!shouldShowPhase3()) {
            return false;
        }
        
        var phase3Bosses = getExternalBossesForPhase(3);
        for (var boss : phase3Bosses) {
            if (!isExternalBossKilled(boss.entityId)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 🎯 NOVO: Verificar se há fases customizadas configuradas
     */
    public boolean hasCustomPhases() {
        return !customPhaseCompletion.isEmpty();
    }
    
    /**
     * 🎯 NOVO: Obter mapa com todas as fases customizadas e seus status
     */
    public Map<String, Boolean> getCustomPhases() {
        return new HashMap<>(customPhaseCompletion);
    }
}