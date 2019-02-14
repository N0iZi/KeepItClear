package ashelest.project.keepitclear;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Andriy Shelest
 * @version 1.0
 * Klassa abstrakcyjna dla realizacji działania czystników
 */

public abstract class Cleaner {

    /**
     * Element klassy Desktop
     * @see Desktop
     */
    public Desktop desktop;

    /**
     * Enumeracja typów zapisów do pliku .log
     * @see Cleaner#logAction(String, LogRows)
     */
    public enum LogRows {
        DELETED,
        MOVED_LOCAL,
        MOVED_CLOUD,
        FAIL
    }

    /**
     * Konstruktor klassy
     */
    public Cleaner() {
        desktop = new Desktop();
    }

    /**
     * Metoda abstrakcyjna dla działalności podtawowej czystnika
     * @param expired - pliki będą usunięte/przenoszone jeżeli nie były otwarte przez dany czas
     * @param logEnabled - czy potrzebne logowanie. Jeżeli true, to wszystkie czynności będą zapisane
     * @return zwraca true jeżeli nie było błędów
     */
    public abstract boolean cleanUp(long expired, boolean logEnabled);

    /**
     * Metoda logowania działalności czystnika
     * @param filepath - ściężka do pliku z którym było wykonano probe usunięcia/przenoszenia
     * @param result - rezultat proby usunięcia/przenoszenia pliku
     * @throws Exception
     */
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
