package net.mirai.dimtr.network;

import io.netty.buffer.ByteBuf;
import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateProgressionToClientPayload(
        // Objetivos originais (mantidos para compatibilidade)
        boolean elderGuardianKilled,
        boolean raidWon,
        boolean ravagerKilled, // Compatibilidade
        boolean evokerKilled, // Compatibilidade
        boolean trialVaultAdvancementEarned,
        boolean voluntaireExileAdvancementEarned,
        boolean phase1Completed,
        boolean witherKilled,
        boolean wardenKilled,
        boolean phase2Completed,

        // Contadores de mobs - Fase 1
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
        int ravagerKills,
        int evokerKills,

        // Contadores de mobs - Fase 2
        int blazeKills,
        int witherSkeletonKills,
        int piglinBruteKills,
        int hoglinKills,
        int zoglinKills,
        int ghastKills,
        int endermiteKills,
        int piglinKills,

        // NOVO: Configurações de requisitos sincronizadas
        int reqZombieKills,
        int reqZombieVillagerKills,
        int reqSkeletonKills,
        int reqStrayKills,
        int reqHuskKills,
        int reqSpiderKills,
        int reqCreeperKills,
        int reqDrownedKills,
        int reqEndermanKills,
        int reqWitchKills,
        int reqPillagerKills,
        int reqCaptainKills,
        int reqVindicatorKills,
        int reqBoggedKills,
        int reqBreezeKills,
        int reqRavagerKills, // VALOR CORRETO: 1
        int reqEvokerKills,  // VALOR CORRETO: 5
        int reqBlazeKills,
        int reqWitherSkeletonKills,
        int reqPiglinBruteKills,
        int reqHoglinKills,  // VALOR CORRETO: 1
        int reqZoglinKills,  // VALOR CORRETO: 1
        int reqGhastKills,
        int reqEndermiteKills,
        int reqPiglinKills
) implements CustomPacketPayload {

    public static final Type<UpdateProgressionToClientPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "update_progression_to_client"));

    public static final StreamCodec<ByteBuf, UpdateProgressionToClientPayload> STREAM_CODEC =
            StreamCodec.of(UpdateProgressionToClientPayload::encode, UpdateProgressionToClientPayload::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(ByteBuf buf, UpdateProgressionToClientPayload payload) {
        // Objetivos (booleans)
        buf.writeBoolean(payload.elderGuardianKilled);
        buf.writeBoolean(payload.raidWon);
        buf.writeBoolean(payload.ravagerKilled);
        buf.writeBoolean(payload.evokerKilled);
        buf.writeBoolean(payload.trialVaultAdvancementEarned);
        buf.writeBoolean(payload.voluntaireExileAdvancementEarned);
        buf.writeBoolean(payload.phase1Completed);
        buf.writeBoolean(payload.witherKilled);
        buf.writeBoolean(payload.wardenKilled);
        buf.writeBoolean(payload.phase2Completed);

        // Contadores Fase 1
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
        buf.writeInt(payload.ravagerKills);
        buf.writeInt(payload.evokerKills);

        // Contadores Fase 2
        buf.writeInt(payload.blazeKills);
        buf.writeInt(payload.witherSkeletonKills);
        buf.writeInt(payload.piglinBruteKills);
        buf.writeInt(payload.hoglinKills);
        buf.writeInt(payload.zoglinKills);
        buf.writeInt(payload.ghastKills);
        buf.writeInt(payload.endermiteKills);
        buf.writeInt(payload.piglinKills);

        // NOVO: Configurações de requisitos
        buf.writeInt(payload.reqZombieKills);
        buf.writeInt(payload.reqZombieVillagerKills);
        buf.writeInt(payload.reqSkeletonKills);
        buf.writeInt(payload.reqStrayKills);
        buf.writeInt(payload.reqHuskKills);
        buf.writeInt(payload.reqSpiderKills);
        buf.writeInt(payload.reqCreeperKills);
        buf.writeInt(payload.reqDrownedKills);
        buf.writeInt(payload.reqEndermanKills);
        buf.writeInt(payload.reqWitchKills);
        buf.writeInt(payload.reqPillagerKills);
        buf.writeInt(payload.reqCaptainKills);
        buf.writeInt(payload.reqVindicatorKills);
        buf.writeInt(payload.reqBoggedKills);
        buf.writeInt(payload.reqBreezeKills);
        buf.writeInt(payload.reqRavagerKills);
        buf.writeInt(payload.reqEvokerKills);
        buf.writeInt(payload.reqBlazeKills);
        buf.writeInt(payload.reqWitherSkeletonKills);
        buf.writeInt(payload.reqPiglinBruteKills);
        buf.writeInt(payload.reqHoglinKills);
        buf.writeInt(payload.reqZoglinKills);
        buf.writeInt(payload.reqGhastKills);
        buf.writeInt(payload.reqEndermiteKills);
        buf.writeInt(payload.reqPiglinKills);
    }

    private static UpdateProgressionToClientPayload decode(ByteBuf buf) {
        // Objetivos
        boolean elderGuardianKilled = buf.readBoolean();
        boolean raidWon = buf.readBoolean();
        boolean ravagerKilled = buf.readBoolean();
        boolean evokerKilled = buf.readBoolean();
        boolean trialVaultAdvancementEarned = buf.readBoolean();
        boolean voluntaireExileAdvancementEarned = buf.readBoolean();
        boolean phase1Completed = buf.readBoolean();
        boolean witherKilled = buf.readBoolean();
        boolean wardenKilled = buf.readBoolean();
        boolean phase2Completed = buf.readBoolean();

        // Contadores Fase 1
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
        int ravagerKills = buf.readInt();
        int evokerKills = buf.readInt();

        // Contadores Fase 2
        int blazeKills = buf.readInt();
        int witherSkeletonKills = buf.readInt();
        int piglinBruteKills = buf.readInt();
        int hoglinKills = buf.readInt();
        int zoglinKills = buf.readInt();
        int ghastKills = buf.readInt();
        int endermiteKills = buf.readInt();
        int piglinKills = buf.readInt();

        // NOVO: Configurações de requisitos
        int reqZombieKills = buf.readInt();
        int reqZombieVillagerKills = buf.readInt();
        int reqSkeletonKills = buf.readInt();
        int reqStrayKills = buf.readInt();
        int reqHuskKills = buf.readInt();
        int reqSpiderKills = buf.readInt();
        int reqCreeperKills = buf.readInt();
        int reqDrownedKills = buf.readInt();
        int reqEndermanKills = buf.readInt();
        int reqWitchKills = buf.readInt();
        int reqPillagerKills = buf.readInt();
        int reqCaptainKills = buf.readInt();
        int reqVindicatorKills = buf.readInt();
        int reqBoggedKills = buf.readInt();
        int reqBreezeKills = buf.readInt();
        int reqRavagerKills = buf.readInt();
        int reqEvokerKills = buf.readInt();
        int reqBlazeKills = buf.readInt();
        int reqWitherSkeletonKills = buf.readInt();
        int reqPiglinBruteKills = buf.readInt();
        int reqHoglinKills = buf.readInt();
        int reqZoglinKills = buf.readInt();
        int reqGhastKills = buf.readInt();
        int reqEndermiteKills = buf.readInt();
        int reqPiglinKills = buf.readInt();

        return new UpdateProgressionToClientPayload(
                elderGuardianKilled, raidWon, ravagerKilled, evokerKilled, trialVaultAdvancementEarned,
                voluntaireExileAdvancementEarned, phase1Completed, witherKilled, wardenKilled, phase2Completed,
                zombieKills, zombieVillagerKills, skeletonKills, strayKills, huskKills, spiderKills,
                creeperKills, drownedKills, endermanKills, witchKills, pillagerKills, captainKills,
                vindicatorKills, boggedKills, breezeKills, ravagerKills, evokerKills,
                blazeKills, witherSkeletonKills, piglinBruteKills, hoglinKills, zoglinKills,
                ghastKills, endermiteKills, piglinKills,
                reqZombieKills, reqZombieVillagerKills, reqSkeletonKills, reqStrayKills, reqHuskKills,
                reqSpiderKills, reqCreeperKills, reqDrownedKills, reqEndermanKills, reqWitchKills,
                reqPillagerKills, reqCaptainKills, reqVindicatorKills, reqBoggedKills, reqBreezeKills,
                reqRavagerKills, reqEvokerKills, reqBlazeKills, reqWitherSkeletonKills, reqPiglinBruteKills,
                reqHoglinKills, reqZoglinKills, reqGhastKills, reqEndermiteKills, reqPiglinKills
        );
    }

    public static void handle(UpdateProgressionToClientPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // DEBUG: Logs detalhados para diagnóstico
            System.out.println("=== PAYLOAD HANDLER CALLED ON CLIENT ===");
            System.out.println("Payload received with values:");
            System.out.println("Ravager req: " + payload.reqRavagerKills() + " (should be 1)");
            System.out.println("Evoker req: " + payload.reqEvokerKills() + " (should be 5)");
            System.out.println("Hoglin req: " + payload.reqHoglinKills() + " (should be 1)");
            System.out.println("Zoglin req: " + payload.reqZoglinKills() + " (should be 1)");

            // Verificar se o ClientProgressionData está sendo atualizado
            System.out.println("Updating ClientProgressionData...");
            try {
                ClientProgressionData.INSTANCE.updateData(payload);
                System.out.println("✅ ClientProgressionData updated successfully!");
            } catch (Exception e) {
                System.err.println("❌ Failed to update ClientProgressionData: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}