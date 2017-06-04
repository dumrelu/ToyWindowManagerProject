package com.iclp.windowmanager;

public class Logger 
{
    public static final int INFO = 0;
    public static final int DEBUG = 1;
    
    private int level;
    
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
    
    public synchronized void log(int level, String message)
    {
        if(level > this.level)
        {
            return;
        }
        
        String logMessage = "[" + getLevelName(level) + "]: " + message;
        onLogMessage(message);
    }
    
    protected void onLogMessage(String message)
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
