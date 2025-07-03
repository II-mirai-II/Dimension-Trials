package net.mirai.dimtr;

import net.mirai.dimtr.config.DimTrConfig;
// import net.mirai.dimtr.config.ConfigurationManager; // TODO: Integrar quando estiver compilando
import net.mirai.dimtr.network.ModNetworking;
import net.mirai.dimtr.util.Constants;
import net.mirai.dimtr.integration.ExternalModIntegration;
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
        LOGGER.info(Constants.LOG_INITIALIZING_MOD);

        // ============================================================================
        // 🎯 CONFIGURAÇÕES
        // ============================================================================
        modContainer.registerConfig(ModConfig.Type.SERVER, DimTrConfig.SERVER_SPEC, Constants.MOD_ID + Constants.CONFIG_SERVER_FILE_SUFFIX);
        modContainer.registerConfig(ModConfig.Type.CLIENT, DimTrConfig.CLIENT_SPEC, Constants.MOD_ID + Constants.CONFIG_CLIENT_FILE_SUFFIX);

        LOGGER.info(Constants.LOG_CONFIG_REGISTERED);

        // ============================================================================
        // 🎯 NETWORKING (SISTEMA CORRIGIDO)
        // ============================================================================
        modEventBus.addListener(ModNetworking::registerPayloads);

        LOGGER.info(Constants.LOG_NETWORKING_REGISTERED);

        // ============================================================================
        // 🎯 CUSTOM REQUIREMENTS & CONFIGURATION MANAGER (NOVOS SISTEMAS)
        // ============================================================================
        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) -> {
            event.enqueueWork(() -> {
                // TODO: Inicializar o sistema de configuração robusto
                // ConfigurationManager.initialize();
                
                // Carregar requisitos customizados
                net.mirai.dimtr.config.CustomRequirements.loadCustomRequirements();
                
                // 🎯 NOVO: Inicializar integração automática com mods externos
                // Executado após as configurações estarem carregadas
                ExternalModIntegration.initialize();
            });
        });

        LOGGER.info(Constants.LOG_CUSTOM_REQUIREMENTS_INITIALIZED);

        // ============================================================================
        // 🎯 LOG DE INICIALIZAÇÃO COMPLETA
        // ============================================================================
        LOGGER.info(Constants.LOG_INITIALIZATION_COMPLETE);
        LOGGER.info(Constants.LOG_FEATURES_AVAILABLE);
        LOGGER.info(Constants.LOG_FEATURE_PHASE_PROGRESSION);
        LOGGER.info(Constants.LOG_FEATURE_PARTY_SYSTEM);
        LOGGER.info(Constants.LOG_FEATURE_HUD_INTERFACE);
        LOGGER.info(Constants.LOG_FEATURE_ADMIN_COMMANDS);
        LOGGER.info(Constants.LOG_FEATURE_PARTY_COMMANDS);
        LOGGER.info(Constants.LOG_FEATURE_INDIVIDUAL_TRACKING);
        LOGGER.info(Constants.LOG_FEATURE_PROXIMITY_MULTIPLIERS);
        LOGGER.info(Constants.LOG_FEATURE_CUSTOM_REQUIREMENTS);
    }
}