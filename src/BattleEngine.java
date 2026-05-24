import java.util.ArrayList;
import java.util.Random;

public class BattleEngine {

	private ArrayList<EnemyREG> EnemySpawn = new ArrayList<EnemyREG>();
	private ArrayList<EnemyJackal> Jackal = new ArrayList<EnemyJackal>();
	private ArrayList<EnemyGateKeeper> GateKeeper = new ArrayList<EnemyGateKeeper>();

	private final int MAXLEVEL = 100;
	private int lastEnemyRecord = 0;

	private GameWindow window;
	private int currentBattleFloor;
	private String location;

	public BattleEngine(GameWindow window) {
		this.window = window;
	}

	// UPDATE: Added location parameter so the engine knows what to spawn
	public Player battle_loader(Player hero, int lvl, String location)
	{
		this.currentBattleFloor = lvl;
		this.location = location;

		if (location.equals("TOWER")) {
			// Tower Bosses every 10 levels
			if(lvl % 10 == 0) {
				spawnJackal(lvl / 10, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
			} else {
				spawnEnemyREG(lvl, location);
			}
		}
		else if (location.equals("WOODS")) {
			// Woods Final Boss at Level 20
			if(lvl == 20) {
				spawnGateKeeper(lvl);
			} else {
				spawnEnemyREG(lvl, location);
			}
		}

		hero = battle_menu(hero, lvl);
		return hero;
	}

	private Player battle_menu(Player hero, int lvl)
	{
		long enemyEXP_Gain = 150L * lvl * lastEnemyRecord;
		long bossEXP_Gain = 1500L * lvl;

		int enemyGold_Gain = 45 * lvl * lastEnemyRecord;
		int bossGold_Gain = 500 * lvl;

		String choice = "";

		// 1. REGULAR ENEMIES
		if(!EnemySpawn.isEmpty())
		{
			do
			{
				window.updateHeader("COMBAT | Hero: " + hero.getName() + " | HP: " + hero.getHP() + " | Floor: " + currentBattleFloor);

				window.clearButtons();
				window.addButton("Attack", "1");
				window.addButton("Defend", "2");
				window.addButton("Heal", "3");
				window.addButton("Run Away", "4");

				choice = window.getButtonInput();

				switch(choice)
				{
					case "1":
						if(lastEnemyRecord > 0)
						{
							EnemySpawn.get(0).setHP(hero.attack(EnemySpawn.get(0).getHP()));
							window.appendLog(hero.attackMSG(0));

							if(EnemySpawn.get(0).getHP() > 0) {
								hero.setHP(EnemySpawn.get(0).attack(hero.getHP()));
							} else {
								defeatEnemy();
								window.appendLog("Enemy Defeated! Remaining enemies: " + lastEnemyRecord);
							}
						}
						break;
					case "2":
						window.appendLog("Player defends, taking no damage.");
						break;
					case "3":
						hero.healHP();
						window.appendLog("Player casts a mending spell and heals to max HP.");
						break;
					case "4":
						EnemySpawn.clear();
						window.appendLog("You managed to slip away into the dark corridors!");
						break;
				}
			} while(!choice.equals("4") && hero.getHP() > 0 && lastEnemyRecord > 0);

			window.clearLog();
			if (lastEnemyRecord == 0) {
				window.appendLog("Victory! All enemies in this sector have been cleared.");
				window.appendLog("Loot Recovered: " + enemyGold_Gain + " Gold!");
				hero.addGold(enemyGold_Gain);
				hero = calculateEXP(hero, enemyEXP_Gain);
			} else if(hero.getHP() <= 0) {
				window.appendLog("Your vision fades to black... Player has perished!");
			}
			waitForAck();
			return hero;
		}

		// 2. TOWER BOSS: JACKAL
		else if(!Jackal.isEmpty())
		{
			do {
				window.updateHeader("BOSS BATTLE | Hero: " + hero.getName() + " | HP: " + hero.getHP() + " | Floor: " + currentBattleFloor);
				window.clearButtons();
				window.addButton("Attack", "1"); window.addButton("Defend", "2");
				window.addButton("Heal", "3"); window.addButton("Run Away", "4");

				choice = window.getButtonInput();

				switch(choice) {
					case "1":
						Jackal.get(0).setHP(hero.attack(Jackal.get(0).getHP()));
						window.appendLog(hero.attackMSG(0));
						if(Jackal.get(0).getHP() > 0) hero.setHP(Jackal.get(0).attack(hero.getHP()));
						else Jackal.remove(0);
						break;
					case "2": window.appendLog("Player braces for impact. Shield raised."); break;
					case "3": hero.healHP(); window.appendLog("Player flashes with radiant healing energy."); break;
					case "4": Jackal.clear(); window.appendLog("You fled from the Guardian Boss!"); break;
				}
			} while(!choice.equals("4") && hero.getHP() > 0 && !Jackal.isEmpty());

			window.clearLog();
			if(Jackal.isEmpty() && !choice.equals("4")) {
				window.appendLog("The Guardian Jackal shatters into dust! You are victorious.");
				window.appendLog("Boss Loot Recovered: " + bossGold_Gain + " Gold!");
				hero.addGold(bossGold_Gain);
				hero = calculateEXP(hero, bossEXP_Gain);
				if (lvl == MAXLEVEL) triggerVictoryEnding(hero);
			} else if(hero.getHP() <= 0) window.appendLog("The Tower claims another soul. Player has perished!");

			waitForAck();
			return hero;
		}

		// 3. WOODS BOSS: GATE KEEPER
		else if(!GateKeeper.isEmpty())
		{
			do {
				window.updateHeader("BOSS BATTLE | Hero: " + hero.getName() + " | HP: " + hero.getHP() + " | Floor: " + currentBattleFloor);
				window.clearButtons();
				window.addButton("Attack", "1"); window.addButton("Defend", "2");
				window.addButton("Heal", "3"); window.addButton("Run Away", "4");

				choice = window.getButtonInput();

				switch(choice) {
					case "1":
						GateKeeper.get(0).setHP(hero.attack(GateKeeper.get(0).getHP()));
						window.appendLog(hero.attackMSG(0));
						if(GateKeeper.get(0).getHP() > 0) hero.setHP(GateKeeper.get(0).attack(hero.getHP()));
						else GateKeeper.remove(0);
						break;
					case "2": window.appendLog("Player braces for impact. Shield raised."); break;
					case "3": hero.healHP(); window.appendLog("Player flashes with radiant healing energy."); break;
					case "4": GateKeeper.clear(); window.appendLog("You fled in terror from the Gate Keeper!"); break;
				}
			} while(!choice.equals("4") && hero.getHP() > 0 && !GateKeeper.isEmpty());

			window.clearLog();
			if(GateKeeper.isEmpty() && !choice.equals("4")) {
				window.appendLog("The Gate Keeper collapses, dropping a heavy, glowing Obsidian Key!");
				window.appendLog("The path to the Tower of Death is now unlocked.");
				window.appendLog("Boss Loot Recovered: " + bossGold_Gain + " Gold!");

				// CRITICAL: Grant the player the progression key!
				hero.setTowerKey(true);
				hero.addGold(bossGold_Gain);
				hero = calculateEXP(hero, bossEXP_Gain);
			} else if(hero.getHP() <= 0) window.appendLog("The woods claim another soul. Player has perished!");

			waitForAck();
			return hero;
		}

		return hero;
	}

	private Player calculateEXP(Player hero, long exp) {
		window.appendLog(hero.getName() + " has gained " + exp + " EXP!");
		hero.setEXP(exp);
		long nextLevelThreshold = (hero.getLVL() * (hero.getLVL() + 1) / 2) * 1000L;
		while (hero.getEXP() >= nextLevelThreshold && hero.getLVL() < MAXLEVEL) {
			hero.setLVL(); hero.levelHP(); hero.setMP(); hero.setSTR();
			hero.setAGL(); hero.setINT(); hero.setSTA(); hero.setLCK();
			hero.setATK(); hero.setDEF();
			window.appendLog("LEVEL UP! Advanced to Level " + hero.getLVL() + "!");
			nextLevelThreshold = (hero.getLVL() * (hero.getLVL() + 1) / 2) * 1000L;
		}
		return hero;
	}

	private void triggerVictoryEnding(Player hero) {
		window.clearLog();
		window.updateHeader("= THE TOWER COLLAPSES =");
		window.appendLog("With a deafening roar, the Level 100 Guardian crumbles into brilliant white ash.");
		window.appendLog("The ancient, corrupt seal holding the Tower of Death together shatters instantly.");
		window.appendLog("\nThe obsidian foundations begin to violently tremble beneath your feet...");
		waitForEndingAck("Next");

		window.clearLog();
		window.updateHeader("= THE ESCAPE =");
		window.appendLog("You sprint down the decaying, spiraling stone staircases as masonry rains down.");
		window.appendLog("Corridors collapse into an endless abyss right behind you, but your instincts carry you forward.");
		window.appendLog("\nBursting through the heavy iron entrance doors, you leap out into the open air...");
		waitForEndingAck("Next");

		window.clearLog();
		window.updateHeader("= EPILOGUE =");
		window.appendLog("A profound silence falls over the valley. The Tower of Death is now nothing but dust.");
		window.appendLog("The morning sun breaks through the mist, illuminating a vast, beautiful green expanse.");
		window.appendLog("The crisp, fresh wilderness air instantly clears your mind of the dungeon's claustrophobic horrors.");
		window.appendLog("\nYou have survived the impossible trial. Your name will be sung for generations.");
		waitForEndingAck("View Final Records");

		window.clearLog();
		window.updateHeader("=== CONQUEROR'S HALL OF FAME ===");
		window.appendLog("      TOWER OF DEATH - VICTORY CHAMPION\n");
		window.appendLog("      Legendary Name: " + hero.getName());
		window.appendLog("      Final Level:    " + hero.getLVL());
		window.appendLog("      Total EXP:      " + hero.getEXP());
		window.appendLog("      Endgame ATK:    " + hero.getTotalATK());
		window.appendLog("      Endgame DEF:    " + hero.getTotalDEF());
		window.appendLog("\n      Thank you for conquering the tower! Designed in 2026.");

		window.clearButtons();
		window.addButton("Close Game", "exit");
		window.getButtonInput();

		System.exit(0);
	}

	private void waitForEndingAck(String buttonLabel) {
		window.clearButtons();
		window.addButton(buttonLabel, "next");
		window.getButtonInput();
	}

	private void spawnEnemyREG(int lvl, String location)
	{
		Random rnd = new Random();
		int spawn = rnd.nextInt(5) + 1;
		for(int i = 0; i < spawn; i++)
		{
			EnemySpawn.add(new EnemyREG(lvl, location));
			lastEnemyRecord++;
		}
		window.appendLog(lastEnemyRecord + " hostile monsters step out of the shadows to attack!");
	}

	private void spawnJackal(int lvl, int hp, int str, int agl, int sta, int atk, int def) {
		Jackal.add(new EnemyJackal(lvl, hp, str, agl, sta, atk, def));
		window.appendLog("WARNING: The floor environment warps. The Guardian Jackal has manifested!");
	}

	private void spawnGateKeeper(int lvl) {
		GateKeeper.add(new EnemyGateKeeper(lvl));
		window.appendLog("WARNING: The mist thickens. The Gate Keeper blocks your path!");
	}

	private boolean defeatEnemy()
	{
		if(lastEnemyRecord > 0) {
			EnemySpawn.remove(0);
			lastEnemyRecord--;
			return false;
		}
		return true;
	}

	private void waitForAck() {
		window.addButton("Continue", "ok");
		window.getButtonInput();
	}
}