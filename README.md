# 📜 Better Vanilla Progress (BVP)

✨ **Embark on a more structured and rewarding vanilla Minecraft experience with Better Vanilla Progress (BVP)!** This mod introduces a progression system that gently guides players through the game's milestones, encouraging exploration and accomplishment before unlocking access to higher-tier dimensions like the Nether and The End. Perfect for players looking for a fresh take on vanilla progression or server owners wanting to create a more controlled and fulfilling early-game experience.

### 🗺️ Overview

Better Vanilla Progress divides the standard Minecraft journey into two key phases, each with a set of required objectives that the server (and thus its players) must collectively achieve before advancing:

**Phase 1: Overworld to Nether**

> _"The Overworld teems with challenges and secrets. Prove your mastery before venturing into the fiery depths."_

This initial phase focuses on overcoming significant Overworld challenges that prepare players for the dangers of the Nether. Access to the Nether is initially restricted, encouraging players to explore and conquer the Overworld first.

**Phase 2: Nether to The End**

> _"The infernal landscape has tested your mettle. Now, the secrets of the End await those who have truly prevailed."_

Having conquered the initial hurdles, the path to the Nether opens. Phase 2 then sets its sights on the formidable challenges within the Nether, ensuring players are well-equipped before they can access The End and face the Ender Dragon.

### ⚙️ Features

* **Phased Progression:** Divides the game into distinct phases, each with specific completion requirements.
* **Server-Wide Unlocks:** Progression is tracked server-wide. Once all required objectives for a phase are completed by any players on the server, the next dimension becomes accessible to everyone.
* **In-Game Progression HUD:** A clean and intuitive HUD (default key: **J**) allows players to easily view the current phase, the remaining objectives, and their completion status.
* **Nether Access Control:** Prevents players from lighting Nether Portals until all Phase 1 objectives are met. Players attempting to do so will receive a clear in-game message and visual/audio feedback.
* **End Access Control:** Restricts the placement of the final Eye of Ender in the End Portal Frames until all Phase 2 objectives are completed. Players will receive a message and feedback if they try prematurely.
* **Global Announcement System:** When a phase is completed, a server-wide announcement notifies all players of the progression.
* **Configurable Requirements:** Server administrators have full control over the specific objectives required for each phase through the server configuration file.

### 🎮 How to Use

1.  Install the Better Vanilla Progress mod on your Minecraft server (and optionally on clients for the HUD).
2.  Press the default key **J** in-game to open the Progression HUD and view the current phase and objectives.
3.  Work together with other players on the server to achieve the required goals for the current phase.
4.  Once all objectives for a phase are completed, the next dimension will unlock for everyone!

### 🛠️ Server Configuration

Server administrators can customize the mod's behavior by editing the `bvp-server.toml` configuration file (usually located in the `config` folder of your Minecraft server). The configuration allows you to:

* Enable or disable each phase of progression.
* Toggle individual requirements for each phase (e.g., requiring the Elder Guardian to be defeated for Phase 1).

Refer to the configuration file for detailed options and descriptions.

### 🐞 Bug Reporting

If you encounter any bugs or issues, please report them on the [GitHub Issues page](https://github.com/II-mirai-II/BetterVanillaProgress/issues). Your feedback is greatly appreciated!