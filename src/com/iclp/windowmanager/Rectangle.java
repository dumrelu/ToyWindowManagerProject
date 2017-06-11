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
    
    public boolean intersects(Rectangle other)
    {
        return (Math.abs(x - other.x) * 2 < (width + other.width)) &&
         (Math.abs(y - other.y) * 2 < (height + other.height));
    }
    
    public boolean contains(Rectangle other)
    {
        return x < other.x && x + width > other.x + other.width
                && y < other.y && y + height > other.y + other.height;
    }

    @Override
    public String toString() 
    {
        return "(x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ")";
    }
    
    
}
