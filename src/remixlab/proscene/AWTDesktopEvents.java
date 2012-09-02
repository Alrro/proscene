/**
 *                     ProScene (version 1.2.0)      
 *    Copyright (c) 2010-2011 by National University of Colombia
 *                 @author Jean Pierre Charalambos      
 *           http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
 * 
 * This source file is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * A copy of the GNU General Public License is available on the World Wide Web
 * at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by
 * writing to the Free Software Foundation, 51 Franklin Street, Suite 500
 * Boston, MA 02110-1335, USA.
 */

package remixlab.proscene;

import java.awt.event.*;

import remixlab.remixcam.core.AbstractScene.Button;
import remixlab.remixcam.core.AbstractScene.CameraKeyboardAction;
import remixlab.remixcam.core.AbstractScene.ClickAction;
import remixlab.remixcam.core.AbstractScene.KeyboardAction;
import remixlab.remixcam.core.AbstractScene.MouseAction;
import remixlab.remixcam.core.InteractiveFrame;
import remixlab.remixcam.devices.DeviceGrabbable;
import remixlab.remixcam.geom.Point;

/**
 * This class provides low level java.awt.* based input event handling.
 * <p>
 * In order to handle input events generated by Processing with proscene, this object needs
 * to be registered  at the PApplet (which is done through
 * {@link remixlab.proscene.Scene#enableKeyboardHandling()} and
 * {@link remixlab.proscene.Scene#enableMouseHandling()}). Input events (keyboard and mouse)
 * generated via Processing will then be directed to {@link #keyEvent(KeyEvent)} and to
 * {@link #mouseEvent(MouseEvent)}.  
 */
public class AWTDesktopEvents {
	protected Scene scene;
	protected MouseAction camMouseAction;
	protected boolean keyHandled;
  //Z O O M _ O N _ R E G I O N
	public Point fCorner;// also used for SCREEN_ROTATE
	public Point lCorner;
	
	public AWTDesktopEvents(Scene s) {
		scene = s;
		camMouseAction = MouseAction.NO_MOUSE_ACTION;
		keyHandled = false;
		fCorner = new Point();
		lCorner = new Point();
	}
	
	// 1. KeyEvents
	
	/**
	 * Keyboard event handler.
	 * 
	 * @see remixlab.proscene.Scene#keyboardIsHandled()
	 * @see remixlab.proscene.Scene#enableKeyboardHandling(boolean)
	 */
	public void keyEvent(KeyEvent e) {
		if( !scene.keyboardIsHandled() )
			return;
		keyHandled = false;
		switch (e.getID()) {		
		case KeyEvent.KEY_PRESSED:
			break;
		case KeyEvent.KEY_TYPED:
			awtKeyTyped(e);
			break;
		case KeyEvent.KEY_RELEASED:
			awtKeyReleased(e);
			break;
		}
	}
	
	/**
	 * Implementation of the key typed event used to handle character shortcuts.
	 * <p>
	 * The handler queries the {@link remixlab.proscene.Scene#currentCameraProfile()}
	 * to see if there's a binding there first. If nothing is found,
	 * the handler look for it in the Scene then.
	 * 
	 * @see #keyTypedCameraKeyboardAction(KeyEvent)
	 * @see #keyTypedKeyboardAction(KeyEvent)
	 */
	protected void awtKeyTyped(KeyEvent e) {
		boolean handled = false;		
		if (scene.currentCameraProfile() != null)
			handled = keyTypedCameraKeyboardAction(e);
		if (!handled)
			handled = keyTypedKeyboardAction(e);
		keyHandled = handled;
	}
	
	/**
	 * Implementation of the key released event used to handle complex shortcuts, i.e.,
	 * shortcuts involving a keycode plus a modifier mask.
	 * <p>
	 * The handler looks for a possible binding in the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} first.
	 * If the {@link remixlab.proscene.Scene#currentCameraProfile()} doesn't bind an action,
	 * the handler searches for it in the Scene.
	 * 
	 * @see #keyReleasedCameraKeyboardAction(KeyEvent)
	 * @see #keyReleasedKeyboardAction(KeyEvent)
	 */
	protected void awtKeyReleased(KeyEvent e) {
		if(keyHandled)
			return;
		boolean handled = false;
		if (scene.currentCameraProfile() != null)
			handled = keyReleasedCameraKeyboardAction(e);
		if (!handled)
			keyReleasedKeyboardAction(e);
	}
	
	/**
	 * Internal use.
	 * <p>
	 * This method extracts the character associated with the key from the KeyEvent
	 * and then queries the {@link remixlab.proscene.Scene#currentCameraProfile()}
	 * to see if there's a binding for it.
	 * 
	 * @return true if a binding was found 
	 */
	protected boolean keyTypedCameraKeyboardAction(KeyEvent e) {
		CameraKeyboardAction kba = null;
		kba = scene.currentCameraProfile().shortcut( e.getKeyChar() );
		if (kba == null)
			return false;
		else {
			scene.handleCameraKeyboardAction(kba);
			return true;
		}
	}
	
	/**
	 * Internal use.
	 * <p>
	 * This method extracts the character associated with the key from the KeyEvent
	 * and then queries the Scene to see if there's a binding for it.
	 * 
	 * @return true if a binding was found 
	 */
	protected boolean keyTypedKeyboardAction( KeyEvent e) {
		if (!e.isAltDown() && !e.isAltGraphDown() && !e.isControlDown()	&& !e.isShiftDown()) {
			Integer path = scene.path(e.getKeyChar());
			if (path != null) {
				scene.pinhole().playPath(path);
				return true;
			}
		}
		
		KeyboardAction kba = null;
		kba = scene.shortcut(e.getKeyChar());
		if (kba == null)
			return false;
		else {
			scene.handleKeyboardAction(kba);
			return true;
		}
	}
	
	/**
	 * Internal use.
	 * <p>
	 * This method extracts the key combination (keycode +  modifier mask) associated with
	 * the KeyEvent and then queries the {@link remixlab.proscene.Scene#currentCameraProfile()}
	 * to see if there's a binding for it.
	 * 
	 * @return true if a binding was found 
	 */
	protected boolean keyReleasedCameraKeyboardAction(KeyEvent e) {
		CameraKeyboardAction kba = null;
		kba = scene.currentCameraProfile().shortcut( e.getModifiersEx(), e.getKeyCode() );
		if (kba == null)
			return false;
		else {
			scene.handleCameraKeyboardAction(kba);
			return true;
		}
	}
	
	/**
	 * Internal use.
	 * <p>
	 * This method extracts the key combination (keycode +  modifier mask) associated with
	 * the KeyEvent and then queries the Scene to see if there's a binding for it.
	 * 
	 * @return true if a binding was found 
	 */
	protected boolean keyReleasedKeyboardAction(KeyEvent e) {
		// 1. Key-frames
		// 1.1. Need to add a key-frame?
		if (((scene.addKeyFrameKeyboardModifier == Scene.Modifier.ALT) && (e.isAltDown()))
	   || ((scene.addKeyFrameKeyboardModifier == Scene.Modifier.ALT_GRAPH) && (e.isAltGraphDown()))
		 || ((scene.addKeyFrameKeyboardModifier == Scene.Modifier.CTRL) && (e.isControlDown()))
		 || ((scene.addKeyFrameKeyboardModifier == Scene.Modifier.SHIFT) && (e.isShiftDown()))) {
			Integer path = scene.path(e.getKeyCode());
			if (path != null) {
				scene.pinhole().addKeyFrameToPath(path);
				return true;
			}
		}
  	// 1.2. Need to delete a key-frame?
		if (((scene.deleteKeyFrameKeyboardModifier == Scene.Modifier.ALT) && (e.isAltDown()))
		 || ((scene.deleteKeyFrameKeyboardModifier == Scene.Modifier.ALT_GRAPH) && (e.isAltGraphDown()))
		 || ((scene.deleteKeyFrameKeyboardModifier == Scene.Modifier.CTRL) && (e.isControlDown()))
		 || ((scene.deleteKeyFrameKeyboardModifier == Scene.Modifier.SHIFT) && (e.isShiftDown()))) {
			Integer path = scene.path(e.getKeyCode());
			if (path != null) {
				scene.pinhole().deletePath(path);
				return true;
			}
		}		
		// 2. General actions
		KeyboardAction kba = null;
		kba = scene.shortcut( e.getModifiersEx(), e.getKeyCode() );
		if (kba == null)
			return false;
		else {
			scene.handleKeyboardAction(kba);
			return true;
		}
	}
	
	// 2. Mouse Events
	
	/**
	 * Internal use.
	 * <p>
	 * Utility function that gets the Scene.Button from this MouseEvent.
	 */
	protected Button getButton(MouseEvent e) {
		Button button = null;
		switch (e.getButton()) {
		case MouseEvent.NOBUTTON:
			break;
		case MouseEvent.BUTTON1: // left button
			button = Button.LEFT;
			break;
		case MouseEvent.BUTTON2: // middle button
			button = Button.MIDDLE;
			break;
		case MouseEvent.BUTTON3: // right button
			button = Button.RIGHT;
			break;
		}
		return button;
	}
	
	/**
	 * Mouse event handler.
	 * 
	 * @see remixlab.proscene.Scene#mouseIsHandled()
	 * @see remixlab.proscene.Scene#enableMouseHandling(boolean)
	 */
	public void mouseEvent(MouseEvent e) {
		scene.mouseX = e.getX();
		scene.mouseY = e.getY();
		if ((scene.currentCameraProfile() == null) || (!scene.mouseIsHandled()) )
			return;
		switch (e.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			awtMouseClicked(e);
			break;
		case MouseEvent.MOUSE_DRAGGED:
			awtMouseDragged(e);
			break;
		case MouseEvent.MOUSE_MOVED:
			awtMouseMoved(e);
			break;
		case MouseEvent.MOUSE_PRESSED:
			awtMousePressed(e);
			break;
		case MouseEvent.MOUSE_RELEASED:
			awtMouseReleased(e);
			break;
		}
	}
	
  /**
   * The action generated when the user clicks the mouse is handled by the
   * {@link remixlab.proscene.Scene#mouseGrabber()} (if any). Otherwise
   * looks in the {@link remixlab.proscene.Scene#currentCameraProfile()} to see if there's
   * a binding for this click event, taking into account the button, the modifier mask, and
   * the number of clicks.
   */
	protected void awtMouseClicked(MouseEvent event) {
		Button button = getButton(event);
		int numberOfClicks = event.getClickCount();
		if (scene.mouseGrabber() != null)
			scene.mouseGrabber().mouseClicked(/**event.getPoint(),*/ button, numberOfClicks, scene.pinhole());
		else {
			ClickAction ca = scene.currentCameraProfile().clickBinding(event.getModifiersEx(), button, numberOfClicks);
			if (ca != null)
				scene.handleClickAction(ca);
		}		
	}
	
	/**
	 * {@link remixlab.proscene.Scene#setMouseGrabber(MouseGrabbable)} to the MouseGrabber that grabs the
	 * mouse (or to {@code null} if none of them grab it).
	 */
	public void awtMouseMoved(MouseEvent e) {
		Point event = new Point((e.getX() - scene.upperLeftCorner.getX()), (e.getY() - scene.upperLeftCorner.getY()));
		scene.setMouseGrabber(null);
		if( scene.hasMouseTracking() )
			for (DeviceGrabbable mg : scene.mouseGrabberPool()) {
				mg.checkIfGrabsMouse(event.getX(), event.getY(), scene.pinhole());
				if (mg.grabsMouse())
					scene.setMouseGrabber(mg);
			}
	}
	
	/**
	 * The action generated when the user clicks and drags the mouse is handled by the
	 * {@link remixlab.proscene.Scene#mouseGrabber()} (if any), or the
	 * {@link remixlab.proscene.Scene#interactiveFrame()}
	 * (if @link remixlab.proscene.Scene#interactiveFrameIsDrawn()), or the
	 * {@link remixlab.proscene.Scene#pinhole()} (checks are performed in that order).
	 * <p>
	 * Mouse displacements are interpreted according to the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} mouse bindings.
	 * 
	 * @see #awtMouseDragged(MouseEvent)
	 * @see #awtMouseReleased(MouseEvent)
	 * @see #mouseWheelMoved(MouseWheelEvent)
	 */
	public void awtMousePressed(MouseEvent e) {
		Point event = new Point((e.getX() - scene.upperLeftCorner.getX()), (e.getY() - scene.upperLeftCorner.getY()));
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) { //covers also the case when mouseGrabberIsADrivableFrame
				InteractiveFrame iFrame = (InteractiveFrame) scene.mouseGrabber();
				iFrame.startAction(scene.currentCameraProfile().frameMouseAction(e), scene.drawIsConstrained());
				iFrame.mousePressed(new Point(event.getX(), event.getY()), scene.pinhole());
			} else
				scene.mouseGrabber().mousePressed(new Point(event.getX(), event.getY()), scene.pinhole());
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			scene.interactiveFrame().startAction(scene.currentCameraProfile().frameMouseAction(e), scene.drawIsConstrained());
			scene.interactiveFrame().mousePressed(new Point(event.getX(), event.getY()), scene.pinhole());
			return;
		}
		camMouseAction = scene.currentCameraProfile().cameraMouseAction(e);
		if (camMouseAction == MouseAction.ZOOM_ON_REGION) {
			fCorner.set(event.getX(), event.getY());
			lCorner.set(event.getX(), event.getY());
		}
		if (camMouseAction == MouseAction.SCREEN_ROTATE)
			fCorner.set(event.getX(), event.getY());
		scene.pinhole().frame().startAction(camMouseAction, scene.drawIsConstrained());
		scene.pinhole().frame().mousePressed(new Point(event.getX(), event.getY()), scene.pinhole());
	}
	
	/**
	 * The mouse dragged event is sent to the {@link remixlab.proscene.Scene#mouseGrabber()}
	 * or the {@link remixlab.proscene.Scene#interactiveFrame()}, or to the
	 * {@link remixlab.proscene.Scene#pinhole()}, according to the action started at
	 * {@link #awtMousePressed(MouseEvent)}.
	 * <p>
	 * Mouse displacements are interpreted according to the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} mouse bindings.
	 * 
	 * @see #awtMousePressed(MouseEvent)
	 * @see #awtMouseReleased(MouseEvent)
	 */
	public void awtMouseDragged(MouseEvent e) {
		Point event = new Point((e.getX() - scene.upperLeftCorner.getX()), (e.getY() - scene.upperLeftCorner.getY()));
		if (scene.mouseGrabber() != null) {
			scene.mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), scene.pinhole());
			if (scene.mouseGrabber().grabsMouse())
				if (scene.mouseGrabberIsAnIFrame) //covers also the case when mouseGrabberIsADrivableFrame
					((InteractiveFrame) scene.mouseGrabber()).mouseDragged(new Point(event.getX(), event.getY()), scene.pinhole());	
				else
					scene.mouseGrabber().mouseDragged(new Point(event.getX(), event.getY()), scene.pinhole());
			else
				scene.setMouseGrabber(null);
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
		  scene.interactiveFrame().mouseDragged(new Point(event.getX(), event.getY()), scene.pinhole());
			return;
		}
		if (camMouseAction == MouseAction.ZOOM_ON_REGION)
			lCorner.set(event.getX(), event.getY());
		else {
			if (camMouseAction == MouseAction.SCREEN_ROTATE)
				fCorner.set(event.getX(), event.getY());
			scene.pinhole().frame().mouseDragged(new Point(event.getX(), event.getY()), scene.pinhole());
		}
	}
	
	/**
	 * The mouse released event (which ends a mouse action) is sent to the
	 * {@link remixlab.proscene.Scene#mouseGrabber()} or the
	 * {@link remixlab.proscene.Scene#interactiveFrame()}, or to the
	 * {@link remixlab.proscene.Scene#pinhole()}, according to the action started at
	 * {@link #awtMousePressed(MouseEvent)}.
	 * <p>
	 * Mouse displacements are interpreted according to the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} mouse bindings.
	 * 
	 * @see #awtMousePressed(MouseEvent)
	 * @see #awtMouseDragged(MouseEvent)
	 */
	public void awtMouseReleased(MouseEvent e) {
		Point event = new Point((e.getX() - scene.upperLeftCorner.getX()), (e.getY() - scene.upperLeftCorner.getY()));
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) //covers also the case when mouseGrabberIsADrivableFrame
				((InteractiveFrame) scene.mouseGrabber()).mouseReleased(new Point(event.getX(), event.getY()), scene.pinhole());
			else
				scene.mouseGrabber().mouseReleased(new Point(event.getX(), event.getY()), scene.pinhole());
			scene.mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), scene.pinhole());
			if (!(scene.mouseGrabber().grabsMouse()))
				scene.setMouseGrabber(null);
			// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			scene.interactiveFrame().mouseReleased(new Point(event.getX(), event.getY()), scene.pinhole());
			// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return;
		}

		if ((camMouseAction == MouseAction.ZOOM_ON_REGION)
				|| (camMouseAction == MouseAction.SCREEN_ROTATE)
				|| (camMouseAction == MouseAction.SCREEN_TRANSLATE))
			lCorner.set(event.getX(), event.getY());
		scene.pinhole().frame().mouseReleased(new Point(event.getX(), event.getY()), scene.pinhole());
		camMouseAction = MouseAction.NO_MOUSE_ACTION;
		// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
	}
	
	// 2.b Wheel	
	
	/**
	 * The action generated when the user start rotating the mouse wheel is handled by the
	 * {@link remixlab.proscene.Scene#mouseGrabber()} (if any), or the
	 * {@link remixlab.proscene.Scene#interactiveFrame()}
	 * (if @link remixlab.proscene.Scene#interactiveFrameIsDrawn()), or the
	 * {@link remixlab.proscene.Scene#pinhole()} (checks are performed in that order).
	 * <p>
	 * Mouse wheel rotation is interpreted according to the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} mouse wheel bindings.
	 * 
	 * @see #awtMousePressed(MouseEvent)
	 */
	public void awtMouseWheelMoved(MouseWheelEvent event) {
		if(!scene.mouseIsHandled())
			return;
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) { //covers also the case when mouseGrabberIsADrivableFrame
				InteractiveFrame iFrame = (InteractiveFrame) scene.mouseGrabber();
				iFrame.startAction(scene.currentCameraProfile().frameWheelMouseAction(event), scene.drawIsConstrained());
				iFrame.mouseWheelMoved(event.getWheelRotation(), scene.pinhole());				
			} else
				scene.mouseGrabber().mouseWheelMoved(event.getWheelRotation(), scene.pinhole());
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			scene.interactiveFrame().startAction(scene.currentCameraProfile().frameWheelMouseAction(event), scene.drawIsConstrained());
			scene.interactiveFrame().mouseWheelMoved(event.getWheelRotation(), scene.pinhole());
			return;
		}
		scene.pinhole().frame().startAction(scene.currentCameraProfile().cameraWheelMouseAction(event), scene.drawIsConstrained());
		scene.pinhole().frame().mouseWheelMoved(event.getWheelRotation(), scene.pinhole());
	}
}

