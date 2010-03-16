/**
 * This java package provides classes to ease the creation of
 * interactive 3D scenes in Processing.
 * @author Jean Pierre Charalambos, A/Prof. National University of Colombia
 * (http://disi.unal.edu.co/profesores/pierre/, http://www.unal.edu.co/).
 * @version 0.7.0
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

import java.awt.event.*;
import java.util.*;

/**
 * Interface for objects that grab mouse focus in a Scene. 
 * <p> 
 * MouseGrabber are objects which react to the mouse cursor, usually when it
 * hovers over them. 
 * <p> 
 * <h3>How does it work ?</h3>
 * All the created MouseGrabbers are grouped in a mouse grabber pool.
 * The Scene parse this pool, calling all the MouseGrabbers'
 * {@link #checkIfGrabsMouse(int, int, Camera)} methods that {@link #setGrabsMouse(boolean)}
 * if desired (method calls should actually be performed on concrete class instances
 * such as InteractiveFrame). 
 * <p> 
 * When a MouseGrabber {@link #grabsMouse()}, it becomes the {@link remixlab.proscene.Scene#mouseGrabber()}.
 * All the mouse events are then transmitted to it instead of being normally processed.
 * This continues while {@link #grabsMouse()} (updated using
 * {@link #checkIfGrabsMouse(int, int, Camera)}) returns {@code true}. 
 * <p> 
 * If you want to (temporarily) disable a specific MouseGrabbers, you can remove it
 * from this pool using {@link #removeFromMouseGrabberPool()}.
 */
public interface MouseGrabber {
	
	final static List<MouseGrabber> MouseGrabberPool = new ArrayList<MouseGrabber>();
	
	/**
	 * Returns a list containing references to all the active MouseGrabbers. 
	 * <p> 
	 * Used by the Scene to parse all the MouseGrabbers and to check if any
	 * of them {@link #grabsMouse()} {@link #checkIfGrabsMouse(int, int, Camera)}. 
	 * <p> 
	 * You should not have to directly use this list. Use
	 * {@link #removeFromMouseGrabberPool()} and {@link #addInMouseGrabberPool()}
	 * to modify this list.
	 * 
	 * @see #getMouseGrabberPool()
	 */    
    List<MouseGrabber> getMouseGrabberPool();
	
	/**
	 * Called by the Scene before they test if the MouseGrabber {@link #grabsMouse()}.
	 * Should {@link #setGrabsMouse(boolean)} according to the mouse position. 
	 * <p> 
	 * This is the core method of the MouseGrabber. Its goal is to update the 
	 * {@link #grabsMouse()} flag according to the mouse and MouseGrabber current positions,
	 * using {@link #setGrabsMouse(boolean)}.
	 * <p>
	 * {@link #grabsMouse()} is usually set to {@code true} when the mouse cursor is close
	 * enough to the MouseGrabber position. It should also be set to {@code false} when the
	 * mouse cursor leaves this region in order to release the mouse focus.
	 * <p>
	 * {@code x} and {@code y} are the mouse cursor coordinates ((0,0) corresponds to the
	 * upper left corner). 
	 * <p> 
	 * A typical implementation will look like:
	 * <p> 
	 * {@code // (posX,posY) is the position of the MouseGrabber on screen.} <br>
	 * {@code // Here, distance to mouse must be less than 10 pixels to activate the MouseGrabber.} <br>
	 * {@code setGrabsMouse( PApplet.sqrt((x-posX)*(x-posX) + (y-posY)*(y-posY)) < 10);} <br> 
	 * <p> 
	 * If the MouseGrabber position is defined in 3D, use the {@code camera} parameter,
	 * corresponding to the calling Scene Camera. Project on screen and then compare
	 * the projected coordinates: 
	 * <p> 
	 * {@code PVector proj = new PVector (camera.projectedCoordinatesOf(myMouseGrabber.frame().position());} <br>
	 * {@code setGrabsMouse((PApplet.abs(x-proj.x) < 5) && (PApplet.(y-proj.y) < 2)); // Rectangular region} <br>
	 */
	void checkIfGrabsMouse(int x, int y, Camera camera);
	
    boolean grabsMouse();
    
    void setGrabsMouse(boolean grabs);
    
    boolean isInMouseGrabberPool();
    
    void addInMouseGrabberPool();
    
    void removeFromMouseGrabberPool();
    
    void clearMouseGrabberPool();
	
    /**
     * Callback method called when the MouseGrabber {@link #grabsMouse()} and a mouse
     * button is pressed. 
     * <p> 
     * The MouseGrabber will typically start an action or change its state when a
     * mouse button is pressed. {@link #mouseMoveEvent(MouseEvent, Camera)} (called at
     * each mouse displacement) will then update the MouseGrabber accordingly and 
     * {@link #mouseReleaseEvent(MouseEvent, Camera)} (called when the mouse button
     * is released) will terminate this action.
     */
    void mousePressEvent(MouseEvent event, Camera camera);
    
    /**
     * Callback method called when the MouseGrabber {@link #grabsMouse()} and the mouse
     * is moved while a button is pressed. 
     * <p> 
     * This method will typically update the state of the MouseGrabber from the mouse
     * displacement. See the {@link #mousePressEvent(MouseEvent, Camera)} documentation
     * for details.
     */
    void mouseMoveEvent(MouseEvent event, Camera camera);
    
    /**
     * Mouse release event callback method.
     * 
     * @see #mousePressEvent(MouseEvent, Camera)
     */
    void mouseReleaseEvent(MouseEvent event, Camera camera);
    
    /**
     * Callback method called when the MouseGrabber {@link #grabsMouse()} and a mouse
     * button is double clicked.
     */     
    void mouseDoubleClickEvent(MouseEvent event, Camera camera);
    
    /**
     * Callback method called when the MouseGrabber {@link #grabsMouse()} and the mouse
     * wheel is used.
     */
    void mouseWheelEvent(MouseWheelEvent event, Camera camera);    
}
