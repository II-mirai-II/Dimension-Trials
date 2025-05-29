package net.mirai.dimtr.network; // Pacote atualizado

import net.mirai.dimtr.DimTrMod; // Pacote e classe principal atualizados
import net.mirai.dimtr.client.ClientProgressionData; // Import adicionado
// UpdateProgressionToClientPayload será desta mesma pasta/pacote (net.mirai.dimtr.network)
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
// import net.neoforged.neoforge.network.handling.IPayloadHandler; // Não é mais IPayloadHandler diretamente, mas sim o método de handle.
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkManager {

    public static final String NETWORK_VERSION = "1.0";

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        // MODID atualizado para o novo mod
        final PayloadRegistrar registrar = event.registrar(DimTrMod.MODID).versioned(NETWORK_VERSION);

        // Payload é S->C (Servidor para Cliente)
        // UpdateProgressionToClientPayload será da classe no novo pacote net.mirai.dimtr.network
        registrar.playToClient(
                UpdateProgressionToClientPayload.TYPE,
                UpdateProgressionToClientPayload.STREAM_CODEC,
                ClientPayloadHandler::handleUpdateProgression // Handler apenas no cliente
        );
        DimTrMod.LOGGER.info("Registered network payloads"); // Logger atualizado
    }

    public static class ClientPayloadHandler {
        public static void handleUpdateProgression(final UpdateProgressionToClientPayload payload, final IPayloadContext context) {
            context.enqueueWork(() -> {
                // Referência a ClientProgressionData atualizada para usar o import
                ClientProgressionData.INSTANCE.updateData(payload);
                DimTrMod.LOGGER.debug("Client received progression update: Phase1 {}, Phase2 {}", // Logger atualizado
                        payload.phase1Completed(), payload.phase2Completed());
            });
        }
    }
}