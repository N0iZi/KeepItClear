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

public class MoverGoogleDrive extends Cleaner {

    private String APPLICATION_NAME = "KeepItClear";
    private JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private String TOKENS_DIRECTORY_PATH = "tokens";

    private List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private String CREDENTIALS_FILE_PATH = "/credentials.json"; //credentials.json file is required to be created through Google Developers site and copied to src/main/java/resources folder

    public boolean isLoggedIn;

    public MoverGoogleDrive() {
        super();
        isLoggedIn = false;
    }

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

    public Drive getService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
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
            fileMetadata.setName("keepitclear/" + filePath.getName());
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
