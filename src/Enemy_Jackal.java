
public class Enemy_Jackal {
	
	private static int LVL1 = 3;
	private static int LVL2 = 5;
	private static int LVL3 = 10;
	
	private String name = "Jackal";
	private int lvl;
	private int hp;
	private int str;
	private int agl;
	private int sta;
	private int atk;
	private int def;
	
	// Default constructor
	public Enemy_Jackal() { }
	
	// Overloaded constructor - based on player's current stats x 3
	public Enemy_Jackal(int lvl, int hp, int str, int agl, int sta, int atk, int def)
	{
		this.lvl = lvl;
		this.hp = hp * lvl;
		this.str = str * lvl;
		this.agl = agl * lvl;
		this.sta = sta * lvl;
		this.atk = (str / 2) * lvl;
		this.def = def * lvl;
	}
	
	// get and set methods for Jackal hp
	public int getHP() { return this.hp; }
	public void setHP(int hp) { this.hp = hp; }	
	
	/* Jackal main attack
	Parameter: heroHP - current player hp value (int)
	Return: Remaining hp of hero (int)
	*/
	public int attack(int heroHP)
	{
		System.out.println(toString());
		return (heroHP - this.atk);
	}
	
	// Returns string value of attack
	public String toString()
	{
		return (this.name + " claw attack hit " + this.atk + " points!");
	}
}
