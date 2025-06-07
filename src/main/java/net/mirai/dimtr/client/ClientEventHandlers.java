package net.mirai.dimtr.client;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.client.gui.screens.ProgressionHUDScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandlers {

    // CORREÇÃO: Keybind definido diretamente aqui
    public static final KeyMapping OPEN_HUD_KEY = new KeyMapping(
            "key.dimtr.open_hud", // Translation key
            GLFW.GLFW_KEY_J, // Default key: J
            "key.categories.dimtr" // Category
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_HUD_KEY);
        DimTrMod.LOGGER.debug("Registered HUD keybinding: J");
    }

    @EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientGameEventHandlers {

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            while (OPEN_HUD_KEY.consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.screen == null) {
                    minecraft.setScreen(new ProgressionHUDScreen());
                    DimTrMod.LOGGER.debug("Progression HUD opened by key press.");
                }
            }
        }
    }
}