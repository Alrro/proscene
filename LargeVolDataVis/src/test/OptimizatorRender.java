package test;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import remixlab.proscene.Scene;
import codeanticode.glgraphics.*;


public class OptimizatorRender {
	/*
	 * Declaraci�n Miembros de Clase
	 * */
	OctreeNode oc;
	ArrayList<Integer> indexes=new ArrayList<Integer>();
	
	/**
	 * This method creates an Octree Node for the given 
	 * GLModel, the root of the node is a class member 
	 * called oc
	 * 
	 * @param model
	 * The GLModel that you create the Octree Node
	 * */
	public OptimizatorRender(GLModel model,PApplet parent){
		oc=new OctreeNode(model,parent);
	}
	
	/**
	 * This method allows the View Frustrum Culling for a GLModel
	 * 
	 * @param model
	 * The GLModel we will apply the VFC
	 */
	public void vfc(GLGraphics renderer , GLModel model , Scene scene){
		renderer.beginGL();
		vfcVerify( oc , scene , renderer , 1);
		renderer.endGL();
		}
	
	
	private void vfcVerify(OctreeNode octree, Scene scene ,GLGraphics renderer,int a){
		switch (scene.camera().sphereIsVisible(new PVector(octree.bss.center.x, octree.bss.center.y, octree.bss.center.z), octree.bss.radius)) {
		case VISIBLE:
			renderer.model(octree.model);
			break;
		case SEMIVISIBLE:
			PVector BBCorner1 = new PVector( (octree.bbs.center.x - octree.bbs.xExtent), (octree.bbs.center.y - octree.bbs.yExtent), (octree.bbs.center.z - octree.bbs.zExtent) );
			PVector BBCorner2 = new PVector( (octree.bbs.center.x + octree.bbs.xExtent), (octree.bbs.center.y + octree.bbs.yExtent), (octree.bbs.center.z + octree.bbs.zExtent) );          
			switch (scene.camera().aaBoxIsVisible(BBCorner1, BBCorner2)) {
			case VISIBLE:
				renderer.model(octree.model);
				break;
			case SEMIVISIBLE:
				if(octree.hijos==null){
					renderer.model(octree.model);
				}else{
					for(int i=0;i<octree.hijos.length;i++){
						a++;
						vfcVerify(octree.hijos[i], scene , renderer , a);
					}
				}
				break;
			case INVISIBLE:
				//System.out.println("no se imprime por cubo");
				break;
			}
			break;
		case INVISIBLE:
			//System.out.println("no se imprime por esfera");
			break;
	}
	}

}
