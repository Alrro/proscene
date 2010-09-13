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

public class WheeledArcballCameraProfile extends ArcballCameraProfile implements MouseWheelListener {

	public WheeledArcballCameraProfile(Scene scn, String n) {
		super(scn, n);
		scene.parent.addMouseWheelListener(this);
	}
	
	/**
	 * Calls the {@link #mouseGrabber()}, {@link #camera()} or
	 * {@link #interactiveFrame()} mouseWheelEvent method.
	 */
	public void mouseWheelMoved(MouseWheelEvent event) {
		if ( scene.mouseGrabber() != null ) {
			if ( scene.mouseGrabberIsAnIFrame ) {
				InteractiveFrame iFrame = (InteractiveFrame)scene.mouseGrabber();
				if ( scene.mouseGrabberIsAnICamFrame ) {
					//TODO: implement me
				}
				else {
					//iFrame.startAction(MouseAction.ZOOM, withConstraint);
					iFrame.startAction(MouseAction.ZOOM);//TODO pend
					iFrame.mouseWheelMoved(event.getWheelRotation(), scene.camera());
				}
			}
			else
				scene.mouseGrabber().mouseWheelMoved(event.getWheelRotation(), scene.camera());
			//test
			/**
			mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY() , camera());
    		if (!(mouseGrabber().grabsMouse()))
    			setMouseGrabber(null);
    		*/
    		//end test
		}
		else if ( scene.interactiveFrameIsDrawn() ) {
			//scene.interactiveFrame().startAction(MouseAction.ZOOM, withConstraint);
			scene.interactiveFrame().startAction(MouseAction.ZOOM);//TODO pend
			scene.interactiveFrame().mouseWheelMoved(event.getWheelRotation(), scene.camera());
		}
		else {
			//scene.camera().frame().startAction(MouseAction.ZOOM, withConstraint);
			scene.camera().frame().startAction(MouseAction.ZOOM);//TODO pend
			scene.camera().frame().mouseWheelMoved(event.getWheelRotation(), scene.camera());			
		}
	}

}
