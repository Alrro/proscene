| Requirements: [Processing >= 2.x](https://processing.org/download/), and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (source code found under the "**examples**" section of the library package) |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Running this example requires the following:

| Extra-requirements: 1. The [proCONTROLL library](http://creativecomputing.cc/p5libs/procontroll/); and, 2. A [space navigator](http://en.wikipedia.org/wiki/3Dconnexion)|
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------|



Here we're going to implement an Agent "feed" from the output generated by a physical device reported by a third party hardware controller.

Our goal is to implement an `HIDAgent` to control our [ActionDrivenCallback example](ActionDrivenCallback.md) scene using a [6DOF space navigator](http://en.wikipedia.org/wiki/3Dconnexion) (after which we name the example in the library relating this tutorial), keeping our `MouseAgent` as object tracker.

Here we will show how little extra-effort is needed to implement this much more involved setup.

# Adding a new action #

We begin by adding a new global action, `CHANGE_POS_SHAPE`, to change our objects shape and position "at once" using our 6DOF **space navigator**:

```
public enum GlobalAction {
  CHANGE_COLOR, 
  CHANGE_STROKE_WEIGHT, 
  CHANGE_POSITION, 
  CHANGE_SHAPE,
  // this is the new action
  CHANGE_POS_SHAPE
}
```

and declare a new 6DOF subset of actions (holding just the new one):

```
public enum SpaceAction implements Actionable<GlobalAction> {
  CHANGE_POS_SHAPE(GlobalAction.CHANGE_POS_SHAPE);

  @Override
  public GlobalAction referenceAction() {
    return act;
  }
  ...
  @Override
  public int dofs() {
    return 6;
  }
  ...
}
```

# Overriding an event feed #

We will implement our `HIDAgent` similarly as we have done it before with our `MouseAgent`, i.e., by extending a paremeterized `GenericMotionAgent`, but this time using the `SpaceAction` instead of the `MotionAction`.

```
public class HIDAgent extends GenericMotionAgent<GenericMotionProfile<SpaceAction>, GenericClickProfile<ClickAction>> implements EventConstants {
  public HIDAgent(TerseHandler h, String n) {
    super(new GenericMotionProfile<SpaceAction>(), new GenericClickProfile<ClickAction>(), h, n);
    //default bindings: no button needed to be pressed to execute an action:
    profile().setBinding(SpaceAction.CHANGE_POS_SHAPE);
    //Tracking will only be performed by our MouseAgent:
    disableTracking();
    //by trial an error we arrived at the following space navigator callibration
    setSensitivities(0.01, 0.01, 0.01, 0.0001, 0.0001, 0.0001);
  }

  //Here we convert our external event (the one reported by a third party hardware controller)
  //to its TerseHandling counterpart, this time an "absolute event"
  //(please remembered the notion from our past GlobalActions tutorial):
  @Override
  public GenericDOF6Event<SpaceAction> feed() {
    return new GenericDOF6Event<SpaceAction>(sliderXpos.getValue(), sliderYpos.getValue(), sliderZpos.getValue(),
                                             sliderXrot.getValue(), sliderYrot.getValue(), sliderZrot.getValue(), 0, 0);
  }
}
```

# `Updating our MouseAgent` #

Each `TerseHandling` **Agent** holds a grabber pool and a default grabber which need no to be in that pool. An Agent can only grab a single object at a given frame. It attempts to do that by first querying the objects in the pool, and, if there's no reply, checking if a default grabber was set.

Therefore, after disabling object tracking on the `HIDAgent`, we simply instruct our `MouseAgent` to set our HIDAgent default grabber, whenever an object from its own grabber pool gets tracked:

```
hidAgent.setDefaultGrabber(trackedGrabber());
```

That is all there's to it. Our `MouseAgent` `mouseEvent` method should now look like this:

```
  public void mouseEvent(processing.event.MouseEvent e) {      
    if ( e.getAction() == processing.event.MouseEvent.MOVE ) {
      event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(), e.getY(), e.getModifiers(), e.getButton());
      updateGrabber(event);
      // and here it comes the new line:
      hidAgent.setDefaultGrabber(trackedGrabber());
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.DRAG ) {
      event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(), e.getY(), e.getModifiers(), e.getButton());
      handle(event);
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.CLICK ) {
      handle(new GenericClickEvent<ClickAction>(e.getModifiers(), e.getButton(), e.getCount()));
    }
  }
```

# Updating our reactable circles #

Finally, we implement our reactable circle 6DOF action with the code in the following `GrabbableCircle.performInteraction` excerpt:

```
      //Here we only take the three event translations and forget about the rotations.
      //Actually, we also could have converted our DOF6Event into a DOF3Event, lossing 3DOFs.
      //However, that's part of another future tutorial. In the mean time please check
      //the remixlab.tersehandling.duoable.event.* API
      case CHANGE_POS_SHAPE:
        radiusX += ((GenericDOF6Event<?>)event).getZ();
        radiusY += ((GenericDOF6Event<?>)event).getZ();
        center.x += ((GenericDOF6Event<?>)event).getX();
        center.y += ((GenericDOF6Event<?>)event).getY();
        break;
```

Now you can play with a space navigator to control (change position and shape) some of the objects you declare within your Processing sketch.