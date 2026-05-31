package tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.ResourceLoader;

public class TileManager {
    private final GamePanel gp;
    public final Tile[] tile;
    public final int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[100];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        setupTiles();
        loadMap("/maps/mapplaceholder.txt");
    }

    private void setupTiles() {
        setupTile(0, "/tiles/29.png", false, false);
        setupTile(1, "/tiles/60.png", true, false);
        setupTile(2, "/tiles/58.png", true, false);
        setupTile(3, "/tiles/61.png", false, false);
        setupTile(4, "/tiles/62.png", true, false);
        setupTile(5, "/tiles/59.png", false, false);
        setupTile(6, "/tiles/27.png", false, true);
    }

    private void setupTile(int index, String imagePath, boolean collision, boolean canHaveDigimon) {
        tile[index] = new Tile();
        tile[index].collision = collision;
        tile[index].canHaveDigimon = canHaveDigimon;
        try {
            tile[index].image = ResourceLoader.image(imagePath);
        } catch (IOException | IllegalArgumentException e) {
            tile[index].image = null;
        }
    }

    private void loadMap(String filePath) {
        try (InputStream is = ResourceLoader.open(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                String line = br.readLine();
                if (line == null) break;
                String[] numbers = line.trim().split("\\s+");
                for (int col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                }
            }
        } catch (Exception e) {
            createFallbackMap();
        }
    }

    private void createFallbackMap() {
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                mapTileNum[col][row] = row == 0 || col == 0 || row == gp.maxWorldRow - 1 || col == gp.maxWorldCol - 1 ? 1 : 0;
            }
        }
    }

    public int getMapTile(int col, int row) {
        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) {
            return 1;
        }
        return mapTileNum[col][row];
    }

    public boolean isCollisionTile(int index) {
        return index < 0 || index >= tile.length || tile[index] == null || tile[index].collision;
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                Tile current = tileNum >= 0 && tileNum < tile.length ? tile[tileNum] : null;
                if (current != null && current.image != null) {
                    g2.drawImage(current.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                } else {
                    g2.setColor(current != null && current.collision ? Color.DARK_GRAY : Color.GRAY);
                    g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                }
            }

            worldCol++;
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
