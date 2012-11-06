/**
 *                     ProScene (version 1.1.94)      
 *    Copyright (c) 2010-2012 by National University of Colombia
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

import processing.core.PApplet;

import remixlab.proscene.Scene.Button;
import remixlab.proscene.Scene.CameraKeyboardAction;
import remixlab.proscene.Scene.ClickAction;
import remixlab.proscene.Scene.KeyboardAction;
import remixlab.proscene.Scene.MouseAction;

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
public class DesktopEvents implements MouseWheelListener {
	protected Scene scene;
	protected PApplet parent;
	protected MouseAction camMouseAction;
	protected boolean keyHandled;
  //Z O O M _ O N _ R E G I O N
	public Point fCorner;// also used for SCREEN_ROTATE
	public Point lCorner;
	
	public DesktopEvents(Scene s) {
		scene = s;
		parent = s.parent;
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
			keyTyped(e);
			break;
		case KeyEvent.KEY_RELEASED:
			keyReleased(e);
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
	protected void keyTyped(KeyEvent e) {
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
	protected void keyReleased(KeyEvent e) {
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
				scene.camera().playPath(path);
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
				scene.camera().addKeyFrameToPath(path);
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
				scene.camera().deletePath(path);
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
		if ((scene.currentCameraProfile() == null) || (!scene.mouseIsHandled()) )
			return;
		switch (e.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			mouseClicked(e);
			break;
		case MouseEvent.MOUSE_DRAGGED:
			mouseDragged(e);
			break;
		case MouseEvent.MOUSE_MOVED:
			mouseMoved(e);
			break;
		case MouseEvent.MOUSE_PRESSED:
			mousePressed(e);
			break;
		case MouseEvent.MOUSE_RELEASED:
			mouseReleased(e);
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
	protected void mouseClicked(MouseEvent event) {
		Button button = getButton(event);
		int numberOfClicks = event.getClickCount();
		if (scene.mouseGrabber() != null)
			scene.mouseGrabber().mouseClicked(/**event.getPoint(),*/ button, numberOfClicks, scene.camera());
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
	public void mouseMoved(MouseEvent e) {
		Point event = new Point((e.getX() - scene.upperLeftCorner.getX()), (e.getY() - scene.upperLeftCorner.getY()));
		scene.setMouseGrabber(null);
		if( scene.hasMouseTracking() )
			for (MouseGrabbable mg : scene.MouseGrabberPool) {
				mg.checkIfGrabsMouse(event.getX(), event.getY(), scene.camera());
				if (mg.grabsMouse())
					scene.setMouseGrabber(mg);
			}
		if ((scene.currentCameraProfile().mode() == CameraProfile.Mode.FIRST_PERSON) &&
				(getButton(e) ==  null) && 
				(scene.cursorIsHiddenOnFirstPerson()) ) {
			scene.camera().frame().startAction(Scene.MouseAction.LOOK_AROUND, scene.drawIsConstrained());
			scene.camera().frame().mouseDragged(event, scene.camera());
		}
	}
	
	/**
	 * The action generated when the user clicks and drags the mouse is handled by the
	 * {@link remixlab.proscene.Scene#mouseGrabber()} (if any), or the
	 * {@link remixlab.proscene.Scene#interactiveFrame()}
	 * (if @link remixlab.proscene.Scene#interactiveFrameIsDrawn()), or the
	 * {@link remixlab.proscene.Scene#camera()} (checks are performed in that order).
	 * <p>
	 * Mouse displacements are interpreted according to the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} mouse bindings.
	 * 
	 * @see #mouseDragged(MouseEvent)
	 * @see #mouseReleased(MouseEvent)
	 * @see #mouseWheelMoved(MouseWheelEvent)
	 */
	public void mousePressed(MouseEvent e) {
		Point event = new Point((e.getX() - scene.upperLeftCorner.getX()), (e.getY() - scene.upperLeftCorner.getY()));
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) { //covers also the case when mouseGrabberIsADrivableFrame
				InteractiveFrame iFrame = (InteractiveFrame) scene.mouseGrabber();
				iFrame.startAction(scene.currentCameraProfile().frameMouseAction(e), scene.drawIsConstrained());
				iFrame.mousePressed(new Point(event.getX(), event.getY()), scene.camera());
			} else
				scene.mouseGrabber().mousePressed(new Point(event.getX(), event.getY()), scene.camera());
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			scene.interactiveFrame().startAction(scene.currentCameraProfile().frameMouseAction(e), scene.drawIsConstrained());
			scene.interactiveFrame().mousePressed(new Point(event.getX(), event.getY()), scene.camera());
			return;
		}
		camMouseAction = scene.currentCameraProfile().cameraMouseAction(e);
		if (camMouseAction == MouseAction.ZOOM_ON_REGION) {
			fCorner.set(event.getX(), event.getY());
			lCorner.set(event.getX(), event.getY());
		}
		if (camMouseAction == MouseAction.SCREEN_ROTATE)
			fCorner.set(event.getX(), event.getY());
		scene.camera().frame().startAction(camMouseAction, scene.drawIsConstrained());
		scene.camera().frame().mousePressed(new Point(event.getX(), event.getY()), scene.camera());
	}
	
	/**
	 * The mouse dragged event is sent to the {@link remixlab.proscene.Scene#mouseGrabber()}
	 * or the {@link remixlab.proscene.Scene#interactiveFrame()}, or to the
	 * {@link remixlab.proscene.Scene#camera()}, according to the action started at
	 * {@link #mousePressed(MouseEvent)}.
	 * <p>
	 * Mouse displacements are interpreted according to the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} mouse bindings.
	 * 
	 * @see #mousePressed(MouseEvent)
	 * @see #mouseReleased(MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		Point event = new Point((e.getX() - scene.upperLeftCorner.getX()), (e.getY() - scene.upperLeftCorner.getY()));
		if (scene.mouseGrabber() != null) {
			scene.mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), scene.camera());
			if (scene.mouseGrabber().grabsMouse())
				if (scene.mouseGrabberIsAnIFrame) //covers also the case when mouseGrabberIsADrivableFrame
					((InteractiveFrame) scene.mouseGrabber()).mouseDragged(new Point(event.getX(), event.getY()), scene.camera());	
				else
					scene.mouseGrabber().mouseDragged(new Point(event.getX(), event.getY()), scene.camera());
			else
				scene.setMouseGrabber(null);
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
		  scene.interactiveFrame().mouseDragged(new Point(event.getX(), event.getY()), scene.camera());
			return;
		}
		if (camMouseAction == MouseAction.ZOOM_ON_REGION)
			lCorner.set(event.getX(), event.getY());
		else {
			if (camMouseAction == MouseAction.SCREEN_ROTATE)
				fCorner.set(event.getX(), event.getY());
			scene.camera().frame().mouseDragged(new Point(event.getX(), event.getY()), scene.camera());
		}
	}
	
	/**
	 * The mouse released event (which ends a mouse action) is sent to the
	 * {@link remixlab.proscene.Scene#mouseGrabber()} or the
	 * {@link remixlab.proscene.Scene#interactiveFrame()}, or to the
	 * {@link remixlab.proscene.Scene#camera()}, according to the action started at
	 * {@link #mousePressed(MouseEvent)}.
	 * <p>
	 * Mouse displacements are interpreted according to the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} mouse bindings.
	 * 
	 * @see #mousePressed(MouseEvent)
	 * @see #mouseDragged(MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		Point event = new Point((e.getX() - scene.upperLeftCorner.getX()), (e.getY() - scene.upperLeftCorner.getY()));
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) //covers also the case when mouseGrabberIsADrivableFrame
				((InteractiveFrame) scene.mouseGrabber()).mouseReleased(new Point(event.getX(), event.getY()), scene.camera());
			else
				scene.mouseGrabber().mouseReleased(new Point(event.getX(), event.getY()), scene.camera());
			scene.mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), scene.camera());
			if (!(scene.mouseGrabber().grabsMouse()))
				scene.setMouseGrabber(null);
			// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			scene.interactiveFrame().mouseReleased(new Point(event.getX(), event.getY()), scene.camera());
			// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return;
		}

		if ((camMouseAction == MouseAction.ZOOM_ON_REGION)
				|| (camMouseAction == MouseAction.SCREEN_ROTATE)
				|| (camMouseAction == MouseAction.SCREEN_TRANSLATE))
			lCorner.set(event.getX(), event.getY());
		scene.camera().frame().mouseReleased(new Point(event.getX(), event.getY()), scene.camera());
		camMouseAction = MouseAction.NO_MOUSE_ACTION;
		// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
	}
	
	// 2.b Wheel	
	
	/**
	 * The action generated when the user start rotating the mouse wheel is handled by the
	 * {@link remixlab.proscene.Scene#mouseGrabber()} (if any), or the
	 * {@link remixlab.proscene.Scene#interactiveFrame()}
	 * (if @link remixlab.proscene.Scene#interactiveFrameIsDrawn()), or the
	 * {@link remixlab.proscene.Scene#camera()} (checks are performed in that order).
	 * <p>
	 * Mouse wheel rotation is interpreted according to the
	 * {@link remixlab.proscene.Scene#currentCameraProfile()} mouse wheel bindings.
	 * 
	 * @see #mousePressed(MouseEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent event) {
		if(!scene.mouseIsHandled())
			return;
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) { //covers also the case when mouseGrabberIsADrivableFrame
				InteractiveFrame iFrame = (InteractiveFrame) scene.mouseGrabber();
				iFrame.startAction(scene.currentCameraProfile().frameWheelMouseAction(event), scene.drawIsConstrained());
				iFrame.mouseWheelMoved(event.getWheelRotation(), scene.camera());				
			} else
				scene.mouseGrabber().mouseWheelMoved(event.getWheelRotation(), scene.camera());
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			scene.interactiveFrame().startAction(scene.currentCameraProfile().frameWheelMouseAction(event), scene.drawIsConstrained());
			scene.interactiveFrame().mouseWheelMoved(event.getWheelRotation(), scene.camera());
			return;
		}
		scene.camera().frame().startAction(scene.currentCameraProfile().cameraWheelMouseAction(event), scene.drawIsConstrained());
		scene.camera().frame().mouseWheelMoved(event.getWheelRotation(), scene.camera());
	}
	
	// 3. Utility package dependent functions: java.awt wrappers that should be replaced
	//    by each platform where proscene is going to be implemented.

	/**
	 * Function that maps characters to virtual keys defined according to
	 * {@code java.awt.event.KeyEvent}.
	 */
	protected static int getVKey(char key) {
	  if(key == '0') return KeyEvent.VK_0;
	  if(key == '1') return KeyEvent.VK_1;
	  if(key == '2') return KeyEvent.VK_2;
	  if(key == '3') return KeyEvent.VK_3;
	  if(key == '4') return KeyEvent.VK_4;
	  if(key == '5') return KeyEvent.VK_5;
	  if(key == '6') return KeyEvent.VK_6;
	  if(key == '7') return KeyEvent.VK_7;
	  if(key == '8') return KeyEvent.VK_8;
	  if(key == '9') return KeyEvent.VK_9;		
	  if((key == 'a')||(key == 'A')) return KeyEvent.VK_A;
	  if((key == 'b')||(key == 'B')) return KeyEvent.VK_B;
	  if((key == 'c')||(key == 'C')) return KeyEvent.VK_C;
	  if((key == 'd')||(key == 'D')) return KeyEvent.VK_D;
	  if((key == 'e')||(key == 'E')) return KeyEvent.VK_E;
	  if((key == 'f')||(key == 'F')) return KeyEvent.VK_F;
	  if((key == 'g')||(key == 'G')) return KeyEvent.VK_G;
	  if((key == 'h')||(key == 'H')) return KeyEvent.VK_H;
	  if((key == 'i')||(key == 'I')) return KeyEvent.VK_I;
	  if((key == 'j')||(key == 'J')) return KeyEvent.VK_J;
	  if((key == 'k')||(key == 'K')) return KeyEvent.VK_K;
	  if((key == 'l')||(key == 'L')) return KeyEvent.VK_L;
	  if((key == 'm')||(key == 'M')) return KeyEvent.VK_M;
	  if((key == 'n')||(key == 'N')) return KeyEvent.VK_N;
	  if((key == 'o')||(key == 'O')) return KeyEvent.VK_O;
	  if((key == 'p')||(key == 'P')) return KeyEvent.VK_P;
	  if((key == 'q')||(key == 'Q')) return KeyEvent.VK_Q;
	  if((key == 'r')||(key == 'R')) return KeyEvent.VK_R;
	  if((key == 's')||(key == 'S')) return KeyEvent.VK_S;
	  if((key == 't')||(key == 'T')) return KeyEvent.VK_T;
	  if((key == 'u')||(key == 'U')) return KeyEvent.VK_U;
	  if((key == 'v')||(key == 'V')) return KeyEvent.VK_V;
	  if((key == 'w')||(key == 'W')) return KeyEvent.VK_W;
	  if((key == 'x')||(key == 'X')) return KeyEvent.VK_X;
	  if((key == 'y')||(key == 'Y')) return KeyEvent.VK_Y;
	  if((key == 'z')||(key == 'Z')) return KeyEvent.VK_Z;
	  return -1;
	}
	
	/**
	 * Wrapper function that simply returns
	 * {@code java.awt.event.KeyEvent.getKeyText(key)}.
	 */
	protected static String getKeyText(int key) {
		return KeyEvent.getKeyText(key);
	}
	
	/**
	 * Wrapper function that simply returns
	 * {@code java.awt.event.KeyEvent.getModifiersExText(mask)}.
	 */
	protected static String getModifiersExText(int mask) {
		return KeyEvent.getModifiersExText(mask);
	}
}
