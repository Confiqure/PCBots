package bots;

import com.github.confiqure.logic.Images;
import com.github.confiqure.util.Time;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * Bot to respond on Gaia forums with auto-generated responses.
 * 
 * @author Dylan Wheeler
 */
public class Gaia extends Main {
    
    private final ArrayList<String> initial = new ArrayList<>(), proceeding = new ArrayList<>(), core = new ArrayList<>(), ending = new ArrayList<>(), emoticon = new ArrayList<>();
    private final ArrayList<String> adjective = new ArrayList<>(), extra = new ArrayList<>();
    private final ArrayList<String> forums = new ArrayList<>();
    private BufferedImage submit, submit2, reply;
    
    /**
     * Creates new instance of Gaia.
     */
    public Gaia() {
        try {
            submit = loadImage("/resources/gaia/submit.png");
            submit2 = loadImage("/resources/gaia/submit2.png");
            reply = loadImage("/resources/gaia/reply.png");
            populateResponses();
            populateTopics();
        } catch (final IOException ex) {}
        System.out.println("Loaded " + forums.size() + " topics with over " + (initial.size() * proceeding.size() * core.size() * ending.size() * emoticon.size() * 2) + " possible responses");
    }
    
    /**
     *
     * Main method.
     * 
     * @param args no arguments necessary
     */
    public static void main(final String[] args) {
        final Gaia gaia = new Gaia();
        gaia.execute();
    }
    
    private void execute() {
        for (final String forum : forums) {
            try {
                Desktop.getDesktop().browse(URI.create("http://www.gaiaonline.com" + forum));
            } catch (final IOException ex) {
                System.err.println("Unable to open forum URL: " + forum);
                return;
            }
            Point button;
            while ((button = Images.getContainsPoint(images.screenshot(), reply)) == null) {
                Time.sleep(1000);
            }
            mouse.click(button.x, button.y);
            Time.sleep(500);
            mouse.click(button.x, button.y + 100);
            Time.sleep(500);
            final String response = generateResponse();
            keyboard.paste(response);
            button = Images.getContainsPoint(images.screenshot(), submit) == null ? Images.getContainsPoint(images.screenshot(), submit2) : Images.getContainsPoint(images.screenshot(), submit);
            while (button == null) {
                System.err.println("User action required");
                Toolkit.getDefaultToolkit().beep();
                Time.sleep(5000);
                button = Images.getContainsPoint(images.screenshot(), submit) == null ? Images.getContainsPoint(images.screenshot(), submit2) : Images.getContainsPoint(images.screenshot(), submit);
            }
            mouse.click(button.x, button.y);
            Time.sleep(1000);
            waitForChrome();
            keyboard.comboType(KeyEvent.VK_CONTROL, KeyEvent.VK_W);
            System.out.println("Responded saying: " + response);
            Time.sleep(30000 + ((int) (new Random().nextDouble() * 30000)));
        }
    }
    
    private void populateResponses() throws IOException {
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/resources/gaia/responses.txt")))) {
            String line;
            int mode = 0;
            while ((line = br.readLine()) != null) {
                switch (line) {
                    case "INITIAL":
                        mode = 0;
                        break;
                    case "PROCEEDING":
                        mode = 1;
                        break;
                    case "CORE":
                        mode = 2;
                        break;
                    case "ENDING":
                        mode = 3;
                        break;
                    case "EMOTICON":
                        mode = 4;
                        break;
                    case "ADJECTIVE":
                        mode = 5;
                        break;
                    case "EXTRA":
                        mode = 6;
                        break;
                    default:
                        switch (mode) {
                            case 0:
                                initial.add(line);
                                break;
                            case 1:
                                proceeding.add(line);
                                break;
                            case 2:
                                core.add(line);
                                break;
                            case 3:
                                ending.add(line);
                                break;
                            case 4:
                                emoticon.add(" " + line);
                                break;
                            case 5:
                                adjective.add(line);
                                break;
                            case 6:
                                extra.add(line);
                                break;
                        }
                }
            }
            br.close();
        }
        int probabilityBalance = initial.size() * 4 / 3;
        for (int i = 0; i < probabilityBalance; i ++) {
            initial.add("");
        }
        probabilityBalance = proceeding.size() * 3 / 2;
        for (int i = 0; i < probabilityBalance; i ++) {
            proceeding.add("");
        }
        probabilityBalance = ending.size() * 5 / 4;
        for (int i = 0; i < probabilityBalance; i ++) {
            ending.add("");
        }
        probabilityBalance = emoticon.size() * 5;
        for (int i = 0; i < probabilityBalance; i ++) {
            emoticon.add("");
        }
    }
    
    private void populateTopics() throws IOException {
        for (final String forum : new String[]
                {"/forum/in-the-news/f.711/", "/forum/chatterbox/f.23/", "/forum/gaming-discussion/f.4/",
                "/forum/in-the-news/f.711/?sequence=41", "/forum/chatterbox/f.23/?sequence=41", "/forum/gaming-discussion/f.4/?sequence=41"}) {
            final URLConnection spoof = new URL("http://www.gaiaonline.com" + forum).openConnection();
            String total = "";
            spoof.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)");
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()))) {
                String str;
                while ((str = in.readLine()) != null) {
                    total += str;
                }
                in.close();
            }
            total = total.substring(total.indexOf("<div class=\"one-line-title\">") + 1);
            for (String chunk : total.split("<div class=\"one-line-title\">")) {
                if (chunk.contains("Admin") || chunk.contains("[ Poll ]") || chunk.contains("<td class=\"replies\">5</td>")) {
                    continue;
                }
                chunk = chunk.substring(chunk.indexOf("href=\"") + 6);
                forums.add(chunk.substring(0, chunk.indexOf("\"")));
            }
        }
        Collections.shuffle(forums, new Random(System.nanoTime()));
    }
    
    private String generateResponse() {
        String init = initial.get((int) (new Random().nextDouble() * initial.size())), proc = "";
        boolean end = false;
        if (!init.isEmpty()) {
            if ((int) (new Random().nextDouble() * 3) == 0) {
                if ((int) (new Random().nextDouble() * 3) == 0) {
                    proc = init + proceeding.get((int) (new Random().nextDouble() * proceeding.size())) + " ";
                    end = true;
                } else {
                    proc = "";
                    end = true;
                }
            } else {
                proc = init + proceeding.get((int) (new Random().nextDouble() * proceeding.size())) + " ";
            }
        }
        String build = proc + core.get((int) (new Random().nextDouble() * core.size())) + (end ? " " + init : "") + ending.get((int) (new Random().nextDouble() * ending.size())) + emoticon.get((int) (new Random().nextDouble() * emoticon.size()));
        if ((int) (new Random().nextDouble() * 2) == 0) {
            build = build.replace("I am", "I'm").replace("I have", "I've").replace("have not", "haven't").replace("do not", "don't").replace("you are", "you're");
        }
        build = build.replace("[e]", extra.get((int) (new Random().nextDouble() * extra.size()))).replace("[a]", adjective.get((int) (new Random().nextDouble() * adjective.size())));
        return build.substring(0, 1).toUpperCase() + build.substring(1);
    }

    @Override
    public void exit() {}
    
}
