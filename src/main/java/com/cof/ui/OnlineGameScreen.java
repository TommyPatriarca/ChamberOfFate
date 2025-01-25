package com.cof.ui;

import com.cof.managers.SoundManager;
import com.controller.Controller;
import com.controller.ControllerOnline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class OnlineGameScreen {
    private ControllerOnline controller;

    public OnlineGameScreen(ControllerOnline controller) {
        this.controller = controller;
    }
}