package remixlab.proscene;

import processing.core.*;
import remixlab.remixcam.core.Renderable;
import remixlab.remixcam.geom.Matrix3D;
import remixlab.remixcam.geom.Vector3D;

public class P5Renderer implements Renderable {
	PGraphics3D pg3d;

	public P5Renderer(PGraphics3D p) {
		pg3d = p;
	}	

	@Override
	public void pushMatrix() {
		pg3d.pushMatrix();
	}

	@Override
	public void popMatrix() {
		pg3d.popMatrix();
	}

	@Override
	public void translate(float tx, float ty) {
		pg3d.translate(tx, ty);
	}

	@Override
	public void translate(float tx, float ty, float tz) {
		pg3d.translate(tx, ty, tz);
	}

	@Override
	public void rotate(float angle) {
		pg3d.rotate(angle);
	}

	@Override
	public void rotateX(float angle) {
		pg3d.rotateX(angle);
	}

	@Override
	public void rotateY(float angle) {
		pg3d.rotateY(angle);
	}

	@Override
	public void rotateZ(float angle) {
		pg3d.rotateZ(angle);
	}

	@Override
	public void rotate(float angle, float vx, float vy, float vz) {
		pg3d.rotate(angle, vx, vy, vz);
	}

	@Override
	public void scale(float s) {
		pg3d.scale(s);
	}

	@Override
	public void scale(float sx, float sy) {
		pg3d.scale(sx, sy);
	}

	@Override
	public void scale(float x, float y, float z) {
		pg3d.scale(x, y, z);
	}	

	@Override
	public void loadIdentity() {
		pg3d.resetMatrix();
	}

	@Override
	public void resetMatrix() {
		pg3d.resetMatrix();
	}

	@Override
	public void loadMatrix(Matrix3D source) {
		PMatrix3D pM = new PMatrix3D();
		pM.set(source.getTransposed(new float[16]));
		pg3d.setMatrix(pM);

	}

	@Override
	public void multiplyMatrix(Matrix3D source) {
		this.applyMatrix(source);
	}

	@Override
	public void applyMatrix(Matrix3D source) {
		PMatrix3D pM = new PMatrix3D();
		pM.set(source.getTransposed(new float[16]));
		pg3d.applyMatrix(pM);
	}	

	@Override
	public void applyMatrixRowMajorOrder(float n00, float n01, float n02, float n03,
			                                 float n10, float n11, float n12, float n13,
			                                 float n20, float n21, float n22, float n23,
			                                 float n30, float n31, float n32, float n33) {
		pg3d.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22,	n23, n30, n31, n32, n33);
	}

	@Override
	public void frustum(float left, float right, float bottom, float top,	float znear, float zfar) {
		pg3d.frustum(left, right, bottom, top, znear, zfar);
	}

	@Override
	public Matrix3D getMatrix() {
		PMatrix3D pM = (PMatrix3D) pg3d.getMatrix();
		return new Matrix3D(pM.get(new float[16]), true);// set it transposed
	}

	@Override
	public Matrix3D getMatrix(Matrix3D target) {
		PMatrix3D pM = (PMatrix3D) pg3d.getMatrix();
		target.setTransposed(pM.get(new float[16]));
		return target;
	}

	@Override
	public void setMatrix(Matrix3D source) {
		resetMatrix();
		applyMatrix(source);
	}

	@Override
	public void printMatrix() {
		pg3d.printMatrix();
	}

	@Override
	public void matrixMode(int mode) {
		pg3d.matrixMode(mode);
	}

	@Override
	public int getWidth() {
		return pg3d.width;
	}

	@Override
	public int getHeight() {
		return pg3d.height;
	}

	@Override
	public void beginShape(int kind) {
		pg3d.beginShape(kind);
	}

	@Override
	public void endShape() {
		pg3d.endShape();
	}

	@Override
	public void vertex(Vector3D v) {
		pg3d.vertex(v.x(), v.y(), v.z());
	}

	@Override
	public void vertex(float x, float y, float z) {
		pg3d.vertex(x, y, z);
	}

	@Override
	public void stroke(int color) {
		pg3d.stroke(color);
	}

	@Override
	public void color(int r, int g, int b) {
		pg3d.stroke(r,g,b);
	}

	@Override
	public void strokeWeight(int weight) {
		pg3d.strokeWeight(weight);
	}

	@Override
	public void pushStyle() {
		pg3d.pushStyle();
	}

	@Override
	public void popStyle() {
		pg3d.popStyle();
	}	
}
