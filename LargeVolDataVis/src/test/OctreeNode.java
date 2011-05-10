package test;

/*
 * Actualmente implementado solo para TRIANGLES
 * */
import java.nio.FloatBuffer;
import java.util.ArrayList;
import remixlab.proscene.*;
import remixlab.proscene.Camera.*;
import processing.core.PApplet;
import processing.core.PVector;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;
import codeanticode.glgraphics.GLModelEffect;

public class OctreeNode {
	int level;
	OctreeNode hijos[];
	GLModel model;
	GLModelEffect lod;
	int[] indices;
	Scene scene;
	DataVis parent;
	int idxMax, idxMin;
	float xMax, xMin, yMax, yMin, zMax, zMin;
	float xMedio, yMedio, zMedio;
	BoundingBox bbs;
	BoundingSphere bss;
	Camera.Cone cone;

	public OctreeNode(Scene scn) {
		hijos = null;
		scene = scn;
		this.parent = (DataVis) scene.parent;
		cone = scene.camera().new Cone();
	}

	public OctreeNode(GLModel model, GLModelEffect lod, int[] indices, Scene scn, int level) {
		this.scene = scn;
		this.parent = (DataVis) scene.parent;
		this.model = model;
		this.lod = lod;
		this.indices = indices;

		//getNormals();
		cone = scene.camera().new Cone();

		idxMin = model.getSize();
		idxMax = idxMin;
		for (int i = 0; i < indices.length; i++) {
			idxMin = PApplet.min(idxMin, indices[i]);
			idxMax = PApplet.max(idxMax, indices[i]);
		}

		model.updateBounds(indices, indices.length);
		xMax = model.xmax;
		xMin = model.xmin;
		yMax = model.ymax;
		yMin = model.ymin;
		zMax = model.zmax;
		zMin = model.zmin;

		xMedio = ((xMax - xMin) / 2) + xMin;
		yMedio = ((yMax - yMin) / 2) + yMin;
		zMedio = ((zMax - zMin) / 2) + zMin;

		bbs = new BoundingBox();
		bss = new BoundingSphere();

		FloatBuffer buf = FloatBuffer.allocate(6);
		buf.put(0, xMax);
		buf.put(1, yMax);
		buf.put(2, zMax);
		buf.put(3, xMin);
		buf.put(4, yMin);
		buf.put(5, zMin);

		bbs.computeFromPoints(buf);
		bss.computeFromPoints(buf);

		this.level = level + 1;
		if (parent.maxOctreeLevel <= level)
			return;

		childsGeneration();
	}

	public void draw(GLGraphics renderer) {
		model.updateIndices(indices, indices.length);
		model.setMinIndex(idxMin);
		model.setMaxIndex(idxMax);
		if (parent.useLOD) {
			renderer.model(model, lod);
		} else {
			renderer.model(model);
		}
	}

	public ArrayList<PVector> getNormals() {
		ArrayList<PVector> list = new ArrayList<PVector>();

		model.beginUpdateNormals();
		FloatBuffer nbuf = model.normals;
		float norm[] = { 0, 0, 0 };
		for (int n = 0; n < indices.length; n += 1) {
			nbuf.position(4 * n);
			nbuf.get(norm, 0, 3);
			list.add(new PVector(norm[0], norm[1], norm[2]));
		}
		model.endUpdateNormals();

		return list;
	}

	private void childsGeneration() {
		ArrayList<Integer> inds1 = new ArrayList<Integer>(), inds2 = new ArrayList<Integer>(), inds3 = new ArrayList<Integer>(), inds4 = new ArrayList<Integer>(), inds5 = new ArrayList<Integer>(), inds6 = new ArrayList<Integer>(), inds7 = new ArrayList<Integer>(), inds8 = new ArrayList<Integer>();

		model.beginUpdateVertices();
		FloatBuffer vbuf = model.vertices;
		float vert[] = { 0, 0, 0 };
		for (int n = 0; n < indices.length; n += 3) {
			// Asumiendo que tres indices consecutivos representan un
			// triangulo.
			int vidx0 = indices[n];
			int vidx1 = indices[n + 1];
			int vidx2 = indices[n + 2];
			// Por convencion: solamente se usa el primer vertice del triangulo
			// para
			// determinar la pertenencia a los hijos del octree. Cualquier otra
			// convencion
			// que determine una pertenencia univoca de cada triangulo seria
			// igualmente valida.
			// Al asignar indices en grupos de tres, siendo cada grupo un
			// triangulo
			// del modelo
			// original, se asegura que (a) se pierdan triangulos al quedar
			// vertices
			// de los mismos
			// en distintas regiones del octree, y (b) no haya redundancia de
			// triangulos.
			vbuf.position(4 * vidx0);
			vbuf.get(vert, 0, 3);
			if (vert[0] > xMedio && vert[1] > yMedio && vert[2] > zMedio) {
				inds1.add(vidx0);
				inds1.add(vidx1);
				inds1.add(vidx2);
			}
			if (vert[0] > xMedio && vert[1] < yMedio && vert[2] > zMedio) {
				inds2.add(vidx0);
				inds2.add(vidx1);
				inds2.add(vidx2);
			}
			if (vert[0] > xMedio && vert[1] > yMedio && vert[2] < zMedio) {
				inds3.add(vidx0);
				inds3.add(vidx1);
				inds3.add(vidx2);
			}
			if (vert[0] > xMedio && vert[1] < yMedio && vert[2] < zMedio) {
				inds4.add(vidx0);
				inds4.add(vidx1);
				inds4.add(vidx2);
			}
			if (vert[0] < xMedio && vert[1] > yMedio && vert[2] > zMedio) {
				inds5.add(vidx0);
				inds5.add(vidx1);
				inds5.add(vidx2);
			}
			if (vert[0] < xMedio && vert[1] < yMedio && vert[2] > zMedio) {
				inds6.add(vidx0);
				inds6.add(vidx1);
				inds6.add(vidx2);
			}
			if (vert[0] < xMedio && vert[1] > yMedio && vert[2] < zMedio) {
				inds7.add(vidx0);
				inds7.add(vidx1);
				inds7.add(vidx2);
			}
			if (vert[0] < xMedio && vert[1] < yMedio && vert[2] < zMedio) {
				inds8.add(vidx0);
				inds8.add(vidx1);
				inds8.add(vidx2);
			}
		}
		vbuf.rewind();
		model.endUpdateVertices();

		hijos = new OctreeNode[8];
		hijos[0] = new OctreeNode(model, lod, toIntArray(inds1), scene, level);
		hijos[1] = new OctreeNode(model, lod, toIntArray(inds2), scene, level);
		hijos[2] = new OctreeNode(model, lod, toIntArray(inds3), scene, level);
		hijos[3] = new OctreeNode(model, lod, toIntArray(inds4), scene, level);
		hijos[4] = new OctreeNode(model, lod, toIntArray(inds5), scene, level);
		hijos[5] = new OctreeNode(model, lod, toIntArray(inds6), scene, level);
		hijos[6] = new OctreeNode(model, lod, toIntArray(inds7), scene, level);
		hijos[7] = new OctreeNode(model, lod, toIntArray(inds8), scene, level);
	}

	private int[] toIntArray(ArrayList<Integer> list) {
		Object obj[] = list.toArray();
		int res[] = new int[obj.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = (Integer) obj[i];
		}
		return res;
	}
}
