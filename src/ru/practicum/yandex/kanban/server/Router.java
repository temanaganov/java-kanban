package ru.practicum.yandex.kanban.server;

import com.sun.net.httpserver.HttpExchange;

import java.util.function.Consumer;

public class Router {
    HttpExchange httpExchange;

    public Router(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
        this.httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
    }

    public void get(String pattern, Consumer<HttpExchange> handler) {
        if (getMethod().equals("GET") && match(getPath(), pattern)) {
            handler.accept(httpExchange);
        }
    }

    public void post(String pattern, Consumer<HttpExchange> handler) {
        if (getMethod().equals("POST") && match(getPath(), pattern)) {
            handler.accept(httpExchange);
        }
    }

    public void put(String pattern, Consumer<HttpExchange> handler) {
        if (getMethod().equals("PUT") && match(getPath(), pattern)) {
            handler.accept(httpExchange);
        }
    }

    public void delete(String pattern, Consumer<HttpExchange> handler) {
        if (getMethod().equals("DELETE") && match(getPath(), pattern)) {
            handler.accept(httpExchange);
        }
    }

    private String getPath() {
        return httpExchange.getRequestURI().getPath();
    }

    private String getMethod() {
        return httpExchange.getRequestMethod();
    }

    private boolean match(String path, String pattern) {
        String[] pathParts = path.split("/");
        String[] patternParts = pattern.split("/");

        if (pathParts.length != patternParts.length) return false;

        for (int i = 0; i < patternParts.length; i++) {
            if (patternParts[i].startsWith(":")) continue;
            if (!patternParts[i].equals(pathParts[i])) return false;
        }

        return true;
    }
}
