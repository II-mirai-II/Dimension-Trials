package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = DimTrMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // REMOVIDO: Linha problemática do ModRecipeProvider
        // generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));

        // TODO: Adicionar outros providers aqui conforme necessário
        // Ex: generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

        DimTrMod.LOGGER.info("Data generation setup completed for " + DimTrMod.MODID);
    }
}