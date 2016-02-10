| Requirements: [Processing >= 2.x](https://processing.org/download/), and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (source code found under the "**examples**" section of the library package |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Running the **`ProScene2`** technological preview **`TerseHandling`** examples requires the following:

| Extra-requirement: [ProScene2 technological preview](http://otrolado.info/remixcam.zip) (source code found under the "**examples**" section of the library package) |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Install the above **`ProScene2`** bundle as any other **Processing** library (details found [here](http://wiki.processing.org/w/How_to_Install_a_Contributed_Library))



# Introduction #

Every **`ProScene2`** application holds two agents by default: a keyboard and a mouse. We begin these (sub) series illustrating how to disable them, implementing low-level event handling using other `TerseHandling` means instead.

# Disabling high-level handling #

Disabling the default agents typically takes place in the setup function:

```
public void setup() {
  ...
  scene.disableDefaultKeyboardAgent();
  scene.disableDefaultMouseAgent();
  ...
}
```

# Low-level handling #

**Processing** "low-level" event handling requires to override **`mouse*`()** and the **`keyPressed()`** methods.

We override a **`mouseMove()`** method to reduce **Processing** `mouseX` and `mouseY` variables into a **`TerseHandling`** `GenericDOF2Event` and use that event to check if the mouse grabs an `InteractiveFrame`:

```
public void mouseMoved() {
  // mouseX and mouseY are reduced into a GenericDOF2Event
  event = new GenericDOF2Event<Constants.DOF2Action>(prevEvent, (float) mouseX, (float) mouseY);
  // iFrame may be grabbing the mouse input in two cases:
  // Enforced by the 'y' key
  if (enforced)
    iFrameGrabsInput = true;
  // or if the mouse position is close enough the the iFrame position:
  else
    iFrameGrabsInput = iFrame.checkIfGrabsInput(event);		
  prevEvent = event.get();
}
```

Using TerseEventTuples a mouse drag will cause some (mouse) dof2 actions to be executed:

```
public void mouseDragged() {
  // a mouse drag will cause action execution without involving any agent:
  event = new GenericDOF2Event<Constants.DOF2Action>(prevEvent, (float) mouseX, (float) mouseY, mouseAction);
  // the action will be executed by the iFrame or the camera:
  if (iFrameGrabsInput)
    scene.terseHandler().enqueueEventTuple(new EventGrabberTuple(event, iFrame));
  else
    scene.terseHandler().enqueueEventTuple(new EventGrabberTuple(event, scene.viewport().frame()));
  prevEvent = event.get();
}
```

TerseEventTuples are also used to perform some `ProScene2` (keyboard) dof0 actions:

```
public void keyPressed() {
  // All keyboard action in proscene are performed by the Scene.
  // Here we define two keyboard actions
  if (key == 'a' || key == 'g') {
    if (key == 'a')
      keyAction = Constants.KeyboardAction.DRAW_GRID;
    if (key == 'g')
      keyAction = Constants.KeyboardAction.DRAW_AXIS;
    kEvent = new GenericKeyboardEvent<Constants.KeyboardAction>(key, keyAction);      
    scene.terseHandler().enqueueEventTuple(new EventGrabberTuple(kEvent, scene));
  }
  // Grabbing the iFrame may be done with the keyboard:
  if (key == 'y') {
    enforced = !enforced;
    if (enforced)
      iFrameGrabsInput = true;
    else
      iFrameGrabsInput = false;
  }	
  // The default mouse action (to be performed when dragging it) may be change here:	
  if (key == 'c')
    if (mouseAction == Constants.DOF2Action.ROTATE)
      mouseAction = Constants.DOF2Action.TRANSLATE;
    else
      mouseAction = Constants.DOF2Action.ROTATE;
}
```