package main.java.Model;

import main.java.Controller.StartController;
import main.java.View.StartView;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static String version = "0.99";
    private static Logger logger = Logger.getLogger(Main.class.getSimpleName());
    static
    {
        try {
            FileHandler warning = new FileHandler("errors.log");
            warning.setFormatter(new SimpleFormatter());
            warning.setLevel(Level.WARNING);

            FileHandler common = new FileHandler("common.log");
            common.setFormatter(new SimpleFormatter());
            common.setLevel(Level.ALL);

            logger.addHandler(warning);
            logger.addHandler(common);

        } catch (IOException ioE) {
            System.err.println("Failed to organize logging to .log files. Exception message: " + ioE.getMessage());
        }
    }

    private static StartView startView;
    private static StartController startController;
    private static StartModel startModel;

    public static String getVersion() {
        return version;
    }

    // TODO: check logging!
    // TODO: better setup access modifiers

    public static void main(String [] args) {
        logger.log(Level.ALL, "Starting application. Version " + version);
        startView = StartView.getInstance();
        startController = new StartController();
        try {
            startModel = new StartModel();
        } catch (IOException ioE) {
            logger.log(Level.WARNING,"Failed to organize logging to .log files for ModifyModel.", ioE);
        }
        startView.setController(startController);
        startController.setModel(startModel);
        startController.setView(startView);
        startModel.setController(startController);
    }
}
