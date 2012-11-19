/**
 * Mouse Sensitivities.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates the involved variables that can be fine tuned
 * to control the mouse behavior, such as sensitivities and damping. 
 *
 * Follow the online indications and note that the displayed '+' '-'
 * symbols are interactive. Click on them to set the value of the variables.
 * 
 * Press 'd' to reset all variables to their default values.
 * Press 'u' to switch the control between camera and interactive frame.
 * Press 'v' to toggle the display of the controls.
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console. 
 */

import remixlab.proscene.*;

Scene scene;
ArrayList buttons;	
int xM = 10;

boolean isIFrame = false;
boolean dispControls = true;

PFont myFont;

void setup() {
  size(640, 360, P3D);
  
  myFont = createFont("FFScala", 14);
  textFont(myFont);
  textAlign(LEFT);

  scene = new Scene(this);
  scene.setGridIsDrawn(false);
  scene.setInteractiveFrame(new InteractiveFrame(scene));
  scene.interactiveFrame().translate(new PVector(30, 30, 0));
  // press 'i' to switch the interaction between the camera frame and the interactive frame
  scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
  // press 'f' to display frame selection hints
  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

  buttons = new ArrayList();

  buttons.add(new ClickButton(scene, new PVector(xM + 210, 50), "+", Sensitivity.ROTATION, true));		
  buttons.add(new ClickButton(scene, new PVector((xM + 210 + ((ClickButton)buttons.get(buttons.size()-1)).myWidth + 5), 50), "-", Sensitivity.ROTATION, false));		
  buttons.add(new ClickButton(scene, new PVector(xM + 210, 70), "+", Sensitivity.TRANSLATION, true));		
  buttons.add(new ClickButton(scene, new PVector((xM + 210 + ((ClickButton)buttons.get(buttons.size()-1)).myWidth + 5), 70), "-", Sensitivity.TRANSLATION, false));		
  buttons.add(new ClickButton(scene, new PVector(xM + 210, 90), "+", Sensitivity.SPINNING, true));		
  buttons.add(new ClickButton(scene, new PVector((xM + 210 + ((ClickButton)buttons.get(buttons.size()-1)).myWidth + 5), 90), "-", Sensitivity.SPINNING, false));		
  buttons.add(new ClickButton(scene, new PVector(xM + 210, 110), "+", Sensitivity.TOSSING, true));		
  buttons.add(new ClickButton(scene, new PVector((xM + 210 + ((ClickButton)buttons.get(buttons.size()-1)).myWidth + 5), 110), "-", Sensitivity.TOSSING, false));		
  buttons.add(new ClickButton(scene, new PVector(xM + 210, 130), "+", Sensitivity.WHEEL, true));		
  buttons.add(new ClickButton(scene, new PVector((xM + 210 + ((ClickButton)buttons.get(buttons.size()-1)).myWidth + 5), 130), "-", Sensitivity.WHEEL, false));		
  buttons.add(new ClickButton(scene, new PVector(xM + 210, 150), "+", Sensitivity.SPINNING_FRICTION, true));		
  buttons.add(new ClickButton(scene, new PVector((xM + 210 + ((ClickButton)buttons.get(buttons.size()-1)).myWidth + 5), 150), "-", Sensitivity.SPINNING_FRICTION, false));		
  buttons.add(new ClickButton(scene, new PVector(xM + 210, 170), "+", Sensitivity.TOSSING_FRICTION, true));		
  buttons.add(new ClickButton(scene, new PVector((xM + 210 + ((ClickButton)buttons.get(buttons.size()-1)).myWidth + 5), 170), "-", Sensitivity.TOSSING_FRICTION, false));		

  //Damping is disabled by default. We set it here:
  scene.camera().frame().setSpinningFriction(0.5f);
  scene.camera().frame().setTossingFriction(0.5f);
  scene.interactiveFrame().setSpinningFriction(0.5f);
  scene.interactiveFrame().setTossingFriction(0.5f);	

  scene.setRadius(150);
  scene.showAll();
}	

void draw() {
  background(0); 

  // Draw 3D scene first
  fill(204, 102, 0);
  box(20, 30, 40);		
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
  scene.interactiveFrame().applyTransformation();//very efficient
  // Draw an axis using the Scene static function
  scene.drawAxis(20);				
  // Draw a second box
  if (scene.interactiveFrame().grabsMouse()) {
    fill(255, 0, 0);
    box(12, 17, 22);
  }
  else if (scene.interactiveFrameIsDrawn()) {
    fill(0, 255, 255);
    box(12, 17, 22);
  }
  else {
    fill(0, 0, 255);
    box(10, 15, 20);
  }			
  popMatrix();

  // Finally draw 2D controls on top of the 3D scene
  displayControls();
}

void displayControls() {
  scene.beginScreenDrawing();
  fill(200);
  if ( !dispControls ) {
    displayText("Press 'v' to display info/controls", xM, 10);
    scene.endScreenDrawing();
    return;
  }
  else {
    displayText("Press 'v' to hide info/controls", xM, 10);
  }

  InteractiveFrame iFrame;		
  if ( isIFrame ) {
    iFrame = scene.interactiveFrame();
    displayText("Interactive frame sensitivities (Press 'u' to view/set those of Camera frame)", xM, 30);
  }
  else {
    iFrame = scene.camera().frame();
    displayText("Camera frame sensitivities (Press 'u' to view/set those of Interactive frame)", xM, 30);
  }

  fill(200, 255, 0);
  displayText(equals(iFrame.rotationSensitivity(), 1.0f) ? "Rotation sensitivity" : "Rotation sensitivity *", xM, 50);		
  displayText(String.format("%.2f", iFrame.rotationSensitivity()), xM + 165, 50);
  displayText(equals(iFrame.translationSensitivity(), 1.0f) ? "Translation sensitivity" : "Translation sensitivity *", xM, 70);		
  displayText(String.format("%.2f", iFrame.translationSensitivity()), xM + 165, 70);
  displayText(equals(iFrame.spinningSensitivity(), 0.3f) ? "Spinning sensitivity" : "Spinning sensitivity *", xM, 90);		
  displayText(String.format("%.2f", iFrame.spinningSensitivity()), xM + 165, 90);		
  displayText(equals(iFrame.tossingSensitivity(), 0.3f) ? "Tossing sensitivity" : "Tossing sensitivity *", xM, 110);
  displayText(String.format("%.2f", iFrame.tossingSensitivity()), xM + 165, 110);
  displayText(equals(iFrame.wheelSensitivity(), 20.0f) ? "Wheel sensitivity" : "Wheel sensitivity *", xM, 130);
  displayText(String.format("%.2f", iFrame.wheelSensitivity()), xM + 165, 130);		
  displayText(equals(iFrame.spinningFriction(), 0.0f) ? "Spinning friction" : "Spinning friction *", xM, 150);
  displayText(String.format("%.2f", iFrame.spinningFriction()), xM + 165, 150);
  displayText(equals(iFrame.tossingFriction(), 1.0f) ? "Tossing friction" : "Tossing friction *", xM, 170);
  displayText(String.format("%.2f", iFrame.tossingFriction()), xM + 165, 170);

  for (int i = 0; i < buttons.size(); i++)
    ( (ClickButton) buttons.get(i)).display();

  fill(200);
  if (!areDefaultsSet(iFrame))
    displayText("Press 'd' to set sensitivities to their default values", xM, 190);

  scene.endScreenDrawing();
}

void increaseSensitivity(Sensitivity sens) {
  if (isIFrame)
    increaseSensitivity(scene.interactiveFrame(), sens);
  else
    increaseSensitivity(scene.camera().frame(), sens);
}

void decreaseSensitivity(Sensitivity sens) {
  if (isIFrame)
    decreaseSensitivity(scene.interactiveFrame(), sens);
  else
    decreaseSensitivity(scene.camera().frame(), sens);
}	

void increaseSensitivity(remixlab.proscene.InteractiveFrame iFrame, Sensitivity sens) {
  changeSensitivity(iFrame, sens, true);
}

void decreaseSensitivity(remixlab.proscene.InteractiveFrame iFrame, Sensitivity sens) {
  changeSensitivity(iFrame, sens, false);
}	

void changeSensitivity(remixlab.proscene.InteractiveFrame iFrame, Sensitivity sens, boolean increase) {
  float step = 1;
  float res;
  switch (sens) {
  case ROTATION:
    step = increase ? 0.5f : -0.5f;
    res = iFrame.rotationSensitivity() + step;
    if (0<= res && res <=10)
      iFrame.setRotationSensitivity(res);			
    break;
  case TRANSLATION:
    step = increase ? 0.5f : -0.5f;
    res = iFrame.translationSensitivity() + step;
    if (0<= res && res <=10)
      iFrame.setTranslationSensitivity(res);
    break;
  case SPINNING:
    step = increase ? 0.1f : -0.1f;
    res = iFrame.spinningSensitivity() + step;
    if (0<= res && res <=100)
      iFrame.setSpinningSensitivity(res);
    break;
  case TOSSING:
    step = increase ? 0.1f : -0.1f;
    res = iFrame.tossingSensitivity() + step;
    if (0<= res && res <=100)
      iFrame.setTossingSensitivity(res);
    break;		
  case WHEEL:
    step = increase ? 5 : -5;
    res = iFrame.wheelSensitivity() + step;
    if (-100<= res && res <=100)
      iFrame.setWheelSensitivity(res);
    break;
  case SPINNING_FRICTION:
    step = increase ? 0.05f : -0.05f;
    res = iFrame.spinningFriction() + step;
    if (0<= res && res <=1)
      iFrame.setSpinningFriction(res);
    break;
  case TOSSING_FRICTION:
    step = increase ? 0.05f : -0.05f;
    res = iFrame.tossingFriction() + step;
    if (0<= res && res <=1)
      iFrame.setTossingFriction(res);
    break;
  }
}

boolean areDefaultsSet(remixlab.proscene.InteractiveFrame iFrame) {
  if (equals(iFrame.rotationSensitivity(), 1.0f) &&
    equals(iFrame.translationSensitivity(), 1.0f) &&
    equals(iFrame.spinningSensitivity(), 0.3f) &&
    equals(iFrame.tossingSensitivity(), 0.3f) &&
    equals(iFrame.wheelSensitivity(), 20.0f) &&
    equals(iFrame.spinningFriction(), 0.0f) &&
    equals(iFrame.tossingFriction(), 1.0f))
    return true;
  return false;
}

void setDefaults(remixlab.proscene.InteractiveFrame iFrame) {
  iFrame.setRotationSensitivity(1.0f);
  iFrame.setTranslationSensitivity(1.0f);
  iFrame.setSpinningSensitivity(0.3f);
  iFrame.setTossingSensitivity(0.3f);
  iFrame.setWheelSensitivity(20.0f);
  iFrame.setSpinningFriction(0.0f);
  iFrame.setTossingFriction(1.0f);
}

void displayText(String text, int x, int y) {
  int width = (int) textWidth(text);
  int height = (int) (textAscent() + textDescent());
  pushStyle();
  text(text, x, y, width, height);
  popStyle();
}

static boolean equals(float a, float b) {
  if (abs(a-b) < 0.01f)
    return true;
  return false;
}	

void keyPressed() {
  if (key == 'u' || key == 'U') {
    isIFrame = !isIFrame;
  }
  if (key == 'v' || key == 'V') {
    dispControls = !dispControls;
    for (int i = 0; i < buttons.size(); i++)
      if (dispControls)
        scene.addInMouseGrabberPool((ClickButton) buttons.get(i));
      else
        scene.removeFromMouseGrabberPool((ClickButton) buttons.get(i));
  }		
  if (key == 'd' || key == 'D') {
    if ( isIFrame )
      setDefaults( scene.interactiveFrame() );
    else
      setDefaults( scene.camera().frame() );
  }
}
