package com.frequentlymisseddeadlines.chessuci;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor @Getter @EqualsAndHashCode
public class GoParameters {
    private final Optional<Integer> whiteTime;
    private final Optional<Integer> whiteTimeIncrement;
    private final Optional<Integer> blackTime;
    private final Optional<Integer> blackTimeIncrement;
    private final Optional<Integer> movesToGo;
    private final Optional<Integer> depth;
    private final Optional<Integer> nodes;
    private final Optional<Integer> mate;
    private final Optional<Integer> moveTime;
    private final Boolean infinite;
}
