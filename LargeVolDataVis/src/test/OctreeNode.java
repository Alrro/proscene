package test;
/*
 * Actualmente implementado solo para TRIANGLES
 * */
import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import processing.core.PApplet;
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
					inds1.add(verts.get(i+3).intValue());
				}
				if(verts.get(i)>xMedio && verts.get(i+1)<yMedio && verts.get(i+2)>zMedio){ 
					inds2.add(verts.get(i+3).intValue());
				}
				if(verts.get(i)>xMedio && verts.get(i+1)>yMedio && verts.get(i+2)<zMedio){ 
					inds3.add(verts.get(i+3).intValue());
				}
				if(verts.get(i)>xMedio && verts.get(i+1)<yMedio && verts.get(i+2)<zMedio){ 
					inds4.add(verts.get(i+3).intValue());
				}
				if(verts.get(i)<xMedio && verts.get(i+1)>yMedio && verts.get(i+2)>zMedio){ 
					inds5.add(verts.get(i+3).intValue());
				}
				if(verts.get(i)<xMedio && verts.get(i+1)<yMedio && verts.get(i+2)>zMedio){
					inds6.add(verts.get(i+3).intValue());
				}
				if(verts.get(i)<xMedio && verts.get(i+1)>yMedio && verts.get(i+2)<zMedio){
					inds7.add(verts.get(i+3).intValue());
				}
				if(verts.get(i)<xMedio && verts.get(i+1)<yMedio && verts.get(i+2)<zMedio){
					inds8.add(verts.get(i+3).intValue());
				}
			}
			
			/*
			 * Aqui se agregan los vertices complementarios que conforman las caras que se pintaran
			 */
			
			verts1=completarVertices(verts1,inds1,indexes);
			verts2=completarVertices(verts2,inds2,indexes);
			verts3=completarVertices(verts3,inds3,indexes);
			verts4=completarVertices(verts4,inds4,indexes);
			verts5=completarVertices(verts5,inds5,indexes);
			verts6=completarVertices(verts6,inds6,indexes);
			verts7=completarVertices(verts7,inds7,indexes);
			verts8=completarVertices(verts8,inds8,indexes);
			
			/*
			 * Se crean los GLModel de los hijos
			 */
			
			GLModel mod1,mod2,mod3,mod4,mod5,mod6,mod7,mod8;
			
			mod1=createGLModel(verts1,inds1,verts);
			mod2=createGLModel(verts2,inds2,verts);
			mod3=createGLModel(verts3,inds3,verts);
			mod4=createGLModel(verts4,inds4,verts);
			mod5=createGLModel(verts5,inds5,verts);
			mod6=createGLModel(verts6,inds6,verts);
			mod7=createGLModel(verts7,inds7,verts);
			mod8=createGLModel(verts8,inds8,verts);
			
			hijos= new OctreeNode[8];
			hijos[0]=new OctreeNode(mod1,parent);
			hijos[1]=new OctreeNode(mod2,parent);
			hijos[2]=new OctreeNode(mod3,parent);
			hijos[3]=new OctreeNode(mod4,parent);
			hijos[4]=new OctreeNode(mod5,parent);
			hijos[5]=new OctreeNode(mod6,parent);
			hijos[6]=new OctreeNode(mod7,parent);
			hijos[7]=new OctreeNode(mod8,parent);
			
			//System.gc();

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
				System.out.print("Entró ");
				verts.add(model.vertices.get());
				verts.add(model.vertices.get());
				verts.add(model.vertices.get());
				model.vertices.get();
				verts.add((float)((model.vertices.position()+1)/4 - 1));
			}
			catch (BufferUnderflowException e) {
				System.out.println("Buffer de vertices, terminado de leer");
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
				System.out.println("Buffer de indices, terminado de leer");
				leer=false;
			}
		}
		return indices;
	}

	
	private ArrayList<Integer> completarVertices(ArrayList<Integer> localVerts,ArrayList<Integer> localInds,ArrayList<Integer> globalInds){
		ArrayList<Integer> verts=new ArrayList<Integer>();
		for(int i=0;i<localVerts.size();i++){
			for(int j=0;j<globalInds.size();j+=3){
				if(globalInds.get(j)== localVerts.get(i) || globalInds.get(j+1)== localVerts.get(i) || globalInds.get(j+2)== localVerts.get(i)){
					localInds.add(globalInds.get(j));
					localInds.add(globalInds.get(j+1));
					localInds.add(globalInds.get(j+2));
					if(!verts.contains(globalInds.get(j)))verts.add(globalInds.get(j));
					if(!verts.contains(globalInds.get(j+1)))verts.add(globalInds.get(j+1));
					if(!verts.contains(globalInds.get(j+2)))verts.add(globalInds.get(j+2));
				}
			}
		}
		return verts;
	}
	
	
	private GLModel createGLModel(ArrayList<Integer> localVerts,ArrayList<Integer> localInds,ArrayList<Float> globalVerts){
		PApplet.println("Creating model with " + localVerts.size() + " vertices.");	  
		GLModel model = new GLModel(parent, localVerts.size(), PApplet.TRIANGLES, GLModel.STATIC);
		if (localVerts.size() == 0) return model;
		
		model.beginUpdateVertices();
		for(int i=0;i<localVerts.size();i++){
			model.updateVertex(i, globalVerts.get(i*4), globalVerts.get((i*4)+1), globalVerts.get((i*4)+2));
		}
		model.endUpdateVertices();
		
		int indices[]=new int[localInds.size()];
		for(int i=0;i<indices.length;i++){
			indices[i]=localVerts.lastIndexOf(localInds.get(i));
		}
		
		model.beginUpdateIndices();
		model.initIndices(indices.length);
		model.updateIndices(indices);
		model.endUpdateIndices();
		return model;
	}

}
