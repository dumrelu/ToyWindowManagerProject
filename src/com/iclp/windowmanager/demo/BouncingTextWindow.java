package com.iclp.windowmanager.demo;

import com.iclp.windowmanager.Desktop;
import com.iclp.windowmanager.Manager;
import com.iclp.windowmanager.Rectangle;
import com.iclp.windowmanager.Renderer;
import com.iclp.windowmanager.Window;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BouncingTextWindow extends Window 
{
    private String bounceText;
    private double textX, textY;
    private double dirX, dirY;
    private double velocity;
    
    private static final int UPDATE_SPEED = 50;         //ms
    private static final double MIN_VELOCITY = 50.0;    //per second
    private static final double MAX_VELOCITY = 150.0;   //per second
    
    public BouncingTextWindow(Manager manager, Desktop desktop, String title, int width, int height, String bounceText) 
    {
        super(manager, desktop, title, width, height);
        
        this.bounceText = bounceText;
        this.textX = width / 4;
        this.textY = height / 4;
        this.dirX = (double) Math.round(Math.random() * 100) / 100;
        this.dirY = (double) Math.round(Math.random() * 100) / 100;
        this.velocity = (MIN_VELOCITY + Math.random() * (MAX_VELOCITY - MIN_VELOCITY)) / ((1000 / UPDATE_SPEED ));
    }
    
    @Override
    public void run()
    {
        while(!isClosed())
        {
            Graphics2D g2 = getBuffer().beginRender();
            g2.setFont(new Font("TimesRoman", Font.PLAIN, 25)); 
            
            //Check if text is still inside the window
            Rectangle textRect = getTextRect(g2, bounceText);
            Rectangle windowRect = new Rectangle(0, Renderer.BORDER_HEIGHT, getWidth(), getHeight());
            if(!windowRect.contains(textRect))
            {
                if(textRect.x <= windowRect.x || textRect.x + textRect.width >= windowRect.x + windowRect.width)
                {
                    dirX *= -1;
                }
                else
                {
                    dirY *= -1;
                }
            }
            
            //Advance
            textX += dirX * velocity;
            textY += dirY * velocity;
            
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.setColor(Color.BLACK);
            g2.drawString(bounceText, (float) textX, (float) textY);
            
            getBuffer().endRender(g2);
            
            try {
                sleep(UPDATE_SPEED);
            } catch (InterruptedException ex) {
                Logger.getLogger(BouncingTextWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private Rectangle getTextRect(Graphics2D g2, String text)
    {
        FontMetrics metrics = g2.getFontMetrics();
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();
        
        return new Rectangle((int) textX, (int) textY, width, height);
    }
}
