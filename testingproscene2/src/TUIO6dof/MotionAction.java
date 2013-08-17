package TUIO6dof;

import remixlab.tersehandling.generic.profile.Actionable;

public enum MotionAction implements Actionable<GlobalAction> {
	CHANGE_POSITION(GlobalAction.CHANGE_POSITION), CHANGE_ROTATION(
			GlobalAction.CHANGE_ROTATION), CHANGE_SHAPE(
			GlobalAction.CHANGE_SHAPE);

	@Override
	public GlobalAction referenceAction() {
		return act;
	}

	@Override
	public String description() {
		return "A simple motion action";
	}

	public boolean is2D() {
		return false;
	}

	@Override
	public int dofs() {
		return 6;
	}

	GlobalAction act;

	MotionAction(GlobalAction a) {
		act = a;
	}
}
