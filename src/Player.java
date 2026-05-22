import java.util.*;

public class Player {
	private final int MIN_HP = 35;
	private final int MAX_HP = (MIN_HP * 4) * 99;
	private final int MAX_STATS = 9999;
	private final int MAXLEVEL = 100;
	private int MAX_HEAL_HP = MIN_HP;

	Random rnd = new Random();

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

	private int gold;
	private Item weapon;
	private Item armor;
	private Item helmet; // NEW
	private Item shield; // NEW
	private ArrayList<Item> inventory;

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

		this.gold = 0;
		this.weapon = null;
		this.armor = null;
		this.helmet = null;
		this.shield = null;
		this.inventory = new ArrayList<>();
	}

	public Player(String name, long exp, int lvl, int hp, int mp,
				  int str, int agl, int Int, int sta, int lck, int atk, int def, int gold)
	{
		this.name = name;
		this.exp = exp;
		this.lvl = lvl;
		this.hp = hp;
		this.mp = mp;
		this.str = str;
		this.agl = agl;
		this.Int = Int;
		this.sta = sta;
		this.lck = lck;
		this.atk = atk;
		this.def = def;

		this.gold = gold;
		this.inventory = new ArrayList<>();

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
	public void setEXP(long exp) { this.exp += exp; }
	public int getLVL() { return this.lvl; }

	public int getGold() { return gold; }
	public void addGold(int amount) { this.gold += amount; }
	public void subtractGold(int amount) { this.gold -= amount; }
	public ArrayList<Item> getInventory() { return inventory; }

	public Item getWeapon() { return weapon; }
	public void setWeapon(Item w) { this.weapon = w; }
	public Item getArmor() { return armor; }
	public void setArmor(Item a) { this.armor = a; }
	public Item getHelmet() { return helmet; }
	public void setHelmet(Item h) { this.helmet = h; }
	public Item getShield() { return shield; }
	public void setShield(Item s) { this.shield = s; }

	public int getTotalATK() { return this.atk + (weapon != null ? weapon.getPower() : 0); }

	// NEW: Calculates total defense from base stat + Armor + Helmet + Shield
	public int getTotalDEF() {
		int total = this.def;
		if (armor != null) total += armor.getPower();
		if (helmet != null) total += helmet.getPower();
		if (shield != null) total += shield.getPower();
		return total;
	}

	public void setLVL()
	{
		this.lvl = this.lvl + 1;
		if(this.lvl >= MAXLEVEL) this.lvl = MAXLEVEL;
	}

	public int getHP() { return this.hp; }
	public void setHP(int heroHP) { this.hp = heroHP; }
	public void healHP() { this.hp = this.MAX_HEAL_HP; }

	public void levelHP() {
		recalculateMaxHealHP();
		healHP();
	}

	public int getMP() { return this.mp; }
	public void setMP() { this.mp += 12; if(this.mp > MAX_STATS) this.mp = MAX_STATS; }
	public int getSTR() { return this.str; }
	public void setSTR() { this.str += 100; if(this.str > MAX_STATS) this.str = MAX_STATS; }
	public int getAGL() { return this.agl; }
	public void setAGL() { this.agl += 17; if(this.agl > MAX_STATS) this.agl = MAX_STATS; }
	public int getINT() { return this.Int; }
	public void setINT() { this.Int += 11; if(this.Int > MAX_STATS) this.Int = MAX_STATS; }
	public int getSTA() { return this.sta; }
	public void setSTA() { this.sta += 14; if(this.sta > MAX_STATS) this.sta = MAX_STATS; }
	public int getLCK() { return this.lck; }
	public void setLCK() { this.lck += 10; if(this.lck > MAX_STATS) this.lck = MAX_STATS; }

	public int getATK() { return this.atk; }
	public void setATK() { this.atk = this.str / 2; if(this.atk > MAX_STATS) this.atk = MAX_STATS; }
	public int getDEF() { return this.def; }
	public void setDEF() { this.def += 12; if(this.def > MAX_STATS) this.def = MAX_STATS; }

	public int attack(int enemyHP)
	{
		int atkRnd = rnd.nextInt(20) + 1;
		System.out.println(attackMSG(atkRnd));
		return (enemyHP - (getTotalATK() + atkRnd));
	}

	public String attackMSG(int atkRnd)
	{
		return (this.name + " hit " + (getTotalATK() + atkRnd) + " points!");
	}

	public String playerStats()
	{
		String wepStr = (weapon != null) ? weapon.getName() + " (+" + weapon.getPower() + ")" : "None";
		String armStr = (armor != null) ? armor.getName() + " (+" + armor.getPower() + ")" : "None";
		String helmStr = (helmet != null) ? helmet.getName() + " (+" + helmet.getPower() + ")" : "None";
		String shldStr = (shield != null) ? shield.getName() + " (+" + shield.getPower() + ")" : "None";

		return ("Name:\t" + this.name + "\n" +
				"Gold:\t" + this.gold + "g\n" +
				"EXP:\t" + this.exp + "\n" +
				"Level:\t" + this.lvl + "\n" +
				"HP:\t" + this.hp + " / " + this.MAX_HEAL_HP + "\n" +
				"MP:\t" + this.mp + "\n\n" +
				"-- EQUIPMENT --\n" +
				"Weapon:\t" + wepStr + "\n" +
				"Armor:\t" + armStr + "\n" +
				"Helmet:\t" + helmStr + "\n" +
				"Shield:\t" + shldStr + "\n\n" +
				"-- BASE STATS --\n" +
				"ATK:\t" + this.atk + " (Total: " + getTotalATK() + ")\n" +
				"DEF:\t" + this.def + " (Total: " + getTotalDEF() + ")\n" +
				"STR:\t" + this.str + "\n" +
				"AGL:\t" + this.agl + "\n");
	}
}