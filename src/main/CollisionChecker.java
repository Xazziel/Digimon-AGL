package main;

import entity.Entity;

public class CollisionChecker {
    private final GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileA;
        int tileB;

        switch (entity.direction) {
            case "up" -> {
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileA = gp.tileM.getMapTile(entityLeftCol, entityTopRow);
                tileB = gp.tileM.getMapTile(entityRightCol, entityTopRow);
                entity.collisionOn = gp.tileM.isCollisionTile(tileA) || gp.tileM.isCollisionTile(tileB);
            }
            case "down" -> {
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileA = gp.tileM.getMapTile(entityLeftCol, entityBottomRow);
                tileB = gp.tileM.getMapTile(entityRightCol, entityBottomRow);
                entity.collisionOn = gp.tileM.isCollisionTile(tileA) || gp.tileM.isCollisionTile(tileB);
            }
            case "left" -> {
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileA = gp.tileM.getMapTile(entityLeftCol, entityTopRow);
                tileB = gp.tileM.getMapTile(entityLeftCol, entityBottomRow);
                entity.collisionOn = gp.tileM.isCollisionTile(tileA) || gp.tileM.isCollisionTile(tileB);
            }
            case "right" -> {
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileA = gp.tileM.getMapTile(entityRightCol, entityTopRow);
                tileB = gp.tileM.getMapTile(entityRightCol, entityBottomRow);
                entity.collisionOn = gp.tileM.isCollisionTile(tileA) || gp.tileM.isCollisionTile(tileB);
            }
        }
    }
}
