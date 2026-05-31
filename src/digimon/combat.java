package digimon;

import digimon.digimons.Digimon;
import digimon.digimons.DigimonList;
import digimon.digimons.DigimonTeam;
import entity.Player;
import main.ResourceLoader;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class combat extends JPanel {

    private static final int BATTLE_WIDTH = 1280;
    private static final int BATTLE_HEIGHT = 720;
    private static final int SAVE_EFFECT_MIN_INTERVAL_MS = 250;
    private static final int DEFAULT_STAT_EFFECT_DURATION_MS = 900;
    private static final int STAT_EFFECT_TIMER_DELAY = 30;
    private static final int STAT_EFFECT_SCROLL_SPEED = 6;
    private static final String SAVE_FILE = "save_digimon.properties";

    private static final int FX_ATTACK = 0;
    private static final int FX_DEFENSE = 1;
    private static final int FX_ACCURACY = 2;
    private static final int FX_EVASION = 3;
    private static final int FX_SPEED = 4;
    private static final int FX_SP_ATTACK = 5;
    private static final int FX_SP_DEFENSE = 6;
    private static final int FX_MULTI = 7;

    private final DigimonTeam equipo = DigimonTeam.createDefaultTeam();
    private Digimon enemy = DigimonList.createKeramon();
    private final Random random = new Random();

    private int turno = 1;
    private boolean digisoulActivado = false;
    private int duracionDigisoul = 0;
    private boolean auraOscuraActivada = false;
    private int duracionAuraOscura = 0;
    private boolean ataqueFallado = false;
    private boolean bloqueo = false;
    private boolean digimonAturdido = false;
    private long lastStatSoundTime = 0;

    private Image backgroundImage;
    private EffectSpriteLabel enemySpriteLabel;
    private EffectSpriteLabel activeDigimonLabel;
    private JLabel enemyNameLabel;
    private JLabel enemyHpLabel;
    private JLabel activeNameLabel;
    private JLabel activeHpLabel;
    private JTextArea txtArea;
    private JTextArea teamArea;
    private JButton btnHabilidad1;
    private JButton btnHabilidad2;
    private JButton btnHabilidad3;
    private JButton btnHabilidad4;

    public combat() {
        setPreferredSize(new Dimension(BATTLE_WIDTH, BATTLE_HEIGHT));
        setSize(BATTLE_WIDTH, BATTLE_HEIGHT);
        setLayout(null);
        setBackground(Color.BLACK);
        setFocusable(false);

        crearInterfaz();
        resetBattleState();
        reiniciarEnemigo();
        actualizarInterfazCombate();
        escribirAyudaEquipo();
    }

    private void crearInterfaz() {
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Font labelFont = new Font("Arial", Font.BOLD, 25);
        Font textFont = new Font("Monospaced", Font.PLAIN, 15);

        backgroundImage = loadImage("/Ui/Fondo.png");

        enemyNameLabel = crearLabel((enemy.getNombre() + ":").toUpperCase(), labelFont, Color.WHITE, SwingConstants.LEFT);
        enemyNameLabel.setBounds(40, 24, 150, 34);
        add(enemyNameLabel);

        enemyHpLabel = crearLabel("100/100", labelFont, new Color(51, 255, 51), SwingConstants.LEFT);
        enemyHpLabel.setBounds(185, 24, 145, 34);
        add(enemyHpLabel);

        teamArea = new JTextArea();
        teamArea.setEditable(false);
        teamArea.setOpaque(true);
        teamArea.setBackground(new Color(0, 0, 0, 175));
        teamArea.setForeground(Color.WHITE);
        teamArea.setFont(new Font("Monospaced", Font.BOLD, 13));
        teamArea.setBorder(BorderFactory.createLineBorder(new Color(0, 116, 217), 2));
        teamArea.setMargin(new java.awt.Insets(8, 8, 8, 8));
        teamArea.setFocusable(false);
        teamArea.setBounds(280, 24, 520, 150);
        add(teamArea);

        activeDigimonLabel = new EffectSpriteLabel();
        activeDigimonLabel.setIcon(scaledIcon(equipo.getActiveDigimon(), false));
        activeDigimonLabel.setHorizontalAlignment(SwingConstants.CENTER);
        activeDigimonLabel.setVerticalAlignment(SwingConstants.CENTER);
        activeDigimonLabel.setOpaque(false);
        activeDigimonLabel.setBounds(50, 128, 520, 360);
        add(activeDigimonLabel);

        enemySpriteLabel = new EffectSpriteLabel();
        enemySpriteLabel.setIcon(scaledIcon(enemy, true));
        enemySpriteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        enemySpriteLabel.setVerticalAlignment(SwingConstants.CENTER);
        enemySpriteLabel.setOpaque(false);
        enemySpriteLabel.setBounds(850, 60, 380, 390);
        add(enemySpriteLabel);

        txtArea = new JTextArea();
        txtArea.setEditable(false);
        txtArea.setBackground(new Color(42, 42, 42));
        txtArea.setForeground(Color.WHITE);
        txtArea.setFont(textFont);
        txtArea.setRows(5);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setBorder(BorderFactory.createTitledBorder(""));
        txtArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
        txtArea.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(txtArea);
        scrollPane.setBounds(32, 500, 720, 188);
        add(scrollPane);

        activeNameLabel = crearLabel("AGUMON:", labelFont, Color.WHITE, SwingConstants.LEFT);
        activeNameLabel.setBounds(800, 462, 225, 40);
        add(activeNameLabel);

        activeHpLabel = crearLabel("100/100", labelFont, new Color(51, 255, 51), SwingConstants.LEFT);
        activeHpLabel.setBounds(1030, 462, 180, 40);
        add(activeHpLabel);

        btnHabilidad1 = crearBoton("LLAMA BEBÉ", buttonFont);
        btnHabilidad1.setBounds(800, 512, 210, 76);
        btnHabilidad1.addActionListener(evt -> usarAtaqueBasico());
        add(btnHabilidad1);

        btnHabilidad2 = crearBoton("GUARDIA DIGITAL", buttonFont);
        btnHabilidad2.setBounds(1030, 512, 210, 76);
        btnHabilidad2.addActionListener(evt -> usarDefensa());
        add(btnHabilidad2);

        btnHabilidad3 = crearBoton("DIGISOUL", buttonFont);
        btnHabilidad3.setBounds(800, 610, 210, 76);
        btnHabilidad3.addActionListener(evt -> usarAumento());
        add(btnHabilidad3);

        btnHabilidad4 = crearBoton("ABSORBER DATOS", buttonFont);
        btnHabilidad4.setBounds(1030, 610, 210, 76);
        btnHabilidad4.addActionListener(evt -> usarDrenaje());
        add(btnHabilidad4);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, BATTLE_WIDTH, BATTLE_HEIGHT, this);
        }
    }

    private Image loadImage(String path) {
        java.net.URL resource = ResourceLoader.getUrl(path);

        if (resource == null) {
            return null;
        }

        return new ImageIcon(resource).getImage();
    }

    private JLabel crearLabel(String text, Font font, Color color, int alignment) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setHorizontalAlignment(alignment);
        return label;
    }

    private JButton crearBoton(String text, Font font) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 116, 217));
        button.setForeground(Color.WHITE);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 86, 163), 2));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 204, 0));
                button.setForeground(new Color(42, 42, 42));
                button.setBorder(BorderFactory.createLineBorder(new Color(212, 161, 0), 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 116, 217));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 86, 163), 2));
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 116, 217));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 86, 163), 2));
            }
        });

        return button;
    }

    private ImageIcon scaledIcon(String path, int width, int height) {
        java.net.URL resource = ResourceLoader.getUrl(path);

        if (resource == null) {
            return new ImageIcon();
        }

        ImageIcon icon = new ImageIcon(resource);
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private ImageIcon scaledIcon(Digimon digimon, boolean enemyRole) {
        if (digimon == null) {
            return new ImageIcon();
        }

        java.net.URL resource = ResourceLoader.getUrl(digimon.getSpritePath(enemyRole));

        if (resource == null) {
            System.err.println("No se encontró el sprite: " + digimon.getSpritePath(enemyRole));
            return new ImageIcon();
        }

        int width = digimon.getSpriteWidth(enemyRole);
        int height = digimon.getSpriteHeight(enemyRole);
        boolean flip = digimon.shouldFlipSprite(enemyRole);

        try {
            BufferedImage original = ImageIO.read(resource);

            if (original == null) {
                System.err.println("No se pudo leer la imagen: " + digimon.getSpritePath(enemyRole));
                return new ImageIcon();
            }

            BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = finalImage.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (flip) {
                g2.translate(width, 0);
                g2.scale(-1, 1);
            }

            g2.drawImage(original, 0, 0, width, height, null);
            g2.dispose();

            return new ImageIcon(finalImage);
        } catch (IOException e) {
            System.err.println("Error al cargar sprite: " + digimon.getSpritePath(enemyRole));
            e.printStackTrace();
            return new ImageIcon();
        }
    }

    private void reproducirStatUpEffect(boolean enemyRole, int effectIndex) {
        if (effectIndex < 0 || effectIndex > 7) {
            return;
        }

        BufferedImage effectImage = cargarStatEffectImage(effectIndex);

        if (effectImage == null) {
            return;
        }

        EffectSpriteLabel label = enemyRole ? enemySpriteLabel : activeDigimonLabel;
        int durationMs = reproducirStatUpWavAndGetDuration();

        if (label != null) {
            label.playStatEffect(effectImage, durationMs);
        }
    }

    private BufferedImage cargarStatEffectImage(int effectIndex) {
        String path = "/Digimon/stadisticShaders/" + effectIndex + ".png";

        try {
            java.net.URL resource = ResourceLoader.getUrl(path);

            if (resource == null) {
                System.err.println("No se encontró efecto de stat up: " + path);
                return null;
            }

            return ImageIO.read(resource);
        } catch (IOException e) {
            System.err.println("No se pudo cargar efecto de stat up: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private int reproducirStatUpWavAndGetDuration() {
        long now = System.currentTimeMillis();

        if (now - lastStatSoundTime < SAVE_EFFECT_MIN_INTERVAL_MS) {
            return DEFAULT_STAT_EFFECT_DURATION_MS;
        }

        lastStatSoundTime = now;

        try {
            java.net.URL resource = ResourceLoader.getUrl("/Digimon/sounds/stat_up.wav");

            if (resource == null) {
                System.err.println("No se encontró sonido: /Digimon/sounds/stat_up.wav");
                return DEFAULT_STAT_EFFECT_DURATION_MS;
            }

            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(resource));

            int durationMs = (int) (clip.getMicrosecondLength() / 1000);

            clip.addLineListener(event -> {
                if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            clip.start();

            return Math.max(durationMs, DEFAULT_STAT_EFFECT_DURATION_MS);
        } catch (Exception e) {
            System.err.println("No se pudo reproducir stat_up.wav");
            e.printStackTrace();
            return DEFAULT_STAT_EFFECT_DURATION_MS;
        }
    }

    public boolean enemigoDerrotado() {
        return enemy.getVida() <= 0;
    }

    public void prepararNuevoEncuentro(String mensaje) {
        resetBattleState();
        reiniciarEnemigo();
        habilitarBotones();
        actualizarInterfazCombate();
        txtArea.append("\n" + mensaje + "\n");
    }

    public void prepararEncuentroAleatorio() {
        prepararNuevoEncuentro("⚠ Un Digimon oscuro apareció en la zona digital.");
    }

    private void usarAtaqueBasico() {
        Digimon digimon = obtenerDigimonActivo();

        if (digimon == null) {
            return;
        }

        iniciarTurno();
        txtArea.append("═════ " + digimon.getNombre().toUpperCase() + " ═════ \n");
        sumarAtaqueDigimon(digimon, digisoulActivado);

        if (!ejecutarEstadoAturdido(digimon)) {
            ataqueFallado = fallarAtaque();

            if (!ataqueFallado) {
                digimon.ataqueBasico(enemy);
                int dano = Math.max(1, digimon.getAtaque() - enemy.getDefensa());
                txtArea.append("¡" + digimon.getNombre() + " usó " + digimon.getHabilidad(0) + "!\n");
                txtArea.append("¡" + enemy.getNombre() + " recibió " + dano + "⚔️ de daño!\n");
            } else {
                txtArea.append("¡" + digimon.getNombre() + " falló el ataque!\n");
            }
        }

        finalizarTurnoJugador();
    }

    private void usarDefensa() {
        Digimon digimon = obtenerDigimonActivo();

        if (digimon == null) {
            return;
        }

        iniciarTurno();
        txtArea.append("═════ " + digimon.getNombre().toUpperCase() + " ═════ \n");
        sumarAtaqueDigimon(digimon, digisoulActivado);

        if (!ejecutarEstadoAturdido(digimon)) {
            digimon.defensaFerrea();
            txtArea.append("¡" + digimon.getNombre() + " usó " + digimon.getHabilidad(1) + "!\n");

            if (digimon.getProbabilidad() == 1) {
                txtArea.append("¡La defensa funcionó! Bloqueará el próximo ataque.\n");
                bloqueo = true;
                reproducirStatUpEffect(false, FX_DEFENSE);
            } else {
                txtArea.append("¡La defensa falló!\n");
            }
        }

        finalizarTurnoJugador();
    }

    private void usarAumento() {
        Digimon digimon = obtenerDigimonActivo();

        if (digimon == null) {
            return;
        }

        iniciarTurno();
        txtArea.append("═════ " + digimon.getNombre().toUpperCase() + " ═════ \n");
        sumarAtaqueDigimon(digimon, digisoulActivado);

        if (!ejecutarEstadoAturdido(digimon)) {
            ataqueFallado = fallarAtaque();

            if (!ataqueFallado) {
                digisoulActivado = true;
                txtArea.append("¡" + digimon.getNombre() + " usó " + digimon.getHabilidad(2) + "!\n");
                txtArea.append("Su ataque aumentará durante 3 turnos.\n");
                sumarAtaqueDigimon(digimon, digisoulActivado);
            } else {
                txtArea.append("¡" + digimon.getNombre() + " falló la carga digital!\n");
            }
        }

        finalizarTurnoJugador();
    }

    private void usarDrenaje() {
        Digimon digimon = obtenerDigimonActivo();

        if (digimon == null) {
            return;
        }

        iniciarTurno();
        txtArea.append("═════ " + digimon.getNombre().toUpperCase() + " ═════ \n");
        sumarAtaqueDigimon(digimon, digisoulActivado);

        if (!ejecutarEstadoAturdido(digimon)) {
            ataqueFallado = fallarAtaque();

            if (!ataqueFallado) {
                digimon.drenaje();
                txtArea.append("¡" + digimon.getNombre() + " usó " + digimon.getHabilidad(3) + "!\n");
                ejecutarDrenaje(digimon);
            } else {
                txtArea.append("¡" + digimon.getNombre() + " falló el ataque!\n");
            }
        }

        finalizarTurnoJugador();
    }

    private void ejecutarDrenaje(Digimon digimon) {
        if (digimon.getProbabilidad() >= 7) {
            int cura = digimon.getAtaque() / 2;
            digimon.setVida(digimon.getVida() + cura);
            txtArea.append("Recuperó " + cura + "❤️.\n");
        } else if (digimon.getProbabilidad() >= 4) {
            txtArea.append("El drenaje no logró absorber datos.\n");
        } else if (digimon.getProbabilidad() >= 2) {
            int cura = digimon.getAtaque();
            digimon.setVida(digimon.getVida() + cura);
            txtArea.append("Recuperó " + cura + "❤️.\n");
        } else {
            int cura = digimon.getAtaque();
            digimon.setVida(digimon.getVida() + cura);
            enemy.setVida(enemy.getVida() - 10);
            txtArea.append("Recuperó " + cura + "❤️ y causó 10⚔️ de daño adicional.\n");
        }
    }

    private void finalizarTurnoJugador() {
        if (!comprobarVidaEnemigo()) {
            actualizarInterfazCombate();
            return;
        }

        ataqueEnemigo();
        txtArea.append("\n");
        actualizarInterfazCombate();
    }

    private boolean ejecutarEstadoAturdido(Digimon digimon) {
        if (!digimonAturdido) {
            return false;
        }

        txtArea.append("¡" + digimon.getNombre() + " está aturdido y perdió el turno!\n");
        digimonAturdido = false;
        return true;
    }

    private Digimon obtenerDigimonActivo() {
        Digimon digimon = equipo.getActiveDigimon();

        if (digimon == null || digimon.estaDebilitado()) {
            comprobarVidaDigimon();
            return null;
        }

        if (enemy.getVida() <= 0) {
            txtArea.append("\nEl combate ya terminó. Presiona C para volver al mapa o iniciar otro encuentro.\n");
            return null;
        }

        return digimon;
    }

    private void resetBattleState() {
        turno = 1;
        digisoulActivado = false;
        duracionDigisoul = 0;
        auraOscuraActivada = false;
        duracionAuraOscura = 0;
        ataqueFallado = false;
        bloqueo = false;
        digimonAturdido = false;
    }

    private void limpiarEfectosDelDigimonActivo() {
        digisoulActivado = false;
        duracionDigisoul = 0;
        bloqueo = false;
        digimonAturdido = false;
    }

    private void reiniciarEnemigo() {
        enemy = DigimonList.createKeramon();
        enemyNameLabel.setText((enemy.getNombre() + ":").toUpperCase());
        enemySpriteLabel.setIcon(scaledIcon(enemy, true));
    }

    public void cambiarDigimon(int slot) {
        int index = slot - 1;
        Digimon actual = equipo.getActiveDigimon();

        if (index == equipo.getActiveIndex() && actual != null) {
            txtArea.append("\n" + actual.getNombre() + " ya está en combate.\n");
            actualizarInterfazCombate();
            return;
        }

        if (equipo.switchTo(index)) {
            limpiarEfectosDelDigimonActivo();
            Digimon nuevo = equipo.getActiveDigimon();
            txtArea.append("\nCambiaste a " + nuevo.getNombre() + ".\n");
            actualizarInterfazCombate();
        } else if (index >= 0 && index < equipo.size()) {
            Digimon digimon = equipo.getDigimons().get(index);
            txtArea.append("\nNo puedes cambiar a " + digimon.getNombre() + " porque está fuera de combate.\n");
        }
    }

    private void ataqueEnemigo() {
        Digimon digimon = equipo.getActiveDigimon();

        if (digimon == null || enemy.getVida() <= 0) {
            return;
        }

        txtArea.append("\n═════ " + enemy.getNombre().toUpperCase() + " ═════ \n");
        enemy.setProbabilidad(random.nextInt(1, 11));

        if (!bloqueo) {
            if (enemy.getProbabilidad() >= 7) {
                ataqueFallado = fallarAtaque();

                if (!ataqueFallado) {
                    int dano = Math.max(1, enemy.getAtaque() - digimon.getDefensa());
                    txtArea.append("¡" + enemy.getNombre() + " usó PUÑETAZO!\n");
                    txtArea.append("¡" + digimon.getNombre() + " recibió " + dano + "⚔️ de daño!\n");
                    digimon.setVida(digimon.getVida() - dano);
                } else {
                    txtArea.append("¡" + enemy.getNombre() + " falló el ataque!\n");
                }
            } else if (enemy.getProbabilidad() >= 4) {
                int dano = Math.max(1, enemy.getAtaque() - digimon.getDefensa());
                txtArea.append("¡" + enemy.getNombre() + " usó PUÑETAZO VERDADERO!\n");
                txtArea.append("¡" + digimon.getNombre() + " recibió " + dano + "⚔️ de daño!\n");
                digimon.setVida(digimon.getVida() - dano);
            } else if (enemy.getProbabilidad() == 3) {
                ataqueFallado = fallarAtaque();

                if (!ataqueFallado) {
                    txtArea.append("¡" + enemy.getNombre() + " usó HIPNOSIS! " + digimon.getNombre() + " queda aturdido.\n");
                    digimonAturdido = true;
                } else {
                    txtArea.append("¡" + enemy.getNombre() + " falló el ataque!\n");
                }
            } else if (enemy.getProbabilidad() == 2) {
                txtArea.append("¡" + enemy.getNombre() + " usó MAGIA CURATIVA!\n");
                enemy.setVida(Math.min(enemy.getVidaMaxima(), enemy.getVida() + enemy.getAtaque()));
            } else {
                ataqueFallado = fallarAtaque();

                if (!ataqueFallado) {
                    txtArea.append("¡" + enemy.getNombre() + " usó AURA OSCURA!\n");
                    auraOscuraActivada = true;
                } else {
                    txtArea.append("¡" + enemy.getNombre() + " falló el ataque!\n");
                }
            }
        } else {
            txtArea.append("¡" + enemy.getNombre() + " falló el ataque por bloqueo de " + digimon.getNombre() + "! 🛡️\n");
        }

        sumarAtaqueEnemigo(auraOscuraActivada);
        comprobarVidaDigimon();
        bloqueo = false;
    }

    private boolean comprobarVidaDigimon() {
        Digimon digimon = equipo.getActiveDigimon();

        if (digimon == null) {
            inhabilitarBotones();
            return false;
        }

        activeHpLabel.setText(Math.max(0, digimon.getVida()) + "/" + digimon.getVidaMaxima());

        if (digimon.getVida() <= 0) {
            txtArea.append("\n☠️ " + digimon.getNombre().toUpperCase() + " FUERA DE COMBATE ☠️\n");
            limpiarEfectosDelDigimonActivo();

            if (equipo.switchToNextAvailable()) {
                Digimon nuevo = equipo.getActiveDigimon();
                txtArea.append("Sale " + nuevo.getNombre() + " al combate.\n");
                actualizarInterfazCombate();
                return true;
            }

            txtArea.append("\n☠️ TODO EL EQUIPO ESTÁ FUERA DE COMBATE ☠️\n");
            txtArea.append("Presiona F9 para cargar partida o reinicia el combate.\n");
            inhabilitarBotones();
            return false;
        }

        return true;
    }

    private boolean comprobarVidaEnemigo() {
        enemyHpLabel.setText(Math.max(0, enemy.getVida()) + "/" + enemy.getVidaMaxima());

        if (enemy.getVida() <= 0) {
            Digimon ganador = equipo.getActiveDigimon();
            txtArea.append("\n🔥 " + enemy.getNombre().toUpperCase() + " FUERA DE COMBATE 🔥\n");

            if (ganador != null) {
                txtArea.append(ganador.ganarExperiencia(45));
            }

            txtArea.append("Combate terminado. Presiona C para volver al mapa; al volver a entrar habrá otro encuentro.\n");
            inhabilitarBotones();
            return false;
        }

        return true;
    }

    private void iniciarTurno() {
        txtArea.append("\n==================== TURNO " + turno + " ====================\n\n");
        turno++;
    }

    private void inhabilitarBotones() {
        Digimon digimon = equipo.getActiveDigimon();

        if (digimon != null) {
            activeHpLabel.setText(Math.max(0, digimon.getVida()) + "/" + digimon.getVidaMaxima());
        }

        btnHabilidad1.setEnabled(false);
        btnHabilidad2.setEnabled(false);
        btnHabilidad3.setEnabled(false);
        btnHabilidad4.setEnabled(false);
    }

    private void habilitarBotones() {
        btnHabilidad1.setEnabled(true);
        btnHabilidad2.setEnabled(true);
        btnHabilidad3.setEnabled(true);
        btnHabilidad4.setEnabled(true);
    }

    private void sumarAtaqueDigimon(Digimon digimon, boolean activo) {
        if (duracionDigisoul < 3 && activo) {
            digimon.setAtaque(digimon.getAtaque() + 3);
            duracionDigisoul++;
            btnHabilidad3.setEnabled(false);
            txtArea.append("EFECTO " + digimon.getHabilidad(2) + ": Ataque +3 (" + digimon.getAtaque() + ") 🔺\n");
            reproducirStatUpEffect(false, FX_ATTACK);
        } else {
            digimon.setAtaque(digimon.getAtaqueBase());
            digisoulActivado = false;
            duracionDigisoul = 0;
            btnHabilidad3.setEnabled(true);
        }
    }

    private void sumarAtaqueEnemigo(boolean activo) {
        if (duracionAuraOscura <= 2 && activo) {
            enemy.setAtaque(enemy.getAtaque() + 3);
            duracionAuraOscura++;
            txtArea.append("EFECTO AURA OSCURA: Ataque +3 (" + enemy.getAtaque() + ") 🔺\n");
            reproducirStatUpEffect(true, FX_ATTACK);
        } else {
            enemy.setAtaque(enemy.getAtaqueBase());
            auraOscuraActivada = false;
            duracionAuraOscura = 0;
        }
    }

    private boolean fallarAtaque() {
        return random.nextInt(1, 11) <= 1;
    }

    public void reiniciarJuego() {
        equipo.restoreAll();
        reiniciarEnemigo();
        resetBattleState();
        txtArea.setText("");
        habilitarBotones();
        actualizarInterfazCombate();
        escribirAyudaEquipo();
    }

    private void actualizarInterfazCombate() {
        Digimon digimon = equipo.getActiveDigimon();

        if (digimon == null) {
            return;
        }

        activeNameLabel.setText((digimon.getNombre() + " Lv" + digimon.getNivel() + ":").toUpperCase());
        activeHpLabel.setText(Math.max(0, digimon.getVida()) + "/" + digimon.getVidaMaxima());
        activeDigimonLabel.setIcon(scaledIcon(digimon, false));

        enemyNameLabel.setText((enemy.getNombre() + ":").toUpperCase());
        enemySpriteLabel.setIcon(scaledIcon(enemy, true));
        enemyHpLabel.setText(Math.max(0, enemy.getVida()) + "/" + enemy.getVidaMaxima());

        String[] habilidades = digimon.getHabilidades();

        if (habilidades.length > 0) {
            btnHabilidad1.setText(habilidades[0]);
        }

        if (habilidades.length > 1) {
            btnHabilidad2.setText(habilidades[1]);
        }

        if (habilidades.length > 2) {
            btnHabilidad3.setText(habilidades[2]);
        }

        if (habilidades.length > 3) {
            btnHabilidad4.setText(habilidades[3]);
        }

        teamArea.setText(equipo.getTeamStatusMultiline());
    }

    private void escribirAyudaEquipo() {
        txtArea.append("Equipo Digimon listo. Presiona 1-6 para cambiar de Digimon.\n");
        txtArea.append("F5 guarda partida. F9 carga partida.\n");
        txtArea.append("Equipo: " + equipo.getTeamStatusText() + "\n\n");
    }

    public boolean guardarPartida(Player player) {
        Properties props = new Properties();

        props.setProperty("player.worldX", String.valueOf(player.worldX));
        props.setProperty("player.worldY", String.valueOf(player.worldY));
        props.setProperty("team.activeIndex", String.valueOf(equipo.getActiveIndex()));
        props.setProperty("enemy.vida", String.valueOf(enemy.getVida()));

        for (int i = 0; i < equipo.size(); i++) {
            Digimon digimon = equipo.getDigimons().get(i);
            String key = "digimon." + i + ".";

            props.setProperty(key + "vida", String.valueOf(digimon.getVida()));
            props.setProperty(key + "vidaMaxima", String.valueOf(digimon.getVidaMaxima()));
            props.setProperty(key + "ataqueBase", String.valueOf(digimon.getAtaqueBase()));
            props.setProperty(key + "defensaBase", String.valueOf(digimon.getDefensaBase()));
            props.setProperty(key + "nivel", String.valueOf(digimon.getNivel()));
            props.setProperty(key + "experiencia", String.valueOf(digimon.getExperiencia()));
            props.setProperty(key + "experienciaSiguiente", String.valueOf(digimon.getExperienciaSiguienteNivel()));
        }

        try (FileOutputStream out = new FileOutputStream(SAVE_FILE)) {
            props.store(out, "Digimon Digital Battle Save");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cargarPartida(Player player) {
        Properties props = new Properties();

        try (FileInputStream in = new FileInputStream(SAVE_FILE)) {
            props.load(in);
        } catch (IOException e) {
            return false;
        }

        player.worldX = leerEntero(props, "player.worldX", player.worldX);
        player.worldY = leerEntero(props, "player.worldY", player.worldY);
        equipo.setActiveIndex(leerEntero(props, "team.activeIndex", equipo.getActiveIndex()));
        enemy.setVida(leerEntero(props, "enemy.vida", enemy.getVida()));

        for (int i = 0; i < equipo.size(); i++) {
            Digimon digimon = equipo.getDigimons().get(i);
            String key = "digimon." + i + ".";

            digimon.setVidaMaxima(leerEntero(props, key + "vidaMaxima", digimon.getVidaMaxima()));
            digimon.setAtaqueBase(leerEntero(props, key + "ataqueBase", digimon.getAtaqueBase()));
            digimon.setDefensaBase(leerEntero(props, key + "defensaBase", digimon.getDefensaBase()));
            digimon.setNivel(leerEntero(props, key + "nivel", digimon.getNivel()));
            digimon.setExperiencia(leerEntero(props, key + "experiencia", digimon.getExperiencia()));
            digimon.setExperienciaSiguienteNivel(leerEntero(props, key + "experienciaSiguiente", digimon.getExperienciaSiguienteNivel()));
            digimon.setVida(leerEntero(props, key + "vida", digimon.getVida()));
            digimon.setAtaque(digimon.getAtaqueBase());
            digimon.setDefensa(digimon.getDefensaBase());
        }

        resetBattleState();
        habilitarBotones();
        actualizarInterfazCombate();
        txtArea.append("\nPartida cargada.\n");

        return true;
    }

    private int leerEntero(Properties props, String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private class EffectSpriteLabel extends JLabel {

        private boolean statEffectActive = false;
        private BufferedImage statEffectImage;
        private Timer statEffectTimer;
        private long statEffectStartTime;
        private int statEffectDurationMs;
        private int statEffectOffsetY = 0;

        public void playStatEffect(BufferedImage effectImage, int durationMs) {
            if (effectImage == null) {
                return;
            }

            statEffectImage = effectImage;
            statEffectDurationMs = Math.max(durationMs, DEFAULT_STAT_EFFECT_DURATION_MS);
            statEffectStartTime = System.currentTimeMillis();
            statEffectOffsetY = 0;
            statEffectActive = true;

            if (statEffectTimer != null && statEffectTimer.isRunning()) {
                statEffectTimer.stop();
            }

            statEffectTimer = new Timer(STAT_EFFECT_TIMER_DELAY, e -> {
                long elapsed = System.currentTimeMillis() - statEffectStartTime;

                if (elapsed >= statEffectDurationMs) {
                    statEffectActive = false;
                    statEffectImage = null;
                    statEffectTimer.stop();
                    repaint();
                    return;
                }

                statEffectOffsetY -= STAT_EFFECT_SCROLL_SPEED;

                if (statEffectOffsetY <= -getHeight()) {
                    statEffectOffsetY = 0;
                }

                repaint();
            });

            statEffectTimer.start();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (!statEffectActive || statEffectImage == null) {
                return;
            }

            Icon icon = getIcon();

            if (!(icon instanceof ImageIcon)) {
                return;
            }

            ImageIcon imageIcon = (ImageIcon) icon;
            int iconWidth = imageIcon.getIconWidth();
            int iconHeight = imageIcon.getIconHeight();

            if (iconWidth <= 0 || iconHeight <= 0) {
                return;
            }

            int spriteX = (getWidth() - iconWidth) / 2;
            int spriteY = (getHeight() - iconHeight) / 2;

            BufferedImage shaderMask = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D maskGraphics = shaderMask.createGraphics();

            maskGraphics.drawImage(imageIcon.getImage(), 0, 0, iconWidth, iconHeight, null);
            maskGraphics.setComposite(AlphaComposite.SrcAtop.derive(0.82f));

            int patternHeight = iconHeight;

            for (int y = statEffectOffsetY; y < iconHeight; y += patternHeight) {
                maskGraphics.drawImage(statEffectImage, 0, y, iconWidth, patternHeight, null);
            }

            for (int y = statEffectOffsetY - patternHeight; y < iconHeight; y += patternHeight) {
                maskGraphics.drawImage(statEffectImage, 0, y, iconWidth, patternHeight, null);
            }

            maskGraphics.dispose();

            Graphics2D g2 = (Graphics2D) g.create();
            g2.drawImage(shaderMask, spriteX, spriteY, null);
            g2.dispose();
        }
    }
}