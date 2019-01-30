package ashelest.project.keepitclear;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class Cleaner {

    public Desktop desktop;

    public enum LogRows {
        DELETED,
        MOVED_LOCAL,
        MOVED_CLOUD,
        FAIL
    }

    public Cleaner() {
        desktop = new Desktop();
    }

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

    public void logAction(String filepath, LogRows result) throws Exception {
        BufferedWriter bufferedWriter;
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        bufferedWriter = new BufferedWriter(new FileWriter("log.txt", true));
        switch (result) {
            case FAIL:
                bufferedWriter.write("[" + time + "] File " + filepath + " was not moved!\n");
                break;
            case DELETED:
                bufferedWriter.write("[" + time + "] File " + filepath + " deleted.\n");
                break;
            case MOVED_LOCAL:
                bufferedWriter.write("[" + time + "] File " + filepath + " moved to local folder.\n");
                break;
            case MOVED_CLOUD:
                bufferedWriter.write("[" + time + "] File " + filepath + " moved to the cloud.\n");
                break;
        }
        bufferedWriter.close();

    }

}
