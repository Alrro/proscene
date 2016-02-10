| Requirements: [Processing >= 2.x](https://processing.org/download/), and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (source code found under the "**examples**" section of the library package |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Running the **`ProScene2`** technological preview **`TerseHandling`** examples requires the following:

| Extra-requirement: [ProScene2 technological preview](http://otrolado.info/remixcam.zip) (source code found under the "**examples**" section of the library package) |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Install the above **`ProScene2`** bundle as any other **Processing** library (details found [here](http://wiki.processing.org/w/How_to_Install_a_Contributed_Library))



# Introduction #

This example is inspired by this [question](https://forum.processing.org/topic/proscene-rotate-camera-around-scene-center-according-to-2d-mouse-position) where a user wants to rotate the camera by just moving the mouse (i.e., without dragging it). Simple as it seems, this behavior was not possible to be cleanly implemented in **`ProScene1`**. Here we show how simple it's done in `ProScene2` using `TerseHandling`.

# Implementing a "`MouseMoveAgent`" #

To implement our `MouseMoveAgent` we extend a **`ProScene2`** `MouseAgent` which is a specialization of the `TerseHandling` `GenericWheeledMotionAgent`, and define (among others) the camera binding needed above:

```
public class MouseMoveAgent extends MouseAgent {
  GenericDOF2Event<Constants.DOF2Action> event, prevEvent;
  public MouseMoveAgent(AbstractScene scn, String n) {
    super(scn, n);
    // while camera rotation requires no mouse button press:
    cameraProfile().setBinding(DOF2Action.ROTATE); // -> MouseEvent.MOVE
    // camera translation requires a mouse left button press:
    cameraProfile().setBinding(TH_LEFT, DOF2Action.TRANSLATE); // -> MouseEvent.DRAG
    // Disable center and right button camera actions (inherited from MouseAgent):
    cameraProfile().setBinding(TH_RIGHT, null);
    cameraProfile().setBinding(TH_CENTER, null);
  }
  public void mouseEvent(processing.event.MouseEvent e) {
    //don't even necessary :P
    //if( e.getAction() == processing.event.MouseEvent.MOVE || e.getAction() == processing.event.MouseEvent.DRAG) {
    event = new GenericDOF2Event<Constants.DOF2Action>(prevEvent, e.getX() - scene.upperLeftCorner.getX(), e.getY() - scene.upperLeftCorner.getY(), e.getModifiers(), e.getButton());
    handle(event);
    prevEvent = event.get();
    //}
  }
}
```

# Switching between our two mouse agents #

Switching between our default mouse agent and the one we implemented above is straightforward. Just bear in mind **Processing** **`mouseEvent`** registration of our newly created agent:

```
public void keyPressed() {
  // We switch between the default mouse agent and the one we created:
  if ( key != ' ') return;
  if ( !scene.terseHandler().isAgentRegistered(agent) ) {
    scene.terseHandler().registerAgent(agent);
    //Processing mouseEvent registration of our agent is necessary:
    scene.parent.registerMethod("mouseEvent", agent);
    scene.disableDefaultMouseAgent();
  }
  else {
    scene.terseHandler().unregisterAgent(agent);
    scene.parent.unregisterMethod("mouseEvent", agent);
    scene.enableDefaultMouseAgent();
  }
}
```