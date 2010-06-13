/**
 * This java package provides classes to ease the creation of
 * interactive 3D scenes in Processing.
 * @author Jean Pierre Charalambos, A/Prof. National University of Colombia
 * (http://disi.unal.edu.co/profesores/pierre/, http://www.unal.edu.co/).
 * @version 0.9.0
 * 
 * Copyright (c) 2010 Jean Pierre Charalambos
 * 
 * This source file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. 
 */

package remixlab.proscene;

import processing.core.*;

import java.awt.Panel;
import java.lang.reflect.*;

public class Viewer extends PApplet {
	public PApplet parent;
	public Panel panel;
	public Scene scene;
	//scene parameters
	float radius;
	boolean updateFrustum;
	
	int width, height;
	Method method;
	Class<? extends PApplet> cls;
	
	//Aux viewers
	public Viewer mainViewer;
	public boolean showMainViewerCamera;
	
	public Viewer(PApplet p, String met, int w, int h, float r, boolean uFrustum) {
		initialize(p,w,h);
		method = searchDrawingMethod(method, met);		
		if(method == null) 
			method = searchDrawingMethod(method, "proscenium");
		radius = r;
		updateFrustum = uFrustum;
	}
	
	public Viewer(PApplet p, int w, int h, float r, boolean uFrustum) {
		initialize(p,w,h);
		method = searchDrawingMethod(method, "proscenium");
		radius = r;
		updateFrustum = uFrustum;
	}
	
	public Viewer(PApplet p, int w, int h, boolean uFrustum) {
		initialize(p,w,h);
		method = searchDrawingMethod(method, "proscenium");
		updateFrustum = uFrustum;
	}
	
	public Viewer(PApplet p, String met, int w, int h) {
		initialize(p,w,h);
		method = searchDrawingMethod(method, met);		
		if(method == null) 
			method = searchDrawingMethod(method, "proscenium");
	}
	
	public Viewer(PApplet p, String met, int w, int h, boolean uFrustum) {
		initialize(p,w,h);
		method = searchDrawingMethod(method, met);		
		//if(method == null)
			//method = searchDrawingMethod(method, "proscenium");
		updateFrustum = uFrustum;
	}
	
	public Viewer(PApplet p, int w, int h) {
		initialize(p,w,h);
		method = searchDrawingMethod(method, "proscenium");
	}
	
	//Aux viewers
	public Viewer(PApplet p, Viewer mViewer, String met, int w, int h) {
		initialize(p,w,h);
		mainViewer = mViewer;
		if(mainViewer != null)
			showMainViewerCamera = true;
		method = searchDrawingMethod(method, met);		
		if(method == null) 
			method = searchDrawingMethod(method, "proscenium");
	}
	
	public Viewer(PApplet p, Viewer mViewer, int w, int h) {
		initialize(p,w,h);
		mainViewer = mViewer;
		if(mainViewer != null)
			showMainViewerCamera = true;
		method = searchDrawingMethod(method, "proscenium");
	}
	
	protected void initialize( PApplet p,   int w,   int h) {
		parent = p;
		width = w;
		height = h;
		panel = new Panel();		
		panel.setEnabled(false);
		panel.setVisible(false);		
		panel.add(this);
		parent.add(this.panel);
		parent.validate();
		panel.setEnabled(true);
		panel.setVisible(true);
		cls = parent.getClass();
		method = null;
		mainViewer = null;
		showMainViewerCamera = false;
		//scene parameters
		radius = 100;
		updateFrustum = false;
	}
	
	public void setup() {
		size(width, height, P3D);
		scene = new Scene(this);
		scene.setRadius(radius);
		scene.enableFrustumUpdate(updateFrustum);
		//scene.setAxisIsDrawn(updateFrustum);
		//scene.enableFrustumUpdate(true);
		scene.showAll();
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void setScene(Scene scn) {
		scene = scn;
	}
	
	public void draw() {
		background(0);
		if (this!=null && scene != null) {
			scene.beginDraw();
			
			if(method != null)
				drawingMethod(this);
			else
				proscenium();
			
			//aux viewer
			if ((showMainViewerCamera) && (mainViewer.getScene() !=null))
			      DrawingUtils.drawCamera(this, mainViewer.getScene().camera() );
			scene.endDraw();
		}
	}
	
	// /**
	public void keyPressed() {
		if ( mainViewer != null ) {
			if (key == 'x')
				showMainViewerCamera = !showMainViewerCamera;
		}		
	}
	// */
	
	public void proscenium() {}
	
	protected Method searchDrawingMethod(Method method, String name) {		
		try {
			method = cls.getDeclaredMethod(name, new Class[] { PApplet.class });
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			method = null;
		}
		return method;
	}
	
	public void drawingMethod(PApplet p) {
		if (p!=null)
			try {
				method.invoke(parent, new Object[] { p });
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}	
}
