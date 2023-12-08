package bb.Model;

import bb.View.StartView;
import bb.Controller.StartController;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static String version = "1.0";

    // logger setup
    public static Logger mainLogger = Logger.getLogger(Main.class.getSimpleName());
    public static FileHandler warning = null;
    public static FileHandler common = null;
    static
    {
        try {
            warning = new FileHandler("errors.log");
            warning.setFormatter(new SimpleFormatter());
            warning.setLevel(Level.WARNING);
            mainLogger.addHandler(warning);
        } catch (IOException ioE) {
            String errorMessage = mainLogger.getName() + " logger setup error.";
            System.err.println(errorMessage + " Exception message: " + ioE.getMessage());
            mainLogger.log(Level.WARNING, errorMessage, ioE);
        }
        try {
            common = new FileHandler("common.log");
            common.setFormatter(new SimpleFormatter());
            common.setLevel(Level.ALL);
            mainLogger.addHandler(common);
        } catch (IOException ioE) {
            String errorMessage = mainLogger.getName() + " logger setup error.";
            System.err.println(errorMessage + " Exception message: " + ioE.getMessage());
            mainLogger.log(Level.WARNING, errorMessage, ioE);
        }
    }

    private static StartView startView;
    private static StartController startController;
    private static StartModel startModel;

    public static String getVersion() {
        return version;
    }

    public static void main(String [] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        mainLogger.log(Level.ALL, "Starting application. Version " + version);
        startView = StartView.getInstance();
        startController = new StartController();
        startModel = new StartModel();
        startView.setController(startController);
        startController.setModel(startModel);
        startController.setView(startView);
        startModel.setController(startController);
    }
}
