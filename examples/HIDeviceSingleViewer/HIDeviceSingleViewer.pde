/**
 * HIDevice by Jean Pierre Charalambos.
 * 
 * This example illustrates the use of the HIDevice (Human Interaction
 * Device) class to manipulate your scene through sophisticated
 * interaction devices, such as the 3d space navigator (which is
 * required to run the sketch).
 *
 * This example requires the procontroll library:
 * http://www.creativecomputing.cc/p5libs/procontroll/
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */
 
import procontroll.*;
import net.java.games.input.*;
import remixlab.proscene.*;

//3DConnexion SpaceNavigator Demo for rotation, translation and buttons
//Device must be correctly installed
//procontroll must be installed

//Ralf Löhmer - rl@loehmer.de

Scene scene;
HIDevice dev;

ControllIO controll;
ControllDevice device; // my SpaceNavigator
ControllSlider sliderXpos; // Positions
ControllSlider sliderYpos;
ControllSlider sliderZpos;
ControllSlider sliderXrot; // Rotations
ControllSlider sliderYrot;
ControllSlider sliderZrot;
ControllButton button1; // Buttons
ControllButton button2;

void setup() {
  size(640, 360, P3D);
  controll = ControllIO.getInstance(this);
  String os = System.getProperty("os.name").toLowerCase();  
  if(os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0)
    device = controll.getDevice("3Dconnexion SpaceNavigator");// magic name for linux    
  else
    device = controll.getDevice("SpaceNavigator");//magic name, for windows

  device.setTolerance(5.00f);

  sliderXpos = device.getSlider(2);
  sliderYpos = device.getSlider(1);
  sliderZpos = device.getSlider(0);
  sliderXrot = device.getSlider(5);
  sliderYrot = device.getSlider(4);
  sliderZrot = device.getSlider(3);
  button1 = device.getButton(0);
  button2 = device.getButton(1);

  // ControllSlider.setMultiplier OR HIDevice.set*Sensitivity 
  // gives the same results. Be sure to just use one of them (no both!).
  // Here we use HIDevice.set*Sensitivity (see below)
  /**
  sliderXpos.setMultiplier(0.01f); // sensitivities
  sliderYpos.setMultiplier(0.01f);
  sliderZpos.setMultiplier(0.01f);
  sliderXrot.setMultiplier(0.0001f);
  sliderYrot.setMultiplier(0.0001f);
  sliderZrot.setMultiplier(0.0001f);
  */
  
  scene = new Scene(this);
  scene.setGridIsDrawn(true);
  scene.setAxisIsDrawn(true);		
  scene.setInteractiveFrame(new InteractiveFrame(scene));
  scene.interactiveFrame().translate(new PVector(30, 30, 0));

  // press 'i' to switch the interaction between the camera frame and the interactive frame
  scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);  
  // press 'f' to display frame selection hints
  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

  // How to use a HIDevice?
  // Option 1: Derivate from HIDevice and override the feeds
  /**
  dev = new HIDevice(scene) {
    public float feedXTranslation() {
      return sliderXpos.getValue();
    }
    public float feedYTranslation() {
      return sliderYpos.getValue();
    }
    public float feedZTranslation() {
      return sliderZpos.getValue();
    }
    public float feedXRotation() {
      return sliderXrot.getValue();
    }
    public float feedYRotation() {
      return sliderYrot.getValue();
    }
    public float feedZRotation() {
      return sliderZrot.getValue();
    }
  };
  // */

  // /**
  // Option 2: declare your own HIDevice and add a feed handler
  // Here we define a RELATIVE mode HIDevice (that's 
  // the space navigator).
  dev = new HIDevice(scene);
  // The following line would define an ABSOLUTE mode HIDevice
  // such as the wii or the kinect (see the HIDevice for details).
  //dev = new HIDevice(scene, HIDevice.Mode.ABSOLUTE);
  dev.addHandler(this, "feed");
  // */

  dev.setTranslationSensitivity(0.01f, 0.01f, 0.01f);
  dev.setRotationSensitivity(0.0001f, 0.0001f, 0.0001f);
  scene.addDevice(dev);
}

void feed(HIDevice d) {
  d.feedTranslation(sliderXpos.getValue(), sliderYpos.getValue(), sliderZpos.getValue());
  d.feedRotation(sliderXrot.getValue(), sliderYrot.getValue(), sliderZrot.getValue());
}

void draw() {
  background(33, 170, 170);
  noStroke();
  // draw scene:		
  fill(204, 102, 0);
  sphere(30);  
  // Save the current model view matrix
  pushMatrix();  
  scene.interactiveFrame().applyTransformation();//very efficient
  // Draw the interactive frame local axis
  scene.drawAxis(20);
  // Draw a box associated with the iFrame
  stroke(122);  
  if ( scene.interactiveFrameIsDrawn() ) {
    fill(0, 255, 255);
    box(10, 15, 12);
  }
  else {
    fill(0,0,255);
    box(10, 15, 12);
  }		
  popMatrix();
}

void keyPressed() {
  if ((key == 'u') || (key == 'U'))
    dev.nextCameraMode();
  if ((key == 'v') || (key == 'V'))
    dev.nextIFrameMode();
}