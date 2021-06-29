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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;

class TrafficMonitorThread extends Thread
{
    XYChart.Series data;
    
    public void run()
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("trafficlogs.txt", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.close();
        writer = null;


        FileReader fr = null;
        try {
            fr = new FileReader("trafficlogs.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        final BufferedReader br = new BufferedReader(fr);



        class TrafficMonitorUpdateTask extends TimerTask
        {
            @Override
            public void run()
            {
                String sCurrentLine;

                try {
                    int i = 1;

                    if ((sCurrentLine = br.readLine()) != null)
                    {
                        if (sCurrentLine.split(" ").length == 2)
                        {    
                            i++;

                            String[] splString = sCurrentLine.split(" ");
                            String val1 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            int val2 = Integer.valueOf(splString[1]);

                            System.out.println(sCurrentLine);

                            Platform.runLater(() -> {
                                data.getData().add(new XYChart.Data(val1, val2));

                                if (data.getData().size() > 10) {
                                    data.getData().remove(0, data.getData().size() - 10);
                                }

                            });     



                            splString = null;
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }

                sCurrentLine = null;
            }
        }

        TimerTask timerTask = new TrafficMonitorUpdateTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 900);                      

    }
}    
