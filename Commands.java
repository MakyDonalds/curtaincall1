package com.curtaincall;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** /deathfx ... (see README for the full list) */
public final class Commands {
    private static final SuggestionProvider<ServerCommandSource> IDS =
            (c, b) -> CommandSource.suggestMatching(Animations.ALL.keySet(), b);

    private static final SuggestionProvider<ServerCommandSource> IDS_RANDOM = (c, b) -> {
        List<String> l = new ArrayList<>(Animations.ALL.keySet());
        l.add("random");
        return CommandSource.suggestMatching(l, b);
    };

    private static final SuggestionProvider<ServerCommandSource> IDS_MINE = (c, b) -> {
        List<String> l = new ArrayList<>(Animations.ALL.keySet());
        l.add("random");
        return CommandSource.suggestMatching(l, b);
    };

    private static void done(ServerCommandSource src, String msg) {
        CurtainCall.cfg.save();
        src.sendFeedback(() -> Text.literal("\u00a76[Curtain Call]\u00a7r " + msg), false);
    }

    private static int fail(ServerCommandSource src, String msg) {
        src.sendError(Text.literal(msg));
        return 0;
    }

    private static LiteralArgumentBuilder<ServerCommandSource> num(String name, float min, float max, BiConsumer<Config, Float> set) {
        return literal(name).then(argument("value", FloatArgumentType.floatArg(min, max)).executes(c -> {
            float v = FloatArgumentType.getFloat(c, "value");
            set.accept(CurtainCall.cfg, v);
            done(c.getSource(), name + " = " + v);
            return 1;
        }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> flag(String name, BiConsumer<Config, Boolean> set) {
        return literal(name).then(argument("value", BoolArgumentType.bool()).executes(c -> {
            boolean v = BoolArgumentType.getBool(c, "value");
            set.accept(CurtainCall.cfg, v);
            done(c.getSource(), name + " = " + v);
            return 1;
        }));
    }

    private static int list(ServerCommandSource src, String category) {
        for (Animations.Cat cat : Animations.Cat.values()) {
            if (category != null && !cat.name().equalsIgnoreCase(category)) continue;
            List<String> ids = new ArrayList<>();
            for (Animations.Anim a : Animations.ALL.values()) {
                if (a.cat() != cat) continue;
                boolean off = CurtainCall.cfg.disabled.contains(a.id());
                ids.add((off ? "\u00a7c" : "\u00a7f") + a.id() + "\u00a77");
            }
            src.sendFeedback(() -> Text.literal("\u00a76" + cat + " (" + ids.size() + "): \u00a77" + String.join(", ", ids)), false);
        }
        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> d) {
        d.register(literal("deathfx")
                .then(literal("list")
                        .executes(c -> list(c.getSource(), null))
                        .then(argument("category", StringArgumentType.word())
                                .suggests((c, b) -> CommandSource.suggestMatching(List.of("funny", "cool", "scary"), b))
                                .executes(c -> list(c.getSource(), StringArgumentType.getString(c, "category")))))

                .then(literal("preview").then(argument("id", StringArgumentType.word()).suggests(IDS_RANDOM).executes(c -> {
                    ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
                    String id = StringArgumentType.getString(c, "id");
                    Animations.Anim a = id.equals("random") ? Animations.random() : Animations.ALL.get(id);
                    if (a == null) return fail(c.getSource(), "Unknown animation: " + id);
                    CurtainCall.play(a, p);
                    return 1;
                })))

                .then(literal("mode").then(argument("mode", StringArgumentType.word())
                        .suggests((c, b) -> CommandSource.suggestMatching(List.of("random", "selected", "cycle"), b))
                        .executes(c -> {
                            try {
                                CurtainCall.cfg.mode = Config.Mode.valueOf(StringArgumentType.getString(c, "mode").toUpperCase());
                            } catch (IllegalArgumentException ex) {
                                return fail(c.getSource(), "Use: random, selected or cycle");
                            }
                            done(c.getSource(), "mode = " + CurtainCall.cfg.mode);
                            return 1;
                        })))

                .then(literal("select").then(argument("id", StringArgumentType.word()).suggests(IDS).executes(c -> {
                    String id = StringArgumentType.getString(c, "id");
                    if (!Animations.ALL.containsKey(id)) return fail(c.getSource(), "Unknown animation: " + id);
                    CurtainCall.cfg.selected = id;
                    CurtainCall.cfg.mode = Config.Mode.SELECTED;
                    done(c.getSource(), "Everyone now dies with: " + id);
                    return 1;
                })))

                .then(literal("mine").then(argument("id", StringArgumentType.word()).suggests(IDS_MINE).executes(c -> {
                    ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
                    String id = StringArgumentType.getString(c, "id");
                    if (!id.equals("random") && !Animations.ALL.containsKey(id)) return fail(c.getSource(), "Unknown animation: " + id);
                    CurtainCall.cfg.playerChoices.put(p.getUuidAsString(), id);
                    done(c.getSource(), "Your death animation: " + id);
                    return 1;
                })))

                .then(literal("enable").then(argument("id", StringArgumentType.word()).suggests(IDS).executes(c -> {
                    CurtainCall.cfg.disabled.remove(StringArgumentType.getString(c, "id"));
                    done(c.getSource(), "enabled " + StringArgumentType.getString(c, "id"));
                    return 1;
                })))
                .then(literal("disable").then(argument("id", StringArgumentType.word()).suggests(IDS).executes(c -> {
                    CurtainCall.cfg.disabled.add(StringArgumentType.getString(c, "id"));
                    done(c.getSource(), "disabled " + StringArgumentType.getString(c, "id"));
                    return 1;
                })))

                .then(literal("category").then(argument("name", StringArgumentType.word())
                        .suggests((c, b) -> CommandSource.suggestMatching(List.of("funny", "cool", "scary"), b))
                        .then(argument("on", BoolArgumentType.bool()).executes(c -> {
                            String n = StringArgumentType.getString(c, "name").toUpperCase();
                            boolean on = BoolArgumentType.getBool(c, "on");
                            if (on) CurtainCall.cfg.disabledCategories.remove(n); else CurtainCall.cfg.disabledCategories.add(n);
                            done(c.getSource(), n + " animations " + (on ? "ON" : "OFF"));
                            return 1;
                        }))))

                .then(num("volume", 0f, 5f, (cfg, v) -> cfg.volume = v))
                .then(num("pitch", 0.5f, 2f, (cfg, v) -> cfg.pitch = v))
                .then(num("particles", 0.1f, 5f, (cfg, v) -> cfg.particles = v))
                .then(num("radius", 8f, 256f, (cfg, v) -> cfg.radius = Math.round(v)))
                .then(flag("enabled", (cfg, v) -> cfg.enabled = v))
                .then(flag("captions", (cfg, v) -> cfg.captions = v))
                .then(flag("chat", (cfg, v) -> cfg.chat = v))
                .then(flag("mobs", (cfg, v) -> cfg.includeMobs = v))
                .then(flag("norepeat", (cfg, v) -> cfg.avoidRepeat = v))

                .then(literal("reload").executes(c -> {
                    CurtainCall.cfg = Config.load();
                    done(c.getSource(), "config reloaded");
                    return 1;
                }))

                .then(literal("status").executes(c -> {
                    Config g = CurtainCall.cfg;
                    String s = "enabled=" + g.enabled + " mode=" + g.mode + " selected=" + g.selected
                            + " volume=" + g.volume + " pitch=" + g.pitch + " particles=" + g.particles
                            + " captions=" + g.captions + " chat=" + g.chat + " mobs=" + g.includeMobs
                            + " radius=" + g.radius + " | " + CurtainCall.pool().size() + "/" + Animations.ALL.size() + " animations active";
                    c.getSource().sendFeedback(() -> Text.literal("\u00a76[Curtain Call]\u00a7r " + s), false);
                    return 1;
                })));
    }
}
