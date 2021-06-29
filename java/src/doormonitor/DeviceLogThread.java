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
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

class DeviceLogThread extends Thread
{

    public ChoiceBox choiceBox;
    public ListView listView;
    
    public void run()
    {
        String device = choiceBox.getSelectionModel().getSelectedItem().toString().split(" ")[0];

        try {
            Runtime.getRuntime().exec(System.getProperty("user.dir") + File.separator + "trafficlogger device " + device);
        } catch (IOException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Runtime.getRuntime().exec(System.getProperty("user.dir") + File.separator + "trafficmonitor " + device);
        } catch (IOException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }                

        device = null;

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("iplogs.txt", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.close();
        writer = null;

        FileReader fr = null;

        try {
            fr = new FileReader("iplogs.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        final BufferedReader br = new BufferedReader(fr);

        class DeviceUpdateTask extends TimerTask
        {
            @Override
            public void run()
            {

                String line;
                ObservableList<String> strList = FXCollections.observableArrayList();

                try {
                    if ((line = br.readLine()) != null)
                    {
                        String[] lineArray = line.split(" - ");

                        for (int i=1; i<lineArray.length; i++)
                        {
                            if (!InetAddress.getByAddress(InetAddress.getByName(lineArray[i]).getAddress()).isSiteLocalAddress())
                            {
                                final String line2 = line;

                                Platform.runLater(() -> {
                                    listView.getItems().add(line2);
                                });

                                break;
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }

                line = null;
                strList = null;
            }
        }

        TimerTask timerTask = new DeviceUpdateTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10);
    }
}
