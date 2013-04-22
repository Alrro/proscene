package basic;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.constraints.*;
import geom.Box;
import geom.Sphere;

public class TestApi extends PApplet {
	Scene scene;
	InteractiveFrame f1, f2, f3, f4, f5;
	Vector3D v, p;
	Vector3D res;

	Box box1, box5;
	Point screenPoint = new Point();
	Camera.WorldPoint wp;
	Vector3D orig = new Vector3D();
	Vector3D dir = new Vector3D();
	Vector3D end = new Vector3D();
	
	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		
		scene.setRadius(500);
		
		scene.setFrameSelectionHintIsDrawn(true);
		v = new Vector3D(20,30,40);
		p = new Vector3D(40,30,20);
		
		f1 = new InteractiveFrame(scene);
		//f1 = new TestIFrame(scene);
		f1.translate(20, 30, 60);
		//f1.rotate(new Quaternion(new Vector3D(1,0,0), HALF_PI));
		f1.scale(2, 1.7f, -2.3f);
		//f1.scale(1, 1, -1);
		//f1.removeFromMouseGrabberPool();
		
		box1 = new Box(scene, f1);
		
		f2 = new InteractiveFrame(scene);
		//f2 = new TestIFrame(scene);
		f2.setReferenceFrame(f1);
		f2.translate(30, 20, -30);
		//f2.rotate(new Quaternion(new Vector3D(0,1,0), -QUARTER_PI));
		f2.scale(-1.2f, 1.1f, 0.8f);
		//f2.scale(-1, 1, 1);
		//f2.removeFromMouseGrabberPool();
		
		f3 = new InteractiveFrame(scene);
		//f3 = new TestIFrame(scene);
		f3.setReferenceFrame(f1);
		f3.translate(15, 20, -30);
		f3.rotate(new Quaternion(new Vector3D(0,1,0), -HALF_PI));		
		f3.scale(1, -1.3f, 1.2f);
		//f3.scale(1, -1, 1);
		//f3.removeFromMouseGrabberPool();
		
		f4 = new InteractiveFrame(scene);
		//f4 = new TestIFrame(scene);
		f4.setReferenceFrame(f2);
		f4.translate(20, 15, 30);
		f4.rotate(new Quaternion(new Vector3D(0,1,0), QUARTER_PI));
		f4.scale(-1.3f, -0.9f, 0.8f);
		//f4.scale(-1, -1, 1);
		//f4.removeFromMouseGrabberPool();
		
		f5 = new InteractiveFrame(scene);
		//f5 = new TestIFrame(scene);
		f5.setReferenceFrame(f4);
		f5.translate(20, 15, 30);
		f5.rotate(new Quaternion(new Vector3D(0,1,0), QUARTER_PI));
		f5.scale(-1.3f, -0.9f, 0.8f);
		//f5.scale(-1, -1, 1);
		//f5.removeFromMouseGrabberPool();
		
		box5 = new Box(scene, f5);
		
		scene.setRadius(200);
		scene.showAll();
		
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);	
	}
	
	public void draw() {
		background(0);
		
		//scene.camera().frame().updateFlyUpVector();		
		//drawPrimitives(color(255));
		
		drawLine();
	    
		/**
		pushMatrix();		
		  scene.applyTransformation(f1);
		  scene.drawAxis(40);
		  drawPrimitives(color(255,0,0));
		  pushMatrix();
		    scene.applyTransformation(f2);
		    scene.drawAxis(40);
		    drawPrimitives(color(0,255,0));		        		    
		  popMatrix();
		popMatrix();
		// */
		
		// /**
		pushMatrix();		
		  scene.applyTransformation(f1);
		  scene.drawAxis(40);
		  drawPrimitives(color(255,0,0));
		  pushMatrix();		  
		    scene.applyTransformation(f3);
		    scene.drawAxis(40);
		    drawPrimitives(color(0,0,255));
		  popMatrix();
		  pushMatrix();
		    scene.applyTransformation(f2);
		    scene.drawAxis(40);
		    drawPrimitives(color(0,255,0));
		    pushMatrix();
		      scene.applyTransformation(f4);
		      scene.drawAxis(40);
		      drawPrimitives(color(125));
		      pushMatrix();
		        scene.applyTransformation(f5);
		        scene.drawAxis(40);
		        drawPrimitives(color(255,0,255));
		      popMatrix();	      
		    popMatrix();    		    
		  popMatrix();
		popMatrix();
		// */
		
		//testing
		
		/**
		// f2 -> world
		res = f2.inverseCoordinatesOf(p);
		drawArrow(res);
		// */
				
		/**
		// f2 -> f1
		res = f1.coordinatesOfFrom(p, f2);
		drawArrow(f1, res);
		// */
				
		/**
		//same as the prev one
		// f2 -> f1
		res = f2.coordinatesOfIn(p, f1);
		drawArrow(f1, res);
		// */
				
		/**
		// Ref. Frame(f1) -> f2
		res = f2.localCoordinatesOf(p);
		drawArrow(f2, res);
		// */
		
		/**
		// f3 -> f4
		res = f4.coordinatesOfFrom(p, f3);
		drawArrow(f4, res);
		// */		
				
		/**
		// f4 -> f3
		res = f4.coordinatesOfIn(p, f3);
		drawArrow(f3, res);
		// */
		
		/**
		v = new Vector3D(50,50,50);		 
		drawArrow(v);
		res = f1.transformOfNoScl(v);
		//res.print();
		drawArrow(f1, res);
		// */
		
		/**
		res = f2.transformOf(v);
		drawArrow(f2, res);
		// */
		
		/**
		res = f2.transformOfFrom(res, f1);
		drawArrow(f2, res);
		// */
	}
	
	public void drawLine() {
		box1.draw();
		box5.draw();
		
		if(wp != null)
		if(wp.found) {
			pushStyle();
			strokeWeight(5);
			this.stroke(255, 0, 0);
			this.point(wp.point.x(), wp.point.y(), wp.point.z());
			
			this.stroke(0, 0, 255);		
			this.line(orig.x(), orig.y(), orig.z(), end.x(), end.y(), end.z());
			//this.line(orig.x(), orig.y(), orig.z(), orig.x(), orig.y(), orig.z());
			popStyle();
		}
	}
	
	public void drawPrimitives(int color) {
		pushStyle();
		stroke(255,255,0);
		//line(0,0,0, v.x(), v.y(), v.z());
		popStyle();
		pushStyle();
		stroke(color);
		strokeWeight(5);
		point(p.x(), p.y(), p.z());
		popStyle();
	}
	
	public void drawArrow(Vector3D vec) {
		drawArrow(null, vec);
	}
	
	public void drawArrow(GeomFrame frame, Vector3D vec) {		
		if(frame != null) {
			pushMatrix();
			// Multiply matrix to get in the frame coordinate system.
			//scene.applyMatrix(frame.matrix());// local, is handy but inefficient
			//scene.applyMatrix(frame.worldMatrix());// world, is handy but inefficient 
			//scene.applyTransformation(frame);
			scene.applyWorldTransformation(frame);
			pushStyle();
			stroke(0,255,255);
			line(0,0,0, vec.x(), vec.y(), vec.z());
			popStyle();
			popMatrix();
		}
		else {
			pushStyle();
			stroke(0,255,255);
			line(0,0,0, vec.x(), vec.y(), vec.z());
			popStyle();
		}
	}
	
	public void mouseClicked() {		
		wp =  scene.camera().pointUnderPixel(new Point(mouseX, mouseY));
		if( wp.found ) {
			screenPoint.set(mouseX, mouseY);
			scene.camera().convertClickToLine(screenPoint, orig, dir);				
			end = Vector3D.add(orig, Vector3D.mult(dir, 1000.0f));
			orig.print();
			dir.print();
			end.print();
		}
	}
	
	public float distanceToSC1() {		
		return Math.abs((scene.camera().frame().coordinatesOf(scene.camera().sceneCenter())).vec[2]);//before scln		
	}
	
	public float distanceToSC2() {				
		Vector3D zCam = scene.camera().frame().zAxis();		
		zCam.normalize();
		Vector3D cam2SceneCenter = Vector3D.sub(scene.camera().position(), scene.camera().sceneCenter());
		return Math.abs(Vector3D.dot(cam2SceneCenter, zCam));
	}
	
	public float distanceToSC3() {	
		Vector3D zCam = scene.camera().frame().magnitude().z() > 0 ? scene.camera().frame().zAxis() : scene.camera().frame().zAxis(false);
		zCam.normalize();
		Vector3D cam2SceneCenter = Vector3D.sub(scene.camera().position(), scene.camera().sceneCenter());
		return Math.abs(Vector3D.dot(cam2SceneCenter, zCam));
	}

	public float distanceToARP1() {
		return Math.abs(scene.camera().cameraCoordinatesOf(scene.camera().frame().arcballReferencePoint()).vec[2]);//before scln		
	}
	
	public float distanceToARP2() {
		Vector3D zCam = scene.camera().frame().zAxis();	
		zCam.normalize();
		Vector3D cam2arp = Vector3D.sub(scene.camera().position(), scene.camera().arcballReferencePoint());
		return Math.abs(Vector3D.dot(cam2arp, zCam));
	}
	
	public float distanceToARP3() {
		Vector3D zCam = scene.camera().frame().magnitude().z() > 0 ? scene.camera().frame().zAxis() : scene.camera().frame().zAxis(false);
		zCam.normalize();
		Vector3D cam2arp = Vector3D.sub(scene.camera().position(), scene.camera().arcballReferencePoint());
		return Math.abs(Vector3D.dot(cam2arp, zCam));
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {
			Vector3D v = f4.zAxis();
			println("f4.zAxis(): " + v + " mag: " + v.mag());			
		}
		if(key == 'v' || key == 'V') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();			
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
		
		if(key == 'm' || key == 'M') {
			println("distanceToSC1(): " + distanceToSC1());
			println("distanceToSC2(): " + distanceToSC2());
			println("distanceToSC3(): " + distanceToSC3());
		}
		
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");
		println("zNear: " + scene.camera().zNear());
		println("zFar: " + scene.camera().zFar());
		println("cam mag: " + scene.camera().frame().magnitude());
	}
	
	public class TestIFrame extends InteractiveFrame {
		public TestIFrame(AbstractScene scn) {
			super(scn);
		}
		
		@Override			
		protected void deviceDragged3D(Point eventPoint, Camera camera) {
			int deltaY = 0;
			if(action != AbstractScene.DeviceAction.NO_MOUSE_ACTION) {
				deltaY = (int) (prevPos.y - eventPoint.y);//as it were LH
				if( scene.isRightHanded() )
					deltaY = -deltaY;
			}
			switch (action) {
			case ROTATE: {
				Vector3D trans = camera.projectedCoordinatesOf(position());
				Quaternion rot = deformedBallQuaternion((int)eventPoint.x, (int)eventPoint.y, trans.x(), trans.y(), camera);
				rot = iFrameQuaternion(rot, camera);
				computeMouseSpeed(eventPoint);
				setSpinningQuaternion(rot);
				//drawSpinningQuaternion();
				spin();
				prevPos = eventPoint;
				break;
			}
			default: {
				super.deviceDragged3D(eventPoint, camera);
			}
			}
		}
		
		@Override
		protected Quaternion deformedBallQuaternion(int x, int y, float cx, float cy, Camera camera) {			
			// Points on the deformed ball
            float px = rotationSensitivity() *                         ((int)prevPos.x - cx)                           / camera.screenWidth();
            float py = rotationSensitivity() * (scene.isLeftHanded() ? ((int)prevPos.y - cy) : ( cy - (int)prevPos.y)) / camera.screenHeight();
            float dx = rotationSensitivity() *                         (x - cx)             / camera.screenWidth();
            float dy = rotationSensitivity() * (scene.isLeftHanded() ? (y - cy) : (cy - y)) / camera.screenHeight();

			Vector3D p1 = new Vector3D(px, py, projectOnBall(px, py));
			Vector3D p2 = new Vector3D(dx, dy, projectOnBall(dx, dy));
			// Approximation of rotation angle Should be divided by the projectOnBall size, but it is 1.0
			Vector3D axis = p2.cross(p1);
			float angle = 2.0f * (float) Math.asin((float) Math.sqrt(axis.squaredNorm() / p1.squaredNorm() / p2.squaredNorm()));			
			return new Quaternion(axis, angle);
		}
		
		/**
		protected Quaternion iFrameQuaternion(Quaternion rot, Camera camera) {
			Vector3D trans = new Vector3D();	
			boolean twist = false;
			
			trans = rot.axis();
			trans = transformOfFrom(trans, camera.frame());
			
			Vector3D res = new Vector3D(trans);			
			// perform conversion			
			if (scaling().x() < 0 )	res.x(-trans.x());
			if (scaling().y() < 0 )	res.y(-trans.y());
			if (scaling().z() < 0 )	res.z(-trans.z());
			
			if(referenceFrame() != null)
				twist = (  referenceFrame().magnitude().x()
						 * referenceFrame().magnitude().y()
						 * referenceFrame().magnitude().z()
						 < 0);
			
			return new Quaternion(res, twist ? rot.angle() : -rot.angle());						
		}*/
		
		public void drawSpinningQuaternion() {
			if(spinningQuaternion()== null)
				return;
			Vector3D axis = ((Quaternion)spinningQuaternion()).axis();			
			axis = Vector3D.mult(axis, 60);
			((Scene)scene).pg3d().pushStyle();
			((Scene)scene).pg3d().pushMatrix();
			applyWorldTransformation();
			
			((Scene)scene).pg3d().noStroke();
			((Scene)scene).pg3d().fill(color(0, 126, 255, 10));			
			((Scene)scene).pg3d().sphere(50);
			
			((Scene)scene).pg3d().stroke(255,0,0);
			((Scene)scene).pg3d().strokeWeight(4);
			((Scene)scene).pg3d().line(0,0,0, axis.x(), axis.y(), axis.z());
						
			((Scene)scene).pg3d().popMatrix();
			((Scene)scene).pg3d().popStyle();
		}
	}
}
