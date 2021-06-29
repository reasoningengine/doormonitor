/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doormonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;

class LogThread extends Thread
{
    
    public TextArea textArea;
    public Slider slider;
    public boolean stopped = false;
    
    public void run()
    {
        Platform.runLater(() -> {
            textArea.setText("Loading the system logs...");
        });

        try 
        { 
            Process p = Runtime.getRuntime().exec("wevtutil qe system /f:text /rd:true /uni:false"); 
            //p.waitFor(); 
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );


            String line;
            String text = "";
            int i = 0;
            //System.out.println("Done1");
            while(((line = reader.readLine()) != null) && (i < Math.round(slider.getValue())) && (stopped == false)) 
            { 
                text = text + line + "\n";
                i++;

            }
            System.out.println("Done");
            final String text2 = text;

            Platform.runLater(() -> {

                textArea.setText(text2);

            });

            p = null;
            reader = null;
            line = null;
            text = null;

        }
        catch(IOException e)
        {
            System.out.println("Error creating the process.");
        }                        

    }
}
