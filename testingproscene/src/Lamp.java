import processing.core.*;
import remixlab.proscene.*;

public class Lamp {
	PApplet parent;
	InteractiveFrame [] frameArray;
	
	Lamp(PApplet p) {
		parent = p;
		frameArray = new InteractiveFrame[4];
		for (int i=0; i<4; ++i) {
			frameArray[i] = new InteractiveFrame();
			// Creates a hierarchy of frames.
			if (i>0) frame(i).setReferenceFrame(frame(i-1));
		}
		
		// Initialize frames
		frame(1).setTranslation(0.0f, 0.0f, 0.08f); // Base height
		frame(2).setTranslation(0.0f, 0.0f, 0.5f);  // Arm length
		frame(3).setTranslation(0.0f, 0.0f, 0.5f);  // Arm length
		
		frame(1).setRotation(new Quaternion(new PVector(1.0f,0.0f,0.0f), 0.6f));
		frame(2).setRotation(new Quaternion(new PVector(1.0f,0.0f,0.0f), -2.0f));
		frame(3).setRotation(new Quaternion(new PVector(1.0f,-0.3f,0.0f), -1.7f));
		
		// Set frame constraints
		WorldConstraint baseConstraint = new WorldConstraint();
		baseConstraint.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new PVector(0.0f,0.0f,1.0f));
		baseConstraint.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.0f,0.0f,1.0f));
		frame(0).setConstraint(baseConstraint);
		
		LocalConstraint XAxis = new LocalConstraint();
		XAxis.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN,  new PVector(0.0f,0.0f,0.0f));
		XAxis.setRotationConstraint   (AxisPlaneConstraint.Type.AXIS, new PVector(1.0f,0.0f,0.0f));
		frame(1).setConstraint(XAxis);
		frame(2).setConstraint(XAxis);
		
		LocalConstraint headConstraint = new LocalConstraint();
		headConstraint.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f,0.0f,0.0f));
		frame(3).setConstraint(headConstraint);
	}
	
	public void draw() {
		// Luxo's local frame
		parent.pushMatrix();
		frame(0).applyTransformation(parent);
		setColor( frame(0).grabsMouse() );
		drawBase();
		
		parent.pushMatrix();//not really necessary here
		frame(1).applyTransformation(parent);
		setColor( frame(1).grabsMouse() );
		drawCylinder();
		drawArm();		
		
		parent.pushMatrix();//not really necessary here
		frame(2).applyTransformation(parent);
		setColor( frame(2).grabsMouse() );
		drawCylinder();
		drawArm();		
		
		parent.pushMatrix();//not really necessary here
		frame(3).applyTransformation(parent);
		setColor( frame(3).grabsMouse() );
		drawHead();
		
		// Add light
		//spotLight(v1, v2, v3, x, y, z, nx, ny, nz, angle, concentration)
		parent.spotLight(155, 255, 255, 0, 0, 0, 0, 0, 1, PApplet.THIRD_PI, 1);
		
		parent.popMatrix();//frame(3)
		
		parent.popMatrix();//frame(2)
		
		parent.popMatrix();//frame(1)
		
		//totally necessary
		parent.popMatrix();//frame(0)
	}
	
	public void drawBase() {
		drawCone(0.0f, 0.03f, 0.15f, 0.15f, 30);
		drawCone(0.03f, 0.05f, 0.15f, 0.13f, 30);
		drawCone(0.05f, 0.07f, 0.13f, 0.01f, 30);
		drawCone(0.07f, 0.09f, 0.01f, 0.01f, 10);
	}

	public void drawArm() {
		parent.translate(0.02f, 0.0f, 0.0f);
		drawCone(0.0f, 0.5f, 0.01f, 0.01f, 10);
		parent.translate(-0.04f, 0.0f, 0.0f);  
		drawCone(0.0f, 0.5f, 0.01f, 0.01f, 10);		
		parent.translate(0.02f, 0.0f, 0.0f);
	}

	public void drawHead() {
		drawCone(-0.02f, 0.06f, 0.04f, 0.04f, 30);
		drawCone(0.06f, 0.15f, 0.04f, 0.17f, 30);
		drawCone(0.15f, 0.17f, 0.17f, 0.17f, 30);
	}

	public void drawCylinder()	{
		parent.pushMatrix();
		parent.rotate(PApplet.HALF_PI, 0.0f, 1.0f, 0.0f);
		drawCone(-0.05f, 0.05f, 0.02f, 0.02f, 20);
		parent.popMatrix();
	}
	
	public void drawCone(float zMin, float zMax, float r1, float r2, int nbSub) {
		parent.translate(0.0f, 0.0f, zMin);
		Scene.cone(nbSub, 0, 0, r1, r2, zMax-zMin);
		parent.translate(0.0f, 0.0f, -zMin);
	}
	
	public void setColor(boolean selected) {
		if (selected)
			parent.fill(240, 240, 0);		
		else
			parent.fill(240, 240, 240);
	}

	public InteractiveFrame frame(int i) {
		return frameArray[i];
	}
}