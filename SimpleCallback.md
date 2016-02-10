| Requirements: [Processing >= 2.x](https://processing.org/download/), and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (source code found under the "**examples**" section of the library package) |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|



To implement a simple callback we will:

  1. Implement a simple `TerseHandling` Agent, which transforms [Processing mouse events](http://processing.org/reference/javadoc/core/processing/event/package-summary.html) into `TerseHandling` events.
  1. Implement an object grabber, define its behavior, and register it at the agent.
  1. Handle agents and events calling `TerseHandler.handle()` in the main event loop.

# Implementing a simple `TerseHandling` Agent #

Every `TerseHandling` **Agent** holds a pool of input **grabbers**, i.e., high-level objects which can react to user input, only one of which may grabbed the agent at a given time (or none). So here we begin by implementing our first `TerseHandling` **Agent**, which will be responsible for:

  1. Transforming **Processing** `MouseEvents` into `TerseHandling` DOF2Events.
  1. Defining which `TerseHandling` event will update the grabber in the Agent pool.
  1. Defining which `TerseHandling` event will actually trigger an action to be performed by the grabber (if there's one selected).

```
public class MouseAgent extends Agent {
  //Declare a DOF2Event (two degrees-of-freedom) event:
  DOF2Event event;

  public MouseAgent(TerseHandler scn, String n) {
    super(scn, n);
  }

  public void mouseEvent(processing.event.MouseEvent e) {
    // Convert the Processing mouse event into the DOF2Event just declared above
    event = new DOF2Event(e.getX(), e.getY());
    // A Processing mouse move event will update the agent grabber, i.e.,
    // it will seek if a grabber in the agent pool grabs the agent:
    if ( e.getAction() == processing.event.MouseEvent.MOVE )
      updateGrabber(event);
    // ... while a Processing mouse drag event will trigger an action on it,
    // provided that there's an object grabbing the Agent.
    // Note that it could have been the other way around,
    // i.e., a mouse move could have been perfectly used to trigger the action. Please refer to this discussion:
    // https://forum.processing.org/topic/proscene-rotate-camera-around-scene-center-according-to-2d-mouse-position
    if ( e.getAction() == processing.event.MouseEvent.DRAG )
      handle(event);
  }
}
```

# Implementing a simple object grabber #

Implementing an object grabber requires either to implement the `AbstractGrabbable` interface, or to derive from `AbstractGrabber`, which is a default implementation of that interface provided for convenience, and to implement its two abstract methods: `checkIfGrabsInput(TerseEvent event)`, and `performInteraction(TerseEvent event)`. Whilst the former will check if the object grabs the agent, the latter will attempt to perform an action on the event, provided the object is grabbing the agent.

```
public class GrabbableCircle extends AbstractGrabber {
  ...
  @Override
  public boolean checkIfGrabsInput(TerseEvent event) {
    if (event instanceof DOF2Event) {
      float x = ((DOF2Event)event).getX();
      float y = ((DOF2Event)event).getY();
      // Since each grabber has a circle shape, select it if the mouse position lies within it:
      return(pow((x - center.x), 2) + pow((y - center.y), 2) <= pow(radius, 2));
    }      
    return false;
  }

  @Override
  public void performInteraction(TerseEvent event) {
    // These functions just update randomly the color and position of the grabber.
    setColor();
    setPositionAndRadius();
  }
```

# Handling agents #

Finally, we declare a `MouseAgent`, a `TerseHandler` object, register our agent at the handler, and call `terseHandler.handle()` at the main event loop.

```
// Declare MouseAgent object, just defined above
MouseAgent agent;
// Declare a TerseHandler object
TerseHandler terseHandler;

void setup() {
  ...
  // Instantiate the TerseHandler object
  terseHandler = new TerseHandler();
  // Instantiate the MouseAgent.
  // Notice that the agent will automatically be added to the TerseHandler object.
  agent = new MouseAgent(terseHandler, "my_mouse");
  registerMethod("mouseEvent", agent);
  ...
}

void draw() {
  ...
  // Call the terseHandler handle() method.
  // In a future tutorial we will study what this function does, and how to bypass it
  // to handle TerseHandling events "at low level".
  terseHandler.handle();
}
```

If you run the sketch (found complete at the zip package here), you should see something like this:

![http://otrolado.info/local/tersehandling-latest/snapshots/boring.png](http://otrolado.info/local/tersehandling-latest/snapshots/boring.png)

Move the mouse around and drag a circle once one is selected!

You may also run the example online using tersehandling.js [here](http://otrolado.info/local/tersehandling-latest/TerseHandling.js/BoringClickAndDrag/war/Boring.html) (documentation coming soon).