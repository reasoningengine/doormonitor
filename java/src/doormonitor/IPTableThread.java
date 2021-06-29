/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doormonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.ListView;

class IPTableThread extends Thread
{
    public Timer timer = null;
    public ListView listView;

    public void run()
    {

        Platform.runLater(() -> {
            listView.getItems().add("Loading the connections list...");
        });

        try 
        { 
            Process p = Runtime.getRuntime().exec("cmd /c netstat -f"); 

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );

            class ConnectionsUpdateTask extends TimerTask
            {
                @Override
                public void run()
                {
                    String line;
                    try {
                        if((line = reader.readLine()) != null)
                        {
                            try
                            {
                                if (line.contains(InetAddress.getLocalHost().getHostName()) == false)
                                {

                                    final String line2 = line;

                                    Platform.runLater(() -> {
                                        //fix NullPointerException
                                        listView.getItems().add(line2);
                                    });
                                }
                            }
                            catch(IOException exception)
                            {
                                System.out.println("Error.");
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    line = null;
                }
            }

            TimerTask timerTask = new ConnectionsUpdateTask();
            timer = new Timer(true);
            timer.scheduleAtFixedRate(timerTask, 0, 1000);

        }
        catch(IOException e)
        {
            System.out.println("Error creating the process.");
        }

    }

}        
