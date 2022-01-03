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
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        final double MAX_FONT_SIZE = 30.0;
        final String IMAGE_PATH = "src/main/resources/pictures/";

        AtomicInteger i = new AtomicInteger();

        MenuBar menuBar = new MenuBar();

        Menu application = new Menu("Application");

        MenuItem start = new MenuItem("Start");
        start.setOnAction(e -> {
            Controller.startAction();
        });

        MenuItem load = new MenuItem("Load");
        load.setOnAction(e -> {
            Controller.loadAction();
        });

        MenuItem create = new MenuItem("Create");
        create.setOnAction(e -> {
            Controller.createAction();
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            Controller.exitAction();
        });

        application.getItems().add(start);
        application.getItems().add(load);
        application.getItems().add(create);
        application.getItems().add(exit);


        Menu details = new Menu("Details");

        MenuItem dictionary = new MenuItem("Dictionary");
        dictionary.setOnAction(e -> {
            Controller.dictionaryAction();
        });

        MenuItem rounds = new MenuItem("Rounds");
        rounds.setOnAction(e -> {
            Controller.roundsAction();
        });

        MenuItem solution = new MenuItem("Solution");
        solution.setOnAction(e -> {
            Controller.solutionAction();
        });

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
        points_num.setText("0");
        HBox points_box = new HBox(points, points_num);

        Label success = new Label();
        success.setText("Correct guesses: ");
        Label success_percent = new Label();
        success_percent.setText("10%");
        HBox success_box = new HBox(success, success_percent);

        HBox upper = new HBox(word_count_box, points_box, success_box);
        upper.setSpacing(150);
        upper.setPadding(new Insets(20));
        upper.setAlignment(Pos.CENTER);

        FileInputStream pic = new FileInputStream(IMAGE_PATH + "stage0.png");
        Image image = new Image(pic);
        ImageView tries = new ImageView(image);
        HBox image_box = new HBox(tries);

        Label word = new Label();
        word.setFont(new Font(MAX_FONT_SIZE));
        word.setText("PLACEHOLDER");
        HBox word_box = new HBox(word);
        word_box.setPadding(new Insets(20,20,20,20));

        VBox game_state_box = new VBox(image_box, word_box);

        Label possible_answers = new Label();
        possible_answers.setText("Position 0: A, B, C, D, E, F, G, H, I, J\n" +
                "Position 1: A, B, C, D, E, F\nPosition 2: A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z");
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

        button.setOnAction(action -> {
            String char_in = char_select.getText();
            String pos_in = pos_select.getText();
            System.out.println(char_in + pos_in);
            char_select.clear();
            pos_select.clear();
            try
            {
                FileInputStream pic_tmp = new FileInputStream(IMAGE_PATH + "stage1.png");
                Image image_tmp = new Image(pic_tmp);
                tries.setImage(image_tmp);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });

        HBox lower = new HBox(char_box, pos_box, button_box);
        lower.setAlignment(Pos.BOTTOM_LEFT);
        lower.setPadding(new Insets(10));

        VBox total = new VBox(menu_box, upper, middle, lower);
        VBox.setVgrow(middle, Priority.ALWAYS);

        load.setOnAction(e -> {
            word_count.setText(Integer.toString(i.getAndIncrement()));
        });

        Scene scene = new Scene(total, 1000, 700);
        stage.setTitle("MediaLab Hangman");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}