package proscene;

import java.awt.event.*;
import java.util.*;

/**
 * Interface for objects that grab mouse focus in a PScene. 
 * <p> 
 * PSMouseGrabber are objects which react to the mouse cursor, usually when it
 * hovers over them. 
 * <p> 
 * <h3>How does it work ?</h3>
 * All the created PSMouseGrabbers are grouped in a mouse grabber pool.
 * The PScene parse this pool, calling all the PSMouseGrabbers'
 * {@link #checkIfGrabsMouse(int, int, PSCamera)} methods that {@link #setGrabsMouse(boolean)}
 * if desired (method calls should actually be performed on concrete class instances
 * such as PSInteractiveFrame). 
 * <p> 
 * When a PSMouseGrabber {@link #grabsMouse()}, it becomes the {@link proscene.PScene#mouseGrabber()}.
 * All the mouse events are then transmitted to it instead of being normally processed.
 * This continues while {@link #grabsMouse()} (updated using
 * {@link #checkIfGrabsMouse(int, int, PSCamera)}) returns {@code true}. 
 * <p> 
 * If you want to (temporarily) disable a specific PSMouseGrabbers, you can remove it
 * from this pool using {@link #removeFromMouseGrabberPool()}.
 */
public interface PSMouseGrabber {
	
	final static List<PSMouseGrabber> MouseGrabberPool = new ArrayList<PSMouseGrabber>();
	
	/**
	 * Returns a list containing references to all the active PSMouseGrabbers. 
	 * <p> 
	 * Used by the PScene to parse all the PSMouseGrabbers and to check if any
	 * of them {@link #grabsMouse()} {@link #checkIfGrabsMouse(int, int, PSCamera)}. 
	 * <p> 
	 * You should not have to directly use this list. Use
	 * {@link #removeFromMouseGrabberPool()} and {@link #addInMouseGrabberPool()}
	 * to modify this list.
	 * 
	 * @see #getMouseGrabberPool()
	 */    
    List<PSMouseGrabber> getMouseGrabberPool();
	
	/**
	 * Called by the PScene before they test if the PSMouseGrabber {@link #grabsMouse()}.
	 * Should {@link #setGrabsMouse(boolean)} according to the mouse position. 
	 * <p> 
	 * This is the core method of the PSMouseGrabber. Its goal is to update the 
	 * {@link #grabsMouse()} flag according to the mouse and PSMouseGrabber current positions,
	 * using {@link #setGrabsMouse(boolean)}.
	 * <p>
	 * {@link #grabsMouse()} is usually set to {@code true} when the mouse cursor is close
	 * enough to the PSMouseGrabber position. It should also be set to {@code false} when the
	 * mouse cursor leaves this region in order to release the mouse focus.
	 * <p>
	 * {@code x} and {@code y} are the mouse cursor coordinates ((0,0) corresponds to the
	 * upper left corner). 
	 * <p> 
	 * A typical implementation will look like:
	 * <p> 
	 * {@code // (posX,posY) is the position of the PSMouseGrabber on screen.} <br>
	 * {@code // Here, distance to mouse must be less than 10 pixels to activate the MouseGrabber.} <br>
	 * {@code setGrabsMouse( PApplet.sqrt((x-posX)*(x-posX) + (y-posY)*(y-posY)) < 10);} <br> 
	 * <p> 
	 * If the PSMouseGrabber position is defined in 3D, use the {@code camera} parameter,
	 * corresponding to the calling PScene PSCamera. Project on screen and then compare
	 * the projected coordinates: 
	 * <p> 
	 * {@code PVector proj = new PVector (camera.projectedCoordinatesOf(myPSMouseGrabber.frame().position());} <br>
	 * {@code setGrabsMouse((PApplet.abs(x-proj.x) < 5) && (PApplet.(y-proj.y) < 2)); // Rectangular region} <br>
	 */
	void checkIfGrabsMouse(int x, int y, PSCamera camera);
	
    boolean grabsMouse();
    
    void setGrabsMouse(boolean grabs);
    
    boolean isInMouseGrabberPool();
    
    void addInMouseGrabberPool();
    
    void removeFromMouseGrabberPool();
    
    void clearMouseGrabberPool();
	
    /**
     * Callback method called when the PSMouseGrabber {@link #grabsMouse()} and a mouse
     * button is pressed. 
     * <p> 
     * The PSMouseGrabber will typically start an action or change its state when a
     * mouse button is pressed. {@link #mouseMoveEvent(MouseEvent, PSCamera)} (called at
     * each mouse displacement) will then update the PSMouseGrabber accordingly and 
     * {@link #mouseReleaseEvent(MouseEvent, PSCamera)} (called when the mouse button
     * is released) will terminate this action.
     */
    void mousePressEvent(MouseEvent event, PSCamera camera);
    
    /**
     * Callback method called when the PSMouseGrabber {@link #grabsMouse()} and the mouse
     * is moved while a button is pressed. 
     * <p> 
     * This method will typically update the state of the PSMouseGrabber from the mouse
     * displacement. See the {@link #mousePressEvent(MouseEvent, PSCamera)} documentation
     * for details.
     */
    void mouseMoveEvent(MouseEvent event, PSCamera camera);
    
    /**
     * Mouse release event callback method.
     * 
     * @see #mousePressEvent(MouseEvent, PSCamera)
     */
    void mouseReleaseEvent(MouseEvent event, PSCamera camera);
    
    /**
     * Callback method called when the PSMouseGrabber {@link #grabsMouse()} and a mouse
     * button is double clicked.
     */     
    void mouseDoubleClickEvent(MouseEvent event, PSCamera camera);
    
    /**
     * Callback method called when the MouseGrabber {@link #grabsMouse()} and the mouse
     * wheel is used.
     */
    void mouseWheelEvent(MouseWheelEvent event, PSCamera camera);    
}
