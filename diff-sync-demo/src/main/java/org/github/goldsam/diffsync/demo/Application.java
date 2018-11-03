/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.github.goldsam.diffsync.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import org.github.goldsam.diffsync.core.Client;
import org.github.goldsam.diffsync.core.Connectable;
import org.github.goldsam.diffsync.core.edit.MemoryEditStack;
import org.github.goldsam.diffsync.core.Host;
import org.github.goldsam.diffsync.text.TextDifferencer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
  private static final Logger logger = LoggerFactory.getLogger(Application.class);
  
  private Host<String, String> host = new Host<>(new TextDifferencer(), MemoryEditStack.Factory.getInstance());
  
  private final NetworkConnectionSimulator ncs1;
  private final NetworkConnectionSimulator ncs2;
  
  private Timer timer; 

  public Application() {
    host.reset("");
    
    UIManager.getDefaults().put(
      "TextArea.font", 
      UIManager.getFont("TextField.font").deriveFont(22f));
    
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");    
    } catch(Exception ex) {
//      log.error("Unable to set look-and-feel.", ex);
    }
    
    JFrame.setDefaultLookAndFeelDecorated(true);

    //Create and set up the window.
    JFrame frame = new JFrame("HelloWorldSwing");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    Client<String, String> c1 = new Client<>(new TextDifferencer(), MemoryEditStack.Factory.getInstance(), false);
    ClientComponent cc1 = new ClientComponent(c1);
    c1.addClientListener(cc1);
      
    Client<String, String> c2 = new Client<>(new TextDifferencer(), MemoryEditStack.Factory.getInstance(), false);
    ClientComponent cc2 = new ClientComponent(c2);
    c2.addClientListener(cc2);
    
    ncs1 = createSimulatedConnection(host, c1);
    ncs2 = createSimulatedConnection(host, c2);
      
    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      cc1, cc2);
    
    
    c1.reset();
    c2.reset();
    frame.getContentPane().add(splitPane);
    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentShown(ComponentEvent e) {
        SwingUtilities.invokeLater(() -> {
          splitPane.setDividerLocation(0.5);
          splitPane.removeComponentListener(this);
        });
      }
    });
      
    //Display the window.
    frame.pack();
    frame.setSize(800, 600);
    frame.setVisible(true);
    
//    scheduleUpdate();
  }
  
  private NetworkConnectionSimulator<String, String> createSimulatedConnection(Connectable<String, String> c1, Connectable<String, String> c2) {
    NetworkConnectionSimulator<String, String> ncs = new NetworkConnectionSimulator<>(c1, c2);
    try {
      ncs.connect();
      return ncs;
    } catch(Exception e) {
      throw new RuntimeException("Unable to create simulated network connection");
    }
  }
  
//  private void scheduleUpdate() {
//    timer = new Timer(1000, new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        
//      }
//    });
//    
//    timer.setRepeats(true);
//    timer.start();
//  }
  
//  private void stop() {
//  
//  }
  
  
  public static void main(String[] args) {
    try {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          new Application();
        }
      });
    } catch (Exception e) {
      logger.error("Application error", e);
    }
  }
}
