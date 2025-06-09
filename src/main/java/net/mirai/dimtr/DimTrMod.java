package net.mirai.dimtr;

import net.mirai.dimtr.command.DimTrCommands;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.network.NetworkManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DimTrMod.MODID)
public class DimTrMod {
    public static final String MODID = "dimtr";
    public static final Logger LOGGER = LogManager.getLogger();

    public DimTrMod(IEventBus modEventBus, ModContainer modContainer) {
        // Configurações
        modContainer.registerConfig(ModConfig.Type.SERVER, DimTrConfig.SERVER_SPEC, DimTrMod.MODID + "-server.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, DimTrConfig.CLIENT_SPEC, DimTrMod.MODID + "-client.toml");

        // CORREÇÃO: Registrar NetworkManager no mod event bus
        modEventBus.addListener(NetworkManager::register);

        // Registrar eventos de comandos
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        DimTrCommands.register(event.getDispatcher());
    }
}