package com.example.hangman;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Session {
    private String dictionary_id;
    private final static String SAVE_PATH = "src/main/resources/com/example/hangman/savegame.txt";


    public Session () {
    }

    public void setDictionary (String dictionary_id) {
        this.dictionary_id = dictionary_id;
    }

    public String getDictionary() {
        return this.dictionary_id;
    }

    public String getDictionaryStats() {
        try {
            String[] dictionary = Dictionary.load(this.dictionary_id);
            int total_words = 0;
            int six_letter_words = 0;
            int seven_to_nine_letter_words = 0;
            int ten_or_more_letter_words = 0;

            for (String s : dictionary) {
                total_words++;
                if (s.length() == 6) {
                    six_letter_words++;
                }
                else if (s.length() > 9) {
                    ten_or_more_letter_words++;
                }
                else {
                    seven_to_nine_letter_words++;
                }
            }

            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            String six = df.format((double) (six_letter_words * 100) / (double) total_words) + "%";
            String seven_nine = df.format((double) (seven_to_nine_letter_words * 100) / (double) total_words) + "%";
            String ten = df.format((double) (ten_or_more_letter_words * 100) / (double) total_words) + "%";

            return "Six letter words: " + six +
                    "\nSeven to nine letter words: " + seven_nine +
                    "\nTen or more letter words:" + ten;

        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public String[] getRounds () {
        Path relative_path = Paths.get(SAVE_PATH);
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
            e.printStackTrace();
        }

        String[] save = file_contents.toArray(new String[0]);

        int entries = Integer.parseInt(save[0]);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        List<String> temp_list;

        for (int i = 0; i < entries; i++) {
            temp = save[i+1];
            temp_list = new ArrayList<>(Arrays.asList(temp.split(",")));
            sb1.append(String.format("Word: %-19s\n", temp_list.get(0)));
            sb2.append(String.format("Tries: %-4s\n", temp_list.get(1)));
            sb3.append(String.format("Winner: %-10s\n", temp_list.get(2)));
        }
        return new String[]{sb1.toString(), sb2.toString(), sb3.toString()};
    }


    public void setRounds(Game game) {
        Path relative_path = Paths.get(SAVE_PATH);
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
            e.printStackTrace();
        }

        String[] save = file_contents.toArray(new String[0]);

        int entries = Integer.parseInt(save[0]);

        StringBuilder sb = new StringBuilder();

        if (entries < 5) {
            sb.append(++entries);
        }
        else {
            sb.append(entries);
        }
        sb.append("\n");
        sb.append(game.getWord());
        sb.append(",");
        sb.append(game.getTotalMoves());
        sb.append(",");
        sb.append(game.getWinner());
        sb.append("\n");

        for (int i = 0; i < entries-1; i++) {
            sb.append(save[i+1]);
            sb.append("\n");
        }

        try (PrintWriter pw = new PrintWriter(filename)) {
            pw.print(sb);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(sb);
    }

}
