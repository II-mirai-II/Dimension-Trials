package net.mirai.dimtr.util;

import net.mirai.dimtr.DimTrMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, DimTrMod.MODID);

    // REMOVIDO: import net.mirai.dimtr.item.ProgressionBookItem;
    // REMOVIDO: qualquer registro de ProgressionBookItem

    // Este registry está vazio agora - o livro foi completamente removido
    // Novos itens podem ser adicionados aqui no futuro se necessário
}