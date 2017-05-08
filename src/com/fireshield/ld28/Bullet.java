package com.fireshield.ld28;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Bullet {

	private float x, y, lX, lY;

	boolean live = true;

	private float vx, vy;
	private Level level;
	double angle;

	Tile hit;

	public Bullet(Level level, float x, float y, int dx, int dy) {
		this.x = x;
		this.y = y;

		lX = x;
		lY = y;

		this.level = level;

		float xx = (dx / 32f - x);
		float yy = (dy / 32f - y);

		angle = Math.atan2(yy, xx);

		vx = (float) (0.8 * Math.cos(angle));
		vy = (float) (0.8 * Math.sin(angle));

	}

	boolean n = false;

	public void mueve() {

		lX = x;
		lY = y;

		if (!checkCollision(0, vy)) {
			y += vy;

		} else {
			y -= vy;
			n = true;;
		}

		if (!n && !checkCollision(vx, 0)) {
			x += vx;

		} else {
			x -= vx;
			n = true;;
		}

		if (n) {

			Sound.play("Explosion22.wav", false);
			live = false;

			level.bala = new Bala(level, x, y);

			for (int i = 0; i < 50; i++) {
				level.particulas.add(new Particle(x, y, Color.GRAY, 20, 20, 2.5f));
			}

		} else {
			for (int h = 0; h < 5; h++) {
				level.particulas.add(new Particle(x + 0.25f - Math.abs(vx) * Math.signum(vx), y - Math.abs(vy) * Math.signum(vy), Color.BLACK, 5, 10,
						0.8f));
			}
		}

	}
	public boolean checkCollision(double xt, double yt) {

		for (int l = 0; l < 4; l++) {

			int xf = (int) ((x + xt) + (l % 2) * 0.002f + 0.3f);
			int yf = (int) ((y + yt) + (l / 2) * 0.0000002f);

			Tile t = level.getTile(xf, yf);

			if (t != null && t.solid) {

				hit = t;
				return true;

			}

		}

		for (int i = 0; i < level.mobs.size(); i++) {
			Rectangle r11 = new Rectangle((int) (x * 32), (int) (y * 32), 16, 16);

			Entity e = level.mobs.get(i);

			Rectangle r2 = new Rectangle((int) (e.getX() * 32), (int) (e.getY() * 32), e.width, e.width);

			if (r11.intersects(r2)) {

				for (int h = 0; h < 100; h++) {
					level.particulas.add(new Particle(x, y, Color.RED, 30, 40, 2.8f));
				}

				level.mobs.get(i).life--;
				level.mobs.get(i).knockbak(vx, vy);

				if (level.mobs.get(i).life <= 0) {
					level.mobs.remove(i);
					level.mobsR--;

					if (level.mobsR > 0 && level.lvl < 10)
						level.spawn();

				}
				return true;
			}
		}
		return false;
	}

	public void draw(Graphics2D g2d, Bitmaps bmp, float interpolation) {

		if (live) {

			AffineTransform transform = new AffineTransform();

			int drawX = (int) (((x - lX) * interpolation + lX) * 32);
			int drawY = (int) (((y - lY) * interpolation + lY) * 32);

			transform.setToRotation(angle + Math.PI / 2, drawX + 8, drawY + 8);
			transform.translate(drawX, drawY);

			g2d.drawImage(bmp.bullet, transform, null);
			g2d.rotate(0);
		}
	}

}
