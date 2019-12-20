package hangman;

import javax.swing.ImageIcon;

public class BodyPart {
  /* Image icon of the Wheel Space */
  private ImageIcon imageIcon;

  /**
   * Construct a Body Part with given inputs.
   * @param imageIcon image of the Wheel Space
   */
  public BodyPart(ImageIcon imageIcon) {
    this.imageIcon = imageIcon;
  } // WheelSpace()

  /**
   * Retrieve WheelSpace image icon.
   * @return WheelSpace image icon
   */
  public ImageIcon getImageIcon() {
    return imageIcon;
  } // getImageIcon()


} // BodyPart
