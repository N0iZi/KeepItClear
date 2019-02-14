package ashelest.project.keepitclear;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Klassa czystnika który przenosze pliki do folderu lokalnego
 * @see Cleaner
 * @author Andriy Shelest
 * @version 1.0
 */

public class MoverLocal extends Cleaner {

    /**
     * Ściężka do folderu lokalnego do którego będzie przenoszono pliki
     */
    private String folderPath;

    /**
     * Kontruktor klassy
     * @param path - ściężka do folderu lokalnego do którego pliki powinne być przenoszone
     */
    public  MoverLocal(String path) {
        super();
        this.folderPath = path;
    }

    /**
     * Metoda która realizuje działalność czystnika
     * @param expired - pliki będą usunięte/przenoszone jeżeli nie były otwarte przez dany czas
     * @param logEnabled - czy potrzebne logowanie. Jeżeli true, to wszystkie czynności będą zapisane
     * @return zwraca true jeżeli nie było błędów
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
            File toMove = new File(file);
            if (toMove.renameTo(new File(folderPath + "\\" + toMove.getName()))) {
                if (toLog) {
                    try {
                        super.logAction(file, LogRows.MOVED_LOCAL);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Unfortunately, something went wrong while logging!\nLogging if off now. Please check the folder with application is without \"Read only\" flag checked.", "KeepItClear: Logging error!", JOptionPane.ERROR_MESSAGE);
                        toLog = false;
                    }
                }
            } else {
                if (toLog) {
                    try {
                        super.logAction(file, LogRows.FAIL);
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
