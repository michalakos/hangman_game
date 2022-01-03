package com.example.hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class Controller {

    public static void startAction() {
        System.out.println("Start");
    }

    public static void loadAction() {
        System.out.println("Load");
    }

    public static void createAction() {
        System.out.println("Create");
    }

    public static void exitAction() {
        System.out.println("Exit");
    }

    public static void dictionaryAction() {
        System.out.println("Dictionary");
    }

    public static void roundsAction() {
        System.out.println("Rounds");
    }

    public static void solutionAction() {
        System.out.println("Solution");
    }
}