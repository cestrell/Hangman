package hangman;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class HangmanFrame extends JFrame {

  /** Number of body parts in the game. */
  public static final int NUM_PARTS = 6;

  /** Path to images folder. */
  public static final String IMAGES_PATH = "hangman/images";

  /** File extension for images. */
  public static final String IMAGE_EXTENSION = "jpg";

  /* Seeded random number generator */
  private Random generator;

  /* Clear-text puzzle representation */
  private String puzzle;

  /* Obfuscated puzzle representation */
  private char[] hidden;

  /* Game logic */
  private GameData data = new GameData();

  /* Button to buy vowels */
  private JButton buyButton;

  /* Button to spin wheel */
  private JButton spinButton;

  /* Button to solve puzzle */
  private JButton solveButton;

  /* Panel that holds all buttons */
  private JPanel buttonPanel;

  /* Panel that holds wheel */
  private JPanel wheelPanel;

  /* Panel that holds all vowels */
  private JPanel vowelPanel;

  /* Panel that holds all consonants */
  private JPanel consPanel;

  /* Panel that holds hidden puzzle */
  private JPanel puzzlePanel;

  /* All possible wheel spaces */
  private static final BodyPart[] IMAGES = loadImages();

  /* All possible vowels */
  private static final String[] VOWELS = { "A", "E", "I", "O", "U" };

  /* All possible consonants */
  private static final String[] CONS = { "B", "C", "D", "F", "G", "H", "J",
    "K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "X", "Y", "Z" };


  /**
   * Loads images from the images/ directory.
   *
   * Assumes that there are exactly NUM_WHEEL_SPACES images
   * numbered from 1 to NUM_WHEEL_SPACES.
   *
   * @return  array of BodyPart objects representing the images
   */
  private static BodyPart[] loadImages() {
    File[] fileList;
    File myDir = null;

    // Allocate array for number of spaces, which is set to a constant
    BodyPart[] bodyParts = new BodyPart[NUM_PARTS];

    // Get a File object for the directory containing the images
    try {
      myDir = new File(Objects.requireNonNull(HangmanFrame.class
              .getClassLoader().getResource(IMAGES_PATH)).toURI());
    } catch (URISyntaxException uriException) {
      System.out.println("Caught a URI syntax exception");
      System.exit(4); // Exit for simplicity
    }

    for (int i = 0; i < NUM_PARTS; i++) {
      fileList = myDir.listFiles(new BodyPartImageFilter(i));
      assert fileList != null;
      if (fileList.length == 1) {
        bodyParts[i] = new BodyPart(new ImageIcon(fileList[0].toString()));
      } else {
        System.out.println("ERROR: Invalid number of images for space: " + i);
        System.out.println("       Expected 1, but found " + fileList.length);
      }
    } // for

    return bodyParts;
  } // loadImages()

  // Helper nested class to filter images used for body parts.
  // Based on specifically expected filename format.
  private static class BodyPartImageFilter implements FileFilter {
    /** Prefix of the requested filename. */
    private String prefix;  // The prefix of the filename

    /**
     * Constructs a filter with the given prefix.
     *
     * @param prefixIn  integer corresponding to the prefix
     */
    BodyPartImageFilter(int prefixIn) {
      // Sets the prefix member to string version of space number
      prefix = Integer.toString(prefixIn);
    } // BodyPartImageFilter()

    /**
     * Tests whether the file provided should be accepted by our file
     * filter. In the FileFilter interface.
     */
    @Override
    public boolean accept(File imageFile) {
      boolean isAccepted = false;

      // Accepted if matched "<...>.jpg" where
      // IMAGE_EXTENSION is assumed to be "jpg" for this example
      if (imageFile.getName().startsWith(prefix + "_") &&
          imageFile.getName().endsWith("." + IMAGE_EXTENSION)) {
        isAccepted = true;
      }

      return isAccepted;
    } // accept()

  } // BodyPartImageFilter

  /**
   * Create and start a game of Hangman.
   */
  public HangmanFrame() {
    super("Hangman");
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

    // Creation prompts
    numPlayerPrompt();
    namePlayerPrompt();
    puzzlePrompt();

    // Panels
    add(makeTopSection(), BorderLayout.NORTH);
    add(makeCenterSection(), BorderLayout.CENTER);
    add(makeBottomSection(), BorderLayout.SOUTH);
  } // WheelOfFortuneFrame()

  /* Ask for the number of players in the game */
  private void numPlayerPrompt() {
    String title = "Number of Players Input";
    String message = "Enter number of players (must be at least 1)";
    boolean validEntry = false;

    while (!validEntry) {
      HangmanDialog numPlayersDialog = new HangmanDialog(this, title, message);
      try {
        int num = Integer.parseInt(numPlayersDialog.getEntryText());
        if (num < 1) {
          throw new Exception();
        }
        data.setNumPlayers(num);
        validEntry = true;

      } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Input must be a positive integer", "Input Error",
            JOptionPane.ERROR_MESSAGE);
      } // try catch
    } // while
  } // numPlayerPrompt()

  /* Ask for the name of the players in the game */
  private void namePlayerPrompt() {
    String title = "Player Name Input";
    String message = "Enter name of player #";

    for (int p = 0; p < data.getNumPlayers(); ++p) {
      HangmanDialog namePlayersDialog = new HangmanDialog(this, title, message + p);

      String name = namePlayersDialog.getEntryText();
      if (!name.isEmpty()) {
        data.addNewPlayer(name);
      }
    } // for
  } // namePlayerPrompt()

  /* Ask for the game puzzle */
  private void puzzlePrompt() {
    String title = "Puzzle Input";
    String message = "Ask a non-player to enter a puzzle";
    HangmanDialog puzzleDialog = new HangmanDialog(this, title, message);

    String entry = puzzleDialog.getEntryText();
    if (!entry.isEmpty()) {
      puzzle = entry.toUpperCase();
      hidden = puzzle.replaceAll("[a-zA-Z]", "-").toCharArray();
    }
  } // puzzlePrompt()

  /* Prompt user for puzzle guess */
  private void solvePrompt() {
    String title = "Solve Puzzle";
    String message = "Enter complete puzzle exactly as displayed";

    HangmanDialog solveDialog =
        new HangmanDialog(this, title, message);

    String guess = solveDialog.getEntryText();
    String info;
    if (guess.toUpperCase().equals(puzzle)) {
      info = data.currentName() + " wins $" + data.currentBalance();

      JOptionPane.showMessageDialog(this, info,
          "Game Over", JOptionPane.INFORMATION_MESSAGE);
      dispose();
    } else {
      info = "Guess by " + data.currentName() + " was incorrect!";
      JOptionPane.showMessageDialog(this, info,
          "Wrong Answer", JOptionPane.ERROR_MESSAGE);
      data.loseATurn();
      enableButtons();
    }
  } // solvePrompt()

  /* Create top section that contains all player panels */
  private JPanel makeTopSection() {
    JPanel topSection = new JPanel(new GridLayout(1, data.getNumPlayers()));
    data.makeNamePanels();
    for (int p = 0; p < data.getNumPlayers(); ++p) {
      topSection.add(data.getNamePanel(p));
    }
    return topSection;
  } // makeTopSection()

  /* Create center section that contains buttons and wheel panel */
  private JPanel makeCenterSection() {
    JPanel centerSection = new JPanel(new FlowLayout());
    makeButtonsPanel();
    makeWheelPanel();
    centerSection.add(buttonPanel);
    centerSection.add(wheelPanel);
    return centerSection;
  } // makeCenterSection()

  /* Create panel with game buttons */
  private void makeButtonsPanel() {
    buttonPanel = new JPanel(new GridLayout(3, 1, 0, 25));

    buyButton = new JButton("Buy a Vowel");
    buyButton.addActionListener(actionEvent -> buyVowel());
    buyButton.setEnabled(false);
    buttonPanel.add(buyButton);

    spinButton = new JButton("Spin the Wheel");
    //buyButton.addActionListener(actionEvent -> );
    buttonPanel.add(spinButton);

    solveButton = new JButton("Solve the Puzzle");
    solveButton.addActionListener(actionEvent -> solvePuzzle());
    buttonPanel.add(solveButton);

    ButtonGroup gameButtons = new ButtonGroup();
    gameButtons.add(buyButton);
    gameButtons.add(spinButton);
    gameButtons.add(solveButton);

  } // makeButtonsPanel()

  /* Create panel with wheel */
  private void makeWheelPanel() {
    wheelPanel = new JPanel(new FlowLayout());
    wheelPanel.add(new JLabel(IMAGES[0].getImageIcon()));
  } // makeWheelPanel()

  /* Create bottom section that contains puzzle panel */
  private JPanel makeBottomSection() {
    JPanel bottomSection = new JPanel(new BorderLayout());
    makePuzzlePanel();
    bottomSection.add(makeLetterPanel(), BorderLayout.NORTH);
    bottomSection.add(puzzlePanel, BorderLayout.SOUTH);
    return bottomSection;
  } // makeBottomSection()

  /* Create panel with letter selection */
  private JPanel makeLetterPanel() {
    JPanel letterPanel = new JPanel(new FlowLayout());
    makeVowelPanel();
    makeConsPanel();
    letterPanel.add(vowelPanel);
    letterPanel.add(consPanel);
    return letterPanel;
  } // makeLetterPanel()

  /* Create panel with vowel selection */
  private void makeVowelPanel() {
    vowelPanel = new JPanel(new GridLayout(3, 2));

    TitledBorder title = BorderFactory.createTitledBorder("Vowels");
    title.setTitleJustification(TitledBorder.LEFT);
    vowelPanel.setBorder(title);

    for (String v : VOWELS) {
      JButton vowel = new JButton(v);
      vowel.addActionListener(actionEvent -> vowelPressed(v));
      vowelPanel.add(vowel);
    } // for
    disableVowels();
  } // makeVowelPanel()

  /* Create panel with consonant selection */
  private void makeConsPanel() {
    consPanel = new JPanel(new GridLayout(3, 7));

    TitledBorder title = BorderFactory.createTitledBorder("Consonants");
    title.setTitleJustification(TitledBorder.LEFT);
    consPanel.setBorder(title);

    for (String c : CONS) {
      JButton cons = new JButton(c);
      cons.addActionListener(actionEvent -> consPressed(c));
      consPanel.add(cons);
    } // for
    disableCons();
  } // makeVowelPanel()

  /* Create panel with puzzle to be solved */
  private void makePuzzlePanel() {
    puzzlePanel = new JPanel(new FlowLayout());
    String label = createHiddenPuzzle();
    puzzlePanel.add(new JLabel(label));
  } // makePuzzlePanel()

  /* Functionality for buy button */
  private void buyVowel() {
    enableVowels();
    data.buyVowel();
  } // buyVowel()


  /* Functionality for solve button */
  private void solvePuzzle() {
    disableButtons();
    solvePrompt();
  } // solvePuzzle()

  /* Simulate spinning of wheel and display next icon */
  private void updateWheel(int index) {
    wheelPanel.removeAll();
    wheelPanel.add(new JLabel(IMAGES[index].getImageIcon()));
    wheelPanel.revalidate();
  } // updateWheel();

  /*  */
  private void enableVowels() {
    disableButtons();

    for (Component component : vowelPanel.getComponents()) {
      JButton label = (JButton) component;
      if (!data.isGuessedVowel(label.getText())) {
        component.setEnabled(true);
      }
    } // for
  } // enableVowels()

  /*  */
  private void enableCons() {
    disableButtons();

    for (Component component : consPanel.getComponents()) {
      JButton label = (JButton) component;
      if (!data.isGuessedCons(label.getText())) {
        component.setEnabled(true);
      }
    } // for
  } // enableCons()

  /*  */
  private void disableVowels() {
    for (Component component : vowelPanel.getComponents()) {
      component.setEnabled(false);
    } // for
  } // disableVowels()

  /*  */
  private void disableCons() {
    for (Component component : consPanel.getComponents()) {
      component.setEnabled(false);
    } // for
  } // disableCons()

  /*  */
  private void vowelPressed(String v) {
    if (puzzle.contains(v)) {
      updatePuzzle(v);
    } else {
      disableVowels();
      data.invalidGuess();
    }

    data.addGuessedVowel(v);
    enableButtons();
    disableVowels();
  } // vowelPressed()

  /*  */
  private void consPressed(String c) {
    if (puzzle.contains(c)) {
      updatePuzzle(c);
      data.validGuess();
    } else {
      data.invalidGuess();
    }

    data.addGuessedCons(c);
    enableButtons();
    disableCons();
  } // consPressed()

  private void updatePuzzle(String letter) {
    if (puzzle.contains(letter)) {
      for (int i = 0; i < puzzle.length(); ++i) {
        if (puzzle.charAt(i) == letter.charAt(0)) {
          hidden[i] = letter.charAt(0);
        }
      }
    }
    puzzlePanel.removeAll();
    String label = createHiddenPuzzle();
    puzzlePanel.add(new JLabel(label));
    puzzlePanel.revalidate();
  } // updatePuzzle

  private String createHiddenPuzzle() {
    StringBuilder temp = new StringBuilder();
    for (char c : hidden) {
      temp.append(c);
      temp.append(" ");
    }
    return temp.toString();
  } // createHiddenPuzzle()

  /* Enable buttons if needed conditions are met */
  private void enableButtons() {
    if (data.currentBalance() >= 250
        && data.vowelsGuessed() != VOWELS.length) {
      buyButton.setEnabled(true);
    }
    if (data.consGuessed() != CONS.length) {
      spinButton.setEnabled(true);
    }
    solveButton.setEnabled(true);
  } // enableButtons()

  /* Disable buttons until choice has been made */
  private void disableButtons() {
    buyButton.setEnabled(false);
    spinButton.setEnabled(false);
    solveButton.setEnabled(false);
  } // disableButtons()

} // HangmanFrame

