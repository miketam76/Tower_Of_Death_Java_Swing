import java.util.ArrayList;
import java.util.Random;

public class Battle_Engine {

	// Constants for battle menu
	private final int ATTACK = 1;
	private final int DEFEND = 2;
	private final int HEAL = 3;
	private final int RUN_AWAY = 4;

	// Arraylists for spawned enemies
	private ArrayList<Enemy_REG> EnemySpawn = new ArrayList<Enemy_REG>();
	private ArrayList<Enemy_Jackal> Jackal = new ArrayList<Enemy_Jackal>();

	private final int MAXLEVEL = 100; // Maximum level of game
	private int lastEnemyRecord = 0; // Used to record how many regular enemies spawned

	// CRITICAL: Scope Fixes
	private GameWindow window;
	private int currentBattleFloor; // Track the floor level locally for the UI header

	// Constructor accepts the GameWindow reference from Game_Engine
	public Battle_Engine(GameWindow window) {
		this.window = window;
	}

	// Load battle when player enters a level
	public Player battle_loader(Player hero, int lvl)
	{
		// Store the current floor level locally so all methods in this class can see it
		this.currentBattleFloor = lvl;

		// Spawn Jackal based on level and hero stats
		if(lvl == 10)
		{
			spawnJackal(1, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 20)
		{
			spawnJackal(1, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 30)
		{
			spawnJackal(2, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 40)
		{
			spawnJackal(2, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 50)
		{
			spawnJackal(3, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 60)
		{
			spawnJackal(3, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 70)
		{
			spawnJackal(4, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 80)
		{
			spawnJackal(4, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 90)
		{
			spawnJackal(5, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == MAXLEVEL)
		{
			spawnJackal(6, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		// For all other levels, load regular enemies
		else
		{
			spawnEnemyREG(lvl);
		}

		hero = battle_menu(hero, lvl);

		return hero;
	}

	// Display Battle menu interface
	private Player battle_menu(Player hero, int lvl)
	{
		// EXP gained for # enemies defeated
		long enemyEXP_Gain = 1000 * lvl * hero.getLVL() * lastEnemyRecord;
		long jackalEXP_Gain = 10000 * lvl * hero.getLVL();

		String choice = "";

		// Determine which enemy the player will face
		if(!EnemySpawn.isEmpty()) // For regular enemies
		{
			do
			{
				// FIX: Update UI Header with current stats and the local floor variable
				window.updateHeader("COMBAT | Hero: " + hero.getName() + " | HP: " + hero.getHP() + " | Dungeon Floor: " + currentBattleFloor);

				// Setup static menu action buttons
				window.clearButtons();
				window.addButton("Attack", "1");
				window.addButton("Defend", "2");
				window.addButton("Heal", "3");
				window.addButton("Run Away", "4");

				choice = window.getButtonInput();

				switch(choice)
				{
					case "1": // ATTACK
						if(lastEnemyRecord > 0) // Track how many remaining
						{
							// Update enemy HP based on hero attack calculation
							EnemySpawn.get(0).setHP(hero.attack(EnemySpawn.get(0).getHP()));
							window.appendLog(hero.attackMSG(0)); // Show player hit in the log

							if(EnemySpawn.get(0).getHP() > 0) {
								hero.setHP(EnemySpawn.get(0).attack(hero.getHP()));
								window.appendLog("The Thug swings back!");
							}
							else
							{
								defeatEnemy();
								window.appendLog("Enemy Defeated! Remaining enemies: " + lastEnemyRecord);
							}
						}
						break;
					case "2": // DEFEND
						window.appendLog("Player defends, taking no damage.");
						break;
					case "3": // HEAL
						hero.healHP();
						window.appendLog("Player casts a mending spell and heals to max HP.");
						break;
					case "4": // RUN AWAY
						EnemySpawn.clear();
						window.appendLog("You managed to slip away into the dark corridors!");
						break;
				}
			}
			while(!choice.equals("4") && hero.getHP() > 0 && lastEnemyRecord > 0);

			// Post-battle evaluation
			window.clearLog();
			if (lastEnemyRecord == 0)
			{
				window.appendLog("Victory! All enemies in this sector have been cleared.");
				hero = calculateEXP(hero, enemyEXP_Gain);
			}
			else if(hero.getHP() <= 0) {
				window.appendLog("Your vision fades to black... Player has perished!");
			}

			waitForAck();
			return hero;
		}
		else if(!Jackal.isEmpty()) // For Boss fights
		{
			do
			{
				window.updateHeader("BOSS BATTLE | Hero: " + hero.getName() + " | HP: " + hero.getHP() + " | Dungeon Floor: " + currentBattleFloor);

				window.clearButtons();
				window.addButton("Attack", "1");
				window.addButton("Defend", "2");
				window.addButton("Heal", "3");
				window.addButton("Run Away", "4");

				choice = window.getButtonInput();

				switch(choice)
				{
					case "1": // ATTACK
						if(!Jackal.isEmpty())
						{
							Jackal.get(0).setHP(hero.attack(Jackal.get(0).getHP()));
							window.appendLog(hero.attackMSG(0));

							if(Jackal.get(0).getHP() > 0) {
								hero.setHP(Jackal.get(0).attack(hero.getHP()));
								window.appendLog("The Jackal slashes viciously with its claws!");
							}
							else
							{
								defeatJackal();
							}
						}
						break;
					case "2": // DEFEND
						window.appendLog("Player braces for impact. Shield raised.");
						break;
					case "3": // HEAL
						hero.healHP();
						window.appendLog("Player flashes with radiant healing energy.");
						break;
					case "4": // RUN AWAY
						Jackal.clear();
						window.appendLog("You fled from the Guardian Boss!");
						break;
				}
			}
			while(!choice.equals("4") && hero.getHP() > 0 && !Jackal.isEmpty());

			window.clearLog();
			if(Jackal.isEmpty() && !choice.equals("4"))
			{
				window.appendLog("The Guardian Jackal shatters into dust! You are victorious.");
				hero = calculateEXP(hero, jackalEXP_Gain);
			}
			else if(hero.getHP() <= 0) {
				window.appendLog("The Tower claims another soul. Player has perished!");
			}

			waitForAck();
			return hero;
		}
		return hero;
	}

	// Levels up player based on the amount of EXP gained per battle
	private Player calculateEXP(Player hero, long exp)
	{
		window.appendLog(hero.getName() + " has gained " + exp + " EXP!");
		hero.setEXP(exp);

		// Level 2 checks
		if(hero.getEXP() >= 1000 && hero.getEXP() < 10000)
		{
			triggerLevelUp(hero);
		}
		else if(hero.getEXP() > 10000 * hero.getLVL())
		{
			triggerLevelUp(hero);
		}
		else if(hero.getEXP() > 20000 * hero.getLVL())
		{
			triggerLevelUp(hero);
		}
		else if(hero.getEXP() > 30000 * hero.getLVL())
		{
			triggerLevelUp(hero);
		}

		return(hero);
	}

	// Helper to mutate stats cleanly during a level up string event
	private void triggerLevelUp(Player hero) {
		hero.setLVL();
		hero.levelHP();
		hero.setMP();
		hero.setSTR();
		hero.setAGL();
		hero.setINT();
		hero.setSTA();
		hero.setLCK();
		hero.setATK();
		hero.setDEF();
		window.appendLog("LEVEL UP! You have advanced to Level " + hero.getLVL() + "!");
	}

	// Creates a random number of enemies to defeat based on the lvl it assigns to it
	private void spawnEnemyREG(int lvl)
	{
		Random rnd = new Random();
		int spawn = rnd.nextInt(5) + 1;
		for(int i = 0; i < spawn; i++)
		{
			EnemySpawn.add(new Enemy_REG(lvl));
			lastEnemyRecord++;
		}
		window.appendLog(lastEnemyRecord + " rogue Thugs step out of the shadows to attack!");
	}

	private void spawnJackal(int lvl, int hp, int str, int agl, int sta, int atk, int def)
	{
		Jackal.add(new Enemy_Jackal(lvl, hp, str, agl, sta, atk, def));
		window.appendLog("WARNING: The floor environment warps. The Guardian Jackal has manifested!");
	}

	private void defeatJackal()
	{
		if(!Jackal.isEmpty()) {
			Jackal.remove(0);
		}
	}

	// Determines if all regular enemies have been defeated
	private boolean defeatEnemy()
	{
		if(lastEnemyRecord > 0)
		{
			EnemySpawn.remove(0);
			lastEnemyRecord--;
			return false;
		}
		else
		{
			return true;
		}
	}

	// Helper method to hold the static window frame state until user clicks continue
	private void waitForAck() {
		window.addButton("Continue", "ok");
		window.getButtonInput();
	}

	public ArrayList <Enemy_REG> getEnemy_Reg() { return EnemySpawn; }
	public ArrayList <Enemy_Jackal> getJackal() { return Jackal; }
}