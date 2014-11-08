package bots;

import com.github.confiqure.input.Keyboard;
import com.github.confiqure.input.Mouse;
import com.github.confiqure.logic.Images;
import com.github.confiqure.util.Time;
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * Shared class between all bots.
 * 
 * @author Dylan Wheeler
 */
public abstract class Main {
    
    /**
     * Image indicating that Google Chrome has finished loading the page.
     */
    public BufferedImage chrome;

    /**
     * Image indicating NetBeans is open.
     */
    public BufferedImage netbeans;

    /**
     * Images class for comparing images and image analysis.
     */
    public Images images;

    /**
     * Mouse class for simulating Mouse input.
     */
    public Mouse mouse;

    /**
     * Keyboard class for simulating Keyboard input.
     */
    public Keyboard keyboard;
    
    /**
     * Creates new instance of Main. Loads the images and classes required. Begins thread to monitor NetBeans being opened.
     */
    public Main() {
        try {
            images = new Images();
            mouse = new Mouse();
            keyboard = new Keyboard();
            chrome = ImageIO.read(new File("resources/chrome.png"));
            netbeans = ImageIO.read(new File("resources/netbeans.png"));
        } catch (final AWTException | IOException ex) {}
        Time.sleep(2000);
        beginThread();
    }
    
    /**
     *
     */
    public void waitForChrome() {
        Time.sleep(500);
        while (!Images.equals(images.screenshot(67, 34, 16, 16), chrome)) {
            Time.sleep(1000);
        }
    }
    
    /**
     * Specify the code that will be executed upon finish.
     */
    public abstract void exit();
    
    private void beginThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Time.sleep(1000);
                    if (Images.equals(images.screenshot(86, 25, 93, 16), netbeans)) {
                        exit();
                        System.exit(0);
                    }
                }
            }
        }).start();
    }
    
}
