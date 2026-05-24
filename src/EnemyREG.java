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

	public EnemyREG(int lvl, String location)
	{
		this.lvl = lvl;

		int baseHp = 11;
		int baseStr = 3;
		int baseAgl = 2;
		int baseSta = 4;
		int baseDef = 1;

		int type = rnd.nextInt(4);

		if (location.equals("WOODS")) {
			switch(type) {
				case 0: this.name = "Dire Wolf";  baseHp = 9; baseStr = 4; baseAgl = 5; baseDef = 1; break;
				case 1: this.name = "Giant Spider"; baseHp = 8; baseStr = 3; baseAgl = 4; baseDef = 1; break;
				case 2: this.name = "Cursed Treant"; baseHp = 16; baseStr = 2; baseAgl = 1; baseDef = 3; break;
				case 3: this.name = "Wraith"; baseHp = 7; baseStr = 5; baseAgl = 4; baseDef = 0; break;
			}
		} else {
			switch(type) {
				case 0: this.name = "Thug"; baseHp = 11; baseStr = 3; baseAgl = 2; baseDef = 1; break;
				case 1: this.name = "Skeleton"; baseHp = 9; baseStr = 4; baseAgl = 3; baseDef = 1; break;
				case 2: this.name = "Cultist"; baseHp = 7; baseStr = 5; baseAgl = 2; baseDef = 0; break;
				case 3: this.name = "Gargoyle"; baseHp = 12; baseStr = 3; baseAgl = 1; baseDef = 3; break;
			}
		}

		// Quadratic scaling keeps enemies dangerous in the late game
		this.hp = (baseHp * lvl) + (baseHp * lvl * lvl / 6);
		this.str = (baseStr * lvl) + (baseStr * lvl * lvl / 10);
		this.agl = (baseAgl * lvl) + (baseAgl * lvl * lvl / 10);
		this.sta = (baseSta * lvl) + (baseSta * lvl * lvl / 10);
		this.atk = this.str;
		this.def = (baseDef * lvl) + (baseDef * lvl * lvl / 10);
	}

	public int getHP() { return this.hp; }
	public void setHP(int hp) { this.hp = hp; }
	public int getDEF() { return this.def; }
	public String getName() { return this.name; }

	public int attack(int heroDEF)
	{
		int atkRnd = rnd.nextInt(15) + 1;
		int rawDmg = this.atk + atkRnd;
		int netDmg = (rawDmg * rawDmg) / (rawDmg + heroDEF);
		if (netDmg < 1) netDmg = 1;
		return netDmg;
	}
}