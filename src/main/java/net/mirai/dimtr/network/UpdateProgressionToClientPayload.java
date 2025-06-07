package net.mirai.dimtr.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateProgressionToClientPayload(
        // Objetivos originais
        boolean elderGuardianKilled,
        boolean raidWon,
        boolean ravagerKilled, // Manter para compatibilidade
        boolean evokerKilled, // Manter para compatibilidade
        boolean trialVaultAdvancementEarned,
        boolean phase1Completed,
        boolean witherKilled,
        boolean wardenKilled,
        boolean phase2Completed,

        // Novos contadores de mobs - Fase 1 (Overworld)
        int zombieKills,
        int zombieVillagerKills,
        int skeletonKills,
        int strayKills,
        int huskKills,
        int spiderKills,
        int creeperKills,
        int drownedKills,
        int endermanKills,
        int witchKills,
        int pillagerKills,
        int captainKills,
        int vindicatorKills,
        int boggedKills,
        int breezeKills,

        // NOVO: Ravager e Evoker como Goal Kills
        int ravagerKills,
        int evokerKills,

        // Novos contadores de mobs - Fase 2 (Nether + High Level)
        int blazeKills,
        int witherSkeletonKills,
        int piglinBruteKills,
        int hoglinKills,
        int zoglinKills,
        int ghastKills,
        int endermiteKills,
        int piglinKills
) implements CustomPacketPayload {

    public static final Type<UpdateProgressionToClientPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "update_progression_to_client"));

    // CORREÇÃO: Usar encode/decode manual para muitos campos
    public static final StreamCodec<ByteBuf, UpdateProgressionToClientPayload> STREAM_CODEC =
            StreamCodec.of(UpdateProgressionToClientPayload::encode, UpdateProgressionToClientPayload::decode);

    private static void encode(ByteBuf buf, UpdateProgressionToClientPayload payload) {
        // Objetivos originais (booleans)
        buf.writeBoolean(payload.elderGuardianKilled);
        buf.writeBoolean(payload.raidWon);
        buf.writeBoolean(payload.ravagerKilled);
        buf.writeBoolean(payload.evokerKilled);
        buf.writeBoolean(payload.trialVaultAdvancementEarned);
        buf.writeBoolean(payload.phase1Completed);
        buf.writeBoolean(payload.witherKilled);
        buf.writeBoolean(payload.wardenKilled);
        buf.writeBoolean(payload.phase2Completed);

        // Fase 1 mob kills (integers)
        buf.writeInt(payload.zombieKills);
        buf.writeInt(payload.zombieVillagerKills);
        buf.writeInt(payload.skeletonKills);
        buf.writeInt(payload.strayKills);
        buf.writeInt(payload.huskKills);
        buf.writeInt(payload.spiderKills);
        buf.writeInt(payload.creeperKills);
        buf.writeInt(payload.drownedKills);
        buf.writeInt(payload.endermanKills);
        buf.writeInt(payload.witchKills);
        buf.writeInt(payload.pillagerKills);
        buf.writeInt(payload.captainKills);
        buf.writeInt(payload.vindicatorKills);
        buf.writeInt(payload.boggedKills);
        buf.writeInt(payload.breezeKills);

        // NOVO: Ravager e Evoker Goal Kills
        buf.writeInt(payload.ravagerKills);
        buf.writeInt(payload.evokerKills);

        // Fase 2 mob kills (integers)
        buf.writeInt(payload.blazeKills);
        buf.writeInt(payload.witherSkeletonKills);
        buf.writeInt(payload.piglinBruteKills);
        buf.writeInt(payload.hoglinKills);
        buf.writeInt(payload.zoglinKills);
        buf.writeInt(payload.ghastKills);
        buf.writeInt(payload.endermiteKills);
        buf.writeInt(payload.piglinKills);
    }

    private static UpdateProgressionToClientPayload decode(ByteBuf buf) {
        // Objetivos originais (booleans)
        boolean elderGuardianKilled = buf.readBoolean();
        boolean raidWon = buf.readBoolean();
        boolean ravagerKilled = buf.readBoolean();
        boolean evokerKilled = buf.readBoolean();
        boolean trialVaultAdvancementEarned = buf.readBoolean();
        boolean phase1Completed = buf.readBoolean();
        boolean witherKilled = buf.readBoolean();
        boolean wardenKilled = buf.readBoolean();
        boolean phase2Completed = buf.readBoolean();

        // Fase 1 mob kills (integers)
        int zombieKills = buf.readInt();
        int zombieVillagerKills = buf.readInt();
        int skeletonKills = buf.readInt();
        int strayKills = buf.readInt();
        int huskKills = buf.readInt();
        int spiderKills = buf.readInt();
        int creeperKills = buf.readInt();
        int drownedKills = buf.readInt();
        int endermanKills = buf.readInt();
        int witchKills = buf.readInt();
        int pillagerKills = buf.readInt();
        int captainKills = buf.readInt();
        int vindicatorKills = buf.readInt();
        int boggedKills = buf.readInt();
        int breezeKills = buf.readInt();

        // NOVO: Ravager e Evoker Goal Kills
        int ravagerKills = buf.readInt();
        int evokerKills = buf.readInt();

        // Fase 2 mob kills (integers)
        int blazeKills = buf.readInt();
        int witherSkeletonKills = buf.readInt();
        int piglinBruteKills = buf.readInt();
        int hoglinKills = buf.readInt();
        int zoglinKills = buf.readInt();
        int ghastKills = buf.readInt();
        int endermiteKills = buf.readInt();
        int piglinKills = buf.readInt();

        return new UpdateProgressionToClientPayload(
                elderGuardianKilled, raidWon, ravagerKilled, evokerKilled, trialVaultAdvancementEarned,
                phase1Completed, witherKilled, wardenKilled, phase2Completed,
                zombieKills, zombieVillagerKills, skeletonKills, strayKills, huskKills,
                spiderKills, creeperKills, drownedKills, endermanKills, witchKills,
                pillagerKills, captainKills, vindicatorKills, boggedKills, breezeKills,
                ravagerKills, evokerKills, // NOVO
                blazeKills, witherSkeletonKills, piglinBruteKills, hoglinKills, zoglinKills,
                ghastKills, endermiteKills, piglinKills
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // CORREÇÃO: Adicionar método handle() que estava faltando
    public static void handle(UpdateProgressionToClientPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Atualizar dados no cliente
            ClientProgressionData.INSTANCE.updateData(payload);
        });
    }
}