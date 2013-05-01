package deviceGrabbers.cameraInterpolation;

import java.util.ArrayList;

import deviceGrabbers.buttons.Button2D;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.Vector3D;

public class CameraInterpolation extends PApplet {
	Scene scene;
	ArrayList buttons;
	int h;
	int fSize = 16;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);

	  //create a camera path and add some key frames:
	  //key frames can be added at runtime with keys [j..n]
	  scene.camera().setPosition(new Vector3D(80,0,0));
	  scene.camera().lookAt( scene.camera().sceneCenter() );
	  scene.camera().addKeyFrameToPath(1);

	  scene.camera().setPosition(new Vector3D(30,30,-80));
	  scene.camera().lookAt( scene.camera().sceneCenter() );
	  scene.camera().addKeyFrameToPath(1);

	  scene.camera().setPosition(new Vector3D(-30,-30,-80));
	  scene.camera().lookAt( scene.camera().sceneCenter() );
	  scene.camera().addKeyFrameToPath(1);

	  scene.camera().setPosition(new Vector3D(-80,0,0));
	  scene.camera().lookAt( scene.camera().sceneCenter() );
	  scene.camera().addKeyFrameToPath(1);

	  //re-position the camera:
	  scene.camera().setPosition(new Vector3D(0,0,1));
	  scene.camera().lookAt( scene.camera().sceneCenter() );
	  scene.showAll();

	  //drawing of camera paths are toggled with key 'r'. 
	  scene.setCameraPathsAreDrawn(true);

	  buttons = new ArrayList(6);
	  for (int i=0; i<5; ++i)
	    buttons.add(null);
	  
	  Button2D button = new ClickButton(scene, new Vector3D(10,5), fSize, 0);
	  h = button.myHeight;
	  buttons.set(0, button);
	}

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);

	  updateButtons();
	  displayButtons();
	}

	void updateButtons() {
	  for (int i = 1; i < buttons.size(); i++) {
	    // Check if CameraPathPlayer is still valid
	    if ((buttons.get(i) != null) && (scene.camera().keyFrameInterpolator(i) == null ) )
	      buttons.set(i, null);
	    // Or add it if needed
	    if ((scene.camera().keyFrameInterpolator(i) != null) && (buttons.get(i) == null))
	      buttons.set(i, new ClickButton(scene, new Vector3D(10, + ( i ) * ( h + 7 )), fSize, i));
	  }
	}

	void displayButtons() {
	  for (int i = 0; i < buttons.size(); i++) {
	    Button2D button = (Button2D) buttons.get(i);
	    if ( button != null )
	      button.display();
	  }
	}
}
