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
        // Massive, tanky boss suited for the end of the Woods (Level 20)
        this.hp = 5000;
        this.atk = 800;
        this.def = 200;
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