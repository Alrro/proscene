public class ClickButton extends Button2D {
  boolean addBox;

  public ClickButton(Scene scn, PVector p, String t, boolean addB) {
    super(scn, p, t);
    addBox = addB;
  }

  void mouseClicked(Integer button, int numberOfClicks, Camera camera) {
    if(numberOfClicks == 1) {
      if(addBox)
        addBox();
      else
        removeBox();
    }
  }
}
