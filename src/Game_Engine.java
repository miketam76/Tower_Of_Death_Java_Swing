import java.util.Scanner;
import java.io.*;

public class Game_Engine {

	private Player hero;
	private Battle_Engine battle;
	private GameWindow window;

	// FIX: Dynamically assigned at runtime based on where the JAR file lives
	private final String SAVEPATH;
	private String filename;

	// Version number
	private final double version = 0.10;

	// Constant variables for main menu
	private final int NEWGAME = 1;
	private final int LOADGAME = 2;
	private final int QUIT = 0;

	// Constant variables for game menu
	private final String UP_LEVEL = "1";
	private final String CURRENT_LEVEL = "2";
	private final String DISPLAY_PLAYER = "3";
	private final String CHANGE_NAME = "4";
	private final String HEAL_PLAYER = "5";
	private final String SAVE_GAME = "6";
	private final String GO_TO_MAIN = "7";

	// Constants for level
	private int CURRENTLEVEL = 1;
	private final int MAXLEVEL = 100;

	public Game_Engine(GameWindow window) {
		this.window = window;

		// Calculate the absolute path of the running JAR directory, then append the folder name
		this.SAVEPATH = locateJarDirectory() + "savegames" + File.separator;

		ensureSaveDirectoryExists();
	}

	// Safely extracts the true execution folder path regardless of how the OS executes the JAR
	private String locateJarDirectory() {
		try {
			String path = Game_Engine.class.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()
					.getPath();

			File jarFile = new File(path);
			// If running from a compiled JAR file, strip away the filename to get its parent directory
			if (path.endsWith(".jar")) {
				return jarFile.getParent() + File.separator;
			}
			// Fallback for running straight from class files inside an IDE development sandbox
			return jarFile.getPath() + File.separator;
		} catch (Exception e) {
			// Absolute last-resort fallback to standard relative paths if URI parsing errors out
			return "." + File.separator;
		}
	}

	private void ensureSaveDirectoryExists() {
		File folder = new File(SAVEPATH);
		if (!folder.exists()) {
			folder.mkdirs();
		}
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

			try {
				choice = Integer.parseInt(window.getButtonInput());
			} catch (NumberFormatException e) {
				choice = -1;
			}

			switch(choice)
			{
				case NEWGAME:
					hero = new Player();
					changeName();
					game_Menu();
					break;
				case LOADGAME:
					if(load_game_menu())
					{
						game_Menu();
					}
					break;
				case QUIT:
					window.clearLog();
					window.updateHeader("GAME OVER");
					window.appendLog("Exiting Tower Of Death. Thank you for playing!");
					window.clearButtons();

					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}

					System.exit(0);
					break;
			}
		}
		while(choice != QUIT);
	}

	public void game_Menu()
	{
		String choice;
		do
		{
			window.clearLog();
			window.updateHeader("Location: Exploration Hub | Current Level: " + CURRENTLEVEL);
			window.clearButtons();

			if(CURRENTLEVEL == 100)
			{
				window.addButton("Portal to Lvl 1", "1");
			}
			else
			{
				window.addButton("Go to Level " + (CURRENTLEVEL + 1), "1");
			}

			window.addButton("Enter Level " + CURRENTLEVEL, "2");
			window.addButton("Show Stats", "3");
			window.addButton("Change Name", "4");
			window.addButton("Heal Player", "5");
			window.addButton("Save Game", "6");
			window.addButton("Main Menu", "7");

			window.appendLog("--- Exploration Options ---");
			window.appendLog("Select an action from the action panel below.");

			choice = window.getButtonInput();

			switch(choice)
			{
				case UP_LEVEL:
					if(CURRENTLEVEL == MAXLEVEL)
					{
						window.clearLog();
						window.appendLog("You have reached the top level and see a swirling portal to go back to Level 1.");
						window.clearButtons();
						window.addButton("Enter Portal (Yes)", "y");
						window.addButton("Stay Here (No)", "n");

						if(window.getButtonInput().equalsIgnoreCase("y"))
						{
							window.clearLog();
							window.appendLog("You step through the portal back to Level 1...\n");
							CURRENTLEVEL = 1;
							waitForAck();
						}
					}
					else {
						CURRENTLEVEL++;
					}
					break;
				case CURRENT_LEVEL:
					window.clearLog();
					window.appendLog("Entering level " + CURRENTLEVEL + "...\n");
					battle = new Battle_Engine(window);
					hero = battle.battle_loader(hero, CURRENTLEVEL);
					break;
				case DISPLAY_PLAYER:
					window.clearLog();
					window.appendLog(hero.playerStats());
					waitForAck();
					break;
				case CHANGE_NAME:
					changeName();
					break;
				case HEAL_PLAYER:
					hero.healHP();
					window.clearLog();
					window.appendLog("Your wounds close completely. Character has been fully healed.\n");
					waitForAck();
					break;
				case SAVE_GAME:
					save_game_menu();
					break;
				case GO_TO_MAIN:
					window.clearLog();
					window.appendLog("Returning to main menu...\n");
					waitForAck();
					break;
			}
		}
		while(!choice.equals(GO_TO_MAIN));
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

		String newname = window.getButtonInput();
		hero.setName(newname);

		window.clearLog();
		window.appendLog("Your identity has been reset to: " + hero.getName());
		waitForAck();
	}

	public boolean load_game_menu() {
		window.clearLog();
		window.updateHeader("LOAD GAME PROFILE");
		window.appendLog("Select a storage data slot to recover your file:\n");

		String slot1Meta = getSlotMetadata("slot1.sav");
		String slot2Meta = getSlotMetadata("slot2.sav");
		String slot3Meta = getSlotMetadata("slot3.sav");

		window.appendLog("[Slot 1] " + slot1Meta);
		window.appendLog("[Slot 2] " + slot2Meta);
		window.appendLog("[Slot 3] " + slot3Meta);

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

		String slot1Meta = getSlotMetadata("slot1.sav");
		String slot2Meta = getSlotMetadata("slot2.sav");
		String slot3Meta = getSlotMetadata("slot3.sav");

		window.appendLog("[Slot 1] " + slot1Meta);
		window.appendLog("[Slot 2] " + slot2Meta);
		window.appendLog("[Slot 3] " + slot3Meta);

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

			if (window.getButtonInput().equals("cancel")) {
				window.clearLog();
				window.appendLog("Save action aborted safely.");
				waitForAck();
				return;
			}
		}

		executeSave(targetFile);
	}

	private String getSlotMetadata(String filename) {
		File f = new File(SAVEPATH + filename);
		if (!f.exists()) {
			return "- Empty Slot -";
		}

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
			CURRENTLEVEL = filereader.nextInt();

			hero = new Player(name, exp, lvl, hp, mp, str, agl, Int, sta, lck, atk, def);
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
			out.write(hero.getName());
			out.newLine();

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
			out.write(CURRENTLEVEL + " ");

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