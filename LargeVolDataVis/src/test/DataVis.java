/**  
 * LargeVolDataVis
 * Programa implementado con las librerias Proscene y GLGraphics de Processing.
 * El objetivo es evaluar el uso de distintos metodos de oclusion de escena a  
 * efectos de mejorar la performance de render en tiempo real de modelos 3D con 
 * gran cantidad de vertices.
 * 
 * En este caso, toda la geometria esta almacenada en un unico objeto GLModel,
 * y subdividida dentro del mismo en cubos, cada uno de ellos utilzando 8 vertices. 
 * 
 * El uso de view-fustrum-culling (VFC) se activa/desactiva con la tecla V.
 * Para utilizar VFC pero sin actualizar el volumen de oclusion, persionar F.
 * 
 */

package test;

import java.nio.FloatBuffer;

import processing.core.*;
import codeanticode.glgraphics.*;
import remixlab.proscene.*;

public class DataVis extends PApplet {
	private static final long serialVersionUID = 1L;
	
	int cubeCount = 500;
	int vertPerCube = 8;
	float cubeSize = 10;
	float volSize = 1000;

	boolean drawingOC = true;
	boolean drawBoundingVolumes = false;
	boolean freezeCalc = false;
	boolean enableVFC,pruebaDibujado;
	
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
		size(800, 800, GLConstants.GLGRAPHICS);
		frameRate(120);

		chrono = new Chronometer(this);

		scene = new Scene(this);
		enableVFC=true;
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
		optim=new OptimizatorRender(cubes,this);
		println("Listo.");
	}
	
	public void draw() {
		
		chrono.update();

		GLGraphics renderer = (GLGraphics) g;
		if (enableVFC) {
			optim.vfc(renderer, cubes, scene);
		} else {
			renderer.beginGL();
			renderer.model(cubes);
			renderer.endGL();
		}
   

		if (drawBoundingVolumes) {
			// Los bounding volumes no se dibujan a trabjas de GLGraphics, sino que con la API regular
			// de Processing, con lo cual no es recomendable dibujarlos cuando se tiene un GLModel muy
			// grande.
			noFill();

			stroke(255, 0, 0);
			for (int i = 0; i < bbs.length; i++) {
				pushMatrix();
				translate(bbs[i].center.x, bbs[i].center.y, bbs[i].center.z);
				box(2.01f * bbs[i].xExtent, 2.01f * bbs[i].yExtent,
						2.01f * bbs[i].zExtent);
				popMatrix();
			}

			stroke(0, 255, 0);
			for (int i = 0; i < bss.length; i++) {
				pushMatrix();
				translate(bss[i].center.x, bss[i].center.y, bss[i].center.z);
				sphere(bss[i].radius);
				popMatrix();
			}
		}

		if (drawingOC) {
		  stroke(0,0,255);
		  pintarOc(optim.oc);
		}
		
		chrono.printFps();
	}
	
	void pintarOc(OctreeNode oct){
		if(oct.hijos!=null){
			for(int i=0;i<8;i++){
				pintarOc(oct.hijos[i]);
			}
		}

		noFill();
		stroke(255, 0, 0);
		pushMatrix();
		translate(oct.bbs.center.x, oct.bbs.center.y, oct.bbs.center.z);
		box(2.01f * oct.bbs.xExtent, 2.01f * oct.bbs.yExtent,
				2.01f * oct.bbs.zExtent);
		popMatrix();

		stroke(0, 255, 0);
		pushMatrix();
		translate(oct.bss.center.x, oct.bss.center.y, oct.bss.center.z);
		sphere(oct.bss.radius);
		popMatrix();

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
			if (drawBoundingVolumes) {
				drawBoundingVolumes = false;
				println("Deshabilitando dibujado de bounding volumes");
			} else {
				drawBoundingVolumes = true;
				println("Habilitando dibujado de bounding volumes");				
			}
			return;
		}
		
		if (key == 'o' || key == 'O') {
	      if (drawingOC) {
	        drawingOC = false;
	        println("Deshabilitando dibujado del octree");
	        
	      } else {
	        drawingOC = true;
	        println("Habilitando dibujado del octree");
	      }
	      return;		 
		}

		if ((key == 'f' || key == 'F') && enableVFC) {
			if (freezeCalc) {
				freezeCalc = false;
				println("Descongelando calculo de VFC");      
			} else {
				freezeCalc = true;
				println("Congelando calculo de VFC con " + renderedCubes + " cubos");
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

			bbs[i].computeFromPoints(points);
			bss[i].computeFromPoints(points);
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
