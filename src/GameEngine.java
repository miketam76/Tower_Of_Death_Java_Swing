import java.util.Scanner;
import java.io.*;

public class GameEngine {

    private Player hero;
    private BattleEngine battle;
    private GameWindow window;
    private final String SAVEPATH;
    private final double version = 1.2;

    private final int NEWGAME = 1;
    private final int LOADGAME = 2;
    private final int QUIT = 0;

    private int CURRENTLEVEL = 1;
    private String currentLocation = "CROSSROADS";

    public GameEngine(GameWindow window) {
        this.window = window;
        this.SAVEPATH = locateJarDirectory() + "savegames" + File.separator;
        ensureSaveDirectoryExists();
    }

    private String locateJarDirectory() {
        try {
            String path = GameEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(path);
            if (path.endsWith(".jar")) return jarFile.getParent() + File.separator;
            return jarFile.getPath() + File.separator;
        } catch (Exception e) {
            return "." + File.separator;
        }
    }

    private void ensureSaveDirectoryExists() {
        File folder = new File(SAVEPATH);
        if (!folder.exists()) folder.mkdirs();
    }

    public void main_Menu()
    {
        int choice = -1;
        do
        {
            // CRITICAL FIX: Intercept a dead player before they reach the main menu
            if (hero != null && hero.getHP() <= 0) {
                handleGameOver();
                continue; // Re-evaluate the loop state after the game over is handled
            }

            window.clearLog();
            window.updateHeader("MAIN MENU");
            window.clearButtons();
            window.addButton("New Game", "1");
            window.addButton("Load Game", "2");
            window.addButton("Quit", "0");

            window.appendLog("Welcome to Tower Of Death - version " + version);
            window.appendLog("\nPlease select an option below.");

            try { choice = Integer.parseInt(window.getButtonInput()); }
            catch (NumberFormatException e) { choice = -1; }

            switch(choice)
            {
                case NEWGAME:
                    hero = new Player();
                    changeName();
                    triggerIntroSequence();
                    currentLocation = "CROSSROADS";
                    CURRENTLEVEL = 1;
                    overworld_Menu();
                    break;
                case LOADGAME:
                    if(load_game_menu()) {
                        if (currentLocation.equals("CROSSROADS") || currentLocation.equals("CITY")) {
                            overworld_Menu();
                        } else {
                            game_Menu();
                        }
                    }
                    break;
                case QUIT:
                    window.clearLog();
                    window.updateHeader("GAME OVER");
                    window.appendLog("Exiting Tower Of Death. Thank you for playing!");
                    window.clearButtons();
                    try { Thread.sleep(800); } catch (InterruptedException e) {}
                    System.exit(0);
                    break;
            }
        } while(choice != QUIT);
    }

    // --- NEW: THE GAME OVER INTERCEPTOR ---
    private void handleGameOver() {
        String choice = "";
        do {
            window.clearLog();
            window.updateHeader("GAME OVER");
            window.appendLog("Your hero has fallen in battle. The darkness consumes you.");
            window.appendLog("What will be your fate?");

            window.clearButtons();
            window.addButton("Load Saved Game", "load");
            window.addButton("Start New Game", "new");

            choice = window.getButtonInput();

            if (choice.equals("load")) {
                if (!hasAnySaveFiles()) {
                    window.clearLog();
                    window.appendLog("There are no saved games available!");
                    waitForAck();
                } else {
                    if (load_game_menu()) {
                        // Load successful, route player to correct location
                        if (currentLocation.equals("CROSSROADS") || currentLocation.equals("CITY")) {
                            overworld_Menu();
                        } else {
                            game_Menu();
                        }
                        return; // Unwind out of the game over state
                    }
                }
            } else if (choice.equals("new")) {
                hero = new Player();
                changeName();
                triggerIntroSequence();
                currentLocation = "CROSSROADS";
                CURRENTLEVEL = 1;
                overworld_Menu();
                return; // Unwind out of the game over state
            }
        } while (true);
    }

    // Helper method to check if the save folder is empty
    private boolean hasAnySaveFiles() {
        File f1 = new File(SAVEPATH + "slot1.sav");
        File f2 = new File(SAVEPATH + "slot2.sav");
        File f3 = new File(SAVEPATH + "slot3.sav");

        return (f1.exists() && f1.length() > 0) ||
                (f2.exists() && f2.length() > 0) ||
                (f3.exists() && f3.length() > 0);
    }

    private void overworld_Menu() {
        currentLocation = "CROSSROADS";
        String choice = "";
        do {
            window.clearLog();
            window.updateHeader("Location: The Crossroads | Gold: " + hero.getGold());
            window.appendLog("You stand at a foggy crossroads. The wind howls through the valley.");
            window.appendLog("Where will your journey take you?");

            window.clearButtons();

            if (hero.hasTowerKey()) {
                window.addButton("Tower of Death (Lv 1-100)", "tower");
            } else {
                window.addButton("Tower of Death (LOCKED)", "tower");
            }

            window.addButton("Whispering Woods (Lv 1-20)", "woods");
            window.addButton("Capital City (Shops & Inn)", "city");
            window.addButton("Save Game", "save");
            window.addButton("Main Menu", "main");

            choice = window.getButtonInput();

            switch(choice) {
                case "tower":
                    if (hero.hasTowerKey()) {
                        currentLocation = "TOWER";
                        CURRENTLEVEL = 1;
                        game_Menu();
                        // CRITICAL FIX: Ensure death unwinds the menu!
                        if (hero.getHP() <= 0) return;
                    } else {
                        window.clearLog();
                        window.appendLog("The massive iron doors of the Tower of Death are sealed shut.");
                        window.appendLog("A mystic keyhole glows with a faint, eerie light...");
                        window.appendLog("\n(You must defeat the Gate Keeper in the Whispering Woods to obtain the key).");
                        waitForAck();
                    }
                    break;
                case "woods":
                    currentLocation = "WOODS";
                    CURRENTLEVEL = 1;
                    game_Menu();
                    // CRITICAL FIX: Ensure death unwinds the menu!
                    if (hero.getHP() <= 0) return;
                    break;
                case "city":
                    city_Menu();
                    break;
                case "save":
                    save_game_menu();
                    break;
            }
        } while (!choice.equals("main"));
    }

    private void city_Menu() {
        currentLocation = "CITY";
        String choice = "";
        do {
            window.clearLog();
            window.updateHeader("Location: Capital City | Gold: " + hero.getGold());
            window.appendLog("The bustling streets of the Capital City surround you.");
            window.appendLog("Merchants hawk their wares, and the local Inn promises a safe rest.");

            window.clearButtons();
            window.addButton("Visit Merchant", "shop");
            window.addButton("Rest at Inn (50g)", "inn");
            window.addButton("Inventory", "inv");
            window.addButton("Show Stats", "stats");
            window.addButton("Leave City", "leave");

            choice = window.getButtonInput();

            switch(choice) {
                case "shop":
                    shop_Menu();
                    break;
                case "inn":
                    if (hero.getGold() >= 50) {
                        hero.subtractGold(50);
                        hero.healHP();
                        window.clearLog();
                        window.appendLog("You pay the innkeeper 50 gold and fall into a deep, dreamless sleep.");
                        window.appendLog("HP Fully Restored!");
                    } else {
                        window.clearLog();
                        window.appendLog("\"No coin, no bed!\" the innkeeper barks.");
                    }
                    waitForAck();
                    break;
                case "inv":
                    inventory_Menu();
                    break;
                case "stats":
                    window.clearLog();
                    window.appendLog(hero.playerStats());
                    waitForAck();
                    break;
            }
        } while (!choice.equals("leave"));

        currentLocation = "CROSSROADS";
    }

    public void game_Menu()
    {
        String choice;
        int maxZoneLevel = currentLocation.equals("TOWER") ? 100 : 20;
        String zoneName = currentLocation.equals("TOWER") ? "Tower of Death" : "Whispering Woods";

        do
        {
            window.clearLog();
            window.updateHeader("Location: " + zoneName + " | Level: " + CURRENTLEVEL + " | HP: " + hero.getHP());

            if (currentLocation.equals("TOWER")) {
                window.appendLog("The stench of death and rusted iron fills your lungs. The obsidian walls pulse.");
            } else if (currentLocation.equals("WOODS")) {
                window.appendLog("Twisted branches reach out like skeletal fingers. The mist hides horrors in the dark.");
            }

            window.appendLog("\n--- Exploration Options ---");

            window.clearButtons();

            if(CURRENTLEVEL == maxZoneLevel) {
                window.addButton("Portal to Start", "1");
            } else {
                window.addButton("Go to Level " + (CURRENTLEVEL + 1), "1");
            }

            window.addButton("Enter Level " + CURRENTLEVEL, "2");
            window.addButton("Inventory", "3");
            window.addButton("Show Stats", "4");
            window.addButton("Flee to Overworld", "5");

            choice = window.getButtonInput();

            switch(choice)
            {
                case "1":
                    if(CURRENTLEVEL == maxZoneLevel) {
                        window.clearLog();
                        window.appendLog("You have reached the end of this zone. A portal back to the entrance swirls before you.");
                        window.clearButtons();
                        window.addButton("Enter Portal (Yes)", "y");
                        window.addButton("Stay Here (No)", "n");
                        if(window.getButtonInput().equalsIgnoreCase("y")) {
                            CURRENTLEVEL = 1;
                            waitForAck();
                        }
                    }
                    else CURRENTLEVEL++;
                    break;
                case "2":
                    window.clearLog();
                    window.appendLog("Delving into " + zoneName + " - Level " + CURRENTLEVEL + "...\n");
                    battle = new BattleEngine(window);
                    hero = battle.battle_loader(hero, CURRENTLEVEL, currentLocation);
                    break;
                case "3":
                    inventory_Menu();
                    break;
                case "4":
                    window.clearLog();
                    window.appendLog(hero.playerStats());
                    waitForAck();
                    break;
                case "5":
                    window.clearLog();
                    window.appendLog("You turn back and flee to the safety of the Crossroads...");
                    waitForAck();
                    currentLocation = "CROSSROADS";
                    break;
            }
        } while(!choice.equals("5") && hero.getHP() > 0);

        // CRITICAL FIX: If you died, instantly return out of this menu to let the unwinder handle the Game Over!
        if (hero.getHP() <= 0) {
            return;
        }
    }

    private void inventory_Menu() {
        String choice = "";
        do {
            window.clearLog();
            window.updateHeader("INVENTORY | Gold: " + hero.getGold());
            window.appendLog("Weapon: " + (hero.getWeapon() != null ? hero.getWeapon().getName() : "None"));
            window.appendLog("Armor:  " + (hero.getArmor() != null ? hero.getArmor().getName() : "None"));
            window.appendLog("Helmet: " + (hero.getHelmet() != null ? hero.getHelmet().getName() : "None"));
            window.appendLog("Shield: " + (hero.getShield() != null ? hero.getShield().getName() : "None"));
            window.appendLog("\nSelect an item in your bag to equip or use it:");

            window.clearButtons();
            if (hero.getInventory().isEmpty()) {
                window.appendLog("\nYour bag is currently empty.");
            } else {
                for(int i = 0; i < hero.getInventory().size(); i++) {
                    window.addButton(hero.getInventory().get(i).getName(), String.valueOf(i));
                }
            }
            window.addButton("Back", "back");

            choice = window.getButtonInput();

            if(!choice.equals("back")) {
                int index = Integer.parseInt(choice);
                Item selected = hero.getInventory().get(index);

                window.clearLog();
                if(selected.getType() == Item.POTION) {
                    hero.healHP();
                    hero.getInventory().remove(index);
                    window.appendLog("You drank the " + selected.getName() + " and fully restored your HP!");
                } else if(selected.getType() == Item.WEAPON) {
                    if (hero.getWeapon() != null) hero.getInventory().add(hero.getWeapon());
                    hero.setWeapon(selected);
                    hero.getInventory().remove(index);
                    window.appendLog("You equipped the " + selected.getName() + "!");
                } else if(selected.getType() == Item.ARMOR) {
                    if (hero.getArmor() != null) hero.getInventory().add(hero.getArmor());
                    hero.setArmor(selected);
                    hero.getInventory().remove(index);
                    window.appendLog("You put on the " + selected.getName() + "!");
                } else if(selected.getType() == Item.HELMET) {
                    if (hero.getHelmet() != null) hero.getInventory().add(hero.getHelmet());
                    hero.setHelmet(selected);
                    hero.getInventory().remove(index);
                    window.appendLog("You put on the " + selected.getName() + "!");
                } else if(selected.getType() == Item.SHIELD) {
                    if (hero.getShield() != null) hero.getInventory().add(hero.getShield());
                    hero.setShield(selected);
                    hero.getInventory().remove(index);
                    window.appendLog("You equipped the " + selected.getName() + "!");
                }
                waitForAck();
            }
        } while(!choice.equals("back"));
    }

    private void shop_Menu() {
        String choice = "";
        do {
            window.clearLog();
            window.updateHeader("TOWER MERCHANT | Your Gold: " + hero.getGold());
            window.appendLog("A mysterious cloaked figure gestures to their wares.");
            window.appendLog("\"What are you buying, stranger?\"");

            window.clearButtons();
            window.addButton("Health Potion (50g)", "Health Potion");

            int pLvl = hero.getLVL();

            if (pLvl >= 90) { addShopTierToButtons(10); }
            else if (pLvl >= 80) { addShopTierToButtons(9); }
            else if (pLvl >= 70) { addShopTierToButtons(8); }
            else if (pLvl >= 60) { addShopTierToButtons(7); }
            else if (pLvl >= 50) { addShopTierToButtons(6); }
            else if (pLvl >= 40) { addShopTierToButtons(5); }
            else if (pLvl >= 30) { addShopTierToButtons(4); }
            else if (pLvl >= 20) { addShopTierToButtons(3); }
            else if (pLvl >= 10) { addShopTierToButtons(2); }
            else { addShopTierToButtons(1); }

            window.addButton("Sell Items", "sell_menu");
            window.addButton("Leave Shop", "leave");

            choice = window.getButtonInput();

            if (choice.equals("sell_menu")) {
                sell_Menu();
            } else if (choice.equals("owned")) {
                window.clearLog();
                window.appendLog("\"You already own that, stranger!\"");
                waitForAck();
            } else if (!choice.equals("leave")) {
                Item desiredItem = Item.getByName(choice);
                window.clearLog();

                if (hero.getGold() >= desiredItem.getCost()) {
                    hero.subtractGold(desiredItem.getCost());
                    hero.getInventory().add(desiredItem);
                    window.appendLog("\"Heh heh... thank you!\"");
                    window.appendLog("\n" + desiredItem.getName() + " added to your inventory.");
                } else {
                    window.appendLog("\"Not enough cash, stranger!\"");
                }
                waitForAck();
            }
        } while(!choice.equals("leave"));
    }

    private void sell_Menu() {
        String choice = "";
        do {
            window.clearLog();
            window.updateHeader("SELL ITEMS | Your Gold: " + hero.getGold());
            window.appendLog("\"What are you selling? I pay half price for old gear!\"");
            window.appendLog("(Equipped items are hidden safely on your character)");

            window.clearButtons();
            if (hero.getInventory().isEmpty()) {
                window.appendLog("\nYour bag is currently empty.");
            } else {
                for(int i = 0; i < hero.getInventory().size(); i++) {
                    Item item = hero.getInventory().get(i);
                    int sellPrice = item.getCost() / 2;
                    window.addButton(item.getName() + " (+" + sellPrice + "g)", "sell_" + i);
                }
            }
            window.addButton("Back to Shop", "back");

            choice = window.getButtonInput();

            if(choice.startsWith("sell_")) {
                int index = Integer.parseInt(choice.split("_")[1]);
                Item selected = hero.getInventory().get(index);
                int sellPrice = selected.getCost() / 2;

                hero.addGold(sellPrice);
                hero.getInventory().remove(index);

                window.clearLog();
                window.appendLog("\"Heh heh... a fine piece! Here's " + sellPrice + " gold.\"");
                waitForAck();
            }
        } while(!choice.equals("back"));
    }

    private void addShopItem(String itemName, int cost) {
        if (hero.hasEquipment(itemName)) {
            window.addButton(itemName + " (Owned)", "owned");
        } else {
            window.addButton(itemName + " (" + cost + "g)", itemName);
        }
    }

    private void addShopTierToButtons(int tier) {
        switch (tier) {
            case 1:
                addShopItem("Broadsword", 100); addShopItem("Leather Armor", 50); addShopItem("Leather Cap", 25); addShopItem("Leather Shield", 25); break;
            case 2:
                addShopItem("Longsword", 500); addShopItem("Bronze Armor", 250); addShopItem("Bronze Helm", 125); addShopItem("Bronze Shield", 125); break;
            case 3:
                addShopItem("Iron Sword", 1500); addShopItem("Iron Armor", 750); addShopItem("Iron Helm", 375); addShopItem("Iron Shield", 375); break;
            case 4:
                addShopItem("Dark Sword", 3000); addShopItem("Dark Armor", 1500); addShopItem("Dark Helm", 750); addShopItem("Dark Shield", 750); break;
            case 5:
                addShopItem("Mythril Sword", 6000); addShopItem("Mythril Armor", 3000); addShopItem("Mythril Helm", 1500); addShopItem("Mythril Shield", 1500); break;
            case 6:
                addShopItem("Flame Sword", 10000); addShopItem("Flame Mail", 5000); addShopItem("Flame Helm", 2500); addShopItem("Flame Shield", 2500); break;
            case 7:
                addShopItem("Ice Brand", 16000); addShopItem("Ice Armor", 8000); addShopItem("Ice Helm", 4000); addShopItem("Ice Shield", 4000); break;
            case 8:
                addShopItem("Defender", 25000); addShopItem("Genji Armor", 12500); addShopItem("Genji Helm", 6250); addShopItem("Genji Shield", 6250); break;
            case 9:
                addShopItem("Ragnarok", 40000); addShopItem("Crystal Mail", 20000); addShopItem("Crystal Helm", 10000); addShopItem("Crystal Shield", 10000); break;
            case 10:
                addShopItem("Excalibur", 75000); addShopItem("Adamant Armor", 37500); addShopItem("Ribbon", 18750); addShopItem("Aegis Shield", 18750); break;
        }
    }

    private void changeName()
    {
        window.clearLog();
        window.updateHeader("CHARACTER CUSTOMIZATION");
        window.appendLog("Choose a default character identity class designation profile:");

        window.clearButtons();
        window.addButton("Valiant Hero", "Valiant Hero");
        window.addButton("Shadow Assassin", "Shadow Assassin");
        window.addButton("Mystic Mage", "Mystic Mage");
        window.addButton("Ottawa Vanguard", "Ottawa Vanguard");

        hero.setName(window.getButtonInput());
        waitForAck();
    }

    private void triggerIntroSequence() {
        window.clearLog();
        window.updateHeader("= THE LEGEND =");
        window.appendLog("For centuries, the obsidian spire known as the Tower of Death has pierced the sky.");
        window.appendLog("It is a place of absolute darkness, crawling with cursed entities and ancient guardians.");
        window.appendLog("Thousands of brave souls have entered its heavy iron doors. None have ever returned.");
        waitForIntroAck("Next");

        window.clearLog();
        window.updateHeader("= THE ARRIVAL =");
        window.appendLog("You stand before the towering gates, the freezing wind howling through the valley.");
        window.appendLog("You have traveled far to claim the legendary power sealed at the 100th floor.");
        window.appendLog("But the massive iron doors refuse to budge. A mystic keyhole glows faintly in the center...");
        waitForIntroAck("Seek the Key");

        window.clearLog();
        window.updateHeader("= THE OVERWORLD =");
        window.appendLog("You turn your back on the locked Tower. To the east lies the Whispering Woods.");
        window.appendLog("Legends say a terrifying Gate Keeper lurks within, holding the Obsidian Key.");
        window.appendLog("\nSteel your nerves, " + hero.getName() + ". Your trial begins now.");
        waitForIntroAck("Begin Journey");
    }

    private void waitForIntroAck(String buttonLabel) {
        window.clearButtons();
        window.addButton(buttonLabel, "next");
        window.getButtonInput();
    }

    public boolean load_game_menu() {
        window.clearLog();
        window.updateHeader("LOAD GAME PROFILE");
        window.appendLog("Select a storage data slot to recover your file:\n");

        window.appendLog("[Slot 1] " + getSlotMetadata("slot1.sav"));
        window.appendLog("[Slot 2] " + getSlotMetadata("slot2.sav"));
        window.appendLog("[Slot 3] " + getSlotMetadata("slot3.sav"));

        window.clearButtons();
        window.addButton("Slot 1", "slot1.sav");
        window.addButton("Slot 2", "slot2.sav");
        window.addButton("Slot 3", "slot3.sav");
        window.addButton("Cancel", "cancel");

        String selectedSlot = window.getButtonInput();
        if (selectedSlot.equals("cancel")) return false;

        File targetFile = new File(SAVEPATH + selectedSlot);
        if (!targetFile.exists()) {
            window.clearLog();
            window.appendLog("There is no data saved in this slot!");
            waitForAck();
            return false;
        }

        return executeLoad(targetFile);
    }

    public void save_game_menu() {
        window.clearLog();
        window.updateHeader("SAVE GAME PROGRESS");
        window.appendLog("Select a storage destination slot:\n");

        window.appendLog("[Slot 1] " + getSlotMetadata("slot1.sav"));
        window.appendLog("[Slot 2] " + getSlotMetadata("slot2.sav"));
        window.appendLog("[Slot 3] " + getSlotMetadata("slot3.sav"));

        window.clearButtons();
        window.addButton("Slot 1", "slot1.sav");
        window.addButton("Slot 2", "slot2.sav");
        window.addButton("Slot 3", "slot3.sav");
        window.addButton("Cancel", "cancel");

        String selectedSlot = window.getButtonInput();
        if (selectedSlot.equals("cancel")) return;

        File targetFile = new File(SAVEPATH + selectedSlot);
        if (targetFile.exists()) {
            window.clearLog();
            window.updateHeader("WARNING: OVERWRITE DETECTED");
            window.appendLog("This slot already contains saved data:\n -> " + getSlotMetadata(selectedSlot));
            window.appendLog("\nAre you absolutely sure you want to overwrite it?");
            window.clearButtons();
            window.addButton("Yes (Overwrite)", "confirm");
            window.addButton("No (Cancel)", "cancel");
            if (window.getButtonInput().equals("cancel")) return;
        }
        executeSave(targetFile);
    }

    private String getSlotMetadata(String filename) {
        File f = new File(SAVEPATH + filename);
        if (!f.exists()) return "- Empty Slot -";

        try (Scanner peekReader = new Scanner(f)) {
            if (!peekReader.hasNextLine()) return "- Empty Slot -";
            String name = peekReader.nextLine().trim();
            long exp = peekReader.nextLong();
            int lvl = peekReader.nextInt();
            int hp = peekReader.nextInt();
            return name + " (Level " + lvl + ") - HP: " + hp;
        } catch (Exception e) {
            return "- Corrupted File Data -";
        }
    }

    private boolean executeLoad(File targetFile) {
        try (Scanner filereader = new Scanner(targetFile)) {
            if (!filereader.hasNextLine()) return false;
            String name = filereader.nextLine().trim();
            long exp = filereader.nextLong();
            int lvl = filereader.nextInt();
            int hp = filereader.nextInt();
            int mp = filereader.nextInt();
            int str = filereader.nextInt();
            int agl = filereader.nextInt();
            int Int = filereader.nextInt();
            int sta = filereader.nextInt();
            int lck = filereader.nextInt();
            int atk = filereader.nextInt();
            int def = filereader.nextInt();
            int gold = filereader.nextInt();
            CURRENTLEVEL = filereader.nextInt();
            currentLocation = filereader.next();
            boolean hasTowerKey = filereader.nextBoolean(); // NEW: Read Key Flag

            hero = new Player(name, exp, lvl, hp, mp, str, agl, Int, sta, lck, atk, def, gold, hasTowerKey);

            String wepName = filereader.next();
            if (!wepName.equals("None")) hero.setWeapon(Item.getByName(wepName));
            String armName = filereader.next();
            if (!armName.equals("None")) hero.setArmor(Item.getByName(armName));
            String helmName = filereader.next();
            if (!helmName.equals("None")) hero.setHelmet(Item.getByName(helmName));
            String shieldName = filereader.next();
            if (!shieldName.equals("None")) hero.setShield(Item.getByName(shieldName));

            int invSize = filereader.nextInt();
            for(int i = 0; i < invSize; i++) {
                hero.getInventory().add(Item.getByName(filereader.next()));
            }

            return true;
        } catch (Exception e) {
            window.clearLog();
            window.appendLog("Critical System Fault: Unable to load save data structure.");
            waitForAck();
            return false;
        }
    }

    private void executeSave(File targetFile) {
        window.clearLog();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(targetFile))) {
            out.write(hero.getName()); out.newLine();
            out.write(hero.getEXP() + " ");
            out.write(hero.getLVL() + " ");
            out.write(hero.getHP() + " ");
            out.write(hero.getMP() + " ");
            out.write(hero.getSTR() + " ");
            out.write(hero.getAGL() + " ");
            out.write(hero.getINT() + " ");
            out.write(hero.getSTA() + " ");
            out.write(hero.getLCK() + " ");
            out.write(hero.getATK() + " ");
            out.write(hero.getDEF() + " ");
            out.write(hero.getGold() + " ");
            out.write(CURRENTLEVEL + " ");
            out.write(currentLocation + " ");
            out.write(hero.hasTowerKey() + " "); // NEW: Save Key Flag

            out.write((hero.getWeapon() != null ? hero.getWeapon().getName().replace(" ", "_") : "None") + " ");
            out.write((hero.getArmor() != null ? hero.getArmor().getName().replace(" ", "_") : "None") + " ");
            out.write((hero.getHelmet() != null ? hero.getHelmet().getName().replace(" ", "_") : "None") + " ");
            out.write((hero.getShield() != null ? hero.getShield().getName().replace(" ", "_") : "None") + " ");

            out.write(hero.getInventory().size() + " ");
            for(Item item : hero.getInventory()) {
                out.write(item.getName().replace(" ", "_") + " ");
            }

            window.appendLog("Success! Game state saved cleanly to destination slot:\n" + targetFile.getName());
        } catch (IOException e) {
            window.appendLog("Critical System Failure: Unable to write data to " + SAVEPATH);
        }
        waitForAck();
    }

    private void waitForAck() {
        window.clearButtons();
        window.addButton("Continue", "ok");
        window.getButtonInput();
    }
}