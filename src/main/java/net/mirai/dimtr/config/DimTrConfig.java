package net.mirai.dimtr.config; // Pacote adicionado

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DimTrConfig {

    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        final Pair<Client, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Server, ModConfigSpec> serverSpecPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();
    }

    public static class Client {
        public final ModConfigSpec.ConfigValue<String> hudKeybindString;

        Client(ModConfigSpec.Builder builder) {
            // Comentário atualizado
            builder.comment("Client-only settings for Dimension Trials").push("client");
            hudKeybindString = builder
                    .comment("The key to open the Progression HUD. Uses GLFW key names (e.g., 'key.keyboard.j'). Default: J")
                    .translation("config.dimtr.hudKeybind") // Chave de tradução atualizada
                    .define("hudKeybindString", "key.keyboard.j");
            builder.pop();
        }
    }

    public static class Server {
        public final ModConfigSpec.BooleanValue enablePhase1;
        public final ModConfigSpec.BooleanValue reqElderGuardian;
        public final ModConfigSpec.BooleanValue reqRaidAndRavager;
        public final ModConfigSpec.BooleanValue reqEvoker;
        public final ModConfigSpec.BooleanValue reqTrialVaultAdv;

        public final ModConfigSpec.BooleanValue enablePhase2;
        public final ModConfigSpec.BooleanValue reqWither;
        public final ModConfigSpec.BooleanValue reqWarden;


        Server(ModConfigSpec.Builder builder) {
            // Comentário atualizado
            builder.comment("Server-side settings for Dimension Trials").push("server");

            builder.push("phase1");
            enablePhase1 = builder
                    .comment("Enable/Disable Phase 1 (Overworld -> Nether) progression gating.")
                    .translation("config.dimtr.server.enablePhase1") // Chave de tradução atualizada
                    .define("enablePhase1", true);
            reqElderGuardian = builder
                    .comment("Require Elder Guardian to be defeated for Phase 1.")
                    .translation("config.dimtr.server.reqElderGuardian") // Chave de tradução atualizada
                    .define("reqElderGuardian", true);
            reqRaidAndRavager = builder
                    .comment("Require a Raid to be won (Hero of the Village) AND a Ravager to be defeated for Phase 1.")
                    .translation("config.dimtr.server.reqRaidAndRavager") // Chave de tradução atualizada
                    .define("reqRaidAndRavager", true);
            reqEvoker = builder
                    .comment("Require an Evoker to be defeated for Phase 1.")
                    .translation("config.dimtr.server.reqEvoker") // Chave de tradução atualizada
                    .define("reqEvoker", true);
            reqTrialVaultAdv = builder
                    .comment("Require the 'Under Lock and Key' (loot a Trial Vault) advancement for Phase 1.")
                    .translation("config.dimtr.server.reqTrialVaultAdv") // Chave de tradução atualizada
                    .define("reqTrialVaultAdv", true);
            builder.pop();

            builder.push("phase2");
            enablePhase2 = builder
                    .comment("Enable/Disable Phase 2 (Nether -> The End) progression gating.")
                    .translation("config.dimtr.server.enablePhase2") // Chave de tradução atualizada
                    .define("enablePhase2", true);
            reqWither = builder
                    .comment("Require the Wither to be defeated for Phase 2.")
                    .translation("config.dimtr.server.reqWither") // Chave de tradução atualizada
                    .define("reqWither", true);
            reqWarden = builder
                    .comment("Require the Warden to be defeated for Phase 2.")
                    .translation("config.dimtr.server.reqWarden") // Chave de tradução atualizada
                    .define("reqWarden", true);
            builder.pop();

            builder.pop();
        }
    }
}