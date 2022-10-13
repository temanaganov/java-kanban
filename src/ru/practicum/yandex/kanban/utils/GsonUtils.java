package ru.practicum.yandex.kanban.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;

public class GsonUtils {
    public static Gson getInstance() {
        return new GsonBuilder().registerTypeAdapter(Instant.class, new GsonInstantAdapter()).create();
    }
}
