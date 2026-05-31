package digimon.digimons;

import java.util.Random;

public class Digimon {
    private String nombre;
    private Integer vida;
    private Integer vidaMaxima;
    private Integer ataque;
    private Integer ataqueBase;
    private Integer defensa;
    private Integer defensaBase;
    private Integer probabilidad;
    private String teamSpritePath;
    private String enemySpritePath;
    private int teamSpriteWidth;
    private int teamSpriteHeight;
    private int enemySpriteWidth;
    private int enemySpriteHeight;
    private boolean flipTeamSprite;
    private boolean flipEnemySprite;
    private String[] habilidades;
    private int nivel;
    private int experiencia;
    private int experienciaSiguienteNivel;

    public Digimon(Integer vida, Integer ataque, Integer defensa) {
        this("Digimon", vida, ataque, defensa, "/Digimon/rookie/square.png",
                new String[]{"ATAQUE", "BLOQUEO", "AUMENTAR", "ABSORBER DATOS"});
    }

    public Digimon(String nombre, Integer vidaMaxima, Integer ataque, Integer defensa, String spritePath, String[] habilidades) {
        this(nombre, vidaMaxima, ataque, defensa, spritePath, spritePath,
                420, 330, false, 260, 330, true, habilidades);
    }

    public Digimon(String nombre, Integer vidaMaxima, Integer ataque, Integer defensa,
                   String teamSpritePath, String enemySpritePath,
                   int teamSpriteWidth, int teamSpriteHeight, boolean flipTeamSprite,
                   int enemySpriteWidth, int enemySpriteHeight, boolean flipEnemySprite,
                   String[] habilidades) {
        this.nombre = nombre;
        this.vidaMaxima = Math.max(1, vidaMaxima);
        this.vida = this.vidaMaxima;
        this.ataque = Math.max(1, ataque);
        this.ataqueBase = this.ataque;
        this.defensa = Math.max(0, defensa);
        this.defensaBase = this.defensa;
        this.probabilidad = 0;
        this.teamSpritePath = teamSpritePath;
        this.enemySpritePath = enemySpritePath;
        this.teamSpriteWidth = Math.max(1, teamSpriteWidth);
        this.teamSpriteHeight = Math.max(1, teamSpriteHeight);
        this.enemySpriteWidth = Math.max(1, enemySpriteWidth);
        this.enemySpriteHeight = Math.max(1, enemySpriteHeight);
        this.flipTeamSprite = flipTeamSprite;
        this.flipEnemySprite = flipEnemySprite;
        this.nivel = 5;
        this.experiencia = 0;
        this.experienciaSiguienteNivel = 60;
        setHabilidades(habilidades);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getVida() {
        return vida;
    }

    public void setVida(Integer vida) {
        this.vida = Math.max(0, Math.min(vida, vidaMaxima));
    }

    public Integer getVidaMaxima() {
        return vidaMaxima;
    }

    public void setVidaMaxima(Integer vidaMaxima) {
        this.vidaMaxima = Math.max(1, vidaMaxima);
        if (vida > this.vidaMaxima) {
            vida = this.vidaMaxima;
        }
    }

    public Integer getAtaque() {
        return ataque;
    }

    public void setAtaque(Integer ataque) {
        this.ataque = Math.max(1, ataque);
    }

    public Integer getAtaqueBase() {
        return ataqueBase;
    }

    public void setAtaqueBase(Integer ataqueBase) {
        this.ataqueBase = Math.max(1, ataqueBase);
    }

    public Integer getDefensa() {
        return defensa;
    }

    public void setDefensa(Integer defensa) {
        this.defensa = Math.max(0, defensa);
    }

    public Integer getDefensaBase() {
        return defensaBase;
    }

    public void setDefensaBase(Integer defensaBase) {
        this.defensaBase = Math.max(0, defensaBase);
    }

    public Integer getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(Integer probabilidad) {
        this.probabilidad = probabilidad;
    }

    public String getSpriteGrande() {
        return teamSpritePath;
    }

    public void setSpriteGrande(String spritePath) {
        this.teamSpritePath = spritePath;
        this.enemySpritePath = spritePath;
    }

    public String getTeamSpritePath() {
        return teamSpritePath;
    }

    public String getEnemySpritePath() {
        return enemySpritePath;
    }

    public String getSpritePath(boolean enemy) {
        return enemy ? enemySpritePath : teamSpritePath;
    }

    public int getSpriteWidth(boolean enemy) {
        return enemy ? enemySpriteWidth : teamSpriteWidth;
    }

    public int getSpriteHeight(boolean enemy) {
        return enemy ? enemySpriteHeight : teamSpriteHeight;
    }

    public boolean shouldFlipSprite(boolean enemy) {
        return enemy ? flipEnemySprite : flipTeamSprite;
    }

    public void setTeamSpriteConfig(String path, int width, int height, boolean flip) {
        this.teamSpritePath = path;
        this.teamSpriteWidth = Math.max(1, width);
        this.teamSpriteHeight = Math.max(1, height);
        this.flipTeamSprite = flip;
    }

    public void setEnemySpriteConfig(String path, int width, int height, boolean flip) {
        this.enemySpritePath = path;
        this.enemySpriteWidth = Math.max(1, width);
        this.enemySpriteHeight = Math.max(1, height);
        this.flipEnemySprite = flip;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = Math.max(1, nivel);
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = Math.max(0, experiencia);
    }

    public int getExperienciaSiguienteNivel() {
        return experienciaSiguienteNivel;
    }

    public void setExperienciaSiguienteNivel(int experienciaSiguienteNivel) {
        this.experienciaSiguienteNivel = Math.max(1, experienciaSiguienteNivel);
    }

    public String[] getHabilidades() {
        return habilidades.clone();
    }

    public String getHabilidad(int index) {
        if (index < 0 || index >= habilidades.length) {
            return "HABILIDAD";
        }
        return habilidades[index];
    }

    public void setHabilidades(String[] habilidades) {
        this.habilidades = new String[]{"ATAQUE", "BLOQUEO", "AUMENTAR", "ABSORBER DATOS"};
        if (habilidades == null) {
            return;
        }
        for (int i = 0; i < this.habilidades.length && i < habilidades.length; i++) {
            this.habilidades[i] = habilidades[i];
        }
    }

    public boolean estaDebilitado() {
        return vida <= 0;
    }

    public void restaurar() {
        vida = vidaMaxima;
        ataque = ataqueBase;
        defensa = defensaBase;
        probabilidad = 0;
    }

    public String ganarExperiencia(int cantidad) {
        StringBuilder texto = new StringBuilder();
        experiencia += Math.max(0, cantidad);
        texto.append(nombre).append(" ganó ").append(cantidad).append(" EXP.\n");

        while (experiencia >= experienciaSiguienteNivel) {
            experiencia -= experienciaSiguienteNivel;
            experienciaSiguienteNivel += 25;
            nivel++;
            vidaMaxima += 8;
            ataqueBase += 2;
            if (nivel % 2 == 0) {
                defensaBase++;
            }
            restaurar();
            texto.append("¡").append(nombre).append(" subió al nivel ").append(nivel).append("!\n");
        }
        return texto.toString();
    }

    public String getEstadoCorto() {
        return "Lv" + nivel + " HP " + Math.max(0, vida) + "/" + vidaMaxima + " EXP " + experiencia + "/" + experienciaSiguienteNivel;
    }

    public void ataqueBasico(Digimon objetivo) {
        if (objetivo == null) {
            return;
        }
        int dano = Math.max(1, getAtaque() - objetivo.getDefensa());
        objetivo.setVida(objetivo.getVida() - dano);
    }

    public void defensaFerrea() {
        probabilidad = new Random().nextInt(1, 3);
    }

    public void drenaje() {
        probabilidad = new Random().nextInt(1, 11);
    }
}
