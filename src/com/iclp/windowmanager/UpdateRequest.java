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
        Manager manager = this.window.getManager();
        
        if(!manager.canUpdate(window))
        {
            //Retry the update at a later time
            manager.update(this);
            return;
        }
        
        manager.lockForUpdate();
        update();
        manager.unlockForUpdate();
        
        manager.requestProcessed(this);
    }
    
    protected abstract void update();
}
