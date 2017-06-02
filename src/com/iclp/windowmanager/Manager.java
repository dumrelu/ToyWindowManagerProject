package com.iclp.windowmanager;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Manager
{
    private class WindowInfo
    {
        String title = "";
        Rectangle rect = new Rectangle();
        ReentrantLock lock = new ReentrantLock();
    }
    
    private class DesktopInfo
    {
        String name = "";
        ArrayList<Window> windows = new ArrayList<>();
        ReentrantLock lock = new ReentrantLock();
    }
    
    private ConcurrentHashMap<Desktop, DesktopInfo> desktops;
    private ConcurrentHashMap<Window, WindowInfo> windows;
    private Logger logger;
    private ExecutorService threadPool;
    private ReentrantReadWriteLock updateLock;
    
    public Manager(int numOfThreads)
    {
        this.desktops = new ConcurrentHashMap<>();
        this.windows = new ConcurrentHashMap<>();
        this.logger = new Logger();
        this.threadPool = Executors.newFixedThreadPool(numOfThreads);
        this.updateLock = new ReentrantReadWriteLock();
    }
    
    public void add(Window window)
    {
        this.windows.put(window, new WindowInfo());
    }
    
    public void setTitle(Window window, String title)
    {
        WindowInfo info = this.windows.get(window);
        
        synchronized(info)
        {
            info.title = title;
        }
    }
    
    public String getTitle(Window window)
    {
        WindowInfo info = this.windows.get(window);
        
        synchronized(info)
        {
            return info.title;
        }
    }
    
    public void setRectangle(Window window, Rectangle rect)
    {
        WindowInfo info = this.windows.get(window);
        
        synchronized(info)
        {
            info.rect = rect;
        }
    }
    
    public Rectangle getRectangle(Window window)
    {
        WindowInfo info = this.windows.get(window);
        
        synchronized(info)
        {
            return info.rect;
        }
    }
    
    
    
    public void add(Desktop desktop)
    {
        this.desktops.put(desktop, new DesktopInfo());
    }
    
    public void setName(Desktop desktop, String name)
    {
        DesktopInfo info = this.desktops.get(desktop);
        
        synchronized(info)
        {
            info.name = name;
        }
    }
    
    public String getName(Desktop desktop)
    {
        DesktopInfo info = this.desktops.get(desktop);
        
        synchronized(info)
        {
            return info.name;
        }
    }
    
    
}
