package com.iclp.windowmanager.demo;

import com.iclp.windowmanager.Desktop;
import com.iclp.windowmanager.Manager;
import com.iclp.windowmanager.Rectangle;
import com.iclp.windowmanager.Window;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MovinTestWindow extends Window
{
    private class Direction
    {
        int x;
        int y;

        public Direction(int x, int y) 
        {
            this.x = x;
            this.y = y;
        }
    }
    
    private ArrayList<Direction> directions;
    private int directionIndex = 0;
    private int movementLength = 0;
    
    private static final int MAX_LENGTH = 50;
    
    public MovinTestWindow(Manager manager, Desktop desktop, String title, int width, int height)
    {
        super(manager, desktop, title, width, height);
        
        this.directions = new ArrayList<>();
        this.directions.add(new Direction(0, 1));
        this.directions.add(new Direction(1, 0));
        this.directions.add(new Direction(0, -1));
        this.directions.add(new Direction(-1, 0));
    }
    
    @Override
    public void run()
    {
        while(!isClosed())
        {
            Graphics2D g2 = getBuffer().beginRender();
            g2.setFont(new Font("TimesRoman", Font.PLAIN, 25)); 
            
            if(movementLength >= MAX_LENGTH)
            {
                directionIndex = (directionIndex + 1) % directions.size();
                movementLength = 0;
            }
            move();
            ++movementLength;
            
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.setColor(Color.BLACK);
            g2.drawString("Catch me if you can!", getWidth() / 2 - 140, getHeight() / 2);
            
            getBuffer().endRender(g2);
            
            try {
                Thread.sleep(33);
            } catch (InterruptedException ex) {
                Logger.getLogger(MovinTestWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
    
    private void move() 
    {
        Direction dir = directions.get(directionIndex);
        int x = getX() + dir.x;
        int y = getY() + dir.y;
        setRectangle(new Rectangle(x, y, getWidth(), getHeight()));
    }
}
