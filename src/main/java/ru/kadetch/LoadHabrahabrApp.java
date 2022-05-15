package ru.kadetch;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class LoadHabrahabrApp {

    public static void main(String[] args) {

        final LoadHabrAppArgs commandLineArgs = new LoadHabrAppArgs();
        final JCommander commander = JCommander
                .newBuilder()
                .addObject(commandLineArgs)
                .build();

        commander.parse(args);

        if (commandLineArgs.help) {
            commander.usage();
        }

        Grabber habrahabr = new Grabber(commandLineArgs.url);
        habrahabr.start();
    }
}
