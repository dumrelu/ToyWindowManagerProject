package com.iclp.windowmanager;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//TODO: 
//      - swapDesktops(watch out for the focused window information)
public class Manager
{
    private class WindowUpdateExecutor implements Runnable
    {
        private Manager manager;
        private Window window;
        private ArrayList<UpdateRequest> requests;

        public WindowUpdateExecutor(Manager manager, Window window) 
        {
            this.manager = manager;
            this.window = window;
            this.requests = new ArrayList<>();
        }
        
        public synchronized void enqueue(UpdateRequest request)
        {
            requests.add(request);
            
            if(requests.size() == 1)
            {
                manager.threadPool.execute(this);
            }
        }

        @Override
        public void run() 
        {
            if(!manager.canUpdate(window))
            {
                //Retry the update at a later time
                manager.threadPool.execute(this);
                return;
            }
            
            manager.lockForUpdate();
            UpdateRequest request;
            synchronized(this)
            {
                request = requests.get(0);
                requests.remove(0);
            }
            request.update();
            manager.unlockForUpdate();

            manager.requestProcessed(request);
            
            synchronized(this)
            {
                if(!requests.isEmpty())
                {
                    manager.threadPool.execute(this);
                }
            }
        }
    }
    
    private class WindowInfo
    {
        String title = "";
        Rectangle rect = new Rectangle();
        WindowUpdateExecutor executor;
        ReentrantLock updateLock = new ReentrantLock();
        
        WindowInfo(WindowUpdateExecutor executor)
        {
            this.executor = executor;
        }
    }
    
    private class DesktopInfo
    {
        String name = "";
        ArrayList<Window> windows = new ArrayList<>();
        Window focusedWindow = null;
        ReentrantLock lock = new ReentrantLock();
    }
    
    private ConcurrentHashMap<Desktop, DesktopInfo> desktops;
    private ConcurrentHashMap<Window, WindowInfo> windows;
    private Logger logger;
    private ExecutorService threadPool;
    private ReentrantReadWriteLock updateLock;
    private CopyOnWriteArrayList<ManagerListener> listeners;
    
    public Manager(int numOfThreads)
    {
        this.desktops = new ConcurrentHashMap<>();
        this.windows = new ConcurrentHashMap<>();
        this.logger = new Logger();
        this.threadPool = Executors.newFixedThreadPool(numOfThreads);
        this.updateLock = new ReentrantReadWriteLock();
        this.listeners = new CopyOnWriteArrayList<>();
    }
    
    public Logger getLogger()
    {
        return this.logger;
    }
    
    public void addListener(ManagerListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeListener(ManagerListener listener)
    {
        listeners.remove(listener);
    }
    
    public void add(Window window)
    {
        this.windows.put(window, new WindowInfo(new WindowUpdateExecutor(this, window)));
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onWindowAdded(window);
                }
            }
        });
    }
    
    public Window getWindowByTitle(String title)
    {
        for(Entry<Window, WindowInfo> entry : this.windows.entrySet())
        {
            if(entry.getValue().title == title)
            {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public void remove(Window window)
    {
        Desktop desktop = getDesktop(window);
        DesktopInfo info = desktops.get(desktop);
        
        synchronized(info)
        {
            info.windows.remove(window);
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onWindowRemoved(window);
                }
            }
        });
    }
    
    public void setTitle(Window window, String title)
    {
        WindowInfo info = this.windows.get(window);
        
        String oldTitle;
        synchronized(info)
        {
            oldTitle = info.title;
            info.title = title;
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onWindowTitleChanged(window, title, oldTitle);
                }
            }
        });
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
        
        Rectangle oldRect;
        synchronized(info)
        {
            oldRect = info.rect;
            info.rect = rect;
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onWindowRectangleChanged(window, rect, oldRect);
                }
            }
        });
    }
    
    public Rectangle getRectangle(Window window)
    {
        WindowInfo info = this.windows.get(window);
        
        synchronized(info)
        {
            return info.rect;
        }
    }
    
    public void setDesktop(Window window, Desktop desktop)
    {
        DesktopInfo desktopInfo = this.desktops.get(desktop);
        Desktop oldDesktop = getDesktop(window);
        if(oldDesktop == null)
        {
            desktopInfo.lock.lock();
            desktopInfo.windows.add(window);
            desktopInfo.lock.unlock();
            
            return;
        }
        
        DesktopInfo oldDesktopInfo = this.desktops.get(oldDesktop);
        
        lockDesktops(desktop, oldDesktop);
        
        oldDesktopInfo.windows.remove(window);
        desktopInfo.windows.add(window);
        
        unlockDesktops(desktop, oldDesktop);
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onWindowDesktopChanged(window, oldDesktop, desktop);
                }
            }
        });
    }
    
    public void swapDesktops(Window first, Window second)
    {
        lockWindows(first, second);
        
        Desktop firstDesktop = getDesktop(first);
        Desktop secondDesktop = getDesktop(second);
        if(firstDesktop != secondDesktop)
        {
            unfocusWindow(first);
            unfocusWindow(second);
            
            setDesktop(first, secondDesktop);
            setDesktop(second, firstDesktop);
            
            Rectangle firstRect = first.getRectangle();
            Rectangle secondRect = second.getRectangle();
            first.setRectangle(new Rectangle(secondRect.x, secondRect.y, firstRect.width, firstRect.height));
            second.setRectangle(new Rectangle(firstRect.x, firstRect.y, secondRect.width, secondRect.height));
        }
        
        unlockWindows(first, second);
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onWindowsSwapped(first, second);
                }
            }
        });
    }
    
    public Desktop getDesktop(Window window)
    {
        for(Entry<Desktop, DesktopInfo> entry : this.desktops.entrySet())
        {
            Desktop desktop = entry.getKey();
            DesktopInfo desktopInfo = entry.getValue();
            
            desktopInfo.lock.lock();
            int index = desktopInfo.windows.indexOf(window);
            desktopInfo.lock.unlock();
            
            if(index != -1)
            {
                return desktop;
            }
        }
        
        return null;
    }
    
    public void add(Desktop desktop)
    {
        this.desktops.put(desktop, new DesktopInfo());
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onDesktopAdded(desktop);
                }
            }
        });
    }
    
    public void setName(Desktop desktop, String name)
    {
        DesktopInfo info = this.desktops.get(desktop);
        
        String oldName;
        synchronized(info)
        {
            oldName = info.name;
            info.name = name;
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onDesktopNameChanged(desktop, name, oldName);
                }
            }
        });
    }
    
    public String getName(Desktop desktop)
    {
        DesktopInfo info = this.desktops.get(desktop);
        
        synchronized(info)
        {
            return info.name;
        }
    }
    
    public KeySetView<Desktop, DesktopInfo> getDesktops()
    {
        return this.desktops.keySet();
    }
    
    public ArrayList<Window> getWindows(Desktop desktop)
    {
        DesktopInfo info = desktops.get(desktop);
        
        synchronized(info)
        {
            return info.windows;
        }
    }
    
    public void focusWindow(Window window)
    {
        Desktop desktop = getDesktop(window);
        DesktopInfo info = desktops.get(desktop);
        
        synchronized(info)
        {
            //Ensure that the focused window is always drawn on top
            info.windows.remove(window);
            info.windows.add(window);
            
            info.focusedWindow = window;
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onWindowFocused(desktop, window);
                }
            }
        });
    }
    
    public void unfocusWindow(Window window)
    {
        Desktop desktop = getDesktop(window);
        DesktopInfo info = desktops.get(desktop);
        
        synchronized(info)
        {
            info.focusedWindow = null;
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onWindowUnfocused(desktop);
                }
            }
        });
    }
    
    public boolean isFocused(Window window)
    {
        Desktop desktop = getDesktop(window);
        DesktopInfo info = desktops.get(desktop);
        
        synchronized(info)
        {
            return info.focusedWindow == window;
        }
    }
    
    public void update(UpdateRequest request)
    {
        WindowInfo info = windows.get(request.getWindow());
        info.executor.enqueue(request);
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onUpdateRequestAdded(request);
                }
            }
        });
    }
    
    public void lockForUpdate()
    {
        this.updateLock.readLock().lock();
    }
    
    public void unlockForUpdate()
    {
        this.updateLock.readLock().unlock();
    }
    
    public void lockForRender()
    {
        this.updateLock.writeLock().lock();
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onUpdatesPaused();
                }
            }
        });
    }
    
    public void unlockForRender()
    {
        this.updateLock.writeLock().unlock();
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onUpdatesResumed();
                }
            }
        });
    }
    
    public void pauseUpdates(Window window)
    {
        WindowInfo info = this.windows.get(window);
        
        synchronized(info)
        {
            info.updateLock.lock();
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onUpdatesPaused(window);
                }
            }
        });
    }
    
    public void resumeUpdates(Window window)
    {
        WindowInfo info = this.windows.get(window);
        
        synchronized(info)
        {
            info.updateLock.unlock();
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onUpdatesResumed(window);
                }
            }
        });
    }
    
    public boolean canUpdate(Window window)
    {
        WindowInfo info = this.windows.get(window);
        
        synchronized(info)
        {
            return !info.updateLock.isLocked();
        }
    }
    
    public void lockDesktop(Desktop desktop)
    {
        DesktopInfo info = this.desktops.get(desktop);
        
        //synchronized(info)
        {
            info.lock.lock();
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onUpdatesPaused(desktop);
                }
            }
        });
    }
    
    public void unlockDesktop(Desktop desktop)
    {
        DesktopInfo info = this.desktops.get(desktop);
        
        //synchronized(info)
        {
            info.lock.unlock();
        }
        
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onUpdatesResumed(desktop);
                }
            }
        });
    }
    
    public void requestProcessed(UpdateRequest request)
    {
        threadPool.execute(new Runnable() {
            @Override
            public void run() 
            {
                for(ManagerListener listener : listeners)
                {
                    listener.onUpdateRequestExecuted(request);
                }
            }
        });
    }
    
    private void lockDesktops(Desktop lhs, Desktop rhs)
    {
        if(lhs.getName().compareTo(rhs.getName()) < 0)
        {
            lockDesktop(lhs);
            lockDesktop(rhs);
        }
        else
        {
            lockDesktop(rhs);
            lockDesktop(lhs);
        }
    }
    
    private void lockWindows(Window lhs, Window rhs)
    {
        if(lhs.getName().compareTo(rhs.getName()) < 0)
        {
            pauseUpdates(lhs);
            pauseUpdates(rhs);
        }
        else
        {
            pauseUpdates(rhs);
            pauseUpdates(lhs);
        }
    }
    
    private void unlockDesktops(Desktop lhs, Desktop rhs)
    {
        unlockDesktop(lhs);
        unlockDesktop(rhs);
    }
    
    private void unlockWindows(Window lhs, Window rhs)
    {
        resumeUpdates(lhs);
        resumeUpdates(rhs);
    }
}
