

import processing.core.*;

/**
*
* @author nakednous
*/

/**
 * We need this class to share the drawing commands among
 * some PApplet instances
 */
public class DrawingCommands {
	public static void cmmnds(PApplet p) {
		 BoxNode.Root.drawIfAllChildrenAreVisible(p, Cam1.cullingCamera);
	}
}
