package undercast.client.achievements;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.Achievement;
import net.minecraft.src.mod_Undercast;

/**
 * @author Flv92
 */
public class UndercastKillsHandler {

    public static BufferedImage killerBuffer = null;
    public static BufferedImage steveHeadBuffer = null;
    private String killer;
    private boolean killOrKilled;

    public UndercastKillsHandler(String message, String username, EntityPlayer player) {
      //When you die from someone
        if (mod_Undercast.CONFIG.showDeathAchievements && message.startsWith(username) && !message.toLowerCase().endsWith(" team") && (message.contains(" by ") || message.contains(" took ") || message.contains("fury of"))) {
            killer = message.substring(message.indexOf("by") + 3, message.lastIndexOf("'s") == -1 ? message.length() : message.lastIndexOf("'s"));
            killOrKilled = false;
            this.printAchievement();
        } //if you kill a person
        else if (mod_Undercast.CONFIG.showKillAchievements && message.contains("by " + username) || message.contains("took " + username) || message.contains("fury of " + username)) {
            System.out.println(message.substring(0, message.indexOf(" ")));
            killer = message.substring(0, message.indexOf(" "));
            killOrKilled = true;
            this.printAchievement();
        }
        //when you die, but nobody killed you.
        else if (mod_Undercast.CONFIG.showDeathAchievements && message.startsWith(username) && !message.toLowerCase().endsWith(" team")) {
            killer = username;
            killOrKilled = false;
            this.printAchievement();
        }
    }

    private void printAchievement() {
        killerBuffer = steveHeadBuffer;
        //Thread charged to load the achievment gui
        Runnable r1 = new Runnable() {
            public void run() {
                URLConnection spoof = null;
                try {
                    System.out.println("Beginning");
                    spoof = new URL("https://minotar.net/helm/" + killer + "/16.png").openConnection();
                    spoof.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
                    killerBuffer = ((BufferedImage) ImageIO.read(spoof.getInputStream()));
                    System.out.println("finished");
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        };
        Runnable r2 = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000L);
                    Achievement custom = (new Achievement(27, "custom", 1, 4, Item.ingotIron, (Achievement) null));
                    UndercastGuiAchievement gui = new UndercastGuiAchievement(Minecraft.getMinecraft());
                    Minecraft.getMinecraft().guiAchievement = gui;
                    gui.addFakeAchievementToMyList(custom, killOrKilled, killer);
                } catch (InterruptedException ex) {
                    Logger.getLogger(UndercastKillsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        t2.start();

    }
}