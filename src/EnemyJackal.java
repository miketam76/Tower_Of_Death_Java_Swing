import java.util.*;

public class EnemyJackal {

	private String name = "Guardian Jackal";
	private int lvl;
	private int hp;
	private int atk;
	private int def;

	Random rnd = new Random();

	// Boss scales quadratically based strictly on the floor level
	public EnemyJackal(int lvl)
	{
		this.lvl = lvl;

		int baseHp = 100;
		int baseAtk = 10;
		int baseDef = 5;

		this.hp = (baseHp * lvl) + (baseHp * lvl * lvl / 5);
		this.atk = (baseAtk * lvl) + (baseAtk * lvl * lvl / 15);
		this.def = (baseDef * lvl) + (baseDef * lvl * lvl / 10);
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