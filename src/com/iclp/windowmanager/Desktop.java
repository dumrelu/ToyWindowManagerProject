package com.iclp.windowmanager;

import java.awt.Color;


public class Desktop 
{
    private Manager manager;
    private FrameBuffer buffer;
    private Color background;
    
    public Desktop(Manager manager, Color background, int width, int height)
    {
        this.manager = manager;
        this.buffer = new FrameBuffer(width, height);
        this.background = background;
        
        this.manager.add(this);
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
    
    public void setBackground(Color background)
    {
        this.background = background;
    }
    
    public Color getBackground()
    {
        return this.background;
    }
    
    public FrameBuffer getBuffer()
    {
        return this.buffer;
    }
}
