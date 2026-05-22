Markdown
# Tower Of Death - Retro Edition (v1.2)

A classic, unforgiving Java-based dungeon crawler capturing the intense challenge and aesthetic of 1980s terminal-based interfaces. Ascend an obsidian spire, manage your resources, and fight your way to the ultimate trial on the 100th floor.

## ⚔️ Features

* **Classic Swing GUI:** A fully custom, thread-safe graphical interface featuring a static green-on-black viewport that eliminates terminal scrolling and captures a true retro PC feel.
* **10-Tier Equipment System:** A *Final Fantasy*-inspired progression system. Farm gold to upgrade your arsenal, scaling from a basic Broadsword and Leather Armor all the way up to the legendary Excalibur and Adamant Armor.
* **Dynamic Economy:** Monsters drop gold upon defeat. Spend your loot at the Tower Merchant, who dynamically updates their shop inventory based on your current player level.
* **100 Floors of Death:** Battle escalating packs of Thugs and face the terrifying Guardian Jackal boss every 10 floors.
* **Character Identities:** Customize your run by taking on the mantle of a Valiant Hero, Shadow Assassin, Mystic Mage, or Ottawa Vanguard.
* **Portable Save Slots:** Features a classic 3-slot save system (`slot1.sav`, `slot2.sav`, `slot3.sav`). The engine dynamically anchors to the `.jar` execution path, meaning you can put the game on a USB stick and take your save files anywhere.
* **Cinematic Narrative:** Experience a custom story prologue upon starting a new game, and an epic multi-page epilogue sequence if you manage to defeat the final Guardian at Level 100.

## 🚀 How to Play

### Running the Game
1. Ensure you have Java installed on your machine.
2. Double-click the compiled `TowerOfDeath.jar` file, or run it via command line:
   ```bash
   java -jar TowerOfDeath.jar
   ```
3. The game will automatically generate a savegames/ directory in the same folder as the .jar file to safely store your progress.

### Controls
All exploration, combat, and inventory management is handled via the dynamic COMMANDS button panel at the bottom of the window.

* **Combat: Choose to Attack, Defend, Heal, or Run Away.

* **Inventory: Equip newly purchased weapons and armor, or consume Health Potions in the field.

* **Shop: Visit the merchant from the Exploration Hub to spend your hard-earned gold.

### 🛠️ Architecture Notes for Developers
This project utilizes a dual-thread Swing architecture. The GUI is constructed safely on the Event Dispatch Thread (EDT), while the procedural game logic (menus, battles, pacing) runs independently on a Background Worker Thread.

A custom wait/notify synchronization loop intercepts Swing button clicks, passing the input strings back to the engine thread to simulate traditional blocking scanner input, creating a seamless bridge between console-style logic and a modern Java GUI.