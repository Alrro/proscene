package remixlab.proscene;

import java.util.ArrayList;

import processing.core.*;

public class Cone {
	PVector axis;
	float angle;
	
	public Cone() {
		reset();
	}
	
	public Cone(PVector vec, float a) {
		set(vec, a);
	}
	
	public Cone(ArrayList<PVector> normals) {
		set(normals);
	}
	
	public Cone(PVector [] normals) {
		set(normals);
	}
	
	public PVector axis() {
		return axis;
	}
	
	public float angle() {
		return angle;
	}
	
	public void reset() {
		axis = new PVector(0,0,1);
		angle = 0;
	}
	
	public void set(PVector vec, float a) {
		axis = vec;
		angle = a;
	}
	
	public void set(ArrayList<PVector> normals) {
		set( normals.toArray( new PVector [normals.size()] ) );		
	}
	
	public void set(PVector [] normals) {
		axis = new PVector(0,0,0);
		if(normals.length == 0) {
			reset();
			return;
		}
		
		PVector [] n = new PVector [normals.length];
		for(int i=0; i<normals.length; i++ ) {
			n[i] = new PVector();
			n[i].set(normals[i]);
			n[i].normalize();
			axis = PVector.add(axis, n[i]);
		}
		
		if ( axis.mag() != 0 ) {
      axis.normalize();
    }
    else {
      axis.set(0,0,1);
    }
		
		angle = 0;        
		for(int i=0; i<normals.length; i++ )		
			angle = PApplet.max( angle, PApplet.acos( PVector.dot(n[i], axis)));		
	}	
}
