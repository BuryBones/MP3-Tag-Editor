package main.java.Model;

import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.Model.Main.mainLogger;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static Logger logger = Logger.getLogger(ExceptionHandler.class.getSimpleName());
    static
    {
        logger.addHandler(main.java.Model.Main.warning);
        logger.addHandler(main.java.Model.Main.common);
    }

    @Override
    public void uncaughtException(Thread aThread, Throwable aThrowable) {
        logger.log(Level.SEVERE, "An unexpected exception! ", aThrowable);
    }
}
