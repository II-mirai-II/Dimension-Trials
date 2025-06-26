# ⚙️ Configuration Guide

Complete guide to configuring Dimension Trials for your server.

## 📁 Configuration Files

The mod creates several configuration files in your `config/` folder:

- `dimtr-server.toml` - Server-side settings (requirements, phases, multipliers)
- `dimtr-client.toml` - Client-side settings (HUD, keybinds, UI)
- `dimtr/custom_requirements/` - Custom phase definitions (JSON files)

## 🔧 Server Configuration (`dimtr-server.toml`)

### Phase Control
```toml
# Enable/disable entire phases
enablePhase1 = true          # Nether access requirements
enablePhase2 = true          # End access requirements

# Enable/disable mob kill requirements per phase
enableMobKillsPhase1 = true
enableMobKillsPhase2 = true
```

### Special Objectives
```toml
# Phase 1 Special Objectives
reqElderGuardian = true      # Require Elder Guardian kill
reqRaid = true              # Require winning a Pillager Raid
reqTrialVaultAdv = true     # Require Trial Vault advancement
reqVoluntaryExile = false   # Require Voluntary Exile advancement

# Phase 2 Special Objectives  
reqWither = true            # Require Wither kill
reqWarden = true            # Require Warden kill
```

### Mob Kill Requirements

#### Phase 1 Mobs:
```toml
# Common Overworld Mobs
reqZombieKills = 50
reqSkeletonKills = 40
reqStrayKills = 10
reqHuskKills = 10
reqSpiderKills = 30
reqCreeperKills = 30
reqDrownedKills = 20

# Special Mobs
reqEndermanKills = 5
reqWitchKills = 5
reqPillagerKills = 20
reqCaptainKills = 0          # Pillager Captains (0 = disabled)
reqVindicatorKills = 10
reqBoggedKills = 10
reqBreezeKills = 5

# Goal Kills (Rare/Powerful)
reqRavagerKills = 1
reqEvokerKills = 5
```

#### Phase 2 Mobs:
```toml
# Nether Mobs
reqBlazeKills = 20
reqWitherSkeletonKills = 15
reqPiglinBruteKills = 5
reqHoglinKills = 1
reqZoglinKills = 1
reqGhastKills = 10
reqPiglinKills = 30          # Hostile Piglins
reqEndermiteKills = 0        # Usually disabled
```

### Multiplier System
```toml
# Enable multipliers after phase completion
enableMultipliers = true
enableXpMultiplier = true

# Multiplier values
phase1Multiplier = 1.5       # 1.5x health/damage after Phase 1
phase2Multiplier = 2.0       # 2.0x health/damage after Phase 2
```

### Party System
```toml
# Enable party system
enablePartySystem = true

# Party settings
maxPartySize = 8             # Maximum players per party
partyInviteTimeout = 300     # Invitation timeout in seconds
```

### Debug & Admin
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

### 1. Casual Server (Reduced Requirements)
```toml
# Reduce mob kill requirements by 50%
reqZombieKills = 25
reqSkeletonKills = 20
reqSpiderKills = 15
reqCreeperKills = 15
reqDrownedKills = 10

# Disable some special objectives
reqWarden = false
reqVoluntaryExile = false

# Lower multipliers
phase1Multiplier = 1.2
phase2Multiplier = 1.5
```

### 2. Hardcore Server (Increased Requirements)
```toml
# Double mob kill requirements
reqZombieKills = 100
reqSkeletonKills = 80
reqSpiderKills = 60
reqCreeperKills = 60

# Enable all objectives
reqWarden = true
reqVoluntaryExile = true
reqTrialVaultAdv = true

# Higher multipliers
phase1Multiplier = 2.0
phase2Multiplier = 3.0
```

### 3. Party-Focused Server
```toml
# Encourage party play
maxPartySize = 12
enablePartySystem = true

# Higher base requirements (balanced by party scaling)
reqZombieKills = 80
reqSkeletonKills = 60

# Significant multipliers for challenge
phase1Multiplier = 2.5
phase2Multiplier = 4.0
```

### 4. Quick Progression Server
```toml
# Minimal requirements for fast progression
reqZombieKills = 10
reqSkeletonKills = 8
reqSpiderKills = 5

# Disable some objectives
reqElderGuardian = false
reqRaid = false
reqWarden = false

# No multipliers
enableMultipliers = false
```

### 5. Boss-Only Challenge
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

## 🎛️ Advanced Configuration

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
