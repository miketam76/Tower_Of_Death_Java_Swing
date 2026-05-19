import java.util.*;

public class Enemy_REG {
	
	private String name = "Thug";
	private int lvl;
	private int hp = 11;
	private int str = 3;
	private int agl = 2;
	private int sta = 4;
	private int atk = str / 2;
	private int def = 1;
	
	Random rnd = new Random();
	
	// Default constructor - Boosts stats based on the level the player is at
	public Enemy_REG(int lvl)
	{
		this.lvl = lvl;
		this.hp = hp * lvl;
		this.str = str * lvl;
		this.agl = agl * lvl;
		this.sta = sta * lvl;
		this.atk = str * lvl;
		this.def = def * lvl; 
	}
	
	// get and set methods for Jackal hp
	public int getHP() { return this.hp; }
	public void setHP(int hp) { this.hp = hp; }	
		
	/* Reg enemy main attack
	Parameter: heroHP - current player hp value (int)
	Return: Remaining hp of hero (int)
	*/
	public int attack(int heroHP)
	{
		// Generate random number between 1 - 20
		int atkRnd = rnd.nextInt(15) + 1;
		System.out.println(toString(atkRnd));
		// Use random number in hit
		return (heroHP - (this.atk + atkRnd));
	}
		
	// Returns string value of attack
	public String toString(int atkRnd)
	{
		return (this.name + " punch hit " + (this.atk + atkRnd) + " points!");
	}

}