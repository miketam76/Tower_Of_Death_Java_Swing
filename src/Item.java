public class Item {
    public static final int WEAPON = 1;
    public static final int ARMOR = 2;
    public static final int POTION = 3;

    private String name;
    private int type;
    private int power;
    private int cost;
    private int minLevel; // The level required for the shop to sell it

    public Item(String name, int type, int power, int cost, int minLevel) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.cost = cost;
        this.minLevel = minLevel;
    }

    public String getName() { return name; }
    public int getType() { return type; }
    public int getPower() { return power; }
    public int getCost() { return cost; }
    public int getMinLevel() { return minLevel; }

    // --- MASTER ITEM REGISTRY (10 TIERS) ---
    public static Item getByName(String searchName) {
        String cleanName = searchName.replace("_", " ");

        switch(cleanName) {
            case "Health Potion": return new Item("Health Potion", POTION, 0, 50, 1);

            // Tier 1 (Level 1)
            case "Broadsword":    return new Item("Broadsword", WEAPON, 20, 100, 1);
            case "Leather Armor": return new Item("Leather Armor", ARMOR, 20, 100, 1);

            // Tier 2 (Level 10)
            case "Longsword":     return new Item("Longsword", WEAPON, 80, 500, 10);
            case "Bronze Armor":  return new Item("Bronze Armor", ARMOR, 80, 500, 10);

            // Tier 3 (Level 20)
            case "Iron Sword":    return new Item("Iron Sword", WEAPON, 150, 1500, 20);
            case "Iron Armor":    return new Item("Iron Armor", ARMOR, 150, 1500, 20);

            // Tier 4 (Level 30)
            case "Dark Sword":    return new Item("Dark Sword", WEAPON, 300, 3000, 30);
            case "Dark Armor":    return new Item("Dark Armor", ARMOR, 300, 3000, 30);

            // Tier 5 (Level 40)
            case "Mythril Sword": return new Item("Mythril Sword", WEAPON, 500, 6000, 40);
            case "Mythril Armor": return new Item("Mythril Armor", ARMOR, 500, 6000, 40);

            // Tier 6 (Level 50)
            case "Flame Sword":   return new Item("Flame Sword", WEAPON, 800, 10000, 50);
            case "Flame Mail":    return new Item("Flame Mail", ARMOR, 800, 10000, 50);

            // Tier 7 (Level 60)
            case "Ice Brand":     return new Item("Ice Brand", WEAPON, 1200, 16000, 60);
            case "Ice Armor":     return new Item("Ice Armor", ARMOR, 1200, 16000, 60);

            // Tier 8 (Level 70)
            case "Defender":      return new Item("Defender", WEAPON, 1800, 25000, 70);
            case "Genji Armor":   return new Item("Genji Armor", ARMOR, 1800, 25000, 70);

            // Tier 9 (Level 80)
            case "Ragnarok":      return new Item("Ragnarok", WEAPON, 2600, 40000, 80);
            case "Crystal Mail":  return new Item("Crystal Mail", ARMOR, 2600, 40000, 80);

            // Tier 10 (Level 90+)
            case "Excalibur":     return new Item("Excalibur", WEAPON, 4000, 75000, 90);
            case "Adamant Armor": return new Item("Adamant Armor", ARMOR, 4000, 75000, 90);

            default:              return null;
        }
    }
}