package remixlab.proscene;

public class MouseBindings {
	public enum Modifier { ALT, SHIFT, CONTROL, ALT_GRAPH }
	public enum Button { RIGHT, MIDDLE, LEFT }
	
	public final class ButtonKeyCombination {
		public ButtonKeyCombination( Button myButton, Modifier myModifier ) {
			modifier = myModifier;			
			button = myButton;
		}
		public final Button button;
		public final Modifier modifier;
	}

	protected Scene scene;
	
	public MouseBindings(Scene scn) {
		scene = scn;
	}

}
