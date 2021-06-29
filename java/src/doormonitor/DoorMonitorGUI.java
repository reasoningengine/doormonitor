/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doormonitor;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.WindowEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.stage.StageStyle;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.UnknownHostException;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import java.io.File;

public class DoorMonitorGUI extends Application {
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    private LogThread logThr;
       
    public static void main(String[] args) {
        launch(args);
    }    
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        stage.setTitle("DoorMonitor");
        stage.setResizable(true);
        final ListView listView1 = (ListView) scene.lookup("#listview1");
        final ListView honeyPotListView = (ListView) scene.lookup("#honeypotlistview");
        final TextArea textArea = (TextArea) scene.lookup("#textarea");
        
        TabPane tabPane = (TabPane) scene.lookup("#tabpane");
        
        Slider updateSlider = (Slider) scene.lookup("#updateslider");
        Slider loginHistorySlider = (Slider) scene.lookup("#loginhistoryslider");
        
        RadioButton dnsButton = (RadioButton) scene.lookup("#dnsbutton");
        RadioButton arpButton = (RadioButton) scene.lookup("#arpbutton");
        RadioButton registryButton = (RadioButton) scene.lookup("#registrybutton");
        RadioButton restoreBrowserButton = (RadioButton) scene.lookup("#restorebrowserbutton");
        RadioButton restoreSystemButton = (RadioButton) scene.lookup("#restoresystembutton");
        RadioButton verifyDriversButton = (RadioButton) scene.lookup("#verifydriversbutton");
        
        Button loadButton = (Button) scene.lookup("#loadbutton");
        Button secureSettingsButton = (Button) scene.lookup("#securesettingsbutton");
        Button honeyPotButton = (Button) scene.lookup("#honeypotbutton");
        Button ipLogButton = (Button) scene.lookup("#iplogbutton");
        Button closeButton = (Button) scene.lookup("#closebutton");
        Button refreshButton = (Button) scene.lookup("#refreshbutton");
        
        ChoiceBox connectionsChoiceBox = (ChoiceBox) scene.lookup("#choicebox1");        
        
        ListView ipLogListView = (ListView) scene.lookup("#iploglistview"); 
        
        TextField honeyPotTextField = (TextField) scene.lookup("#honeypottextfield");
        
        Pane pane = (Pane)scene.lookup("#pane");
        
        LineChart lineChart = (LineChart)scene.lookup("#linechart");
        
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        

        honeyPotTextField.setText("80");
        
        XYChart.Series data1 = new XYChart.Series(); 
        data1.setName("bits/second");  
        lineChart.getData().addAll(data1);
                
        honeyPotButton.setOnAction((event) -> {    
            
            Process myProcess = null;
            
            if (honeyPotButton.getText() == "Disable")
            {
                
                honeyPotButton.setText("Enable");
                
                CloseProcesses closeProcessesThread = new CloseProcesses();
                closeProcessesThread.processNames = new String[] {"honeypot.exe"};
                closeProcessesThread.start();  

            }
            else
            {
                
                honeyPotButton.setText("Disable");
                try {
                    Runtime.getRuntime().exec(System.getProperty("user.dir") + File.separator + "honeypot " + honeyPotTextField.getText());
                } catch (IOException ex) {
                    Logger.getLogger(DoorMonitorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
        });
        

        secureSettingsButton.setOnAction((event) ->{
            if (dnsButton.isSelected()){
                try {
                    Runtime.getRuntime().exec("cmd.exe /c start ipconfig /flushdns & pause");
                } catch (IOException e) {                
                    e.printStackTrace();
                } 
            }
            
            if (arpButton.isSelected()){
                try {
                    Runtime.getRuntime().exec("cmd.exe /c start arp -d -a & pause");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (registryButton.isSelected()){
                try {
                    Runtime.getRuntime().exec("cmd.exe /c start reg delete HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run & pause");
                }catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            
            if (restoreBrowserButton.isSelected()){
                try{
                    String strAppd = System.getenv("APPDATA");
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir " + strAppd + "\\Roaming\\Mozilla /s & pause");
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir " + strAppd + "\\Roaming\\Google\\Chrome /s & pause");
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir " + strAppd + "\\Roaming\\Mozilla /s & pause");
                    
                    
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir " + strAppd + "\\Local\\Mozilla /s & pause");
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir " + strAppd + "\\Local\\Google\\Chrome /s & pause");
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir \"" + strAppd + "\\Local\\Microsoft\\Internet Explorer\" /s & pause");                    
       
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir " + strAppd + "\\LocalLow\\Mozilla /s & pause");
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir " + strAppd + "\\LocalLow\\Google\\Chrome /s & pause");
                    Runtime.getRuntime().exec("cmd.exe /c start rmdir \"" + strAppd + "\\LocalLow\\Microsoft\\Internet Explorer /s & pause");
                    strAppd = null;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }                
            }
            
            if (verifyDriversButton.isSelected()){
                try{
                    Runtime.getRuntime().exec("cmd.exe /c start sigverif");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }                
            }

            if (restoreSystemButton.isSelected()){
                try{
                    Runtime.getRuntime().exec("cmd.exe /c sfc /scannow");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }                
            }
            
            System.out.println("Done");
            
        });
        
        
        loadButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg0)
            {
                logThr = new LogThread();
                logThr.slider = loginHistorySlider;
                logThr.textArea = textArea;
                logThr.start();              
            }
        });
        
     
        closeButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg0)
            {	
                CloseProcesses closeProcessesThread = new CloseProcesses();
                closeProcessesThread.exit = true;
                closeProcessesThread.processNames = new String[] {"honeypot.exe", "trafficlogger.exe", "trafficmonitor.exe"};
                closeProcessesThread.start();
            }
        });         
        
        
        TrafficMonitorThread trafficMonitorThread = new TrafficMonitorThread();
        trafficMonitorThread.data = data1;
        trafficMonitorThread.start();
        
        
        HoneypotThread honeypotThread = new HoneypotThread();
        honeypotThread.listView = honeyPotListView;
        honeypotThread.start();
        
    
        listView1.setOnMouseClicked(new EventHandler<MouseEvent>()
        {

            @Override
            public void handle(MouseEvent arg0) {
                
                //perform NullPointer check here
                
                if (listView1.getSelectionModel().getSelectedItem().toString().split("\\s+").length == 5)
                {
                    String strIPInfo = listView1.getSelectionModel().getSelectedItem().toString().split("\\s+")[3].split(":")[0];
                    System.out.println(listView1.getSelectionModel().getSelectedItem().toString().split("\\s+").length);
                    
                }
            }
        });        
        
        
        IPTableThread thread1 = new IPTableThread();
        thread1.listView = listView1;
        thread1.start();
        
        refreshButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {

            @Override
            public void handle(MouseEvent arg0) {
                listView1.getItems().clear();
                System.out.println(thread1.timer == null);
                thread1.timer.cancel();
                thread1.timer.purge();
                IPTableThread thread2 = new IPTableThread();
                thread2.start();
            }
        });
       
        DeviceThread deviceThread = new DeviceThread();
        deviceThread.choiceBox = connectionsChoiceBox;
        deviceThread.start();
        
        ipLogButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg0)
            {
                DeviceLogThread deviceLogThread = null;
                
                if (ipLogButton.getText() == "Disable")
                {

                    ipLogButton.setText("Enable");
                    	
                    CloseProcesses closeProcessesThread = new CloseProcesses();
                    closeProcessesThread.processNames = new String[] {"trafficlogger.exe", "trafficmonitor.exe"};
                    closeProcessesThread.start();                   	
                    	
                }
                else
                {
                    ipLogButton.setText("Disable");
                    
                    if (deviceLogThread == null)
                    {
                        deviceLogThread = new DeviceLogThread();
                        deviceLogThread.choiceBox = connectionsChoiceBox;
                        deviceLogThread.listView = ipLogListView;
                        deviceLogThread.start();
                    }
                    else
                    {
                    	//This is not dead code in reality. Do not remove.
                    	
                        String device = connectionsChoiceBox.getSelectionModel().getSelectedItem().toString().split(" ")[0];

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
                    }
                }                
            }
        });          
        
        
        /*stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) 
            {

            }
        });*/
        
    }
   
}
