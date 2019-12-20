package hangman;

import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HangmanDialog extends JDialog {

  /* User entry field for dialog */
  private JTextField field;

  /**
   * Create a new dialog with a given title and message.
   * @param mainFrame dialog parent
   * @param title dialog title
   * @param message dialog message
   */
  HangmanDialog(JFrame mainFrame, String title, String message) {
    super(mainFrame, title, true);

    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    top.add(new JLabel(message));

    field = new JTextField(40);
    JPanel middle = new JPanel();
    middle.add(field);

    JButton okButton = new JButton("OK");
    okButton.addActionListener(event -> checkStatus());
    JPanel bottom = new JPanel();
    bottom.add(okButton);

    add(top);
    add(middle);
    add(bottom);

    pack();
    setVisible(true);
  } // WheelDialog()

  /**
   * Retrieve user entry field text.
   * @return  user entry field text
   */
  public String getEntryText() {
    return field.getText();
  } // getEntryText()

  /* Determine if dialog has valid entry */
  private void checkStatus() {
    if (!field.getText().isEmpty()) {
      setVisible(false);
    }
  } // checkStatus()

} // HangmanDialog
