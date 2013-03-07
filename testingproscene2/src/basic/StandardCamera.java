package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
//import remixlab.remixcam.constraints.*;

//import remixlab.proscene.AxisPlaneConstraint.Type;

@SuppressWarnings("serial")
public class StandardCamera extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;

	public void setup() {
		size(640, 720, P3D);
		//size(640, 720, OPENGL);

		canvas = createGraphics(640, 360, P3D);
		//canvas = createGraphics(640, 360, OPENGL);
		scene = new Scene(this, canvas);
		scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
		// enable computation of the frustum planes equations (disabled by
		// default)
		scene.enableFrustumEquationsUpdate();
		scene.setGridIsDrawn(false);
		scene.addDrawHandler(this, "mainDrawing");
		
		/**
		WorldConstraint constraint2d = new WorldConstraint();
		PVector direction = new PVector(0,0,1);
		constraint2d.setRotationConstraint(Type.AXIS, direction);
		//constraint2d.setTranslationConstraint(Type.PLANE, direction);
		scene.camera().frame().setConstraint(constraint2d);
		*/		

		auxCanvas = createGraphics(640, 360, P3D);
		//auxCanvas = createGraphics(640, 360, OPENGL);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, auxCanvas, 0, 360);
		auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
		auxScene.setAxisIsDrawn(false);
		auxScene.setGridIsDrawn(false);
		auxScene.setRadius(200);
		auxScene.showAll();
		auxScene.addDrawHandler(this, "auxiliarDrawing");

		handleMouse();
	}

	public void mainDrawing(Scene s) {
		PGraphicsOpenGL p = s.pggl();
		p.background(0);
		p.noStroke();
		// the main viewer camera is used to cull the sphere object against its
		// frustum
		switch (scene.camera().sphereIsVisible(new Vector3D(0, 0, 0), 40)) {
		case VISIBLE:
			p.fill(0, 255, 0);
			p.sphere(40);
			break;
		case SEMIVISIBLE:
			p.fill(255, 0, 0);
			p.sphere(40);
			break;
		case INVISIBLE:
			break;
		}
	}

	public void auxiliarDrawing(Scene s) {
		mainDrawing(s);
		//s.drawCamera(scene.camera());		
		
		s.pg3d().pushStyle();
		s.pg3d().stroke(255,255,0);
		s.pg3d().fill(255,255,0,160);
		s.drawCamera(scene.camera());
		s.pg3d().popStyle();
	}

	public void draw() {
		handleMouse();
		canvas.beginDraw();
		scene.beginDraw();
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		auxScene.beginDraw();
		auxScene.endDraw();
		auxCanvas.endDraw();
		// We retrieve the scene upper left coordinates defined above.
		image(auxCanvas, auxScene.upperLeftCorner.x, auxScene.upperLeftCorner.y);
	}

	public void handleMouse() {
		if (mouseY < 360) {
			scene.enableMouseHandling();
			scene.enableKeyboardHandling();
			auxScene.disableMouseHandling();
			auxScene.disableKeyboardHandling();
		} else {
			scene.disableMouseHandling();
			scene.disableKeyboardHandling();
			auxScene.enableMouseHandling();
			auxScene.enableKeyboardHandling();
		}
	}
	
	public void keyPressed() {
		if(key == 'w') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();
			if(scene.isRightHanded())
				println("Scene is RIGHT handed");
			else
				println("Scene is LEFT handed");
		}
		if(key == 'W') {
			if(auxScene.isRightHanded())
				auxScene.setLeftHanded();			
			else
				auxScene.setRightHanded();
			if(auxScene.isRightHanded())
				println("auxScene is RIGHT handed");
			else
				println("auxScene is LEFT handed");
		}
		if(key == 'x') {
			scene.camera().frame().setScaling(-scene.camera().frame().scaling().x(),
					                           scene.camera().frame().scaling().y(),
					                           scene.camera().frame().scaling().z());
            println("scaling by -x");
		}
		if(key == 'X') {
			scene.camera().frame().setScaling(2*scene.camera().frame().scaling().x(),
                                              scene.camera().frame().scaling().y(),
                                              scene.camera().frame().scaling().z());                                              
			println("scaling by 2x");
		}
		if(key == 'y') {
			scene.camera().frame().setScaling( scene.camera().frame().scaling().x(),
					                          -scene.camera().frame().scaling().y(),
					                           scene.camera().frame().scaling().z());
            println("scaling by -y");
		}
		if(key == 'Y') {
			scene.camera().frame().setScaling(scene.camera().frame().scaling().x(),
											  2*scene.camera().frame().scaling().y(),
                                              scene.camera().frame().scaling().z());                                              
			println("scaling by 2y");
		}
		if(key == 'z') {
			scene.camera().frame().setScaling( scene.camera().frame().scaling().x(),
					                           scene.camera().frame().scaling().y(),
					                          -scene.camera().frame().scaling().z());
            println("scaling by -z");
		}
		if(key == 'Z') {
			scene.camera().frame().setScaling(scene.camera().frame().scaling().x(),
                                              scene.camera().frame().scaling().y(),
                                              2*scene.camera().frame().scaling().z());                                              
			println("scaling by 2z");
		}		
		if(key == 'q' || key == 'Q') {
			/**
			f1.setScaling( f1.scaling().x(),
					-f1.scaling().y(),
					f1.scaling().z()
					);*/
			scene.camera().lookAt(scene.center());
		}
		println("cam mag: " + scene.camera().frame().magnitude());
	}

	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.StandardCamera" });
	}
}