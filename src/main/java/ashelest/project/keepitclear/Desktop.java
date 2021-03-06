package ashelest.project.keepitclear;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.filechooser.FileSystemView;

/**
 * Klassa dla otrzymania listy plików z "Pulpitu"
 * @author Andriy Shelest
 * @version 1.0
 */

public class Desktop {

    /**
     * Metoda dla otrzymania tablicy plików które znajdują się na "Pulpicie"
     * @return tablica plików z "Pulpitu"
     */
    private File[] getFilesList() {
        /*
        FOR CLEANER CLASSES TESTS!
        1. Comment first row
        2. Uncomment second row
         */
        File desktopLocation = FileSystemView.getFileSystemView().getHomeDirectory();
        //File desktopLocation = new File(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/test");
        return desktopLocation.listFiles();
    }

    /**
     *
     * @param expired - pliki będą usunięte/przenoszone jeżeli nie były otwarte przez dany czas
     * @return lista plików dla czystki
     * @see Cleaner#cleanUp(long, boolean)
     * @throws IOException
     */
    public ArrayList<String> getFilesToDelete(long expired) throws IOException {
        ArrayList <String> filesToDelete = new ArrayList<>();
        File[] allFiles = getFilesList();
        Date date = new Date();
        long timeMillis = date.getTime();
        for (File file :
                allFiles) {
            if (file.isFile() && timeMillis - Files.readAttributes(file.toPath(), BasicFileAttributes.class).lastAccessTime().toMillis() > expired)
                filesToDelete.add(file.toString());
        }
        return filesToDelete;
    }

}
