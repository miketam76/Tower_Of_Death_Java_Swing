import java.util.*;

public class Player {
	// Constants for HP and lvl
	private final int MIN_HP = 35; // Level 1 HP value
	private final int MAX_HP = (MIN_HP * 4) * 99; // Maximum HP value
	private final int MAX_STATS = 9999; // Maximum for all other stats
	private final int MAXLEVEL = 100; // Maximum player level

	private int MAX_HEAL_HP = MIN_HP; // Current max heal HP based on player level

	// Constants for gear bonuses
	private final int regular_gear = 10;
	private final int bronze_gear = 60;
	private final int iron_gear = 140;
	private final int diamond_gear = 350;
	private final int crystal_gear = 670;
	private final int excalibur_gear = 2000;

	Random rnd = new Random();

	// Basic stats
	private String name;
	private long exp;
	private int lvl;
	private int hp;
	private int mp;
	private int str;
	private int agl;
	private int Int;
	private int sta;
	private int lck;
	private int atk;
	private int def;

	// Default constructor - Used to create new player
	public Player()
	{
		this.name = "Hero";
		this.exp = 0;
		this.lvl = 1;
		this.hp = 35;
		this.mp = 2;
		this.str = 10;
		this.agl = 8;
		this.Int = 1;
		this.sta = 15;
		this.lck = 8;
		this.atk = str / 2;
		this.def = 2;
		this.MAX_HEAL_HP = MIN_HP;
	}

	// Overloaded - used to load character data from file
	public Player(String name, long exp, int lvl, int hp, int mp,
				  int str, int agl, int Int, int sta, int lck, int atk, int def)
	{
		this.name = name;
		this.exp = exp;
		this.lvl = lvl;
		this.hp = hp + gear_bonus();
		this.mp = mp + gear_bonus();
		this.str = str + gear_bonus();
		this.agl = agl + gear_bonus();
		this.Int = Int + gear_bonus();
		this.sta = sta + gear_bonus();
		this.lck = lck + gear_bonus();
		this.atk = atk;
		this.def = def + gear_bonus();

		// FIX: Calculate the correct maximum healing health pool limit dynamically upon file parsing
		recalculateMaxHealHP();
	}

	private void recalculateMaxHealHP() {
		this.MAX_HEAL_HP = (MIN_HP * 4) * this.lvl;
		if (this.MAX_HEAL_HP > MAX_HP) {
			this.MAX_HEAL_HP = MAX_HP;
		}
	}

	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }

	public long getEXP() { return this.exp; }
	public void setEXP(long exp) { this.exp = this.exp + exp; }

	public int getLVL() { return this.lvl; }
	public void setLVL()
	{
		this.lvl = this.lvl + 1;
		if(this.lvl >= MAXLEVEL)
			this.lvl = MAXLEVEL;
	}

	public int getHP() { return this.hp; }
	public void setHP(int heroHP) { this.hp = heroHP; }
	public void healHP() { this.hp = this.MAX_HEAL_HP; }

	// FIX: Streamlined level-up updates so boundary constraints evaluation applies uniformly
	public void levelHP()
	{
		recalculateMaxHealHP();
		healHP();
	}

	public int getMP() { return this.mp; }
	public void setMP()
	{
		this.mp = this.mp + 12;
		if(this.mp > MAX_STATS)
			this.mp = MAX_STATS;
	}

	public int getSTR() { return this.str; }
	public void setSTR()
	{
		this.str = this.str + 100;
		if(this.str > MAX_STATS)
			this.str = MAX_STATS;
	}

	public int getAGL() { return this.agl; }
	public void setAGL()
	{
		this.agl = this.agl + 17;
		if(this.agl > MAX_STATS)
			this.agl = MAX_STATS;
	}

	public int getINT() { return this.Int; }
	public void setINT()
	{
		this.Int = this.Int + 11;
		if(this.Int > MAX_STATS)
			this.Int = MAX_STATS;
	}

	public int getSTA() { return this.sta; }
	public void setSTA()
	{
		this.sta = this.sta + 14;
		if(this.sta > MAX_STATS)
			this.sta = MAX_STATS;
	}

	public int getLCK() { return this.lck; }
	public void setLCK()
	{
		this.lck = this.lck + 10;
		if(this.lck > MAX_STATS)
			this.lck = MAX_STATS;
	}

	public int getATK() { return this.atk; }
	public void setATK()
	{
		this.atk = this.str / 2;
		if(this.atk > MAX_STATS)
			this.atk = MAX_STATS;
	}

	public int getDEF() { return this.def; }
	public void setDEF()
	{
		this.def = this.def + 12;
		if(this.def > MAX_STATS)
			this.def = MAX_STATS;
	}

	public int attack(int enemyHP)
	{
		int atkRnd = rnd.nextInt(20) + 1;
		System.out.println(attackMSG(atkRnd));
		return (enemyHP - ((this.atk + atkRnd) + gear_bonus()));
	}

	public String attackMSG(int atkRnd)
	{
		return (this.name + " hit " + (this.atk + atkRnd + gear_bonus()) + " points!");
	}

	public String playerStats()
	{
		return ("Name:\t" + this.name + "\n" +
				"EXP:\t" + this.exp + "\n" +
				"Level:\t" + this.lvl + "\n" +
				"HP:\t" + this.hp + " / " + this.MAX_HEAL_HP + "\n" +
				"MP:\t" + this.mp + " + " + gear_bonus() + "\n" +
				"STR:\t" + this.str + " + " + gear_bonus() + "\n" +
				"AGL:\t" + this.agl + " + " + gear_bonus() + "\n" +
				"INT:\t" + this.Int + " + " + gear_bonus() + "\n" +
				"STA:\t" + this.sta + " + " + gear_bonus() + "\n" +
				"LCK:\t" + this.lck + " + " + gear_bonus() + "\n" +
				"ATK:\t" + this.atk + " + " + gear_bonus() + "\n" +
				"DEF:\t" + this.def + " + " + gear_bonus() + "\n\n");
	}

	public int gear_bonus()
	{
		if(lvl >= 1 && lvl < 10)
			return regular_gear;
		else if(lvl >= 10 && lvl < 30)
			return bronze_gear;
		else if(lvl >= 30 && lvl < 50)
			return iron_gear;
		else if (lvl >= 50 && lvl < 70)
			return diamond_gear;
		else if (lvl >= 70 && lvl < 90)
			return crystal_gear;
		else if (lvl >= 90)
			return excalibur_gear;
		return 0;
	}
}