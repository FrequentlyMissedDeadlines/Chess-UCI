package com.frequentlymisseddeadlines.chessuci;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@RunWith(MockitoJUnitRunner.class)
public class UciProtocolTest extends UciProtocol {

    @Mock
    UciListener mockedListener;

    @Mock
    PrintStream mockedOut;

    UciProtocol tested = null;

    @Before
    public void setup() {
        tested = new UciProtocol(mockedListener);
        tested.out = mockedOut;

        when(mockedListener.getAuthorName()).thenReturn("Mocked Author");
        when(mockedListener.getEngineName()).thenReturn("Mocked Engine name");

        List<String> supportedOptions = new LinkedList<>();
        supportedOptions.add("Option 1");
        supportedOptions.add("Option 2");
        when(mockedListener.listSupportedOptions()).thenReturn(supportedOptions);
    }

    @Test
    public void testUci() {
        verify(mockedListener, never()).onConnection();
        verify(mockedOut, never()).println(ArgumentMatchers.anyString());

        tested.uci("");

        verify(mockedListener, times(1)).onConnection();
        verify(mockedOut, times(1)).println("id name Mocked Engine name");
        verify(mockedOut, times(1)).println("id author Mocked Author");
        verify(mockedOut, times(1)).println("option Option 1");
        verify(mockedOut, times(1)).println("option Option 2");
    }

    @Test
    public void testIsReady() {
        verify(mockedListener, never()).getReady();
        verify(mockedOut, never()).println(ArgumentMatchers.anyString());

        tested.isReady("");

        verify(mockedListener, times(1)).getReady();
        verify(mockedOut, times(1)).println("readyok");
    }

    @Test
    public void testSetOption() {
        verify(mockedListener, never()).setOptionValue(ArgumentMatchers.anyString());

        tested.setOption("name mocked option value 12");

        verify(mockedListener, times(1)).setOptionValue("name mocked option value 12");
    }

    @Test
    public void testPosition() {
        verify(mockedListener, never()).setPosition(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        tested.position("startpos moves move1 move2");
        String[] expectedMoves = new String[] {"move1", "move2"};
        verify(mockedListener, times(1)).setPosition(STARTING_POSITION_FEN, expectedMoves);

        tested.position("fen rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1 moves move1 move2");
        verify(mockedListener, times(1)).setPosition("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", expectedMoves);

        tested.position("startpos");
        verify(mockedListener, times(1)).setPosition(STARTING_POSITION_FEN, new String[0]);

        tested.position("fen rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PP/RNBQKBNR b KQkq e3 0 1");
        verify(mockedListener, times(1)).setPosition("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PP/RNBQKBNR b KQkq e3 0 1", new String[0]);

        tested.position("blablabla");
        verify(mockedListener, times(2)).setPosition(STARTING_POSITION_FEN, new String[0]);
    }

    @Test
    public void testGo() throws InterruptedException {
        final int whiteTime = 100;
        final int whiteIncr = 1;
        final int blackTime = 200;
        final int blackIncr = 2;
        final int movestogo = 10;
        final int depth = 5;
        final int nodes = 10000;
        final int mate = 7;
        final int movetime = 50;

        GoParameters expected = new GoParameters(
                Optional.of(whiteTime),
                Optional.of(whiteIncr),
                Optional.of(blackTime),
                Optional.of(blackIncr),
                Optional.of(movestogo),
                Optional.of(depth),
                Optional.of(nodes),
                Optional.of(mate),
                Optional.of(movetime),
                Boolean.TRUE
        );

        GoParameters emptyValues = new GoParameters(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Boolean.FALSE
        );

        verify(mockedListener, never()).go(ArgumentMatchers.any());

        tested.go("mate " + mate + " movetime " + movetime + " infinite " + " wtime " + whiteTime + " btime "
                + blackTime + " winc " + whiteIncr + " binc " + blackIncr + " movestogo " + movestogo
                + " nodes " + nodes + " depth " + depth);

        Thread.sleep(10);
        verify(mockedListener, times(1)).go(ArgumentMatchers.eq(expected));

        tested.go("");
        Thread.sleep(10);
        verify(mockedListener, times(1)).go(ArgumentMatchers.eq(emptyValues));

        tested.go("mate movetime wtime");
        Thread.sleep(10);
        verify(mockedListener, times(2)).go(ArgumentMatchers.eq(emptyValues));
    }

    @Test
    public void testUciProtocol() throws InterruptedException {
        String data = "uci\nunknown command\nsetoption name Ponder value false\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        new UciProtocol(mockedListener, mockedOut);
        Thread.sleep(10);

        verify(mockedListener, times(1)).onConnection();
        verify(mockedOut, times(1)).println("id name Mocked Engine name");
        verify(mockedOut, times(1)).println("id author Mocked Author");
        verify(mockedOut, times(1)).println("option Option 1");
        verify(mockedOut, times(1)).println("option Option 2");
        verify(mockedListener, times(1)).setOptionValue("name Ponder value false");
    }

    public UciProtocolTest() {
        super(null);
    }
}
