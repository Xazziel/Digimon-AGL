package entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity{
	
	GamePanel gp;
	KeyHandler keyH;
	
	public Player(GamePanel gp,KeyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;
		
		setDefaultValues();
		getPlayerImage();
		
	}
	
	public void setDefaultValues() {
		x=100;
		y=120;
		speed=4;
		direction = "up";
	}
	
	public void getPlayerImage() {
		
		try {
			up = ImageIO.read(getClass().getResourceAsStream("/Player/7.png"));
			up1 = ImageIO.read(getClass().getResourceAsStream("/Player/9.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/Player/8.png"));
			down = ImageIO.read(getClass().getResourceAsStream("/Player/4.png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/Player/6.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/Player/5.png"));
			left = ImageIO.read(getClass().getResourceAsStream("/Player/10.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/Player/11.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/Player/12.png"));
			right = ImageIO.read(getClass().getResourceAsStream("/Player/13.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/Player/14.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/Player/15.png"));
		
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public void update() {
		if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
	        
	        if (keyH.upPressed) {
	            direction = "up";
	            y -= speed;
	        } else if (keyH.downPressed) {
	            direction = "down";
	            y += speed;
	        } else if (keyH.leftPressed) {
	            direction = "left";
	            x -= speed;
	        } else if (keyH.rightPressed) {
	            direction = "right";
	            x += speed;
	        }

	        // Lógica de animación (solo corre cuando te mueves)
	        spriteCounter++;
	        if (spriteCounter > 12) { 
	            // Si estaba en 0 (idle) o en 2, pasamos al 1
	            if (spriteNum == 1 || spriteNum == 0) {
	                spriteNum = 2;
	            } else {
	                spriteNum = 1;
	            }
	            spriteCounter = 0;
	        }
	    } else {
	        // Si no hay ninguna tecla presionada, el sprite vuelve a ser 0 (IDLE)
	        spriteNum = 0;
	    }
	}
	
	public void draw(Graphics2D g2) {
		
		//g2.setColor(Color.black);
		//g2.fillRect(x, y, gp.tileSize, gp.tileSize);
		
		BufferedImage image = null;
		
		switch(direction) {
		case"up":
			if(spriteNum==0) {
				image=up;
			}
			else if(spriteNum==1) {
				image=up1;
			}
			else if(spriteNum==2) {
				image=up2;
			}
			
			break;
		case"down":
			if(spriteNum==0) {
				image=down;
			}
			else if(spriteNum==1) {
				image=down1;
			}
			else if(spriteNum==2) {
				image=down2;
			}
			break;	
		case"left":
			if(spriteNum==0) {
				image=left;
			}
			else if(spriteNum==1) {
				image=left1;
			}
			else if(spriteNum==2) {
				image=left2;
			}
			break;	
		case"right":
			if(spriteNum==0) {
				image=right;
			}
			else if(spriteNum==1) {
				image=right1;
			}
			else if(spriteNum==2) {
				image=right2;
			}
			break;
		}
		
		g2.drawImage(image, x, y, gp.tileSize,gp.tileSize, null);
	}

}
