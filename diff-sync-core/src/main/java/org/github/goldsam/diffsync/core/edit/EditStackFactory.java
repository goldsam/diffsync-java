package org.github.goldsam.diffsync.core.edit;

/**
 * @param <P> Patch type.
 */
@FunctionalInterface
public interface EditStackFactory<P> {
  EditStack<P> createEditStack();
}
