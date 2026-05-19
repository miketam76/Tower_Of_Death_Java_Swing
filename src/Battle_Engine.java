import java.util.ArrayList;
import java.util.Random;

public class Battle_Engine {

	private ArrayList<Enemy_REG> EnemySpawn = new ArrayList<Enemy_REG>();
	private ArrayList<Enemy_Jackal> Jackal = new ArrayList<Enemy_Jackal>();

	private final int MAXLEVEL = 100;
	private int lastEnemyRecord = 0;

	private GameWindow window;
	private int currentBattleFloor;

	public Battle_Engine(GameWindow window) {
		this.window = window;
	}

	public Player battle_loader(Player hero, int lvl)
	{
		this.currentBattleFloor = lvl;

		if(lvl == 10 || lvl == 20) {
			spawnJackal(1, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 30 || lvl == 40) {
			spawnJackal(2, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 50 || lvl == 60) {
			spawnJackal(3, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 70 || lvl == 80) {
			spawnJackal(4, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == 90) {
			spawnJackal(5, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else if(lvl == MAXLEVEL) {
			spawnJackal(6, hero.getHP(), hero.getSTR(), hero.getAGL(), hero.getSTA(), hero.getATK(), hero.getDEF());
		}
		else {
			spawnEnemyREG(lvl);
		}

		hero = battle_menu(hero, lvl);
		return hero;
	}

	private Player battle_menu(Player hero, int lvl)
	{
		// FIX: Stabilized base drops to prevent fast leveling inflation
		long enemyEXP_Gain = 150L * lvl * lastEnemyRecord;
		long jackalEXP_Gain = 1500L * lvl;

		String choice = "";

		if(!EnemySpawn.isEmpty())
		{
			do
			{
				window.updateHeader("COMBAT | Hero: " + hero.getName() + " | HP: " + hero.getHP() + " | Dungeon Floor: " + currentBattleFloor);

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
								window.appendLog("The Thug swings back!");
							}
							else
							{
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
			}
			while(!choice.equals("4") && hero.getHP() > 0 && lastEnemyRecord > 0);

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
		else if(!Jackal.isEmpty())
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
					case "1":
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
					case "2":
						window.appendLog("Player braces for impact. Shield raised.");
						break;
					case "3":
						hero.healHP();
						window.appendLog("Player flashes with radiant healing energy.");
						break;
					case "4":
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

				// FIX: Intercept victory state at Level 100 to stream ending cinematic crawler
				if (lvl == MAXLEVEL) {
					triggerVictoryEnding(hero);
				}
			}
			else if(hero.getHP() <= 0) {
				window.appendLog("The Tower claims another soul. Player has perished!");
			}

			waitForAck();
			return hero;
		}
		return hero;
	}

	// FIX: Replaced linear chain checks with a dynamic quadratic loop
	private Player calculateEXP(Player hero, long exp)
	{
		window.appendLog(hero.getName() + " has gained " + exp + " EXP!");
		hero.setEXP(exp);

		long nextLevelThreshold = (hero.getLVL() * (hero.getLVL() + 1) / 2) * 1000L;

		while (hero.getEXP() >= nextLevelThreshold && hero.getLVL() < MAXLEVEL)
		{
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

			window.appendLog("LEVEL UP! Advanced to Level " + hero.getLVL() + "!");

			nextLevelThreshold = (hero.getLVL() * (hero.getLVL() + 1) / 2) * 1000L;
		}

		return hero;
	}

	// --- FIX: MULTI-STAGE NARRATIVE ENDING SYSTEM ---
	private void triggerVictoryEnding(Player hero) {
		// Screen 1
		window.clearLog();
		window.updateHeader("= THE TOWER COLLAPSES =");
		window.appendLog("With a deafening roar, the Level 100 Guardian crumbles into brilliant white ash.");
		window.appendLog("The ancient, corrupt seal holding the Tower of Death together shatters instantly.");
		window.appendLog("\nThe obsidian foundations begin to violently tremble beneath your feet...");
		waitForEndingAck("Next");

		// Screen 2
		window.clearLog();
		window.updateHeader("= THE ESCAPE =");
		window.appendLog("You sprint down the decaying, spiraling stone staircases as masonry rains down.");
		window.appendLog("Corridors collapse into an endless abyss right behind you, but your instincts carry you forward.");
		window.appendLog("\nBursting through the heavy iron entrance doors, you leap out into the open air...");
		waitForEndingAck("Next");

		// Screen 3
		window.clearLog();
		window.updateHeader("= EPILOGUE =");
		window.appendLog("A profound silence falls over the valley. The Tower of Death is now nothing but dust.");
		window.appendLog("The morning sun breaks through the mist, illuminating a vast, beautiful green expanse.");
		window.appendLog("The crisp, fresh wilderness air instantly clears your mind of the dungeon's claustrophobic horrors.");
		window.appendLog("\nYou have survived the impossible trial. Your name will be sung for generations.");
		waitForEndingAck("View Final Records");

		// Screen 4: Stats Summary Screen
		window.clearLog();
		window.updateHeader("=== CONQUEROR'S HALL OF FAME ===");
		window.appendLog("      TOWER OF DEATH - VICTORY CHAMPION\n");
		window.appendLog("      Legendary Name: " + hero.getName());
		window.appendLog("      Final Level:    " + hero.getLVL());
		window.appendLog("      Total EXP:      " + hero.getEXP());
		window.appendLog("      Endgame ATK:    " + hero.getATK());
		window.appendLog("      Endgame DEF:    " + hero.getDEF());
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

	private void waitForAck() {
		window.addButton("Continue", "ok");
		window.getButtonInput();
	}

	public ArrayList <Enemy_REG> getEnemy_Reg() { return EnemySpawn; }
	public ArrayList <Enemy_Jackal> getJackal() { return Jackal; }
}