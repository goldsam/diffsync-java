package org.github.goldsam.diffsync.core;

public class IntDifferencer implements Differencer<Integer, Integer>{

  private IntDifferencer() {}
  
  private static final IntDifferencer instance = new IntDifferencer();
  
  @Override
  public Integer difference(Integer sourceDocument, Integer destDocument) {
    return destDocument - sourceDocument;
  }

  @Override
  public Integer patch(Integer sourceDocument, Integer patch, boolean fuzzy) throws PatchFailedException {
    return sourceDocument + patch;
  }  
  
  public static IntDifferencer getInstance() {
    return instance;
  }
}
