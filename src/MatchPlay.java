import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.sql.Time;
import java.time.Year;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.text.AbstractDocument.BranchElement;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

public class MatchPlay implements Runnable {
	static Robot robot;
	static int height;
	static int width;
	static Random random;
	final static int arenaTopx = 745;
	final static int arenaTopy = 175;
	final static int arenaBottomx = 1174;
	final static int arenaBottomy = 756;
	static volatile BufferedImage image;
	final static int DISTANCE_BETWEEN_SLOTS = 100;

	static int towersBroken;

	public static void main(String[] args) throws AWTException, InterruptedException {
		initialize();
		(new Thread(new MatchPlay())).start();

		while (true) {
			TimeUnit.SECONDS.sleep(1);
			upgradeCards();
			TimeUnit.SECONDS.sleep(1);
			requestCards();
			TimeUnit.SECONDS.sleep(1);
			openChest();
			TimeUnit.SECONDS.sleep(3);
			dailyQuest();
			TimeUnit.SECONDS.sleep(5);
			playMatch();

		}

	}

	public static void playArrows(ArrayList<Point> enemies) throws InterruptedException {
		int maxEnemies = 0;
		Point maxPoint = new Point(0, 0);
		for (int i = 0; i < enemies.size(); i++) {
			int enemiesIn = 0;
			for (int k = 0; k < enemies.size(); k++) {
				if (Math.pow(enemies.get(k).getX() - enemies.get(i).getX(), 2)
						+ Math.pow(enemies.get(k).getY() - enemies.get(i).getY(), 2) < 16) {
					enemiesIn++;

				}
			}
			if (enemiesIn > maxEnemies) {
				maxEnemies = enemiesIn;
				maxPoint = enemies.get(i);
			}
		}
		
		if (maxEnemies > 4) {
			for (int q = 0; q < 4; q++) {
				int color = image.getRGB(890 + q * DISTANCE_BETWEEN_SLOTS, 928);
				int blue = color & 0xff;
				int green = (color & 0xff00) >> 8;
				int red = (color & 0xff0000) >> 16;

				if (red < 150 && green < 200 && blue > 250) {
					selectCard(q);
					TimeUnit.SECONDS.sleep(1);
					click((int) maxPoint.getX(), (int) maxPoint.getY() + 30);
					TimeUnit.SECONDS.sleep(1);
				}
			}
		}
	}

	public static void requestCards() throws InterruptedException {
		click(1080, 940);
		TimeUnit.SECONDS.sleep(2);

		int color = image.getRGB(815, 873);
		int blue = color & 0xff;
		int green = (color & 0xff00) >> 8;
		int red = (color & 0xff0000) >> 16;

		if (red != green || green != blue) {
			click(815, 873);
			click(780, 380);
			click(787, 482);
		}
		TimeUnit.SECONDS.sleep(1);
		click(930, 940);
	}

	public static void upgradeCards() throws InterruptedException {
		click(820, 940);
		TimeUnit.SECONDS.sleep(3);
		for (int x = arenaTopx; x < arenaBottomx; x++) {
			for (int y = arenaTopy; y < arenaBottomy; y++) {
				int colorz = image.getRGB(x, y);
				int bluez = colorz & 0xff;
				int greenz = (colorz & 0xff00) >> 8;
				int redz = (colorz & 0xff0000) >> 16;

				if (redz == 86 && greenz == 255 && bluez == 88) {
					click(x, y);
					click(x, y);
					click(930, 834);
					TimeUnit.SECONDS.sleep(5);
					click();
				}
			}
		}
		TimeUnit.SECONDS.sleep(2);
		click(1000, 940);
	}

	public static void playMatch() throws InterruptedException {
		click(897, 640);
		TimeUnit.SECONDS.sleep(5);

		while (true) {

			Point nearestEnemy = new Point(0, 0);
			ArrayList<Point> enemies = detectEnemies();
			//playArrows(enemies);
			for (int i = 0; i < enemies.size(); i++) {
				if (enemies.get(i).getY() > nearestEnemy.x) {
					nearestEnemy.y = (int) enemies.get(i).getY();
					nearestEnemy.x = (int) enemies.get(i).getX();
				}
			}

			int arrowSlot = 5;

			for (int q = 0; q < 4; q++) {
				int color = image.getRGB(890 + q * DISTANCE_BETWEEN_SLOTS, 928);
				int blue = color & 0xff;
				int green = (color & 0xff00) >> 8;
				int red = (color & 0xff0000) >> 16;

				if (red < 150 && green < 200 && blue > 250) {
					arrowSlot = q;
				}
			}

			if (nearestEnemy.y != 0) {
				int selected = random.nextInt(4);
				while (true) {
					if (selected == arrowSlot) {
						selected = random.nextInt(4);
					} else {
						break;
					}
				}
				selectCard(selected);
				click((int) nearestEnemy.getX(), (int) (limitY(arenaBottomy + arenaTopy) / 2 - nearestEnemy.getY() + 100
						+ (arenaBottomy + arenaTopy) / 2));
			}

			int colorz = image.getRGB(1166, 973);
			int bluez = colorz & 0xff;
			int greenz = (colorz & 0xff00) >> 8;
			int redz = (colorz & 0xff0000) >> 16;
			if (redz > 180) {
				selectCard(random.nextInt(4));
				click(950, 736);
			}
			TimeUnit.MILLISECONDS.sleep(50);
			if (random.nextInt(6) == 1) {
				emote(random.nextInt(9));
			}
			TimeUnit.MILLISECONDS.sleep(500);

			int color = image.getRGB(947, 880);
			int blue = color & 0xff;
			int green = (color & 0xff00) >> 8;
			int red = (color & 0xff0000) >> 16;

			if (red + blue + green == 765) {
				click(947, 880);
				TimeUnit.SECONDS.sleep(1);
				return;
			}

			int color2 = image.getRGB(802, 608);
			int blue2 = color2 & 0xff;
			int green2 = (color & 0xff00) >> 8;
			int red2 = (color & 0xff0000) >> 16;

			color = image.getRGB(1119, 607);
			blue = color & 0xff;
			green = (color & 0xff00) >> 8;
			red = (color & 0xff0000) >> 16;
			if (!(blue > 200)) {
				if (!(blue2 > 200)) {
					towersBroken = 2;
				} else {
					towersBroken = 1;
				}

			} else {
				if (blue2 > 200) {
					towersBroken = 0;
				} else {
					towersBroken = -1;
				}
			}

		}
	}

	public static ArrayList<Point> detectEnemies() {
		ArrayList<Point> enemyLocations = new ArrayList<>();

		for (int x = arenaTopx; x < arenaBottomx; x++) {
			for (int y = arenaTopy; y < arenaBottomy; y++) {
				int color = image.getRGB(x, y);
				int blue = color & 0xff;
				int green = (color & 0xff00) >> 8;
				int red = (color & 0xff0000) >> 16;

				if (red > 210 && blue == green && green < 60) {
					int colorz = image.getRGB(x - 4, y);
					int bluez = colorz & 0xff;
					int greenz = (colorz & 0xff00) >> 8;
					int redz = (colorz & 0xff0000) >> 16;
					if (bluez + greenz + redz == 765) {
						for (int i = 0; i < enemyLocations.size(); i++) {
							if (enemyLocations.get(i).getY() != 0) {
								if (Math.sqrt(Math.pow(enemyLocations.get(i).getY() - y, 2)
										+ Math.pow(enemyLocations.get(i).getX() - x, 2)) < 15) {
									continue;
								}
							}
						}
						enemyLocations.add(new Point(x, y));
					}
				}
			}
		}
		return enemyLocations;
	}

	public void run() {
		while (true) {
			image = robot.createScreenCapture(new Rectangle(0, 0, width, height));
			int color = image.getRGB(1315, 600);
			int blue = color & 0xff;
			int green = (color & 0xff00) >> 8;
			int red = (color & 0xff0000) >> 16;

			if (blue + green + red == 765) {
				System.exit(0);
			}

			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void initialize() throws AWTException, InterruptedException {
		random = new Random();
		robot = new Robot();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		TimeUnit.SECONDS.sleep(5);
	}

	public static int getCard(int slotNum) {

		int color = image.getRGB(width / 2 + 100 * slotNum, height - height / 10);
		int blue = color & 0xff;
		int green = (color & 0xff00) >> 8;
		int red = (color & 0xff0000) >> 16;

		return slotNum;

	}

	public static void selectCard(int slotNum) throws InterruptedException {
		switch (slotNum) {
		case 0:
			click(864, 866);
			break;
		case 1:
			click(962, 866);
			break;
		case 2:
			click(1050, 866);
			break;
		case 3:
			click(1150, 866);
			break;
		}
	}

	public static int limitX(int x) {
		if (x > arenaBottomx) {
			return arenaBottomx;
		} else if (x < arenaTopx) {
			return arenaTopx;
		} else {
			return x;
		}
	}

	public static int limitY(int y) {
		if (y > arenaBottomy) {
			return arenaBottomy;
		} else if (y < arenaTopy) {
			return arenaTopy;
		} else {
			return y;
		}
	}

	public static void swipe(int direction) throws InterruptedException {
		switch (direction) {
		case -1:
			robot.mouseMove(730, 460);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseMove(1190, 460);
			break;

		case 1:
			robot.mouseMove(1190, 460);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseMove(730, 460);
			break;
		}
		TimeUnit.MILLISECONDS.sleep(100);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public static void dailyQuest() throws InterruptedException {
		int color = image.getRGB(916, 238);
		int blue = color & 0xff;
		int green = (color & 0xff00) >> 8;
		int red = (color & 0xff0000) >> 16;

		if (red > blue) {
			click(916, 238);
			TimeUnit.SECONDS.sleep(1);
			for (int x = 0; x < 3; x++) {
				for (int y = 238; y < 890; y++) {
					color = image.getRGB(795 + x * 130, y);
					blue = color & 0xff;
					green = (color & 0xff00) >> 8;
					red = (color & 0xff0000) >> 16;
					if (blue < 100 && green < 220 && red == 255) {
						for (int i = 0; i < 10; i++) {
							click(795 + x * 130, y);
							TimeUnit.SECONDS.sleep(1);
						}
						click(1194, 148);
						return;
					}
				}
			}
			click(1194, 148);
		}

	}

	public static void openChest() throws InterruptedException {

		boolean openChest = true;
		for (int x = 701; x < 1200; x++) {
			for (int y = 737; y < 890; y++) {
				int color = image.getRGB(x, y);
				int blue = color & 0xff;
				int green = (color & 0xff00) >> 8;
				int red = (color & 0xff0000) >> 16;

				if (red == 255 && green == 203 && blue == 71) {
					for (int i = 0; i < 20; i++) {
						click(x, y);
						TimeUnit.SECONDS.sleep(1);
					}
				}

				if (red == 102 && green == 255 && blue == 102) {
					openChest = false;
				}
			}
		}

		if (openChest) {
			click(1000, 800);
			click(950, 650);
		}

		int color = image.getRGB(1024, 223);
		int blue = color & 0xff;
		int green = (color & 0xff00) >> 8;
		int red = (color & 0xff0000) >> 16;
		if (red + blue + green == 765) {
			click(1000, 220);
			for (int i = 0; i < 10; i++) {
				click();
				TimeUnit.SECONDS.sleep(1);
			}
		}

	}

	public static void click(int x, int y) throws InterruptedException {
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		TimeUnit.MILLISECONDS.sleep(100);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		TimeUnit.MILLISECONDS.sleep(100);
	}

	public static void click() throws InterruptedException {
		robot.mousePress(InputEvent.BUTTON1_MASK);
		TimeUnit.MILLISECONDS.sleep(100);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		TimeUnit.MILLISECONDS.sleep(100);
	}

	public static void emote(int num) throws InterruptedException {
		click(750, 840);
		switch (num) {
		case 0:
			click(840, 800);
			break;
		case 1:
			click(960, 800);
			break;
		case 2:
			click(1080, 800);
			break;
		case 3:
			click(1200, 800);
			break;
		case 4:
			click(840, 860);
			break;
		case 5:
			click(1000, 860);
			break;
		case 6:
			click(1150, 860);
			break;
		case 7:
			click(840, 900);
			break;
		case 8:
			click(1000, 900);
			break;
		case 9:
			click(1150, 900);
			break;
		}

	}
}
