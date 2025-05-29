package net.mirai.dimtr.init;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.item.ProgressionBookItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DimTrMod.MODID);

    public static final Supplier<Item> PROGRESSION_BOOK = ITEMS.register("progression_book",
            () -> new ProgressionBookItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    // Listener para adicionar o item à aba criativa
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) { // Ou CreativeModeTabs.FUNCTIONAL_BLOCKS, etc.
            event.accept(PROGRESSION_BOOK.get());
        }
        // Se quiser em múltiplas abas, adicione mais if's ou use um Set de abas.
    }
}