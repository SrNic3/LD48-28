package com.fireshield.ld28;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Particle {

	float x, y, lX, lY, vy, vx;

	int life;
	int size;

	Color color;

	public Particle(float x, float y, Color color, int minlife, int maxlife, float vel) {

		Random rnd = new Random();

		this.x = (float) (x + 0.5 * (0.5f - rnd.nextFloat()));
		this.y = (float) (y + 0.5 * (0.5f - rnd.nextFloat()));

		double angle = Math.toRadians(rnd.nextInt(360));
		vy = (float) Math.cos(angle) * rnd.nextFloat() * vel;
		vx = (float) Math.sin(angle) * rnd.nextFloat() * vel;

		life = minlife + rnd.nextInt(maxlife);

		this.color = color;

		size = 2 + rnd.nextInt(5);

	}

	public void move() {

		life--;

		lX = x;
		lY = y;

		x += vx / 10;
		y += vy / 10;

	}

	public void draw(Graphics2D g2d, Bitmaps bmp, float interpolation) {

		int drawX = (int) (((x - lX) * interpolation + lX) * 32f);
		int drawY = (int) (((y - lY) * interpolation + lY) * 32f);

		g2d.setColor(color);

		g2d.fillRect(drawX, drawY, size, size);

	}
}
