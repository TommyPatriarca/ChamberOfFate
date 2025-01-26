package com.cof.ui;

import com.cof.managers.SoundManager;
import com.controller.Controller;
import com.controller.ControllerOnline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * La classe per lo sfondo le gioco online
 */
public class OnlineGameScreen {
    private ControllerOnline controller;

    /**
     * il costruttore della classe
     * @param controller il controllore
     */
    public OnlineGameScreen(ControllerOnline controller) {
        this.controller = controller;
    }
}