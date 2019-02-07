package ashelest.project.keepitclear;

import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Configurator {

    public enum CleanerTypes {
        NONE,
        REMOVER,
        MOVER_LOCAL,
        MOVER_DROPBOX,
        MOVER_GOOGLEDRIVE
    }

    public boolean isOnDuty;

    private class CleanerTimerTask extends TimerTask {

        public void run() {
            if (currentType != CleanerTypes.NONE) {
                cleaner.cleanUp(sinceLastAccess * 86400000, toLog);
            }
        }

    }

    private Cleaner cleaner;
    private Timer timer;
    private String dropboxAccess;
    private String googleDriveFolderId;
    private String localMoverFolder;
    private int sinceLastAccess;
    private int period;
    private Date sinceDate;
    private boolean toLog;

    public CleanerTypes currentType;

    public Configurator() {
        currentType = CleanerTypes.NONE;
        isOnDuty = false;
    }

    public Cleaner getCleaner() {
        return cleaner;
    }

    public void resetType() {
        currentType = CleanerTypes.NONE;
        cleaner = null;
        dropboxAccess = null;
        localMoverFolder = null;
    }

    public boolean setTimer() {
        if (currentType != CleanerTypes.NONE) {
            timer.scheduleAtFixedRate(new CleanerTimerTask(), sinceDate, period * 86400000);
            isOnDuty = true;
            return true;
        }
        return false;
    }

    public void dropTimer() {
        timer.cancel();
        timer = null;
        isOnDuty = false;
    }

    public void setSinceLastAccess(int days) {
        sinceLastAccess = days;
    }

    public int getSinceLastAccess() {
        return sinceLastAccess;
    }

    public void setPeriod(int days) {
        period = days;
    }

    public int getPeriod(int days) {
        return period;
    }

    public void setSinceDate(Date date) {
        sinceDate = date;
    }

    public Date getSinceDate() {
        return sinceDate;
    }

    public void enableLog() {
        toLog = true;
    }

    public void disableLog() {
        toLog = false;
    }

    public boolean getToLog() {
        return toLog;
    }

    public String getGoogleDriveFolderId() {
        return googleDriveFolderId;
    }

    public String getLocalMoverFolder() {
        return localMoverFolder;
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
                throw new IllegalArgumentException("Folder id was not entered.");
        }
    }

    public void setCleaner(CleanerTypes cleanerType, String folder) {
        switch (cleanerType) {
            case REMOVER:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
            case MOVER_LOCAL:
                setupMoverLocal(folder);
                localMoverFolder = folder;
                currentType = CleanerTypes.MOVER_LOCAL;
                break;
            case MOVER_DROPBOX:
                throw new IllegalArgumentException("Login and password were not entered.");
            case MOVER_GOOGLEDRIVE:
                cleaner = new MoverGoogleDrive(folder);
                try {
                    ((MoverGoogleDrive) cleaner).getService();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An error occurred while trying to login to Google Drive.\nPlease, check ReadMe for instructions.", "KeepItClear: Google Drive login error!", JOptionPane.ERROR_MESSAGE);
                    cleaner = null;
                    return;
                }
                googleDriveFolderId = folder;
                currentType = CleanerTypes.MOVER_GOOGLEDRIVE;
                break;
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

    public boolean writeConfiguration() {
        JSONObject configurationJson = new JSONObject();
        switch (currentType) {
            case NONE:
                configurationJson.put("type", "NONE");
                break;
            case REMOVER:
                configurationJson.put("type", "REMOVER");
                break;
            case MOVER_LOCAL:
                configurationJson.put("type", "MOVER_LOCAL");
                configurationJson.put("folder", localMoverFolder);
                break;
            case MOVER_GOOGLEDRIVE:
                configurationJson.put("type", "MOVER_GOOGLEDRIVE");
                configurationJson.put("folderId", googleDriveFolderId);
                break;
            case MOVER_DROPBOX:
                configurationJson.put("type", "MOVER_DROPBOX");
                configurationJson.put("access", dropboxAccess);
                break;
        }
        if (currentType != CleanerTypes.NONE) {
            configurationJson.put("period", period);
            configurationJson.put("sinceLastAccess", sinceLastAccess);
            configurationJson.put("sinceDate", sinceDate.getTime());
        }
        try {
            Files.write(Paths.get("configuration.json"), configurationJson.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't write settings to file!", "KeepItClear: Internal error!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean readConfiguration() {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) jsonParser.parse(new FileReader("configuration.json"));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not read settings, or file \"configuration.json\" does not exist.", "KeepItClear: Internal error!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        switch ((String) jsonObject.get("type")) {
            case "NONE":
                resetType();
                break;
            case "REMOVER":
                resetType();
                setCleaner(CleanerTypes.REMOVER);
                break;
            case "MOVER_LOCAL":
                resetType();
                localMoverFolder = (String) jsonObject.get("folder");
                setCleaner(CleanerTypes.MOVER_LOCAL, localMoverFolder);
                break;
            case "MOVER_GOOGLEDRIVE":
                resetType();
                googleDriveFolderId = (String) jsonObject.get("folderId");
                setCleaner(CleanerTypes.MOVER_GOOGLEDRIVE, googleDriveFolderId);
                break;
            case "MOVER_DROPBOX":
                resetType();
                dropboxAccess = (String) jsonObject.get("access");
                cleaner = new MoverDropbox(dropboxAccess);
                currentType = CleanerTypes.MOVER_DROPBOX;
                break;
        }
        period = ((Long) jsonObject.get("period")).intValue();
        sinceLastAccess = ((Long) jsonObject.get("sinceLastAccess")).intValue();
        sinceDate = new Date((Long) jsonObject.get("sinceDate"));
        return true;
    }

}
