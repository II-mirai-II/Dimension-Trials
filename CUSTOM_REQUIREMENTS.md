# 🎨 Custom Requirements System

**Create unlimited custom phases and integrate any mod - no coding required!**

## 🌟 Overview

Transform Dimension Trials into the perfect progression system for your modpack with JSON-based custom phases. Add Phase 3, 4, 5+ with unique requirements, boss fights, and mod integration.

### Key Capabilities
- **Unlimited Phases:** Create as many progression phases as needed
- **Universal Mod Support:** Integrate Twilight Forest, Aether, Industrial mods, and more
- **Complex Dependencies:** Build intricate progression chains between phases
- **Custom Multipliers:** Define unique difficulty scaling per phase
- **Zero Coding:** Simple JSON configuration system

---

## 🚀 Quick Setup

1. **Start the mod** - Auto-creates `config/dimtr/custom_requirements/example_requirements.json`
2. **Edit the example file** or create new `.json` files in the same folder
3. **Set `"enabled": true`** to activate your requirements
4. **Restart server** to load changes

---

## 📋 Basic Configuration

### Simple Twilight Forest Phase
```json
{
  "name": "Twilight Forest Expansion",
  "description": "Phase 3 - Master the Twilight Forest",
  "enabled": true,
  "customPhases": {
    "phase3": {
      "name": "Phase 3: Twilight Mastery",
      "description": "Conquer the magical forest dimension",
      "dimensionAccess": ["twilightforest:twilight_forest"],
      "requiredPreviousPhases": ["phase1", "phase2"],
      "mobRequirements": {
        "twilightforest:skeleton_druid": 15,
        "twilightforest:wraith": 10,
        "twilightforest:redcap": 25
      },
      "specialObjectives": {
        "twilight_lich": {
          "displayName": "Lich Defeated",
          "description": "Defeat the Twilight Lich boss",
          "required": true
        }
      },
      "healthMultiplier": 2.5,
      "damageMultiplier": 2.5,
      "enabled": true
    }
  }
}
```

### Multi-Phase Progression
```json
{
  "name": "Extended Progression Pack",
  "enabled": true,
  "customPhases": {
    "phase3": {
      "name": "Phase 3: Magical Realms",
      "requiredPreviousPhases": ["phase1", "phase2"],
      "dimensionAccess": ["twilightforest:twilight_forest", "aether:the_aether"],
      "mobRequirements": {
        "twilightforest:lich": 1,
        "aether:valkyrie": 10
      },
      "healthMultiplier": 2.0
    },
    "phase4": {
      "name": "Phase 4: Industrial Complex",
      "requiredPreviousPhases": ["phase3"],
      "specialObjectives": {
        "fusion_reactor": {
          "displayName": "Fusion Reactor Built",
          "description": "Construct and activate a fusion reactor"
        }
      },
      "healthMultiplier": 3.0
    }
  }
}
```

---

## 🎯 Popular Integrations

### AllTheMods Integration
```json
{
  "customPhases": {
    "atm_phase": {
      "name": "AllTheMods Mastery",
      "dimensionAccess": ["allthemodium:mining", "allthemodium:other"],
      "mobRequirements": {
        "allthemodium:piglich": 1
      }
    }
  }
}
```

### Create Mod Integration
```json
{
  "customPhases": {
    "tech_phase": {
      "name": "Industrial Revolution",
      "specialObjectives": {
        "automation_master": {
          "displayName": "Automation Master",
          "description": "Build advanced Create contraptions"
        }
      }
    }
  }
}
```

---

## 🔧 Configuration Reference

### Phase Properties
- **`name`** (string): Display name for the phase
- **`description`** (string): Detailed phase description  
- **`enabled`** (boolean): Whether this phase is active
- **`dimensionAccess`** (array): Dimensions requiring this phase completion
- **`requiredPreviousPhases`** (array): Phases that must be completed first
- **`mobRequirements`** (object): Mob ID → kill count mappings
- **`specialObjectives`** (object): Custom objective definitions
- **`healthMultiplier`** (number): Mob health multiplier after completion
- **`damageMultiplier`** (number): Mob damage multiplier after completion
- **`xpMultiplier`** (number): XP gain multiplier after completion

### Objective Properties
- **`displayName`** (string): Name shown to players
- **`description`** (string): Detailed objective description
- **`required`** (boolean): Whether objective is mandatory

---

## 🛠️ Troubleshooting

### Common Issues
- **Phase not loading:** Validate JSON syntax, ensure `"enabled": true`
- **Mob requirements not working:** Verify mob IDs with F3 debug info
- **Dimension blocking not working:** Check dimension ID format and mod installation

### Debug Commands
```
/dimtr debug custom_requirements true    # Enable debug logging
/dimtr status custom                     # Check loaded phases
/dimtr reload custom_requirements        # Reload configuration
```

---

## 💡 Best Practices

1. **Start Simple:** Begin with basic mob requirements, add complexity gradually
2. **Test Thoroughly:** Validate mob IDs and dimension names before deployment
3. **Balance Multipliers:** Consider server population and intended difficulty
4. **Document Changes:** Keep clear descriptions for server players
5. **Backup Configs:** Save working configurations before major changes

---

**🔗 Related Guides:** [Configuration](CONFIGURATION.md) | [Party System](PARTY_SYSTEM.md) | [Main README](README.md)**
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
