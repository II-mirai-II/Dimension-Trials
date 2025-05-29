package net.mirai.dimtr.item;

import net.mirai.dimtr.client.gui.screens.ProgressionBookScreen; // Ainda necessário para o tipo da tela
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
// Não são necessários imports diretos de classes client-only aqui no topo da classe principal do item.

public class ProgressionBookItem extends Item {

    public ProgressionBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            // A chamada para a lógica do cliente está dentro da verificação isClientSide.
            // A classe ClientInteractionHandler só será carregada quando este método for chamado no cliente.
            ClientInteractionHandler.performClientBookOpen(player, itemStack);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    // Classe interna estática para encapsular toda a lógica e imports específicos do cliente.
    private static class ClientInteractionHandler {
        // Este método e esta classe só serão efetivamente carregados e executados no lado do cliente.
        private static void performClientBookOpen(Player player, ItemStack bookStack) {
            // Imports e classes client-side podem ser usados aqui sem problemas.
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();

            // Verifica se estamos em um estado seguro para interagir com a UI (o jogador e o nível existem)
            if (minecraft.player == null || minecraft.level == null) {
                return;
            }

            minecraft.getSoundManager().play(
                    net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                            net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, // Som que sabemos que compila
                            1.0F,
                            0.85F + player.level().random.nextFloat() * 0.2F // Pitch levemente diferente para "abrir"
                    )
            );
            minecraft.setScreen(new ProgressionBookScreen(bookStack));
        }
    }
}