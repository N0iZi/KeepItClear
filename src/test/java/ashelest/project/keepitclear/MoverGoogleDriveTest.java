package ashelest.project.keepitclear;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class MoverGoogleDriveTest {

    public MoverGoogleDrive moverGoogleDrive = new MoverGoogleDrive("1eiUTbiQPKPigb6Zf-eW06Ks_Wvrs2fup");

    @Test
    public void cleanUpTest() {
        assertTrue(moverGoogleDrive.cleanUp(1, true));
    }

}
