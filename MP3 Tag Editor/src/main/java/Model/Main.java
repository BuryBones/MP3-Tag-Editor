package main.java.Model;

import main.java.Controller.StartController;
import main.java.View.StartView;

public class Main {
    private static String version = "0.99";

    private static StartView startView;
    private static StartController startController;
    private static StartModel startModel;

    public static String getVersion() {
        return version;
    }

    public static void main(String [] args) {
        startView = StartView.getInstance();
        startController = new StartController();
        startModel = new StartModel();

        startView.setController(startController);
        startController.setModel(startModel);
        startController.setView(startView);
        startModel.setController(startController);
    }
}
