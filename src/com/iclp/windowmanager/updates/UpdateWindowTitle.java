package com.iclp.windowmanager.updates;

import com.iclp.windowmanager.Manager;
import com.iclp.windowmanager.UpdateRequest;
import com.iclp.windowmanager.Window;

public class UpdateWindowTitle extends UpdateRequest
{
    private String title;
    
    public UpdateWindowTitle(Window window, String title) 
    {
        super(window);
        
        this.title = title;
    }

    @Override
    public void update() 
    {
        Window window = getWindow();
        Manager manager = window.getManager();
        
        manager.setTitle(window, this.title);
    }

    @Override
    public String toString() 
    {
        return "UpdateWindowTitle{" + "title=" + title + '}';
    }
}
