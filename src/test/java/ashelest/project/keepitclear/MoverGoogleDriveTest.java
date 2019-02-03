package ashelest.project.keepitclear;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class MoverGoogleDriveTest {

    public MoverGoogleDrive moverGoogleDrive = new MoverGoogleDrive();

    @Test
    public void cleanUpTest() {
        assertTrue(moverGoogleDrive.cleanUp(1, true));
    }

}
