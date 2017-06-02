package com.iclp.windowmanager;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class DesktopCanvas extends Canvas
{
    private Desktop desktop;
    private BufferedImage buffer;
    private Window selectedWindow = null;
    private boolean isMosePressed = false;
    
    public DesktopCanvas(Desktop desktop)
    {
        this.desktop = desktop;
        this.buffer = new BufferedImage(desktop.getBuffer().getWidth(), desktop.getBuffer().getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        setSize(this.buffer.getWidth(), this.buffer.getHeight());
        
        //TODO: listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                onMousePressed(e.getX(), e.getY());
            }
        });
    }
    
    public Desktop getDesktop()
    {
        return this.desktop;
    }
    
    @Override
    public void update(Graphics g)
    {
        paint(g);
    }
    
    @Override
    public void paint(Graphics g)
    {
        Graphics bufferGraphics = this.buffer.getGraphics();
        onPaint(bufferGraphics);
        
        g.drawImage(this.buffer, 0, 0, null);
    }
    
    private void onPaint(Graphics g)
    {
        FrameBuffer frameBuffer = this.desktop.getBuffer();
        frameBuffer.getLock().lock();
        
        g.drawImage(frameBuffer.getBuffer(), 0, 0, null);
        
        frameBuffer.getLock().unlock();
    }
    
    public void onMousePressed(int x, int y)
    {
        Manager manager = this.desktop.getManager();
        
        manager.getLogger().log(Logger.DEBUG, "Mouse click on desktop \"" +
                this.desktop.getName() + "\" at coordinates: (" + 
                x + ", " + y + ")");
        
        Window clickedWindow = findClickedWindow(x, y);
        
        if(this.selectedWindow != null)
        {
            manager.resumeUpdates(this.selectedWindow);
            this.selectedWindow = null;
        }
        
        if(clickedWindow != null)
        {
            this.selectedWindow = clickedWindow;
            manager.getLogger().log(Logger.DEBUG, "Window selected: " + this.selectedWindow.getTitle());
        }
        
        this.isMosePressed = true;
    }

    private Window findClickedWindow(int x, int y) 
    {
        Manager manager = this.desktop.getManager();
        Window clickedWindow = null;
        
        manager.lockDesktop(this.desktop);
        
        for(Window window : manager.getWindows(desktop))
        {
            manager.pauseUpdates(window);
            if(window.getRectangle().intersects(x, y))
            {
                clickedWindow = window;
                break;
            }
            manager.resumeUpdates(window);
        }
        
        manager.unlockDesktop(this.desktop);
        return clickedWindow;
    }
}
