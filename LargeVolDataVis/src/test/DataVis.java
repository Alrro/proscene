package test;

import processing.core.*;
import codeanticode.glgraphics.*;
import remixlab.proscene.*;
import test.Chronometer;
import test.OctreeNode;
import test.OptimizatorRender;
import java.util.ArrayList;

public class DataVis extends PApplet {
	private static final long serialVersionUID = 1L;

	int lastTimeMyFxWasIssued = -1;// lastFrameUpdate is init to 0

	// La maxima profundidad del octree. El tiempo de inicializacion
	// depende exponencialmente de este parametro.
	int maxOctreeLevel = 2;

	// Si esta variable es true, entonces el modelo se dibuja con una sola
	// llamada a renderer.model(), reuniendo todos los indices de los nodos
	// visibles del octree en un unico array.
	// En el caso de que sea false, cada nodo visible se dibuja por separado.
	boolean oneDrawCall = true;

	// Tamanio del volumen 3D donde los objectos son distribuidos aleatoriamente
	float volSize = 1000;

	// Numero total de objetos individuales:
	int objCount = 500;

	// Tamanio de cada objeto.
	float objSize = 10;

	boolean drawBBS = false;
	boolean drawBSS = false;
	boolean enableVFC = true;
	boolean enableBFC = true;
	boolean cameraChange = false;
	boolean useLOD = false;

	Scene scene;
	PVector viewDir;
	OptimizatorRender optim;
	Chronometer chrono;

	GLModel objects;
	int allIndices[];
	int minInd, maxInd;
	int vertPerObj;
	int indicesPerObj;

	GLModelEffect lod;

	public void setup() {
		size(600, 600, GLConstants.GLGRAPHICS);
		frameRate(120);

		chrono = new Chronometer(this);

		// lod = new GLModelEffect(this, "lod.xml");

		scene = new Scene(this);
		scene.enableFrustumEquationsUpdate();
		scene.setRadius(volSize);
		scene.showAll();
		scene.setAxisIsDrawn(false);
		scene.setGridIsDrawn(false);
		// scene.disableKeyboardHandling();

		sphereDetail(10);

		println("Creando cubos...");
		createObjects();
		println("Creando el Optimizador...");
		optim = new OptimizatorRender(objects, lod, allIndices, scene);
		println("Listo.");
	}

	public void draw() {
		chrono.update();

		// Esta variable la usamos para evitar re-enviar los indices.
		// cameraChange = scene.camera().lastFrameUpdate == frameCount - 1;
		// println(scene.camera().lastFrameUpdate + " " + (frameCount - 1) + " "
		// + cameraChange);
		cameraChange = scene.camera().lastFrameUpdate != lastTimeMyFxWasIssued;

		GLGraphics renderer = (GLGraphics) g;
		renderer.beginGL();
		renderer.lights();

		// shader.start();
		// shader.setFloatUniform("FpLevel", 0);
		// shader.setFloatUniform("Radius", 100);
		// shader.setVecUniform("Color", 1, 1, 0, 1);
		
		viewDir = scene.camera().viewDirection(); //cached

		if (enableVFC) {
			optim.vfc(renderer, objects, lod, scene);
		} else {
			// Dibujando todo el sistema, sin optimizacion de VFC.
			if (useLOD) {
				renderer.model(objects, lod);
			} else {
				renderer.model(objects);
			}
		}

		// shader.stop();
		renderer.endGL();

		if (enableVFC && (drawBBS || drawBSS)) {
			pintarOc(optim.oc);
		}

		chrono.printFps();
	}

	public void keyPressed() {
		// only useful if scene.disableKeyboardHandling() has been called in
		// setup
		// if (key == 'c' || key == 'C') { scene.toggleCameraType(); return; }
		
		if (key == 'b' || key == 'B') {
			enableBFC = !enableBFC;
			if(enableBFC)
				println("Activando BFC");
			else
				println("Desactivando BFC");			
		}

		if (key == 'v' || key == 'V') {
			if (enableVFC) {
				disableVFC();
			} else {
				enableVFC();
			}
			return;
		}

		if (key == 'x' || key == 'X') {
			if (drawBBS) {
				drawBBS = false;
				println("Deshabilitando dibujado de los bounding boxes del octree");
			} else {
				drawBBS = true;
				println("Habilitando dibujado de los bounding boxes del octree");
			}
			return;
		}

		if (key == 'y' || key == 'Y') {
			if (drawBSS) {
				drawBSS = false;
				println("Deshabilitando dibujado de los bounding spheres del octree");
			} else {
				drawBSS = true;
				println("Habilitando dibujado de los bounding spheres del octree");
			}
			return;
		}

		if (key == 'l' || key == 'L') {
			if (useLOD) {
				useLOD = false;
				println("Deshabilitando LOD");
			} else {
				useLOD = true;
				println("Habilitando LOD");
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

	private void pintarOc(OctreeNode oct) {
		// Los bounding volumes no se dibujan a trabjas de GLGraphics, sino que
		// con la API regular
		// de Processing, con lo cual no es recomendable dibujarlos cuando se
		// tiene un GLModel muy
		// grande.
		if (oct.hijos != null) {
			for (int i = 0; i < 8; i++) {
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

	private void enableVFC() {
		scene.enableFrustumEquationsUpdate();
		enableVFC = true;
		println("Activando VFC");
	}

	private void disableVFC() {
		scene.disableFrustumEquationsUpdate();
		enableVFC = false;
		println("Desactivando VFC");

		// Necesitamos de usar todos los indices. Pero los enviamos
		// una sola vez.
		objects.updateIndices(allIndices, allIndices.length);
		objects.setMinIndex(minInd);
		objects.setMaxIndex(maxInd);
	}

	private void createObjects() {
		ArrayList<PVector> vertices = new ArrayList<PVector>();
		ArrayList<PVector> normals = new ArrayList<PVector>();
		ArrayList<float[]> centers = new ArrayList<float[]>();

		// Primero creamos un objeto individual.
		createObject(vertices, normals);
		vertPerObj = vertices.size();
		indicesPerObj = vertices.size();

		// Creamos un GLModel conteniendo objCount copias del objeto:
		objects = new GLModel(this, vertPerObj * objCount, TRIANGLES,
				GLModel.STATIC);

		objects.beginUpdateVertices();
		for (int i = 0; i < objCount; i++) {
			int n0 = vertPerObj * i;

			// Desplazamos la copia i-esima de manera aleatoria dentro del
			// volumen de
			// visualizacion
			float x0 = random(-volSize, +volSize);
			float y0 = random(-volSize, +volSize);
			float z0 = random(-volSize, +volSize);

			for (int j = 0; j < vertPerObj; j++) {
				PVector v = (PVector) vertices.get(j);
				objects.updateVertex(n0 + j, x0 + objSize * v.x, y0 + objSize
						* v.y, z0 + objSize * v.z);
				// Guardando el centro de la copia actual del objecto, para cada
				// vertice del mismo.
				centers.add(new float[] { x0, y0, z0 });
			}
		}
		objects.endUpdateVertices();

		objects.initNormals();
		objects.beginUpdateNormals();
		for (int i = 0; i < objCount; i++) {
			int n0 = vertPerObj * i;
			for (int j = 0; j < vertPerObj; j++) {
				PVector v = (PVector) normals.get(j);
				objects.updateNormal(n0 + j, v.x, v.y, v.z);
			}
		}
		objects.endUpdateNormals();

		objects.initAttributes(1);
		objects.setAttribute(0, "Object center", 3);
		objects.updateAttributes(0, centers);

		objects.initColors();
		objects.setColors(255, 200, 120, 255);

		// Los vertices del GLModel determinan una secuencia de triangulos,
		// por lo cual los indices reflejan esta estructura:
		int n = indicesPerObj * objCount;
		allIndices = new int[n];
		for (int i = 0; i < n; i++) {
			allIndices[i] = i;
		}
		minInd = 0;
		maxInd = n - 1;

		objects.initIndices(allIndices.length, GLModel.STREAM);
		objects.autoIndexBounds(false);
		objects.updateIndices(allIndices, allIndices.length);
		objects.setMinIndex(minInd);
		objects.setMaxIndex(maxInd);
	}

	private void createObject(ArrayList<PVector> vertices,
			ArrayList<PVector> normals) {
		addTriangle(vertices, normals, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f);

		addTriangle(vertices, normals, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f,
				0.0f, 1.0f, 0.0f);

		addTriangle(vertices, normals, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f);

		addTriangle(vertices, normals, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 0.0f);

		addTriangle(vertices, normals, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
				0.0f, -1.0f, 0.0f);

		addTriangle(vertices, normals, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f,
				0.0f, -1.0f, 0.0f);

		addTriangle(vertices, normals, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f,
				0.0f, -1.0f, 0.0f);

		addTriangle(vertices, normals, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
				0.0f, -1.0f, 0.0f);
	}

	private void addTriangle(ArrayList<PVector> vertices,
			ArrayList<PVector> normals, float x0, float y0, float z0, float x1,
			float y1, float z1, float x2, float y2, float z2) {
		PVector v0 = new PVector(x0, y0, z0);
		PVector v1 = new PVector(x1, y1, z1);
		PVector v2 = new PVector(x2, y2, z2);

		PVector v01 = PVector.sub(v1, v0);
		PVector v02 = PVector.sub(v2, v0);
		PVector tnorm = v01.cross(v02);

		vertices.add(v0);
		vertices.add(v1);
		vertices.add(v2);

		normals.add(tnorm);
		normals.add(tnorm);
		normals.add(tnorm);
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "test.DataVis" });
	}
}
