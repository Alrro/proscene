| Requirements: [Processing >= 2.x](https://processing.org/download/), and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (source code found under the "**examples**" section of the library package |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

This is a much simpler tutorial showing how "to force" and action on a grabber, bypassing event parsing, simply by using message passing from the agent to the grabber instead. The syntax is as follows:

```
enqueueEventTuple(new EventGrabberDuobleTuple(event, LOCAL_ACTION, grabber));
```

The code is identical to that of the [previous tutorial](ActionDrivenCallback.md), but we have modified a bit the agent mouse listener as follows:

```
public void mouseEvent(processing.event.MouseEvent e) {      
    if ( e.getAction() == processing.event.MouseEvent.MOVE ) {
      event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(), e.getY(), e.getModifiers(), e.getButton());
      updateGrabber(event);
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.DRAG ) {
      event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(), e.getY(), e.getModifiers(), e.getButton());
      // If the Control modifier key is down when a mouse drag occurs, force a MotionAction.CHANGE_POSITION
      // on grabber circles[20], using event.
      if(event.isControlDown())
        tearseHandler().enqueueEventTuple(new EventGrabberDuobleTuple(event, MotionAction.CHANGE_POSITION, circles[20]));
      else
        handle(event);
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.CLICK ) {
      handle(new GenericClickEvent<ClickAction>(e.getModifiers(), e.getButton(), e.getCount()));
    }
  }
```