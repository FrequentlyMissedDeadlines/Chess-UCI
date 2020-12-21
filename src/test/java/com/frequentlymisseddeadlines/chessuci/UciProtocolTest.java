package com.frequentlymisseddeadlines.chessuci;


import org.junit.Test;

import static org.junit.Assert.*;

public class UciProtocolTest {

    @Test
    public void testDummyMethod() {
        UciProtocol protocol = new UciProtocol("something");

        assertEquals("something", protocol.getSomething());
        assertEquals(1, protocol.dummyMethod(0));
    }
}
