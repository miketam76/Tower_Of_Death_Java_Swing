# Tower Of Death — Swing UI (Slot Save) Edition

This project is a small Java RPG originally written for the console and converted to a lightweight Swing UI. The game logic is unchanged, but all input and output are handled through a Swing window (no terminal libraries required).

Key points for this revision:
- Swing-only UI: the game runs in a window with a static header pane, an action log pane, and a dynamic button panel for choices.
- Save system: three fixed save slots (`slot1.sav`, `slot2.sav`, `slot3.sav`) stored in the `savegames/` folder next to the game working directory. The game asks for confirmation before overwriting an existing slot.
- Saves are two-line text files: line 1 is the character name (allows spaces); line 2 contains numeric attributes in a fixed order.
- The `savegames/` folder is created automatically using `File.mkdirs()` when the game starts.

## What changed
- UI is now Swing-based: `GameWindow.java` provides the panes and the button-driven input model.
- `runtime_Tester.java` initializes the Swing UI on the EDT and runs the `Game_Engine` on a background thread so the UI remains responsive.
- The save/load flow uses a three-slot interface (FF-style slots). Loading and saving are interactive: you pick the slot by clicking its button and the engine handles confirmation when overwriting.

## Files of interest
- `src/runtime_Tester.java` — program entry; boots Swing and starts the game thread.
- `src/GameWindow.java` — Swing UI (header, log, buttons); exposes blocking `getButtonInput()` for the game thread.
- `src/Game_Engine.java` — main menu, game menu, and save/load slot handling.
- `src/Battle_Engine.java` — battle flow and button-driven combat.
- `src/Player.java`, `src/Enemy_REG.java`, `src/Enemy_Jackal.java` — game models and combat calculations.

## Requirements
- Java Runtime (JRE) or JDK 8+ installed.

## Build & run (Windows PowerShell)
From the project root (where `src/` is located):

```powershell
# Compile all Java sources into the out/ directory
javac -d out src\*.java

# Run the game
java -cp out runtime_Tester
```

Or use your IDE (IntelliJ IDEA, Eclipse, NetBeans) and run the `runtime_Tester` main class.

## How to play
- Use the buttons in the window to make choices (attack, defend, heal, move between menus, save/load, etc.).
- The action log pane shows messages and auto-scrolls as new lines arrive.
- To load or save, choose the corresponding menu option; the game will show the three save slots and let you pick one.
- When saving, if the chosen slot already contains a file, the game requests confirmation before overwriting.

## Save format (for reference)
- `slotX.sav` (text file)
  - Line 1: character name (text, may contain spaces)
  - Line 2: numeric attributes separated by spaces in this order:
    EXP LVL HP MP STR AGL INT STA LCK ATK DEF CURRENTLEVEL

## Troubleshooting
- If the window appears but the game doesn't respond: ensure you launched `runtime_Tester` (it starts the UI and then starts the game logic thread).
- Save files are located in the `savegames/` folder relative to the working directory used when launching the game. The game attempts to create this folder automatically.
- If a save file is corrupted or missing expected data, the loader will present an error and allow you to retry.

## Development notes
- The UI uses `SwingUtilities.invokeLater` for safe EDT updates.
- Game logic waits for user input via a synchronized wait/notify approach in `GameWindow.getButtonInput()` so the background game thread can block without freezing the UI.
- File I/O uses try-with-resources patterns in the engine to close readers/writers safely.
