package com.frequentlymisseddeadlines.chessuci;

import lombok.Generated;

import java.util.LinkedList;

/**
 * Implement this interface to branch your engine on UCI protocol events
 */
public interface UciListener {

    /**
     * This method is called when a GUI get connected to your Chess Engine
     */
    default void onConnection() {

    }

    /**
     * Return your Chess Engine Name. It may be displayed in the GUI.
     * @return chess engine name
     */
    String getEngineName();

    /**
     * Return your name. It may be displayed in the GUI.
     * @return your name
     */
    String getAuthorName();


    /**
     * List all options that are supported by your engine. The GUI will send you back their values if they are supported.
     * For example:
     * <ul>
     *     <li>name NalimovPath type string default <empty></li>
     *     <li>name ClearHash type button</li>
     *     <li>name Nullmove type check default true</li>
     * </ul>
     * More details <a href="http://wbec-ridderkerk.nl/html/UCIProtocol.html">in the specs</a>.
     * @return list of options supported by the engine
     */
    default Iterable<String> listSupportedOptions() {
        return new LinkedList<>();
    }

    /**
     * This method is called when an option value is returned by the GUI. Feel free to use it or not in your Engine.
     * Example value: <code>name NalimovCache value 32</code>
     * @param option option name and value
     */
    default void setOptionValue(String option) {

    }

    /**
     * This method is called to synchronize the GUI with your engine in case it provided some input that could be long to process by
     * the engine. If you are ready to proceed, simply return;
     */
    default void getReady() {

    }

    /**
     * This method is called when the position has changed. It provides the initial position and all moves done from it.
     * @param initialPosition the initial position in <a href="https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation">FEN notation</a>
     * @param moves an array of String containing all moves done from the starting position. The moves are in origin-destination notation.
     *              For example:
     *              <ul>
     *              <li>
     *                  e2e4
     *              </li>
     *              <li>
     *                  e5e7
     *              </li>
     *              </ul>
     */
    void setPosition(String initialPosition, String[] moves);


    /**
     * This method is called to tell the engine it should search for a move to play. Once it found one, it must
     * return it as a origin-destination String.
     * @param parameters the search parameters
     * @return
     */
    String go(GoParameters parameters);
}
