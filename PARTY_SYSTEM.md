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

### **Smart Synchronization** *(Fixed in v1.3)*
- Fixed kill tracking to properly share between party members
- Corrected HUD display to show shared party progress
- Enhanced client-server synchronization for real-time updates

---

## 🎮 Party Commands

### Essential Commands
```
/dimtr party create          # Create new party (become leader)
/dimtr party invite <player> # Invite player to your party
/dimtr party accept          # Accept pending invitation
/dimtr party leave           # Leave current party
/dimtr party info            # Show detailed party status
```

### Leadership Commands *(Leaders only)*
```
/dimtr party kick <player>    # Remove member from party
/dimtr party promote <player> # Transfer leadership
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
- Monitor progress regularly with `/dimtr party info`
- Remove inactive players to maintain group efficiency
- Celebrate milestone completions to maintain motivation

---

## 🔧 Technical Details

### **Progress Calculation**
```
Individual Requirement = Base Requirement ÷ Party Size
Shared Pool = All Member Kills Combined
```

### **Fixed Issues (v1.3)**
- **Kill Distribution:** Kills now properly count for all party members
- **HUD Accuracy:** Client display shows shared party progress instead of individual
- **Progress Transfer:** Previous individual progress correctly transfers to party system
- **Synchronization:** Enhanced client-server communication for real-time updates

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

**🔗 Related Guides:** [Configuration](CONFIGURATION.md) | [Custom Requirements](CUSTOM_REQUIREMENTS.md) | [Main README](README.md)**
