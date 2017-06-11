package com.iclp.windowmanager;

import com.iclp.windowmanager.updates.UpdateWindowDesktop;
import com.iclp.windowmanager.updates.UpdateWindowRectangle;
import com.iclp.windowmanager.updates.UpdateWindowTitle;

//TODO: 
//      - focus/isFocused
public class Window extends Thread
{
    private Manager manager;
    private FrameBuffer buffer;
    private boolean closed = false;
    
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
        this.manager.update(new UpdateWindowTitle(this, title));
    }
    
    public String getTitle()
    {
        return this.manager.getTitle(this);
    }
    
    public void setDesktop(Desktop desktop)
    {
        this.manager.update(new UpdateWindowDesktop(this, desktop));
    }
    
    public Desktop getDesktop()
    {
        return this.manager.getDesktop(this);
    }
    
    public void setRectangle(Rectangle rectangle)
    {
        this.manager.update(new UpdateWindowRectangle(this, rectangle));
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
    
    public int getX()
    {
        return this.getRectangle().x;
    }
    
    public int getY()
    {
        return this.getRectangle().y;
    }
    
    public boolean isClosed()
    {
        return closed;
    }
    
    protected synchronized void onCloseButton()
    {
        this.manager.remove(this);
        closed = true;
    }
    
    protected synchronized void onClick(int x, int y)
    {
        this.manager.getLogger().log(Logger.DEBUG, "Window \"" + getTitle() + "\" clicked at: ("
                                                    + x + ", " + y + ")");
    }
}
