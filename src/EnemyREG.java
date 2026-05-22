import java.util.*;

public class EnemyREG {

	private String name;
	private int lvl;
	private int hp;
	private int str;
	private int agl;
	private int sta;
	private int atk;
	private int def;

	Random rnd = new Random();

	// Default constructor - Randomly generates a monster archetype and scales it
	public EnemyREG(int lvl)
	{
		this.lvl = lvl;

		// Baseline stats for standard "Thug"
		int baseHp = 11;
		int baseStr = 3;
		int baseAgl = 2;
		int baseSta = 4;
		int baseDef = 1;

		// Randomly select 1 of 6 enemy types
		int type = rnd.nextInt(6);

		switch(type) {
			case 0:
				this.name = "Thug";
				// Uses standard baseline stats
				break;
			case 1:
				this.name = "Goblin";
				// Agile but fragile
				baseHp = 8; baseStr = 2; baseAgl = 4; baseSta = 2; baseDef = 1;
				break;
			case 2:
				this.name = "Skeleton";
				// Hits harder, less health
				baseHp = 9; baseStr = 4; baseAgl = 3; baseSta = 2; baseDef = 1;
				break;
			case 3:
				this.name = "Slime";
				// Very tanky, low attack
				baseHp = 16; baseStr = 2; baseAgl = 1; baseSta = 5; baseDef = 2;
				break;
			case 4:
				this.name = "Cultist";
				// Glass cannon
				baseHp = 7; baseStr = 5; baseAgl = 2; baseSta = 2; baseDef = 0;
				break;
			case 5:
				this.name = "Gargoyle";
				// High defense brute
				baseHp = 12; baseStr = 3; baseAgl = 1; baseSta = 5; baseDef = 3;
				break;
		}

		// Scale the selected base stats by the dungeon level
		this.hp = baseHp * lvl;
		this.str = baseStr * lvl;
		this.agl = baseAgl * lvl;
		this.sta = baseSta * lvl;
		this.atk = this.str * lvl;
		this.def = baseDef * lvl;
	}

	public int getHP() { return this.hp; }
	public void setHP(int hp) { this.hp = hp; }
	public String getName() { return this.name; } // Added for dynamic logging

	public int attack(int heroHP)
	{
		int atkRnd = rnd.nextInt(15) + 1;
		System.out.println(toString(atkRnd));
		return (heroHP - (this.atk + atkRnd));
	}

	public String toString(int atkRnd)
	{
		// Changed "punch" to a generic "strike" so it makes sense for weapons/claws
		return (this.name + " strikes for " + (this.atk + atkRnd) + " damage!");
	}
}