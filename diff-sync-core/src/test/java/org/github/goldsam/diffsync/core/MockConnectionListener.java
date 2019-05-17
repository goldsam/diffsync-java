//package org.github.goldsam.diffsync.core;
//
//import java.util.ArrayList;
//import java.util.List;
//import org.github.goldsam.diffsync.core.context.SynchronizedView;
//
//public class MockConnectionListener<D, P> implements ConnectionListener<D, P> {
//
//  public static class ConnectionEvent<T, U, C> {
//    
//    public enum EventType {
//      SEND_EDITS,
//      SEND_DOCUMENT
//    }
//    
//    private final SynchronizedView<T, U> localContext;
//    private final EventType eventType;
//    private final C cause;
//    
//    public ConnectionEvent(SynchronizedView<T, U> localContext, EventType eventType, C cause) {
//      this.localContext = localContext;
//      this.eventType = eventType;
//      this.cause = cause;
//    }
//
//    public SynchronizedView<T, U> getLocalContext() {
//      return localContext;
//    }
//
//    public EventType getEventType() {
//      return eventType;
//    }
//    
//    public C getCause() {
//      return cause;
//    }
//  }
//
//  private List<ConnectionEvent<D, P, ?>> events = new ArrayList<>();
//  
//  @Override
//  public void onSendEdits(SynchronizedView<D, P> localContext, SendEditsCause cause) {
//    events.add(new ConnectionEvent<>(localContext, ConnectionEvent.EventType.SEND_EDITS, cause));
//  }
//
//  @Override
//  public void onSendDocument(SynchronizedView<D, P> localContext, SendDocumentCause cause) {
//    events.add(new ConnectionEvent<>(localContext, ConnectionEvent.EventType.SEND_DOCUMENT, cause));   
//  }
//  
//  @Override
//  public void onSendResetRequest(SynchronizedView<D, P> localContext) {
//  
//  }
//
//  public List<ConnectionEvent<D, P, ?>> getEvents() {
//    return new ArrayList<>(events);
//  }
//}
