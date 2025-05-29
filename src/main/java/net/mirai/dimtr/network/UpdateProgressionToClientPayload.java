package net.mirai.dimtr.network; // Pacote atualizado

import io.netty.buffer.ByteBuf;
import net.mirai.dimtr.DimTrMod; // Pacote e classe principal atualizados
import net.mirai.dimtr.data.ProgressionData; // Pacote de ProgressionData atualizado
// import net.minecraft.network.codec.ByteBufCodecs; // Não estava sendo usado diretamente, mantido comentado se você não precisar.
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateProgressionToClientPayload(
        boolean elderGuardianKilled,
        boolean raidWon,
        boolean ravagerKilled,
        boolean evokerKilled,
        boolean trialVaultAdvancementEarned,
        boolean phase1Completed,
        boolean witherKilled,
        boolean wardenKilled,
        boolean phase2Completed
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateProgressionToClientPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimTrMod.MODID, "update_progression")); // MODID atualizado

    public static final StreamCodec<ByteBuf, UpdateProgressionToClientPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeBoolean(payload.elderGuardianKilled());
                        buf.writeBoolean(payload.raidWon());
                        buf.writeBoolean(payload.ravagerKilled());
                        buf.writeBoolean(payload.evokerKilled());
                        buf.writeBoolean(payload.trialVaultAdvancementEarned());
                        buf.writeBoolean(payload.phase1Completed());
                        buf.writeBoolean(payload.witherKilled());
                        buf.writeBoolean(payload.wardenKilled());
                        buf.writeBoolean(payload.phase2Completed());
                    },
                    buf -> new UpdateProgressionToClientPayload(
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean()
                    )
            );

    // Construtor usando ProgressionData do pacote net.mirai.dimtr.data
    public UpdateProgressionToClientPayload(ProgressionData data) {
        this(
                data.elderGuardianKilled,
                data.raidWon,
                data.ravagerKilled,
                data.evokerKilled,
                data.trialVaultAdvancementEarned,
                data.phase1Completed,
                data.witherKilled,
                data.wardenKilled,
                data.phase2Completed
        );
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}