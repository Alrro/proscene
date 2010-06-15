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
	
	String scnConfigMethod;
	
	int width, height, color;
	Method method;
	
	/**
	//Aux viewers
	public Viewer mainViewer;
	public boolean showMainViewerCamera;
	*/
	
	public Viewer(PApplet p, String met, int w, int h, int c, String cScn) {
		//initialize(p,w,h);
		//1. Main container papplet
		parent = p;
		
		//2. Viewer's size and color
		width = w;
		height = h;		
		color = c;
		
		//3. Lay out (this block seems the one having trouble)
		panel = new Panel();
		//panel.setVisible(false);
		panel.setEnabled(false);//testing
		panel.add(this);	
		parent.add(panel);
		parent.validate();//testing
		//panel.setVisible(true);
		panel.setEnabled(true);//testing
		
		/**
		panel = new Panel();		
		panel.setEnabled(false);
		panel.setVisible(false);		
		panel.add(this);
		parent.add(this.panel);
		parent.validate();
		panel.setEnabled(true);
		panel.setVisible(true);
		// */
		//
		
		//4. Drawing method
		method = null;
		if(met != null)
			method = searchDrawingMethod(method, met);		
		if(method == null) 
			method = searchDrawingMethod(method, "proscenium");
		
		//5. Configure method
		scnConfigMethod = cScn;
		
		/**
		//6. Auxiliary viewer
		mainViewer = null;
		showMainViewerCamera = false;
		*/
	}
	
	public Viewer(PApplet p, String met, int w, int h, int c) {
		this(p, met, w, h, c, null);
	}
	
	public Viewer(PApplet p, int w, int h, int c, String cScn) {
		this(p, null, w, h, c, cScn);
	}
	
	public Viewer(PApplet p, int w, int h, int c) {
		this(p, null, w, h, c, null);
	}
	
	public Viewer(PApplet p, String met, int w, int h, String cScn) {
		this(p, met, w, h, 0, cScn);
	}
	
	public Viewer(PApplet p, String met, int w, int h) {
		this(p, met, w, h, 0, null);
	}
	
	public Viewer(PApplet p, int w, int h, String cScn) {
		this(p, null, w, h, 0, cScn);
	}
	
	public Viewer(PApplet p, int w, int h) {
		this(p, null, w, h, 0, null);
	}
	
	/**
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
	*/
	
	public void setup() {
		size(width, height, P3D);
		scene = new Scene(this);
		
		if (scnConfigMethod != null)
			configureScene(scene, scnConfigMethod);
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void configureScene(Scene scn, String name) {
		Method sMethod = null;
		try {
			sMethod = parent.getClass().getDeclaredMethod(name, new Class[] { Scene.class });
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			sMethod = null;
		}
		if (sMethod!=null)
			try {
				sMethod.invoke(parent, new Object[] { scn });
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
	
	public void draw() {
		background(color);
		if (this!=null && scene != null) {
			scene.beginDraw();
			
			if(method != null)
				drawingMethod(this);
			else
				proscenium();
			
			/**
			//aux viewer
			if ((showMainViewerCamera) && (mainViewer.getScene() !=null))
			      DrawingUtils.drawCamera(this, mainViewer.getScene().camera() );
			*/
			
			scene.endDraw();
		}
	}
	
	 /**
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
			method = parent.getClass().getDeclaredMethod(name, new Class[] { PApplet.class });
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			method = null;
		}
		return method;
	}
	
	public void drawingMethod(PApplet p) {
		if (p!=null && method!=null)
			try {
				method.invoke(parent, new Object[] { p });
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
	}
}
