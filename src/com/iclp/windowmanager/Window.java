package com.iclp.windowmanager;


public class Window extends Thread
{
    private Manager manager;
    private FrameBuffer buffer;
    
    public Window(Manager manager, Desktop desktop, String title, int width, int height)
    {
        this.manager = manager;
        this.buffer = new FrameBuffer(width, height);
        
        this.manager.add(this);
        this.manager.setRectangle(this, new Rectangle(0, 0, width, height));
        this.manager.setTitle(this, title);
        this.manager.setDesktop(this, desktop);
    }
    
    public Manager getManager()
    {
        return this.manager;
    }
    
    public void setTitle(String title)
    {
        this.manager.setTitle(this, title);
        //TODO: update request
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
        return this.manager.getRectangle(this);
    }
    
    public FrameBuffer getBuffer()
    {
        return this.buffer;
    }
    
    public int getWidth()
    {
        return this.buffer.getWidth();
    }
    
    public int getHeight()
    {
        return this.buffer.getHeight();
    }
}
