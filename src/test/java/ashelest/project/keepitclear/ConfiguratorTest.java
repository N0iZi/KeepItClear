package ashelest.project.keepitclear;

import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.assertTrue;

public class ConfiguratorTest {

    @Test
    public void setCleanerRemoverTest() {
        Configurator configurator = new Configurator();
        configurator.setCleaner(Configurator.CleanerTypes.REMOVER);
        assertTrue(configurator.getCleaner().cleanUp(1, true));
    }

    @Test
    public void setCleanerMoverLocalTest() {
        Configurator configurator = new Configurator();
        configurator.setCleaner(Configurator.CleanerTypes.MOVER_LOCAL, "TEST_FOLDER_2_PATH");
        assertTrue(configurator.getCleaner().cleanUp(1, true));
    }

    @Test
    public void setCleanerMoverDropboxTest() {
        Configurator configurator = new Configurator();
        configurator.setCleaner(Configurator.CleanerTypes.MOVER_DROPBOX, "E-MAIL", "PASSWORD");
        assertTrue(configurator.getCleaner().cleanUp(1, true));
    }

    @Test
    public void setCleanerMoverGoogleDriveTest() {
        Configurator configurator = new Configurator();
        configurator.setCleaner(Configurator.CleanerTypes.MOVER_GOOGLEDRIVE, "GOOGLE_DRIVE_FOLDER_ID");
        assertTrue(configurator.getCleaner().cleanUp(1, true));
    }

    @Test
    public void writeConfigurationTest() {
        Configurator configurator = new Configurator();
        configurator.setCleaner(Configurator.CleanerTypes.MOVER_GOOGLEDRIVE, "GOOGLE_DRIVE_FOLDER_ID");
        configurator.setPeriod(30);
        configurator.setSinceLastAccess(14);
        configurator.setSinceDate(new Date(649874654));
        assertTrue(configurator.writeConfiguration());
    }

    @Test
    public void readConfigurationTest() {
        Configurator configurator = new Configurator();
        assertTrue(configurator.readConfiguration());
    }
}
