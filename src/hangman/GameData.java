package hangman;

import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 *
 *
 **/
public class GameData {
  /* List of player names */
  private ArrayList<String> playerNames = new ArrayList<>();

  /* List of player balances */
  private ArrayList<Integer> playerBalance = new ArrayList<>();

  /* List of player panels */
  private ArrayList<JPanel> namePanels = new ArrayList<>();

  /* List of player titles */
  private ArrayList<TitledBorder> nameTitles = new ArrayList<>();

  /* List of guessed consonants */
  private ArrayList<String> guessedCons = new ArrayList<>();

  /* List of guessed vowels */
  private ArrayList<String> guessedVowels = new ArrayList<>();

  /* Number of players in the game */
  private int numPlayers = 0;

  /* Index of the current player */
  private int current = 0;

  /* Potential balance increase */
  private int toAdd = 0;

  /**
   * Create a new player with a given name.
   * @param name name of new player
   */
  public void addNewPlayer(String name) {
    playerNames.add(name);
    playerBalance.add(0);
  } // addNewPlayer()

  /**
   * Set the number of players in the game.
   * @param numPlayers number of players in the game
   */
  public void setNumPlayers(int numPlayers) {
    this.numPlayers = numPlayers;
  } // setNumPlayers()

  /**
   * Retrieve the number of players in the game.
   * @return number of players in the game
   */
  public int getNumPlayers() {
    return numPlayers;
  } // getNumPlayers()

  /**
   * Set potential winnings for when a guess is correct.
   * @param money potential increase in balance
   */
  public void setToAdd(int money) {
    toAdd = money;
  } // setPotentialMoney()

  /**
   * Retrieve the name of the current player.
   * @return name of the current player
   */
  public String currentName() {
    return playerNames.get(current);
  } // currentName()

  /**
   * Retrieve the balance of the current player.
   * @return balance of the current player
   */
  public int currentBalance() {
    return playerBalance.get(current);
  } // currentBalance()

  /**
   * Create panels with all player names.
   */
  public void makeNamePanels() {
    for (int p = 0; p < numPlayers; ++p) {
      namePanels.add(makeNamePanel(p));
    } // for
    updatePanels();
  } // makeNamePanels()

  /**
   * Update player information on panels.
   */
  public void updatePlayers() {
    updatePanels();
  } // updatePlayers();

  /**
   *  Retrieve the name panel for a given player.
   * @param pos position of given player
   * @return name panel for given player
   */
  public JPanel getNamePanel(int pos) {
    return namePanels.get(pos);
  } // getNamePanel()

  /**
   * Determine if a vowel has been guessed already.
   * @param vowel vowel that needs to be checked
   * @return whether or not a vowel has been guessed
   */
  public boolean isGuessedVowel(String vowel) {
    return guessedVowels.contains(vowel);
  } // isGuessedVowel()

  /**
   * Determine if a consonant has been guessed already.
   * @param cons consonant that needs to be checked
   * @return whether or not a consonant has been guessed
   */
  public boolean isGuessedCons(String cons) {
    return guessedCons.contains(cons);
  } // isGuessedCons()

  /**
   * Add a vowel to the list of guessed vowels.
   * @param vowel vowel that has been guessed
   */
  public void addGuessedVowel(String vowel) {
    guessedVowels.add(vowel);
  } // addGuessedVowel()

  /**
   * Add a consonant to the list of guessed consonants.
   * @param cons consonant that has been guessed
   */
  public void addGuessedCons(String cons) {
    guessedCons.add(cons);
  } // addGuessedCons()

  /**
   * Determine how many vowels have been guessed.
   * @return how many vowels have been guessed
   */
  public int vowelsGuessed() {
    return guessedVowels.size();
  } // vowelsGuessed()

  /**
   * Determine how many consonants have been guessed.
   * @return how many consonants have been guessed
   */
  public int consGuessed() {
    return guessedCons.size();
  } // consGuessed()


  /**
   * Update player information if correct guess has been made.
   */
  public void validGuess() {
    addMoney();
    updatePlayers();
  } // validGuess()

  /**
   * Update player information if incorrect guess has been made.
   */
  public void invalidGuess() {
    advancePlayer();
    updatePlayers();
  } // invalidGuess()

  /**
   * Update current player information when they purchase a vowel.
   */
  public void buyVowel() {
    playerBalance.set(current, playerBalance.get(current) - 250);
    updatePlayers();
  } // buyVowel()

  /**
   * Bankrupt the current player.
   */
  public void bankruptPlayer() {
    playerBalance.set(current, 0);
    advancePlayer();
    updatePlayers();
  } // bankruptPlayer()

  /**
   * Make the current player lose a turn.
   */
  public void loseATurn() {
    advancePlayer();
    updatePlayers();
  } // loseATurn()

  /* Move current player to the next player */
  private void advancePlayer() {
    current = (current + 1) % numPlayers;
  } // advancePlayer()

  /* Add money to current player balance */
  private void addMoney() {
    playerBalance.set(current, playerBalance.get(current) + toAdd);
  } // addMoney()

  /* Create individual name panels for a given player */
  private JPanel makeNamePanel(int p) {
    TitledBorder title = BorderFactory.createTitledBorder(playerNames.get(p));
    title.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    title.setTitleJustification(TitledBorder.LEFT);
    nameTitles.add(title);

    JPanel namePanel = new JPanel(new FlowLayout());
    namePanel.setBorder(title);
    namePanel.add(new JLabel(Integer.toString(playerBalance.get(p))));
    return namePanel;
  } // makeNamePanel()

  /* Update player panels to reflect new information */
  private void updatePanels() {
    for (int p = 0; p < numPlayers; ++p) {
      Border playerBorder;
      if (p == current) {
        playerBorder = BorderFactory.createLineBorder(Color.RED);
      } else {
        playerBorder = BorderFactory.createLineBorder(Color.BLACK);
      }
      nameTitles.get(p).setBorder(playerBorder);
      namePanels.get(p).removeAll();
      namePanels.get(p).add(new JLabel(Integer.toString(playerBalance.get(p))));
      namePanels.get(p).revalidate();
      namePanels.get(p).setBorder(nameTitles.get(p));
      namePanels.get(p).repaint();
    } // for
  } // updatePanels()

} // GameData
