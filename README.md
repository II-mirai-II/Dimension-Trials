# Dimension Trials 💀

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![Mod Version](https://img.shields.io/badge/Version-1.3-blue.svg)]()
[![Party System](https://img.shields.io/badge/NEW-Party%20System-brightgreen.svg)]()
[![Custom Requirements](https://img.shields.io/badge/NEW-Custom%20Requirements-orange.svg)]()

![HUD Preview](https://github.com/II-mirai-II/Dimension-Trials/blob/main/img_1.png?raw=true)

## **A challenging Minecraft mod that gates access to other dimensions behind specific achievements and mob elimination goals, forcing players to fully explore and master each dimension before progressing further.**

### 🆕 **What's New in v1.3?**
- **🎮 Redesigned HUD Interface** - Completely revamped progression HUD with modern design and improved navigation
- **👥 Complete Party System** - Team up with friends for reduced individual requirements and shared progress
- **🎨 Custom Requirements System** - Unlimited custom phases through JSON configuration
- **🔧 Enhanced Multiplayer** - Persistent party names and real-time synchronization

**[📋 View Full Changelog](Changelog%20v1.3.md)**

---

## 🌟 **Key Features**

### 🚪 **Progressive Dimension Access**
- **Phase 1 → Nether:** Complete Overworld challenges first
- **Phase 2 → The End:** Master the Nether before final dimension
- **Custom Phases:** Create unlimited additional phases with JSON config
- **Smart Blocking:** Safe teleportation back to spawn for unauthorized access

### 🎮 **Modern HUD System** *(Redesigned in v1.3)*
- **Press `J`** to open the sleek new progression interface
- **Tabbed Navigation:** Easy switching between objectives and progress
- **Live Tracking:** Real-time counters with visual completion indicators
- **Party Integration:** See team members, leader status, and shared progress
- **Interactive Design:** Click navigation with comprehensive tooltips

### 👥 **Party System** *(NEW in v1.3)*
- **Reduced Requirements:** Kill counts divided by party size
- **Shared Progress:** All team members contribute to same objectives
- **Real-time Updates:** Dynamic requirement scaling as members join/leave
- **Persistent Names:** Offline members show actual usernames, not generic IDs

**[📖 Full Party System Guide →](PARTY_SYSTEM.md)**

### 🎨 **Custom Requirements** *(NEW in v1.3)*
- **Unlimited Phases:** Create Phase 3, 4, 5+ through JSON files
- **Mod Integration:** Full support for Twilight Forest, Aether, and any mod
- **No Coding Required:** Easy JSON configuration system
- **Automatic Examples:** Creates sample files on first run

**[📋 Custom Requirements Guide →](CUSTOM_REQUIREMENTS.md)**

---

## 🎯 **Default Progression**

### **Phase 1: THE NETHER** 🔥
**Requirements:** 16 Overworld mob types + special objectives
- 🧟 Zombies: 50 kills | 💀 Skeletons: 40 kills | 🕷 Spiders: 30 kills
- 🛡 Elder Guardian defeat | 🏴 Pillager Raid victory | 🗝 Trial Vault advancement

### **Phase 2: THE END** 🌌  
**Requirements:** 8 Nether mob types + 125% of Phase 1 mobs
- 🔥 Blazes: 20 kills | 💀 Wither Skeletons: 15 kills | 👻 Ghasts: 10 kills
- 💀 Wither boss defeat | 🌑 Warden defeat

*All requirements are fully configurable and can be disabled individually*

---

## 🚀 **Quick Start**

1. **Install the mod** and start your world
2. **Press `J`** to open the new HUD and see your objectives
3. **Work through Phase 1** - complete Overworld challenges
4. **Access the Nether** once Phase 1 is complete
5. **Complete Phase 2** to unlock The End
6. **Optional:** Create parties with `/dimtr party create` for team play

### 🎮 **Navigation:**
- **`J` Key:** Open/close progression HUD
- **Arrow Keys:** Switch between HUD tabs
- **Q/E Keys:** Navigate pages within tabs
- **Mouse:** Click tabs and hover for tooltips

---

## ⚙️ **Configuration & Customization**

### **📁 Easy Configuration**
- **Server Config:** Adjust requirements, toggle phases, set multipliers
- **Client Config:** Customize HUD appearance, keybinds, and UI settings
- **Custom Requirements:** Create unlimited phases with JSON files

**[📋 Complete Configuration Guide →](CONFIGURATION.md)**

### **🎨 Popular Customizations**
```toml
# Quick examples
reqZombieKills = 25          # Reduce zombie requirement
enablePhase1 = false         # Skip Phase 1 entirely
phase1Multiplier = 2.0       # Double mob difficulty after Phase 1
maxPartySize = 8             # Allow larger parties
```

---

## 🔧 **Commands**

### **Administrative Commands**
```
/dimtr complete phase1       # Force complete Phase 1
/dimtr reset all            # Reset all progress  
/dimtr status               # Show current status
/dimtr sync                 # Force client sync
```

### **Party Commands** *(NEW)*
```
/dimtr party create         # Create new party
/dimtr party invite <player>  # Invite player
/dimtr party info           # Show party details
/dimtr party leave          # Leave party
```

---

## 🌍 **Multiplayer & Compatibility**

- **✅ Fully Multiplayer:** Global progression with party support
- **✅ Highly Configurable:** Adapt to any server style
- **✅ Multi-Language:** 9+ language translations
- **✅ Mod Compatible:** Integrates with any mod through custom requirements
- **✅ Performance Optimized:** Minimal server impact

---

## 📚 **Documentation**

- **[👥 Party System Guide](PARTY_SYSTEM.md)** - Complete party features guide
- **[🎨 Custom Requirements](CUSTOM_REQUIREMENTS.md)** - Create unlimited custom phases
- **[⚙️ Configuration Guide](CONFIGURATION.md)** - Complete setup instructions
- **[📋 Changelog v1.3](Changelog%20v1.3.md)** - All new features and fixes

---

## 🤝 **Community & Support**

- **🐛 Bug Reports:** [GitHub Issues](https://github.com/II-mirai-II/Dimension-Trials/issues)
- **💡 Feature Requests:** [GitHub Discussions](https://github.com/II-mirai-II/Dimension-Trials/discussions)
- **📖 Wiki:** [Complete Documentation](https://github.com/II-mirai-II/Dimension-Trials/wiki)
- **🎮 Community Configs:** Share your custom requirements and configurations

---

**Made with ❤️ for the Minecraft community**

*Perfect for survival servers seeking meaningful progression challenges with unlimited customization possibilities!*