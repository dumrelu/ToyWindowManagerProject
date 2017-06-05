package com.iclp.windowmanager;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReentrantLock;

public class FrameBuffer 
{
    private BufferedImage buffer;
    private ReentrantLock lock;
    
    public FrameBuffer(int width, int height)
    {
        this.lock = new ReentrantLock();
        resize(width, height);
    }
    
    public int getWidth()
    {
        return this.buffer.getWidth();
    }
    
    public int getHeight()
    {
        return this.buffer.getHeight();
    }
    
    public void resize(int width, int height)
    {
        if(this.buffer != null && width == getWidth() && height == getHeight())
        {
            return;
        }
        
        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
    
    public ReentrantLock getLock()
    {
        return this.lock;
    }
    
    public BufferedImage getBuffer()
    {
        return this.buffer;
    }
    
    public Graphics2D beginRender()
    {
        this.lock.lock();
        return this.buffer.createGraphics();
    }
    
    public Graphics2D tryBeginRender()
    {
        if(!this.lock.tryLock())
        {
            return null;
        }
        return this.buffer.createGraphics();
    }
    
    public void endRender(Graphics2D graphics)
    {
        graphics.dispose();
        this.lock.unlock();
    }
}
