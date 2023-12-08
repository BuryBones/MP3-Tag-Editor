package bb.Model;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static Logger logger = Logger.getLogger(ExceptionHandler.class.getSimpleName());
    static
    {
        logger.addHandler(Main.warning);
        logger.addHandler(Main.common);
    }

    @Override
    public void uncaughtException(Thread aThread, Throwable aThrowable) {
        logger.log(Level.SEVERE, "An unexpected exception! ", aThrowable);
        Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
        JOptionPane.showMessageDialog(activeWindow, "An unexpected problem has occurred!\n\rThe application may work incorrectly.",
                "Unexpected error!",JOptionPane.ERROR_MESSAGE);

    }
}
