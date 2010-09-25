package remixlab.proscene;

import java.awt.Point;
import java.awt.event.*;

import processing.core.PApplet;

import remixlab.proscene.Scene.Button;
import remixlab.proscene.Scene.CameraKeyboardAction;
import remixlab.proscene.Scene.ClickAction;
import remixlab.proscene.Scene.KeyboardAction;
import remixlab.proscene.Scene.MouseAction;

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
	}
	
	// 1. KeyEvents
	
	/**
	 * Method interface between proscene and processing to handle the keyboard.
	 * 
	 * @see remixlab.proscene.Scene#keyboardIsHandled()
	 * @see remixlab.proscene.Scene#enableKeyboardHandling(boolean)
	 */
	public void keyEvent(KeyEvent e) {
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
	
	protected void keyTyped(KeyEvent e) {
		boolean handled = false;		
		if (scene.currentCameraProfile() != null)
			handled = handleKeyTypedCameraKeyboardAction(e);
		if (!handled)
			handled = handleKeyTypedKeyboardAction(e);
		keyHandled = handled;
	}
	
	/**
	 * Associates the different interactions to the keys.
	 */
	protected void keyReleased(KeyEvent e) {
		if(keyHandled)
			return;
		boolean handled = false;
		if (scene.currentCameraProfile() != null)
			handled = handleKeyReleasedCameraKeyboardAction(e);
		if (!handled)
			handleKeyReleasedKeyboardAction(e);
	}
	
	protected boolean handleKeyTypedCameraKeyboardAction(KeyEvent e) {
		CameraKeyboardAction kba = null;
		kba = scene.currentCameraProfile().shortcut( e.getKeyChar() );
		if (kba == null)
			return false;
		else {
			scene.handleCameraKeyboardAction(kba);
			return true;
		}
	}
	
	protected boolean handleKeyTypedKeyboardAction( KeyEvent e) {
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
	
	protected boolean handleKeyReleasedCameraKeyboardAction(KeyEvent e) {
		CameraKeyboardAction kba = null;
		kba = scene.currentCameraProfile().shortcut( e.getModifiersEx(), e.getKeyCode() );
		if (kba == null)
			return false;
		else {
			scene.handleCameraKeyboardAction(kba);
			return true;
		}
	}
	
	protected boolean handleKeyReleasedKeyboardAction( KeyEvent e) {
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
	 * Method interface between proscene and processing to handle the mouse.
	 * 
	 * @see remixlab.proscene.Scene#mouseIsHandled()
	 * @see remixlab.proscene.Scene#enableMouseHandling(boolean)
	 */
	public void mouseEvent(MouseEvent e) {
		if (scene.currentCameraProfile() == null)
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
	
  //click event
	protected void mouseClicked(MouseEvent e) {
		 /**
		String text = InputEvent.getModifiersExText(e.getModifiersEx());		
		PApplet.println("modifiers in mouse click: " + text);
		PApplet.println("number of clicks: " + e.getClickCount());
		// */
		
		//1. get button
		Button button = getButton(e);
		ClickAction ca = scene.currentCameraProfile().clickShortcut(e.getModifiersEx(), button, e.getClickCount());		
		if (ca == null) 
			return;
		else
			scene.handleClickAction(ca);
	}
	
	/**
	 * Sets the Camera from processing camera parameters.
	 * <p>
	 * {@link remixlab.proscene.Scene#setMouseGrabber(MouseGrabber)} to the MouseGrabber that grabs the
	 * mouse (or to {@code null} if none of them grab it).
	 */
	public void mouseMoved(MouseEvent event) {		
		scene.setMouseGrabber(null);
		for (MouseGrabber mg : scene.MouseGrabberPool) {
			mg.checkIfGrabsMouse(event.getX(), event.getY(), scene.camera());
			if (mg.grabsMouse())
				scene.setMouseGrabber(mg);
		}
	}
	
	/**
	 * When the user clicks on the mouse: If a {@link remixlab.proscene.Scene#mouseGrabber()} is defined,
	 * {@link remixlab.proscene.MouseGrabber#mousePressed(Point, Camera)} is
	 * called. Otherwise, the {@link remixlab.proscene.Scene#camera()} or the {@link remixlab.proscene.Scene#interactiveFrame()}
	 * interprets the mouse displacements, depending on mouse bindings.
	 * 
	 * @see #mouseDragged(MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {
	  /**
		String text = InputEvent.getModifiersExText(event.getModifiersEx());		
		PApplet.println("modifiers in mouse press: " + text);
		// */
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) { //covers also the case when mouseGrabberIsADrivableFrame
				InteractiveFrame iFrame = (InteractiveFrame) scene.mouseGrabber();
				iFrame.startAction(scene.currentCameraProfile().iFrameMouseAction(event), scene.drawIsConstrained());
				iFrame.mousePressed(event.getPoint(), scene.camera());
			} else
				scene.mouseGrabber().mousePressed(event.getPoint(), scene.camera());
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			scene.interactiveFrame().startAction(scene.currentCameraProfile().iFrameMouseAction(event), scene.drawIsConstrained());
			scene.interactiveFrame().mousePressed(event.getPoint(), scene.camera());
			return;
		}
		camMouseAction = scene.currentCameraProfile().cameraMouseAction(event);// updates camMouseAction
		if (camMouseAction == MouseAction.ZOOM_ON_REGION) {
			fCorner = event.getPoint();
			lCorner = event.getPoint();
		}
		if (camMouseAction == MouseAction.SCREEN_ROTATE)
			fCorner = event.getPoint();
		scene.camera().frame().startAction(camMouseAction, scene.drawIsConstrained());
		scene.camera().frame().mousePressed(event.getPoint(), scene.camera());
	}
	
	/**
	 * Mouse drag event is sent to the {@link remixlab.proscene.Scene#mouseGrabber()} (if any) or to the
	 * {@link remixlab.proscene.Scene#camera()} or the {@link remixlab.proscene.Scene#interactiveFrame()}, depending on mouse
	 * bindings.
	 * 
	 * @see #mouseMoved(MouseEvent)
	 */
	public void mouseDragged(MouseEvent event) {
		if (scene.mouseGrabber() != null) {
			scene.mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), scene.camera());
			if (scene.mouseGrabber().grabsMouse())
				// TODO: the following case needs testing
				if (scene.mouseGrabberIsADrivableFrame)
					((InteractiveDrivableFrame) scene.mouseGrabber()).iDrivableMouseDragged(event.getPoint(), scene.camera());
				else
					scene.mouseGrabber().mouseDragged(event.getPoint(), scene.camera());
			else
				scene.setMouseGrabber(null);
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			// TODO: the following case needs testing
			if (scene.interactiveFrameIsDrivable)
				((InteractiveDrivableFrame)scene.interactiveFrame()).iDrivableMouseDragged(event.getPoint(), scene.camera());
			else
				scene.interactiveFrame().mouseDragged(event.getPoint(), scene.camera());
			return;
		}
		if (camMouseAction == MouseAction.ZOOM_ON_REGION)
			lCorner = event.getPoint();
		else {
			if (camMouseAction == MouseAction.SCREEN_ROTATE)
				fCorner = event.getPoint();
			scene.camera().frame().mouseDragged(event.getPoint(), scene.camera());
		}
	}
	
	/**
	 * Calls the {@link remixlab.proscene.Scene#mouseGrabber()}, {@link remixlab.proscene.Scene#camera()} or
	 * {@link remixlab.proscene.Scene#interactiveFrame()} mouseReleaseEvent method.
	 */
	public void mouseReleased(MouseEvent event) {
		if (scene.mouseGrabber() != null) {
			// TODO: the following case needs testing
			if (scene.mouseGrabberIsADrivableFrame)
				((InteractiveDrivableFrame) scene.mouseGrabber()).iDrivableMouseReleased(event.getPoint(), scene.camera());
			else
				scene.mouseGrabber().mouseReleased(event.getPoint(), scene.camera());
			scene.mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), scene.camera());
			if (!(scene.mouseGrabber().grabsMouse()))
				scene.setMouseGrabber(null);
			// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			// TODO: the following case needs testing
			if (scene.interactiveFrameIsDrivable)
				((InteractiveDrivableFrame)scene.interactiveFrame()).iDrivableMouseReleased(event.getPoint(), scene.camera());
			else
				scene.interactiveFrame().mouseReleased(event.getPoint(), scene.camera());
			// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return;
		}

		if ((camMouseAction == MouseAction.ZOOM_ON_REGION)
				|| (camMouseAction == MouseAction.SCREEN_ROTATE)
				|| (camMouseAction == MouseAction.SCREEN_TRANSLATE))
			lCorner = event.getPoint();
		scene.camera().frame().mouseReleased(event.getPoint(), scene.camera());
		camMouseAction = MouseAction.NO_MOUSE_ACTION;
		// iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
	}
	
	// 2.b Wheel
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) { //covers also the case when mouseGrabberIsADrivableFrame
				if (scene.mouseGrabberIsADrivableFrame) {	
					InteractiveDrivableFrame iFrame = (InteractiveDrivableFrame) scene.mouseGrabber();
					iFrame.startAction(scene.currentCameraProfile().frameWheelMouseAction(event), scene.drawIsConstrained());
					iFrame.iDrivableMouseWheelMoved(event.getWheelRotation(), scene.camera());
				}
				else {
					InteractiveFrame iFrame = (InteractiveFrame) scene.mouseGrabber();
					iFrame.startAction(scene.currentCameraProfile().frameWheelMouseAction(event), scene.drawIsConstrained());
					iFrame.mouseWheelMoved(event.getWheelRotation(), scene.camera());
				}
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
}
