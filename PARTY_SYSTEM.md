# 👥 Party System Guide

**NEW in v1.3!** Team up with friends to tackle the challenges together with reduced individual requirements.

## 🌟 Key Benefits

* **Reduced Requirements:** Mob kill requirements are divided by the number of party members
* **Shared Progress:** All party members contribute to the same objectives
* **Real-time HUD:** See party members, leader status, and current multipliers
* **Persistent Names:** Offline party members show their actual usernames
* **Dynamic Updates:** Requirements automatically adjust when members join/leave

## 🎮 How It Works

* **Example:** If a requirement is 100 zombie kills and you have 4 party members, each member only needs 25 kills
* **Shared Pool:** All kills from party members count toward the same goal
* **Fair Distribution:** Requirements scale down automatically, making progression achievable for teams

## 📋 Party Commands

* `/dimtr party create` - Create a new party (becomes leader)
* `/dimtr party invite <player>` - Invite a player to your party
* `/dimtr party accept` - Accept a party invitation
* `/dimtr party leave` - Leave your current party
* `/dimtr party kick <player>` - Kick a member (leaders only)
* `/dimtr party promote <player>` - Promote to leader (leaders only)
* `/dimtr party info` - Display detailed party information

## 💡 Party Strategies

### For Small Groups (2-3 players):
* **Coordinate hunting:** Divide mob types between members
* **Share resources:** Pool weapons and armor for efficiency
* **Plan expeditions:** Tackle rare mobs together

### for Large Groups (4+ players):
* **Assign roles:** Designate scouts, fighters, and support players
* **Territory division:** Split up to cover more ground
* **Communication:** Use in-game chat or external voice chat

### Leadership Tips:
* **Monitor progress:** Use `/dimtr party info` regularly
* **Keep team motivated:** Celebrate milestone completions
* **Manage membership:** Remove inactive players when necessary

## 🔧 Technical Details

### Multiplier Calculation:
```
Individual Requirement = Base Requirement ÷ Party Size
```

### Progress Sharing:
- All kills count toward party progress regardless of who makes the kill
- Objectives completed by any member benefit the entire party
- Party members can be in different dimensions and still contribute

### Data Persistence:
- Party data survives server restarts
- Offline members remain in party with cached names
- Progress is automatically synchronized when members reconnect

---

**Back to [Main README](README.md)**
