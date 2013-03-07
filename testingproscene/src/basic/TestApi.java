package basic;

import processing.core.*;
import remixlab.proscene.*;

public class TestApi extends PApplet {
	Scene scene;
	//InteractiveFrame f1, f2, f3, f4;
	TestIFrame f1, f2, f3, f4;
	PVector v, p;
	PVector res;	
	boolean drawSpnQuat = false;
	
	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setFrameSelectionHintIsDrawn(true);
		v = new PVector(20,30,40);
		p = new PVector(40,30,20);
		
		//f1 = new InteractiveFrame(scene);
		f1 = new TestIFrame(scene);
		f1.translate(20, 30, 60);
		f1.rotate(new Quaternion(new PVector(1,0,0), HALF_PI));
		
		//f2 = new InteractiveFrame(scene);
		f2 = new TestIFrame(scene);
		f2.setReferenceFrame(f1);
		f2.translate(30, 20, -30);
		f2.rotate(new Quaternion(new PVector(0,1,0), -QUARTER_PI));
		
		//f3 = new InteractiveFrame(scene);
		f3 = new TestIFrame(scene);
		f3.setReferenceFrame(f1);
		f3.translate(15, 20, -30);
		f3.rotate(new Quaternion(new PVector(0,1,0), -HALF_PI));
		f3.removeFromMouseGrabberPool();
		
		//f4 = new InteractiveFrame(scene);
		f4 = new TestIFrame(scene);
		f4.setReferenceFrame(f2);
		f4.translate(20, 15, 30);
		f4.rotate(new Quaternion(new PVector(0,1,0), QUARTER_PI));
		f4.removeFromMouseGrabberPool();
		
		scene.setRadius(200);
		scene.showAll();
		
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		
		/**
		println("float.min: " + Float.MIN_VALUE);
		println("(float)Math.pow(2, -149): " + (float)Math.pow(2, -149));		
		println("Abstract scene float.min: " + AbstractScene.FLOAT_EPS);
		println("2f/Float.MIN_VALUE: " + 2f/Float.MIN_VALUE);
		println("2f/Float.MIN_VALUE: " + 2f/AbstractScene.FLOAT_EPS);
		*/
	}
	
	public void draw() {
		background(0);
		drawPrimitives(color(255));
		
		pushMatrix();	
		  scene.applyTransformation(f1);
		  scene.drawAxis(40);
		  drawPrimitives(color(255,0,0));		  
		  /**
		  pushMatrix;		  
		    scene.applyTransformation(f3);
		    scene.drawAxis(40);
		    drawPrimitives(color(0,0,255));
		  popMatrix;
		  // */		  
		  pushMatrix();
		    scene.applyTransformation(f2);
		    scene.drawAxis(40);
		    drawPrimitives(color(0,255,0));
		    /**
		    pushMatrix;
		      scene.applyTransformation(f4);
		      scene.drawAxis(40);
		      drawPrimitives(color(125));
		    popMatrix;
		    //*/		    		    
		  popMatrix();		  		  
		popMatrix();
		
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
				
		// /**
		// f4 -> f3
		res = f4.coordinatesOfIn(p, f3);
		drawArrow(f3, res);
		// */
		
		if (drawSpnQuat) {
			f1.drawSpinningQuaternion();
			f2.drawSpinningQuaternion();
		}
	}	
	
	public void drawPrimitives(int color) {
		pushStyle();
		stroke(255,255,0);
		//line(0,0,0, v.x, v.y, v.z);
		popStyle();
		pushStyle();
		stroke(color);
		strokeWeight(5);
		point(p.x, p.y, p.z);
		popStyle();
	}
	
	public void drawArrow(PVector vec) {
		drawArrow(null, vec);
	}
	
	public void drawArrow(Frame frame, PVector vec) {		
		if(frame != null) {
			pushMatrix();
			// Multiply matrix to get in the frame coordinate system.
			//scene.applyMatrix(frame.matrix);// local, is handy but inefficient
			//scene.applyMatrix(frame.worldMatrix);// world, is handy but inefficient 
			//scene.applyTransformation(frame);
			scene.applyWorldTransformation(frame);
			pushStyle();
			stroke(0,255,255);
			line(0,0,0, vec.x, vec.y, vec.z);
			popStyle();
			popMatrix();
		}
		else {
			pushStyle();
			stroke(0,255,255);
			line(0,0,0, vec.x, vec.y, vec.z);
			popStyle();
		}
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {
			drawSpnQuat = !drawSpnQuat;
		}
		if(key == 'v' || key == 'V') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();			
		}
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");
	}
	
	public class TestIFrame extends InteractiveFrame {
		public TestIFrame(Scene scn) {
			super(scn);
		}
		
		@Override			
		public void mouseDragged(Point eventPoint, Camera camera) {
			int deltaY = 0;
			if(action != Scene.MouseAction.NO_MOUSE_ACTION) {
				deltaY = (int) (prevPos.y - eventPoint.y);//as it were LH
				if( scene.isRightHanded() )
					deltaY = -deltaY;
			}
			switch (action) {
			case ROTATE: {
				PVector trans = camera.projectedCoordinatesOf(position());
				Quaternion rot = deformedBallQuaternion((int)eventPoint.x, (int)eventPoint.y,	trans.x, trans.y, camera);
				rot = iFrameQuaternion(rot, camera);
				computeMouseSpeed(eventPoint);
				setSpinningQuaternion(rot);
				drawSpinningQuaternion();
				spin();
				prevPos = eventPoint;
				break;
			}
			default: {				
				super.mouseDragged(eventPoint, camera);
			}
			}
		}
		
		@Override
		protected Quaternion deformedBallQuaternion(int x, int y, float cx, float cy, Camera camera) {
			// First determine how displacements should be interpreted
			boolean keepX = true;
			boolean keepY = scene.isLeftHanded();
			
		    // Points on the deformed ball
			float px = rotationSensitivity() * (keepX ? ((int)prevPos.x - cx) : (cx - (int)prevPos.x)) / camera.screenWidth();
			float py = rotationSensitivity() * (keepY ? ((int)prevPos.y - cy) : (cy - (int)prevPos.y)) / camera.screenHeight();
			float dx = rotationSensitivity() * (keepX ? (x - cx) : (cx - x)) / camera.screenWidth();
			float dy = rotationSensitivity() * (keepY ? (y - cy) : (cy - y)) / camera.screenHeight();		

			PVector p1 = new PVector(px, py, projectOnBall(px, py));
			PVector p2 = new PVector(dx, dy, projectOnBall(dx, dy));
			// Approximation of rotation angle Should be divided by the projectOnBall size, but it is 1.0
			PVector axis = p2.cross(p1);
			float angle = 2.0f * PApplet.asin(PApplet.sqrt(MathUtils.squaredNorm(axis) / MathUtils.squaredNorm(p1) / MathUtils.squaredNorm(p2)));
			return new Quaternion(axis, angle);
		}
		
		protected Quaternion iFrameQuaternion(Quaternion rot, Camera camera) {
			PVector trans = new PVector();
			
			/**
			trans.set(-rot.x, -rot.y, -rot.z);			
			trans = camera.frame().orientation().rotate(trans);
			trans = transformOf(trans);
			return new Quaternion(trans, rot.angle());
			// */
			
			/**
			trans = rot.axis();
			trans = camera.frame().inverseTransformOf(trans);
			trans = transformOf(trans);
			return new Quaternion(trans, -rot.angle());
			// */
			
			// /**
			trans = rot.axis();
			trans = transformOfFrom(trans, camera.frame());
			return new Quaternion(trans, -rot.angle());
			// */
		}
		
		public void drawSpinningQuaternion() {
			if(spinningQuaternion()== null)
				return;
			PVector axis = spinningQuaternion().axis();			
			axis = PVector.mult(axis, 60);
			scene.renderer().pushStyle();
			scene.renderer().pushMatrix();
			applyWorldTransformation();
			
			scene.renderer().noStroke();
			scene.renderer().fill(color(0, 126, 255, 10));			
			scene.renderer().sphere(50);
			
			scene.renderer().stroke(255,0,0);
			scene.renderer().strokeWeight(4);
			scene.renderer().line(0,0,0, axis.x, axis.y, axis.z);
						
			scene.renderer().popMatrix();
			scene.renderer().popStyle();
		}
	}
}
