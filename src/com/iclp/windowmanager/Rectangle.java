package com.iclp.windowmanager;


public class Rectangle 
{
    public int x;
    public int y;
    public int width;
    public int height;
    
    public Rectangle(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Rectangle()
    {
        this(0, 0, 0, 0);
    }
    
    public boolean intersects(int x, int y)
    {
        int right = this.x + this.width;
        int bottom = this.y + this.height;
        return x >= this.x && x < right &&
               y >= this.y && y < bottom;
    }
}
