package org.github.goldsam.diffsync.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class ClientComponent extends JPanel {
  
  private static final long serialVersionUID = 1L;
  
  public ClientComponent() {
    //super(new GridBagLayout());
    super(new BorderLayout());
    
    setBackground(Color.red);
    
    JTextArea textArea = new JTextArea();
    
    //textArea.setFont(textArea.getFont().deriveFont(20));
//    GridBagConstraints c = new GridBagConstraints();
//    c.gridx = 0;
//    c.gridy = 0;
//    c.gridheight = 1;
//    c.gridwidth = 1;
//    
//    add(textArea, c);
    //add(textArea);
    
    JScrollPane scroll = new JScrollPane(textArea);
    add(scroll, BorderLayout.CENTER);
    
    textArea.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        handleChange(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        handleChange(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        handleChange(e);
      }

      private void handleChange(DocumentEvent e) {
        Document document = e.getDocument();
        String documentText;
        try {
          documentText = document.getText(0, document.getLength());
        } catch(BadLocationException ex) {
          throw new RuntimeException("Unable to get document teext.", ex);
=        }
      }
    });
    
  }
}
