package net.mirai.dimtr.data;

import net.mirai.dimtr.DimTrMod;
import net.mirai.dimtr.util.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Como removemos o livro, comentar a receita
        // ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PROGRESSION_BOOK.get())
        //         .pattern("   ") // Linha 1: Vazia
        //         .pattern("AB ") // Linha 2: Ametista, Livro, Vazio
        //         .pattern("   ") // Linha 3: Vazia
        //         .define('A', Items.AMETHYST_SHARD)
        //         .define('B', Items.BOOK)
        //         .unlockedBy("has_book", has(Items.BOOK)) // Condição para desbloquear a receita
        //         .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
        //         .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DimTrMod.MODID, "progression_book"));
    }
}