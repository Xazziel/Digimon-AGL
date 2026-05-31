package digimon.digimons;

import java.util.ArrayList;
import java.util.List;

public final class DigimonList {
    private static final String BASE = "/Digimon/rookie/";

    private DigimonList() {}

    public static Digimon createAgumon() {
        return create("Agumon", 110, 14, 6, "Agumon.png",
                380, 320, true,
                250, 250, false,
                "PEPPER BREATH", "GUARDIA DIGITAL", "DIGISOUL", "ABSORBER DATOS");
    }

    public static Digimon createElizamon() {
        return create("Elizamon", 115, 13, 7, "Elizamon.png",
                390, 330, true,
                300, 300, false,
                "RAYO CARMESÍ", "ESCAMA DATA", "PULSO DIGITAL", "ABSORBER DATOS");
    }

    public static Digimon createGaomon() {
        return create("Gaomon", 100, 15, 5, "Gaomon.png",
                360, 320, false,
                245, 245, true,
                "DOUBLE BACKHAND", "GUARDIA VELOZ", "DIGISOUL", "ABSORBER DATOS");
    }

    public static Digimon createGaossmon() {
        return create("Gaossmon", 120, 13, 7, "Gaossmon.png",
                360, 310, true,
                260, 250, false,
                "BLUE BITE", "PIEL DRAGÓN", "CARGA DATA", "ABSORBER DATOS");
    }

    public static Digimon createGazimon() {
        return create("Gazimon", 95, 16, 4, "Gazimon.png",
                360, 310, true,
                255, 245, false,
                "GAZI CLAW", "REFLEJO DIGITAL", "DIGISOUL", "ABSORBER DATOS");
    }

    public static Digimon createGekkomon() {
        return create("Gekkomon", 125, 11, 8, "Gekkomon.png",
                420, 320, true,
                300, 270, false,
                "TONGUE SMASH", "CANTO DEFENSIVO", "ONDA DATA", "ABSORBER DATOS");
    }

    public static Digimon createJellymon() {
        return create("Jellymon", 105, 14, 6, "Jellymon.png",
                440, 330, true,
                330, 300, false,
                "BIBI THUNDER", "MANTO ACUÁTICO", "PULSO DATA", "ABSORBER DATOS");
    }

    public static Digimon createKeramon() {
        return create("Keramon", 110, 15, 5, "Keramon.png",
                350, 320, true,
                250, 250, false,
                "CRAZY GIGGLE", "BUG WALL", "AURA OSCURA", "DRENAR DATOS");
    }

    public static Digimon createLopmon() {
        return create("Lopmon", 100, 12, 7, "Lopmon.png",
                360, 310, true,
                255, 245, false,
                "PETIT TWISTER", "OREJA GUARDIANA", "CARGA DATA", "ABSORBER DATOS");
    }

    public static List<Digimon> createAllDigimon() {
        List<Digimon> digimons = new ArrayList<>();
        digimons.add(createAgumon());
        digimons.add(createElizamon());
        digimons.add(createGaomon());
        digimons.add(createGaossmon());
        digimons.add(createGazimon());
        digimons.add(createGekkomon());
        digimons.add(createJellymon());
        digimons.add(createKeramon());
        digimons.add(createLopmon());
        return digimons;
    }

    public static List<Digimon> createDefaultTeamDigimon() {
        List<Digimon> team = new ArrayList<>();
        team.add(createAgumon());
        team.add(createElizamon());
        team.add(createGaomon());
        team.add(createGaossmon());
        team.add(createGazimon());
        team.add(createJellymon());
        return team;
    }

    private static Digimon create(String name, int hp, int attack, int defense, String imageName,
                                  int teamWidth, int teamHeight, boolean flipTeam,
                                  int enemyWidth, int enemyHeight, boolean flipEnemy,
                                  String move1, String move2, String move3, String move4) {
        String path = BASE + imageName;
        return new Digimon(name, hp, attack, defense,
                path, path,
                teamWidth, teamHeight, flipTeam,
                enemyWidth, enemyHeight, flipEnemy,
                new String[]{move1, move2, move3, move4});
    }
}
