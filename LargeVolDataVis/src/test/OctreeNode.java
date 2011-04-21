package test;
/*
 * Actualmente implementado solo para TRIANGLES
 * */
import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.core.PConstants;
import codeanticode.glgraphics.GLModel;

public class OctreeNode {
	OctreeNode hijos[];
	GLModel model;
	PApplet parent;
	float xMax,xMin,yMax,yMin,zMax,zMin;
	float xMedio,yMedio,zMedio;
	BoundingBox bbs;
	BoundingSphere bss;

	public OctreeNode() {
		hijos=null;
	}

	public OctreeNode(GLModel model , PApplet parent){
		this.parent=parent;
		this.model=model;
		childsGeneration();
	}

	private void childsGeneration(){
		ArrayList<Float> verts = GLModelToVertexArray();
		ArrayList<Integer> indexes = GLModelToIndexArray();
		System.out.print("");
		if (verts.size() == 0) return;

		xMax=xMin=verts.get(0);
		yMax=yMin=verts.get(1);
		zMax=zMin=verts.get(2);

		for(int i=0;i<verts.size();i+=4){
			xMax=Math.max(xMax, verts.get(i));
			xMin=Math.min(xMin, verts.get(i));
			yMax=Math.max(yMax, verts.get(i+1));
			yMin=Math.min(yMin, verts.get(i+1));
			zMax=Math.max(zMax, verts.get(i+2));
			zMin=Math.min(zMin, verts.get(i+2));
		}

		if(model.vertices.capacity()/4>2000){

			ArrayList<Integer>  verts1=new ArrayList<Integer>()
			,verts2=new ArrayList<Integer>()
			,verts3=new ArrayList<Integer>()
			,verts4=new ArrayList<Integer>()
			,verts5=new ArrayList<Integer>()
			,verts6=new ArrayList<Integer>()
			,verts7=new ArrayList<Integer>()
			,verts8=new ArrayList<Integer>();

			ArrayList<Integer>  inds1=new ArrayList<Integer>()
			,inds2=new ArrayList<Integer>()
			,inds3=new ArrayList<Integer>()
			,inds4=new ArrayList<Integer>()
			,inds5=new ArrayList<Integer>()
			,inds6=new ArrayList<Integer>()
			,inds7=new ArrayList<Integer>()
			,inds8=new ArrayList<Integer>();

			xMedio=((xMax-xMin)/2)+xMin;
			yMedio=((yMax-yMin)/2)+yMin;
			zMedio=((zMax-zMin)/2)+zMin;

			for(int i=0;i<verts.size();i+=4){
				if(verts.get(i)>xMedio && verts.get(i+1)>yMedio && verts.get(i+2)>zMedio){ 
					verts1.add(i/4);
				}
				if(verts.get(i)>xMedio && verts.get(i+1)<yMedio && verts.get(i+2)>zMedio){ 
					verts2.add(i/4);
				}
				if(verts.get(i)>xMedio && verts.get(i+1)>yMedio && verts.get(i+2)<zMedio){ 
					verts3.add(i/4);
				}
				if(verts.get(i)>xMedio && verts.get(i+1)<yMedio && verts.get(i+2)<zMedio){ 
					verts4.add(i/4);
				}
				if(verts.get(i)<xMedio && verts.get(i+1)>yMedio && verts.get(i+2)>zMedio){ 
					verts5.add(i/4);
				}
				if(verts.get(i)<xMedio && verts.get(i+1)<yMedio && verts.get(i+2)>zMedio){
					verts6.add(i/4);
				}
				if(verts.get(i)<xMedio && verts.get(i+1)>yMedio && verts.get(i+2)<zMedio){
					verts7.add(i/4);
				}
				if(verts.get(i)<xMedio && verts.get(i+1)<yMedio && verts.get(i+2)<zMedio){
					verts8.add(i/4);
				}
			}

			initIndices(inds1, verts1, indexes, verts);
			initIndices(inds2, verts2, indexes, verts);
			initIndices(inds3, verts3, indexes, verts);
			initIndices(inds4, verts4, indexes, verts);
			initIndices(inds5, verts5, indexes, verts);
			initIndices(inds6, verts6, indexes, verts);
			initIndices(inds7, verts7, indexes, verts);
			initIndices(inds8, verts8, indexes, verts);

			/*
			System.out.println("Vertices 1: "+ verts1);
			System.out.println("Vertices 2: "+ verts2);
			System.out.println("Vertices 3: "+ verts3);
			System.out.println("Vertices 4: "+ verts4);
			System.out.println("Vertices 5: "+ verts5);
			System.out.println("Vertices 6: "+ verts6);
			System.out.println("Vertices 7: "+ verts7);
			System.out.println("Vertices 8: "+ verts8);

			System.out.println("Indices 1: "+ inds1);
			System.out.println("Indices 2: "+ inds2);
			System.out.println("Indices 3: "+ inds3);
			System.out.println("Indices 4: "+ inds4);
			System.out.println("Indices 5: "+ inds5);
			System.out.println("Indices 6: "+ inds6);
			System.out.println("Indices 7: "+ inds7);
			System.out.println("Indices 8: "+ inds8);*/

			/*
			 * Se crean los GLModel de los hijos
			 */

			GLModel mod1,mod2,mod3,mod4,mod5,mod6,mod7,mod8;

			mod1=createGLModel(verts1,inds1,verts,indexes);
			mod2=createGLModel(verts2,inds2,verts,indexes);
			mod3=createGLModel(verts3,inds3,verts,indexes);
			mod4=createGLModel(verts4,inds4,verts,indexes);
			mod5=createGLModel(verts5,inds5,verts,indexes);
			mod6=createGLModel(verts6,inds6,verts,indexes);
			mod7=createGLModel(verts7,inds7,verts,indexes);
			mod8=createGLModel(verts8,inds8,verts,indexes);

			hijos= new OctreeNode[8];
			hijos[0]=new OctreeNode(mod1,parent);
			hijos[1]=new OctreeNode(mod2,parent);
			hijos[2]=new OctreeNode(mod3,parent);
			hijos[3]=new OctreeNode(mod4,parent);
			hijos[4]=new OctreeNode(mod5,parent);
			hijos[5]=new OctreeNode(mod6,parent);
			hijos[6]=new OctreeNode(mod7,parent);
			hijos[7]=new OctreeNode(mod8,parent);
		}
		bbs=new BoundingBox();
		bss=new BoundingSphere();

		FloatBuffer buf=FloatBuffer.allocate(6);
		buf.put(0,xMax);
		buf.put(1,yMax);
		buf.put(2,zMax);
		buf.put(3,xMin);
		buf.put(4,yMin);
		buf.put(5,zMin);


		bbs.computeFromPoints(buf);
		bss.computeFromPoints(buf);
	}

	private ArrayList<Float> GLModelToVertexArray(){
		boolean leer=true;
		ArrayList<Float> verts=new ArrayList<Float>();
		if (model.getSize() == 0) return verts;
		while(leer){
			try{
				//System.out.print("Entr� ");
				verts.add(model.vertices.get());
				verts.add(model.vertices.get());
				verts.add(model.vertices.get());
				model.vertices.get();
				verts.add((float)((model.vertices.position()+1)/4 - 1));
			}
			catch (BufferUnderflowException e) {
				leer=false;
			}
		}
		return verts;
	}

	private ArrayList<Integer> GLModelToIndexArray( ){		
		boolean leer=true;
		ArrayList<Integer> indices=new ArrayList<Integer>();
		if (model.getSize() == 0) return indices;
		while(leer){
			try{
				indices.add(model.indices.get());
			}catch (Exception e) {
				leer=false;
			}
		}
		return indices;
	}

	private void initIndices(ArrayList<Integer> localInds, ArrayList<Integer> localVerts, ArrayList<Integer> globalInds,ArrayList<Float> globalVerts){
		switch(model.getMode()){
		case GL.GL_POINT:
			break;
		case GL.GL_LINE:
			break;
		case GL.GL_LINE_STRIP:
			break;
		case GL.GL_LINE_LOOP:
			break;
		case GL.GL_TRIANGLES:
			boolean tmp1,tmp2,tmp3;
			
			for(int i=0;i<globalInds.size();i+=3){

				tmp1=localVerts.contains(globalInds.get(i));
				tmp2=localVerts.contains(globalInds.get(i+1));
				tmp3=localVerts.contains(globalInds.get(i+2));
				
				boolean addInds= tmp1 || tmp2 || tmp3;

				if(addInds){
					if(!tmp1)localVerts.add(globalInds.get(i));
					if(!tmp2)localVerts.add(globalInds.get(i+1));
					if(!tmp3)localVerts.add(globalInds.get(i+2));

					localInds.add(globalInds.get(i));
					localInds.add(globalInds.get(i+1));
					localInds.add(globalInds.get(i+2));
				}
			}
			System.out.println("Numero de Indices del modelo: "+localInds.size());
			break;
		case GL.GL_TRIANGLE_STRIP:
			break;
		case GL.GL_QUADS:
			break;
		case GL.GL_QUAD_STRIP:
			break;
		case GL.GL_POLYGON:
			break;
		default:
			System.out.println("Modo del modelo: "+model.getMode());
			System.out.println("Modo POINTS: "+GL.GL_POINTS);
			System.out.println("Modo LINES: "+GL.GL_LINES);
			System.out.println("Modo LINE_STRIP: "+GL.GL_LINE_STRIP);
			System.out.println("Modo LINE_LOOP: "+GL.GL_LINE_LOOP);
			System.out.println("Modo TRIANGLES: "+GL.GL_TRIANGLES);
			System.out.println("Modo TRIANGLE_FAN: "+GL.GL_TRIANGLE_FAN);
			System.out.println("Modo TRIANGLE_STRIP: "+GL.GL_TRIANGLE_STRIP);
			System.out.println("Modo QUAD_STRIP: "+GL.GL_QUAD_STRIP);
			System.out.println("Modo QUADS: "+GL.GL_QUADS);
			System.out.println("Modo POLYGON: "+GL.GL_POLYGON);
			throw new RuntimeException("Metodo de indexacion no soportado");
		}
	}

	private GLModel createGLModel(ArrayList<Integer> localVerts , ArrayList<Integer> localInds , 
			ArrayList<Float> globalVerts , ArrayList<Integer> globalInds){

		if(localVerts.size()<=0)throw new RuntimeException("Intentando crear GLModel vacio");

		PApplet.println("Creating model with " + localVerts.size() + " vertices.");

		switch(model.getMode()){
			
			case GL.GL_POINT:
				break;
				
			case GL.GL_LINE:
				break;
				
			case GL.GL_LINE_STRIP:
				break;
				
			case GL.GL_LINE_LOOP:
				break;
				
			case GL.GL_TRIANGLES:
				
				GLModel modelChild= new GLModel(parent, localVerts.size(), PApplet.TRIANGLES , GLModel.STATIC);
				
				modelChild.beginUpdateVertices();
				for(int i=0;i<localVerts.size();i++){
					modelChild.updateVertex(i,globalVerts.get(localVerts.get(i)*4+0),
							globalVerts.get(localVerts.get(i)*4+1),globalVerts.get(localVerts.get(i)*4+2));
					System.out.println(i+"  "+localVerts.get(i)+"  "+globalVerts.get(localVerts.get(i)*4+0)+"  "+
							globalVerts.get(localVerts.get(i)*4+1)+"  "+globalVerts.get(localVerts.get(i)*4+2));
				}
				modelChild.endUpdateVertices();
	
				int indices[]=new int[localInds.size()];
				for(int i=0;i<indices.length;i++){
					indices[i]=localVerts.indexOf(localInds.get(i));
				}

	
				//modelChild.beginUpdateIndices();
				modelChild.initIndices(indices.length);
				modelChild.autoIndexBounds(false);
				modelChild.updateIndices(indices);
				//modelChild.endUpdateIndices();
				
				System.out.println(modelChild.vertices.capacity()+" "+modelChild.indices.capacity());

				return modelChild;
				
			case GL.GL_TRIANGLE_STRIP:
				break;
				
			case GL.GL_QUADS:
				break;
				
			case GL.GL_QUAD_STRIP:
				break;
				
			case GL.GL_POLYGON:
				break;
				
			default:
				System.out.println("Modo del modelo: "+model.getMode());
				System.out.println("Modo POINTS: "+GL.GL_POINTS);
				System.out.println("Modo LINES: "+GL.GL_LINES);
				System.out.println("Modo LINE_STRIP: "+GL.GL_LINE_STRIP);
				System.out.println("Modo LINE_LOOP: "+GL.GL_LINE_LOOP);
				System.out.println("Modo TRIANGLES: "+GL.GL_TRIANGLES);
				System.out.println("Modo TRIANGLE_FAN: "+GL.GL_TRIANGLE_FAN);
				System.out.println("Modo TRIANGLE_STRIP: "+GL.GL_TRIANGLE_STRIP);
				System.out.println("Modo QUAD_STRIP: "+GL.GL_QUAD_STRIP);
				System.out.println("Modo QUADS: "+GL.GL_QUADS);
				System.out.println("Modo POLYGON: "+GL.GL_POLYGON);
				throw new RuntimeException("Metodo de indexacion no soportado");
		}

		return null;
	}
	
}
