package org.github.goldsam.diffsync;

public interface Connectable<D, P> {
  
  Connection<D, P> connect(ConnectionListener<D, P> connectionListener) throws Exception;
}
