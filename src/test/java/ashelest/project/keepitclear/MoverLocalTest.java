package ashelest.project.keepitclear;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class MoverLocalTest {

    public MoverLocal moverLocal = new MoverLocal("C:\\Users\\melom\\Desktop\\test2");

    @Test
    public void cleanUpTest() {
        assertTrue(moverLocal.cleanUp(1, true)); //Test was completed with file search in separated folder
    }

}
