package ashelest.project.keepitclear;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Remover extends Cleaner {

    public Remover() {
        super();
    }

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
