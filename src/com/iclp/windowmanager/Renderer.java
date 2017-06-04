package com.iclp.windowmanager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Renderer extends Thread
{
    private Manager manager;
    private int fps;
    private ArrayList<DesktopCanvas> canvases;
    
    public static final int BORDER_HEIGHT = 20;
    public static final int TITLE_OFFSET = 10;
    public static final int CLOSE_BUTTON_SIZE = (int) (BORDER_HEIGHT * 0.6);
    public static final int CLOSE_BUTTON_OFFSET = 10;
    public static final int START_BAR_HEIGHT = 30;
    public static final int START_BUTTON_WIDTH = 100;
    public static final int START_TIME_WIDTH = 125;
    public static final int MAX_START_BAR_LENGTH = 125;
    
    public Renderer(Manager manager, int fps)
    {
        this.manager = manager;
        this.fps = fps;
        this.canvases = new ArrayList<>();
        
        for(Desktop desktop : manager.getDesktops())
        {
            canvases.add(new DesktopCanvas(desktop));
        }
    }
    
    public ArrayList<DesktopCanvas> getCanvases()
    {
        return this.canvases;
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            manager.lockForRender();
            
            for(DesktopCanvas canvas : this.canvases)
            {
                Desktop desktop = canvas.getDesktop();
                
                this.manager.lockDesktop(desktop);
                renderDesktop(desktop);
                this.manager.unlockDesktop(desktop);
                
                canvas.repaint();
            }
            
            manager.unlockForRender();
            
            try 
            {
                sleep(1000 / this.fps);
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void renderDesktop(Desktop desktop)
    {
        FrameBuffer frameBuffer = desktop.getBuffer();
        Graphics2D g = frameBuffer.beginRender();
        
        Color background = desktop.getBackground();
        g.setColor(background);
        g.fillRect(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        
        for(Window window : this.manager.getWindows(desktop))
        {
            FrameBuffer windowFrameBuffer = window.getBuffer();
            Graphics2D windowG = windowFrameBuffer.tryBeginRender();
            Rectangle windowRect = window.getRectangle();
            
            if(windowG == null)
            {
                continue;
            }
            
            g.drawImage(windowFrameBuffer.getBuffer(), windowRect.x, windowRect.y, null);
            g.setColor(Color.BLACK);
            g.drawRoundRect(windowRect.x, windowRect.y, windowRect.width, windowRect.height, 3, 3);
            
            g.setColor(Color.DARK_GRAY);
            g.fillRect(windowRect.x, windowRect.y, windowRect.width, BORDER_HEIGHT);
            
            //Title
            String title = window.getTitle();
            int fontHeight = Math.round(getTextHeight(windowG, title));
            int textMargin = BORDER_HEIGHT - fontHeight;
            int textY = fontHeight + textMargin / 2;
            
            g.setColor(Color.WHITE);
            g.drawString(window.getTitle(), windowRect.x + TITLE_OFFSET, windowRect.y + textY);
            
            //Close button
            Rectangle closeButtonRect = getCloseButtonRect(windowRect);
            
            g.setColor(Color.RED);
            g.fillOval(closeButtonRect.x, closeButtonRect.y, closeButtonRect.width, closeButtonRect.height);
            
            //Focus border
            if(manager.isFocused(window))
            {
                g.setColor(Color.YELLOW);
                Stroke oldStroke = g.getStroke();
                g.setStroke(new BasicStroke(3.5f));
                g.drawRoundRect(windowRect.x, windowRect.y, windowRect.width, windowRect.height, 3, 3);
                g.setStroke(oldStroke);
            }

            
            windowFrameBuffer.endRender(windowG);
        }
        
        //Start bar
        AffineTransform oldTransform = g.getTransform();
        g.setColor(Color.LIGHT_GRAY);
        g.translate(0, desktop.getBuffer().getHeight() - START_BAR_HEIGHT);
        g.fillRect(0, 0, desktop.getBuffer().getWidth(), START_BAR_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, desktop.getBuffer().getWidth(), START_BAR_HEIGHT);


        //Start button
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, START_BUTTON_WIDTH, START_BAR_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, START_BUTTON_WIDTH, START_BAR_HEIGHT);
        Font oldFont = g.getFont();
        g.setFont(new Font("TimesRoman", Font.PLAIN, 25)); 
        int fontHeight = Math.round(getTextHeight(g, "Start"));
        g.drawString("Start", TITLE_OFFSET, (int) (fontHeight * 0.8));
        
        //Start bar time
        Date currentDate = new Date();
        g.setColor(Color.BLACK);
        g.drawRect(desktop.getBuffer().getWidth() - START_TIME_WIDTH, 0, START_TIME_WIDTH, START_BAR_HEIGHT);
        g.drawString(String.format("%02d", currentDate.getHours()) 
                + ":" + String.format("%02d", currentDate.getMinutes()) 
                + ":" + String.format("%02d", currentDate.getSeconds()), desktop.getBuffer().getWidth() - START_TIME_WIDTH, (int) (fontHeight * 0.8));
        
        //Startbar applications
        g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        int xStart = START_BUTTON_WIDTH;
        int rectLength = getStartBarWindowLength(desktop.getBuffer().getWidth(), this.manager.getWindows(desktop).size());
        ArrayList<Window> windows = new ArrayList<>(this.manager.getWindows(desktop));
        Collections.sort(windows, new WindowNameComparator());
        for(Window window : windows)
        {
            g.setColor(new Color(230, 230, 230));
            g.fillRect(xStart, 0, rectLength, START_BAR_HEIGHT);
            
            g.setColor(Color.BLACK);
            g.drawRect(xStart, 0, rectLength, START_BAR_HEIGHT);
            
            if(manager.isFocused(window))
            {
                g.setColor(Color.YELLOW);
            }
                
            drawCenteredString(g, window.getTitle(), new Rectangle(xStart, 0, rectLength, START_BAR_HEIGHT));
            
            xStart += rectLength;
        }

        g.setTransform(oldTransform);
        
        frameBuffer.endRender(g);
    }
    
    public static Rectangle getCloseButtonRect(Rectangle windowRect)
    {
        int buttonMargin = BORDER_HEIGHT - CLOSE_BUTTON_SIZE;
        int buttonX = windowRect.x + windowRect.width - CLOSE_BUTTON_OFFSET - CLOSE_BUTTON_SIZE / 2;
        int buttonY = windowRect.y + buttonMargin / 2;
        
        return new Rectangle(buttonX, buttonY, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE);
    }
    
    private float getTextHeight(Graphics2D g2, String text)
    {
        FontRenderContext fcc = g2.getFontRenderContext();
        return g2.getFont().getLineMetrics(text, fcc).getHeight();
    }
    
    private int getStartBarWindowLength(int desktopWidth, int numOfWindows)
    {
        if(numOfWindows == 0)
        {
            return 0;
        }
        
        desktopWidth -= START_BUTTON_WIDTH + START_TIME_WIDTH;
        int length = desktopWidth / numOfWindows;
        if(length > MAX_START_BAR_LENGTH)
        {
            return MAX_START_BAR_LENGTH;
        }
        return length;
    }
    
    public void drawCenteredString(Graphics2D g, String text, Rectangle rect) 
    {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Draw the String
        g.drawString(text, x, y);
    }
    
    private class WindowNameComparator implements Comparator<Window>
    {
        @Override
        public int compare(Window o1, Window o2) 
        {
            return o1.getTitle().compareTo(o2.getTitle());
        }
        
    }
}
