package local_search.complement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StopExecuteTest {
    @Test void detieneAlLlegarAlLimite() {
        StopExecute stop = new StopExecute();
        assertFalse(stop.stopIterations(0, 5));
        assertFalse(stop.stopIterations(4, 5));
        assertTrue(stop.stopIterations(5, 5));
        assertTrue(stop.stopIterations(6, 5));
    }
}
