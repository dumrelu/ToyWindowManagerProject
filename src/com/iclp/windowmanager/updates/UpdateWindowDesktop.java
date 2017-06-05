package com.iclp.windowmanager.updates;

import com.iclp.windowmanager.Desktop;
import com.iclp.windowmanager.Manager;
import com.iclp.windowmanager.UpdateRequest;
import com.iclp.windowmanager.Window;

public class UpdateWindowDesktop extends UpdateRequest
{
    private Desktop desktop;
    
    public UpdateWindowDesktop(Window window, Desktop desktop) 
    {
        super(window);
        
        this.desktop = desktop;
    }

    @Override
    protected void update() 
    {
        Window window = getWindow();
        Manager manager = window.getManager();
        
        manager.setDesktop(window, this.desktop);
    }

    @Override
    public String toString() 
    {
        return "UpdateWindowDesktop{" + "desktop=" + desktop.getName() + '}';
    }
    
    
}