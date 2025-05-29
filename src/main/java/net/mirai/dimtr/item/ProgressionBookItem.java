package net.mirai.dimtr.item;

import net.mirai.dimtr.client.gui.screens.ProgressionBookScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;

public class ProgressionBookItem extends Item {

    public ProgressionBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            // TENTATIVA DE CORREÇÃO DO SOM: Usando BOOK_PAGE_TURN como um som genérico de interação com livro
            // Se este não for o ideal, podemos procurar outro SoundEvent ou remover o som temporariamente.
            // O SoundEvents.ITEM_BOOK_PUT que eu sugeri antes também pode ser uma opção se o LECTERN_BOOK_PUT não existe.
            // Vamos tentar o som de virar página, que é garantido existir.
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F, 0.8F)); // Pitch levemente alterado para diferenciar de virar a página
            Minecraft.getInstance().setScreen(new ProgressionBookScreen(itemStack));
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}