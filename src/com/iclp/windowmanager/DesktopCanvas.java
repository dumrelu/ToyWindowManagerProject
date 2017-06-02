package com.iclp.windowmanager;

import java.awt.Canvas;
import java.awt.Graphics;
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
}
