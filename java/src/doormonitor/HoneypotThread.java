/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doormonitor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.ListView;

class HoneypotThread extends Thread
{
    
    public ListView listView;
    
    public void run()
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("honeypotlogs.txt", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.close();
        writer = null;

        FileReader fr = null;
        try {
            fr = new FileReader("honeypotlogs.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        final BufferedReader br = new BufferedReader(fr);

        class HoneypotUpdateTask extends TimerTask
        {
            @Override
            public void run()
            {
                String line;
                try {
                    if ((line = br.readLine()) != null)
                    {
                        final String adrString = line;
                        Platform.runLater(() -> {
                            listView.getItems().add(adrString);
                        });
                        System.out.println(adrString);                
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                line = null;
            }
        }

        TimerTask timerTask = new HoneypotUpdateTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 1000);                   
    }
}
