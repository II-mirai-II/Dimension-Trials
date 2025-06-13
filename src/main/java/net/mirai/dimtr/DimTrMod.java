package net.mirai.dimtr;

import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.network.ModNetworking;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe principal do mod Dimension Trials - VERSÃO CORRIGIDA
 *
 * ✅ Sistema de progressão por fases
 * ✅ Sistema de parties colaborativas
 * ✅ HUD modular e interativo
 * ✅ Comandos administrativos e de party (CORRIGIDO)
 * ✅ Networking robusto (CORRIGIDO)
 */
@Mod(DimTrMod.MODID)
public class DimTrMod {

    public static final String MODID = "dimtr";
    public static final Logger LOGGER = LogManager.getLogger();

    public DimTrMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("🚀 Initializing Dimension Trials Mod...");

        // ============================================================================
        // 🎯 CONFIGURAÇÕES
        // ============================================================================
        modContainer.registerConfig(ModConfig.Type.SERVER, DimTrConfig.SERVER_SPEC, MODID + "-server.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, DimTrConfig.CLIENT_SPEC, MODID + "-client.toml");

        LOGGER.info("✅ Registered server and client configurations");

        // ============================================================================
        // 🎯 NETWORKING (SISTEMA CORRIGIDO)
        // ============================================================================
        modEventBus.addListener(ModNetworking::registerPayloads);

        LOGGER.info("✅ Registered networking system with party support");

        // ============================================================================
        // 🎯 LOG DE INICIALIZAÇÃO COMPLETA
        // ============================================================================
        LOGGER.info("🎯 Dimension Trials Mod initialization complete!");
        LOGGER.info("📋 Features available:");
        LOGGER.info("   • Phase-based progression system");
        LOGGER.info("   • Collaborative party system ✅");
        LOGGER.info("   • Modular HUD interface");
        LOGGER.info("   • Administrative commands (/dimtr)");
        LOGGER.info("   • Party management (/dimtr party) ✅");
        LOGGER.info("   • Individual progression tracking ✅");
        LOGGER.info("   • Proximity-based multipliers ✅");
    }
}