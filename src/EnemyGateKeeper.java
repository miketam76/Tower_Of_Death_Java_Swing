public class EnemyGateKeeper {

    private String name = "The Gate Keeper";
    private int lvl;
    private int hp;
    private int atk;
    private int def;

    public EnemyGateKeeper(int lvl)
    {
        this.lvl = lvl;
        // A massive, tanky boss suited for the end of the Woods (Level 20)
        this.hp = 1800;
        this.atk = 180;
        this.def = 80;
    }

    public int getHP() { return this.hp; }
    public void setHP(int hp) { this.hp = hp; }
    public String getName() { return this.name; }

    public int attack(int heroHP)
    {
        System.out.println(this.name + " swings its massive lantern, draining your life force!");
        return (heroHP - this.atk);
    }
}
