package test;

/*
 * Actualmente implementado solo para TRIANGLES
 * */
import java.nio.FloatBuffer;
import java.util.ArrayList;
import processing.core.PApplet;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLModel;

public class OctreeNode {
  int level;
  OctreeNode hijos[];
  GLModel model;
  int[] indices;
  DataVis parent;
  int idxMax, idxMin;
  float xMax, xMin, yMax, yMin, zMax, zMin;
  float xMedio, yMedio, zMedio;
  BoundingBox bbs;
  BoundingSphere bss;

  public OctreeNode() {
    hijos = null;
  }

  public OctreeNode(GLModel model, int[] indices, DataVis parent, int level) {
    this.parent = parent;
    this.model = model;
    this.indices = indices;

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
    // PApplet.println("Drawing octree");
    model.updateIndices(indices, indices.length);
    model.setMinIndex(idxMin);
    model.setMaxIndex(idxMax);
    renderer.model(model);
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
      // Por convencion: solamente se usa el primer vertice del triangulo para
      // determinar la pertenencia a los hijos del octree. Cualquier otra
      // convencion
      // que determine una pertenencia univoca de cada triangulo seria
      // igualmente valida.
      // Al asignar indices en grupos de tres, siendo cada grupo un triangulo
      // del modelo
      // original, se asegura que (a) se pierdan triangulos al quedar vertices
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
    hijos[0] = new OctreeNode(model, toIntArray(inds1), parent, level);
    hijos[1] = new OctreeNode(model, toIntArray(inds2), parent, level);
    hijos[2] = new OctreeNode(model, toIntArray(inds3), parent, level);
    hijos[3] = new OctreeNode(model, toIntArray(inds4), parent, level);
    hijos[4] = new OctreeNode(model, toIntArray(inds5), parent, level);
    hijos[5] = new OctreeNode(model, toIntArray(inds6), parent, level);
    hijos[6] = new OctreeNode(model, toIntArray(inds7), parent, level);
    hijos[7] = new OctreeNode(model, toIntArray(inds8), parent, level);
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
