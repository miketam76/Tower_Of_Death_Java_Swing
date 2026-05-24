import java.util.*;

public class EnemyGateKeeper {

    private String name = "The Gate Keeper";
    private int lvl;
    private int hp;
    private int atk;
    private int def;

    Random rnd = new Random();

    public EnemyGateKeeper(int lvl)
    {
        this.lvl = lvl;

        // Base stats tailored to create a tanky, heavy-hitting roadblock
        int baseHp = 125;
        int baseAtk = 20;
        int baseDef = 5;

        // Dynamic scaling: At Level 20, this creates a ~5000 HP / 800 ATK / 200 DEF Boss
        // If you ever move him to Level 30, he automatically scales up to 9300 HP!
        this.hp = (baseHp * lvl) + (baseHp * lvl * lvl / 20);
        this.atk = (baseAtk * lvl) + (baseAtk * lvl * lvl / 20);
        this.def = (baseDef * lvl) + (baseDef * lvl * lvl / 20);
    }

    public int getHP() { return this.hp; }
    public void setHP(int hp) { this.hp = hp; }
    public int getDEF() { return this.def; }
    public String getName() { return this.name; }

    public int attack(int heroDEF)
    {
        int atkRnd = rnd.nextInt(20) + 1;
        int rawDmg = this.atk + atkRnd;

        // Classic RPG mitigation math applied to the Gate Keeper
        int netDmg = (rawDmg * rawDmg) / (rawDmg + heroDEF);
        if (netDmg < 1) netDmg = 1;

        return netDmg;
    }
}