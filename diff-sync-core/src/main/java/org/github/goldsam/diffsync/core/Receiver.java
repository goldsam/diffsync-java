package org.github.goldsam.diffsync.core;

/**
 * @param <P> PatchType
 */
public interface Receiver<P> {
  public void receiveEdit(
    P patch, 
    long patchSourceVersion, 
    long lastReceivedVersion) throws PatchFailedException;
    
  
  
}
