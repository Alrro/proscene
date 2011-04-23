package test;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import processing.core.*;
import codeanticode.glgraphics.*;
import remixlab.proscene.*;
import test.BoundingBox;
import test.BoundingSphere;
import test.Chronometer;
import test.OctreeNode;
import test.OptimizatorRender;

public class DataVis extends PApplet {
	private static final long serialVersionUID = 1L;

  // La maxima profundidad del octree.
  int maxOctreeLevel = 2;	
  
  // Si esta variable es true, entonces el modelo se dibuja con una sola
  // llamada a renderer.model(), reuniendo todos los indices de los nodos
  // visibles del octree en un unico array.
  // En el caso de que sea false, cada nodo visible se dibuja por separado.
  boolean oneDrawCall = false;
  	
	int cubeCount = 5000;
	int vertPerCube = 8;
	float cubeSize = 10;
	float volSize = 1000;

	boolean drawBBS = false;
	boolean drawBSS = false;	
	boolean enableVFC = true;
	
	GLModel cubes;
	Scene scene;
	
	OptimizatorRender optim;

	// Los indices de los 8 vertices en un cubo, de manera tal que definen	
	// 12 triangulos (2 por cara).
	int cubeIndices[] = { 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7, 3,
			3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2 };
	int indicesPerCube = cubeIndices.length;
	int indices0[] = new int[indicesPerCube * cubeCount];
	int indices[] = new int[indicesPerCube * cubeCount];
	int minInd, maxInd;
	int first, last;
	int renderedCubes;	

	Chronometer chrono;

	BoundingBox[] bbs;
	BoundingSphere[] bss;

	public void setup() {
		size(600, 600, GLConstants.GLGRAPHICS);
		frameRate(120);

		chrono = new Chronometer(this);

		scene = new Scene(this);
		scene.enableFrustumEquationsUpdate();
		scene.setRadius(volSize);
		scene.showAll();
		scene.setAxisIsDrawn(false);
		scene.setGridIsDrawn(false);
		scene.disableKeyboardHandling();

		sphereDetail(10);

		println("Creando cubos...");
		createCubes();
		println("Creando el Optimizador...");
		optim = new OptimizatorRender(cubes, indices0, this);
		println("Listo.");
	}
	
	public void draw() {		
		chrono.update();

		GLGraphics renderer = (GLGraphics) g;
    
		GL gl = renderer.gl;
    // Deshabilitando depth mask para que el dibujado de
    // objetos semi-transparentes no tenga artefactos.
    //gl.glDepthMask(false);
		
		if (enableVFC) {
			optim.vfc(renderer, cubes, scene);
		} else {
		  // Dibujando todo el sistema, sin optimizacion de VFC.
			renderer.beginGL();
	    cubes.updateIndices(indices0, indices0.length);
	    cubes.setMinIndex(minInd);
	    cubes.setMaxIndex(maxInd);     
	    renderer.model(cubes);  
			renderer.endGL();
		}

    //gl.glDepthMask(true);		
		
		if (enableVFC && (drawBBS || drawBSS)) {
		  pintarOc(optim.oc);
		}
		
		chrono.printFps();
	}
	
	void pintarOc(OctreeNode oct){
	  // Los bounding volumes no se dibujan a trabjas de GLGraphics, sino que con la API regular
    // de Processing, con lo cual no es recomendable dibujarlos cuando se tiene un GLModel muy
    // grande.	  
		if(oct.hijos!=null){
			for(int i=0;i<8;i++){
				pintarOc(oct.hijos[i]);
			}
		}

		if (drawBBS) {
		  noFill();
	  	stroke(255, 0, 0);
		  pushMatrix();
		  translate(oct.bbs.center.x, oct.bbs.center.y, oct.bbs.center.z);
		  box(2.01f * oct.bbs.xExtent, 2.01f * oct.bbs.yExtent,
			  	2.01f * oct.bbs.zExtent);
		  popMatrix();
		}

    if (drawBSS) {		
      noFill();
		  stroke(0, 255, 0);
		  pushMatrix();
		  translate(oct.bss.center.x, oct.bss.center.y, oct.bss.center.z);
		  sphere(oct.bss.radius);
		  popMatrix();
    }
	}

	void enableVFC() {
		scene.enableFrustumEquationsUpdate();
		enableVFC = true;
		println("Activando VFC");
	}

	void disableVFC() {
		scene.disableFrustumEquationsUpdate();
		enableVFC = false;
		println("Desactivando VFC");
	}
	
	public void keyPressed() {
		if (key == 'c' || key == 'C') {
			scene.toggleCameraType();
			return;
		}

		if (key == 'v' || key == 'V') {
			if (enableVFC)
				disableVFC();
			else
				enableVFC();
			return;
		}
		
    if (key == 'b' || key == 'B') {
      if (drawBBS) {
        drawBBS = false;
        println("Deshabilitando dibujado de los bounding boxes del octree");
      } else {
        drawBBS = true;
        println("Habilitando dibujado de los bounding boxes del octree");
      }
      return;
    }
    
    if (key == 's' || key == 'S') {
      if (drawBSS) {
        drawBSS = false;
        println("Deshabilitando dibujado de los bounding spheres del octree");
      } else {
        drawBSS = true;
        println("Habilitando dibujado de los bounding spheres del octree");
      }
      return;
    }    
    
    if (key == '1') {
      if (oneDrawCall) {
        oneDrawCall = false;
        println("Deshabilitando dibujado en una sola llamada");
      } else {
        oneDrawCall = true;
        println("Habilitando dibujado en una sola llamada");
      }
      return;
    }    
	}

	void createCubes() {
		cubes = new GLModel(this, vertPerCube * cubeCount, TRIANGLES, GLModel.STATIC);

		bbs = new BoundingBox[cubeCount];
		bss = new BoundingSphere[cubeCount];

		cubes.beginUpdateVertices();
		for (int i = 0; i < cubeCount; i++) {
			int n0 = vertPerCube * i;
			float x0 = random(-volSize, +volSize);
			float y0 = random(-volSize, +volSize);    
			float z0 = random(-volSize, +volSize);

			cubes.updateVertex(n0 + 0, x0 - cubeSize, y0 - cubeSize, z0 - cubeSize);
			cubes.updateVertex(n0 + 1, x0 + cubeSize, y0 - cubeSize, z0 - cubeSize);
			cubes.updateVertex(n0 + 2, x0 + cubeSize, y0 + cubeSize, z0 - cubeSize);
			cubes.updateVertex(n0 + 3, x0 - cubeSize, y0 + cubeSize, z0 - cubeSize);
			cubes.updateVertex(n0 + 4, x0 - cubeSize, y0 - cubeSize, z0 + cubeSize);
			cubes.updateVertex(n0 + 5, x0 + cubeSize, y0 - cubeSize, z0 + cubeSize);
			cubes.updateVertex(n0 + 6, x0 + cubeSize, y0 + cubeSize, z0 + cubeSize);
			cubes.updateVertex(n0 + 7, x0 - cubeSize, y0 + cubeSize, z0 + cubeSize);

			bbs[i] = new BoundingBox();
			bss[i] = new BoundingSphere();    

			// Aqui uso un FloatBuffer para pasarle los vertices a los algoritmos de calculo de
			// bbs y bss, es medio engorroso pero lo deje asi porque el codigo original de jMonkey
			// usa FloatBuffers. Mas adelante se puede intergrar mejor los calculos de bounding
			// volumes con las estructuras de datos de GLGraphics.
			FloatBuffer points = FloatBuffer.allocate(vertPerCube * 3);
			points.put(0, x0 - cubeSize);
			points.put(1, y0 - cubeSize); 
			points.put(2, z0 - cubeSize);    
			points.put(3, x0 + cubeSize); 
			points.put(4, y0 - cubeSize); 
			points.put(5, z0 - cubeSize);
			points.put(6, x0 + cubeSize); 
			points.put(7, y0 + cubeSize); 
			points.put(8, z0 - cubeSize);    
			points.put(9, x0 - cubeSize); 
			points.put(10, y0 + cubeSize); 
			points.put(11, z0 - cubeSize);    
			points.put(12, x0 - cubeSize); 
			points.put(13, y0 - cubeSize); 
			points.put(14, z0 + cubeSize);    
			points.put(15, x0 + cubeSize); 
			points.put(16, y0 - cubeSize); 
			points.put(17, z0 + cubeSize);    
			points.put(18, x0 + cubeSize); 
			points.put(19, y0 + cubeSize); 
			points.put(20, z0 + cubeSize);    
			points.put(21, x0 - cubeSize); 
			points.put(22, y0 + cubeSize); 
			points.put(23, z0 + cubeSize);
		}
		cubes.endUpdateVertices();

		cubes.initColors();
		cubes.setColors(255, 200);

		// Creating vertex indices for all the cubes in the model.
		// Since each cube is identical, the indices are the same,
		// with the exception of the shifting to take into account
		// the position of the cube inside the model.
		minInd = vertPerCube * cubeCount; 
		maxInd = -minInd;
		for (int i = 0; i < cubeCount; i++) {
			int n0 = indicesPerCube * i;    
			int m0 = vertPerCube * i;
			for (int j = 0; j < indicesPerCube; j++) indices0[n0 + j] = m0 + cubeIndices[j];
			minInd = min(minInd, vertPerCube * i);
			maxInd = max(maxInd, vertPerCube * (i + 1) - 1);    
		}  

		cubes.initIndices(indicesPerCube * cubeCount, GLModel.STREAM);
		cubes.autoIndexBounds(false);
		cubes.updateIndices(indices0, indices0.length);
		cubes.setMinIndex(minInd);
		cubes.setMaxIndex(maxInd);
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "test.DataVis" });
	}
}
