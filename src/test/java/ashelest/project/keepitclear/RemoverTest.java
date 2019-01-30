package ashelest.project.keepitclear;


import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class RemoverTest {

    public Remover remover = new Remover();

    @Test
    public void cleanUpTest(){
        assertTrue(remover.cleanUp(1, true)); //Test was completed with file search in separated folder
    }

}
