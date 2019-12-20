package hangman;

import javax.swing.JFrame;

public class Hangman {
  public static void main(String[] args) {
    HangmanFrame gameFrame;
    gameFrame = new HangmanFrame();
    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gameFrame.pack();
    gameFrame.setVisible(true);
  } // main

} // Hangman
