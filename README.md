# Dimension Trials 💀

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)
[![Mod Version](https://img.shields.io/badge/Version-1.3-blue.svg)]()
[![NeoForge](https://img.shields.io/badge/NeoForge-Compatible-orange.svg)](https://neoforged.net/)

**Transform your Minecraft experience with meaningful progression challenges. Gate dimensional access behind achievements and objectives, forcing players to truly master each dimension before advancing to the next.**

![HUD Preview](https://github.com/II-mirai-II/Dimension-Trials/blob/main/img_1.png?raw=true)

---

## 🎯 **What This Mod Does**

🚪 **Block Dimensional Access** - No more rushing to the Nether or End on day one  
👥 **Team Up with Friends** - NEW Party System with shared objectives and reduced requirements  
🎮 **Track Your Progress** - Beautiful HUD showing real-time progression  
🎨 **Infinite Customization** - Create unlimited custom phases with JSON configuration  
⚙️ **Server-Friendly** - Highly configurable for any playstyle or server size  
💾 **Data Safety** - Backup & recovery system prevents progress loss
✨ **XP Multipliers** - Earn bonus XP as you progress through phases
❓**NEW in v1.3:** Automatic integration with 2 popular mods, Multiplayer/Solo Progression system and much more!

---

## 📚 **Learn More**

- **[👥 Party System Guide](PARTY_SYSTEM.md)** - Complete cooperative gameplay guide
- **[🎨 Custom Requirements](CUSTOM_REQUIREMENTS.md)** - Create unlimited custom phases
- **[⚙️ Configuration](CONFIGURATION.md)** - Complete setup and customization guide
- **[📋 Full Changelog v1.3](Changelog%20v1.3.md)** - All new features and improvements

---

## 🌟 **Core Progression System**

### **Phase 1: Unlock the Nether** 🔥
Complete Overworld challenges before accessing the Nether:
- **Kill 16 mob types** (Zombies: 50, Skeletons: 40, Spiders: 30, etc.)
- **Defeat special bosses** (Elder Guardian, complete Pillager Raid)
- **Earn key advancements** (Trial Vault exploration)

### **Phase 2: Unlock the End** 🌌
Master the Nether before reaching the final dimension:
- **Eliminate 7 Nether mob types** (Blazes: 20, Wither Skeletons: 15, Ghasts: 10, etc.)
- **Conquer ultimate bosses** (Wither, Warden)
- **Prove your worthiness** for the End dimension

### **Custom Phases: Unlimited Possibilities** ✨
Create Phase 3, 4, 5+ with JSON files - integrate any mod seamlessly!

---

## 👥 **Party System** 

**Team up and share the challenge!**
- **Reduced Requirements:** Party members share the load (with dynamic scaling)
- **Shared Progress:** All kills count toward the same pool
- **Real-time Updates:** Dynamic scaling as members join/leave
- **Cross-Dimensional:** Hunt in different dimensions simultaneously

```
/party create         # Start your team
/party invite <player> # Invite friends
```

---

## 🎮 **Modern Interface**

**Press `J` to open the sleek progression HUD:**
- ✅ **Live Progress Tracking** with visual completion indicators
- 👥 **Party Integration** showing team members and shared objectives  
- 🎯 **Interactive Navigation** with tabs and comprehensive tooltips
- 📊 **Real-time Synchronization** across all party members

---

## ⚙️ **Perfect for Any Server**

### 🏰 **Casual Servers**
```toml
reqZombieKills = 25          # Reduce requirements
reqWarden = false            # Skip optional bosses
```

### ⚔️ **Hardcore Servers**
```toml
reqZombieKills = 100         # Double requirements  
phase1Multiplier = 2.5       # Massive difficulty scaling
```

### 👥 **Party-Focused Servers**
```toml
maxPartySize = 10            # Large teams (max: 10)
enablePartySystem = true     # Essential cooperative play
```

---

## 🚀 **Quick Start Guide**

1. **Install the Mod**:
   - Ensure you have NeoForge for Minecraft 1.21.1 installed
   - Download the mod JAR from CurseForge or GitHub
   - Place in your `mods` folder

2. **In-Game Setup**:
   - Start your world normally
   - Press `J` to open progression HUD to see your objectives
   - Complete Phase 1 to unlock the Nether
   - Master Phase 2 to unlock the End
   - Optional: Use `/party create` to team up with friends

---

## 🎨 **Unlimited Customization (- BETA -)**

### **JSON-Based Custom Phases**
```json
{
  "name": "Phase 3: Twilight Forest",
  "dimensionAccess": ["twilightforest:twilight_forest"],
  "mobRequirements": {
    "twilightforest:lich": 1,
    "twilightforest:hydra": 1
  },
  "healthMultiplier": 2.5
}
```

**Works with ANY mod ( PROBABLY ):** Twilight Forest, Aether, Create, AllTheMods, Industrial mods, and more!

---

### **Mod Integration: Automatic Boss Detection** 🤖
**NEW in v1.3:** Automatic integration with 2 popular mods!
- **Mowzie's Mobs** - Ferrous Wroughtnaut, Frostmaw, Umvuthi, Naga, Sculptor become required bosses
- **L_Ender's Cataclysm** - Netherite Monstrosity, Ignis, Harbinger, End bosses auto-added
- **Phase 3 Creation** - End-tier bosses automatically create a new post-End phase
- **Smart Classification** - Bosses sorted by spawn dimension (Overworld=Phase1, Nether=Phase2, End=Phase3)

> 📖 **[Complete Integration Guide](CUSTOM_REQUIREMENTS.md#-popular-integrations)**

---

## 📋 **Essential Commands**

```
# Party Management
/party create              # Create a new party
/party invite <player>     # Invite player to party
/party accept              # Accept party invitation
/party leave               # Leave current party
/party info                # View party details

# Personal Progression  
/dimtr status                    # Check your progression

# Admin Commands (OP level 2)
/dimtr player <player> status    # Check any player's progression
/dimtr complete phase1           # Complete Phase 1 for yourself
/dimtr complete phase2           # Complete Phase 2 for yourself
/dimtr player <player> complete phase1  # Complete Phase 1 for target player
/dimtr backup create             # Create manual data backup
/dimtr backup restore <id>       # Restore from backup
```

---

## 🤝 **Support & Community**

**🐛 Issues:** [GitHub Issues](https://github.com/II-mirai-II/Dimension-Trials/issues) | **💡 Ideas:** [GitHub Discussions](https://github.com/II-mirai-II/Dimension-Trials/discussions)

---

## 🧩 **Compatibility**

- **Required**: Minecraft 1.21.1 with NeoForge
- **Optional integrations**: Mowzie's Mobs, L_Ender's Cataclysm
- **Compatible with (- BETA -)**: Most dimension-adding mods including Twilight Forest, The Aether
- **Multiplayer**: Full server support with synchronized progression

---

**Made with ❤️ for the Minecraft community**

*Perfect for survival servers seeking meaningful progression with unlimited customization possibilities!*
