package com.fireshield.ld28;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bitmaps {

	public BufferedImage player;
	public BufferedImage player_jump;
	public BufferedImage bullet;
	public BufferedImage block;
	public BufferedImage back;
	public BufferedImage mob1;
	public BufferedImage heart;
	public BufferedImage mouse;

	public void init() throws IOException {
		player = ImageIO.read(Game.class.getResourceAsStream("/manolo.png"));
		player_jump = ImageIO.read(Game.class.getResourceAsStream("/manolo_jump.png"));
		bullet = ImageIO.read(Game.class.getResourceAsStream("/bullet.png"));
		block = ImageIO.read(Game.class.getResourceAsStream("/block.png"));
		back = ImageIO.read(Game.class.getResourceAsStream("/back.png"));
		mob1 = ImageIO.read(Game.class.getResourceAsStream("/mob1.png"));
		heart = ImageIO.read(Game.class.getResourceAsStream("/heart.png"));
		mouse = ImageIO.read(Game.class.getResourceAsStream("/mouse.png"));
	}

}
