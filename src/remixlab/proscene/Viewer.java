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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PApplet parent;
	public Panel panel;
	Scene scene;
	public int width, height;
	public boolean registered;
	
	public Viewer(final PApplet p, final int w, final int h) {
		parent = p;
		width = w;
		height = h;
		registered = true;
		panel = new Panel();		
		panel.setEnabled(false);
		panel.setVisible(false);		
		panel.add(this);
		parent.add(this.panel);
		panel.setEnabled(true);
		panel.setVisible(true);
	}
	
	public void setup() {
		size(width, height, P3D);
		scene = new Scene(this);
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
		if(registered)
			proscenium(scene.parent);
		else
			proscenium();
		scene.endDraw();
		}
	}
	
	public void proscenium() {}
	
	//public void proscenium() {
	public void proscenium(final PApplet p) {
		if(p!=null) {
		boolean foundProscenium = true;
		Method method = null;
		
		Class<? extends PApplet> cls = parent.getClass();
		//parent.getClass().getName()
		
		try {
			//method = cls.getDeclaredMethod( "proscenium", new Class[] {PApplet.class} );
			method = parent.getClass().getDeclaredMethod( "proscenium", new Class[] {PApplet.class} );
			//method = parent.getClass().getDeclaredMethod( "proscenium" );
		} catch (final SecurityException e) {			
			e.printStackTrace();
			foundProscenium = false;
		} catch (final NoSuchMethodException e) {			
			foundProscenium = false;
		}
		if (foundProscenium && method != null) {
			try {
				method.invoke(parent, new Object [] {p});
				//method.invoke(parent);
			}  
			catch(final IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); }
			catch (final InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (final IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}		
	}
	}
}
