package com.curtaincall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** All settings. Saved to config/curtaincall.json, editable by hand or with /deathfx. */
public class Config {
    public enum Mode { RANDOM, SELECTED, CYCLE }

    public boolean enabled = true;
    public Mode mode = Mode.RANDOM;
    public String selected = "bowling_strike";   // used in SELECTED mode
    public float volume = 1.0f;                  // 0 - 5  (above 1 = audible from further away)
    public float pitch = 1.0f;                   // 0.5 - 2
    public float particles = 1.0f;               // particle density multiplier
    public boolean captions = true;              // action-bar captions
    public boolean chat = true;                  // funny chat lines
    public boolean includeMobs = false;          // also play on mob deaths
    public boolean avoidRepeat = true;           // random mode never repeats the last one
    public int radius = 48;                      // who sees captions / chat
    public int cycleIndex = 0;
    public Set<String> disabled = new HashSet<>();            // individual animations turned off
    public Set<String> disabledCategories = new HashSet<>();  // FUNNY / COOL / SCARY
    public Map<String, String> playerChoices = new HashMap<>(); // uuid -> animation id

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("curtaincall.json");
    }

    public static Config load() {
        try {
            Path f = file();
            if (Files.exists(f)) {
                Config c = GSON.fromJson(Files.readString(f), Config.class);
                if (c != null) return c;
            }
        } catch (Exception e) {
            CurtainCall.LOG.error("Could not read config, using defaults", e);
        }
        Config c = new Config();
        c.save();
        return c;
    }

    public void save() {
        try {
            Files.writeString(file(), GSON.toJson(this));
        } catch (Exception e) {
            CurtainCall.LOG.error("Could not save config", e);
        }
    }
}
