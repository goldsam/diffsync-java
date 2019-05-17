package org.github.goldsam.diffsync.edit;

import java.util.Comparator;

public class EditUtils {
  private static final Comparator<Edit<?>> versionComparator = new Comparator<Edit<?>>() {
    @Override
    public int compare(Edit<?> edit1, Edit<?> edit2) {
      return (int)(edit1.getVersion() - edit2.getVersion());
    }
  };
  
  public static Comparator<Edit<?>> getVersionComparator() {
    return versionComparator;
  }
}
