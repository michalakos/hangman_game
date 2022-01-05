package com.example.hangman;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Application {
    private static String DICT_PATH = "medialab/hangman_DICTIONARY-";
    @Override
    public void start(Stage stage) throws IOException {
        Session session = new Session();
        Game game = new Game();

        final double MAX_FONT_SIZE = 30.0;
        final String IMAGE_PATH = "src/main/resources/pictures/";

        MenuBar menuBar = new MenuBar();

        Menu application = new Menu("Application");

        MenuItem start = new MenuItem("Start");

        MenuItem load = new MenuItem("Load");
        load.setOnAction(e -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);

            Label dict_label = new Label();
            dict_label.setText("DICTIONARY_ID: ");
            HBox d_label_box = new HBox(dict_label);
            d_label_box.setPadding(new Insets(10));
            d_label_box.setAlignment(Pos.CENTER_LEFT);

            TextField dict_num = new TextField();
            HBox d_input_box = new HBox(dict_num);
            d_input_box.setPadding(new Insets(10));
            d_input_box.setAlignment(Pos.CENTER_RIGHT);

            HBox d_box = new HBox(d_label_box, d_input_box);
            Label message = new Label();

            VBox input = new VBox(d_box, message);

            Button new_dict_button = new Button();
            new_dict_button.setText("Load");
            new_dict_button.setPadding(new Insets(10));
            HBox button = new HBox(new_dict_button);
            button.setAlignment(Pos.BOTTOM_RIGHT);
            button.setPadding(new Insets(5));

            new_dict_button.setOnAction(event -> {
                try {
                    String dict_id = dict_num.getText();
                    File file = new File(DICT_PATH + dict_id + ".txt");
                    if (file.exists()) {
                        session.setDictionary(dict_id);
                        Stage st = (Stage) new_dict_button.getScene().getWindow();
                        st.close();
                    }
                    else {
                        message.setText("This dictionary does not exist");
                    }
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            });
            VBox popup = new VBox(input, button);
            VBox.setVgrow(input, Priority.ALWAYS);
            Scene popup_scene = new Scene(popup, 500, 200);
            dialog.setScene(popup_scene);
            dialog.setTitle("Load a dictionary");
            dialog.show();
        });

        MenuItem create = new MenuItem("Create");
        create.setOnAction(e -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);

            Label dict_label = new Label();
            dict_label.setText("DICTIONARY_ID: ");
            HBox d_label_box = new HBox(dict_label);
            d_label_box.setPadding(new Insets(10));
            d_label_box.setAlignment(Pos.CENTER_LEFT);

            TextField dict_num = new TextField();
            HBox d_input_box = new HBox(dict_num);
            d_input_box.setPadding(new Insets(10));
            d_input_box.setAlignment(Pos.CENTER_RIGHT);

            HBox dict = new HBox(d_label_box, d_input_box);

            Label open_lib_label = new Label();
            open_lib_label.setText("OpenLibraryID:   ");
            HBox ol_label_box = new HBox(open_lib_label);
            ol_label_box.setAlignment(Pos.CENTER_LEFT);
            ol_label_box.setPadding(new Insets(10));

            TextField open_lib_num = new TextField();
            HBox ol_input_box = new HBox(open_lib_num);
            ol_input_box.setPadding(new Insets(10));
            ol_input_box.setAlignment(Pos.CENTER_RIGHT);

            HBox open_lib = new HBox(ol_label_box, ol_input_box);

            VBox input = new VBox(dict, open_lib);

            Button new_dict_button = new Button();
            new_dict_button.setText("Create");
            new_dict_button.setPadding(new Insets(10));
            HBox button = new HBox(new_dict_button);
            button.setAlignment(Pos.BOTTOM_RIGHT);
            button.setPadding(new Insets(5));

            new_dict_button.setOnAction(event -> {
                try {
                    String dict_id = dict_num.getText();
                    String open_lib_id = open_lib_num.getText();
                    System.out.println(dict_id + open_lib_id);
                    Dictionary.add(dict_id, open_lib_id);
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
                Stage st = (Stage) new_dict_button.getScene().getWindow();
                st.close();
            });
            VBox popup = new VBox(input, button);
            VBox.setVgrow(input, Priority.ALWAYS);
            Scene popup_scene = new Scene(popup, 500, 200);
            dialog.setScene(popup_scene);
            dialog.setTitle("Create a dictionary");
            dialog.show();
            Controller.createAction();
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            Stage st = (Stage) menuBar.getScene().getWindow();
            st.close();
        });

        application.getItems().add(start);
        application.getItems().add(load);
        application.getItems().add(create);
        application.getItems().add(exit);


        Menu details = new Menu("Details");

        MenuItem dictionary = new MenuItem("Dictionary");
        dictionary.setOnAction(e -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);

            Label dict_label = new Label();
            dict_label.setText(session.getDictionaryStats());
            HBox d_box = new HBox(dict_label);
            d_box.setPadding(new Insets(10));
            d_box.setAlignment(Pos.CENTER);

            VBox popup = new VBox(d_box);
            VBox.setVgrow(d_box, Priority.ALWAYS);
            Scene popup_scene = new Scene(popup, 500, 200);
            dialog.setScene(popup_scene);
            dialog.setTitle("Dictionary information");
            dialog.show();
        });

        MenuItem rounds = new MenuItem("Rounds");
        rounds.setOnAction(e -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);

            Label rounds_word = new Label();
            Label rounds_tries = new Label();
            Label rounds_winner = new Label();

            String[] rounds_strings = session.getRounds();
            rounds_word.setText(rounds_strings[0]);
            rounds_tries.setText(rounds_strings[1]);
            rounds_winner.setText(rounds_strings[2]);

            HBox rounds_box = new HBox(rounds_word, rounds_tries, rounds_winner);
            rounds_box.setPadding(new Insets(10));
            rounds_box.setAlignment(Pos.CENTER);

            VBox popup = new VBox(rounds_box);
            VBox.setVgrow(rounds_box, Priority.ALWAYS);
            Scene popup_scene = new Scene(popup, 500, 200);
            dialog.setScene(popup_scene);
            dialog.setTitle("Previous games");
            dialog.show();
        });

        MenuItem solution = new MenuItem("Solution");

        details.getItems().add(dictionary);
        details.getItems().add(rounds);
        details.getItems().add(solution);

        menuBar.getMenus().add(application);
        menuBar.getMenus().add(details);

        VBox menu_box = new VBox(menuBar);

        Label words = new Label();
        words.setText("Total words in dictionary: ");
        Label word_count = new Label();
        HBox word_count_box = new HBox(words, word_count);

        Label points = new Label();
        points.setText("Points: ");
        Label points_num = new Label();
        points_num.setText("");
        HBox points_box = new HBox(points, points_num);

        Label success = new Label();
        success.setText("Correct guesses: ");
        Label success_percent = new Label();
        success_percent.setText("");
        HBox success_box = new HBox(success, success_percent);

        HBox upper = new HBox(word_count_box, points_box, success_box);
        upper.setSpacing(150);
        upper.setPadding(new Insets(20));
        upper.setAlignment(Pos.CENTER);

        FileInputStream pic = new FileInputStream(IMAGE_PATH + "stage0.png");
        Image image = new Image(pic);
        ImageView tries = new ImageView(image);
        HBox image_box = new HBox(tries);
        image_box.setPadding(new Insets(10));

        Label word = new Label();
        word.setFont(new Font(MAX_FONT_SIZE));
        word.setText("");
        HBox word_box = new HBox(word);
        word_box.setPadding(new Insets(20));

        VBox game_state_box = new VBox(image_box, word_box);

        Label possible_answers = new Label();
        possible_answers.setText("");
        possible_answers.setPadding(new Insets(30));
        HBox pos_ans_box = new HBox(possible_answers);

        HBox middle = new HBox(game_state_box, pos_ans_box);
        middle.setSpacing(10);
        middle.setPadding(new Insets(30, 50, 10, 10));
        middle.setAlignment(Pos.CENTER_LEFT);

        Label msg1 = new Label();
        msg1.setText("Insert character (A-Z)");
        TextField char_select = new TextField();
        VBox char_box = new VBox(msg1, char_select);
        char_box.setPadding(new Insets(30));

        Label msg2 = new Label();
        msg2.setText("Select position (starting from 1)");
        TextField pos_select = new TextField();
        VBox pos_box = new VBox(msg2, pos_select);
        pos_box.setPadding(new Insets(30));

        Button button = new Button("Submit");
        HBox button_box = new HBox(button);
        button_box.setPadding(new Insets(40));

        solution.setOnAction(e -> {
            if (game.getFinished()) {
                System.err.println("Game has finished");
                return;
            }
            char_select.clear();
            pos_select.clear();

            String game_word = game.getSolution();
            tries.setImage(game.getTries());

            word.setText(game_word);
            possible_answers.setText(game.getPossibleAnswers());
            points_num.setText(game.getPoints());
            success_percent.setText(game.getSuccessPercentage());
            word_count.setText(game.getAvailableWordCount());
            session.setRounds(game);
        });

        button.setOnAction(action -> {
            char char_in = char_select.getText().charAt(0);
            int pos;
            try {
                pos = Integer.parseInt(pos_select.getText());
            }
            catch (Exception e) {
                pos = 0;
            }
            byte pos_in = (byte) pos;
            if (char_select.getText().length() > 1 || char_in > 'Z' || char_in < 'A') {
                System.err.println("Input must be a single character from A to Z");
            }
            else if (pos_in > game.getLength() || pos_in < 1 || game.getFoundPositions().contains(pos_in)) {
                System.err.println("Position must be a number designating a missing character where the first " +
                        "character of the word has position 1");
            }
            char_select.clear();
            pos_select.clear();
            char_select.requestFocus();
            if (game.getFinished()) {
                System.err.println("Game has finished");
                return;
            }
            boolean game_finished = game.nextMove(char_in, pos_in);
            game.updateProbabilities();
            tries.setImage(game.getTries());

            word.setText(game.getDisplayedWord());
            possible_answers.setText(game.getPossibleAnswers());
            points_num.setText(game.getPoints());
            success_percent.setText(game.getSuccessPercentage());
            word_count.setText(game.getAvailableWordCount());

            if (game_finished) {
                String msg = (game.getWinner() == "PLAYER") ? "Congratulations!" : "Better luck next time";
                System.out.println(msg);
                session.setRounds(game);
            }
        });

        HBox lower = new HBox(char_box, pos_box, button_box);
        lower.setAlignment(Pos.BOTTOM_LEFT);
        lower.setPadding(new Insets(10));

        VBox total = new VBox(menu_box, upper, middle, lower);
        VBox.setVgrow(middle, Priority.ALWAYS);

        Scene scene = new Scene(total, 1000, 700);
        stage.setTitle("MediaLab Hangman");
        stage.setScene(scene);
        stage.show();


        start.setOnAction(e -> {
            Controller.startAction();

            game.setGame(session.getDictionary());
            tries.setImage(game.getTries());
            word.setText(game.getDisplayedWord());
            possible_answers.setText(game.getPossibleAnswers());
            points_num.setText("0");
            success_percent.setText(game.getSuccessPercentage());
            word_count.setText(game.getAvailableWordCount());
        });
    }

    public static void main(String[] args) {
        launch();
    }
}