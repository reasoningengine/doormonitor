/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doormonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

class DeviceThread extends Thread
{
    private ArrayList<String> arrayList = new ArrayList<String>();
    public ChoiceBox choiceBox;
    
    public void run()
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("devicelist.txt", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.close();
        writer = null;
        try {
            Runtime.getRuntime().exec(System.getProperty("user.dir") + File.separator + "trafficlogger list");
        } catch (IOException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }                

        try {

            FileReader fr = new FileReader("devicelist.txt");
            final BufferedReader reader = new BufferedReader(fr);


            class DeviceUpdateTimer extends TimerTask
            {
                @Override
                public void run()
                {                           
                    //rewrite this shit
                    String line;
                    ObservableList<String> strList = FXCollections.observableArrayList();

                    try {
                        if ((line = reader.readLine()) != null)
                        {
                            if (line != "" && line != null && line != "\n")
                            {
                                arrayList.add(line);
                                Platform.runLater(() -> {
                                    strList.setAll(arrayList);
                                    choiceBox.setItems(strList);
                                });

                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            TimerTask timerTask = new DeviceUpdateTimer();
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(timerTask, 0, 1000);

        } catch (IOException e) {

            e.printStackTrace();

        }  
    }
} 