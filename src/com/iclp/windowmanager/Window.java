package com.iclp.windowmanager;


public class Window extends Thread
{
    private Manager manager;
    private FrameBuffer buffer;
    
    public Window(Manager manager, Desktop desktop, int width, int height)
    {
        this.manager = manager;
        this.buffer = new FrameBuffer(width, height);
        
        this.manager.add(this);
        this.manager.setDesktop(this, desktop);
    }
    
    public Manager getManager()
    {
        return this.manager;
    }
    
    public String setTitle(String title)
    {
        this.manager.setTitle(this, title);
    }
    
    public String getTitle()
    {
        return this.manager.getTitle(this);
    }
    
    public void setDesktop(Desktop desktop)
    {
        this.manager.setDesktop(this, desktop);
    }
    
    public Desktop getDesktop()
    {
        return this.manager.getDesktop(this);
    }
    
    public void setRectangle(Rectangle rectangle)
    {
        this.manager.setRectangle(this, rectangle);
    }
    
    public Rectangle getRectangle()
    {
        return this.manager.getRectangle();
    }
    
    public FrameBuffer getBuffer()
    {
        return this.buffer;
    }
}
