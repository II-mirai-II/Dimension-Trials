package net.mirai.dimtr.init; // Ou o pacote que você preferir para registros

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.item.ProgressionBookItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

public class ModItems {

    // Cria um DeferredRegister para itens, associado ao MODID do seu mod
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DimTrMod.MODID);

    // Registra o ProgressionBookItem
    public static final Supplier<Item> PROGRESSION_BOOK = ITEMS.register("progression_book",
            () -> new ProgressionBookItem(new Item.Properties().stacksTo(1))); // stacksTo(1) é comum para livros

    // Método para registrar o DeferredRegister no event bus do mod
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    // Listener para adicionar o item à aba criativa (opcional, mas recomendado)
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) { // Ou qualquer outra aba que você preferir
            event.accept(PROGRESSION_BOOK.get());
        }
    }
}