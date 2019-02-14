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

/**
 * Klassa która kontroluje dzałalność czystnika
 * @author Andriy Shelest
 * @version 1.0
 */

public class Configurator {

    /**
     * Enumeracja typów czystników
     * @see Configurator#setCleaner(CleanerTypes)
     * @see Configurator#setCleaner(CleanerTypes, String)
     * @see Configurator#setCleaner(CleanerTypes, String, String)
     */
    public enum CleanerTypes {
        NONE,
        REMOVER,
        MOVER_LOCAL,
        MOVER_DROPBOX,
        MOVER_GOOGLEDRIVE
    }

    /**
     * Atrybuta która pokazuje czy jest czystnik włączony
     */
    public boolean isOnDuty;

    /**
     * Klassa wewnętrzna która realizuje harmonogram startów czystki
     * @see TimerTask
     */
    private class CleanerTimerTask extends TimerTask {

        /**
         * Start czystki
         * @see Cleaner
         */
        public void run() {
            if (currentType != CleanerTypes.NONE) {
                cleaner.cleanUp(sinceLastAccess * 86400000, toLog);
            }
        }

    }

    /**
     * Element klassy czystnika
     */
    private Cleaner cleaner;
    /**
     * Element klassy Timer
     * @see Timer
     */
    private Timer timer;
    /**
     * Klucz dostępu do konta Dropbox
     * @see MoverDropbox
     */
    private String dropboxAccess;
    /**
     * ID folderu na Google Dysk do którego pliki muszą być przenoszone
     * @see MoverGoogleDrive
     */
    private String googleDriveFolderId;
    /**
     * Ściężka do folderu lokalnego do którego pliki powinne być przenoszone
     * @see MoverLocal
     */
    private String localMoverFolder;
    /**
     * Czas "nieotwerania" pliku po którym plik musi być usunięty/przenoszony
     */
    private int sinceLastAccess;
    /**
     * Period czystki
     */
    private int period;
    /**
     * Dzień startowy dla harmonogramu czystki
     * @see Configurator#setTimer()
     */
    private Date sinceDate;
    /**
     * Wyznacza czy potrzebne logowanie
     * @see Cleaner
     */
    private boolean toLog;

    /**
     * Typ terażniejszego czystnika
     */
    public CleanerTypes currentType;

    /**
     * Konstrukror klassy
     */
    public Configurator() {
        currentType = CleanerTypes.NONE;
        isOnDuty = false;
        toLog = false;
    }

    /**
     * Zwraca element klassy czystnika
     * @return element klassy czystnika
     */
    public Cleaner getCleaner() {
        return cleaner;
    }

    /**
     * Zrzut klassy czystnika
     */
    public void resetType() {
        currentType = CleanerTypes.NONE;
        cleaner = null;
        dropboxAccess = null;
        localMoverFolder = null;
    }

    /**
     * Ustawienie harmonogramu czystki i włączenie czystnika
     * @return rezultat operacji
     */
    public boolean setTimer() {
        if (currentType != CleanerTypes.NONE) {
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new CleanerTimerTask(), sinceDate, Long.valueOf(period) * 86400000);
            isOnDuty = true;
            return true;
        }
        return false;
    }

    /**
     * Zrzut harmonogramu czystki i wyłączenie czystnika
     */
    public void dropTimer() {
        timer.cancel();
        timer = null;
        isOnDuty = false;
    }

    /**
     * Ustawienie czas "nieotwerania" pliku po którym plik musi być usunięty/przenoszony
     * @param days - czas "nieotwerania" pliku po którym plik musi być usunięty/przenoszony (dni)
     */
    public void setSinceLastAccess(int days) {
        sinceLastAccess = days;
    }

    /**
     * Otrzymanie czasu "nieotwerania" pliku po którym plik musi być usunięty/przenoszony
     * @return czas "nieotwerania" pliku po którym plik musi być usunięty/przenoszony (dni)
     */
    public int getSinceLastAccess() {
        return sinceLastAccess;
    }

    /**
     * Ustalenie periodu czystki
     * @param days - period czystki (dni)
     */
    public void setPeriod(int days) {
        period = days;
    }

    /**
     * Otrzymanie periodu czystki
     * @return period czystki (dni)
     */
    public int getPeriod() {
        return period;
    }

    /**
     * Ustalenie dnia startowy dla harmonogramu czystki
     * @param date - dzień startowy dla harmonogramu czystki (data)
     */
    public void setSinceDate(Date date) {
        sinceDate = date;
    }

    /**
     * Otrzymanie dnia startowego dla harmonogramu czystki
     * @return dzień startowy dla harmonogramu czystki (data)
     */
    public Date getSinceDate() {
        return sinceDate;
    }

    /**
     * Włączenie logowania
     */
    public void enableLog() {
        toLog = true;
    }

    /**
     * Wyłączenie logowania
     */
    public void disableLog() {
        toLog = false;
    }

    /**
     * Otrymanie znaczenia czy logowanie włączono
     * @return czy logowanie włączono
     */
    public boolean getToLog() {
        return toLog;
    }

    public void setToLog(boolean value) {
        toLog = value;
    }

    /**
     * Otrzymaie ID folderu na Google Dysk do którego pliki muszą być przenoszone
     * @return ID folderu na Google Dysk do którego pliki muszą być przenoszone
     */
    public String getGoogleDriveFolderId() {
        return googleDriveFolderId;
    }

    /**
     * Otrzymanie ściężki do folderu lokalnego do którego pliki powinne być przenoszone
     * @return ściężka do folderu lokalnego do którego pliki powinne być przenoszone
     */
    public String getLocalMoverFolder() {
        return localMoverFolder;
    }

    /**
     * Ustalienie ściężka do folderu lokalnego do którego pliki powinne być przenoszone
     * @param path - ściężka do folderu lokalnego do którego pliki powinne być przenoszone
     */
    public void setLocalMoverFolder(String path) {
        localMoverFolder = path;
    }

    /**
     * Otrymanie klucza dostępu do konta Dropbox
     * @return klucz dostępu do konta Dropbox
     */
    public String getDropboxAccess() {
        return dropboxAccess;
    }

    /**
     * Ustalenie typu czystnika
     * @param cleanerType - typ czystnika
     */
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

    /**
     * Ustalenie typu czystnika
     * @param cleanerType - czystnkika
     * @param folderOrKey - ściężka do folderu lokalnego lub ID folderu Google Dysk lub klucz dostępu do konta Dropbox
     */
    public void setCleaner(CleanerTypes cleanerType, String folderOrKey) {
        switch (cleanerType) {
            case REMOVER:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
            case MOVER_LOCAL:
                setupMoverLocal(folderOrKey);
                localMoverFolder = folderOrKey;
                currentType = CleanerTypes.MOVER_LOCAL;
                break;
            case MOVER_DROPBOX:
                cleaner = new MoverDropbox(dropboxAccess);
                currentType = CleanerTypes.MOVER_DROPBOX;
                break;
            case MOVER_GOOGLEDRIVE:
                cleaner = new MoverGoogleDrive(folderOrKey);
                try {
                    ((MoverGoogleDrive) cleaner).getService();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An error occurred while trying to login to Google Drive.\nPlease, check ReadMe for instructions.", "KeepItClear: Google Drive login error!", JOptionPane.ERROR_MESSAGE);
                    cleaner = null;
                    return;
                }
                googleDriveFolderId = folderOrKey;
                currentType = CleanerTypes.MOVER_GOOGLEDRIVE;
                break;
        }
    }

    /**
     * Ustalenie typu czystnika
     * @param cleanerType - typ czystnika
     * @param login - imię użutkownika Dropbox
     * @param password - hasło do konta Dropbox
     */
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

    /**
     * Ustalenie pierwszego dostępu do konta Dropbox
     * @param login - imię użutkownika Dropbox
     * @param password - hasło do konta Dropbox
     * @return rezultat proby otrzymania pierwszego dostępu
     */
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

    /**
     * Metoda dodatkowa dla ustalenie czystnika który przenosze pliki do folderu lokalnego
     * @param path
     */
    private void setupMoverLocal(String path) {
        cleaner = new MoverLocal(path);
    }

    /**
     * Zapisywanie konfiguracji do pliku JSON
     * @return rezultat zapisywania
     */
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
            configurationJson.put("toLog", toLog);
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

    /**
     * Odczyt konfiguracji z pliku JSON
     * @return rezultat odczytu konfiguracji
     */
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
        toLog = (Boolean) jsonObject.get("toLog");
        return true;
    }

}
