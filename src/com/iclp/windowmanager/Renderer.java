package com.iclp.windowmanager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Renderer extends Thread
{
    private Manager manager;
    private int fps;
    private ArrayList<DesktopCanvas> canvases;
    
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
            
            //TODO: draw title bar
            
            //Focus border
            if(manager.isFocused(window))
            {
                g.setColor(Color.YELLOW);
                g.setStroke(new BasicStroke(3.5f));
                g.drawRect(windowRect.x, windowRect.y, windowRect.width, windowRect.height);
            }
            
            windowFrameBuffer.endRender(g);
        }
        
        frameBuffer.endRender(g);
    }
}
