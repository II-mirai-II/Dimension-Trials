package net.mirai.dimtr.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkManager {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        // Registrar o payload para comunicação cliente-servidor
        registrar.playToClient(
                UpdateProgressionToClientPayload.TYPE,
                UpdateProgressionToClientPayload.STREAM_CODEC,
                UpdateProgressionToClientPayload::handle
        );
    }
}