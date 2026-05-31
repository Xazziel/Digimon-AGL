package main;

import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import digimon.combat;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int screenWidth = 1280;
    public final int screenHeight = 720;
    public final int maxScreenCol = screenWidth / tileSize;
    public final int maxScreenRow = screenHeight / tileSize;

    private final int battleWidth = screenWidth;
    private final int battleHeight = screenHeight;

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    private final int fps = 60;
    public final TileManager tileM = new TileManager(this);
    public final CollisionChecker cChecker = new CollisionChecker(this);
    public final KeyHandler keyH = new KeyHandler();
    public final Player player = new Player(this, keyH);

    private Thread gameThread;
    private boolean battleOpen = false;
    private boolean cToggleLocked = false;
    private final combat battle;
    private final Random encounterRandom = new Random();
    private int movementCounter = 0;
    private String mapMessage = "";
    private int mapMessageTicks = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.black);
        setDoubleBuffered(true);
        setLayout(null);
        addKeyListener(keyH);
        setFocusable(true);

        battle = new combat();
        battle.setBounds(0, 0, battleWidth, battleHeight);
        battle.setVisible(false);
        add(battle);

        installBattleToggleKey();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / fps;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void update() {
        if (!battleOpen) {
            int previousX = player.worldX;
            int previousY = player.worldY;
            player.update();

            if (previousX != player.worldX || previousY != player.worldY) {
                checkRandomEncounter();
            }
        }

        if (mapMessageTicks > 0) {
            mapMessageTicks--;
        }
    }

    private void checkRandomEncounter() {
        movementCounter++;
        if (movementCounter < 120) {
            return;
        }
        movementCounter = 0;

        if (encounterRandom.nextInt(100) < 12) {
            battle.prepararEncuentroAleatorio();
            showMapMessage("¡Encuentro digital detectado!");
            openBattle();
        }
    }

    private void installBattleToggleKey() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, false), "toggleBattle");
        getActionMap().put("toggleBattle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!cToggleLocked) {
                    cToggleLocked = true;
                    toggleBattle();
                }
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, true), "unlockBattleToggle");
        getActionMap().put("unlockBattleToggle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cToggleLocked = false;
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false), "saveGame");
        getActionMap().put("saveGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean saved = battle.guardarPartida(player);
                showMapMessage(saved ? "Partida guardada en save_digimon.properties" : "No se pudo guardar la partida");
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, false), "loadGame");
        getActionMap().put("loadGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean loaded = battle.cargarPartida(player);
                showMapMessage(loaded ? "Partida cargada" : "No existe una partida guardada");
                repaint();
            }
        });

        for (int slot = 1; slot <= 6; slot++) {
            final int teamSlot = slot;
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + slot, 0, false), "switchDigimon" + slot);
            getActionMap().put("switchDigimon" + slot, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (battleOpen) {
                        battle.cambiarDigimon(teamSlot);
                    }
                }
            });
        }
    }

    private void toggleBattle() {
        if (battleOpen) {
            closeBattle();
        } else {
            openBattle();
        }
    }

    private void openBattle() {
        if (battle.enemigoDerrotado()) {
            battle.prepararNuevoEncuentro("Nuevo encuentro digital iniciado.");
        }
        battleOpen = true;
        stopMovementInput();
        battle.setVisible(true);
        battle.requestFocusInWindow();
        repaint();
    }

    private void closeBattle() {
        battleOpen = false;
        battle.setVisible(false);
        stopMovementInput();
        requestFocusInWindow();
        repaint();
    }

    private void stopMovementInput() {
        keyH.upPressed = false;
        keyH.downPressed = false;
        keyH.leftPressed = false;
        keyH.rightPressed = false;
        keyH.shiftPressed = false;
        keyH.cPressed = false;
    }

    private void showMapMessage(String message) {
        mapMessage = message;
        mapMessageTicks = 180;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (!battleOpen) {
            tileM.draw(g2);
            player.draw(g2);
            drawHelp(g2);
            drawMapMessage(g2);
        }

        g2.dispose();
    }

    private void drawHelp(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(24, 24, 460, 136, 18, 18);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 17));
        g2.drawString("WASD / Flechas: mover", 44, 56);
        g2.drawString("C: abrir / cerrar combate", 44, 84);
        g2.drawString("1-6 en combate: cambiar Digimon", 44, 112);
        g2.drawString("F5: guardar   F9: cargar", 44, 140);
    }

    private void drawMapMessage(Graphics2D g2) {
        if (mapMessageTicks <= 0 || mapMessage == null || mapMessage.isBlank()) {
            return;
        }
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRoundRect(380, 620, 520, 56, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(mapMessage, 405, 655);
    }
}
