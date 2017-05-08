package com.fireshield.ld28;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	private static JFrame frame;
	private Bitmaps bmp;
	private Level level;

	long t1 = System.currentTimeMillis();
	long t2 = System.currentTimeMillis();

	private boolean running = true;
	private boolean paused = false;
	private int fps = 60;
	private int frameCount = 0;

	private int mx, my;

	int ticks = 0;

	float interpolation;

	private ArrayList<Direccion> lastDir = new ArrayList<Direccion>();

	static float scale = 0;

	public static void main(String[] args) {

		Game game = new Game();

		Thread t = new Thread(game);
		t.start();

		frame = new JFrame("One Shoot, One opportunity");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 530);
//		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
//		frame.setUndecorated(true);
		frame.setAutoRequestFocus(true);
		frame.add(game);
		frame.setResizable(false);
		frame.setVisible(true);

		ImageIcon emptyIcon = new ImageIcon(new byte[0]);
		Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(emptyIcon.getImage(), new Point(0, 0), "Invisible");
		frame.setCursor(invisibleCursor);

		scale = frame.getWidth() / 32f;

	}

	public Game() {

		try {

			lastDir.add(Direccion.NO);
			bmp = new Bitmaps();
			bmp.init();
			level = new Level();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D) {
					lastDir.remove(Direccion.DER);
				}
				if (e.getKeyCode() == KeyEvent.VK_A) {
					lastDir.remove(Direccion.IZQ);
				}

				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					lastDir.remove(Direccion.DER);
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					lastDir.remove(Direccion.IZQ);
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D && !lastDir.contains(Direccion.DER)) {
					lastDir.add(Direccion.DER);
				}
				if (e.getKeyCode() == KeyEvent.VK_A && !lastDir.contains(Direccion.IZQ)) {
					lastDir.add(Direccion.IZQ);
				}

				if (e.getKeyCode() == KeyEvent.VK_RIGHT && !lastDir.contains(Direccion.DER)) {
					lastDir.add(Direccion.DER);
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT && !lastDir.contains(Direccion.IZQ)) {
					lastDir.add(Direccion.IZQ);
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (System.currentTimeMillis() - level.player.lastJump > 100)
						level.player.jump();
				}

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!level.start)
						level.start = true;
					if (level.nextLvl)
						level.nextLevel();
					if (level.loose || level.win)
						level = new Level();

				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (System.currentTimeMillis() - level.player.lastJump > 100)
						level.player.jump();
				}

			}
		});

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {

			}
			public void mouseEntered(java.awt.event.MouseEvent evt) {
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
			}
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if (level.start && !level.loose && level.player.bullet >= 1 && !level.nextLvl) {
					Sound.play("Hit_Hurt8.wav", false);
					level.bullets.add(new Bullet(level, (level.player.getX() + 0.25f), (level.player.getY() + 0.25f),
							(int) (evt.getX() / (scale / 32f)), (int) (evt.getY() / (scale / 32f))));
					level.player.bullet--;
				}
			}
			public void mouseReleased(java.awt.event.MouseEvent evt) {
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent me) {
				mx = me.getX();
				my = me.getY();
			}
		});

		setFocusable(true);

	}
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		frameCount++;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2d.scale(scale / 32f, scale / 32f);

		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Verdana", Font.BOLD, 10));

		g2d.fillRect(0, 0, 1024, 600);

		level.draw(g2d, bmp, interpolation);
		g2d.setColor(Color.WHITE);
		g2d.drawString("FPS:" + fps, 10, 20);
		g2d.drawString("U:" + lastTick, 70, 20);

		g2d.drawImage(bmp.mouse, (int) (mx / (scale / 32f)) - 16, (int) (my / (scale / 32f)) - 16, 32, 32, null);

		g2d.dispose();

	}

	int lastTick = 0;
	private void updateGame() {
		ticks++;

		level.player.setDireccion(lastDir.get(lastDir.size() - 1));
		level.update();

	}

	private void gameLoop() {
		final double GAME_HERTZ = 30.0;
		final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
		final int MAX_UPDATES_BEFORE_RENDER = 3;
		double lastUpdateTime = System.nanoTime();
		double lastRenderTime = System.nanoTime();

		final double TARGET_FPS = 60;
		final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

		int lastSecondTime = (int) (lastUpdateTime / 1000000000);

		Sound.play("music.wav", true);

		while (running) {
			double now = System.nanoTime();
			int updateCount = 0;

			if (!paused) {

				while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
					updateGame();
					lastUpdateTime += TIME_BETWEEN_UPDATES;
					updateCount++;

				}

				if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
					lastUpdateTime = now - TIME_BETWEEN_UPDATES;
				}

				interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
				repaint();
				lastRenderTime = now;

				int thisSecond = (int) (lastUpdateTime / 1000000000);
				if (thisSecond > lastSecondTime) {
					System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
					fps = frameCount;
					frameCount = 0;

					lastTick = ticks;
					System.out.println(ticks + "");
					ticks = 0;

					lastSecondTime = thisSecond;
				}

				while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
					Thread.yield();

					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}

					now = System.nanoTime();
				}
			}
		}
	}

	@Override
	public void run() {
		gameLoop();
	}
}
