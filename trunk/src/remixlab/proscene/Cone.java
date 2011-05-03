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
	
	/**
	virtual void setConeOfNormals( Mesh & mesh ) {      
    typename Mesh::FaceIter                  f_it;
    VectorT< FT, 3 > aux;
    if ( !mesh.has_face_normals() )
    mesh.request_face_normals();       
    mesh.update_face_normals();
    //normalize face normals--needed?R they are already normalize!  
    //for (f_it=mesh.faces_begin(); f_it!=mesh.faces_end(); ++f_it) {
    //  aux = mesh.normal(f_it.handle());
    //  aux.normalize();
    //  //mesh.set_normal (f_it, aux);//the same as:
    //  mesh.set_normal (f_it.handle(), aux);
    //}
    axis.vectorize(0);
    for (f_it=mesh.faces_begin(); f_it!=mesh.faces_end(); ++f_it) {
      axis = axis + mesh.normal(f_it.handle());
    }
    if ( axis.norm() != 0 ) {
      axis.normalize();
    }
    else {
      axis[0] = 0;
      axis[0] = 0;
      axis[1] = 1;
    }
    angle = 0;        
    for (f_it=mesh.faces_begin(); f_it!=mesh.faces_end(); ++f_it)
      angle = std::max( angle, (FT) acos( dot(mesh.normal(f_it.handle()), axis) ) );
  }
  // */
	
	public void set(ArrayList<PVector> normals) {
		set( (PVector[]) normals.toArray() );
	}
	
	public void set(PVector [] normals) {
		axis = new PVector(0,0,0);
		if(normals.length == 0) {
			reset();
			return;
		}
		PVector [] n = new PVector [normals.length];
		for(int i=0; i<normals.length; i++ ) {
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
