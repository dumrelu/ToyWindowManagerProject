package com.iclp.windowmanager.updates;

import com.iclp.windowmanager.Manager;
import com.iclp.windowmanager.Rectangle;
import com.iclp.windowmanager.UpdateRequest;
import com.iclp.windowmanager.Window;

public class UpdateWindowRectangle extends UpdateRequest
{
    private Rectangle rect;
    
    public UpdateWindowRectangle(Window window, Rectangle rect) 
    {
        super(window);
        
        this.rect = rect;
    }

    @Override
    public void update() 
    {
        Window window = getWindow();
        Manager manager = window.getManager();
        
        manager.setRectangle(window, this.rect);
        window.getBuffer().resize(rect.width, rect.height);
    }

    @Override
    public String toString() 
    {
        return "UpdateWindowRectangle{ window=" + getWindow().getTitle() + ", rect=" + rect + '}';
    }
}