package org.github.goldsam.diffsync.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.github.goldsam.diffsync.core.Client;
import org.github.goldsam.diffsync.core.ClientListener;
import org.github.goldsam.diffsync.core.edit.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientComponent extends JPanel implements ClientListener<String, String>{
  
  private static final long serialVersionUID = 1L;
  
  private static final Logger logger = LoggerFactory.getLogger(ClientComponent.class);
  
  private final JTextArea textArea = new JTextArea();
  private final Client<String, String> client;
      
  private boolean updating = false;
   
  public ClientComponent(Client<String, String> client) {
    super(new BorderLayout());
    
    this.client = client;
   
    setBackground(Color.red);
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
        if (updating) {
          return;
        }
        
        Document document = e.getDocument();
        String documentText;
        try {
          documentText = document.getText(0, document.getLength());
          client.update(documentText);
        } catch(BadLocationException ex) {
          throw new RuntimeException("Unable to get document teext.", ex);
        }
      }
    });
  }

  @Override
  public void onDocumentUpdated(String document, long localVersion) {
    updateTextArea(document);      
  }

  @Override
  public void onEditProcessed(String document, Edit<String> edit, long remoteVersion) {
    updateTextArea(document);
  }
  
  private void updateTextArea(String document) {
    SwingUtilities.invokeLater(() -> {
      updating = true;
      try {
        textArea.setText(document);
      } finally {
        updating = false;
      }
    });
  }
}
