package net.mirai.dimtr.network;

import net.mirai.dimtr.DimTrMod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Sistema de networking completo para Dimension Trials
 *
 * ✅ Sistema de progressão individual
 * ✅ Sistema de parties colaborativas
 * ✅ Sincronização robusta cliente-servidor
 * ✅ Compatibilidade completa com NeoForge
 */
public class ModNetworking {

    /**
     * Registrar todos os payloads de rede do mod
     *
     * Este método é chamado automaticamente pelo sistema de eventos
     * durante a inicialização do mod via DimTrMod.java
     */
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // Obter registrador usando o MODID correto
        PayloadRegistrar registrar = event.registrar(DimTrMod.MODID);

        // ============================================================================
        // 🎯 SISTEMA DE PROGRESSÃO INDIVIDUAL
        // ============================================================================
        registrar.playToClient(
                UpdateProgressionToClientPayload.TYPE,
                UpdateProgressionToClientPayload.STREAM_CODEC,
                UpdateProgressionToClientPayload::handle
        );

        // ============================================================================
        // 🎯 SISTEMA DE PARTIES COLABORATIVAS
        // ============================================================================
        registrar.playToClient(
                UpdatePartyToClientPayload.TYPE,
                UpdatePartyToClientPayload.STREAM_CODEC,
                UpdatePartyToClientPayload::handle
        );

        // ============================================================================
        // 🎯 LOG DE CONFIRMAÇÃO
        // ============================================================================
        DimTrMod.LOGGER.info("✅ Network payloads registered successfully:");
        DimTrMod.LOGGER.info("   • UpdateProgressionToClientPayload (Individual progression sync)");
        DimTrMod.LOGGER.info("   • UpdatePartyToClientPayload (Party system sync)");
        DimTrMod.LOGGER.info("🎯 All networking systems are operational!");
    }
}