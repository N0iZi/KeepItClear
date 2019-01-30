package ashelest.project.keepitclear;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class MoverDropboxTest {

    public MoverDropbox moverDropbox = new MoverDropbox(""); //Enter OAuth2 token here

    @Test
    public void cleanUpTest() {
        assertTrue(moverDropbox.cleanUp(1, true));
    }

}
