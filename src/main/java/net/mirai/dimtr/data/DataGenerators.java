package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
// import net.neoforged.neoforge.common.data.ExistingFileHelper; // Removido se não usado por outros providers
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        // ExistingFileHelper existingFileHelper = event.getExistingFileHelper(); // Se precisar para outros providers
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Adiciona seu RecipeProvider
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));

        // Você pode adicionar outros providers aqui (modelos, estados de bloco, etc.)
        // Ex: generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
    }
}