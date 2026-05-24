import java.util.*;

public class EnemyJackal {

	private String name = "Guardian Jackal";
	private int lvl;
	private int hp;
	private int atk;
	private int def;

	Random rnd = new Random();

	public EnemyJackal(int floorLvl)
	{
		this.lvl = floorLvl;

		// The Jackal inherits the +20 Tower difficulty spike
		int effectiveLvl = floorLvl + 20;

		int baseHp = 150;
		int baseAtk = 15;
		int baseDef = 5;

		this.hp = (baseHp * effectiveLvl) + (baseHp * effectiveLvl * effectiveLvl / 30);
		this.atk = (baseAtk * effectiveLvl) + (baseAtk * effectiveLvl * effectiveLvl / 20);
		this.def = (baseDef * effectiveLvl) + (baseDef * effectiveLvl * effectiveLvl / 20);
	}

	public int getHP() { return this.hp; }
	public void setHP(int hp) { this.hp = hp; }
	public int getDEF() { return this.def; }
	public String getName() { return this.name; }

	public int attack(int heroDEF)
	{
		int atkRnd = rnd.nextInt(20) + 1;
		int rawDmg = this.atk + atkRnd;

		int netDmg = (rawDmg * rawDmg) / (rawDmg + heroDEF);
		if (netDmg < 1) netDmg = 1;

		return netDmg;
	}
}