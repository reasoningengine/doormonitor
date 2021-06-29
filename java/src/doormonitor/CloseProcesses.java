/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doormonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CloseProcesses extends Thread {

	private ArrayList<String> processList = new ArrayList<String>();;
	public String[] processNames = {"honeypot.exe", "trafficlogger.exe", "trafficmonitor.exe"};
	public boolean exit = false;
	
	public void run()
	{
        try 
        { 
            Process p = Runtime.getRuntime().exec("tasklist"); 
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );
            
        
            String line;
            while((line = reader.readLine()) != null) 
            { 
            	for (String v : processNames)
            	{
	            	if (line.contains(v))
	            	{
	            		processList.add(v);
	            	}
            	}

            }

            for (String v2 : processList)
            {
            	Runtime.getRuntime().exec("taskkill /F /IM " + v2);
            }
            
            if (exit)
            {
            	
                TimerTask task = new TimerTask() {
                    public void run() {
                    	System.exit(0);
                    }
                };
                
                Timer timer = new Timer("Timer");
                timer.schedule(task, 1000L);
            }
            
        }
        catch(IOException e)
        {
            System.out.println("Error creating the process.");
        } 
	}
	
}
