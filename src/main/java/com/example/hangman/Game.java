package com.example.hangman;

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
    // probabilites is an array of floats where each row represents a letter
    // of the selected word and each column the count of words in the possible_answers
    // set containing each letter in each position
    private int[][] probabilities;
    // points are the points accumulated during this game
    private int points;
    // length is the length of the word
    private byte length;
    // tries is the number of mistakes the player can make before losing the game
    private byte tries;
    // victory is true if the game reached its final state and the player won
    private boolean victory;


    // constructor
    // TODO: remove prints
    public Game (String dictionary_id) {
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

        // at game start the player has 0 points
        this.points = 0;

        // save the length of the selected word
        this.length = (byte)this.word.length();

        // the number of tries is 5 for every new game
        this.tries = 5;

        // initialise victory flag
        this.victory = false;

        // create probabilites array
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
                    this.probabilities[i][(int)(c - 'A')]++;
                }
            }
        }

        // debugging print statements
        System.out.println(this.word);
        System.out.println(this.length);
        System.out.println(this.possible_answers.toString());
        for (int i = 0; i < this.probabilities.length; i++) {
            for (int j = 0; j < this.probabilities[0].length; j++) {
                System.out.print(this.probabilities[i][j] + " ");
            }
            System.out.println("\n");
        }
    }


    // receives a character and the position to put it
    // inserts character into position and increases points if correct guess
    // decrements tries by one and decreases points if wrong guess
    // returns true is the game reached a final state (win/loss)
    // TODO: remove prints
    public boolean nextMove (char c, byte position) {
        // check if the move is valid
        if (position >= 0 && position < this.length && this.displayed_word[position] == '\u0000') {

            // correct guess
            if (this.word.charAt(position) == c) {

                // add character in found characters
                this.displayed_word[position] = c;

                float probability = this.probabilities[position][(int)(c-'A')];
                probability /= this.possible_answers.size();
                
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
                    return true;
                }

                // debugging prints
                // correct guess, game continues
                System.out.println("Found " + c + " in position " + position);
                System.out.println(this.displayed_word);
                return false;
            }

            // wrong guess
            else {
                points -= Math.min(this.points, 15);

                // lost game
                if (--this.tries == 0) {
                    // debugging prints
                    System.out.println("Out of tries");
                    System.out.println("Word was " + this.word);
                    this.victory = false;
                    return true;
                }
                // wrong guess, game continues
                else {
                    System.out.println("No " + c + " in position " + position);
                    return false;
                }
            }
        }

        // illegal move - shouldn't happen with gui
        else {
            System.out.println("nextMove illegal arguments" + c + " " + position);
            return false;
        }
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
                        int index = (int)(word.charAt(j) - 'A');
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
            for (int ch = (int)'A'; ch <= (int)'Z'; ch++) {
                letters[ch - (int)'A'] = (char)ch;
            }

            // override sort function to sort letters array
            // by comparing the number of appearances of each letter 
            // in given position
            Arrays.sort(letters, new Comparator<Character>() {
                @Override 
                public int compare (Character o1, Character o2) {
                    int index1 = (int) o1 - (int)'A';
                    int index2 = (int) o2 - (int)'A';
                    return Integer.compare(probs[index1], probs[index2]);
                }
            });

            // append results to result array
            // from end to begining of letter array because sorting does it 
            // in increasing order
            for (int i = 0; i < 26; i++) {
                most_probable_chars[word_char][i] = letters[25-i];
            }
        }

        // return result
        return most_probable_chars;
    }
}
