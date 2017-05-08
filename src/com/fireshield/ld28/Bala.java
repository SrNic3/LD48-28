package com.fireshield.ld28;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Bala {

	float x, y, lY, vx;
	Level level;
	boolean live = true;

	float vy = 0.1f;

	private Tile floor;

	public Bala(Level level, float x, float y) {

		this.x = x;
		this.y = y;

		this.level = level;

	}

	public void mueve() {

		lY = y;
		if (!checkCollision(0, 0.1)) {
			y += vy;
		} else if (floor != null && vy > 0) {

			y = floor.y / 32 - 16f / 32;
			vy = 0;
		}

		if (playerNearCollision()) {
			y += vy;
			x += vx;
		} else {
			vy = 0.1f;

		}
	}

	public boolean checkCollision(double xt, double yt) {

		if (playerCollision()) {
			Sound.play("Pickup.wav", false);
			level.player.bullet++;
			live = false;
		}

		int xf = (int) (x + 0.3f + xt);
		int yf = (int) (y + 0.45f + yt);

		Tile t = level.getTile(xf, yf);

		floor = null;
		if (t != null && t.solid) {
			floor = t;
			return true;
		}

		return false;
	}
	public void draw(Graphics2D g2d, Bitmaps bmp, float interpolation) {

		int drawY = (int) (((y - lY) * interpolation + lY) * 32f);

		g2d.drawImage(bmp.bullet, (int) (x * 32), drawY, 16, 16, null);

	}
	public boolean playerCollision() {
		Rectangle r1 = new Rectangle((int) ((x) * 32), (int) (y * 32), 16, 20);
		Player p = level.player;

		Rectangle r2 = new Rectangle((int) (p.getX() * 32), (int) (p.getY() * 32), 32, 32);

		if (r1.intersects(r2)) {

			return true;

		}

		return false;
	}

	public boolean playerNearCollision() {
		Rectangle r1 = new Rectangle((int) ((x) * 32 - 32), (int) ((y) * 32 - 32), 80, 80);
		Player p = level.player;

		Rectangle r2 = new Rectangle((int) (p.getX() * 32), (int) (p.getY() * 32), 32, 32);

		if (r1.intersects(r2)) {

			float xx = x - p.getX() - 0.25f;
			float yy = y - p.getY() - 0.25f;

			vx = 0.05f * Math.signum(-xx);
			vy = 0.05f * Math.signum(-yy);

			System.out.println(vx);

			return true;
		}
		return false;
	}
}
