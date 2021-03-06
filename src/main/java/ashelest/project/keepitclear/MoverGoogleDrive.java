package ashelest.project.keepitclear;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.client.http.FileContent;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Klassa czystnika który przenosze pliki do chmury danych Google Dysk
 */

public class MoverGoogleDrive extends Cleaner {


    private String APPLICATION_NAME = "KeepItClear";
    private JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /**
     * Ściężka do kluczów dla dostępu do konta Google
     */
    private String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Obwód zezwoleń dla działalnośći w chmurie danych Google Dysk
     */
    private List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    /**
     * Plik z ID aplikacji w Google Developers Console
     */
    private String CREDENTIALS_FILE_PATH = "/credentials.json"; //credentials.json file is required to be created through Google Developers site and copied to src/main/java/resources folder

    /**
     * ID folderu na Google Dysk do którego pliki będą przenoszone
     */
    private String folderId;

    /**
     * Konstruktor klassy
     * @param driveFolderId - ID folderu na Google Dysk do którego pliki muszą być przenoszone
     */
    public MoverGoogleDrive(String driveFolderId) {
        super();
        this.folderId = driveFolderId;
    }

    /**
     * Autoryzacja aplikacji w serwisach Google
     * @param HTTP_TRANSPORT - element klassy dla transportu danych przez HTTP
     * @see <a href="https://developers.google.com/api-client-library/java/google-http-java-client/reference/1.20.0/com/google/api/client/http/HttpTransport">Dokumentacja Google APIs</a>
     * @return dane dostępu do Google APIs dla terażniejszej sesji
     * @throws IOException
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = MoverGoogleDrive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Otrymanie dostępu do konta Google Dysk
     * @return element klassy dla wezwań do Google Dysk API
     * @see <a href="https://developers.google.com/resources/api-libraries/documentation/drive/v3/java/latest/com/google/api/services/drive/Drive.html">Dokumentacja Google Dysk API</a>
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public Drive getService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
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
        Drive service;
        try {
            service = getService();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        for (String file :
                files) {
            File fileMetadata = new File();
            java.io.File filePath = new java.io.File(file);
            fileMetadata.setName(filePath.getName());
            fileMetadata.setParents(Collections.singletonList(folderId));
            FileContent mediaContent = new FileContent(null, filePath);
            try {
                File uploadFile = service.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
            } catch (IOException e) {
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
                    new java.io.File(file).delete();
                }
            }
        }
        return result;
    }

}
