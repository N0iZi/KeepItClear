package ashelest.project.keepitclear;

import org.junit.Test;

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
        configurator.setCleaner(Configurator.CleanerTypes.MOVER_GOOGLEDRIVE);
        assertTrue(configurator.getCleaner().cleanUp(1, true));
    }
}
