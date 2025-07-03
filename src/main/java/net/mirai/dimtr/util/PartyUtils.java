package net.mirai.dimtr.util;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.data.PartyData;
import net.mirai.dimtr.data.PartyManager;
import net.mirai.dimtr.data.PlayerProgressionData;
import net.mirai.dimtr.data.ProgressionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe utilitária para centralizar operações relacionadas a parties
 * Extraída de várias partes do código para melhorar organização e manutenção
 */
public class PartyUtils {

    /**
     * Calcula o multiplicador de requisitos com base no tamanho da party
     * 
     * @param memberCount Número de membros na party
     * @return Multiplicador de requisitos (1.0 = sem mudança)
     */
    public static double calculateRequirementMultiplier(int memberCount) {
        if (memberCount <= 1) {
            return 1.0; // Sem multiplicador para jogador sozinho
        }

        // Base: 1.0 + 0.5 por membro adicional
        // Ex: 2 membros = 1.5x, 3 membros = 2.0x, 4 membros = 2.5x
        double baseMultiplier = 1.0 + ((memberCount - 1) * 0.5);
        
        // Limitado a um máximo de 3.0x (correspondente a 5+ membros)
        return Math.min(baseMultiplier, 3.0);
    }

    /**
     * Verifica se um jogador está em uma party válida
     * 
     * @param playerId ID do jogador
     * @param serverLevel Nível do servidor
     * @return true se o jogador está em uma party
     */
    public static boolean isPlayerInValidParty(UUID playerId, ServerLevel serverLevel) {
        PartyManager partyManager = PartyManager.get(serverLevel);
        if (partyManager == null) {
            return false;
        }
        
        PartyData party = partyManager.getPlayerParty(playerId);
        return party != null && party.getMembers().size() > 0;
    }

    /**
     * Obtém todos os jogadores online de uma party
     * 
     * @param party Dados da party
     * @param serverLevel Nível do servidor
     * @return Lista de jogadores online
     */
    public static List<ServerPlayer> getOnlinePartyMembers(PartyData party, ServerLevel serverLevel) {
        if (party == null || serverLevel == null) {
            return new ArrayList<>();
        }
        
        List<ServerPlayer> onlineMembers = new ArrayList<>();
        for (UUID memberId : party.getMembers()) {
            ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(memberId);
            if (player != null) {
                onlineMembers.add(player);
            }
        }
        
        return onlineMembers;
    }
    
    /**
     * Notifica todos os membros online de uma party sobre uma conquista
     * 
     * @param party Dados da party
     * @param serverLevel Nível do servidor
     * @param message Mensagem de notificação
     */
    public static void notifyPartyMembers(PartyData party, ServerLevel serverLevel, String message) {
        if (party == null || serverLevel == null || message == null || message.isEmpty()) {
            return;
        }
        
        List<ServerPlayer> onlineMembers = getOnlinePartyMembers(party, serverLevel);
        for (ServerPlayer player : onlineMembers) {
            NotificationHelper.sendNotification(player, NotificationHelper.NotificationType.INFO, message);
        }
    }
    
    /**
     * Verifica se a party completa alcançou um objetivo ou um requisito de mobs
     * 
     * @param party Dados da party
     * @param objectiveName Nome do objetivo para log
     * @param isCompleted Se o objetivo está completo
     */
    public static void checkAndNotifyPartyObjective(PartyData party, ServerLevel serverLevel, 
                                                  String objectiveName, boolean isCompleted) {
        if (isCompleted) {
            // Criar uma mensagem de texto simples para notificação
            String message = "A party " + party.getName() + " completou o objetivo: " + objectiveName;
            notifyPartyMembers(party, serverLevel, message);
            
            DimTrMod.LOGGER.info("Party {} completou objetivo: {}", 
                               party.getLeaderId(), objectiveName);
        }
    }
    
    /**
     * Sincroniza progresso entre jogador e party (em ambas as direções)
     * 
     * @param playerId ID do jogador
     * @param serverLevel Nível do servidor
     */
    public static void syncPlayerWithParty(UUID playerId, ServerLevel serverLevel) {
        if (!isPlayerInValidParty(playerId, serverLevel)) {
            return;
        }
        
        PartyManager partyManager = PartyManager.get(serverLevel);
        ProgressionManager progressionManager = ProgressionManager.get(serverLevel);
        
        PartyData party = partyManager.getPlayerParty(playerId);
        PlayerProgressionData playerData = progressionManager.getPlayerData(playerId);
        
        if (party == null || playerData == null) {
            return;
        }
        
        // Atualizar objetivos principais
        boolean changed = false;
        
        // Sincronizar de jogador para party
        if (playerData.elderGuardianKilled && !party.isSharedElderGuardianKilled()) {
            party.setSharedElderGuardianKilled(true);
            changed = true;
        }
        
        // Sincronizar de party para jogador
        if (party.isSharedElderGuardianKilled() && !playerData.elderGuardianKilled) {
            playerData.elderGuardianKilled = true;
            changed = true;
        }
        
        // Marcar como modificado se houve mudanças
        if (changed) {
            partyManager.setDirty();
            progressionManager.setDirty();
        }
    }
}
