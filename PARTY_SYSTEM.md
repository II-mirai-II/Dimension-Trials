# 👥 Party System Guide

**Cooperative progression system with shared objectives and reduced individual requirements**

## 🌟 How Parties Work

### **Shared Progress Pool**
- All party members contribute kills to the same objectives
- Requirements automatically scale down by party size
- **Example:** 100 zombie requirement ÷ 4 members = 25 kills each

### **Dynamic Scaling**
- Requirements update instantly as members join/leave
- Works across dimensions - members can hunt in different areas
- Progress persists even when members are offline

### **Smart Synchronization**
- Fixed kill tracking to properly share between party members
- Corrected HUD display to show shared party progress
- Enhanced client-server synchronization for real-time updates

### **Requirement Calculation**
- Base formula: `Individual Requirement = Base Requirement ÷ Party Size`
- Party size affects difficulty multipliers (up to 3.0x for 5+ members)
- Party size cap: 10 members maximum (configurable)
- Scaling examples:
  - 2 players: 50% requirement each, 1.5x multiplier
  - 3 players: 33% requirement each, 2.0x multiplier 
  - 4 players: 25% requirement each, 2.5x multiplier
  - 5+ players: 20% requirement each, 3.0x multiplier (maximum)

---

## 🎮 Party Commands

### Essential Commands
```
/party create          # Create new party (become leader)
/party invite <player> # Invite player to your party
/party accept          # Accept pending invitation
/party leave           # Leave current party
/party info            # Show detailed party status
```

### Leadership Commands *(Leaders only)*
```
/party kick <player>    # Remove member from party
/party promote <player> # Transfer leadership
/party disband          # Dissolve the entire party
```

### Advanced Commands
```
/party public           # Make party publicly joinable
/party private          # Make party invite-only
/party rename <name>    # Change party name
/party chat <message>   # Send message to party members
```

---

## 💡 Party Strategies

### **Small Groups (2-3 players)**
- **Coordinate hunting:** Split mob types between members
- **Resource sharing:** Pool weapons, armor, and supplies
- **Strategic positioning:** Cover different biomes simultaneously

### **Large Groups (4+ players)**  
- **Role specialization:** Scouts, fighters, support players
- **Territory division:** Assign regions to maximize efficiency
- **Communication:** Use voice chat or coordinate through game chat

### **Leadership Tips**
- Monitor progress regularly with `/party info`
- Remove inactive players to maintain group efficiency
- Celebrate milestone completions to maintain motivation

---

## 🔧 Technical Details

### **Progress Calculation**
```
Individual Requirement = Base Requirement ÷ Party Size
Party Multiplier = 1.0 + ((memberCount - 1) * 0.5) [capped at 3.0]
```

### **Multiplier System**
- **Health & Damage**: Parties increase enemy challenge proportionally
- **XP Gain**: Party members gain increased XP based on phase progression
- **Balancing**: System designed to make parties beneficial but not overpowered

### **Progress Synchronization**
- **Real-time Updates:** Progress syncs immediately when:
  - A member kills a required mob
  - A member completes a special objective
  - A member joins or leaves the party
- **Batch Processing:** Multiple kills are batched for network efficiency
- **Delta Updates:** Only changed data is transmitted to reduce network load
- **Conflict Resolution:** Server is the authority for resolving conflicting data

### **Data Persistence**
- Party data survives server restarts
- Offline member names are preserved and displayed correctly
- Progress automatically synchronizes when members reconnect

---

## 🎯 Party Benefits

### **Reduced Grinding**
- Significantly lower individual requirements
- Shared effort makes challenging objectives achievable
- Faster overall progression for groups

### **Enhanced Cooperation**
- Encourages teamwork and communication
- Builds stronger server communities
- Makes difficult content accessible to casual players

### **Flexible Gameplay**
- Members can contribute from different dimensions
- No proximity requirements for progress sharing
- Accommodates different play schedules and styles

---

## 🔄 Integration with Custom Phases

The party system fully integrates with custom phases, providing these additional benefits:

### **Shared Custom Phase Progress**
- All custom phase objectives are shared among party members
- Custom mob kills contribute to the shared party pool
- When one member completes a custom objective, it's shared with all members

### **Custom Dimension Access**
- When a party completes a custom phase, all members gain access to unlocked dimensions
- Party members can explore custom dimensions together without individual completion
- Dimensional access restrictions apply consistently to all party members

### **Custom Phase Celebration**
- Special celebration effects trigger for all online party members when a custom phase is completed
- All members receive notification messages for significant achievements
- HUD updates in real-time to show completion status

### **Custom Multipliers**
- Custom phase multipliers (health, damage, XP) apply to all party members
- Party members benefit from the same difficulty scaling
- Multiplier effects stack properly with party size adjustments

---

## 🔄 Party Progress Transfer

### **Joining a Party**
When a player joins a party:
1. Their individual progress is compared with the party's shared progress
2. For each objective, the higher value (individual or party) is used
3. The player retains access to any dimensions they've already unlocked
4. The party's shared progress is updated if the new member brings higher values

### **Leaving a Party**
When a player leaves a party:
1. They retain their current progress level
2. Their individual data is extracted from the shared pool
3. They continue progression individually from that point
4. The party's requirements automatically adjust for the remaining members

### **Party Disbanding**
When a party is disbanded:
1. All members retain their current progress level
2. Each player's data is converted to individual progression
3. All previously unlocked dimensions remain accessible
4. Players can immediately form new parties without losing progress

---

## 🛠️ Troubleshooting Party Issues

### **Common Party Problems**
- **Invites not working:** Check invitation timeout (default: 5 minutes)
- **Progress not syncing:** Ensure server and client are on the same mod version
- **Party members not showing:** Verify party data with `/party info`
- **Requirements not scaling:** Check if party system is enabled in config

### **Party Debug Commands**
```
/dimtr debug party              # Show detailed party debug info
/dimtr reload party             # Reload party system
/dimtr validate party <id>      # Validate party data integrity
```

### **Data Recovery**
If party data becomes corrupted:
1. Use `/dimtr backup list` to view available backups
2. Restore with `/dimtr backup restore <id>`
3. Individual player data can be recovered even if party data is lost

---

**🔗 Related Guides:** [Configuration](CONFIGURATION.md) | [Custom Requirements](CUSTOM_REQUIREMENTS.md) | [Main README](README.md)**
