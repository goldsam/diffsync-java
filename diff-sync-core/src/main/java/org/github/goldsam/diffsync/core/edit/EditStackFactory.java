package org.github.goldsam.diffsync.core.edit;

import org.github.goldsam.diffsync.core.LocalContext;
import org.github.goldsam.diffsync.core.edit.EditStack;

/**
 * @param <P> Patch type.
 */
public interface EditStackFactory<P> {
  EditStack<P> createEditStack(LocalContext<?, P> localContext);
}
