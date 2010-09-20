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

public class ThirdPersonCameraProfile extends CameraProfile {
	public ThirdPersonCameraProfile(Scene scn, String n) {
		super(scn, n);

		setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.MOVE_FORWARD);
		setIFrameShortcut(Scene.Button.MIDDLE, Scene.MouseAction.LOOK_AROUND);
		setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.MOVE_BACKWARD);
		setIFrameShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT,
				Scene.MouseAction.ROLL);
		setIFrameShortcut(Scene.Button.RIGHT, Scene.Modifier.SHIFT,
				Scene.MouseAction.DRIVE);

		setShortcut('+', Scene.CameraKeyboardAction.INCREASE_AVATAR_FLY_SPEED);
		setShortcut('-', Scene.CameraKeyboardAction.DECREASE_AVATAR_FLY_SPEED);
		setShortcut('a', Scene.CameraKeyboardAction.INCREASE_AZYMUTH);
		setShortcut('A', Scene.CameraKeyboardAction.DECREASE_AZYMUTH);
		setShortcut('i', Scene.CameraKeyboardAction.INCREASE_INCLINATION);
		setShortcut('I', Scene.CameraKeyboardAction.DECREASE_INCLINATION);
		setShortcut('t', Scene.CameraKeyboardAction.INCREASE_TRACKING_DISTANCE);
		setShortcut('T', Scene.CameraKeyboardAction.DECREASE_TRACKING_DISTANCE);
	}
}
