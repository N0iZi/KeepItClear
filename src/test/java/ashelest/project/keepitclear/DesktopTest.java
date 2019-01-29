package ashelest.project.keepitclear;

import org.junit.Test;
import static junit.framework.TestCase.fail;

public class DesktopTest {

    Desktop tested = new Desktop();

    @Test
    public void getFilesToDeleteTest() {
        try {
            System.out.println(tested.getFilesToDelete(86400000)); //Test: getting files which was not opened for 24 hours
        } catch (Exception e) {
            fail("EXCEPTION!");
        }

    }

}
