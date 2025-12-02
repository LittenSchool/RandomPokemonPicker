public class Pokemon {
    String name;
    double natDexNumber;
    int hp;
    int atk;
    int def;
    int sAtk;
    int sDef;
    int speed;

    public Pokemon(String name, double natDexNumber, int hp, int atk, int def, int sAtk, int sDef, int speed) {
        this.name = name;
        this.natDexNumber = natDexNumber;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.sAtk = sAtk;
        this.sDef = sDef;
        this.speed = speed;
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

    public String getCSV() {
        return name + "," + natDexNumber  + "," + hp  + "," + atk + "," + def + "," + sAtk + "," + sDef + "," + speed;
    }
}
