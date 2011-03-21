package test;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class OctreeNode {
	OctreeNode hijos[];
	ArrayList<Float> indexes;
	float xMax,xMin,yMax,yMin,zMax,zMin;
	float xMedio,yMedio,zMedio;
	float xTam,yTam,zTam;
	BoundingBox bbs;
	BoundingSphere bss;
	
	public OctreeNode() {
		hijos=null;
	}
	
	public OctreeNode(Vector3f[] verts) {
		//Definimos los puntos maximos y minimos de entre los vertices del nodo padre
		xMax=verts[0].x;
		xMin=verts[0].x;
		yMax=verts[0].y;
		yMin=verts[0].y;
		zMax=verts[0].z;
		zMin=verts[0].z;
		for(int i=1;i<verts.length;i++){
			if(xMax>verts[i].x)
				xMax=verts[i].x;
			if(xMin<verts[i].x)
				xMin=verts[i].x;
			if(yMax>verts[i].y)
				yMax=verts[i].y;
			if(yMin<verts[i].y)
				yMin=verts[i].y;
			if(zMax>verts[i].z)
				zMax=verts[i].z;
			if(zMin<verts[i].z)
				zMin=verts[i].z;
		}

		//Creamos puntos medios para los hijos
		
		xTam=(xMax-xMin)/2;
		yTam=(yMax-yMin)/2;
		zTam=(zMax-zMin)/2;
		
		xMedio=xTam+xMin;
		yMedio=yTam+yMin;
		zMedio=zTam+zMin;
		
		//Decidimos si es necesario crear los hijos
		if(verts.length>2000){
			ArrayList<Vector3f>  verts1=new ArrayList<Vector3f>()
							,verts2=new ArrayList<Vector3f>()
							,verts3=new ArrayList<Vector3f>()
							,verts4=new ArrayList<Vector3f>()
							,verts5=new ArrayList<Vector3f>()
							,verts6=new ArrayList<Vector3f>()
							,verts7=new ArrayList<Vector3f>()
							,verts8=new ArrayList<Vector3f>();
			
			//Separamos vertices para crear los 8 nodos hijos
			for(int i=0;i<verts.length;i++){
				if(verts[i].x>xMedio && verts[i].y>yMedio && verts[i].z>zMedio){ 
					verts1.add(verts[i]);
				}
				if(verts[i].x>xMedio && verts[i].y<yMedio && verts[i].z>zMedio){ 
					verts2.add(verts[i]);
				}
				if(verts[i].x>xMedio && verts[i].y>yMedio && verts[i].z<zMedio){ 
					verts3.add(verts[i]);
				}
				if(verts[i].x>xMedio && verts[i].y<yMedio && verts[i].z<zMedio){ 
					verts4.add(verts[i]);
				}
				if(verts[i].x<xMedio && verts[i].y>yMedio && verts[i].z>zMedio){ 
					verts5.add(verts[i]);
				}
				if(verts[i].x<xMedio && verts[i].y<yMedio && verts[i].z>zMedio){
					verts6.add(verts[i]);
				}
				if(verts[i].x<xMedio && verts[i].y>yMedio && verts[i].z<zMedio){
					verts7.add(verts[i]);
				}
				if(verts[i].x<xMedio && verts[i].y<yMedio && verts[i].z<zMedio){
					verts8.add(verts[i]);
				}
			}
			//Creamos los nodos hijos
			hijos=new OctreeNode[8];
			Vector3f[] 
			vertsTmp=new Vector3f[verts1.size()];
			hijos[0]=new OctreeNode(verts1.toArray(vertsTmp));
			vertsTmp=new Vector3f[verts2.size()];
			hijos[1]=new OctreeNode(verts2.toArray(vertsTmp));
			vertsTmp=new Vector3f[verts3.size()];
			hijos[2]=new OctreeNode(verts3.toArray(vertsTmp));
			vertsTmp=new Vector3f[verts4.size()];
			hijos[3]=new OctreeNode(verts4.toArray(vertsTmp));
			vertsTmp=new Vector3f[verts5.size()];
			hijos[4]=new OctreeNode(verts5.toArray(vertsTmp));
			vertsTmp=new Vector3f[verts6.size()];
			hijos[5]=new OctreeNode(verts6.toArray(vertsTmp));
			vertsTmp=new Vector3f[verts7.size()];
			hijos[6]=new OctreeNode(verts7.toArray(vertsTmp));
			vertsTmp=new Vector3f[verts8.size()];
			hijos[7]=new OctreeNode(verts8.toArray(vertsTmp));
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
	
	
	

}
