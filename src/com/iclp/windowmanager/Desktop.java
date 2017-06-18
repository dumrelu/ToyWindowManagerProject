package com.iclp.windowmanager;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Desktop 
{
    private Manager manager;
    private FrameBuffer buffer;
    private BufferedImage background;
    
    public Desktop(Manager manager, String name, String backgroundImagePath, int width, int height) throws IOException
    {
        this.manager = manager;
        this.buffer = new FrameBuffer(width, height);
        this.background = ImageIO.read(new File(backgroundImagePath));
        
        this.manager.add(this);
        this.manager.setName(this, name);
    }
    
    public Manager getManager()
    {
        return this.manager;
    }
    
    public void setName(String name)
    {
        this.manager.setName(this, name);
    }
    
    public String getName()
    {
        return this.manager.getName(this);
    }
    
    public void setBackground(BufferedImage background)
    {
        this.background = background;
    }
    
    public BufferedImage getBackground()
    {
        return this.background;
    }
    
    public FrameBuffer getBuffer()
    {
        return this.buffer;
    }
}
