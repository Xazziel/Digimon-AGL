package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JPanel;

import entity.Player;

public class GamePanel extends JPanel implements Runnable{

	//Screen Settings
	
	final int originalTileSize = 32;
	final int scale = 3;
	
	public final int tileSize = originalTileSize*scale;
	final int maxScreenCol = 12;
	final int maxScreenRow = 9;
	final int screenWidth = tileSize*maxScreenCol;
	final int screenHeight = tileSize*maxScreenCol;
	
	//FPS
	int fps=60;
	
	KeyHandler keyH = new KeyHandler(); 
	Thread gameThread;
	Player player = new Player(this,keyH);
	
	//Set posicion default del jugador que juega
	int playerX=100;
	int playerY=100;
	int playerSpeed=4;
	
	public GamePanel()
	{
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(Color.white);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}

	public void startGameThread() {
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		
		while(gameThread != null) {
			
			double drawInterval = 1000000000/fps;
			double nextDrawTime = System.nanoTime() + drawInterval;
			//System.out.print("Mod del Año ");
			
			//actualiza la informacion 
			update();
			//pos dibuja que mas va hacer? no profe estos comentarios son de verdad aqui no hay IAslop mejor me hago fan del america
			repaint();
			
			
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime/1000000;
				
				if (remainingTime < 0)
				{
					remainingTime = 0;
				}
				
				Thread.sleep((long) remainingTime);
				
				nextDrawTime += drawInterval;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void update() {
		
		player.update();
		
	}
	
	public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D)g;
			
			player.draw(g2);
		
			g2.dispose();
		}
}
