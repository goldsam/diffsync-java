package org.github.goldsam.diffsync.edit;

/**
 * @param <P> Patch type.
 */
@FunctionalInterface
public interface EditStackFactory<P> {
  EditStack<P> createEditStack();
}
