package ashelest.project.keepitclear;

import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;

public class Configurator {

    public enum CleanerTypes {
        NONE,
        REMOVER,
        MOVER_LOCAL,
        MOVER_DROPBOX,
        MOVER_GOOGLEDRIVE
    }

    private Cleaner cleaner;
    private String dropboxAccess;
    public CleanerTypes currentType;

    public Configurator() {
        currentType = CleanerTypes.NONE;
    }

    public Cleaner getCleaner() {
        return cleaner;
    }

    public void resetType() {
        currentType = CleanerTypes.NONE;
        cleaner = null;
    }

    public void setCleaner(CleanerTypes cleanerType) {
        switch (cleanerType) {
            case REMOVER:
                cleaner = new Remover();
                currentType = CleanerTypes.REMOVER;
                break;
            case MOVER_LOCAL:
                throw new IllegalArgumentException("Local folder for moving files was not defined.");
            case MOVER_DROPBOX:
                throw new IllegalArgumentException("Login and password were not entered.");
            case MOVER_GOOGLEDRIVE:
                cleaner = new MoverGoogleDrive();
                try {
                    ((MoverGoogleDrive) cleaner).getService();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An error occurred while trying to login to Google Drive.\nPlease, check ReadMe for instructions.", "KeepItClear: Google Drive login error!", JOptionPane.ERROR_MESSAGE);
                    cleaner = null;
                    return;
                }
                currentType = CleanerTypes.MOVER_GOOGLEDRIVE;
                break;
        }
    }

    public void setCleaner(CleanerTypes cleanerType, String localFolder) {
        switch (cleanerType) {
            case REMOVER:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
            case MOVER_LOCAL:
                setupMoverLocal(localFolder);
                currentType = CleanerTypes.MOVER_LOCAL;
                break;
            case MOVER_DROPBOX:
                throw new IllegalArgumentException("Login and password were not entered.");
            case MOVER_GOOGLEDRIVE:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
        }
    }

    public void setCleaner(CleanerTypes cleanerType, String login, String password) {
        switch (cleanerType) {
            case REMOVER:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
            case MOVER_LOCAL:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
            case MOVER_DROPBOX:
                if (setupDropbox(login, password)) {
                    cleaner = new MoverDropbox(dropboxAccess);
                    currentType = CleanerTypes.MOVER_DROPBOX;
                }
                break;
            case MOVER_GOOGLEDRIVE:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
        }
    }

    private boolean setupDropbox(String login, String password) {
        DbxAppInfo appInfo;
        try {
            appInfo = DbxAppInfo.Reader.readFromFile(new File(getClass().getClassLoader().getResource("appinfo.json").getFile()));
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Error reading <app-info-file>: " + ex.getMessage());
            return false;
        }
        DbxRequestConfig requestConfig = new DbxRequestConfig("shelest/keepitclear");
        DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withNoRedirect()
                .build();
        String authorizeUrl = webAuth.authorize(webAuthRequest);
        if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(authorizeUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String code = JOptionPane.showInputDialog(null, "Now application requires Dropbox OAuth2 key for access to your account. Please follow next steps\n" +
                "1. If Dropbox login page did not open go to: " + authorizeUrl + "\n" +
                "2. Click \"Allow\" (you might have to log in first);\n" +
                "3. Copy the authorization code to input field.\n", "KeepItClear: Dropbox authentication", JOptionPane.QUESTION_MESSAGE);
        if (code == null || code.equals("")) {
            JOptionPane.showMessageDialog(null, "Code was not entered. Login failed.", "KeepItClear: Dropbox auth error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        code = code.trim();

        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finishFromCode(code);
        } catch (DbxException e) {
            JOptionPane.showMessageDialog(null, "An error occurred during Dropbox login. Please, check ReadMe for instructions.", "KeepItClear: Dropbox auth error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        dropboxAccess = authFinish.getAccessToken();
        return true;
    }

    private void setupMoverLocal(String path) {
        cleaner = new MoverLocal(path);
    }

}
