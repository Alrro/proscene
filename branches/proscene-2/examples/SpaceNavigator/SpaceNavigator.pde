import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.proscene.*;
import procontroll.*;
import net.java.games.input.*;

Scene scene;
Device dev;

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

// Sphere Variables
float globeRadius = 250;
int xDetail = 40;
int yDetail = 30;
float[] xGrid = new float[xDetail+1];
float[] yGrid = new float[yDetail+1];
float[][][] allPoints = new float[xDetail+1][yDetail+1][3];

// Texture
PImage texmap;

void setup() {
  size(700, 700, P3D);
  noStroke();   
  scene = new Scene(this);
  scene.setRadius(globeRadius*1.8f);
  scene.showAll();  
  //scene.setGridIsDrawn(false);
  //scene.setAxisIsDrawn(false);		
  scene.setInteractiveFrame(new InteractiveFrame(scene));
  scene.interactiveFrame().translate(new Vector3D(1.3f*globeRadius, 1.3f*globeRadius/2, 0));

  // press 'f' to draw frame selection hint
  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
  // press 'i' to switch the interaction between the camera frame and the interactive frame
  scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);

  // Define the RELATIVE mode Device.  
  openSpaceNavigator();
  // /*
  dev = new Device(scene);
  dev.addHandler(this, "feed");
  dev.setTranslationSensitivity(0.01f, 0.01f, 0.01f);
  dev.setRotationSensitivity(0.0001f, 0.0001f, 0.0001f);
  dev.setCameraMode(Device.CameraMode.GOOGLE_EARTH);
  scene.addDevice(dev);
  // */

  texmap = loadImage("world32k.jpg"); 
  setupSphere(globeRadius, xDetail, yDetail);
}

void draw() {  
  background(0);
  drawSphere(texmap);

  pushMatrix();
  scene.interactiveFrame().applyTransformation();//very efficient
  // Draw the interactive frame local axis
  scene.drawAxis(70);
  // Draw a box associated with the iFrame
  stroke(122);
  if (scene.interactiveFrameIsDrawn() || scene.interactiveFrame().grabsMouse()) {
    fill(0, 255, 255);
    box(50, 75, 60);
  }
  else {
    fill(0, 0, 255);
    box(50, 75, 60);
  }		
  popMatrix();
}

void openSpaceNavigator() {
  println(System.getProperty("os.name"));
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
}

void feed(Device d) {
  d.feedTranslation(sliderXpos.getValue(), sliderYpos.getValue(), sliderZpos.getValue());
  d.feedRotation(sliderXrot.getValue(), sliderYrot.getValue(), sliderZrot.getValue());
}

void keyPressed() {
  if ((key == 'u') || (key == 'U'))
    dev.nextCameraMode();
  if ((key == 'v') || (key == 'V'))
    dev.nextIFrameMode();
  if ((key == 'x') || (key == 'X')){
    if( scene.isLeftHanded() ) {
      scene.setRightHanded();
      println("set right handed coordinate system convention");
    }
    else{
      scene.setLeftHanded();
      println("set left handed coordinate system convention");
    }
  }
}

void setupSphere(float R, int xDetail, int yDetail) {
  // Create a 2D grid of standardized mercator coordinates
  for (int i = 0; i <= xDetail; i++) {
    xGrid[i]= i / (float) xDetail;
  } 
  for (int i = 0; i <= yDetail; i++) {
    yGrid[i]= i / (float) yDetail;
  }

  textureMode(NORMALIZED);

  // Transform the 2D grid into a grid of points on the sphere, using the inverse mercator projection
  for (int i = 0; i <= xDetail; i++) {
    for (int j = 0; j <= yDetail; j++) {
      allPoints[i][j] = mercatorPoint(R, xGrid[i], yGrid[j]);
    }
  }
}

float[] mercatorPoint(float R, float x, float y) {
  float[] thisPoint = new float[3];
  float phi = x*2*PI;
  float theta = PI - y*PI;

  thisPoint[0] = R*sin(theta)*cos(phi);
  thisPoint[1] = R*sin(theta)*sin(phi);
  thisPoint[2] = R*cos(theta);

  return thisPoint;
}

void drawSphere(PImage Map) {
  pushStyle();
  noStroke();
  for (int j = 0; j < yDetail; j++) {
    beginShape(TRIANGLE_STRIP);
    texture(Map);
    for (int i = 0; i <= xDetail; i++) {
      vertex(allPoints[i][j+1][0], allPoints[i][j+1][1], allPoints[i][j+1][2], xGrid[i], yGrid[j+1]);
      vertex(allPoints[i][j][0], allPoints[i][j][1], allPoints[i][j][2], xGrid[i], yGrid[j]);
    }
    endShape(CLOSE);
  }
  popStyle();
}

