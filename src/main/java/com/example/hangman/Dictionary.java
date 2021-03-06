package com.example.hangman;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import javax.json.*;

/**
 * class providing methods to create and load dictionaries
 */
public class Dictionary {
    // path of dictionaries
    private static final String PATH = "medialab/";

    /**
     * Creates a text file (dictionary) in PATH containing the words in the
     * value field (if it exists) of the description field of the json object returned
     * from the OpenLibrary API call.
     *
     * @param dictionary_id id of the created dictionary (arbitrary)
     * @param open_library_id id of book in OpenLibrary
     * @throws Exception creation of dictionary failed
     */
    public static void add (String dictionary_id, String open_library_id) throws Exception {

        // set the url containing the requested book's json object
        URL url;
        try {
            String u = "https://openlibrary.org/works/" + open_library_id + ".json";
            url = new URL(u);
        }
        catch (MalformedURLException e){
            System.err.println("Dictionary.add(): no url match");
            throw e;
        }

        // create json reader and read "description" value
        // store value in string
        String description;
        try (
            InputStream is = url.openStream();
            JsonReader rdr = Json.createReader(is)) {
                JsonObject obj = rdr.readObject();
                byte counter = 0;
                description = "";
                try {
                    description = obj.getString("description");
                }
                catch (Exception e) {
                    counter++;
                }
                try {
                    obj = obj.getJsonObject("description");
                    description = obj.getString("value");
                }
                catch (Exception e) {
                    counter++;
                }
                if (counter == 2) {
                    System.err.println("This work does not contain 'description' field");
                    return;
                }
                description = description.replace("-", " ");
            }
        catch(IOException e) {
            System.err.println("Dictionary.add(): error creating dictionary");
            throw e;
        }

        // scan string containing description
        // remove punctuation and numbers and convert to uppercase
        // ignore words with less than 6 letters
        // store in set to keep unique words
        Scanner scanner = new Scanner(description);
        HashSet<String> unique_words = new HashSet<>();
        while (scanner.hasNext()) {
            String  word = scanner.next().replaceAll("[^a-zA-Z]", "").toUpperCase();
            if (word.length() > 5) {
                unique_words.add(word);
            }
        }
        scanner.close();

        // specify destination file path to save dictionary
        Path relative_path = Paths.get(PATH + "hangman_DICTIONARY-" + dictionary_id + ".txt");
        Path absolute_path = relative_path.toAbsolutePath();
        String filename = absolute_path.toString();

        // set_size is used to not put newline after last word in set
        int set_size = unique_words.size();

        // parse set and store words in destination file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            int counter = 0;
            for (String s : unique_words) {
                if (set_size == ++counter) {
                    writer.write(s);
                }
                else {
                    writer.write(s+"\n");
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Loads an existing dictionary from PATH given its id and returns its contents
     *
     * @param dictionary_id id of existing dictionary
     * @return dictionary words
     * @throws Exceptions.InvalidCountException duplicate word in dictionary
     * @throws Exceptions.InvalidRangeException word with less than six letters in dictionary
     * @throws Exceptions.UnbalancedException dictionary consists of more than 80% of small words (fewer than nine letters)
     * @throws Exceptions.UndersizeException dictionary contains fewer than 20 words
     * @throws FileNotFoundException no dictionary matching given id
     */
    public static String[] load (String dictionary_id)
    throws Exceptions.InvalidCountException, Exceptions.InvalidRangeException, 
    Exceptions.UnbalancedException, Exceptions.UndersizeException, FileNotFoundException {

        // specify dictionary file path
        Path relative_path = Paths.get(PATH + "hangman_DICTIONARY-" + dictionary_id + ".txt");
        Path absolute_path = relative_path.toAbsolutePath();
        String filename = absolute_path.toString();

        String temp;
        List<String> file_contents = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))){
            while (scanner.hasNextLine()) {
                temp = scanner.nextLine();
                file_contents.add(temp);
            }
        }
        catch (IOException e) {
            throw new FileNotFoundException();
        }

        String[] dictionary = file_contents.toArray(new String[0]);

        // check that the dictionary contains only unique words
        HashSet<String> words = new HashSet<>();
        // flag to check if dictionary contains words with less than 6 letters
        boolean undersized = false;
        // count of words with more than 8 letters
        int big_words = 0;

        for (String s : dictionary) {
            words.add(s);

            if (s.length() < 6) {
                undersized = true;
            }
            else if (s.length() > 8) {
                big_words++;
            }
        }

        // check if dictionary complies with all restraints

        if (words.size() != dictionary.length) {
            throw new Exceptions.InvalidCountException(
                "Duplicate word in dictionary with id: " + dictionary_id);
        }
        
        if (words.size() < 20) {
            throw new Exceptions.UndersizeException(
                "Total dictionary size less than minimum of 20 words");
        }

        if (undersized) {
            throw new Exceptions.InvalidRangeException(
                "Word with less than 6 letters contained in dictionary with id: " + dictionary_id);
        }

        if (big_words * 5 < words.size()) {
            // raise UnbalancedException
            throw new Exceptions.UnbalancedException(
                "Words with less than 9 letters are more than 80% of words in dictionary with id: " + dictionary_id);
        }
        
        return dictionary;
    }
}
