package ru.kadetch;

import com.beust.jcommander.Parameter;

public class LoadHabrAppArgs {
    @Parameter(names = "--help", description = "справка", help = true)
    boolean help;

    @Parameter(names = "--url", description = "URL для загрузки")
//    String url = "http://habrahabr.ru";
    String url = "https://habr.com/ru";
//    String url = "https://habr.com/ru/all/";
}
