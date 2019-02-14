package ashelest.project.keepitclear;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Klassa czystnika który usuwa pliki
 * @see Cleaner
 * @author Andriy Shelest
 * @version 1.0
 */

public class Remover extends Cleaner {

    /**
     * Konstruktor klassy
     */
    public Remover() {
        super();
    }

    /**
     * Metoda która realizuje działalność czystnika który usuwa pliki
     * @param expired - pliki będą usunięte/przenoszone jeżeli nie były otwarte przez dany czas
     * @param logEnabled - czy potrzebne logowanie. Jeżeli true, to wszystkie czynności będą zapisane
     * @return zwraca true jeżeli nie było błędów
     * @see Cleaner#cleanUp(long, boolean)
     */
    @Override
    public boolean cleanUp(long expired, boolean logEnabled) {
        ArrayList<String> files;
        boolean result = true;
        boolean toLog = logEnabled;
        try {
            files = desktop.getFilesToDelete(expired);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        for (String file :
                files) {
            File toDelete = new File(file);
            if (toDelete.delete()) {
                if (toLog) {
                    try {
                        logAction(file, LogRows.DELETED);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Unfortunately, something went wrong while logging!\nLogging if off now. Please check the folder with application is without \"Read only\" flag checked.", "KeepItClear: Logging error!", JOptionPane.ERROR_MESSAGE);
                        toLog = false;
                    }
                }
            } else {
                if (toLog) {
                    try {
                        logAction(file, LogRows.FAIL);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Unfortunately, something went wrong while logging!\nLogging if off now. Please check the folder with application is without \"Read only\" flag checked.", "KeepItClear: Logging error!", JOptionPane.ERROR_MESSAGE);
                        toLog = false;
                    }
                }
                result = false;
            }
        }
        return result;
    }

}
