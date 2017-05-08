package com.fireshield.ld28;

import java.awt.Graphics2D;

public class Player {

	private float x, y, lX, lY;
	private float vx, vy;
	private int width;

	private float gravity = 0.1f;

	private boolean jump = false;

	private Direccion direccion;

	private Level level;

	public int bullet = 1;

	private Tile floor;
	private Tile top;

	public long lastJump;

	public int life = 10;

	public long lastHurt = System.currentTimeMillis();

	public Player(Level level, float x, float y) {

		this.level = level;

		this.x = x;
		this.y = y;
		lX = x;
		lY = y;

		width = 32;

		direccion = Direccion.NO;

		vx = 0;
		vy = 0;
	}

	public void setDireccion(Direccion dir) {
		direccion = dir;
	}

	public void mueve() {

		lX = x;
		lY = y;

		switch (direccion) {
			case DER :
				vx += 0.07f;
				if (vx > 0.3)
					vx = 0.3f;
				break;
			case IZQ :
				vx -= 0.07f;
				if (vx < -0.3)
					vx = -0.3f;
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
		}

		if (!checkCollision(0, vy)) {
			y += vy;
			jump = true;
		} else {
			if (!jump)
				vy = 0;
			if (top == null) {
				jump = false;
				vy = 0;
			}
			jump = false;
			if (floor != null)
				y = (int) ((floor.y - 1) / 32);
		}

	}
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void knockBack(float x, float y) {
		vx = x;
		this.y += y;
	}

	public void jump() {

		if (!jump) {
			lastJump = System.currentTimeMillis();
			Sound.play("Jump2.wav", false);
			vy = -1.1f;
			jump = true;
			return;
		}
	}

	public void draw(Graphics2D g2d, Bitmaps bmp, float interpolation) {

		int drawX = (int) (((x - lX) * interpolation + lX) * 32f);
		int drawY = (int) (((y - lY) * interpolation + lY) * 32f);

		if (!jump)
			g2d.drawImage(bmp.player, drawX, drawY, width, width, null);
		else
			g2d.drawImage(bmp.player_jump, drawX, drawY, width, width, null);

	}

	public boolean checkCollision(double xt, double yt) {

		for (int l = 0; l < 4; l++) {

			int xf = (int) ((x + xt) + (l % 2) * 0.42 + 0.25f);
			int yf = (int) ((y + yt) + (l / 2) * 0.58 + 0.35f);

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
}
