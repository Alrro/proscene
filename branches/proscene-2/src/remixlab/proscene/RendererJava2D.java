package remixlab.proscene;

import java.util.List;

import processing.core.*;

import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

/**
import remixlab.remixcam.core.AbstractScene;
import remixlab.remixcam.core.Camera;
import remixlab.remixcam.core.ViewPort;
import remixlab.remixcam.core.SimpleFrame;
import remixlab.remixcam.geom.Vector3D;
import remixlab.remixcam.geom.Matrix3D;
*/

public class RendererJava2D extends Renderer {
	public RendererJava2D(AbstractScene scn, PGraphicsJava2D renderer) {
		super(scn, renderer);
	}
	
	public PGraphicsJava2D pgj2d() {
	  return (PGraphicsJava2D) pg();	
	}	
	
	@Override
	public void beginScreenDrawing() {
		float[] wh = scene.camera().getOrthoWidthHeight();
		Vector3D pos = scene.camera().position();
		Quaternion quat = scene.camera().frame().orientation();
		scale((scene.getWidth()/2)/wh[0], (scene.getHeight()/2)/wh[1]);
		translate(pos.x(), pos.y());
		if(scene.camera().frame().orientation().axis().z() > 0)
			rotate(quat.angle());
		//TODO: hack! to compensate when axis gets reverted
		else
			rotate(-quat.angle());
		translate(-scene.getWidth()/2, -scene.getHeight()/2);		
	}
	
	@Override
	public void endScreenDrawing() {
		float[] wh = scene.camera().getOrthoWidthHeight();
		Vector3D pos = scene.camera().position();
		Quaternion quat = scene.camera().frame().orientation();
		
		translate(scene.getWidth()/2, scene.getHeight()/2);
		if(scene.camera().frame().orientation().axis().z() > 0)
			rotate(-quat.angle());
		//TODO: hack! to compensate when axis gets reverted
		else
			rotate(quat.angle());
		translate(-pos.x(), -pos.y());
		scale(wh[0]/(scene.getWidth()/2), wh[1]/(scene.getHeight()/2));		
	}

	@Override
	public void pushProjection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void popProjection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetProjection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadProjection(Matrix3D source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyProjection(Matrix3D source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyProjectionRowMajorOrder(float n00, float n01, float n02,
			float n03, float n10, float n11, float n12, float n13, float n20,
			float n21, float n22, float n23, float n30, float n31, float n32,
			float n33) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix3D getProjection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix3D getProjection(Matrix3D target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cylinder(float w, float h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cone(int detail, float x, float y, float r, float h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cone(int detail, float x, float y, float r1, float r2, float h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawCamera(Camera camera, boolean drawFarPlane, float scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawKFIViewport(float scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawZoomWindowHint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawScreenRotateLineHint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawArcballReferencePointHint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawCross(float px, float py, float size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawFilledCircle(int subdivisions, Vector3D center, float radius) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawFilledSquare(Vector3D center, float edge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawShooterTarget(Vector3D center, float length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawPath(List<SimpleFrame> path, int mask, int nbFrames,
			int nbSteps, float scale) {
		// TODO Auto-generated method stub
		
	}	
}
