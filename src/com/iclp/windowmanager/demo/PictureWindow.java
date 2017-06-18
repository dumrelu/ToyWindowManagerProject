package com.iclp.windowmanager.demo;

import com.iclp.windowmanager.Desktop;
import com.iclp.windowmanager.Manager;
import com.iclp.windowmanager.Window;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class PictureWindow extends Window
{
    private String imagePath;
    
    public PictureWindow(Manager manager, Desktop desktop, String title, int width, int height, String imagePath) {
        super(manager, desktop, title, width, height);
        
        this.imagePath = imagePath;
    }
    
    @Override
    public void run()
    {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            Logger.getLogger(PictureWindow.class.getName()).log(Level.SEVERE, null, ex);
            onCloseButton();
        }
        
        Graphics2D g = getBuffer().beginRender();
        g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        getBuffer().endRender(g);
    }
}
