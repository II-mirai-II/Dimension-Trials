package net.mirai.dimtr.network;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.client.ClientPartyData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpdatePartyToClientPayload(
        UUID partyId,
        String partyName,
        boolean isPublic, // 🎯 NOVO: Se a party é pública ou privada
        UUID leaderId,
        List<UUID> members,
        double progressionMultiplier,
        // 🔧 ADICIONADO: Campos que estavam faltando
        int memberCount,
        Map<String, Integer> sharedMobKills,
        boolean sharedElderGuardianKilled,
        boolean sharedRaidWon,
        boolean sharedTrialVaultAdvancementEarned,
        boolean sharedVoluntaireExileAdvancementEarned,
        boolean sharedWitherKilled,
        boolean sharedWardenKilled,
        boolean phase1SharedCompleted,
        boolean phase2SharedCompleted,

        // 🎯 NOVO: Custom Phases data
        Map<String, Boolean> sharedCustomPhaseCompletion,
        Map<String, Map<String, Integer>> sharedCustomMobKills,
        Map<String, Map<String, Boolean>> sharedCustomObjectiveCompletion
) implements CustomPacketPayload {

    public static final Type<UpdatePartyToClientPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(DimTrMod.MODID, "update_party_to_client")
    );

    // ✅ CORRIGIDO: Codec personalizado para UUID com suporte a null
    private static final StreamCodec<FriendlyByteBuf, UUID> UUID_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, UUID uuid) {
            buf.writeBoolean(uuid != null);
            if (uuid != null) {
                buf.writeUUID(uuid);
            }
        }

        @Override
        public UUID decode(FriendlyByteBuf buf) {
            boolean hasValue = buf.readBoolean();
            return hasValue ? buf.readUUID() : null;
        }
    };

    public static final StreamCodec<FriendlyByteBuf, UpdatePartyToClientPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, UpdatePartyToClientPayload payload) {
            // Dados básicos da party
            UUID_STREAM_CODEC.encode(buf, payload.partyId);
            ByteBufCodecs.STRING_UTF8.encode(buf, payload.partyName);
            buf.writeBoolean(payload.isPublic); // 🎯 ADICIONADO: Encode isPublic
            UUID_STREAM_CODEC.encode(buf, payload.leaderId);

            // Lista de membros
            ByteBufCodecs.VAR_INT.encode(buf, payload.members.size());
            for (UUID member : payload.members) {
                UUID_STREAM_CODEC.encode(buf, member);
            }

            ByteBufCodecs.DOUBLE.encode(buf, payload.progressionMultiplier);
            ByteBufCodecs.VAR_INT.encode(buf, payload.memberCount);

            // Shared mob kills
            ByteBufCodecs.VAR_INT.encode(buf, payload.sharedMobKills.size());
            for (Map.Entry<String, Integer> entry : payload.sharedMobKills.entrySet()) {
                ByteBufCodecs.STRING_UTF8.encode(buf, entry.getKey());
                ByteBufCodecs.VAR_INT.encode(buf, entry.getValue());
            }

            // Objetivos especiais
            buf.writeBoolean(payload.sharedElderGuardianKilled);
            buf.writeBoolean(payload.sharedRaidWon);
            buf.writeBoolean(payload.sharedTrialVaultAdvancementEarned);
            buf.writeBoolean(payload.sharedVoluntaireExileAdvancementEarned);
            buf.writeBoolean(payload.sharedWitherKilled);
            buf.writeBoolean(payload.sharedWardenKilled);
            buf.writeBoolean(payload.phase1SharedCompleted);
            buf.writeBoolean(payload.phase2SharedCompleted);

            // 🎯 NOVO: Custom Phases data
            ByteBufCodecs.VAR_INT.encode(buf, payload.sharedCustomPhaseCompletion.size());
            for (Map.Entry<String, Boolean> entry : payload.sharedCustomPhaseCompletion.entrySet()) {
                ByteBufCodecs.STRING_UTF8.encode(buf, entry.getKey());
                buf.writeBoolean(entry.getValue());
            }

            ByteBufCodecs.VAR_INT.encode(buf, payload.sharedCustomMobKills.size());
            for (Map.Entry<String, Map<String, Integer>> entry : payload.sharedCustomMobKills.entrySet()) {
                ByteBufCodecs.STRING_UTF8.encode(buf, entry.getKey());
                ByteBufCodecs.VAR_INT.encode(buf, entry.getValue().size());
                for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
                    ByteBufCodecs.STRING_UTF8.encode(buf, subEntry.getKey());
                    ByteBufCodecs.VAR_INT.encode(buf, subEntry.getValue());
                }
            }

            ByteBufCodecs.VAR_INT.encode(buf, payload.sharedCustomObjectiveCompletion.size());
            for (Map.Entry<String, Map<String, Boolean>> entry : payload.sharedCustomObjectiveCompletion.entrySet()) {
                ByteBufCodecs.STRING_UTF8.encode(buf, entry.getKey());
                ByteBufCodecs.VAR_INT.encode(buf, entry.getValue().size());
                for (Map.Entry<String, Boolean> subEntry : entry.getValue().entrySet()) {
                    ByteBufCodecs.STRING_UTF8.encode(buf, subEntry.getKey());
                    buf.writeBoolean(subEntry.getValue());
                }
            }
        }

        @Override
        public UpdatePartyToClientPayload decode(FriendlyByteBuf buf) {
            // Dados básicos da party
            UUID partyId = UUID_STREAM_CODEC.decode(buf);
            String partyName = ByteBufCodecs.STRING_UTF8.decode(buf);
            boolean isPublic = buf.readBoolean(); // 🎯 ADICIONADO: Decode isPublic
            UUID leaderId = UUID_STREAM_CODEC.decode(buf);

            // Lista de membros
            int memberListSize = ByteBufCodecs.VAR_INT.decode(buf);
            List<UUID> members = new ArrayList<>();
            for (int i = 0; i < memberListSize; i++) {
                members.add(UUID_STREAM_CODEC.decode(buf));
            }

            double progressionMultiplier = ByteBufCodecs.DOUBLE.decode(buf);
            int memberCount = ByteBufCodecs.VAR_INT.decode(buf);

            // Shared mob kills
            int mobKillsSize = ByteBufCodecs.VAR_INT.decode(buf);
            Map<String, Integer> sharedMobKills = new HashMap<>();
            for (int i = 0; i < mobKillsSize; i++) {
                String mobType = ByteBufCodecs.STRING_UTF8.decode(buf);
                int kills = ByteBufCodecs.VAR_INT.decode(buf);
                sharedMobKills.put(mobType, kills);
            }

            // Objetivos especiais
            boolean sharedElderGuardianKilled = buf.readBoolean();
            boolean sharedRaidWon = buf.readBoolean();
            boolean sharedTrialVaultAdvancementEarned = buf.readBoolean();
            boolean sharedVoluntaireExileAdvancementEarned = buf.readBoolean();
            boolean sharedWitherKilled = buf.readBoolean();
            boolean sharedWardenKilled = buf.readBoolean();
            boolean phase1SharedCompleted = buf.readBoolean();
            boolean phase2SharedCompleted = buf.readBoolean();

            // 🎯 NOVO: Custom Phases data
            int customPhaseSize = ByteBufCodecs.VAR_INT.decode(buf);
            Map<String, Boolean> sharedCustomPhaseCompletion = new HashMap<>();
            for (int i = 0; i < customPhaseSize; i++) {
                String phaseId = ByteBufCodecs.STRING_UTF8.decode(buf);
                boolean completed = buf.readBoolean();
                sharedCustomPhaseCompletion.put(phaseId, completed);
            }

            int customMobKillsSize = ByteBufCodecs.VAR_INT.decode(buf);
            Map<String, Map<String, Integer>> sharedCustomMobKills = new HashMap<>();
            for (int i = 0; i < customMobKillsSize; i++) {
                String mobType = ByteBufCodecs.STRING_UTF8.decode(buf);
                int customKillCountSize = ByteBufCodecs.VAR_INT.decode(buf);
                Map<String, Integer> killCounts = new HashMap<>();
                for (int j = 0; j < customKillCountSize; j++) {
                    String customId = ByteBufCodecs.STRING_UTF8.decode(buf);
                    int count = ByteBufCodecs.VAR_INT.decode(buf);
                    killCounts.put(customId, count);
                }
                sharedCustomMobKills.put(mobType, killCounts);
            }

            int customObjectivesSize = ByteBufCodecs.VAR_INT.decode(buf);
            Map<String, Map<String, Boolean>> sharedCustomObjectiveCompletion = new HashMap<>();
            for (int i = 0; i < customObjectivesSize; i++) {
                String objectiveId = ByteBufCodecs.STRING_UTF8.decode(buf);
                int customObjectiveSize = ByteBufCodecs.VAR_INT.decode(buf);
                Map<String, Boolean> customObjectives = new HashMap<>();
                for (int j = 0; j < customObjectiveSize; j++) {
                    String customId = ByteBufCodecs.STRING_UTF8.decode(buf);
                    boolean completed = buf.readBoolean();
                    customObjectives.put(customId, completed);
                }
                sharedCustomObjectiveCompletion.put(objectiveId, customObjectives);
            }

            return new UpdatePartyToClientPayload(
                    partyId, partyName, isPublic, leaderId, members, progressionMultiplier,
                    memberCount, sharedMobKills, sharedElderGuardianKilled, sharedRaidWon,
                    sharedTrialVaultAdvancementEarned, sharedVoluntaireExileAdvancementEarned,
                    sharedWitherKilled, sharedWardenKilled, phase1SharedCompleted, phase2SharedCompleted,
                    sharedCustomPhaseCompletion, sharedCustomMobKills, sharedCustomObjectiveCompletion
            );
        }
    };

    public static void handle(UpdatePartyToClientPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                // 🔧 CORRIGIDO: Usar INSTANCE em vez de método estático
                ClientPartyData.INSTANCE.updateData(payload);
                DimTrMod.LOGGER.info(Constants.LOG_PARTY_DATA_UPDATED,
                        payload.partyName, payload.members.size());
            } catch (Exception e) {
                DimTrMod.LOGGER.error("Failed to update party data on client", e);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}