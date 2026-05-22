import java.util.Scanner;
import java.io.*;

public class GameEngine {

	private Player hero;
	private BattleEngine battle;
	private GameWindow window;
	private final String SAVEPATH;
	private final double version = 1.20;

	private final int NEWGAME = 1;
	private final int LOADGAME = 2;
	private final int QUIT = 0;

	private int CURRENTLEVEL = 1;
	private final int MAXLEVEL = 100;

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
					game_Menu();
					break;
				case LOADGAME:
					if(load_game_menu()) game_Menu();
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

	public void game_Menu()
	{
		String choice;
		do
		{
			window.clearLog();
			window.updateHeader("Location: Hub | Level: " + CURRENTLEVEL + " | Gold: " + hero.getGold());
			window.clearButtons();

			if(CURRENTLEVEL == 100) window.addButton("Portal to Lvl 1", "1");
			else window.addButton("Go to Level " + (CURRENTLEVEL + 1), "1");

			window.addButton("Enter Level " + CURRENTLEVEL, "2");
			window.addButton("Show Stats", "3");
			window.addButton("Inventory", "4");
			window.addButton("Visit Shop", "5");
			window.addButton("Heal Player", "6");
			window.addButton("Save Game", "7");
			window.addButton("Main Menu", "8");

			window.appendLog("--- Exploration Options ---");
			window.appendLog("Select an action from the action panel below.");

			choice = window.getButtonInput();

			switch(choice)
			{
				case "1":
					if(CURRENTLEVEL == MAXLEVEL) {
						window.clearLog();
						window.appendLog("You have reached the top level and see a swirling portal to go back to Level 1.");
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
					window.appendLog("Entering level " + CURRENTLEVEL + "...\n");
					battle = new BattleEngine(window);
					hero = battle.battle_loader(hero, CURRENTLEVEL);
					break;
				case "3":
					window.clearLog();
					window.appendLog(hero.playerStats());
					waitForAck();
					break;
				case "4":
					inventory_Menu();
					break;
				case "5":
					shop_Menu();
					break;
				case "6":
					hero.healHP();
					window.clearLog();
					window.appendLog("Your wounds close completely. Character has been fully healed.\n");
					waitForAck();
					break;
				case "7":
					save_game_menu();
					break;
				case "8":
					window.clearLog();
					window.appendLog("Returning to main menu...\n");
					waitForAck();
					break;
			}
		} while(!choice.equals("8"));
	}

	private void inventory_Menu() {
		String choice = "";
		do {
			window.clearLog();
			window.updateHeader("INVENTORY | Gold: " + hero.getGold());
			window.appendLog("Equipped Weapon: " + (hero.getWeapon() != null ? hero.getWeapon().getName() : "None"));
			window.appendLog("Equipped Armor:  " + (hero.getArmor() != null ? hero.getArmor().getName() : "None"));
			window.appendLog("\nSelect an item in your bag to equip or use it:");

			window.clearButtons();
			if (hero.getInventory().isEmpty()) {
				window.appendLog("\nYour bag is currently empty.");
			} else {
				for(int i = 0; i < hero.getInventory().size(); i++) {
					window.addButton(hero.getInventory().get(i).getName(), String.valueOf(i));
				}
			}
			window.addButton("Back to Hub", "back");

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
					hero.setWeapon(selected);
					window.appendLog("You equipped the " + selected.getName() + "!");
				} else if(selected.getType() == Item.ARMOR) {
					hero.setArmor(selected);
					window.appendLog("You put on the " + selected.getName() + "!");
				}
				waitForAck();
			}
		} while(!choice.equals("back"));
	}

	// --- NEW: DYNAMIC PROGRESSION SHOP SYSTEM ---
	private void shop_Menu() {
		String choice = "";
		do {
			window.clearLog();
			window.updateHeader("TOWER MERCHANT | Your Gold: " + hero.getGold());
			window.appendLog("A mysterious cloaked figure gestures to their wares.");
			window.appendLog("\"What are you buying, stranger?\"");

			window.clearButtons();
			window.addButton("Health Potion (50g)", "Health Potion");

			// Dynamically determine which tier brackets to show based on player level
			int pLvl = hero.getLVL();

			if (pLvl >= 90) { addShopTierToButtons(9); addShopTierToButtons(10); }
			else if (pLvl >= 80) { addShopTierToButtons(8); addShopTierToButtons(9); }
			else if (pLvl >= 70) { addShopTierToButtons(7); addShopTierToButtons(8); }
			else if (pLvl >= 60) { addShopTierToButtons(6); addShopTierToButtons(7); }
			else if (pLvl >= 50) { addShopTierToButtons(5); addShopTierToButtons(6); }
			else if (pLvl >= 40) { addShopTierToButtons(4); addShopTierToButtons(5); }
			else if (pLvl >= 30) { addShopTierToButtons(3); addShopTierToButtons(4); }
			else if (pLvl >= 20) { addShopTierToButtons(2); addShopTierToButtons(3); }
			else if (pLvl >= 10) { addShopTierToButtons(1); addShopTierToButtons(2); }
			else { addShopTierToButtons(1); } // Level 1-9 just gets Tier 1

			window.addButton("Leave Shop", "leave");

			choice = window.getButtonInput();

			if(!choice.equals("leave")) {
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

	// Helper to keep the shop UI clean
	private void addShopTierToButtons(int tier) {
		switch (tier) {
			case 1:
				window.addButton("Broadsword (100g)", "Broadsword");
				window.addButton("Leather Armor (100g)", "Leather Armor"); break;
			case 2:
				window.addButton("Longsword (500g)", "Longsword");
				window.addButton("Bronze Armor (500g)", "Bronze Armor"); break;
			case 3:
				window.addButton("Iron Sword (1500g)", "Iron Sword");
				window.addButton("Iron Armor (1500g)", "Iron Armor"); break;
			case 4:
				window.addButton("Dark Sword (3000g)", "Dark Sword");
				window.addButton("Dark Armor (3000g)", "Dark Armor"); break;
			case 5:
				window.addButton("Mythril Sword (6000g)", "Mythril Sword");
				window.addButton("Mythril Armor (6000g)", "Mythril Armor"); break;
			case 6:
				window.addButton("Flame Sword (10k g)", "Flame Sword");
				window.addButton("Flame Mail (10k g)", "Flame Mail"); break;
			case 7:
				window.addButton("Ice Brand (16k g)", "Ice Brand");
				window.addButton("Ice Armor (16k g)", "Ice Armor"); break;
			case 8:
				window.addButton("Defender (25k g)", "Defender");
				window.addButton("Genji Armor (25k g)", "Genji Armor"); break;
			case 9:
				window.addButton("Ragnarok (40k g)", "Ragnarok");
				window.addButton("Crystal Mail (40k g)", "Crystal Mail"); break;
			case 10:
				window.addButton("Excalibur (75k g)", "Excalibur");
				window.addButton("Adamant Armor (75k g)", "Adamant Armor"); break;
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
		window.appendLog("\nWith a heavy push, the massive doors groan open, swallowing you into the shadows.");
		waitForIntroAck("Step Inside");

		window.clearLog();
		window.updateHeader("= THE SAFE ZONE =");
		window.appendLog("The heavy doors slam shut behind you, sealing away the outside world.");
		window.appendLog("You find yourself in a dim, magically warded Exploration Hub at the base of the tower.");
		window.appendLog("From here, the only way forward is up.");
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

			hero = new Player(name, exp, lvl, hp, mp, str, agl, Int, sta, lck, atk, def, gold);

			String wepName = filereader.next();
			if (!wepName.equals("None")) hero.setWeapon(Item.getByName(wepName));
			String armName = filereader.next();
			if (!armName.equals("None")) hero.setArmor(Item.getByName(armName));

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

			out.write((hero.getWeapon() != null ? hero.getWeapon().getName().replace(" ", "_") : "None") + " ");
			out.write((hero.getArmor() != null ? hero.getArmor().getName().replace(" ", "_") : "None") + " ");

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