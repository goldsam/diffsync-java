package org.github.goldsam.diffsync.core;

import java.util.ArrayList;
import java.util.List;
import org.github.goldsam.diffsync.core.context.LocalContext;

public class MockConnectionListener<D, P> implements ConnectionListener<D, P> {

  public static class ConnectionEvent<T, U, C> {
    
    public enum EventType {
      SEND_EDITS,
      SEND_DOCUMENT
    }
    
    private final LocalContext<T, U> localContext;
    private final EventType eventType;
    private final C cause;
    
    public ConnectionEvent(LocalContext<T, U> localContext, EventType eventType, C cause) {
      this.localContext = localContext;
      this.eventType = eventType;
      this.cause = cause;
    }

    public LocalContext<T, U> getLocalContext() {
      return localContext;
    }

    public EventType getEventType() {
      return eventType;
    }
    
    public C getCause() {
      return cause;
    }
  }

  private List<ConnectionEvent<D, P, ?>> events = new ArrayList<>();
  
  @Override
  public void onSendEdits(LocalContext<D, P> localContext, SendEditsCause cause) {
    events.add(new ConnectionEvent<>(localContext, ConnectionEvent.EventType.SEND_EDITS, cause));
  }

  @Override
  public void onSendDocument(LocalContext<D, P> localContext, SendDocumentCause cause) {
    events.add(new ConnectionEvent<>(localContext, ConnectionEvent.EventType.SEND_DOCUMENT, cause));   
  }
  
  @Override
  public void onSendResetRequest(LocalContext<D, P> localContext) {
  
  }

  public List<ConnectionEvent<D, P, ?>> getEvents() {
    return new ArrayList<>(events);
  }
}
