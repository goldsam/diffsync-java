package org.github.goldsam.diffsync.core;

/**
 *
 * @param <D> Document type.
 * @param <P> Patch type.
 */
public class Workspace<D, P> {
  
  private Differencer<D, P> differencer;
  
  private D currentDocument;
  private D shadowDocument;
  private D backupDocument;
  
  long localVersion; // n
  long remoteVersion; // m
  
  boolean isServer;
  long backupVersion;
  
  private final MemoryEditStack<P> edits = new MemoryEditStack<>();
  
  private final EditSender<P> editSender;
  
  private int authorId;
  
  public Workspace(EditSender<P> editSender) {
    this.editSender = editSender;
  }

  public void update() {
    update(currentDocument);
  }
  
  public void update(D newCurrentDocument) {
    P patch = differencer.Difference(shadowDocument, newCurrentDocument);    
    edits.pushEdit(patch, localVersion);
    shadowDocument = newCurrentDocument;
    currentDocument = newCurrentDocument;
    localVersion++;
    editSender.sendEdits(edits, remoteVersion);
  }
  
  public void receiveEdit(P patch, long patchSourceVersion, long lastReceivedLocalVersion) {
    if((lastReceivedLocalVersion == localVersion) && 
       (patchSourceVersion == localVersion)) 
    {
      shadowDocument = differencer.Patch(shadowDocument, patch);
      remoteVersion++;
      
      if (isServer) {
        backupDocument = shadowDocument;
        backupVersion = localVersion; 
      }
      
      currentDocument = differencer.Patch(currentDocument, patch);
      edits.purgeEdits(lastReceivedLocalVersion);
      update();
    } 
    else if (isServer &&
             (lastReceivedLocalVersion == backupVersion) && 
             (patchSourceVersion == backupVersion)) 
    {
      shadowDocument = backupDocument;
      localVersion = backupVersion;
      edits.clear();
      
      update();
    }
    
    editSender.sendAck(patchSourceVersion);
  }
}
