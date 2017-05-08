package com.fireshield.ld28;

import java.awt.Graphics2D;

public class Tile {
	public int x, y;
	public boolean solid;

	public Tipo tipo;

	public Tile(int x, int y, Tipo tipo) {
		this.x = x;
		this.y = y;
		this.tipo = tipo;

	}

	public void draw(Graphics2D g2d, Bitmaps bmp) {

		if (tipo == Tipo.BLOCK)
			g2d.drawImage(bmp.block, x, y, 32, 32, null);
		if (tipo == Tipo.BACKGROUND)
			g2d.drawImage(bmp.back, x, y, 32, 32, null);

	}

}
