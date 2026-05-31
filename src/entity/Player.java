package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;
import main.ResourceLoader;

public class Player extends Entity {
    private final GamePanel gp;
    private final KeyHandler keyH;
    public final int screenX;
    public final int screenY;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        screenX = gp.screenWidth / 2 - gp.tileSize / 2;
        screenY = gp.screenHeight / 2 - gp.tileSize / 2;
        solidArea = new Rectangle(8, 18, 28, 28);
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }

    private BufferedImage load(String path) throws IOException {
        return ResourceLoader.image(path);
    }

    public void getPlayerImage() {
        try {
            up = load("/Player/7.png");
            up1 = load("/Player/9.png");
            up2 = load("/Player/8.png");
            down = load("/Player/4.png");
            down1 = load("/Player/6.png");
            down2 = load("/Player/5.png");
            left = load("/Player/10.png");
            left1 = load("/Player/11.png");
            left2 = load("/Player/12.png");
            right = load("/Player/13.png");
            right1 = load("/Player/14.png");
            right2 = load("/Player/15.png");
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            speed = keyH.shiftPressed ? 8 : 5;
            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (!collisionOn) {
                switch (direction) {
                    case "up" -> worldY -= speed;
                    case "down" -> worldY += speed;
                    case "left" -> worldX -= speed;
                    case "right" -> worldX += speed;
                }
            }

            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = spriteNum == 1 ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = down;
        switch (direction) {
            case "up" -> image = spriteNum == 1 ? up1 : spriteNum == 2 ? up2 : up;
            case "down" -> image = spriteNum == 1 ? down1 : spriteNum == 2 ? down2 : down;
            case "left" -> image = spriteNum == 1 ? left1 : spriteNum == 2 ? left2 : left;
            case "right" -> image = spriteNum == 1 ? right1 : spriteNum == 2 ? right2 : right;
        }
        if (image != null) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
