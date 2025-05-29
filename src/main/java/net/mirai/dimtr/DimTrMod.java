package net.mirai.dimtr;

import com.mojang.logging.LogUtils;
import net.mirai.dimtr.client.ClientEventHandlers;
import net.mirai.dimtr.config.DimTrConfig;
import net.mirai.dimtr.event.ModEventHandlers;
import net.mirai.dimtr.init.ModItems; // Import ModItems
import net.mirai.dimtr.network.NetworkManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(DimTrMod.MODID)
public class DimTrMod {
    public static final String MODID = "dimtr";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DimTrMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Dimension Trials - Initializing");

        modContainer.registerConfig(ModConfig.Type.CLIENT, DimTrConfig.CLIENT_SPEC, DimTrMod.MODID + "-client.toml");
        modContainer.registerConfig(ModConfig.Type.SERVER, DimTrConfig.SERVER_SPEC, DimTrMod.MODID + "-server.toml");

        // Registra os itens
        ModItems.register(modEventBus);

        // Adiciona listeners de evento do mod
        modEventBus.addListener(NetworkManager::registerPayloads);
        modEventBus.addListener(ClientEventHandlers::onRegisterKeyMappings); // Mantido caso haja outras key mappings futuras
        modEventBus.addListener(ModItems::onBuildCreativeModeTabContents); // Para adicionar o item à aba criativa

        // Registra handlers de evento do Forge/NeoForge
        NeoForge.EVENT_BUS.register(ModEventHandlers.class);
    }
}