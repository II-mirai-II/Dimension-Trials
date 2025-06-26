# 🎨 Custom Requirements System

**Revolutionary JSON-based system** for creating unlimited custom phases and integrating with any mod!

## 🌟 Key Features

* **Unlimited Custom Phases:** Create Phase 3, 4, 5+ with unique requirements
* **Mod Integration:** Full support for any Minecraft mod (Twilight Forest, Aether, etc.)
* **Custom Objectives:** Define special boss fights and advancement requirements
* **Flexible Multipliers:** Set custom health/damage/XP multipliers per phase
* **Dependency System:** Create complex progression chains between phases
* **No Coding Required:** Everything configured through JSON files

## 📁 Configuration Location

Custom requirements are stored in: `config/dimtr/custom_requirements/`

## 🚀 Quick Start Guide

1. **Run the mod once** - it automatically creates `example_requirements.json`
2. **Navigate to** `config/dimtr/custom_requirements/`
3. **Edit the example file** or create your own `.json` files
4. **Set `"enabled": true`** to activate your custom requirements
5. **Restart the server** to load changes

## 📋 Example Configuration

### Basic Example (Twilight Forest Integration):

```json
{
  "name": "Twilight Forest Integration",
  "description": "Phase 3 requirements for Twilight Forest",
  "enabled": true,
  "customPhases": {
    "phase3": {
      "name": "Phase 3: Twilight Forest",
      "description": "Master the Twilight Forest before progressing",
      "dimensionAccess": ["twilightforest:twilight_forest"],
      "requiredPreviousPhases": ["phase1", "phase2"],
      "specialObjectives": {
        "twilight_lich": {
          "displayName": "Lich Defeated",
          "description": "Kill the Twilight Lich boss",
          "required": true
        },
        "twilight_hydra": {
          "displayName": "Hydra Defeated", 
          "description": "Kill the Twilight Hydra boss",
          "required": true
        }
      },
      "mobRequirements": {
        "twilightforest:skeleton_druid": 15,
        "twilightforest:wraith": 10,
        "twilightforest:redcap": 25
      },
      "healthMultiplier": 2.5,
      "damageMultiplier": 2.5,
      "xpMultiplier": 2.5,
      "enabled": true
    }
  }
}
```

### Advanced Example (Multiple Phases):

```json
{
  "name": "Extended Progression Pack",
  "description": "Phases 3-5 for extended gameplay",
  "enabled": true,
  "customPhases": {
    "phase3": {
      "name": "Phase 3: Magical Dimensions",
      "requiredPreviousPhases": ["phase1", "phase2"],
      "dimensionAccess": ["twilightforest:twilight_forest", "botania:alfheim"],
      "mobRequirements": {
        "twilightforest:lich": 1,
        "botania:pixie": 50
      },
      "healthMultiplier": 2.0
    },
    "phase4": {
      "name": "Phase 4: Tech Dimensions", 
      "requiredPreviousPhases": ["phase3"],
      "dimensionAccess": ["industrialforegoing:mining"],
      "specialObjectives": {
        "tech_boss": {
          "displayName": "Tech Boss Defeated",
          "description": "Defeat the ultimate tech boss",
          "required": true
        }
      },
      "healthMultiplier": 3.0
    }
  }
}
```

## 🎯 What You Can Customize

### Phase Properties:
* **Phase Names & Descriptions:** Full localization support
* **Dimension Access:** Block access to any dimension until requirements are met
* **Phase Dependencies:** Define which phases must be completed first

### Requirements:
* **Special Objectives:** Custom boss fights and advancement requirements
* **Mob Requirements:** Any mob from any mod with custom kill counts
* **Complex Chains:** Multi-step progression requirements

### Multipliers & Rewards:
* **Health Multipliers:** Increase mob health for completed phases
* **Damage Multipliers:** Increase mob damage output
* **XP Multipliers:** Boost experience gains from combat

## 💡 Popular Use Cases

### Modpack Integration:
```json
{
  "name": "ATM9 Integration",
  "customPhases": {
    "phase3": {
      "name": "Phase 3: AllTheMods",
      "dimensionAccess": ["allthemodium:mining", "allthemodium:other"],
      "mobRequirements": {
        "allthemodium:piglich": 1
      }
    }
  }
}
```

### Challenge Server Setup:
```json
{
  "name": "Hardcore Challenge",
  "customPhases": {
    "phase3": {
      "name": "Phase 3: Extreme Challenge",
      "mobRequirements": {
        "minecraft:wither": 5,
        "minecraft:ender_dragon": 3
      },
      "healthMultiplier": 5.0,
      "damageMultiplier": 3.0
    }
  }
}
```

### Mod-Specific Progression:
```json
{
  "name": "Aether Progression",
  "customPhases": {
    "aether_phase": {
      "name": "Aether Mastery",
      "dimensionAccess": ["aether:the_aether"],
      "specialObjectives": {
        "slider_defeated": {
          "displayName": "Slider Defeated",
          "description": "Defeat the Slider boss in Aether",
          "required": true
        }
      },
      "mobRequirements": {
        "aether:valkyrie": 10,
        "aether:cockatrice": 25
      }
    }
  }
}
```

## 🔧 Configuration Reference

### Root Object Properties:
- `name` (string): Display name for the requirement set
- `description` (string): Description of what this adds
- `enabled` (boolean): Whether this requirement set is active
- `customPhases` (object): Map of phase IDs to phase definitions

### Phase Properties:
- `name` (string): Display name for the phase
- `description` (string): Phase description
- `enabled` (boolean, default: true): Whether this phase is active
- `dimensionAccess` (array): List of dimension IDs that require this phase
- `requiredPreviousPhases` (array): Phase IDs that must be completed first
- `specialObjectives` (object): Map of objective IDs to objective definitions
- `mobRequirements` (object): Map of mob IDs to required kill counts
- `healthMultiplier` (number, default: 1.0): Mob health multiplier after completion
- `damageMultiplier` (number, default: 1.0): Mob damage multiplier after completion
- `xpMultiplier` (number, default: 1.0): XP gain multiplier after completion

### Objective Properties:
- `displayName` (string): Name shown to players
- `description` (string): Detailed description
- `required` (boolean, default: true): Whether this objective is mandatory

## 🛠️ Troubleshooting

### Common Issues:

**Phase not loading:**
- Check JSON syntax with a validator
- Ensure `"enabled": true` is set
- Check server logs for parsing errors

**Mob requirements not working:**
- Verify mob ID format (use F3 debug info)
- Ensure mod is installed on server
- Check spelling and capitalization

**Dimension access not blocked:**
- Verify dimension ID format
- Ensure phase dependencies are met
- Check if dimension exists

### Debug Commands:
```
# Enable debug logging
/dimtr debug custom_requirements true

# Check loaded phases
/dimtr status custom

# Reload configuration
/dimtr reload custom_requirements
```

## 🎮 Integration Examples

### Popular Mod Combinations:

**Twilight Forest + Aether:**
```json
{
  "customPhases": {
    "magical_realms": {
      "name": "Magical Realms Mastery",
      "dimensionAccess": ["twilightforest:twilight_forest", "aether:the_aether"],
      "mobRequirements": {
        "twilightforest:lich": 1,
        "aether:valkyrie_queen": 1
      }
    }
  }
}
```

**Industrial Mods:**
```json
{
  "customPhases": {
    "tech_mastery": {
      "name": "Technology Mastery",
      "specialObjectives": {
        "fusion_reactor": {
          "displayName": "Fusion Reactor Built",
          "description": "Build a working fusion reactor"
        }
      }
    }
  }
}
```

---

**Back to [Main README](README.md)**
