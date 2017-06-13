package com.iclp.windowmanager.demo;

import com.iclp.windowmanager.Desktop;
import com.iclp.windowmanager.Manager;
import com.iclp.windowmanager.Window;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DesktopMovingWindow extends Window
{
    private int secondsToMove;
    private int secondsPassed = 0;
    
    private static final int MIN_SECONDS = 5;
    private static final int MAX_SECONDS = 15;
    
    public DesktopMovingWindow(Manager manager, Desktop desktop, String title, int width, int height) 
    {
        super(manager, desktop, title, width, height);
        
        secondsToMove = (int)(MIN_SECONDS + Math.random() * (MAX_SECONDS - MIN_SECONDS));
    }
    
    @Override
    public void run()
    {
        while(!isClosed())
        {
            Graphics2D g2 = getBuffer().beginRender();
            g2.setFont(new Font("TimesRoman", Font.PLAIN, 15)); 
            
            if(secondsPassed >= secondsToMove)
            {
                secondsPassed = 0;
                secondsToMove = (int)(MIN_SECONDS + Math.random() * (MAX_SECONDS - MIN_SECONDS));
                
                Desktop currentDesktop = getDesktop();
                for(Desktop desktop : getManager().getDesktops())
                {
                    if(currentDesktop != desktop && getManager().tryLockDesktop(desktop))
                    {
                    	if(getManager().tryLockDesktop(currentDesktop))
                    	{
                    		setDesktop(desktop);
                    		getManager().unlockDesktop(currentDesktop);
                    	}
                    	getManager().unlockDesktop(desktop);
                    }
                }
            }
            
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.setColor(Color.BLACK);
            g2.drawString("I might move to another desktop!", getWidth() / 2 - 140, getHeight() / 2);
            g2.drawString("(" + (secondsToMove - secondsPassed) + ")", getWidth() / 2 - 10, getHeight() / 2 + 15);
            
            getBuffer().endRender(g2);
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MovinTestWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ++secondsPassed;
        }
    }
}
