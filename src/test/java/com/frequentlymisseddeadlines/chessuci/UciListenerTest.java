package com.frequentlymisseddeadlines.chessuci;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

public class UciListenerTest {
    UciListener tested = new UciListener() {
        @Override
        public String getEngineName() {
            return null;
        }

        @Override
        public String getAuthorName() {
            return null;
        }

        @Override
        public void setPosition(String initialPosition, String[] moves) {

        }

        @Override
        public String go(GoParameters parameters) {
            return null;
        }
    };

    // Empty default methods, nothing to test, just for coverage...
    @Test
    public void testEmptyDefaultMethods() {
        tested.onConnection();
        tested.setOptionValue("option");
        tested.getReady();
    }

    @Test
    public void testListSupportedOptions() {
        assertFalse(tested.listSupportedOptions().iterator().hasNext());
    }

}
