package com.iclp.windowmanager;

public abstract class UpdateRequest implements Runnable
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
    
    @Override
    public void run()
    {
        //TODO: 
    }
    
    protected abstract void update();
}
