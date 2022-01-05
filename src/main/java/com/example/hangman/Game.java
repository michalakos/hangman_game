package com.example.hangman;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    // word variable contains the randomly selected word from current dictionary
    private String word;
    // displayed_word is the letters of the word that the player has found
    private char[] displayed_word;
    // possible_answers contains all the words in the dictionary that could 
    // fit in the answer
    private HashSet<String> possible_answers = new HashSet<>();
    private final HashSet<Byte> found_positions = new HashSet<>();
    // probabilities is an array of floats where each row represents a letter
    // of the selected word and each column the count of words in the possible_answers
    // set containing each letter in each position
    private int[][] probabilities;
    // points are the points accumulated during this game
    private int points;
    // total number of moves executed in current game
    private byte total_moves;
    // number of correct moves executed in current game
    private byte correct_moves;
    // length is the length of the word
    private byte length;
    // tries is the number of mistakes the player can make before losing the game
    private byte tries;
    // victory is true if the game reached its final state and the player won
    private boolean victory;
    private boolean finished;


    // constructor
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


    // TODO: remove prints
    public void setGame (String dictionary_id) {
        this.word = "";
        this.length = 0;
        this.displayed_word = new char[]{'\u0000'};
        this.points = 0;
        this.total_moves = 0;
        this.correct_moves = 0;
        this.tries = 6;
        this.victory = false;
        this.finished = false;

        HashSet<String> possible_answers = new HashSet<>();
        HashSet<Byte> found_positions = new HashSet<>();

        // retrieve Dictionary
        String[] dictionary;
        try {
            dictionary = Dictionary.load(dictionary_id);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
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

        // debugging print statements
        System.out.println(this.word);
        System.out.println(this.length);
        System.out.println(this.possible_answers.toString());

        for (int[] integer : this.probabilities) {
            for (int j = 0; j < this.probabilities[0].length; j++) {
                System.out.print(integer[j] + " ");
            }
            System.out.println("\n");
        }
    }


    public HashSet<Byte> getFoundPositions() {
        return found_positions;
    }


    // receives a character and the position to put it
    // inserts character into position and increases points if correct guess
    // decrements tries by one and decreases points if wrong guess
    // returns true is the game reached a final state (win/loss)
    // TODO: remove prints
    public boolean nextMove (char c, byte position) {
        position--;
        // check if the move is valid
        if (position >= 0 && position < this.length && this.displayed_word[position] == '\u0000') {
            this.total_moves++;

            // correct guess
            if (this.word.charAt(position) == c) {
                found_positions.add((byte) (position + 1));
                this.correct_moves++;

                // add character in found characters
                this.displayed_word[position] = c;

                float probability = (float) this.probabilities[position][c-'A']
                                        / (float) this.possible_answers.size();
                
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


                // the player found the word
                if (this.word.equals(new String(this.displayed_word))) {
                    System.out.println("Victory");
                    this.victory = true;
                    this.finished = true;
                }

                // debugging prints
                // correct guess, game continues
                else {
                    System.out.println("Found " + c + " in position " + position);
                    System.out.println(this.displayed_word);
                }
            }

            // wrong guess
            else {
                points -= Math.min(this.points, 15);
                System.out.println(this.tries-1);

                // lost game
                if (--this.tries == 0) {
                    // debugging prints
                    System.out.println("Out of tries");
                    System.out.println("Word was " + this.word);
                    this.victory = false;
                    this.finished = true;
                }
                // wrong guess, game continues
                else {
                    System.out.println("No " + c + " in position " + position);
                }
            }
        }

        // illegal move - shouldn't happen with gui
        else {
            System.out.println("nextMove illegal arguments" + c + " " + position);
        }
        this.updateProbabilities();

        return this.finished;
    }


    // find updated probabilities after player move
    // should be called after every nextMove()
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
                if (this.displayed_word[i] != word.charAt(i) && this.displayed_word[i] != '\u0000') {
                    
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


    // get most probable characters for each position
    // TODO: also return numeric probabilities
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


    public String getAvailableWordCount () {
        return Integer.toString(this.possible_answers.size());
    }


    public String getPoints () {
        return Integer.toString(this.points);
    }


    public String getWord() {
        return this.word;
    }


    public int getTotalMoves() {
        return this.total_moves;
    }


    public byte getLength() {
        return this.length;
    }


    public boolean getFinished() {
        return this.finished;
    }


    public String getWinner() {
        if (this.finished) {
            return this.victory ? "PLAYER" : "COMPUTER";
        }
        else {
             return "INVALID";
        }
    }


    public String getSuccessPercentage() {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        double result = (double) (this.correct_moves * 100) / (double) this.total_moves;
        return (Double.isNaN(result)) ? "0.0%" : df.format(result) + "%";
    }


    public Image getTries() {
        final String IMAGE_PATH = "src/main/resources/pictures/stage";
        try {
            FileInputStream pic = new FileInputStream(IMAGE_PATH + (6-this.tries) + ".png");
            return new Image(pic);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getSolution() {
        for (int[] int_arr : this.probabilities) {
            for (int i : int_arr) {
                i = 0;
            }
        }
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


    public String getDisplayedWord() {
        StringBuilder sb = new StringBuilder();
        String s;
        for (char c : this.displayed_word) {
            s = (c=='\u0000') ? "_ " : c + " ";
            sb.append(s);
        }
        return sb.toString();
    }


    public String getPossibleAnswers() {
        System.out.println(this.possible_answers);
        StringBuilder sb = new StringBuilder();
        boolean newline = true;
        char [][] prob_chars = this.getProbChars();

        for (int i = 0; i < prob_chars.length; i++) {
            sb.append("Position "+ (i+1) + ": ");
            if (this.displayed_word[i] != '\u0000') {
                sb.append("-\n");
                newline = true;
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
