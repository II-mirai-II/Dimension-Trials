# ⚙️ Configuration Guide

**Complete setup guide for server administrators and players**

## 📁 Configuration Files

- **`dimtr-server.toml`** - Core server settings (requirements, phases, multipliers, party system)
- **`dimtr-client.toml`** - Client preferences (HUD appearance, keybinds, UI settings)  
- **`dimtr/custom_requirements/`** - JSON files for unlimited custom phases
- **`dimtr/templates/`** - Configuration templates for different server types

---

## 🔧 Essential Server Settings (`dimtr-server.toml`)

### Phase Management
```toml
enablePhase1 = true          # Nether access requirements
enablePhase2 = true          # End access requirements
enableMobKillsPhase1 = true  # Toggle mob requirements for Phase 1
enableMobKillsPhase2 = true  # Toggle mob requirements for Phase 2
```

### Key Mob Requirements
```toml
# Phase 1 - Common Overworld Mobs
reqZombieKills = 50
reqSkeletonKills = 40
reqSpiderKills = 30
reqCreeperKills = 30
reqDrownedKills = 20

# Phase 1 - Special Objectives
reqElderGuardian = true      # Ocean monument boss
reqRaid = true              # Pillager raid victory
reqTrialVaultAdv = true     # Trial chambers advancement

# Phase 2 - Nether Mobs  
reqBlazeKills = 20
reqWitherSkeletonKills = 15
reqGhastKills = 10

# Phase 2 - Boss Objectives
reqWither = true            # Wither boss defeat
reqWarden = true            # Deep dark warden defeat
```

### Multiplier System
```toml
enableMultipliers = true
phase1Multiplier = 1.5      # 1.5x mob health/damage after Phase 1
phase2Multiplier = 2.0      # 2.0x mob health/damage after Phase 2
enableXpMultiplier = true   # Bonus XP from combat
xpMultiplierPhase1 = 1.25   # 25% bonus XP after Phase 1
xpMultiplierPhase2 = 1.5    # 50% bonus XP after Phase 2
```

### Party System Settings
```toml
enablePartySystem = true
maxPartySize = 10            # Maximum players per party
partyInviteTimeout = 300    # Invitation timeout (seconds)
partyProgressionSharing = true # Share progression between members
```

### Backup System
```toml
enableAutomaticBackups = true  # Enable automatic data backups
backupInterval = 30           # Backup interval in minutes
maxBackupCount = 10           # Maximum number of backups to keep
```

### Debug & Logging
```toml
enableDebugLogging = false    # Enable detailed debug logs
logProgressionEvents = true   # Log progression milestones
logPartyEvents = true         # Log party system events
```

---

## 🖥️ Client Customization (`dimtr-client.toml`)

### HUD Controls
```toml
hudKeybind = "key.keyboard.j"        # Main HUD toggle key
hudScale = 1.0                       # HUD size scaling
hudOpacity = 0.9                     # HUD transparency
showProgressPercentages = true       # Show completion percentages
showPartyInfo = true                 # Display party members
```

### Interface Preferences
```toml
enableSounds = true          # HUD sound effects
soundVolume = 1.0           # Sound volume level
enableAnimations = true     # UI animations
compactMode = false         # Compact HUD layout
```

---

## 🎯 Quick Configuration Presets

### Casual Server (Easier progression)
```toml
# Reduce requirements by 50%
reqZombieKills = 25
reqSkeletonKills = 20
reqWarden = false           # Disable optional bosses
phase1Multiplier = 1.2      # Lower difficulty scaling
```

### Hardcore Server (Maximum challenge)
```toml
# Double requirements
reqZombieKills = 100
reqSkeletonKills = 80
reqWarden = true            # All bosses required
phase1Multiplier = 2.5      # Higher difficulty scaling
```

### Party-Focused Server
```toml
maxPartySize = 10           # Large parties
reqZombieKills = 80         # Higher base (balanced by party scaling)
enablePartySystem = true    # Essential for this mode
```

### Speed Run Server
```toml
reqZombieKills = 10         # Minimal requirements
reqElderGuardian = false    # Skip optional objectives
enableMultipliers = false  # No difficulty scaling
```

---

## 🔍 Troubleshooting & Commands

### Debug Commands
```
/dimtr status               # Current configuration and progress
/dimtr reload config        # Reload configuration files
/dimtr debug config         # Detailed configuration info
/dimtr debug logging true   # Enable debug logging temporarily
```

### Common Issues
- **Config not loading:** Check TOML syntax, restart server after changes
- **Mob requirements not working:** Verify `enableMobKillsPhase1/2` is true
- **Multipliers not applying:** Ensure `enableMultipliers` is true and phases completed
- **Party progress not syncing:** Check `partyProgressionSharing` is enabled

---

## 🔧 External Mod Integration

### Automatic Boss Detection
```toml
# Enable automatic integration with supported mods
enableExternalModIntegration = true

# Configure which mods to integrate with
integrateMowziesMobs = true       # Mowzie's Mobs bosses
integrateCataclysm = true         # L_Ender's Cataclysm bosses
integrateAether = true            # Aether bosses
```

### Custom Integration Example
```toml
# Define custom boss requirements for external mods
[externalBossRequirements]
"twilightforest:naga" = { required = true, phase = 1 }
"twilightforest:lich" = { required = true, phase = 1 }
"twilightforest:hydra" = { required = true, phase = 2 }
```

---

## 📁 Configuration Templates

The mod includes several pre-made configuration templates in `config/dimtr/templates/` that you can use as a starting point:

### Available Templates
- **`casual.toml`** - Relaxed requirements for casual play
- **`hardcore.toml`** - Extreme requirements for challenge servers
- **`party_focused.toml`** - Optimized for party gameplay
- **`boss_only.toml`** - Only boss objectives, no mob grinding
- **`speedrun.toml`** - Minimal requirements for fast progression

### Using Templates
1. Navigate to `config/dimtr/templates/`
2. Copy your chosen template file
3. Paste and rename to `dimtr-server.toml` in the `config/` directory
4. Restart your server to apply the configuration
5. Customize further as needed

You can also create your own templates by copying your working configuration to the templates directory.

---

**📖 For detailed configuration options and advanced setups, see the original [Configuration Guide](CONFIGURATION.md)**

**🔗 Related:** [Custom Requirements](CUSTOM_REQUIREMENTS.md) | [Party System](PARTY_SYSTEM.md) | [Main README](README.md)**
```toml
# Debug logging
enableDebugLogging = false

# Admin features
allowAdminBypass = true      # Ops can bypass restrictions
```

## 🖥️ Client Configuration (`dimtr-client.toml`)

### HUD Settings
```toml
# Main HUD keybind
hudKeybind = "key.keyboard.j"

# HUD Display
hudScale = 1.0               # HUD size scaling
hudOpacity = 0.9             # HUD transparency
showProgressPercentages = true
showPartyInfo = true         # Show party members in HUD

# HUD Position
hudAnchor = "TOP_LEFT"       # TOP_LEFT, TOP_RIGHT, CENTER, etc.
hudOffsetX = 10             # X offset from anchor
hudOffsetY = 10             # Y offset from anchor
```

### Interface Options
```toml
# Sound effects
enableSounds = true
soundVolume = 1.0

# Visual effects
enableAnimations = true
showTooltips = true
compactMode = false          # Compact HUD layout
```

## 📋 Common Configuration Scenarios

*For quick configuration templates, check the `config/dimtr/templates/` directory*

### Specialized Server Types

1. **Boss-Only Challenge**
```toml
# Disable all mob kills
enableMobKillsPhase1 = false
enableMobKillsPhase2 = false

# Keep only boss objectives
reqElderGuardian = true
reqWither = true
reqWarden = true

# High multipliers for boss focus
phase1Multiplier = 3.0
phase2Multiplier = 5.0
```

### Individual Player Progression
```toml
# Enable individual progression (experimental)
enableIndividualProgression = false
proximityRadius = 100        # Radius for party detection
```

### Custom Dimension Integration
```toml
# Custom dimensions that should be blocked
# (Use with custom requirements system)
customBlockedDimensions = [
    "twilightforest:twilight_forest",
    "aether:the_aether"
]
```

## 🔍 Troubleshooting Configuration

### Common Issues:

**Configuration not loading:**
- Check TOML syntax
- Restart server after changes
- Check server logs for errors

**Mob requirements not working:**
- Ensure `enableMobKillsPhase1/2` is true
- Check if phase is enabled
- Verify mob names in debug logs

**Multipliers not applying:**
- Ensure `enableMultipliers` is true
- Check if phases are completed
- Verify mob types are supported

### Debug Commands:
```
/dimtr status                # Show current configuration
/dimtr debug config          # Show detailed config info
/dimtr reload config         # Reload configuration files
```

## 📊 Configuration Templates

### Template Files:
The mod includes several configuration templates in `config/dimtr/templates/`:

- `casual.toml` - Relaxed requirements for casual play
- `hardcore.toml` - Extreme requirements for challenge servers
- `party_focused.toml` - Optimized for party gameplay
- `boss_only.toml` - Only boss objectives, no mob grinding

To use a template:
1. Copy the template file
2. Rename it to `dimtr-server.toml`
3. Customize as needed

---

**Back to [Main README](README.md)**