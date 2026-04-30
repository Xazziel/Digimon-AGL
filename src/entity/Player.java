package entity;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity{
	
	GamePanel gp;
	KeyHandler keyH;
	public int screenX;
	public int screenY;
	
	public Player(GamePanel gp,KeyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;
		
		screenX = gp.screenWidth/2-(gp.tileSize/2);
		screenY = gp.screenHeight/2-(gp.tileSize/2);
		
		solidArea = new Rectangle();
		solidArea.x = 0;
		solidArea.y = 16;
		solidArea.width = 32;
		solidArea.height = 32;		
		
		setDefaultValues();
		getPlayerImage();
		
	}
	
	public void setDefaultValues() {
		worldX=gp.tileSize*23;
		worldY=gp.tileSize*21;
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
	        } else if (keyH.downPressed) {
	            direction = "down";
	        } else if (keyH.leftPressed) {
	            direction = "left";
	        } else if (keyH.rightPressed) {
	            direction = "right";
	        }

	        //check player collision
	        collisionOn = false;
	        gp.cChecker.checkTile(this);
	        
	        //si es false,player se mueve sin problemas
	        if(collisionOn == false)
	        {
	        	 switch (direction) {
	 				case "up": 
	 		            worldY -= speed;
	 		        break;    
	 				case "down": 
	 					worldY += speed;
	 				break;    
	 				case "left": 
	 					worldX -= speed;
	 				break;    
	 				case "right": 
	 					worldX += speed;
	 				break;    
	 			}
	        }
	        
	        spriteCounter++;
	        if (spriteCounter > 12) { 
	            if (spriteNum == 1 || spriteNum == 0) {
	                spriteNum = 2;
	            } else {
	                spriteNum = 1;
	            }
	            spriteCounter = 0;
	        }
	    } else {
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
		
		g2.drawImage(image, screenX, screenY, gp.tileSize,gp.tileSize, null);
	}

}
