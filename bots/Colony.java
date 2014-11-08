package bots;

import com.github.confiqure.logic.Images;
import com.github.confiqure.util.Time;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * Bot to play Colony games via the hacked client.
 * 
 * @author Dylan Wheeler
 */
public class Colony extends Main {
    private BufferedImage playerList, duplicateSlots, groundZero;
    private int gamesPlayed = 0;
    private final String[] games = {"norm", "normal", "eq", "earthquake", "join", "join all", "join plz", "blah", "cold war", "dogfight", "missles",
        "yolo", "pros", "pros only", "winners", "win", "awesome", "bored", "fun", "funny", "click", "click me", "click here", "click now", "epic", "game",
        "amazing", "cool", "boredom", "lets do this", "beep", "yum", "love", "life", "do this", "this is it", "final", "boiii", "pwnage", "secret", "shh"};
    
    /**
     * Creates new instance of Colony.
     */
    public Colony() {
        try {
            playerList = ImageIO.read(new File("resources/colony/playerList.png"));
            duplicateSlots = ImageIO.read(new File("resources/colony/duplicateSlots.png"));
            groundZero = ImageIO.read(new File("resources/colony/groundZero.png"));
        } catch (final IOException ex) {
            System.err.println("Unable to load reference images");
        }
    }
    
    /**
     * 
     * Main method.
     * 
     * @param args no arguments necessary
     */
    public static void main(final String[] args) {
        final Colony colony = new Colony();
        Time.sleep(2000);
        long last = Time.millis();
        while (true) {
            colony.gameLoop();
            System.out.println("Completed game in " + ((Time.millis() - last) / 1000) + " seconds");
            last = Time.millis();
        }
    }
    
    private void gameLoop() {
        long timeout;
        makeGame();
        Time.sleep(1000);
        timeout = Time.millis() + 240000;
        while (Images.contains(images.screenshot(300, 500, 100, 100), playerList) && Time.millis() < timeout) {
            Time.sleep(500);
        }
        while (true) {
            enterSlots();
            mouse.click(800, 620);
            Time.sleep(300);
            if (Images.contains(images.screenshot(700, 300, 150, 50), duplicateSlots)) {
                mouse.click(893, 283);
                Time.sleep(200);
            } else {
                break;
            }
        }
        timeout = Time.millis() + 60000;
        while (!Images.contains(images.screenshot(400, 300, 100, 100), groundZero) && Time.millis() < timeout) {
            Time.sleep(1000);
        }
        if (!Images.contains(images.screenshot(400, 300, 100, 100), groundZero)) {
            System.exit(0);
        }
        finishGame();
        Time.sleep(200);
        gamesPlayed ++;
    }
    
    private void makeGame() {
        mouse.click(461, 561);
        Time.sleep(200);
        mouse.click(494, 314);
        Time.sleep(100);
        keyboard.paste(games[(int) (new Random().nextDouble() * games.length)]);
        Time.sleep(100);
        mouse.click(494, 368);
        Time.sleep(200);
    }
    
    private void enterSlots() {
        mouse.click(419, 453);
        Time.sleep(100);
        keyboard.type(KeyEvent.VK_H);
        Time.sleep(100);
        mouse.click(419, 421);
        Time.sleep(100);
        keyboard.type(KeyEvent.VK_H);
        Time.sleep(100);
        mouse.click(419, 287);
        Time.sleep(100);
        keyboard.type(KeyEvent.VK_H);
        Time.sleep(100);
        mouse.click(419, 253);
        Time.sleep(100);
        keyboard.type(KeyEvent.VK_T);
        Time.sleep(100);
    }
    
    private void finishGame() {
        mouse.click(349, 333);
        Time.sleep(200);
        mouse.click(711, 617);
        Time.sleep(1000);
        mouse.click(687, 338);
        Time.sleep(200);
        mouse.click(673, 526);
    }

    @Override
    public void exit() {
        System.out.println("Played a total of " + gamesPlayed + " games");
    }
    
}
