# Dimension Trials 💀

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![img_1.png](img_1.png)
**Dimension Trials** is a Minecraft mod that introduces a new layer of challenge and progression 📜 to your survival experience. Instead of having immediate access to all dimensions, players must now meet specific requirements to unlock the Nether and The End, making dimensional travel feel more like an RPG-style achievement 🧭.

## 🌟 Features

* **Progressive Dimension Access:** The Nether 🔥 and The End 🌌 are locked until specific server-wide objectives are met.
* **Phased Progression:** Unlock dimensions in stages 📊:
    * **Phase 1:** Focuses on Overworld and early-game challenges to unlock the Nether.
    * **Phase 2:** Involves Nether and advanced challenges to unlock The End.
* **The Progression Book 📖:** An in-game guide that details all requirements for each phase, showing what's completed and what's still pending.
    * Automatically given to players on their first join 🎁.
    * Craftable if lost 🛠️.
    * Easy navigation using **Q** (previous page) and **E** (next page) keys ⌨️.
* **Server-Wide Progression 🌍:** All progression is global to the world/server. When one group of players achieves a milestone, it benefits all players 🤝.
* **Configurable Challenges ⚙️:** Server administrators can enable/disable entire phases or individual requirements within each phase via a server-side configuration file.
* **Multiplayer Friendly 👥:** Designed to work seamlessly on dedicated servers.
* **Localized 🌐:** Currently available in English, Portuguese (Português do Brasil), Mandarin Chinese (简体中文), Hindi (हिन्दी), Spanish (Español), French (Français), and Arabic (العربية).
* **Custom Sounds 🔊:** Unique sound cues for attempting to access locked portals and for opening the Progression Book.
* **Admin Commands 💻:** For managing or testing progression.

## 🎮 Gameplay: How to Play

1.  **Begin Your Journey:** When you first join a world with Dimension Trials, your access to the Nether and The End will be restricted.
2.  **The Progression Book:** You will receive a "Progression Book". This book is your primary guide. Open it (right-click while holding) to see the current phase and the objectives you need to complete.
3.  **Complete Objectives:** Work with other players on the server to tackle the challenges listed in the book for Phase 1. These might include defeating specific bosses or achieving certain advancements. The book will update to reflect server-wide progress.
4.  **Unlock the Nether:** Once all server-configured requirements for Phase 1 are met, access to the Nether will be unlocked for everyone on the server 🔥.
5.  **Advance to Phase 2:** With the Nether unlocked, the Progression Book will now show the objectives for Phase 2. These typically involve challenges within the Nether and against tougher foes.
6.  **Conquer The End:** Upon completing all server-configured Phase 2 requirements, The End dimension will become accessible, allowing players to face the Ender Dragon 🐉.

## 📖 The Progression Book

* **Obtaining ✋:** Automatically given to players on their first join. If lost, it can be crafted.
    * **Crafting Recipe 🛠️:** 1 Book + 1 Amethyst Shard (shaped recipe: Amethyst Shard to the left of a Book in the middle row of a 3x3 crafting grid).
        ```
        . . .
        A B .  (A=Amethyst Shard, B=Book)
        . . .
        ```
* **Usage 👆:** Hold the book and right-click to open it. Use the **Q** key to go to the previous page and the **E** key to go to the next page. Press **ESC** to close the book.
* **Content 🔍:** The book details:
    * Instructions on how to use the book.
    * An overview of the mod's purpose.
    * **Phase 1 - NETHER:** Lists all objectives required to unlock the Nether.
    * **Phase 2 - THE END:** Lists all objectives required to unlock The End (visible after Phase 1 is complete or its requirements are met).
    * Each objective shows its status (✔ completed or ❌ pending) and a brief description.

## 🎯 Progression Phases & Requirements

Progression is global. Once a requirement is met by any player or group, it's marked completed for the entire server.

### Phase 1 - NETHER 🔥 (Unlock access to the Nether)
*(All requirements below can be individually enabled/disabled in the server config)*
* ⚔️ **Defeat Elder Guardian:** Vanquish an Elder Guardian in an Ocean Monument.
* 🏘️ **Win a Raid (Hero of the Village):** Successfully defend a village and earn the "Hero of the Village" effect.
* 🐂 **Defeat Ravager:** Slay a Ravager.
* 🧙 **Defeat Evoker:** Defeat an Evoker, typically found in Woodland Mansions.
* 🗝️ **Loot a Trial Vault:** Obtain the "Under Lock and Key" advancement by looting a Trial Vault in a Trial Chamber.

### Phase 2 - THE END 🌌 (Unlock access to The End)
*(Requires Phase 1 to be effectively complete. All requirements below can be individually enabled/disabled in the server config)*
* 💀 **Defeat Wither:** Summon and defeat the Wither.
* 🔊 **Defeat Warden:** Awaken and defeat the Warden in the Deep Dark.

## 👥 Multiplayer

Dimension Trials is designed for multiplayer. All progression towards unlocking dimensions is shared server-wide. When a requirement is met, it is reflected for all online players, and players joining later will also see the updated global progression status.

## ⚙️ Configuration (Server-Side)

Dimension Trials offers a server-side configuration file (`dimtr-server.toml`) located in the `serverconfig` folder of your world save (for single-player) or in the main `config` folder of your dedicated server (if not overridden by world-specific server configs).

You can customize the mod's behavior by editing this file. The following options are available:

* `enablePhase1` (default: `true`): Enable/Disable Phase 1 (Overworld -> Nether) progression gating.
* `reqElderGuardian` (default: `true`): Require Elder Guardian to be defeated for Phase 1.
* `reqRaidAndRavager` (default: `true`): Require a Raid to be won AND a Ravager to be defeated for Phase 1.
* `reqEvoker` (default: `true`): Require an Evoker to be defeated for Phase 1.
* `reqTrialVaultAdv` (default: `true`): Require the 'Under Lock and Key' (loot a Trial Vault) advancement for Phase 1.
* `enablePhase2` (default: `true`): Enable/Disable Phase 2 (Nether -> The End) progression gating.
* `reqWither` (default: `true`): Require the Wither to be defeated for Phase 2.
* `reqWarden` (default: `true`): Require the Warden to be defeated for Phase 2.

## 💻 Admin Commands

For server administrators (permission level 2+), the following commands are available:

* `/dimtr completephase1`: Marks all Phase 1 requirements as complete.
* `/dimtr completephase2`: Marks all Phase 1 and Phase 2 requirements as complete.
* `/dimtr resetprogress`: Resets all progression for all phases and requirements.
* `/dimtr setgoal <goal_name> <true|false>`: Sets the specified goal to completed or not.
    * Available `goal_name`s: `elder_guardian`, `raid_won`, `ravager_killed`, `evoker_killed`, `trial_vault_adv`, `wither_killed`, `warden_killed`.

## 🐛 Bug Reports & Issues

Found a bug or have an issue to report? Please submit it to our [GitHub Issues Page](https://github.com/YOUR_USERNAME/Dimension-Trials/issues) (Replace with your actual link).
When reporting, please include:
* 📝 Your Minecraft version.
* 📝 Your NeoForge version.
* 📝 The Dimension Trials mod version.
* 📝 A clear description of the bug.
* 📝 Steps to reproduce the bug.
* 📝 Any relevant logs or screenshots.

## 🌐 Supported Languages

* English (US)
* Português (Brasil)
* 简体中文 (Mandarin Chinese - Simplified)
* हिन्दी (Hindi)
* Español (Spanish)
* Français (French)
* العربية (Arabic)

*(Community translations are welcome! 🤗)*