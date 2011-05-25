package test_hemesh;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.proscene.Camera.Cone;

import wblut.math.*;
import wblut.hemesh.modifiers.*;
import wblut.hemesh.kdtree.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.tools.*;
import wblut.hemesh.*;
import wblut.hemesh.subdividors.*;
import wblut.geom.*;

@SuppressWarnings("serial")
public class BFCulling extends PApplet {
	public class Face {
		public Face(Scene scn, HE_Face f) {
			scene = scn;
			face = f;
			normal = face.normal();
			normal.normalize();
			p5normal = new PVector((float)face.normal().x, (float)face.normal().y, (float)face.normal().z);
			cone = scene.camera().new Cone(p5normal, 0);
		}
		Scene scene;
		PVector p5normal;
		WB_Normal normal;
		Camera.Cone cone;
		HE_Face face;
	}
	
	boolean bfc = true;
	HE_Mesh cage;
	Scene scene;
	int renderedFaces = 0;
	ArrayList<Face> faces;
	//ArrayList<WB_Normal> normals;
	//ArrayList<Cone> cones;

	public void setup() {
		size(700, 700, P3D);

		scene = new Scene(this);
		scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
		scene.setRadius(500);
		scene.showAll();
		// scene.enableFrustumEquationsUpdate();
		scene.setGridIsDrawn(false);

		cage = new HE_Mesh(new HEC_Box(this).setDepth(400).setHeight(400).setWidth(400));
		cage.modify(new HEM_ChamferCorners().setDistance(80));
		// HES_Planar() subdivision can include a measure of randomness
		cage.subdivide(new HES_Planar().setRandom(true).setRange(0.4), 2);

		// A save choice after introducing any kind of randomness is to
		// triangulate possible concave faces.
		// Concave faces do not invalidate the mesh but can give unexpected
		// results.
		cage.triangulateConcaveFaces();

		cage.modify(new HEM_Lattice().setDepth(10).setWidth(10).setFuse(true));
		HE_Selection sel = new HE_Selection();
		Iterator<HE_Face> fItr = cage.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (random(100) < 50)
				sel.add(f);
		}
		cage.subdivideSelected(new HES_CatmullClark(), sel, 2);
		
		faces = new ArrayList<Face>();
		//normals = new ArrayList<WB_Normal>();
		Iterator<HE_Face> faceItr = cage.fItr();
		HE_Face face;
		renderedFaces = 0;
		while (faceItr.hasNext()) {
			face = faceItr.next();
			faces.add(new Face(scene, face));			
		}
	}

	public void draw() {
		background(120);
		lights();
		fill(255);
		noStroke();
		cage.drawFaces();
		
		Iterator<Face> it = faces.iterator();
		Face nC;
		renderedFaces = 0;
		while( it.hasNext() ) {
			nC = it.next();
			if(bfc) {
				/**	
				// pending			
				if( scene.camera().faceIsBackFacing( nC.p5normal ) ) {
					cage.drawFace( nC.face.key() );
					renderedFaces++;
				}	
				*/	
			}
			else {
				cage.drawFace( nC.face.key() );
				renderedFaces++;
			}
		}		
			 
		stroke(0);
	}
	
	public void keyPressed() {
		if(key == 'n') {
			println("rendered models " + renderedFaces);
		}
		if(key == 'b') {
			bfc = !bfc;
			if(bfc)
				println("BFC enabled");
			else
				println("BFC disabled");
		}
	}

	/**
	 * //Accessible through face public void setup(){ size(100,100,P3D); HE_Mesh
	 * mesh = new HE_Mesh(new HEC_Cube(this).setEdge(100)); HE_Face
	 * face=mesh.fItr().next();
	 * 
	 * int fkey = face.key();// the unique key
	 * 
	 * WB_Point fc = face.center(); // face center, calculated on the fly
	 * 
	 * int order=face.order();//number of vertices, calculated on the fly
	 * 
	 * WB_Normal fn = face.normal(); // face normal, cached
	 * 
	 * double area = face.area(); // face area, cached
	 * 
	 * HE.VertexType type = face.type(); // face type, HE_VertexType.CONVEX or
	 * HE_VertexType.CONCAVE, cached
	 * 
	 * ArrayList<HE_Vertex> vertices = face.vertices(); // get all vertices
	 * belonging to face //alternative face.vertices(vertices);// get all
	 * vertices and put them in existing arraylist, replacing previous content.
	 * 
	 * ArrayList<HE_Halfedge> halfedges = face.halfedges();// get all halfedges
	 * belonging to face
	 * 
	 * ArrayList<HE_Edge> edges = face.edges();// get all edges belonging to
	 * face
	 * 
	 * ArrayList<HE_Face> faces = face.faces();// get all neighboring faces
	 * 
	 * HE_Halfedge he=face.halfedge();// Starting halfedge of face
	 * //face.setHalfedge(he); // Change starting halfedge
	 * //face.clearHalfedge(); // Set starting halfedge to null
	 * 
	 * WB_Plane P = face.toPlane();//Get plane of face with origin in face
	 * center WB_Polygon poly = face.toPolygon();//Get face as a polygon
	 * 
	 * face.sort();// set leftmost halfedge as starting halfedge
	 * 
	 * face.invalidate(); // refresh all cached info. }
	 */
}
