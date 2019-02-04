package ashelest.project.keepitclear;

import com.dropbox.core.DbxRequestConfig;

import javax.swing.*;

public class Configurator {

    public enum CleanerTypes {
        REMOVER,
        MOVER_LOCAL,
        MOVER_DROPBOX,
        MOVER_GOOGLEDRIVE
    }

    private Cleaner cleaner;

    public void setCleaner(CleanerTypes cleanerType) {
        switch (cleanerType) {
            case REMOVER:
                cleaner = new Remover();
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
                }
                break;
        }
    }

    public void setCleaner(CleanerTypes cleanerType, String localFolder) {
        switch (cleanerType) {
            case REMOVER:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
            case MOVER_LOCAL:
                cleaner = new MoverLocal(localFolder);
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
                //TODO: Create Dropbox auth method and then method call here
            case MOVER_GOOGLEDRIVE:
                throw new IllegalArgumentException("Too much arguments for this cleaner type");
        }
    }

    private void setupMoverLocal(String path) {
        cleaner = new MoverLocal(path);
    }

}
