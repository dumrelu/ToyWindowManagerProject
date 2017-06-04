/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iclp.windowmanager;

/**
 *
 * @author relu
 */
public interface ManagerListener 
{
    public void onWindowAdded(Window window);
    public void onWindowRemoved(Window window);
    public void onWindowFocused(Desktop desktop, Window window);
    public void onWindowUnfocused(Desktop desktop);
    
    public void onDesktopAdded(Desktop desktop);
    public void onDesktopNameChanged(Desktop desktop, String newName, String oldName);
    
    public void onWindowTitleChanged(Window window, String newTitle, String oldTitle);
    public void onWindowRectangleChanged(Window window, Rectangle newRect, Rectangle oldRect);
    public void onWindowDesktopChanged(Window window, Desktop newDesktop, Desktop oldDesktop);
    
    public void onUpdateRequestAdded(UpdateRequest request);
    public void onUpdateRequestExecuted(UpdateRequest request);
    
    public void onUpdatesPaused();
    public void onUpdatesPaused(Desktop desktop);
    public void onUpdatesPaused(Window window);
    public void onUpdatesResumed();
    public void onUpdatesResumed(Desktop desktop);
    public void onUpdatesResumed(Window window);
}
