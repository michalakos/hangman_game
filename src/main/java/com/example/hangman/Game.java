package com.example.hangman;

import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * contains information about active game
 */
public class Game {
    // game's target word
    private String word;

    // word appearing in GUI
    private char[] displayed_word;

    // dictionary words fitting current state of word
    private HashSet<String> possible_answers = new HashSet<>();

    // positions where missing letters are found
    private HashSet<Byte> found_positions = new HashSet<>();

    // each row represents a letter of the selected word
    // each column the count of possible answers containing every letter in the alphabet (first item is 'A', last 'Z')
    private int[][] probabilities;

    // points accumulated in this game
    private int points;

    // total number of moves executed in current game
    private byte total_moves;

    // number of successful moves in current game
    private byte correct_moves;

    // length of target word
    private byte length;

    // remaining wrong moves before loss
    private byte tries;

    // game is finished
    private boolean finished;

    // player won the game
    private boolean victory;

    // last move was successful
    private boolean success;

    // position of last move
    private byte last_pos;

    // letter of last move
    private char last_char;


    /**
     * constructor
     */
    public Game () {
        this.word = "";
        this.length = 0;
        this.displayed_word = new char[]{'\u0000'};
        this.points = 0;
        this.total_moves = 0;
        this.correct_moves = 0;
        this.tries = 6;
        this.victory = false;
        this.finished = false;
    }


    /**
     * reset game state
     * @param dictionary_id id of dictionary file used in game
     * @throws Exception failed to load dictionary
     */
    public void setGame (String dictionary_id) throws Exception{
        this.word = "";
        this.length = 0;
        this.displayed_word = new char[]{'\u0000'};
        this.points = 0;
        this.total_moves = 0;
        this.correct_moves = 0;
        this.tries = 6;
        this.victory = false;
        this.finished = false;

        this.possible_answers = new HashSet<>();
        this.found_positions = new HashSet<>();

        // retrieve Dictionary
        String[] dictionary;
        try {
            dictionary = Dictionary.load(dictionary_id);
        }
        catch (Exceptions.UnbalancedException | Exceptions.UndersizeException | Exceptions.InvalidRangeException |
            Exceptions.InvalidCountException | FileNotFoundException e) {
            throw new Exception("Game.setGame(): error loading dictionary with id: " + dictionary_id);
        }

        // get a random word from the dictionary
        int dictionary_length = dictionary.length;
        int random_index = ThreadLocalRandom.current().nextInt(0, dictionary_length);
        this.word = dictionary[random_index];

        // create empty char array displaying found characters
        this.displayed_word = new char[this.word.length()];

        // save the length of the selected word
        this.length = (byte)this.word.length();

        // create probabilities array
        this.probabilities = new int[this.length][26];

        // add dictionary words with same length in possible_answers
        // and update probabilities array accordingly
        char c;
        for (String s : dictionary) {
            if (s.length() == this.length) {
                this.possible_answers.add(s);

                // for each word in possible answers increment its appearances 
                // counter by one in the position it was found
                for (int i = 0; i < this.length; i++) {
                    c = s.charAt(i);
                    this.probabilities[i][c - 'A']++;
                }
            }
        }
    }


    /**
     * @return positions of characters successfully found
     */
    public HashSet<Byte> getFoundPositions() {
        return found_positions;
    }


    /**
     * try letter in position
     * if successful insert letter and increase points
     * if unsuccessful reduce number of tries left before loss by one and decrease points
     * @param c letter given by player
     * @param position position in which to try letter
     * @return game still in session
     */
    public boolean nextMove (char c, byte position) {
        position--;
        // check if the move is valid
        if (position >= 0 && position < this.length && this.displayed_word[position] == '\u0000') {
            this.last_char =c;
            this.last_pos = position;
            this.total_moves++;

            // correct guess
            if (this.word.charAt(position) == c) {
                found_positions.add((byte) (position + 1));
                this.correct_moves++;
                this.success = true;

                // add character in found characters
                this.displayed_word[position] = c;

                float probability = (float) this.probabilities[position][c-'A']
                                        / (float) this.possible_answers.size();

                // increase points based on probability of letter in current position
                if (probability >= 0.6f) {
                    points += 5;
                }
                else if (probability >= 0.4f) {
                    points += 10;
                }
                else if (probability >= 0.25f) {
                    points += 15;
                }
                else {
                    points += 30;
                }


                // victory
                if (this.word.equals(new String(this.displayed_word))) {
                    this.victory = true;
                    this.finished = true;
                }
            }

            // wrong guess
            else {
                this.success = false;
                points -= Math.min(this.points, 15);

                // defeat
                if (--this.tries == 0) {
                    this.victory = false;
                    this.finished = true;
                }
            }
        }

        // illegal move - shouldn't happen with gui
        else {
            System.err.println("nextMove illegal arguments" + c + " " + position);
        }
        this.updateProbabilities();

        return this.finished;
    }


    /**
     * update possible answers
     * called after every nextMove()
     */
    public void updateProbabilities() {
        // flag to mark removed words
        boolean removed;

        // new set to add updated possible answers
        HashSet<String> new_set = new HashSet<>();

        // iterate every word in set
        for (String word : this.possible_answers) {
            // initialise flag
            removed = false;

            // iterate over word's characters
            for (int i = 0; i < this.length; i++) {

                // the word is no longer a possible answer
                if ((this.displayed_word[i] != word.charAt(i) && this.displayed_word[i] != '\u0000') ||
                        (this.last_char == word.charAt(this.last_pos) && !this.success)){
                    
                    // update probabilities array removing the word
                    for (int j = 0; j < this.length; j++) {
                        int index = word.charAt(j) - 'A';
                        this.probabilities[j][index]--;
                    }

                    // raise flag
                    removed = true;

                    // stop after finding the word isn't a possible answer
                    break;
                }    
            }

            // only add words that aren't marked as removed
            if (!removed) {
                new_set.add(word);
            }
        }

        // update possible answers
        this.possible_answers = new_set;
    }


    /**
     * @return probability matrix where each row represents the target word's positions and each column every letter
     * from A to Z, where the value is the number of possible answers having the letter in this position
     */
    public char[][] getProbChars () {
        // character array to return values
        char[][] most_probable_chars = new char[this.length][26];
        // temporary array to sort by number of appearances
        Character[] letters = new Character[26];

        // for every character position in the wanted word
        for (int word_char = 0; word_char < this.length; word_char++) {

            // probs contains number of appearances of each character
            // in selected position
            int[] probs = this.probabilities[word_char].clone();

            // letters contains every character from 'A' to 'Z' 
            for (int ch = 'A'; ch <= (int)'Z'; ch++) {
                letters[ch - (int)'A'] = (char)ch;
            }

            // override sort function to sort letters array
            // by comparing the number of appearances of each letter 
            // in given position
            Arrays.sort(letters, (o1, o2) -> {
                int index1 = (int) o1 - (int)'A';
                int index2 = (int) o2 - (int)'A';
                return Integer.compare(probs[index1], probs[index2]);
            });

            // append results to result array
            // from end to beginning of letter array because sorting does it
            // in increasing order
            for (int i = 0; i < 26; i++) {
                most_probable_chars[word_char][i] = letters[25-i];
            }
        }

        // return result
        return most_probable_chars;
    }


    /**
     * @return number of possible answers in dictionary
     */
    public String getAvailableWordCount () {
        return Integer.toString(this.possible_answers.size());
    }

    /**
     * @return player's points
     */
    public String getPoints () {
        return Integer.toString(this.points);
    }

    /**
     * @return target word
     */
    public String getWord() {
        return this.word;
    }

    /**
     * @return total moves made in game
     */
    public int getTotalMoves() {
        return this.total_moves;
    }

    /**
     * @return length of target word
     */
    public byte getLength() {
        return this.length;
    }

    /**
     * @return game is finished
     */
    public boolean getFinished() {
        return this.finished;
    }

    /**
     * @return "PLAYER" if player won or "COMPUTER" otherwise
     */
    public String getWinner() {
        if (this.finished) {
            return this.victory ? "PLAYER" : "COMPUTER";
        }
        // sanity check
        else {
             return "INVALID";
        }
    }

    /**
     * @return percentage of successful moves
     */
    public String getSuccessPercentage() {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        double result = (double) (this.correct_moves * 100) / (double) this.total_moves;
        return (Double.isNaN(result)) ? "0.0%" : df.format(result) + "%";
    }

    /**
     * @return image representing remaining unsuccessful moves
     */
    public Image getTries() {
        final String IMAGE_PATH = "src/main/resources/pictures/stage";
        try {
            FileInputStream pic = new FileInputStream(IMAGE_PATH + (6-this.tries) + ".png");
            return new Image(pic);
        }
        catch (Exception e) {
            System.err.println("Game.getTries(): error loading images");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * reset game state and return missing word
     * @return target word
     */
    public String getSolution() {
        this.updateProbabilities();
        this.tries = 0;
        this.points = 0;
        this.victory = false;
        this.finished = true;
        this.possible_answers = new HashSet<>();
        for (int i = 0; i < this.length; i++) {
            this.displayed_word[i] = this.word.charAt(i);
        }
        return this.getDisplayedWord();
    }

    /**
     * @return string representing state of target word where '_' represents a missing letter
     */
    public String getDisplayedWord() {
        StringBuilder sb = new StringBuilder();
        String s;
        // '\u0000' means letter not yet found
        for (char c : this.displayed_word) {
            s = (c=='\u0000') ? "_ " : c + " ";
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * @return dictionary words fitting in current state of missing word
     */
    public String getPossibleAnswers() {
        StringBuilder sb = new StringBuilder();
        boolean newline = true;
        char [][] prob_chars = this.getProbChars();

        // get list of possible letters for each of the positions containing a missing letter
        for (int i = 0; i < prob_chars.length; i++) {
            sb.append(String.format("Position %d: ", i+1));
            if (this.displayed_word[i] != '\u0000') {
                sb.append("-\n");
                continue;
            }

            for (int j = 0; j < prob_chars[0].length; j++) {
                if (this.probabilities[i][prob_chars[i][j]-'A'] > 0) {
                    if (!newline) {
                        sb.append(", ");
                    }
                    sb.append(prob_chars[i][j]);
                    newline = false;
                }
            }
            sb.append("\n");
            newline = true;
        }

        return sb.toString();
    }
}
