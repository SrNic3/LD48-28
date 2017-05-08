package com.fireshield.ld28;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Level {

	public Player player;
	public Tile[][] map = new Tile[32][16];
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();

	public ArrayList<Particle> particulas = new ArrayList<Particle>();
	public ArrayList<Entity> mobs = new ArrayList<Entity>();

	public Bala bala;

	boolean loose = false;
	boolean start = false;
	boolean nextLvl = false;

	boolean win = false;

	int mobsR = 2;

	int lvl = 0;

	public Level() {

		nextLevel();
	}

	public Tile getTile(int x, int y) {
		if (x < 32 && y < 16)
			return map[x][y];
		return null;
	}

	public Tile getTileFuture(double x, double y, float xf, float yf) {

		if (x + xf < 32 && y + yf < 16)
			return map[(int) (x + xf)][(int) (y + yf)];

		return null;
	}

	public void draw(Graphics2D g2d, Bitmaps bmp, float interpolation) {

		if (!start && !nextLvl) {

			g2d.setColor(Color.WHITE);
			g2d.drawString("Pres enter to start", 450, 200);
			g2d.drawString("You only had one Shoot, One opportunity", 390, 280);

			g2d.drawString("A D to move right and left, SPACE to jump, right mouse to fire", 330, 400);

		} else if (!loose && !nextLvl) {

			for (int i = 0; i < 32; i++) {
				for (int j = 0; j < 16; j++) {
					map[i][j].draw(g2d, bmp);
				}
			}

			player.draw(g2d, bmp, interpolation);
			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).draw(g2d, bmp, interpolation);
			}

			for (int i = 0; i < particulas.size(); i++) {
				if (particulas.get(i).life > 0)
					particulas.get(i).draw(g2d, bmp, interpolation);
				else
					particulas.remove(i);
			}

			for (int i = 0; i < mobs.size(); i++) {
				if (mobs.get(i).live)
					mobs.get(i).draw(g2d, bmp, interpolation);
			}

			if (bala != null && bala.live)
				bala.draw(g2d, bmp, interpolation);

			g2d.drawString(player.bullet + "", 10, 10);

			for (int i = 0; i < player.life; i++) {
				g2d.drawImage(bmp.heart, 350 + i * 30, 5, 25, 25, null);

			}

			int alpha = 200 - (int) (255 * (System.currentTimeMillis() - player.lastHurt) / 250);

			if (alpha < 0)
				alpha = 0;
			if (alpha > 255)
				alpha = 255;

			Color color = new Color(255, 0, 0, alpha);

			g2d.setPaint(color);

			g2d.fillRect(0, 0, 1024, 530);

		} else if (loose) {

			g2d.setColor(Color.WHITE);
			g2d.drawString("You have died ", 450, 200);

			g2d.drawString("Press enter to try again", 420, 400);

		} else if (nextLvl && !win) {
			g2d.setColor(Color.WHITE);
			g2d.drawString("Press enter to continue", 450, 200);

		}

		if (win) {
			g2d.setColor(Color.BLACK);

			g2d.fillRect(0, 0, 1024, 600);
			g2d.setColor(Color.WHITE);
			g2d.drawString("Congratulations, you have defeated yours evil clons", 350, 200);
			g2d.drawString("with only one bullet.", 450, 220);

		}
	}

	public void update() {
		if (!win) {

			if (mobs.size() == 0) {
				nextLvl = true;
			}

			if (player.life == 0) {
				player.life = -1;
				Sound.play("lose.wav", false);
				loose = true;
			}
			if (start && !loose && !nextLvl) {
				player.mueve();
				for (int i = 0; i < bullets.size(); i++) {
					if (bullets.get(i).live)
						bullets.get(i).mueve();
				}

				for (int i = 0; i < particulas.size(); i++) {
					particulas.get(i).move();
				}

				for (int i = 0; i < mobs.size(); i++) {
					mobs.get(i).mueve();
				}

				if (bala != null && bala.live)
					bala.mueve();
			}
		}
	}

	public void readLl() throws IOException {

		BufferedImage lvl1 = ImageIO.read(Game.class.getResourceAsStream("/" + "lvl" + lvl + ".png"));

		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 16; j++) {

				int colour = lvl1.getRGB(i, j);

				int red = (colour & 0x00ff0000) >> 16;
				int green = (colour & 0x0000ff00 >> 8);
				int blue = (colour & 0x000000ff);

				if (red == 0 && blue == 0 && green == 0) {
					map[i][j] = new Tile(i * 32, j * 32, Tipo.BLOCK);

					map[i][j].solid = true;

				}

				if (red == 255 && blue == 255 && green == 255) {
					map[i][j] = new Tile(i * 32, j * 32, Tipo.BACKGROUND);
				}
			}
		}

		int num = lvl;

		if (num > 7)
			num = 7;

		if (lvl == 9)
			num = 5;

		if (lvl != 10)
			while (mobs.size() <= num)
				spawn();
		else
			while (mobs.size() < 2)
				spawn();

		while (!spawnP())
			spawnP();
	}
	public void nextLevel() {

		nextLvl = false;

		if (lvl == 10) {
			win = true;
			return;
		}

		bala = null;
		mobs.clear();
		particulas.clear();
		bullets.clear();
		lvl++;
		mobsR = lvl * 2;
		try {
			readLl();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean spawnP() {
		Random rnd = new Random();
		int xx = 1 + rnd.nextInt(30);
		int yy = 1 + rnd.nextInt(14);

		if (map[xx][yy].solid)
			return false;

		player = new Player(this, xx, yy);
		return true;

	}

	public boolean spawn() {
		Random rnd = new Random();
		int xx = 1 + rnd.nextInt(29);
		int yy = 1 + rnd.nextInt(13);

		if (map[xx][yy].solid)
			return false;

		int tipo = 1;
		if (lvl == 10)
			tipo = 2;

		mobs.add(new Entity(this, xx, yy, tipo));
		return true;

	}
}
