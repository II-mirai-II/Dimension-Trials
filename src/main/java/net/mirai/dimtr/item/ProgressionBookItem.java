package net.mirai.dimtr.item;

import net.mirai.dimtr.client.gui.screens.ProgressionBookScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder; // Import CORRIGIDO
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;

public class ProgressionBookItem extends Item {

    public ProgressionBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            // No cliente, abre a tela do livro.
            Minecraft.getInstance().setScreen(new ProgressionBookScreen(itemStack));
        }

        // Retorna SUCCESS para indicar que a ação foi realizada.
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}