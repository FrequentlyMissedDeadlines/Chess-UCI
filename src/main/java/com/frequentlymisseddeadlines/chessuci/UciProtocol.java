package com.frequentlymisseddeadlines.chessuci;

import lombok.Getter;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UciProtocol {
    public static final String STARTING_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Getter
    private final UciListener listener;

    private final Map<String, Consumer<String>> commands = new HashMap<>();

    protected PrintStream out = System.out;
    protected Scanner in;

    public UciProtocol(UciListener listener) {
        this.listener = listener;
        in = new Scanner(System.in);

        setupAllCommands();
        new Thread(this::uciProtocol).start();
    }

    protected UciProtocol(UciListener listener, PrintStream out) {
        this.listener = listener;
        this.out = out;
        in = new Scanner(System.in);
        setupAllCommands();
        new Thread(this::uciProtocol).start();
    }

    protected void setupAllCommands() {
        commands.put("uci", this::uci);
        commands.put("setoption", this::setOption);
        commands.put("isready", this::isReady);
        commands.put("position", this::position);
        commands.put("go", this::go);
    }

    protected void isReady(String notUsed) {
        listener.getReady();
        out.println("readyok");
    }

    private final Pattern patternWhiteTime = Pattern.compile(".*wtime (\\d+).*");
    private final Pattern patternWhiteTimeIncr = Pattern.compile(".*winc (\\d+).*");
    private final Pattern patternBlackTime = Pattern.compile(".*btime (\\d+).*");
    private final Pattern patternBlackTimeIncr = Pattern.compile(".*binc (\\d+).*");
    private final Pattern patternMovesToGo = Pattern.compile(".*movestogo (\\d+).*");
    private final Pattern patternDepth = Pattern.compile(".*depth (\\d+).*");
    private final Pattern patternNodes = Pattern.compile(".*nodes (\\d+).*");
    private final Pattern patternMate = Pattern.compile(".*mate (\\d+) .*");
    private final Pattern patternMovetime = Pattern.compile(".*movetime (\\d+).*");

    private final Pattern[] goNumeralPatterns = new Pattern[] {
            patternWhiteTime, patternWhiteTimeIncr, patternBlackTime, patternBlackTimeIncr,
            patternMovesToGo, patternDepth, patternNodes, patternMate, patternMovetime
    };

    //TODO Implement all parameters (searchmoves and ponder missing)
    protected void go(String params) {
        List<Optional<Integer>> options = Arrays.stream(goNumeralPatterns).map(pattern -> {
            Matcher matcher = pattern.matcher(params);
            Optional<Integer> option = Optional.empty();
            if (matcher.matches()) {
                option = Optional.of(Integer.parseInt(matcher.group(1)));
            }
            return option;
        }).collect(Collectors.toList());

        Boolean infinite = params.contains("infinite");

        GoParameters goParameters = new GoParameters(options.get(0),
                options.get(1),
                options.get(2),
                options.get(3),
                options.get(4),
                options.get(5),
                options.get(6),
                options.get(7),
                options.get(8),
                infinite);

        new Thread(() -> {
            String bestMove = listener.go(goParameters);
            out.println("bestmove " + bestMove);
        }).start();
    }

    final Pattern positionPattern = Pattern.compile("(?:fen (?<fen>.* \\d+ \\d+)|startpos)(?: moves (?<moves>.*))?");

    protected void position(String position) {
        String[] moves = new String[0];
        String startingPosition = STARTING_POSITION_FEN;

        Matcher matcher = positionPattern.matcher(position);

        if (matcher.matches()) {
            if (matcher.group("fen") != null) {
                startingPosition = matcher.group("fen");
            }
            if (matcher.group("moves") != null) {
                moves = matcher.group("moves").split(" ");
            }
        }

        listener.setPosition(startingPosition, moves);
    }

    protected void uci(String notUsed) {
        listener.onConnection();
        out.println("id name " + listener.getEngineName());
        out.println("id author " + listener.getAuthorName());
        Iterable<String> options = listener.listSupportedOptions();
        for (String option : options) {
            out.println("option " + option);
        }
        out.println("uciok");
    }

    protected void setOption(String option) {
        listener.setOptionValue(option);
    }

    protected void uciProtocol() {
        while(true) {
            String message = in.nextLine();
            String[] commandAndParams = message.split(" ", 2);
            String command = commandAndParams[0];
            String params = commandAndParams.length > 1 ? commandAndParams[1] : "";

            if (commands.containsKey(command)) {
                commands.get(command).accept(params);
            }
        }
    }
}
