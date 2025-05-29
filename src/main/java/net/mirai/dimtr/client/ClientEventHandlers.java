package net.mirai.dimtr.client;

// import com.mojang.blaze3d.platform.InputConstants; // Não mais necessário para a HUD key
import net.mirai.dimtr.DimTrMod;
// import net.mirai.dimtr.util.Constants; // Não mais necessário para a HUD key category
// import net.minecraft.client.KeyMapping; // Não mais necessário para a HUD key
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
// import net.neoforged.neoforge.client.event.ClientTickEvent; // Não mais necessário para a HUD key
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
// import net.neoforged.neoforge.client.settings.KeyConflictContext; // Não mais necessário para a HUD key
// import org.lwjgl.glfw.GLFW; // Não mais necessário para a HUD key

public class ClientEventHandlers {

    // KeyMapping openHudKey REMOVIDO

    // Método onRegisterKeyMappings agora está vazio ou pode ser removido
    // se não houver outras key mappings para registrar nesta classe.
    // Por ora, vamos mantê-lo caso você adicione outras no futuro, mas sem o openHudKey.
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        // openHudKey = new KeyMapping(...); // REMOVIDO
        // event.register(openHudKey); // REMOVIDO
        // DimTrMod.LOGGER.info("Registered key mappings (HUD Key: J)"); // REMOVIDO
        // Se não houver outras key mappings, pode-se logar:
        // DimTrMod.LOGGER.info("No client key mappings to register for now.");
        // Ou simplesmente deixar o método vazio.
    }

    // A classe interna GameEventHandlers e o método onClientTick foram removidos
    // já que sua única função era abrir a ProgressionHUDScreen.
    // Se você tiver outros ClientTickEvent handlers, eles precisariam ser separados.
    // Por enquanto, assumindo que era apenas para a HUD:
    /*
    @EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameEventHandlers {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            // Lógica do openHudKey.consumeClick() e Minecraft.getInstance().setScreen(new ProgressionHUDScreen()); REMOVIDA
        }
    }
    */
    // Se GameEventHandlers não tiver mais nenhum listener, a classe pode ser removida completamente.
    // Para este passo, vamos remover o GameEventHandlers se sua única responsabilidade era o onClientTick da HUD.
}