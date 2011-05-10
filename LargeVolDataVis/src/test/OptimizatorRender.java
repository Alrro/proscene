package test;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import remixlab.proscene.Scene;
import codeanticode.glgraphics.*;

public class OptimizatorRender {
	/*
	 * Declaracion Miembros de Clase
	 */
	OctreeNode oc;
	ArrayList<Integer> indexes = new ArrayList<Integer>();
	Scene scene;
	DataVis parent;

	int[] drawIndices;
	int indCount;
	int indMax, indMin;

	/**
	 * This method creates an Octree Node for the given GLModel, the root of the
	 * node is a class member called oc
	 * 
	 * @param model
	 *            The GLModel that you create the Octree Node
	 * */
	public OptimizatorRender(GLModel model, GLModelEffect lod, int[] indices, Scene scn) {
		scene = scn;
		this.parent = (DataVis) scene.parent;
		drawIndices = new int[indices.length];
		oc = new OctreeNode(model, lod, indices, scene, 0);
	}

	/**
	 * This method allows the View Frustrum Culling for a GLModel
	 * 
	 * @param model
	 *            The GLModel we will apply the VFC
	 */
	public void vfc(GLGraphics renderer, GLModel model, GLModelEffect lod, Scene scene) {
		if (parent.oneDrawCall) {
			indCount = 0;
			indMin = drawIndices.length;
			indMax = -indMin;
		}
		vfcVerify(oc, scene, renderer, 1);
		if (parent.oneDrawCall) {
			if (parent.cameraChange) {
				model.updateIndices(drawIndices, indCount);
				model.setMinIndex(indMin);
				model.setMaxIndex(indMax);
			}
			if (parent.useLOD) {
				renderer.model(model, lod);
			} else {
				renderer.model(model);
			}
		}
	}

	private void vfcVerify(OctreeNode octree, Scene scene, GLGraphics renderer, int a) {
		//BFC is enabled within VFC :P
		if(parent.enableBFC)
			if(scene.camera().coneIsBackFacing(parent.viewDir, octree.cone))
				return;
			
		PVector center = new PVector(octree.bss.center.x, octree.bss.center.y, octree.bss.center.z);
		switch (scene.camera().sphereIsVisible(center, octree.bss.radius)) {
		case VISIBLE:
			if (parent.oneDrawCall) {
				copyIndicesFromOC(octree);
			} else {
				octree.draw(renderer);
			}
			break;
		case SEMIVISIBLE:
			// PApplet.println("Rendering octree semivisible");
			PVector BBCorner1 = new PVector(
					(octree.bbs.center.x - octree.bbs.xExtent),
					(octree.bbs.center.y - octree.bbs.yExtent),
					(octree.bbs.center.z - octree.bbs.zExtent));
			PVector BBCorner2 = new PVector(
					(octree.bbs.center.x + octree.bbs.xExtent),
					(octree.bbs.center.y + octree.bbs.yExtent),
					(octree.bbs.center.z + octree.bbs.zExtent));
			switch (scene.camera().aaBoxIsVisible(BBCorner1, BBCorner2)) {
			case VISIBLE:
				if (parent.oneDrawCall) {
					copyIndicesFromOC(octree);
				} else {
					octree.draw(renderer);
				}
				break;
			case SEMIVISIBLE:
				if (octree.hijos == null) {
					if (parent.oneDrawCall) {
						copyIndicesFromOC(octree);
					} else {
						octree.draw(renderer);
					}
				} else {
					for (int i = 0; i < octree.hijos.length; i++) {
						a++;
						vfcVerify(octree.hijos[i], scene, renderer, a);
					}
				}
				break;
			case INVISIBLE:
				// System.out.println("no se imprime por cubo");
				break;
			}
			break;
		case INVISIBLE:
			// System.out.println("no se imprime por esfera");
			break;
		}
	}

	private void copyIndicesFromOC(OctreeNode oc) {
		if (parent.cameraChange) {
			PApplet.arrayCopy(oc.indices, 0, drawIndices, indCount,
					oc.indices.length);
			indCount += oc.indices.length;
			indMin = PApplet.min(indMin, oc.idxMin);
			indMin = PApplet.max(indMin, oc.idxMax);
		}
	}
}
