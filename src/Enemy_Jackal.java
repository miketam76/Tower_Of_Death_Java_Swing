public class Enemy_Jackal {

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

	// Overloaded constructor - scales based on boss tier and base parameters
	public Enemy_Jackal(int lvl, int hp, int str, int agl, int sta, int atk, int def)
	{
		this.lvl = lvl;
		this.hp = hp * lvl;
		this.str = str * lvl;
		this.agl = agl * lvl;
		this.sta = sta * lvl;

		// FIX: Changed divisor from 2 to 12 to stop the boss from scaling into accidental one-shot kills
		this.atk = (str / 12) * lvl;

		this.def = def * lvl;
	}

	public int getHP() { return this.hp; }
	public void setHP(int hp) { this.hp = hp; }

	public int attack(int heroHP)
	{
		System.out.println(toString());
		return (heroHP - this.atk);
	}

	public String toString()
	{
		return (this.name + " claw attack hit " + this.atk + " points!");
	}
}