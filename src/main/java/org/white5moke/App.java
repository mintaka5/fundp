package org.white5moke;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.white5moke.util.Messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

enum COMMANDS {
    START("start"),
    STOP("stop"),
    EXIT("exit");

    String name;

    COMMANDS(String name) {
        this.name = name;
    }
}

public class App {
    private static InputStreamReader isr = new InputStreamReader(System.in);
    private static BufferedReader reader = new BufferedReader(isr);
    private static Node node;

    public static void main(String[] args) throws IOException, IllegalArgumentException {
        System.out.println(Messaging.Colour.BRIGHT_MAGENTA.get() + "----- client started -----" + Messaging.Colour.RESET.get());
        System.out.print(Messaging.Colour.GREEN.get() + ">> " + Messaging.Colour.RESET.get());

        while (true) {
            List<String> wordList = Arrays.asList(StringUtils.split(reader.readLine()));

            String firstWord = wordList.get(0);
            boolean isCommandWord = EnumUtils.isValidEnumIgnoreCase(COMMANDS.class, firstWord);
            if (isCommandWord) doCommands(wordList);
            else doInput(wordList);

            System.out.print(Messaging.Colour.GREEN.get() + ">> " + Messaging.Colour.RESET.get());
        }
    }

    private static void doInput(List<String> wl) {
        String t = wl.stream().reduce("", (p, e) -> p.concat(e+" "));
        System.out.println(Messaging.Colour.BRIGHT_YELLOW.get() + "<< " + t + Messaging.Colour.RESET.get());

    }

    private static void doCommands(List<String> wl) throws IOException {
        String cmdWord = wl.get(0).trim();

        final COMMANDS cmds = null;
        switch (COMMANDS.valueOf(cmdWord.toUpperCase())) {
            case EXIT -> {
                System.out.println(Messaging.Colour.BRIGHT_MAGENTA.get() + "----- client exiting -----" + Messaging.Colour.RESET.get());
                reader.close();
                System.exit(0);
            }
            case START -> {
                doStartCommand(wl);
            }
            case STOP -> {
                System.out.println(Messaging.Colour.BRIGHT_MAGENTA.get() + "----- node stopped -----" + Messaging.Colour.RESET.get());
            }
        }
    }

    public static void doStartCommand(List<String> wl) throws IOException {
        List<String> subWL = wl.subList(1, wl.size());

        if(subWL.size() != 1) {
            System.out.println(Messaging.Colour.BRIGHT_RED.get() + "***** did not provide enough arguments (start <port number>) *****" + Messaging.Colour.RESET.get());
            return;
        }

        int port = Integer.valueOf(subWL.get(0).trim());

        node = new Node();
        node.start();

        System.out.println(Messaging.Colour.BRIGHT_MAGENTA.get() + "----- node started -----" + Messaging.Colour.RESET.get());
    }
}
