import java.util.Objects;

public class Pokemon {
    String name;
    double natDexNumber;
    int hp;
    int atk;
    int def;
    int sAtk;
    int sDef;
    int speed;
    String evoLine;
    String colour = "000000";
    int owner;

    public Pokemon(String name, double natDexNumber, int hp, int atk, int def, int sAtk, int sDef, int speed,String evoLine) {
        this.name = name;
        this.natDexNumber = natDexNumber;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.sAtk = sAtk;
        this.sDef = sDef;
        this.speed = speed;
        this.evoLine = evoLine;
    }

    public Pokemon(String name) {
        this.name = name;
        this.natDexNumber = Objects.requireNonNull(RandomNumberGUI.nameToPokemon(name)).getNatDexNumber();
        this.hp = Objects.requireNonNull(RandomNumberGUI.nameToPokemon(name)).getHp();
        this.atk = Objects.requireNonNull(RandomNumberGUI.nameToPokemon(name)).getAtk();
        this.def = Objects.requireNonNull(RandomNumberGUI.nameToPokemon(name)).getDef();
        this.sAtk = Objects.requireNonNull(RandomNumberGUI.nameToPokemon(name)).getsAtk();
        this.sDef = Objects.requireNonNull(RandomNumberGUI.nameToPokemon(name)).getsDef();
        this.speed = Objects.requireNonNull(RandomNumberGUI.nameToPokemon(name)).getSpeed();
        this.evoLine = Objects.requireNonNull(RandomNumberGUI.nameToPokemon(name)).getEvoLine();
    }

    public Pokemon(double natDexNumber) {
        this.natDexNumber = natDexNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public void setsAtk(int sAtk) {
        this.sAtk = sAtk;
    }

    public void setsDef(int sDef) {
        this.sDef = sDef;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setEvoLine(String evoLine) {
        this.evoLine = evoLine;
    }

    public String getEvoLine() {
        return evoLine;
    }

    public String getName() {
        return name;
    }

    public int getBST() {
        return atk+def+sAtk+sDef+hp+speed;
    }

    public String getCSV() {
        return name + "," + natDexNumber  + "," + hp  + "," + atk + "," + def + "," + sAtk + "," + sDef + "," + speed + "," + evoLine;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public double getNatDexNumber() {
        return natDexNumber;
    }

    public void setNatDexNumber(double natDexNumber) {
        this.natDexNumber = natDexNumber;
    }

    public int getHp() {
        return hp;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public int getsAtk() {
        return sAtk;
    }

    public int getsDef() {
        return sDef;
    }

    public int getSpeed() {
        return speed;
    }
}
