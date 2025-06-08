package net.mirai.dimtr.network;

import net.mirai.dimtr.DimTrMod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(DimTrMod.MODID);

        // CORREÇÃO: Registrar o payload com o handler correto
        registrar.playToClient(
                UpdateProgressionToClientPayload.TYPE,
                UpdateProgressionToClientPayload.STREAM_CODEC,
                UpdateProgressionToClientPayload::handle
        );

        DimTrMod.LOGGER.info("Registered UpdateProgressionToClientPayload network handler");
    }
}