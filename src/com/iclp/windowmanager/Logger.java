package com.iclp.windowmanager;

import java.util.concurrent.atomic.AtomicInteger;

public class Logger 
{
    public static final int INFO = 0;
    public static final int DEBUG = 1;
    
    private int level;
    private AtomicInteger counter = new AtomicInteger(0);
    
    public Logger()
    {
        this(DEBUG);
    }
    
    public Logger(int level)
    {
        this.level = level;
    }
    
    public int getLevel()
    {
        return this.level;
    }
    
    void setLevel(int level)
    {
        this.level = level;
    }
    
    public void log(int level, String message)
    {
        if(level > this.level)
        {
            return;
        }
        
        String logMessage = "[" + getLevelName(level) + "][" + counter.incrementAndGet() + "]: " + message;
        onLogMessage(logMessage);
    }
    
    protected synchronized void onLogMessage(String message)
    {
        System.out.println(message);
    }
    
    private static String getLevelName(int level)
    {
        switch(level)
        {
            case INFO:
                return "INFO";
            case DEBUG:
                return "DEBUG";
        }
        return "INVALID LEVEL";
    }
}
