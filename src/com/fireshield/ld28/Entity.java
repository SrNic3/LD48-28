package com.fireshield.ld28;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Entity {

	private float x, y, vx, vy, lX, lY;

	public int tipo;

	private float gravity = 0.1f;

	public boolean jump;
	private Direccion direccion;

	private Tile floor;
	private Tile top;

	public int width = 32;

	private Level level;

	public boolean live = true;
	public int life = 2;

	public Entity(Level level, float x, float y, int tipo) {
		this.x = x;
		this.y = y;

		lX = x;
		lY = y;

		vx = 0;
		vy = 0;

		this.level = level;

		this.tipo = tipo;

		width = 32 * tipo;

		if (tipo == 1)
			life = 1;
		if (tipo == 2)
			life = 10;

		direccion = Direccion.NO;

	}

	public void IA() {
		float xx = x - level.player.getX();
		float yy = y - level.player.getY();

		if (xx > 0)
			direccion = Direccion.IZQ;
		else
			direccion = Direccion.DER;

		if (yy > 0) {
			jump();
		}
	}

	public long t = System.currentTimeMillis();

	public void mueve() {

		if (System.currentTimeMillis() - t > 350) {
			IA();
			t = System.currentTimeMillis();
		}

		lX = x;
		lY = y;

		switch (direccion) {
			case DER :
				vx += 0.05f;
				if (vx > 0.18)
					vx = 0.18f;
				break;
			case IZQ :
				vx -= 0.05f;
				if (vx < -0.18)
					vx = -0.18f;
				break;
			default :

				if (vx > 0)
					vx -= 0.03;
				if (vx < 0)
					vx += 0.03;

				if (Math.abs(vx) <= 0.09)
					vx = 0;
				break;
		}

		vy += gravity;

		if (vy > 0.5)
			vy = 0.5f;

		if (!checkCollision(vx, 0)) {
			x += vx;
		} else {
			jump();
		}

		if (!checkCollision(0, vy)) {
			y += vy;
			if (vy > 0 && vy < 0.1)
				jump = true;
		} else {
			if (!jump)
				vy = 0;
			if (top == null) {
				jump = false;
				vy = 0;
			}
			if (floor != null)
				y = (int) ((floor.y - 1) / 32);
		}

		checkCollisionPlayer();

	}
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void jump() {

		if (!jump) {
			Sound.play("Jump2.wav", false);
			if (tipo == 1)
				vy = -1.0f;
			if (tipo == 2)
				vy -= 1.1f;
			jump = true;
			return;
		}
	}

	public void draw(Graphics2D g2d, Bitmaps bmp, float interpolation) {

		int drawX = (int) (((x - lX) * interpolation + lX) * 32f);
		int drawY = (int) (((y - lY) * interpolation + lY) * 32f);

		g2d.drawImage(bmp.mob1, drawX, drawY, width, width, null);

	}

	public void checkCollisionPlayer() {
		Rectangle r1 = new Rectangle((int) ((x) * 32), (int) (y * 32), width, width);
		Player p = level.player;

		Rectangle r2 = new Rectangle((int) (p.getX() * 32), (int) (p.getY() * 32), 30, 3);

		if (r1.intersects(r2)) {

			if (System.currentTimeMillis() - level.player.lastHurt > 666) {
				Sound.play("Hi.wav", false);
				level.player.knockBack(2f * vx, 0);
				level.player.life--;
				level.player.lastHurt = System.currentTimeMillis();
			}
		}

		for (int i = 0; i < level.mobs.size(); i++) {
			Entity e = level.mobs.get(i);

			if (!e.equals(this)) {
				r2 = new Rectangle((int) e.getX() * 32, (int) e.getY() * 32, width, width);

				if (r1.intersects(r2)) {

					level.mobs.get(i).vx += vx;
					vx = 0;

				}
			}
		}

	}
	public boolean checkCollision(double xt, double yt) {

		for (int l = 0; l < 4; l++) {

			int xf = (int) ((x + xt) + (l % 2) * 0.42 * width / 32 + 0.25f);
			int yf = (int) ((y + yt) + (l / 2) * 0.57 * width / 32 + 0.35f);

			Tile t = level.getTile(xf, yf);

			if (t != null && t.solid) {

				top = null;
				floor = null;

				if (vy > 0)
					floor = t;
				else if (vy < 0)
					top = t;
				return true;
			}

		}
		return false;
	}

	public void knockbak(float kx, float ky) {

		vx += kx;
		vy += ky;

	}
}
