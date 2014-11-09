package bots;

import com.github.confiqure.logic.Images;
import com.github.confiqure.util.Time;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * Bot to play Thin Ice in Club Penguin.
 * 
 * @author Dylan Wheeler
 */
public class ThinIce extends Main {
    
    private Robot r;
    private Random rand;
    private BufferedImage yes, start, finish, coin, map, disconnect, code042, code052, code072, code082, code092, code102, newLevel, score;
    private final HashMap<Integer, char[]> levels = new HashMap<>();
    private int gamesPlayed = 0;
    private long startTime;
    private final Rectangle GAME_WINDOW = new Rectangle(177, 97, 900, 623);
    
    public ThinIce() {
        final long loadT = Time.millis();
        try {
            r = new Robot();
            rand = new Random();
            yes = loadImage("/resources/thinice/yes.png");
            start = loadImage("/resources/thinice/start.png");
            finish = loadImage("/resources/thinice/finish.png");
            coin = loadImage("/resources/thinice/coin.png");
            map = loadImage("/resources/thinice/map.png");
            disconnect = loadImage("/resources/thinice/disconnect.png");
            code042 = loadImage("/resources/thinice/042.png");
            code052 = loadImage("/resources/thinice/052.png");
            code072 = loadImage("/resources/thinice/072.png");
            code082 = loadImage("/resources/thinice/082.png");
            code092 = loadImage("/resources/thinice/092.png");
            code102 = loadImage("/resources/thinice/102.png");
            newLevel = loadImage("/resources/thinice/newLevel.png");
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/resources/thinice/levels.txt")))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] parts = line.split(":");
                    levels.put(Integer.parseInt(parts[0]), parts[1].toCharArray());
                }
                br.close();
            }
        } catch (final AWTException ex) {
            System.err.println("Unable to initialize Robot object");
            return;
        } catch (final IOException ex) {
            System.err.println("Unable to load reference images");
            return;
        }
        log("Loaded bot in " + (Time.millis() - loadT) + "ms");
        startMonitoring();
    }
    
    /**
     *
     * Main method.
     * 
     * @param args no arguments necessary
     */
    public static void main(final String[] args) {
        final ThinIce thinice = new ThinIce();
        Time.sleep(2000);
        thinice.startTime = Time.millis();
        log("Beginning macro sequence");
        while (true) {
            thinice.gameLoop();
        }
    }
    
    private void startMonitoring() {
        log("Initializing threads");
        new Thread(() -> {
            BufferedImage next, old = images.screenshot(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 40);
            while (true) {
                Time.sleep(600000);
                if (Images.equals((next = images.screenshot(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 40)), old)) {
                    try {
                        if (isProcessRunning("itunes.exe")) {
                            Toolkit.getDefaultToolkit().beep();
                        } else {
                            log("Screen has not changed for 10 minutes, forcing shut down");
                            ImageIO.write(next, "png", new File("shutdown.png"));
                            Runtime.getRuntime().exec("shutdown.exe -s");
                            exit();
                            System.exit(0);
                        }
                    } catch (final IOException ex) {}
                } else {
                    old = next;
                }
            }
        }).start();
    }
    
    private void gameLoop() {
        startGame();
        playGame();
        goMainScreen();
        gamesPlayed ++;
    }
    
    private void startGame() {
        long timeout;
        click(851, 479); //step away from game
        Time.sleep(500);
        click(956, 221); //step toward the game
        timeout = Time.millis() + 10000;
        while (!Images.contains(images.screenshot(GAME_WINDOW), yes)) {
            if (Time.millis() > timeout) {
                startGame();
                return;
            }
            Time.sleep(1000);
        }
        click(600, 387); //click yes
        timeout = Time.millis() + 10000;
        while (!Images.contains(images.screenshot(GAME_WINDOW), start)) {
            if (Time.millis() > timeout) {
                startGame();
                return;
            }
            Time.sleep(500);
        }
        Time.sleep(1000);
        click(658, 615); //click start
        Time.sleep(1000);
        click(440, 613); //click play
        Time.sleep(2000);
    }
    
    private void playGame() {
        int level = 1, code = level * 10 + 1;
        score = images.screenshot(613, 129, 40, 24);
        while (true) {
            if (level == 4 && Images.contains(images.screenshot(GAME_WINDOW), code042)) {
                code = 42;
            } else if (level == 5 && Images.contains(images.screenshot(GAME_WINDOW), code052)) {
                code = 52;
            } else if (level == 7 && Images.contains(images.screenshot(GAME_WINDOW), code072)) {
                code = 72;
            } else if (level == 8 && Images.contains(images.screenshot(GAME_WINDOW), code082)) {
                code = 82;
            } else if (level == 9 && Images.contains(images.screenshot(GAME_WINDOW), code092)) {
                code = 92;
            } else if (level == 10 && Images.contains(images.screenshot(GAME_WINDOW), code102)) {
                code = 102;
            } else {
                try {
                    ImageIO.write(images.screenshot(GAME_WINDOW), "png", new File("screen.png"));
                } catch (final IOException ex) {}
            }
            for (int i = 0; i < levels.get(code).length; i ++) {
                if ((levels.get(code)[i] + "").toUpperCase().equals(levels.get(code)[i] + "")) {
                    switch (levels.get(code)[i]) {
                        case 'L':
                            r.keyPress(KeyEvent.VK_LEFT);
                            break;
                        case 'R':
                            r.keyPress(KeyEvent.VK_RIGHT);
                            break;
                        case 'U':
                            r.keyPress(KeyEvent.VK_UP);
                            break;
                        case 'D':
                            r.keyPress(KeyEvent.VK_DOWN);
                            break;
                    }
                    Time.sleep(500);
                    BufferedImage tmp;
                    while (!Images.equals(tmp = images.screenshot(613, 129, 40, 24), score)) {
                        Time.sleep(200);
                        score = tmp;
                    }
                    switch (levels.get(code)[i]) {
                        case 'L':
                            r.keyRelease(KeyEvent.VK_LEFT);
                            break;
                        case 'R':
                            r.keyRelease(KeyEvent.VK_RIGHT);
                            break;
                        case 'U':
                            r.keyRelease(KeyEvent.VK_UP);
                            break;
                        case 'D':
                            r.keyRelease(KeyEvent.VK_DOWN);
                            break;
                    }
                    continue;
                }
                switch (levels.get(code)[i]) {
                    case 'l':
                        r.keyPress(KeyEvent.VK_LEFT);
                        Time.sleep(60);
                        r.keyRelease(KeyEvent.VK_LEFT);
                        break;
                    case 'r':
                        r.keyPress(KeyEvent.VK_RIGHT);
                        Time.sleep(60);
                        r.keyRelease(KeyEvent.VK_RIGHT);
                        break;
                    case 'u':
                        r.keyPress(KeyEvent.VK_UP);
                        Time.sleep(60);
                        r.keyRelease(KeyEvent.VK_UP);
                        break;
                    case 'd':
                        r.keyPress(KeyEvent.VK_DOWN);
                        Time.sleep(60);
                        r.keyRelease(KeyEvent.VK_DOWN);
                        break;
                }
                Time.sleep(220);
                final BufferedImage tmp = images.screenshot(613, 129, 40, 24);
                if (Images.equals(score, tmp)) {
                    break;
                } else {
                    score = tmp;
                }
            }
            Time.sleep(200);
            if (!Images.contains(images.screenshot(GAME_WINDOW), newLevel)) {
                if (level == 10 && Images.contains(images.screenshot(GAME_WINDOW), finish)) {
                    return;
                }
                if (levels.get(code + 1) != null) {
                    code ++;
                } else {
                    code = level * 10 + 1;
                }
                click(433, 645); //click reset
                Time.sleep(500);
            } else {
                level ++;
                code = level * 10 + 1;
            }
        }
    }
    
    private void goMainScreen() {
        long timeout;
        timeout = Time.millis() + 10000;
        while (!Images.contains(images.screenshot(GAME_WINDOW), finish)) {
            if (Time.millis() > timeout) {
                goMainScreen();
                return;
            }
            Time.sleep(500);
        }
        while (!Images.contains(images.screenshot(441, 202, 475, 351), coin)) {
            click(863, 636); //finish
            Time.sleep(1000);
        }
        while (!Images.contains(images.screenshot(GAME_WINDOW), map)) {
            click(881, 233); //exit
            Time.sleep(2000);
        }
    }
    
    private void click(final int x, final int y) {
        mouse.click(random(x - 5, x + 5), random(y - 5, y + 5));
    }
    
    private int random(final int x, final int y) {
        return (int) (rand.nextDouble() * (y - x)) + x;
    }

    private static String format(final long time) {
        final int sec = (int) (time / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
        return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
    }

    private static boolean isProcessRunning(final String name) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("tasklist").getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(name)) {
                    return true;
                }
            }
            reader.close();
        }
        return false;
    }
    
    private static void log(String msg) {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true))) {
            writer.write(msg = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] " + msg);
            writer.newLine();
            writer.close();
        } catch (IOException ex) {}
        System.out.println(msg);
    }
    
    @Override
    public void exit() {
        log("Detected force stop");
        final long elapsed = Time.millis() - startTime;
        log("Time Run: " + format(elapsed));
        log("Played " + gamesPlayed + " games, making " + new DecimalFormat("#,###").format(508 * gamesPlayed) + " money (" + (508 * gamesPlayed) / Math.ceil(elapsed / 60000D) + " per minute)");
    }
    
}
