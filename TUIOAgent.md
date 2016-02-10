| Requirements: [Processing >= 2.x](https://processing.org/download/), and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (source code found under the "**examples**" section of the library package) |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

We are happy to offer this new example in collaboration with [Edumo](http://edumo.net/wp/), whom we are grateful. Running this example requires the following:

| Extra-requirements: 1. [TUIO Processing client](http://www.tuio.org/?processing); and, 2. [tuiodroid](https://code.google.com/p/tuiodroid/) (if you have an Android enable device) and/or a [tuiosimulator](http://www.tuio.org/?software) (if you don't)|
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|



This example shows how to plug-in, and setup a touch device allowing to select and control the circles in our [previous example](ActionDrivenCallback.md) with it. To make it more interesting, we will allow concurrent finger control, so that various circles can be manipulated with different fingers at the same time. We will also keep the mouse agent intact, so that we still can manipulate the circles using the mouse, as we have done it so far. This example requires the [TUIO Processing client](http://www.tuio.org/?processing) and, either a TUIO tracker such as [android's](https://code.google.com/p/tuiodroid/) (if you have an Android enable device) or a [TUIO simulator](http://www.tuio.org/?software) (if you don't), to send TUIO messages from.

To achieve this extra-functionality we will:

  1. Implement a TUIOAgent.
  1. Register the tuioAgent by updating our scene setup.

# Implementing the TUIOAgent #

We declare our TUIOAgent in the same exact way [we've done so far](ActionDrivenCallback#Implementing_a_generic_TerseHandling_agent_using_parameterized_p.md) with our MouseAgent, and then
defining the CHANGE\_POSITION motion action binding:

```
public class TUIOAgent extends GenericMotionAgent<GenericMotionProfile<MotionAction>, GenericClickProfile<ClickAction>> implements EventConstants {
  ...
  //We use the profile to define the CHANGE_POSITION motion action binding:
  profile().setBinding(TH_LEFT, MotionAction.CHANGE_POSITION);
  ...
}
```

and declare a **[**Finger, circle**]** tuple `HashMap` agent extra-attribute, to keep track about which circle is being selected/manipulated by which finger:

```
Map<Integer, Grabbable> grabMap = new HashMap<Integer, Grabbable>();
```

now, each time a finger starts interaction we invoke `updateGrabber(event)` to check whether or not a circle was picked, and then update the `grabMap` accordingly:

```
//this is the TUIO function telling us a new finger enters interaction:
public void addTuioCursor(TuioCursor tcur) {
    //Reduce tuio reported event into its TerseHandling counterpart:
    event = new GenericDOF2Event<MotionAction>(prevEvent, tcur.getScreenX(canvas.width), tcur.getScreenY(canvas.height), 
    0, 0);

    //so we just update the grabber in the agent pool, using our terseEvent:
    updateGrabber(event);
    Grabbable grabbable = this.trackedGrabber();

    //and if something gets caught, we update the hashMap:
    if (grabbable != null) {
      grabMap.put(tcur.getCursorID(), grabbable);
    }
  }
```

and, conversely, we also remove the tuple from the `grabMap` when a finger ends interaction as it leaves the tuio touch surface:

```
  // called when a cursor is removed from the scene
  public void removeTuioCursor(TuioCursor tcur) {
    event = new GenericDOF2Event<MotionAction>(prevEvent, -1000, -1000, 0, 0);
    // call it for consistency
    updateGrabber(event);
    // remove the tuple
    grabMap.remove(tcur.getCursorID());
  }
```

Finally, each time a finger is "dragged", our agent automatically handles the event:

```
  public void updateTuioCursor(TuioCursor tcur) {
    //retrieve the circle according to the given finger:
    Grabbable trackedGrabber = grabMap.get(tcur.getCursorID());
    if (trackedGrabber != null) {
      //reduce the tuio event into its TerseEvent counterpart:
      event = new GenericDOF2Event<MotionAction>(prevEvent, 
      tcur.getScreenX(canvas.width), 
      tcur.getScreenY(canvas.height), 0, TH_LEFT);
      //disable tracking to not intervene with the action being performed,
      //possibly, by more than one finger:
      disableTracking();
      setDefaultGrabber(trackedGrabber);
      handle(event);
      //renable tracking to allow more fingers to be added or removed:
      enableTracking();
    } 
    prevEvent = event.get();
  }
```

# Scene setup update #

Now that we have implemented our TUIOAgent, we just add it to our TerseHandler, using the scene [setup](http://processing.org/reference/setup_.html), and fill in with our circles:

```
...
MOUSEAgent mouseAgent;
TUIOAgent tuioAgent;
...
void setup() {
  terseHandler = new TerseHandler();
  //instantiate the new agent
  tuioAgent = new TUIOAgent(terseHandler, "my_tuio", g);
  mouseAgent = new MOUSEAgent(terseHandler, "my_mouse");
  registerMethod("mouseEvent", mouseAgent);
  circles = new GrabbableCircle[50];
  for (int i = 0; i < circles.length; i++) {
    //circles are added to the mouse agent by default
    circles[i] = new GrabbableCircle(this, g, mouseAgent);
    //we also add them to tuio's:
    tuioAgent.addInPool(circles[i]);
  }
  ...
}
```

Now you can drag the circles around using your fingers with a touch device.