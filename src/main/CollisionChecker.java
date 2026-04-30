package main;

import entity.Entity;

public class CollisionChecker {
	
GamePanel gp;	
	
	public CollisionChecker(GamePanel gp ) {
		this.gp = gp;
	}
	
	public void checkTile(Entity entity) {

		int entityLeftWorldX = entity.worldX + entity.solidArea.x,
			entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width,
			entityTopWorldY = entity.worldY + entity.solidArea.y,
			entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
		
		int entityLeftCol = entityLeftWorldX/gp.tileSize,
			entityRightCol = entityRightWorldX/gp.tileSize,
			entityTopRow = entityTopWorldY/gp.tileSize,
			entityBottomRow = entityBottomWorldY/gp.tileSize;
		
		int tileA,tileB;
		
		switch(entity.direction) {
			case"up":
				entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
				tileA = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
				tileB = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
				if(gp.tileM.tile[tileA].collision == true || gp.tileM.tile[tileB].collision == true) {
					entity.collisionOn = true;
				}
			break;	
			case"down":
				entityBottomRow = (entityBottomWorldY + entity.speed)/gp.tileSize;
				tileA = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
				tileB = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
				if(gp.tileM.tile[tileA].collision == true || gp.tileM.tile[tileB].collision == true) {
					entity.collisionOn = true;
				}
			break;	
			case"left":
				entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
				tileA = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
				tileB = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
				if(gp.tileM.tile[tileA].collision == true || gp.tileM.tile[tileB].collision == true) {
					entity.collisionOn = true;
				}
			break;	
			case"right":
				entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
				tileA = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
				tileB = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
				if(gp.tileM.tile[tileA].collision == true || gp.tileM.tile[tileB].collision == true) {
					entity.collisionOn = true;
				}
			break;	
		}
				
		
	}
}
