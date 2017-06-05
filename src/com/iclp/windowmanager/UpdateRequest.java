package com.iclp.windowmanager;

public abstract class UpdateRequest
{
    private Window window;
    
    public UpdateRequest(Window window)
    {
        this.window = window;
    }
    
    public Window getWindow()
    {
        return this.window;
    }
    
    public abstract void update();
}
