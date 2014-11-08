package bots;

import com.github.confiqure.logic.Images;
import com.github.confiqure.util.Time;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * Bot to repeat rounds of BTD5.
 * 
 * @author Dylan Wheeler
 */
public class Bloons extends Main {
    
    private BufferedImage play, load, go, menu, offerMenu, rankup;
    private int gamesPlayed = 0, offersClosed = 0, ranks = 0;
    
    /**
     * Creates new instance of Bloons.
     */
    public Bloons() {
        try {
            play = ImageIO.read(new File("resources/bloons/play.png"));
            load = ImageIO.read(new File("resources/bloons/load.png"));
            go = ImageIO.read(new File("resources/bloons/go.png"));
            menu = ImageIO.read(new File("resources/bloons/menu.png"));
            offerMenu = ImageIO.read(new File("resources/bloons/offerMenu.png"));
            rankup = ImageIO.read(new File("resources/bloons/rankup.png"));
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
        final Bloons bloons = new Bloons();
        Time.sleep(2000);
        while (true) {
            bloons.gameLoop();
        }
    }
    
    private void gameLoop() {
        startGame();
        playGame();
        goMainScreen();
        gamesPlayed ++;
    }
    
    private void startGame() {
        long timeout;
        mouse.click(881, 569); //PLAY
        timeout = Time.millis() + 5000;
        /*while (!equals(r.createScreenCapture(new Rectangle(618, 384, 40, 40)), load)) {
            if (Time.millis() > timeout) {
                startGame();
                return;
            }
            Time.sleep(500);
        }*/
        Time.sleep(1000);
        mouse.click(571, 407); //LOAD
        timeout = Time.millis() + 10000;
        while (!Images.contains(images.screenshot(), go)) {
            if (Time.millis() > timeout) {
                startGame();
                return;
            }
            Time.sleep(500);
        }
    }
    
    private void playGame() {
        long timeout;
        mouse.click(935, 601); //GO
        Time.sleep(250);
        mouse.click(935, 601); //FF
        timeout = Time.millis() + 30000;
        while (!Images.contains(images.screenshot(), menu)) {
            if (Time.millis() > timeout) {
                playGame();
                return;
            }
            if (Images.contains(images.screenshot(), rankup)) {
                mouse.click(643, 543);
                ranks ++;
            }
            Time.sleep(500);
        }
    }
    
    private void goMainScreen() {
        long timeout;
        mouse.click(380, 456); //MENU
        timeout = Time.millis() + 5000;
        while (!Images.contains(images.screenshot(), play)) {
            if (Time.millis() > timeout) {
                goMainScreen();
                return;
            }
            if (Images.contains(images.screenshot(), offerMenu)) {
                mouse.click(854, 297); //CLOSE
                offersClosed ++;
                Time.sleep(1000);
                return;
            }
            Time.sleep(500);
        }
    }

    @Override
    public void exit() {
        System.out.println("Played " + gamesPlayed + " games");
        System.out.println("Closed " + offersClosed + " offers");
        System.out.println("Earned " + (gamesPlayed * 2866) + " experience, gaining " + ranks + " ranks");
    }
    
}
