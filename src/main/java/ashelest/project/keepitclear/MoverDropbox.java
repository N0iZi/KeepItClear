package ashelest.project.keepitclear;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.ArrayList;

public class MoverDropbox extends Cleaner {

    private String ACCESS_TOKEN;

    public MoverDropbox(String token) {
        super();
        this.ACCESS_TOKEN = token;
    }

    private DbxClientV2 login() throws DbxException {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("shelest/keepitclear").build();
        return new DbxClientV2(config, ACCESS_TOKEN);
    }

    @Override
    public boolean cleanUp(long expired, boolean logEnabled) {
        ArrayList<String> files;
        boolean result = true;
        boolean toLog = logEnabled;
        boolean uploadFailed = false;
        try {
            files = desktop.getFilesToDelete(expired);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        DbxClientV2 client;
        try {
            client = login();
        } catch (DbxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unfortunately, login to Dropbox was failed. Please, check login data.", "KeepItClear: Dropbox login error!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        for (String file :
                files) {
            try (InputStream in = new FileInputStream(file)) {
                FileMetadata metadata = client.files().uploadBuilder("/keepitclear/" + new File(file).getName()).uploadAndFinish(in);
            } catch (Exception e) {
                e.printStackTrace();
                uploadFailed = true;
            }
            if (toLog) {
                if (uploadFailed) {
                    try {
                        super.logAction(file, LogRows.FAIL);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Unfortunately, something went wrong while logging!\nLogging if off now. Please check the folder with application is without \"Read only\" flag checked.", "KeepItClear: Logging error!", JOptionPane.ERROR_MESSAGE);
                        toLog = false;
                    }
                    result = false;
                } else {
                    try {
                        super.logAction(file, LogRows.MOVED_CLOUD);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Unfortunately, something went wrong while logging!\nLogging if off now. Please check the folder with application is without \"Read only\" flag checked.", "KeepItClear: Logging error!", JOptionPane.ERROR_MESSAGE);
                        toLog = false;
                    }
                    new File(file).delete();
                }
            }
        }
        return result;
    }

}
