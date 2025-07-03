package net.mirai.dimtr.util;

import net.mirai.dimtr.DimTrMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Utility class for sending rich notifications to players.
 * Provides consistent visual and audio feedback for important events.
 */
public class NotificationHelper {

    /**
     * Notification types with different visual styles and sounds.
     */
    public enum NotificationType {
        SUCCESS(ChatFormatting.GREEN, "✅", SoundEvents.EXPERIENCE_ORB_PICKUP),
        INFO(ChatFormatting.AQUA, "ℹ️", SoundEvents.NOTE_BLOCK_BELL),
        WARNING(ChatFormatting.YELLOW, "⚠️", SoundEvents.NOTE_BLOCK_SNARE),
        ERROR(ChatFormatting.RED, "❌", SoundEvents.NOTE_BLOCK_BASS),
        ACHIEVEMENT(ChatFormatting.GOLD, "🏆", SoundEvents.UI_TOAST_CHALLENGE_COMPLETE),
        PARTY(ChatFormatting.LIGHT_PURPLE, "👥", SoundEvents.NOTE_BLOCK_CHIME);

        public final ChatFormatting color;
        public final String icon;
        public final SoundEvent sound;

        NotificationType(ChatFormatting color, String icon, Holder.Reference<SoundEvent> soundHolder) {
            this.color = color;
            this.icon = icon;
            this.sound = soundHolder.value();
        }
        
        NotificationType(ChatFormatting color, String icon, SoundEvent sound) {
            this.color = color;
            this.icon = icon;
            this.sound = sound;
        }
    }

    /**
     * Sends a notification to a player with visual and audio feedback.
     * @param player The player to send the notification to
     * @param type The type of notification
     * @param title The notification title
     * @param message The notification message (optional)
     * @param playSound Whether to play the associated sound
     */
    public static void sendNotification(ServerPlayer player, NotificationType type, String title, 
                                       String message, boolean playSound) {
        // Send title message
        Component titleComponent = Component.literal(type.icon + " " + title)
                .withStyle(type.color);
        player.sendSystemMessage(titleComponent);
        
        // Send message if provided
        if (message != null && !message.isEmpty()) {
            Component messageComponent = Component.literal(message)
                    .withStyle(ChatFormatting.GRAY);
            player.sendSystemMessage(messageComponent);
        }
        
        // Play sound if requested
        if (playSound && type.sound != null) {
            player.playNotifySound(type.sound, SoundSource.MASTER, 1.0f, 1.0f);
        }
    }

    /**
     * Sends a notification with default sound enabled.
     */
    public static void sendNotification(ServerPlayer player, NotificationType type, String title, String message) {
        sendNotification(player, type, title, message, true);
    }

    /**
     * Sends a notification with only a title.
     */
    public static void sendNotification(ServerPlayer player, NotificationType type, String title) {
        sendNotification(player, type, title, null, true);
    }

    /**
     * Sends a success notification.
     */
    public static void sendSuccess(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.SUCCESS, title, message);
    }

    /**
     * Sends a success notification with only a title.
     */
    public static void sendSuccess(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.SUCCESS, title);
    }

    /**
     * Sends an info notification.
     */
    public static void sendInfo(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.INFO, title, message);
    }

    /**
     * Sends an info notification with only a title.
     */
    public static void sendInfo(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.INFO, title);
    }

    /**
     * Sends a warning notification.
     */
    public static void sendWarning(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.WARNING, title, message);
    }

    /**
     * Sends a warning notification with only a title.
     */
    public static void sendWarning(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.WARNING, title);
    }

    /**
     * Sends an error notification.
     */
    public static void sendError(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.ERROR, title, message);
    }

    /**
     * Sends an error notification with only a title.
     */
    public static void sendError(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.ERROR, title);
    }

    /**
     * Sends an achievement notification.
     */
    public static void sendAchievement(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.ACHIEVEMENT, title, message);
    }

    /**
     * Sends an achievement notification with only a title.
     */
    public static void sendAchievement(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.ACHIEVEMENT, title);
    }

    /**
     * Sends a party-related notification.
     */
    public static void sendParty(ServerPlayer player, String title, String message) {
        sendNotification(player, NotificationType.PARTY, title, message);
    }

    /**
     * Sends a party-related notification with only a title.
     */
    public static void sendParty(ServerPlayer player, String title) {
        sendNotification(player, NotificationType.PARTY, title);
    }

    /**
     * Sends a progress update notification.
     * @param player The player
     * @param objectiveName The name of the objective
     * @param current Current progress
     * @param required Required progress
     * @param isCompleted Whether the objective is now completed
     */
    public static void sendProgressUpdate(ServerPlayer player, String objectiveName, int current, 
                                         int required, boolean isCompleted) {
        if (isCompleted) {
            sendAchievement(player, 
                Component.translatable(Constants.NOTIFICATION_PROGRESS_COMPLETE, objectiveName).getString(),
                Component.translatable(Constants.NOTIFICATION_PROGRESS_COMPLETE_DESC).getString());
        } else {
            String progressText = Component.translatable(Constants.NOTIFICATION_PROGRESS_PREFIX, current, required).getString();
            sendInfo(player, objectiveName, progressText);
        }
    }

    /**
     * Sends a phase completion notification with celebration effects.
     * @param player The player
     * @param phaseName The name of the completed phase
     * @param unlockedContent What was unlocked
     */
    public static void sendPhaseCompletion(ServerPlayer player, String phaseName, String unlockedContent) {
        // Main completion message
        sendAchievement(player, 
                Component.translatable(Constants.NOTIFICATION_PHASE_COMPLETE, phaseName).getString(),
                Component.translatable(Constants.NOTIFICATION_PHASE_COMPLETE_CONGRATS, phaseName).getString());
        
        // Unlocked content message
        if (unlockedContent != null && !unlockedContent.isEmpty()) {
            sendSuccess(player, 
                    Component.translatable(Constants.NOTIFICATION_CONTENT_UNLOCKED).getString(),
                    Component.translatable(Constants.NOTIFICATION_CONTENT_ACCESSIBLE, unlockedContent).getString());
        }
        
        // Additional celebration sound
        player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1.0f, 1.2f);
    }

    /**
     * Sends a party join notification to all party members.
     * @param newMember The player who joined
     * @param partyMembers Array of all party members to notify
     * @param partyName The name of the party
     */
    public static void sendPartyJoinNotification(ServerPlayer newMember, ServerPlayer[] partyMembers, String partyName) {
        String joinMessage = Component.translatable(Constants.NOTIFICATION_PARTY_JOIN, newMember.getName().getString()).getString();
        
        for (ServerPlayer member : partyMembers) {
            if (member != newMember) {
                sendParty(member, Component.translatable(Constants.NOTIFICATION_PARTY_UPDATE).getString(), joinMessage);
            }
        }
        
        // Welcome message for the new member
        sendParty(newMember, 
                Component.translatable(Constants.NOTIFICATION_PARTY_WELCOME, partyName).getString(),
                Component.translatable(Constants.NOTIFICATION_PARTY_SHARE_PROGRESS).getString());
    }

    /**
     * Sends a multiplier notification when a player gains a multiplier.
     * @param player The player
     * @param multiplier The new multiplier value
     * @param reason The reason for gaining the multiplier
     */
    public static void sendMultiplierGained(ServerPlayer player, float multiplier, String reason) {
        String title = Component.translatable(Constants.NOTIFICATION_MULTIPLIER_GAINED).getString();
        String message = Component.translatable(Constants.NOTIFICATION_MULTIPLIER_FROM, 
                String.format("%.1fx", multiplier), reason).getString();
        sendSuccess(player, title, message);
    }

    /**
     * 🎆 SISTEMA DE CELEBRAÇÃO COMPLETAMENTE REFORMULADO 🎆
     * 
     * Lança um espetáculo de fogos de artifício épico para celebrar a conclusão de fases.
     * Agora com múltiplos tipos de celebração, efeitos visuais aprimorados e melhor integração com parties.
     * 
     * @param player O jogador que completou a fase
     * @param phaseNumber A fase que foi completada (1=Overworld, 2=Nether, 3=End)
     * @param isPartyContext Se true, é uma celebração de party (mais intensa)
     */
    public static void launchCelebrationFireworks(ServerPlayer player, int phaseNumber, boolean isPartyContext) {
        if (player == null || player.level() == null) return;
        
        try {
            // 🎵 Sons de celebração personalizados por fase
            playCelebrationSounds(player, phaseNumber, isPartyContext);
            
            MinecraftServer server = player.level().getServer();
            if (server == null) return;
            
            CommandSourceStack source = player.createCommandSourceStack()
                .withPermission(4)
                .withSuppressedOutput();
            
            // 🎆 Determinar intensidade da celebração
            int fireworkCount = isPartyContext ? 20 : 12; // Mais fogos para parties
            int grandFinaleDelay = isPartyContext ? 40 : 25; // Mais tempo para party finale
            
            // 🌟 Primeira onda: Fogos em círculo
            launchCircleFireworks(server, source, player, phaseNumber, fireworkCount);
            
            // ⭐ Segunda onda: Fogos aleatórios
            launchRandomFireworks(server, source, player, phaseNumber, fireworkCount / 2);
            
            // 🎇 Grande finale especial
            launchGrandFinale(server, source, player, phaseNumber, grandFinaleDelay, isPartyContext);
            
            // ✨ Efeitos de partículas ambientais
            createAmbientParticles(player, phaseNumber, isPartyContext);
            
        } catch (Exception e) {
            DimTrMod.LOGGER.error("🎆 Falha ao iniciar celebração espetacular: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Versão simplificada para compatibilidade com código existente
     */
    public static void launchCelebrationFireworks(ServerPlayer player, int phaseNumber) {
        launchCelebrationFireworks(player, phaseNumber, false);
    }
    
    /**
     * 🎊 Celebração especial para parties - lança fogos para todos os membros
     */
    public static void launchPartyCelebrationFireworks(java.util.List<ServerPlayer> partyMembers, int phaseNumber) {
        if (partyMembers == null || partyMembers.isEmpty()) return;
        
        // 🌟 Primeira onda: Sincronizada para todos
        for (int i = 0; i < partyMembers.size(); i++) {
            final ServerPlayer member = partyMembers.get(i);
            final int memberIndex = i;
            
            if (member != null && member.level() != null) {
                MinecraftServer server = member.level().getServer();
                if (server != null) {
                    // Delay escalonado para criar efeito cascata entre membros
                    server.tell(new net.minecraft.server.TickTask(
                        server.getTickCount() + memberIndex * 5, () -> {
                            try {
                                // Usar contexto de party para celebração mais intensa
                                launchCelebrationFireworks(member, phaseNumber, true);
                                
                                // Som especial de party
                                member.level().playSound(null, member.blockPosition(),
                                    SoundEvents.BELL_BLOCK,
                                    SoundSource.MASTER, 0.8F, 1.0F + memberIndex * 0.1F);
                                    
                            } catch (Exception e) {
                                DimTrMod.LOGGER.error("🎊 Erro na celebração de party para {}: {}", 
                                    member.getGameProfile().getName(), e.getMessage());
                            }
                        }));
                }
            }
        }
        
        // 🎆 Segunda onda: Finale coletivo após 3 segundos
        if (!partyMembers.isEmpty()) {
            ServerPlayer firstMember = partyMembers.get(0);
            if (firstMember != null && firstMember.level() != null) {
                MinecraftServer server = firstMember.level().getServer();
                if (server != null) {
                    server.tell(new net.minecraft.server.TickTask(
                        server.getTickCount() + 60, () -> {
                            try {
                                launchPartyGrandFinale(partyMembers, phaseNumber);
                            } catch (Exception e) {
                                DimTrMod.LOGGER.error("🎆 Erro no grande finale da party: {}", e.getMessage());
                            }
                        }));
                }
            }
        }
    }
    
    /**
     * 🌟 Grande finale especial para parties
     */
    private static void launchPartyGrandFinale(java.util.List<ServerPlayer> partyMembers, int phaseNumber) {
        for (ServerPlayer member : partyMembers) {
            if (member == null || member.level() == null) continue;
            
            MinecraftServer server = member.level().getServer();
            if (server == null) continue;
            
            CommandSourceStack source = member.createCommandSourceStack()
                .withPermission(4)
                .withSuppressedOutput();
            
            // 🎇 Múltiplos fogos simultâneos ao redor do jogador
            for (int i = 0; i < 8; i++) {
                final int index = i;
                final double angle = (2 * Math.PI * index) / 8;
                
                server.tell(new net.minecraft.server.TickTask(server.getTickCount() + index, () -> {
                    try {
                        double posX = member.getX() + Math.cos(angle) * 6;
                        double posY = member.getY() + 4 + Math.random() * 2;
                        double posZ = member.getZ() + Math.sin(angle) * 6;
                        
                        String command = createFireworkCommand(posX, posY, posZ, phaseNumber, FireworkType.GRAND_FINALE);
                        server.getCommands().performPrefixedCommand(source, command);
                        
                        // Som épico
                        member.level().playSound(null, posX, posY, posZ,
                            SoundEvents.FIREWORK_ROCKET_LARGE_BLAST,
                            SoundSource.MASTER, 2.0F, 0.5F + index * 0.1F);
                            
                    } catch (Exception e) {
                        DimTrMod.LOGGER.error("🌟 Erro no finale individual: {}", e.getMessage());
                    }
                }));
            }
            
            // 💥 Explosão central final
            server.tell(new net.minecraft.server.TickTask(server.getTickCount() + 20, () -> {
                try {
                    String command = createFireworkCommand(member.getX(), member.getY() + 5, member.getZ(), 
                        phaseNumber, FireworkType.GRAND_FINALE);
                    server.getCommands().performPrefixedCommand(source, command);
                    
                    // Som final épico
                    member.level().playSound(null, member.blockPosition(),
                        SoundEvents.WITHER_SPAWN,
                        SoundSource.MASTER, 1.0F, 2.0F);
                        
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("💥 Erro na explosão central: {}", e.getMessage());
                }
            }));
        }
    }
    
    /**
     * 🎵 Tocar sons de celebração personalizados
     */
    private static void playCelebrationSounds(ServerPlayer player, int phaseNumber, boolean isPartyContext) {
        float volume = isPartyContext ? 1.5F : 1.0F;
        
        // Som principal de conquista
        player.level().playSound(null, player.blockPosition(), 
            SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 
            SoundSource.MASTER, volume, 1.0F);
        
        // Som de level up
        player.level().playSound(null, player.blockPosition(), 
            SoundEvents.PLAYER_LEVELUP, 
            SoundSource.MASTER, volume * 0.8F, 0.75F);
        
        // Sons específicos por fase
        switch (phaseNumber) {
            case 1 -> { // Overworld - Sons místicos
                player.level().playSound(null, player.blockPosition(), 
                    SoundEvents.EXPERIENCE_ORB_PICKUP, 
                    SoundSource.MASTER, volume, 1.2F);
            }
            case 2 -> { // Nether - Sons de fogo
                player.level().playSound(null, player.blockPosition(), 
                    SoundEvents.BLAZE_AMBIENT, 
                    SoundSource.MASTER, volume * 0.6F, 1.5F);
            }
            default -> { // End - Sons de dragão
                player.level().playSound(null, player.blockPosition(), 
                    SoundEvents.ENDER_DRAGON_AMBIENT, 
                    SoundSource.MASTER, volume * 0.4F, 1.8F);
            }
        }
    }
    
    /**
     * 🎆 Lançar fogos em formação circular
     */
    private static void launchCircleFireworks(MinecraftServer server, CommandSourceStack source, 
                                            ServerPlayer player, int phaseNumber, int count) {
        double radius = 8.0;
        
        for (int i = 0; i < count; i++) {
            final int index = i;
            final double angle = (2 * Math.PI * index) / count;
            
            server.tell(new net.minecraft.server.TickTask(server.getTickCount() + index * 2, () -> {
                try {
                    double posX = player.getX() + Math.cos(angle) * radius;
                    double posY = player.getY() + 2 + Math.random() * 3;
                    double posZ = player.getZ() + Math.sin(angle) * radius;
                    
                    // 🚀 Usar API direta em vez de comandos para melhor confiabilidade
                    spawnFireworkDirect(player.serverLevel(), posX, posY, posZ, phaseNumber, 
                        Math.random() < 0.7 ? FireworkType.BASIC : FireworkType.ENHANCED);
                    
                    // Som de lançamento
                    player.level().playSound(null, posX, posY, posZ,
                        SoundEvents.FIREWORK_ROCKET_LAUNCH,
                        SoundSource.MASTER, 0.6F, 0.8F + (float)(Math.random() * 0.4F));
                        
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("🎆 Erro ao lançar foguete circular: {}", e.getMessage());
                }
            }));
        }
    }
    
    /**
     * 🎇 Lançar fogos aleatórios para preencher o céu
     */
    private static void launchRandomFireworks(MinecraftServer server, CommandSourceStack source, 
                                            ServerPlayer player, int phaseNumber, int count) {
        for (int i = 0; i < count; i++) {
            final int index = i;
            
            server.tell(new net.minecraft.server.TickTask(server.getTickCount() + 15 + index * 3, () -> {
                try {
                    double posX = player.getX() + (Math.random() - 0.5) * 16;
                    double posY = player.getY() + 1 + Math.random() * 4;
                    double posZ = player.getZ() + (Math.random() - 0.5) * 16;
                    
                    FireworkType type = Math.random() < 0.5 ? FireworkType.BASIC : 
                                       Math.random() < 0.8 ? FireworkType.ENHANCED : FireworkType.SPECTACULAR;
                    
                    String command = createFireworkCommand(posX, posY, posZ, phaseNumber, type);
                    server.getCommands().performPrefixedCommand(source, command);
                    
                    // Partículas extras
                    createFireworkParticles(player, posX, posY, posZ, phaseNumber);
                    
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("🎆 Erro ao lançar foguete aleatório: {}", e.getMessage());
                }
            }));
        }
    }
    
    /**
     * 🌟 Grande finale com fogos espetaculares
     */
    private static void launchGrandFinale(MinecraftServer server, CommandSourceStack source, 
                                        ServerPlayer player, int phaseNumber, int delay, boolean isPartyContext) {
        
        int finaleCount = isPartyContext ? 5 : 3;
        
        for (int i = 0; i < finaleCount; i++) {
            final int index = i;
            
            server.tell(new net.minecraft.server.TickTask(server.getTickCount() + delay + index * 8, () -> {
                try {
                    double posX = player.getX() + (Math.random() - 0.5) * 4;
                    double posY = player.getY() + 2;
                    double posZ = player.getZ() + (Math.random() - 0.5) * 4;
                    
                    String command = createFireworkCommand(posX, posY, posZ, phaseNumber, FireworkType.GRAND_FINALE);
                    server.getCommands().performPrefixedCommand(source, command);
                    
                    // Som especial de explosão
                    player.level().playSound(null, posX, posY, posZ,
                        SoundEvents.FIREWORK_ROCKET_LARGE_BLAST,
                        SoundSource.MASTER, 1.5F, 0.7F + index * 0.1F);
                        
                } catch (Exception e) {
                    DimTrMod.LOGGER.error("🌟 Erro ao lançar grande finale: {}", e.getMessage());
                }
            }));
        }
    }
    
    /**
     * ✨ Criar efeitos de partículas ambientais
     */
    private static void createAmbientParticles(ServerPlayer player, int phaseNumber, boolean isPartyContext) {
        int particleCount = isPartyContext ? 200 : 100;
        
        for (int i = 0; i < particleCount; i++) {
            double px = player.getX() + (Math.random() - 0.5) * 20;
            double py = player.getY() + Math.random() * 10;
            double pz = player.getZ() + (Math.random() - 0.5) * 20;
            
            switch (phaseNumber) {
                case 1 -> { // Overworld - Partículas místicas
                    player.level().addParticle(ParticleTypes.END_ROD, px, py, pz,
                        (Math.random() - 0.5) * 0.2, Math.random() * 0.3, (Math.random() - 0.5) * 0.2);
                    
                    if (Math.random() < 0.3) {
                        player.level().addParticle(ParticleTypes.ENCHANT, px, py, pz,
                            (Math.random() - 0.5) * 0.1, Math.random() * 0.2, (Math.random() - 0.5) * 0.1);
                    }
                }
                case 2 -> { // Nether - Partículas de fogo
                    player.level().addParticle(ParticleTypes.FLAME, px, py, pz,
                        (Math.random() - 0.5) * 0.2, Math.random() * 0.3, (Math.random() - 0.5) * 0.2);
                    
                    if (Math.random() < 0.4) {
                        player.level().addParticle(ParticleTypes.LAVA, px, py, pz,
                            (Math.random() - 0.5) * 0.1, Math.random() * 0.1, (Math.random() - 0.5) * 0.1);
                    }
                }
                default -> { // End - Partículas de dragão
                    player.level().addParticle(ParticleTypes.DRAGON_BREATH, px, py, pz,
                        (Math.random() - 0.5) * 0.2, Math.random() * 0.3, (Math.random() - 0.5) * 0.2);
                    
                    if (Math.random() < 0.2) {
                        player.level().addParticle(ParticleTypes.PORTAL, px, py, pz,
                            (Math.random() - 0.5) * 0.3, Math.random() * 0.2, (Math.random() - 0.5) * 0.3);
                    }
                }
            }
        }
    }
    
    /**
     * 🎆 Criar partículas específicas para fogos
     */
    private static void createFireworkParticles(ServerPlayer player, double x, double y, double z, int phaseNumber) {
        for (int i = 0; i < 25; i++) {
            double px = x + (Math.random() - 0.5) * 3;
            double py = y + Math.random() * 3;
            double pz = z + (Math.random() - 0.5) * 3;
            
            switch (phaseNumber) {
                case 1 -> player.level().addParticle(ParticleTypes.FIREWORK, px, py, pz,
                    (Math.random() - 0.5) * 0.3, Math.random() * 0.4, (Math.random() - 0.5) * 0.3);
                case 2 -> player.level().addParticle(ParticleTypes.FLAME, px, py, pz,
                    (Math.random() - 0.5) * 0.3, Math.random() * 0.4, (Math.random() - 0.5) * 0.3);
                default -> player.level().addParticle(ParticleTypes.DRAGON_BREATH, px, py, pz,
                    (Math.random() - 0.5) * 0.3, Math.random() * 0.4, (Math.random() - 0.5) * 0.3);
            }
        }
    }
    
    /**
     * 🎨 Tipos de fogos de artifício
     */
    private enum FireworkType {
        BASIC, ENHANCED, SPECTACULAR, GRAND_FINALE
    }
    
    /**
     * 🎆 Criar comando de foguete com base no tipo e fase
     */
    private static String createFireworkCommand(double x, double y, double z, int phaseNumber, FireworkType type) {
        return switch (type) {
            case BASIC -> createBasicFirework(x, y, z, phaseNumber);
            case ENHANCED -> createEnhancedFirework(x, y, z, phaseNumber);
            case SPECTACULAR -> createSpectacularFirework(x, y, z, phaseNumber);
            case GRAND_FINALE -> createGrandFinaleFirework(x, y, z, phaseNumber);
        };
    }
    
    /**
     * 🎆 Fogos básicos por fase
     */
    private static String createBasicFirework(double x, double y, double z, int phaseNumber) {
        return switch (phaseNumber) {
            case 1 -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:1,explosions:[" +
                "{shape:\"small_ball\",has_flicker:true,has_trail:true,colors:[I;2651799,6316128],fade_colors:[I;4312372,8388736]}]}}}}",
                x, y, z);
            case 2 -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:1,explosions:[" +
                "{shape:\"large_ball\",has_flicker:true,has_trail:true,colors:[I;16711680,16755200],fade_colors:[I;16733440,8339200]}]}}}}",
                x, y, z);
            default -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:1,explosions:[" +
                "{shape:\"burst\",has_flicker:true,has_trail:true,colors:[I;8991416,13061821],fade_colors:[I;8388736,4194304]}]}}}}",
                x, y, z);
        };
    }
    
    /**
     * ✨ Fogos aprimorados com múltiplas explosões
     */
    private static String createEnhancedFirework(double x, double y, double z, int phaseNumber) {
        return switch (phaseNumber) {
            case 1 -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:2,explosions:[" +
                "{shape:\"star\",has_flicker:true,has_trail:true,colors:[I;2651799,6316128,10485760],fade_colors:[I;4312372,8388736]}," +
                "{shape:\"creeper\",has_flicker:true,has_trail:true,colors:[I;16777215,43775],fade_colors:[I;26367]}]}}}}",
                x, y, z);
            case 2 -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:2,explosions:[" +
                "{shape:\"large_ball\",has_flicker:true,has_trail:true,colors:[I;16711680,16755200,16762880],fade_colors:[I;16733440,8339200]}," +
                "{shape:\"burst\",has_flicker:true,has_trail:true,colors:[I;16747520,14565440],fade_colors:[I;8339200]}]}}}}",
                x, y, z);
            default -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:2,explosions:[" +
                "{shape:\"burst\",has_flicker:true,has_trail:true,colors:[I;8991416,13061821,11141290],fade_colors:[I;8388736,4194304]}," +
                "{shape:\"creeper\",has_flicker:true,has_trail:true,colors:[I;16711935,12801229],fade_colors:[I;8323327]}]}}}}",
                x, y, z);
        };
    }
    
    /**
     * 🌟 Fogos espetaculares com efeitos especiais
     */
    private static String createSpectacularFirework(double x, double y, double z, int phaseNumber) {
        return switch (phaseNumber) {
            case 1 -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:3,explosions:[" +
                "{shape:\"star\",has_flicker:true,has_trail:true,colors:[I;2651799,6316128,10485760,65535],fade_colors:[I;4312372,8388736,26367]}," +
                "{shape:\"small_ball\",has_flicker:true,has_trail:true,colors:[I;16777215,43775,65535],fade_colors:[I;26367,6316128]}," +
                "{shape:\"creeper\",has_flicker:true,has_trail:true,colors:[I;43775,65535,26367],fade_colors:[I;4312372]}]}}}}",
                x, y, z);
            case 2 -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:3,explosions:[" +
                "{shape:\"large_ball\",has_flicker:true,has_trail:true,colors:[I;16711680,16755200,16762880,16733440],fade_colors:[I;16733440,8339200,4194304]}," +
                "{shape:\"burst\",has_flicker:true,has_trail:true,colors:[I;16747520,14565440,16733440],fade_colors:[I;8339200,4194304]}," +
                "{shape:\"creeper\",has_flicker:true,has_trail:true,colors:[I;16733440,16747520],fade_colors:[I;8339200]}]}}}}",
                x, y, z);
            default -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:3,explosions:[" +
                "{shape:\"burst\",has_flicker:true,has_trail:true,colors:[I;8991416,13061821,11141290,16711935],fade_colors:[I;8388736,4194304]}," +
                "{shape:\"star\",has_flicker:true,has_trail:true,colors:[I;16711935,12801229,13369599],fade_colors:[I;8323327,4194304]}," +
                "{shape:\"creeper\",has_flicker:true,has_trail:true,colors:[I;12801229,16711935],fade_colors:[I;8323327]}]}}}}",
                x, y, z);
        };
    }
    
    /**
     * 💥 Grande finale com explosões massivas
     */
    private static String createGrandFinaleFirework(double x, double y, double z, int phaseNumber) {
        return switch (phaseNumber) {
            case 1 -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:3,explosions:[" +
                "{shape:\"star\",has_flicker:true,has_trail:true,colors:[I;2651799,6316128,10485760,65535,43775],fade_colors:[I;4312372,8388736,26367,65535]}," +
                "{shape:\"small_ball\",has_flicker:true,has_trail:true,colors:[I;16777215,43775,65535,26367],fade_colors:[I;26367,6316128,4312372]}," +
                "{shape:\"creeper\",has_flicker:true,has_trail:true,colors:[I;43775,65535,26367,16777215],fade_colors:[I;4312372,8388736]}," +
                "{shape:\"large_ball\",has_flicker:true,has_trail:true,colors:[I;65535,26367,43775],fade_colors:[I;6316128]}]}}}}",
                x, y, z);
            case 2 -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:3,explosions:[" +
                "{shape:\"large_ball\",has_flicker:true,has_trail:true,colors:[I;16711680,16755200,16762880,16733440,14565440],fade_colors:[I;16733440,8339200,4194304]}," +
                "{shape:\"burst\",has_flicker:true,has_trail:true,colors:[I;16747520,14565440,16733440,16755200],fade_colors:[I;8339200,4194304,2097152]}," +
                "{shape:\"creeper\",has_flicker:true,has_trail:true,colors:[I;16733440,16747520,16762880],fade_colors:[I;8339200,4194304]}," +
                "{shape:\"star\",has_flicker:true,has_trail:true,colors:[I;16755200,16733440],fade_colors:[I;8339200]}]}}}}",
                x, y, z);
            default -> String.format(
                "summon minecraft:firework_rocket %.2f %.2f %.2f " +
                "{FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{" +
                "\"minecraft:fireworks\":{flight_duration:3,explosions:[" +
                "{shape:\"burst\",has_flicker:true,has_trail:true,colors:[I;8991416,13061821,11141290,16711935,12801229],fade_colors:[I;8388736,4194304,8323327]}," +
                "{shape:\"star\",has_flicker:true,has_trail:true,colors:[I;16711935,12801229,13369599,11141290],fade_colors:[I;8323327,4194304,8388736]}," +
                "{shape:\"creeper\",has_flicker:true,has_trail:true,colors:[I;12801229,16711935,13369599],fade_colors:[I;8323327,4194304]}," +
                "{shape:\"large_ball\",has_flicker:true,has_trail:true,colors:[I;16711935,11141290],fade_colors:[I;8323327,4194304]}]}}}}",
                x, y, z);
        };
    }
    
    /**
     * 🏆 Celebração especial para achievements importantes (como completar todas as fases)
     */
    public static void launchEpicAchievementCelebration(ServerPlayer player, String achievementName) {
        if (player == null || player.level() == null) return;
        
        try {
            MinecraftServer server = player.level().getServer();
            if (server == null) return;
            
            CommandSourceStack source = player.createCommandSourceStack()
                .withPermission(4)
                .withSuppressedOutput();
            
            // 🏆 Mensagem épica
            player.sendSystemMessage(Component.literal(
                String.format("🏆✨ EPIC ACHIEVEMENT UNLOCKED: %s ✨🏆", achievementName))
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            
            // 🎵 Sons épicos
            player.level().playSound(null, player.blockPosition(), 
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 
                SoundSource.MASTER, 2.0F, 0.5F);
            
            player.level().playSound(null, player.blockPosition(), 
                SoundEvents.WITHER_SPAWN, 
                SoundSource.MASTER, 1.0F, 2.0F);
            
            // 🌟 Espetáculo de fogos épico - múltiplas camadas
            // Camada 1: Círculo grande
            launchCircleFireworks(server, source, player, 3, 24); // 24 fogos em círculo
            
            // Camada 2: Fogos aleatórios intensos
            for (int i = 0; i < 50; i++) {
                final int index = i;
                server.tell(new net.minecraft.server.TickTask(server.getTickCount() + index * 2, () -> {
                    try {
                        double posX = player.getX() + (Math.random() - 0.5) * 25;
                        double posY = player.getY() + 2 + Math.random() * 8;
                        double posZ = player.getZ() + (Math.random() - 0.5) * 25;
                        
                        FireworkType type = index % 2 == 0 ? FireworkType.SPECTACULAR : FireworkType.GRAND_FINALE;
                        String command = createFireworkCommand(posX, posY, posZ, 3, type);
                        server.getCommands().performPrefixedCommand(source, command);
                        
                    } catch (Exception e) {
                        DimTrMod.LOGGER.error("🏆 Erro no espetáculo épico: {}", e.getMessage());
                    }
                }));
            }
            
            // 🎇 Grande finale múltiplo
            for (int i = 0; i < 10; i++) {
                final int index = i;
                server.tell(new net.minecraft.server.TickTask(server.getTickCount() + 100 + index * 5, () -> {
                    try {
                        double posX = player.getX() + (Math.random() - 0.5) * 8;
                        double posY = player.getY() + 5;
                        double posZ = player.getZ() + (Math.random() - 0.5) * 8;
                        
                        String command = createFireworkCommand(posX, posY, posZ, 3, FireworkType.GRAND_FINALE);
                        server.getCommands().performPrefixedCommand(source, command);
                        
                        // Som épico escalonado
                        player.level().playSound(null, posX, posY, posZ,
                            SoundEvents.FIREWORK_ROCKET_LARGE_BLAST,
                            SoundSource.MASTER, 2.0F, 0.3F + index * 0.1F);
                            
                    } catch (Exception e) {
                        DimTrMod.LOGGER.error("🎇 Erro no finale múltiplo: {}", e.getMessage());
                    }
                }));
            }
            
            // ✨ Partículas épicas
            createEpicAmbientParticles(player);
            
            DimTrMod.LOGGER.info("🏆 Epic achievement celebration launched for {}: {}", 
                player.getGameProfile().getName(), achievementName);
                
        } catch (Exception e) {
            DimTrMod.LOGGER.error("🏆 Falha ao iniciar celebração épica de achievement: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * ✨ Criar partículas épicas para achievements especiais
     */
    private static void createEpicAmbientParticles(ServerPlayer player) {
        // Múltiplas camadas de partículas épicas
        for (int i = 0; i < 500; i++) {
            double px = player.getX() + (Math.random() - 0.5) * 30;
            double py = player.getY() + Math.random() * 15;
            double pz = player.getZ() + (Math.random() - 0.5) * 30;
            
            // Mix de todas as partículas para efeito épico
            switch (i % 6) {
                case 0 -> player.level().addParticle(ParticleTypes.END_ROD, px, py, pz,
                    (Math.random() - 0.5) * 0.3, Math.random() * 0.5, (Math.random() - 0.5) * 0.3);
                case 1 -> player.level().addParticle(ParticleTypes.FIREWORK, px, py, pz,
                    (Math.random() - 0.5) * 0.4, Math.random() * 0.6, (Math.random() - 0.5) * 0.4);
                case 2 -> player.level().addParticle(ParticleTypes.ENCHANT, px, py, pz,
                    (Math.random() - 0.5) * 0.2, Math.random() * 0.4, (Math.random() - 0.5) * 0.2);
                case 3 -> player.level().addParticle(ParticleTypes.DRAGON_BREATH, px, py, pz,
                    (Math.random() - 0.5) * 0.3, Math.random() * 0.4, (Math.random() - 0.5) * 0.3);
                case 4 -> player.level().addParticle(ParticleTypes.FLAME, px, py, pz,
                    (Math.random() - 0.5) * 0.2, Math.random() * 0.3, (Math.random() - 0.5) * 0.2);
                case 5 -> player.level().addParticle(ParticleTypes.PORTAL, px, py, pz,
                    (Math.random() - 0.5) * 0.4, Math.random() * 0.3, (Math.random() - 0.5) * 0.4);
            }
        }
    }
    
    /**
     * 🚀 Spawnar foguete diretamente usando API do Minecraft (mais confiável que comandos)
     */
    private static void spawnFireworkDirect(ServerLevel level, double x, double y, double z, int phaseNumber, FireworkType type) {
        try {
            // Criar entity de foguete
            net.minecraft.world.entity.projectile.FireworkRocketEntity firework = 
                new net.minecraft.world.entity.projectile.FireworkRocketEntity(level, x, y, z, createFireworkItemStack(phaseNumber, type));
            
            // Spawnar no mundo
            level.addFreshEntity(firework);
            
            DimTrMod.LOGGER.debug("🎆 Firework spawned directly at {}, {}, {} - Phase: {}, Type: {}", 
                x, y, z, phaseNumber, type);
                
        } catch (Exception e) {
            DimTrMod.LOGGER.error("🚀 Erro ao spawnar foguete diretamente: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 🎨 Criar ItemStack de foguete com as especificações corretas
     */
    private static net.minecraft.world.item.ItemStack createFireworkItemStack(int phaseNumber, FireworkType type) {
        net.minecraft.world.item.ItemStack fireworkStack = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.FIREWORK_ROCKET);
        
        // Criar lista de explosões
        java.util.List<net.minecraft.world.item.component.FireworkExplosion> explosions = new java.util.ArrayList<>();
        
        switch (type) {
            case BASIC -> explosions.add(createBasicExplosion(phaseNumber));
            case ENHANCED -> {
                explosions.add(createBasicExplosion(phaseNumber));
                explosions.add(createEnhancedExplosion(phaseNumber));
            }
            case SPECTACULAR -> {
                explosions.add(createBasicExplosion(phaseNumber));
                explosions.add(createEnhancedExplosion(phaseNumber));
                explosions.add(createSpectacularExplosion(phaseNumber));
            }
            case GRAND_FINALE -> {
                explosions.add(createBasicExplosion(phaseNumber));
                explosions.add(createEnhancedExplosion(phaseNumber));
                explosions.add(createSpectacularExplosion(phaseNumber));
                explosions.add(createGrandFinaleExplosion(phaseNumber));
            }
        }
        
        // Configurar componente de foguete
        int flightDuration = switch (type) {
            case BASIC -> 1;
            case ENHANCED -> 2;
            case SPECTACULAR, GRAND_FINALE -> 3;
        };
        
        net.minecraft.world.item.component.Fireworks fireworks = new net.minecraft.world.item.component.Fireworks(flightDuration, explosions);
        fireworkStack.set(net.minecraft.core.component.DataComponents.FIREWORKS, fireworks);
        
        return fireworkStack;
    }
    
    /**
     * 🎆 Criar explosão básica por fase
     */
    private static net.minecraft.world.item.component.FireworkExplosion createBasicExplosion(int phaseNumber) {
        return switch (phaseNumber) {
            case 1 -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.SMALL_BALL,
                IntList.of(2651799, 6316128), // Colors
                IntList.of(4312372, 8388736), // Fade colors
                true, // Has trail
                true  // Has flicker
            );
            case 2 -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.LARGE_BALL,
                IntList.of(16711680, 16755200), // Vermelho/laranja para Nether
                IntList.of(16733440, 8339200),
                true, true
            );
            default -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.BURST,
                IntList.of(8991416, 13061821), // Roxo para End
                IntList.of(8388736, 4194304),
                true, true
            );
        };
    }
    
    /**
     * ✨ Criar explosão aprimorada
     */
    private static net.minecraft.world.item.component.FireworkExplosion createEnhancedExplosion(int phaseNumber) {
        return switch (phaseNumber) {
            case 1 -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.STAR,
                IntList.of(2651799, 6316128, 10485760),
                IntList.of(4312372, 8388736),
                true, true
            );
            case 2 -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.LARGE_BALL,
                IntList.of(16711680, 16755200, 16762880),
                IntList.of(16733440, 8339200),
                true, true
            );
            default -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.BURST,
                IntList.of(8991416, 13061821, 11141290),
                IntList.of(8388736, 4194304),
                true, true
            );
        };
    }
    
    /**
     * 🌟 Criar explosão espetacular
     */
    private static net.minecraft.world.item.component.FireworkExplosion createSpectacularExplosion(int phaseNumber) {
        return switch (phaseNumber) {
            case 1 -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.STAR,
                IntList.of(2651799, 6316128, 10485760, 65535),
                IntList.of(4312372, 8388736, 26367),
                true, true
            );
            case 2 -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.LARGE_BALL,
                IntList.of(16711680, 16755200, 16762880, 16733440),
                IntList.of(16733440, 8339200, 4194304),
                true, true
            );
            default -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.BURST,
                IntList.of(8991416, 13061821, 11141290, 16711935),
                IntList.of(8388736, 4194304),
                true, true
            );
        };
    }
    
    /**
     * 💥 Criar explosão de grande finale
     */
    private static net.minecraft.world.item.component.FireworkExplosion createGrandFinaleExplosion(int phaseNumber) {
        return switch (phaseNumber) {
            case 1 -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.STAR,
                IntList.of(2651799, 6316128, 10485760, 65535, 43775),
                IntList.of(4312372, 8388736, 26367, 65535),
                true, true
            );
            case 2 -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.LARGE_BALL,
                IntList.of(16711680, 16755200, 16762880, 16733440, 14565440),
                IntList.of(16733440, 8339200, 4194304),
                true, true
            );
            default -> new net.minecraft.world.item.component.FireworkExplosion(
                net.minecraft.world.item.component.FireworkExplosion.Shape.BURST,
                IntList.of(8991416, 13061821, 11141290, 16711935, 12801229),
                IntList.of(8388736, 4194304, 8323327),
                true, true
            );
        };
    }
}
