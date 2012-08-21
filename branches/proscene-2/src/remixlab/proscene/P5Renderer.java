/**
 *                     ProScene (version 1.9.90)      
 *    Copyright (c) 2010-2011 by National University of Colombia
 *                 @author Jean Pierre Charalambos      
 *           http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
 * 
 * This source file is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * A copy of the GNU General Public License is available on the World Wide Web
 * at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by
 * writing to the Free Software Foundation, 51 Franklin Street, Suite 500
 * Boston, MA 02110-1335, USA.
 */

package remixlab.proscene;

import processing.core.*;
import processing.opengl.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

public class P5Renderer implements Renderable {
	PGraphicsOpenGL pg3d;

	public P5Renderer(PGraphicsOpenGL renderer) {
		pg3d = renderer;
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
	public void pushProjection() {
		pg3d.pushProjection();
	}

	@Override
	public void popProjection() {
		pg3d.popProjection();
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
	public void resetMatrix() {
		pg3d.resetMatrix();
	}

	@Override
	public void resetProjection() {
		pg3d.projection.reset();
	}

	@Override
	public void loadMatrix(Matrix3D source) {
		PMatrix3D pM = new PMatrix3D();
		pM.set(source.getTransposed(new float[16]));
		pg3d.setMatrix(pM);
	}
	
	@Override
	public void loadProjection(Matrix3D source) {
		PMatrix3D pM = new PMatrix3D();
		pM.set(source.getTransposed(new float[16]));
		pg3d.setProjection(pM);
	}

	@Override
	public void multiplyMatrix(Matrix3D source) {
		this.applyMatrix(source);
	}
	
	@Override
	public void multiplyProjection(Matrix3D source) {
		this.applyProjection(source);
	}

	@Override
	public void applyMatrix(Matrix3D source) {
		PMatrix3D pM = new PMatrix3D();
		pM.set(source.getTransposed(new float[16]));
		pg3d.applyMatrix(pM);
	}
	
	@Override
	public void applyProjection(Matrix3D source) {
		PMatrix3D pM = new PMatrix3D();
		pM.set(source.getTransposed(new float[16]));
		pg3d.applyProjection(pM);
	}

	@Override
	public void applyMatrixRowMajorOrder(float n00, float n01, float n02, float n03,
			                                 float n10, float n11, float n12, float n13,
			                                 float n20, float n21, float n22, float n23,
			                                 float n30, float n31, float n32, float n33) {
		pg3d.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22,	n23, n30, n31, n32, n33);
	}
	
	@Override
	public void applyProjectionRowMajorOrder(float n00, float n01, float n02, float n03,
			                                 float n10, float n11, float n12, float n13,
			                                 float n20, float n21, float n22, float n23,
			                                 float n30, float n31, float n32, float n33) {		
		pg3d.applyProjection(new PMatrix3D(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22,	n23, n30, n31, n32, n33));
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
	public Matrix3D getProjection() {
		PMatrix3D pM = pg3d.projection.get();
		return new Matrix3D(pM.get(new float[16]), true);// set it transposed
	}

	@Override
	public Matrix3D getMatrix(Matrix3D target) {
		PMatrix3D pM = (PMatrix3D) pg3d.getMatrix();
		target.setTransposed(pM.get(new float[16]));
		return target;
	}
	
	@Override
	public Matrix3D getProjection(Matrix3D target) {
		PMatrix3D pM = pg3d.projection.get();
		target.setTransposed(pM.get(new float[16]));
		return target;
	}

	@Override
	public void setMatrix(Matrix3D source) {
		resetMatrix();
		applyMatrix(source);
	}
	
	@Override
	public void setProjection(Matrix3D source) {
		resetProjection();
		applyProjection(source);
	}

	@Override
	public void printMatrix() {
		pg3d.printMatrix();
	}
	
	@Override
	public void printProjection() {
		pg3d.printProjection();
	}
}
