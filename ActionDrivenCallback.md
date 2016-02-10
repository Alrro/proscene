| Requirements: [Processing >= 2.x](https://processing.org/download/), and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (source code found under the "**examples**" section of the library package) |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|



Suppose that we know in advance the precise set of actions our library should support (a reasonable assumption), together with the hardware needed to carrying them out. By design, our framework expects groups of these actions to be carried out only by some specific hardware (or group of events), even simultaneously, i.e., to performing different actions, on different objects, eventually controlled with a specific group of events at the same time, such as within a collaborative immersive experience setup.

By just putting little extra effort, in the example `ReactableCircles` we show how to implement a much richer callback mechanism than the one [previously introduced](SimpleCallback.md). In this tutorial we will:

  1. Declare groups of actions defined respect to a _global_ set.
  1. Implement a much more expressive `TerseHandling` agent able not only to transform input events, but to parse the resulting `TerseHandling` events counterparts into actions, using generic parameterized **profiles**. Each individual profile holds a mapping between `TerseHandling` **shortcuts** and user-defined actions, providing reacher means to characterize `TerseHandling` events and hence allowing to trigger more final user actions unambiguously. <a href='Hidden comment: , dubbed _generic input event sketching_'></a>
  1. Implement an object grabber, define its behavior according to a given group of actions (or to the global set), and register it at the agent.
  1. Handle agents and events calling `TerseHandler.handle()` in the main event loop, just as we did in the [previous tutorial](SimpleCallback.md).

# Declaring groups of actions respect to a global set #

Suppose then that our global set of actions comprising our library functionality is defined as follows:

```
public enum GlobalAction {
  CHANGE_COLOR, 
  CHANGE_STROKE_WEIGHT, 
  CHANGE_POSITION, 
  CHANGE_SHAPE
}
```

To circumvent some of the limitations found within Java enums, we implement action sub-sets using a generic `interface` expected to be parametarized with the global `enum` action set, and then defining a one-to-one mapping among the local subset and the global set. It sounds a bit intimidating, but actually it is quite simple:

```
public enum MotionAction implements Actionable<GlobalAction> {
  //Define the mapping using a proper constructor
  CHANGE_POSITION(GlobalAction.CHANGE_POSITION), 
  CHANGE_SHAPE(GlobalAction.CHANGE_SHAPE);

  // Return the global mapping just defined above.
  @Override
  public GlobalAction referenceAction() {
    return act;
  }
  ...
  // local action requires two degrees-of-freedom
  @Override
  public int dofs() {
    return 2;
  }

  GlobalAction act;

  MotionAction(GlobalAction a) {
    act = a;
  }
}
```

Same as with the `ClickActions`, i.e., actions to be handled as mouse clicked events are triggered (or simulated).

```
public enum ClickAction implements Actionable<GlobalAction> {
  //Define the mapping using a proper constructor
  CHANGE_COLOR(GlobalAction.CHANGE_COLOR), 
  CHANGE_STROKE_WEIGHT(GlobalAction.CHANGE_STROKE_WEIGHT);

  // Return the global mapping defined above.
  @Override
  public GlobalAction referenceAction() {
    return act;
  }
  ...
  // local "click" action requires no degrees-of-freedom?
  // yes, why not?
  @Override
  public int dofs() {
    return 0;
  }

  GlobalAction act;

  ClickAction(GlobalAction a) {
    act = a;
  }
}
```

# Implementing a generic `TerseHandling` agent using parameterized profiles #

We now need to choose a generic `TerseHandling` agent where to start from, and here we take a `GenericMotionAgent` which seems to be fine suiting our needs (for the different options, please refer to the `remixlab.tersehandling.duoable.agent` package): it precisely holds a `GenericMotionProfile` and a `GenericClickProfile` (for the different profile options, please refer to the `remixlab.tersehandling.duoable.profile` package).

As with any generic class, its type is defined according to its parameterization. Here we:

  1. Parameterize the `GenericMotionProfile` using the `MotionAction`, and the resulting type to parameterize the `GenericMotionAgent`.
  1. Parameterize the `GenericClickProfile` using the `ClickAction`, and the resulting type to complete the parameterization of the `GenericMotionAgent`.
  1. Finally, parameterize the events using the `MotionAction` and the `ClickAction` too (the set of provided generic events reflect those of their non-generic counterparts, shares the same inheritance tree, and may be found at the `remixlab.tersehandling.event` package).

Observe that, while not strictly necessary, we parameterize events to preserve both, consistency and simplicity, i.e., it encourages a given event and the action it encapsulates to share the same DOFs, and it allows to keep the following method signature: `AbstractGrabber.performInteraction(TerseEvent event)`, instead of the more complex and ambiguous: `AbstractGrabber.performInteraction(TerseEvent event, Actionable<?> action)`.

Also note that each time we generate a `GenericDOF2Event`, we passed to it its predecesor. In such a way we define a _relative event_: by taking a sequence of such events we expect to generate an action. In constrast to that, an _absolute event_ can generate an action by its own, and therefore no event sequence is needed, i.e., no predecesor is needed to instantiate it.

```
//here we do what what was just so poorly said above:
public class MouseAgent extends GenericMotionAgent<GenericMotionProfile<MotionAction>, GenericClickProfile<ClickAction>> implements EventConstants {
  // Parameterize the events too:
  GenericDOF2Event<MotionAction> event, prevEvent;
  public MouseAgent(TerseHandler scn, String n) {
    super(new GenericMotionProfile<MotionAction>(), 
    new GenericClickProfile<ClickAction>(), scn, n);
    //Here we can define some default bindings:
    //(after all, this is what we were after)
    //Left button + 1 click -> binds a CHANGE_COLOR click action
    clickProfile().setClickBinding(TH_LEFT, 1, ClickAction.CHANGE_COLOR);
    clickProfile().setClickBinding(TH_RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
    clickProfile().setClickBinding(TH_SHIFT, TH_RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
    profile().setBinding(TH_LEFT, MotionAction.CHANGE_POSITION);
    //Shift modifier + Left button -> binds a CHANGE_SHAPE mouse action
    profile().setBinding(TH_SHIFT, TH_LEFT, MotionAction.CHANGE_SHAPE);
    profile().setBinding(TH_RIGHT, MotionAction.CHANGE_SHAPE);
  }
  
  //Event listening and transformation:
  public void mouseEvent(processing.event.MouseEvent e) {
    //Event chosen to define the selection of grabbers      
    if ( e.getAction() == processing.event.MouseEvent.MOVE ) {
      event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(), e.getY(), e.getModifiers(), e.getButton());
      updateGrabber(event);
      prevEvent = event.get();
    }
    //... and the one chosen to perform actions:
    if ( e.getAction() == processing.event.MouseEvent.DRAG ) {
      event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(), e.getY(), e.getModifiers(), e.getButton());
      handle(event);
      prevEvent = event.get();
    }
    if ( e.getAction() == processing.event.MouseEvent.CLICK ) {
      //so "handle" just means: try to perform an action:
      handle(new GenericClickEvent<ClickAction>(e.getModifiers(), e.getButton(), e.getCount()));
    }
  }
}
```

# Implementing an _action-driven_ object grabber #

We know seek to switch among the different global actions (or local action if that was the preference) on the grabber, reacting to the different types of events (or it could be a single type, if local actions were the preference above):

```
public class GrabbableCircle extends AbstractGrabber {
  ...
  @Override
  public boolean checkIfGrabsInput(TerseEvent event) {
    if (event instanceof GenericDOF2Event) {
      float x = ((GenericDOF2Event<?>)event).getX();
      float y = ((GenericDOF2Event<?>)event).getY();
      //It now is an ellipse:
      return(pow((x - center.x), 2)/pow(radiusX, 2) + pow((y - center.y), 2)/pow(radiusY, 2) <= 1);
    }      
    return false;
  }

  //Switch on the global action (requires sometimes casting the event then):
  @Override
  public void performInteraction(TerseEvent event) {
    if (event instanceof Duoable) {
      switch ((GlobalAction) ((Duoable<?>)event).action().referenceAction()) {
        case CHANGE_COLOR:
        contourColour = color(random(100, 255), random(100, 255), random(100, 255));
        break;
      case CHANGE_STROKE_WEIGHT:
        if (event.isShiftDown()) {					
          if (sWeight > 1)
            sWeight--;
        }
        else			
          sWeight++;		
        break;
      case CHANGE_POSITION:
        setPosition( ((GenericDOF2Event<?>)event).getX(), ((GenericDOF2Event<?>)event).getY() );
        break;
        case CHANGE_SHAPE:
        radiusX += ((GenericDOF2Event<?>)event).getDX();
        radiusY += ((GenericDOF2Event<?>)event).getDY();
        break;
      }
    }
  }
}
```

# Handling agents #

Same as it was shown in the [previous example](CallBack.md). Now if you run the sketch (found complete at the zip package here), you should see something like this:

![http://otrolado.info/local/tersehandling-latest/snapshots/reactable.png](http://otrolado.info/local/tersehandling-latest/snapshots/reactable.png)

Move the mouse around, drag a circle once one is selected or click it!

You may also run the example online using tersehandling.js [here](http://otrolado.info/local/tersehandling-latest/TerseHandling.js/TuplesTerse/war/TuplesTerse.html) (documentation coming soon).