package ashelest.project.keepitclear;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
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

    public abstract boolean cleanUp(long expired, boolean logEnabled);

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
