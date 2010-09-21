/**
 *                     ProScene (version 1.0.0-alpha1)      
 *             Copyright (c) 2010 by RemixLab, DISI-UNAL      
 *            http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
 * 
 * @author Jean Pierre Charalambos
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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import remixlab.proscene.Scene.MouseAction;

public class WheeledCameraProfile extends CameraProfile implements
		MouseWheelListener {

	public final Integer WHEEL = 1;
	protected ShortcutMappings<Shortcut<Integer>, Scene.MouseAction> cameraWheelActions;
	protected ShortcutMappings<Shortcut<Integer>, Scene.MouseAction> frameWheelActions;
	
	public WheeledCameraProfile(Scene scn, String n) {
		super(scn, n, Mode.ARCBALL);
		scene.parent.addMouseWheelListener(this);
		cameraWheelActions = new ShortcutMappings<Shortcut<Integer>, Scene.MouseAction>(scene);
		frameWheelActions = new ShortcutMappings<Shortcut<Integer>, Scene.MouseAction>(scene);
		setCameraWheelShortcut( MouseAction.ZOOM );
		setCameraWheelShortcut( Scene.Modifier.CONTROL, MouseAction.MOVE_FORWARD );
		setCameraWheelShortcut( Scene.Modifier.ALT, MouseAction.MOVE_BACKWARD );
		setFrameWheelShortcut( MouseAction.ZOOM );
		setFrameWheelShortcut( Scene.Modifier.CONTROL, MouseAction.MOVE_FORWARD );
		setFrameWheelShortcut( Scene.Modifier.ALT, MouseAction.MOVE_BACKWARD );
	}

	//public void mouseWheelMoved(MouseWheelEvent event) {
		//scene.mouseWheelMoved(event.getWheelRotation());
	//}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		if (scene.mouseGrabber() != null) {
			if (scene.mouseGrabberIsAnIFrame) { //covers also the case when mouseGrabberIsADrivableFrame
				if (scene.mouseGrabberIsADrivableFrame) {	
					InteractiveDrivableFrame iFrame = (InteractiveDrivableFrame) scene.mouseGrabber();
					iFrame.startAction(frameWheelMouseAction(event), scene.drawIsConstrained());
					iFrame.iDrivableMouseWheelMoved(event.getWheelRotation(), scene.camera());
				}
				else {
					InteractiveFrame iFrame = (InteractiveFrame) scene.mouseGrabber();
					iFrame.startAction(frameWheelMouseAction(event), scene.drawIsConstrained());
					iFrame.mouseWheelMoved(event.getWheelRotation(), scene.camera());
				}
			} else
				scene.mouseGrabber().mouseWheelMoved(event.getWheelRotation(), scene.camera());
			return;
		}
		if (scene.interactiveFrameIsDrawn()) {
			scene.interactiveFrame().startAction(frameWheelMouseAction(event), scene.drawIsConstrained());
			scene.interactiveFrame().mouseWheelMoved(event.getWheelRotation(), scene.camera());
			return;
		}
		scene.camera().frame().startAction(cameraWheelMouseAction(event), scene.drawIsConstrained());
		scene.camera().frame().mouseWheelMoved(event.getWheelRotation(), scene.camera());
	}
	
	//Camera Wheel
	public void removeAllCameraWheelShortcuts() {
		cameraWheelActions.removeAllMappings();
	}

	public boolean isCameraWheelKeyInUse(Shortcut<Integer> key) {
		return cameraWheelActions.isShortcutInUse(key);
	}

	public boolean isWheelActionBindedToCamera(Scene.MouseAction action) {
		return cameraWheelActions.isActionMapped(action);
	}

	public void setCameraWheelShortcut(Scene.Modifier modifier,	Scene.MouseAction action) {
		cameraWheelActions.setMapping(new Shortcut<Integer>(WHEEL, modifier),
				action);
	}

	public void setCameraWheelShortcut(Scene.MouseAction action) {
		cameraWheelActions.setMapping(new Shortcut<Integer>(WHEEL), action);
	}

	public void removeCameraWheelShortcut(Scene.Modifier modifier) {
		cameraWheelActions.removeMapping(new Shortcut<Integer>(WHEEL, modifier));
	}

	public void removeCameraWheelShortcut() {
		cameraWheelActions.removeMapping(new Shortcut<Integer>(WHEEL));
	}

	public Scene.MouseAction cameraWheelShortcut(Scene.Modifier modifier) {
		return cameraWheelActions.mapping(new Shortcut<Integer>(WHEEL, modifier));
	}

	public Scene.MouseAction cameraWheelShortcut() {
		return cameraWheelActions.mapping(new Shortcut<Integer>(WHEEL));
	}
	
	public MouseAction cameraWheelMouseAction(MouseWheelEvent e) {
		MouseAction wMouseAction = MouseAction.NO_MOUSE_ACTION;

		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()
				|| e.isShiftDown()) {
			if (e.isAltDown())
				wMouseAction = cameraWheelShortcut(Scene.Modifier.ALT);
			if (e.isAltGraphDown())
				wMouseAction = cameraWheelShortcut(Scene.Modifier.ALT_GRAPH);
			if (e.isControlDown())
				wMouseAction = cameraWheelShortcut(Scene.Modifier.CONTROL);
			if (e.isShiftDown())
				wMouseAction = cameraWheelShortcut(Scene.Modifier.SHIFT);
			if (wMouseAction != null)
				return wMouseAction;
		}

		wMouseAction = cameraWheelShortcut();

		if (wMouseAction == null)
			wMouseAction = MouseAction.NO_MOUSE_ACTION;

		return wMouseAction;
	}
	
  //Frame Wheel
	public void removeAllFrameWheelShortcuts() {
		frameWheelActions.removeAllMappings();
	}

	public boolean isFrameWheelKeyInUse(Shortcut<Integer> key) {
		return frameWheelActions.isShortcutInUse(key);
	}

	public boolean isWheelActionBindedToFrame(Scene.MouseAction action) {
		return frameWheelActions.isActionMapped(action);
	}

	public void setFrameWheelShortcut(Scene.Modifier modifier,	Scene.MouseAction action) {
		frameWheelActions.setMapping(new Shortcut<Integer>(WHEEL, modifier),
				action);
	}

	public void setFrameWheelShortcut(Scene.MouseAction action) {
		frameWheelActions.setMapping(new Shortcut<Integer>(WHEEL), action);
	}

	public void removeFrameWheelShortcut(Scene.Modifier modifier) {
		frameWheelActions.removeMapping(new Shortcut<Integer>(WHEEL, modifier));
	}

	public void removeFrameWheelShortcut() {
		frameWheelActions.removeMapping(new Shortcut<Integer>(WHEEL));
	}

	public Scene.MouseAction frameWheelShortcut(Scene.Modifier modifier) {
		return frameWheelActions.mapping(new Shortcut<Integer>(WHEEL, modifier));
	}

	public Scene.MouseAction frameWheelShortcut() {
		return frameWheelActions.mapping(new Shortcut<Integer>(WHEEL));
	}
	
	public MouseAction frameWheelMouseAction(MouseWheelEvent e) {
		MouseAction fMouseAction = MouseAction.NO_MOUSE_ACTION;

		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()
				|| e.isShiftDown()) {
			if (e.isAltDown())
				fMouseAction = frameWheelShortcut(Scene.Modifier.ALT);
			if (e.isAltGraphDown())
				fMouseAction = frameWheelShortcut(Scene.Modifier.ALT_GRAPH);
			if (e.isControlDown())
				fMouseAction = frameWheelShortcut(Scene.Modifier.CONTROL);
			if (e.isShiftDown())
				fMouseAction = frameWheelShortcut(Scene.Modifier.SHIFT);
			if (fMouseAction != null)
				return fMouseAction;
		}

		fMouseAction = frameWheelShortcut();

		if (fMouseAction == null)
			fMouseAction = MouseAction.NO_MOUSE_ACTION;

		return fMouseAction;
	}
}