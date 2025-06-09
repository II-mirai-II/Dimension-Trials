# Dimension Trials 💀

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)

![HUD Preview](https://github.com/II-mirai-II/Dimension-Trials/blob/main/img_1.png?raw=true)

## **A challenging Minecraft mod that gates access to other dimensions behind specific achievements and mob elimination goals, forcing players to fully explore and master each dimension before progressing further.**

## 🌟 Key Features

### 🚪 **Dimensional Gating System**

*   **Phase 1 - THE NETHER:** Blocks Nether access until Overworld challenges are completed
*   **Phase 2 - THE END:** Requires Phase 1 completion plus high-level Nether objectives
*   **Progressive Difficulty:** Each phase builds upon the previous one
*   **Smart Teleportation:** Players attempting unauthorized access are safely returned to spawn

### 📊 **Advanced HUD System**

*   **Press `J`** to open an elegant real-time progression HUD
*   **Multi-window Interface:** Tabbed navigation between objectives and mob elimination goals
*   **Live Progress Tracking:** Visual indicators (✔ completed, ❌ pending) with detailed counters
*   **Pagination Support:** Easy navigation through extensive requirements with Q/E keys
*   **Interactive Elements:** Click-to-navigate tabs and comprehensive tooltips
*   **Sound Effects:** Immersive audio feedback for all interactions

### ⚔️ **Comprehensive Mob Elimination Goals**

*   **Phase 1:** 16 different Overworld mob types to defeat
*   **Phase 2:** 8 new Nether mob types + increased Overworld requirements
*   **Smart Reset System:** Phase 2 requires 125% of Phase 1 Overworld mobs
*   **Voluntary Exile Support:** Includes Pillager Captain advancement tracking
*   **Fully Configurable:** Every mob count can be adjusted via server config

### ⚙️ **Highly Configurable**

*   **Individual Toggles:** Enable/disable any requirement independently
*   **Scalable Difficulty:** Adjust mob kill requirements from 0 to 1000+ per type
*   **Phase Control:** Each phase can be completely disabled if desired
*   **Multiplier System:** Optional mob health/damage/XP increases after phase completion
*   **Command Support:** Administrative commands for progress management

### 🎮 **Enhanced User Experience**

*   **Dual Interface:** Modern HUD + classic Progression Book for compatibility
*   **Multi-language Support:** Available in 9+ languages (EN, PT-BR, ES, FR, DE, IT, AR, HI, ZH-CN)
*   **Real-time Updates:** Progress syncs instantly across all players
*   **Global Announcements:** Server-wide notifications when phases unlock
*   **Advancement Integration:** Custom achievements for phase completion

## 🎯 Progression Phases & Requirements

### Phase 1 - THE NETHER 🔥 (Unlock access to the Nether)

_All requirements below can be individually enabled/disabled in the server config_

#### 🎖️ Special Objectives:

*   🛡 **Defeat Elder Guardian:** Find and defeat an Elder Guardian in an Ocean Monument
*   🏴 **Win a Raid:** Successfully defend a village from a Pillager raid
*   🗝 **Trial Vault Advancement:** Complete a Trial Chamber and earn the "Under Lock and Key" advancement
*   🏴 **Voluntary Exile:** Defeat a Pillager Captain to earn this advancement

#### ⚔️ Mob Elimination Goals (DEFAULT):

**👥 Common Mobs:**

*   🧟 **Zombies:** 50 kills
*   💀 **Skeletons:** 40 kills
*   🏔 **Strays:** 10 kills
*   🏜 **Husks:** 10 kills
*   🕷 **Spiders:** 30 kills
*   💥 **Creepers:** 30 kills
*   🌊 **Drowned:** 20 kills

**⭐ Special Mobs:**

*   👤 **Endermen:** 5 kills
*   🧙 **Witches:** 5 kills
*   🏹 **Pillagers:** 20 kills
*   ⚔ **Vindicators:** 10 kills
*   ☠ **Bogged:** 10 kills
*   💨 **Breezes:** 5 kills

**🎯 Goal Kills (Rare & Powerful):**

*   🐗 **Ravagers:** 1 kill
*   🔮 **Evokers:** 5 kills

***

### Phase 2 - THE END 🌌 (Unlock access to The End)

_Requires Phase 1 completion. All requirements below can be individually enabled/disabled_

#### 🎖️ Special Objectives:

*   💀 **Defeat Wither:** Summon and defeat the Wither boss
*   🌑 **Defeat Warden:** Awaken and defeat the Warden in the Deep Dark

#### ⚔️ Mob Elimination Goals (DEFAULT):

**🔥 Nether Mobs:**

*   🔥 **Blazes:** 20 kills
*   💀 **Wither Skeletons:** 15 kills
*   🐷 **Piglin Brutes:** 5 kills
*   🐗 **Hoglins:** 1 kill
*   ☠️ **Zoglins:** 1 kill
*   👻 **Ghasts:** 10 kills
*   🐷 **Hostile Piglins:** 30 kills

**🔄 Reset Overworld Challenges:** Complete 125% of Phase 1 Overworld mob requirements

## 🔧 Configuration

The mod creates detailed configuration files in your `config/` folder:

### Server Configuration (`dimtr-server.toml`)

*   **Phase Toggles:** Enable/disable each phase independently
*   **Special Objectives:** Toggle individual requirements (Elder Guardian, Raid, Wither, Warden, etc.)
*   **Mob Kill Requirements:** Customize kill counts for every mob type (0-1000+ range)
*   **Multiplier System:** Optional mob health/damage/XP increases after phase completion

### Client Configuration (`dimtr-client.toml`)

*   **HUD Keybind:** Customize the progression HUD key (default: J)
*   **Interface Settings:** HUD position, scale, and display options

### Configuration Examples:

```
# Disable Phase 1 entirely (open Nether access)
enablePhase1 = false

# Reduce Zombie requirement to 25 kills
reqZombieKills = 25

# Disable Wither requirement for Phase 2
reqWither = false

# Enable Voluntary Exile requirement
reqVoluntaryExile = true

# Enable 1.5x mob multiplier after Phase 1
enableMultipliers = true
phase1Multiplier = 1.5

# Enable XP multiplier system
enableXpMultiplier = true
```

## 🎮 How to Play

1.  **Start Your World:** Begin normally in the Overworld
2.  **Get Your Book:** Craft or find a Progression Book to track your journey
3.  **Check Progress:** Press `J` to open the comprehensive progression HUD
4.  **Complete Phase 1:** Work through Overworld objectives and mob elimination
5.  **Access Nether:** Becomes available once Phase 1 requirements are met
6.  **Tackle Phase 2:** New Nether objectives plus increased Overworld requirements
7.  **Unlock The End:** Complete Phase 2 to access the End dimension
8.  **Enhanced Challenge:** Optional multipliers make mobs stronger and give more XP after each phase

### HUD Navigation:

*   **Window Switching:** Use arrow keys (→/←) or click tabs to switch between windows
*   **Page Navigation:** Use Q/E keys to navigate through pages within windows
*   **Tooltips:** Hover over objectives for detailed descriptions
*   **Progress Tracking:** Real-time counters show your exact progress

### Pro Tips:

*   **Use the HUD:** The `J` key opens a comprehensive progress tracker with detailed information
*   **Plan Ahead:** Some mobs are rare - prepare accordingly and explore thoroughly
*   **Server Config:** Admins can adjust difficulty to match their community's playstyle
*   **Multiplayer Friendly:** Progress is global - work together as a team!
*   **Command Access:** Admins can use `/dimtr` commands for progress management

## 🌍 Localization

Dimension Trials supports multiple languages:

*   **English (en\_us)** - Default
*   **Português Brasileiro (pt\_br)** - Brazilian Portuguese
*   **Español (es\_es)** - Spanish
*   **Français (fr\_fr)** - French
*   **Deutsch (de\_de)** - German
*   **Italiano (it\_it)** - Italian
*   **العربية (ar\_sa)** - Arabic
*   **हिन्दी (hi\_in)** - Hindi
*   **中文 (zh\_cn)** - Chinese (Simplified)

## 🔧 Commands

### `/dimtr` - Administrative Commands

*   `/dimtr complete phase1` - Force complete Phase 1
*   `/dimtr complete phase2` - Force complete Phase 2
*   `/dimtr reset all` - Reset all progress
*   `/dimtr reset phase1` - Reset Phase 1 progress
*   `/dimtr reset phase2` - Reset Phase 2 progress
*   `/dimtr reset mob_kills` - Reset all mob kill counters
*   `/dimtr set goal <goal> <true/false>` - Set specific objective completion
*   `/dimtr set mob_kill <mob> <count>` - Set mob kill counters
*   `/dimtr status` - Show detailed progression status
*   `/dimtr sync` - Force synchronize progress with client
*   `/dimtr debug payload` - Debug client-server synchronization

_Requires operator permissions_

## 🤝 Contributing

We welcome contributions! Feel free to:

*   **Report bugs or suggest features:** [GitHub Issues](https://github.com/II-mirai-II/Dimension-Trials/issues)
*   Submit pull requests for improvements
*   Share configuration presets for different server types
*   Create community content or tutorials
*   Help with translations for additional languages

***

**Made with ❤️ for the Minecraft community**

_Perfect for survival servers looking to add meaningful progression challenges and extend gameplay time in each dimension!_