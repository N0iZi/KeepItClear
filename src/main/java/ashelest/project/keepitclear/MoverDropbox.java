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

/**
 * Klassa czystnika który przenosze pliki do chmury danych Dropbox
 * @see Cleaner
 * @author Andriy Shelest
 * @version 1.0
 */
public class MoverDropbox extends Cleaner {

    /**
     * Klucz dla powtórnego dostępu do konta Dropbox
     * @see MoverDropbox#login()
     */
    private String ACCESS_TOKEN;

    /**
     * Konstruktor klassy
     * @param token - Klucz dostępu do konta Dropbox
     */
    public MoverDropbox(String token) {
        super();
        this.ACCESS_TOKEN = token;
    }

    /**
     * Autoryzacja w serwisie Dropbox
     * @return Klassa dla wyzwań z Dropbox API. Dokładniej: <a href="https://dropbox.github.io/dropbox-sdk-java/api-docs/v2.1.x/">Dokumentacja Dropbox API</a>
     * @throws DbxException
     */
    private DbxClientV2 login() throws DbxException {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("shelest/keepitclear").build();
        return new DbxClientV2(config, ACCESS_TOKEN);
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
                    uploadFailed = false;
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
