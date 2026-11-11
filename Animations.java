package com.curtaincall;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import static com.curtaincall.Animations.Cat.COOL;
import static com.curtaincall.Animations.Cat.FUNNY;
import static com.curtaincall.Animations.Cat.SCARY;
import static com.curtaincall.Ctx.lerp;

/** The animation library: 34 funny, 12 cool, 9 scary = 55. Add your own with a("id", CATEGORY, "Name", c -> {...}). */
public final class Animations {
    public enum Cat { FUNNY, COOL, SCARY }

    public record Anim(String id, Cat cat, String name, Consumer<Ctx> run) {}

    public static final Map<String, Anim> ALL = new LinkedHashMap<>();
    private static final Random RNG = new Random();

    private static void a(String id, Cat cat, String name, Consumer<Ctx> run) {
        ALL.put(id, new Anim(id, cat, name, run));
    }

    public static Anim random() {
        return new ArrayList<>(ALL.values()).get(RNG.nextInt(ALL.size()));
    }

    private static double r(double range) { return (Math.random() - 0.5) * 2 * range; }

    public static void init() {
        // =====================================================================
        // FUNNY
        // =====================================================================
        a("bowling_strike", FUNNY, "Bowling Strike", c -> {
            c.each(0, 30, t -> c.bl(0, Blocks.BLACK_CONCRETE, lerp(12, 0.6, t), 0.3, 0, 3, 0.08, 0));
            c.rep(0, 5, 6, i -> c.s(0, "block.stone.step", 0.8f, 0.5f));
            c.s(30, "entity.generic.explode", 0.7f, 1.7f);
            c.bl(30, Blocks.WHITE_CONCRETE, 0.6, 0.6, 0, 70, 0.9, 0.4);
            c.tune(31, 2, "block.note_block.xylophone", 1f, 1.0f, 1.26f, 1.5f, 2.0f);
            c.say(30, "STRIKE!");
            c.chat(34, "Perfect game. 300 points.");
        });

        a("anvil_drop", FUNNY, "ACME Anvil", c -> {
            c.say(0, "ACME Anvil Co.");
            c.s(0, "entity.arrow.shoot", 0.6f, 0.5f);
            c.each(0, 14, t -> c.bl(0, Blocks.ANVIL, 0, lerp(16, 1, t), 0, 3, 0.1, 0));
            c.s(14, "block.anvil.land", 1.6f, 0.7f);
            c.p(14, "poof", 40, 0.6, 0.08);
            c.chat(18, "Classic.");
        });

        a("piano_drop", FUNNY, "Grand Piano", c -> {
            c.say(0, "A grand piano. Obviously.");
            c.each(0, 16, t -> c.bl(0, Blocks.NOTE_BLOCK, 0, lerp(18, 1, t), 0, 3, 0.2, 0));
            c.s(16, "block.anvil.land", 1.4f, 0.5f);
            c.s(16, "block.note_block.bass", 1.5f, 0.5f);
            c.s(16, "block.note_block.bass", 1.5f, 0.53f);
            c.s(16, "block.note_block.pling", 1.5f, 0.7f);
            c.p(16, "note", 30, 1.2, 1);
        });

        a("duck_army", FUNNY, "Duck Uprising", c -> {
            c.say(0, "Rubber duck uprising");
            c.rep(0, 3, 10, i -> {
                c.s(0, "entity.chicken.ambient", 1f, 1.7f + (i % 3) * 0.1f);
                c.it(0, Items.YELLOW_DYE, 0, 1, i * 0.3 - 1.5, 6, 0.2, 0.1);
                c.p(0, "poof", 4, 0.3, 0.02);
            });
            c.chat(32, "(they're chickens, the budget was low)");
        });

        a("wrong_door", FUNNY, "Wrong Door", c -> {
            c.say(0, "Wrong door.");
            c.s(0, "block.wooden_door.open", 1f, 1f);
            c.bl(0, Blocks.OAK_PLANKS, 1, 1, 0, 15, 0.3, 0.05);
            c.s(14, "entity.villager.no", 1.2f, 1f);
            c.s(30, "block.wooden_door.close", 1f, 1f);
            c.chat(16, "Occupied!");
        });

        a("sad_trombone", FUNNY, "Sad Trombone", c -> {
            c.say(0, "wah wah wah wahhhh");
            c.tune(0, 10, "block.note_block.didgeridoo", 1.5f, 1.0f, 0.9f, 0.8f, 0.6f);
            c.tune(36, 3, "block.note_block.didgeridoo", 1.5f, 0.6f, 0.58f, 0.56f, 0.54f, 0.52f);
            c.each(0, 50, t -> { c.pa(0, "cloud", 0, 2.8, 0, 2, 0.3, 0); c.pa(0, "falling_water", 0, 2.6, 0, 2, 0.3, 0); });
        });

        a("banana_peel", FUNNY, "Banana Peel", c -> {
            c.bl(0, Blocks.YELLOW_CONCRETE, 0.8, 0.1, 0, 12, 0.1, 0.02);
            c.s(2, "entity.slime.squish", 1.2f, 1.3f);
            c.each(4, 12, t -> c.pa(0, "cloud", lerp(0, -4, t), 0.2, 0, 3, 0.2, 0.02));
            c.s(8, "entity.player.attack.knockback", 1.2f, 0.8f);
            c.s(18, "entity.generic.big_fall", 1f, 0.9f);
            c.p(18, "poof", 20, 0.4, 0.05);
            c.say(4, "Slipped on a banana.");
        });

        a("pie_face", FUNNY, "Pie to the Face", c -> {
            c.s(0, "entity.snowball.throw", 1f, 0.8f);
            c.each(0, 10, t -> c.it(0, Items.PUMPKIN_PIE, lerp(5, 0.5, t), 1.5, 0, 1, 0, 0));
            c.s(10, "entity.slime.squish", 1.5f, 0.7f);
            c.it(10, Items.PUMPKIN_PIE, 0.4, 1.5, 0, 25, 0.3, 0.15);
            c.say(10, "Pie. In. Face.");
        });

        a("fish_slap", FUNNY, "Slapped by a Fish", c -> {
            c.say(0, "Slapped by a fish.");
            c.each(0, 6, t -> c.it(0, Items.COD, 1.2, 1.4, lerp(2.5, -2.5, t), 2, 0.05, 0));
            c.s(2, "entity.player.attack.sweep", 1f, 0.6f);
            c.s(3, "entity.fishing_bobber.splash", 1.4f, 1f);
            c.s(5, "entity.player.attack.knockback", 1.2f, 0.9f);
            c.p(6, "splash", 30, 0.5, 0.2);
        });

        a("toilet_flush", FUNNY, "Flushed", c -> {
            c.say(0, "*flush*");
            c.s(0, "entity.generic.splash", 1.2f, 1f);
            c.s(0, "block.bubble_column.whirlpool_inside", 1.5f, 1f);
            c.each(0, 40, t -> {
                double ang = t * Math.PI * 8, rad = lerp(1.5, 0.1, t), u = lerp(2, 0, t);
                c.pa(0, "bubble", Math.cos(ang) * rad, u, Math.sin(ang) * rad, 2, 0.05, 0);
                c.pa(0, "splash", Math.cos(ang) * rad, u, Math.sin(ang) * rad, 1, 0, 0);
            });
            c.s(42, "entity.generic.splash", 1f, 0.6f);
            c.chat(44, "Sent to the sewers.");
        });

        a("cow_abduction", FUNNY, "Cow Abduction", c -> {
            c.say(0, "Moo. (abducted)");
            c.s(0, "entity.cow.ambient", 1.2f, 1.2f);
            c.s(0, "block.beacon.ambient", 1.5f, 1.5f);
            c.each(0, 40, t -> {
                c.pa(0, "end_rod", 0, Math.random() * 12, 0, 2, 0.3, 0);
                c.it(0, Items.BEEF, 0, lerp(0, 12, t), 0, 1, 0.05, 0);
            });
            c.s(30, "entity.cow.ambient", 1f, 2f);
            c.s(40, "entity.firework_rocket.blast", 1f, 1f);
            c.pa(40, "firework", 0, 12, 0, 30, 0.5, 0.1);
        });

        a("confetti_cannon", FUNNY, "Confetti Cannon", c -> {
            c.say(0, "Congratulations on dying!");
            c.s(0, "entity.firework_rocket.launch", 1f, 1f);
            c.s(8, "entity.firework_rocket.blast", 1.5f, 1f);
            c.s(10, "entity.firework_rocket.twinkle", 1.5f, 1f);
            c.s(10, "entity.villager.celebrate", 1.2f, 1f);
            c.pa(8, "firework", 0, 2, 0, 80, 1.2, 0.25);
            c.pa(8, "happy_villager", 0, 1.5, 0, 25, 1.5, 0.1);
            c.it(8, Items.PINK_DYE, 0, 3, 0, 30, 1.2, 0.05);
        });

        a("achievement_get", FUNNY, "Achievement Get", c -> {
            c.s(0, "ui.toast.challenge_complete", 1.2f, 1f);
            c.p(0, "totem_of_undying", 30, 0.7, 0.4);
            c.say(0, "Advancement Made! [Rest in Pieces]");
            c.chat(2, "\u00a7a[Rest in Pieces]\u00a7r Die in the dumbest way possible");
        });

        a("press_f", FUNNY, "Press F", c -> {
            c.say(0, "Press F to pay respects");
            c.rep(10, 6, 5, i -> c.s(0, "ui.button.click", 0.8f, 1f + i * 0.05f));
            c.chat(12, "<xX_Pro_Xx> F");
            c.chat(18, "<Steve_69> F");
            c.chat(24, "<Herobrine> F");
            c.chat(32, "<The Creeper> F (ssssss)");
            c.p(20, "heart", 8, 0.5, 0.1);
        });

        a("loading_screen", FUNNY, "Respawn Loading", c -> {
            String[] st = {"0%", "12%", "37%", "64%", "99%", "99%", "99%.", "99%.."};
            c.rep(0, 14, st.length, i -> {
                c.say(0, "Respawning... " + st[i]);
                c.s(0, "block.note_block.hat", 1f, 1f + i * 0.05f);
            });
            c.chat(112, "Still loading...");
        });

        a("lag_spike", FUNNY, "Lag Spike", c -> {
            c.say(0, "Ping: 9999ms");
            c.s(0, "block.beacon.deactivate", 1f, 0.6f);
            c.rep(0, 5, 8, i -> c.pa(0, "electric_spark", r(1.5), Math.random() * 2.5, r(1.5), 12, 0.3, 0.2));
            c.s(40, "block.note_block.bit", 1f, 2f);
            c.chat(42, "Lag. Definitely lag. Not my fault.");
        });

        a("thanos_snap", FUNNY, "Perfectly Balanced", c -> {
            c.chat(0, "I don't feel so good...");
            c.s(0, "block.respawn_anchor.deplete", 1.2f, 0.8f);
            c.each(0, 40, t -> {
                c.pa(0, "ash", 0, lerp(0.2, 1.8, Math.random()), 0, 6, 0.4, 0.01);
                c.pa(0, "white_ash", lerp(0, 3, t), lerp(0.5, 2, t), lerp(0, -3, t), 4, 0.3, 0.02);
            });
            c.say(10, "Perfectly balanced.");
        });

        a("log_swap", FUNNY, "Replacement Jutsu", c -> {
            c.p(0, "poof", 60, 0.8, 0.1);
            c.s(0, "entity.enderman.teleport", 1f, 1.5f);
            c.s(0, "entity.player.attack.sweep", 1f, 1f);
            c.bl(2, Blocks.OAK_LOG, 0, 1, 0, 30, 0.3, 0.1);
            c.say(2, "It was a log all along.");
            c.chat(6, "Replacement jutsu!");
        });

        a("potion_mixup", FUNNY, "Wrong Potion", c -> {
            c.say(0, "Drank the wrong potion.");
            c.s(0, "entity.generic.drink", 1f, 1f);
            c.s(10, "entity.puffer_fish.blow_up", 1.2f, 0.8f);
            c.each(10, 30, t -> c.pa(0, "witch", 0, lerp(1, 2.2, t), 0, 3, 0.3, 0));
            c.boom(40);
            c.p(40, "cloud", 40, 1, 0.2);
        });

        a("hold_music", FUNNY, "Hold Music", c -> {
            c.say(0, "Your call is important to us.");
            c.tune(0, 12, "block.note_block.chime", 0.8f, 1.0f, 1.26f, 1.5f, 1.26f, 1.0f, 1.12f, 1.26f, 1.0f);
            c.each(0, 96, t -> { if (Math.random() < 0.2) c.pa(0, "note", 0, 2, 0, 1, 0.5, 1); });
            c.chat(80, "Estimated wait time: forever.");
        });

        a("scam_call", FUNNY, "Extended Warranty", c -> {
            c.say(0, "Incoming call: Car Warranty Dept.");
            c.rep(0, 6, 8, i -> c.s(0, "block.note_block.pling", 1f, i % 2 == 0 ? 1.6f : 1.9f));
            c.chat(50, "We've been trying to reach you about your extended warranty.");
            c.s(52, "entity.villager.no", 1f, 1f);
        });

        a("goat_launch", FUNNY, "Screaming Launch", c -> {
            c.say(0, "AAAAAAAAAAAA");
            c.s(0, "entity.goat.screaming.ambient", 2f, 1f);
            c.s(14, "entity.goat.screaming.ambient", 2f, 0.8f);
            c.s(28, "entity.goat.screaming.ambient", 2f, 0.6f);
            c.each(0, 40, t -> c.pa(0, "poof", lerp(0, 16, t), 1 + 24 * t * (1 - t), 0, 2, 0.1, 0));
            c.s(40, "entity.generic.explode", 0.8f, 1.5f);
        });

        a("trampoline", FUNNY, "Trampoline", c -> {
            c.say(0, "BOING");
            c.rep(0, 10, 4, i -> {
                c.s(0, "entity.slime.jump", 1.3f, 1f + i * 0.15f);
                c.p(0, "item_slime", 12, 0.4, 0.1);
            });
            c.each(0, 40, t -> c.pa(0, "happy_villager", 0, Math.abs(Math.sin(t * Math.PI * 4)) * 3 * (1 - t), 0, 1, 0.05, 0));
        });

        a("totem_fail", FUNNY, "Totem Fail", c -> {
            c.s(0, "item.totem.use", 1.3f, 1f);
            c.p(0, "totem_of_undying", 80, 0.8, 0.5);
            c.say(0, "TOTEM ACTIVATED!");
            c.say(40, "...it was in your other hand.");
            c.s(40, "entity.villager.no", 1.2f, 0.8f);
            c.chat(46, "Totem skill issue.");
        });

        a("squid_ink", FUNNY, "Ink'd", c -> {
            c.say(0, "Ink'd.");
            c.s(0, "entity.squid.squirt", 1.5f, 1f);
            c.p(0, "squid_ink", 80, 0.8, 0.15);
            c.each(0, 30, t -> c.pa(0, "glow_squid_ink", 0, lerp(0.5, 3, t), 0, 3, 0.4, 0));
        });

        a("chicken_rain", FUNNY, "Chicken Rain", c -> {
            c.say(0, "It's raining chickens.");
            c.rep(0, 2, 20, i -> {
                c.it(0, Items.FEATHER, r(2), 7, r(2), 3, 0.1, 0.02);
                c.it(0, Items.EGG, r(2), 7, r(2), 1, 0.1, 0.02);
            });
            c.rep(0, 7, 6, i -> c.s(0, "entity.chicken.ambient", 1f, 0.8f + i * 0.2f));
            c.s(36, "entity.chicken.egg", 1.2f, 1f);
        });

        a("tiny_violin", FUNNY, "Tiny Violin", c -> {
            c.say(0, "Playing the world's smallest violin");
            c.tune(0, 10, "block.note_block.flute", 0.6f, 1.5f, 1.4f, 1.26f, 1.12f, 1.0f);
            c.rep(0, 10, 5, i -> c.pa(0, "note", 0, 2, 0, 3, 0.3, 1));
            c.p(30, "falling_water", 10, 0.2, 0);
        });

        a("surprise_party", FUNNY, "Surprise Party", c -> {
            c.say(0, "SURPRISE!");
            c.rep(0, 4, 3, i -> c.s(0, "entity.villager.celebrate", 1.2f, 0.9f + i * 0.15f));
            c.s(0, "entity.firework_rocket.blast", 1.5f, 1f);
            c.pa(0, "firework", 0, 2, 0, 70, 1.4, 0.2);
            c.it(0, Items.CAKE, 1, 2, 0, 10, 0.5, 0.1);
            c.chat(14, "It was supposed to be YOUR party. Too late.");
        });

        a("vine_boom", FUNNY, "Vine Boom", c -> {
            c.say(0, "*vine boom*");
            c.s(0, "block.anvil.land", 2f, 0.5f);
            c.s(0, "entity.warden.sonic_boom", 0.8f, 1.2f);
            c.pa(0, "sonic_boom", 0, 1, 0, 1, 0, 0);
            c.ring(2, "cloud", 2, 24, 0.3);
        });

        a("creeper_hug", FUNNY, "Aww Man", c -> {
            c.s(0, "entity.creeper.primed", 1.5f, 1f);
            c.say(0, "Aww man...");
            c.chat(2, "A creeper just wanted a hug.");
            c.boom(30);
            c.p(30, "poof", 50, 1, 0.2);
        });

        a("disco_fever", FUNNY, "Disco Fever", c -> {
            c.say(0, "Staying alive");
            c.rep(0, 5, 16, i -> {
                c.ring(0, i % 2 == 0 ? "end_rod" : "firework", 1 + i * 0.2, 16, 0.1);
                c.pa(0, "note", r(1.5), 2, r(1.5), 2, 0.3, 1);
                c.s(0, "block.note_block.pling", 0.8f, 0.7f + (i % 4) * 0.3f);
                c.s(0, "block.note_block.basedrum", 1f, 1f);
            });
        });

        a("pigs_fly", FUNNY, "When Pigs Fly", c -> {
            c.say(0, "When pigs fly.");
            c.rep(0, 12, 3, i -> c.s(0, "entity.pig.ambient", 1.3f, 1f + i * 0.2f));
            c.each(0, 40, t -> {
                c.it(0, Items.PORKCHOP, r(0.5), lerp(0, 12, t), r(0.5), 1, 0.1, 0);
                c.pa(0, "cloud", 0, lerp(0, 12, t), 0, 1, 0.3, 0);
            });
        });

        a("mlg_airhorn", FUNNY, "MLG", c -> {
            c.say(0, "MLG 360 NO-SCOPE");
            c.rep(0, 3, 4, i -> c.s(0, "block.note_block.bit", 1.5f, 2f));
            c.s(0, "ui.toast.challenge_complete", 1.5f, 1.4f);
            c.p(0, "crit", 60, 1, 0.5);
            c.pa(0, "firework", 0, 1.5, 0, 40, 1, 0.2);
            c.chat(6, "<xX_Pro_Xx> GET REKT");
        });

        a("midnight_snack", FUNNY, "Midnight Snack", c -> {
            c.say(0, "Died of a midnight snack.");
            c.rep(0, 6, 6, i -> {
                c.s(0, "entity.generic.eat", 1f, 0.9f + i * 0.05f);
                c.it(0, Items.CAKE, 0.5, 1.3, 0, 4, 0.2, 0.05);
            });
            c.s(42, "entity.player.burp", 1.5f, 1f);
        });

        // =====================================================================
        // COOL
        // =====================================================================
        a("angel_ascension", COOL, "Ascension", c -> {
            c.say(0, "Ascended.");
            c.s(0, "block.beacon.activate", 1.3f, 1f);
            c.s(0, "entity.player.levelup", 1f, 0.8f);
            c.each(0, 50, t -> {
                double ang = t * 20;
                c.pa(0, "end_rod", Math.cos(ang) * 0.6, lerp(0, 14, t), Math.sin(ang) * 0.6, 2, 0.05, 0);
                c.pa(0, "glow", 0, lerp(0, 14, t), 0, 1, 0.3, 0);
                c.pa(0, "end_rod", 0.2, lerp(0, 14, t) - 0.5, 1.2 + t, 1, 0.1, 0);
                c.pa(0, "end_rod", 0.2, lerp(0, 14, t) - 0.5, -1.2 - t, 1, 0.1, 0);
            });
            c.s(50, "entity.firework_rocket.twinkle", 1.5f, 1f);
        });

        a("lightning_strike", COOL, "Smitten", c -> {
            c.say(0, "Smitten.");
            c.bolt(0, 0, 0);
            c.s(0, "entity.lightning_bolt.thunder", 2f, 1f);
            c.p(2, "smoke", 60, 0.6, 0.1);
            c.p(2, "electric_spark", 40, 0.8, 0.3);
        });

        a("supernova", COOL, "Supernova", c -> {
            c.say(0, "SUPERNOVA");
            c.s(0, "block.beacon.power_select", 1.5f, 0.5f);
            c.each(0, 30, t -> c.ring(0, "end_rod", lerp(4, 0.2, t), 12, 1.2));
            c.boom(30);
            c.s(30, "entity.firework_rocket.blast", 2f, 0.6f);
            c.rep(30, 2, 8, i -> c.ring(0, "firework", 1 + i * 1.5, 32, 1));
        });

        a("phoenix_rebirth", COOL, "Phoenix Down", c -> {
            c.say(0, "Phoenix Down.");
            c.s(0, "item.firecharge.use", 1.5f, 0.7f);
            c.each(0, 50, t -> {
                double ang = t * Math.PI * 10, rad = 1.2 * (1 - t) + 0.3;
                c.pa(0, "flame", Math.cos(ang) * rad, lerp(0, 6, t), Math.sin(ang) * rad, 2, 0.02, 0);
                c.pa(0, "lava", Math.cos(ang) * rad, lerp(0, 6, t), Math.sin(ang) * rad, 1, 0.1, 0);
            });
            c.s(50, "item.totem.use", 1.2f, 1.2f);
            c.pa(50, "totem_of_undying", 0, 6, 0, 80, 1, 0.5);
        });

        a("ender_vanish", COOL, "Ender Vanish", c -> {
            c.say(0, "*blip*");
            c.s(0, "block.portal.ambient", 1.2f, 1.5f);
            c.each(0, 25, t -> c.p(0, "reverse_portal", 6, 2 * (1 - t) + 0.2, 0.05));
            c.s(25, "entity.enderman.teleport", 1.5f, 1f);
            c.p(25, "portal", 100, 0.5, 1.5);
        });

        a("matrix_glitch", COOL, "Glitch", c -> {
            c.say(0, "Error 404: Player not found");
            c.rep(0, 3, 12, i -> {
                c.pa(0, "electric_spark", r(1.5), Math.random() * 2.5, r(1.5), 12, 0.3, 0.2);
                c.s(0, "block.note_block.bit", 0.7f, (float) (0.6 + Math.random() * 1.4));
            });
            c.s(36, "block.beacon.deactivate", 1f, 1.5f);
        });

        a("ice_shatter", COOL, "Frozen & Shattered", c -> {
            c.say(0, "Frozen solid. Shattered.");
            c.s(0, "block.powder_snow.step", 1.2f, 0.7f);
            c.p(0, "snowflake", 60, 0.6, 0.02);
            c.bl(0, Blocks.ICE, 0, 1, 0, 25, 0.4, 0);
            c.s(25, "block.glass.break", 1.8f, 0.8f);
            c.s(27, "block.glass.break", 1.8f, 1.2f);
            c.bl(25, Blocks.ICE, 0, 1, 0, 90, 0.7, 0.3);
            c.p(25, "snowflake", 50, 0.9, 0.2);
        });

        a("samurai_slash", COOL, "Nothing Personal", c -> {
            c.say(0, "Nothing personal.");
            c.rep(0, 6, 3, i -> {
                c.pa(0, "sweep_attack", 0.6, 0.5 + i * 0.5, 0, 1, 0.3, 0);
                c.s(0, "entity.player.attack.sweep", 1.3f, 1f + i * 0.15f);
            });
            c.s(24, "entity.player.attack.crit", 1.5f, 0.8f);
            c.p(26, "crit", 50, 0.6, 0.5);
            c.chat(30, "You were already dead.");
        });

        a("meteor_strike", COOL, "Meteor", c -> {
            c.say(0, "Incoming!");
            c.s(0, "entity.ghast.shoot", 1.3f, 0.5f);
            c.each(0, 30, t -> {
                c.pa(0, "flame", lerp(10, 0, t), lerp(25, 0.5, t), 0, 6, 0.2, 0.02);
                c.pa(0, "large_smoke", lerp(10, 0, t) + 0.7, lerp(25, 0.5, t) + 1, 0, 2, 0.2, 0);
                c.pa(0, "lava", lerp(10, 0, t), lerp(25, 0.5, t), 0, 1, 0.3, 0);
            });
            c.boom(30);
            c.s(30, "entity.generic.explode", 2f, 0.6f);
            c.rep(30, 2, 6, i -> c.ring(0, "flame", 1 + i * 1.5, 28, 0.3));
        });

        a("black_hole", COOL, "Spaghettification", c -> {
            c.say(0, "Spaghettified.");
            c.s(0, "block.portal.ambient", 1.5f, 0.6f);
            c.each(0, 50, t -> {
                double ang = t * 30, rad = lerp(4, 0.2, t);
                c.pa(0, "reverse_portal", Math.cos(ang) * rad, 1.2, Math.sin(ang) * rad, 2, 0, 0);
                c.pa(0, "reverse_portal", Math.cos(ang + Math.PI) * rad, 1.2, Math.sin(ang + Math.PI) * rad, 2, 0, 0);
                c.pa(0, "smoke", 0, 1.2, 0, 1, 0.3, 0);
            });
            c.s(50, "entity.generic.explode", 1.2f, 0.5f);
            c.p(50, "poof", 40, 0.5, 0.3);
        });

        a("soul_reaper", COOL, "Soul Collected", c -> {
            c.say(0, "Soul collected.");
            c.s(0, "particle.soul_escape", 1.5f, 1f);
            c.each(0, 50, t -> {
                double ang = t * Math.PI * 6;
                c.pa(0, "soul", Math.cos(ang) * 0.7, lerp(0, 8, t), Math.sin(ang) * 0.7, 2, 0.05, 0);
                c.pa(0, "soul_fire_flame", Math.cos(ang + 3) * 0.7, lerp(0, 8, t), Math.sin(ang + 3) * 0.7, 1, 0.05, 0);
            });
        });

        a("firework_finale", COOL, "Grand Finale", c -> {
            c.say(0, "Grand finale!");
            c.rep(0, 6, 8, i -> {
                c.pa(0, "firework", r(3), 3 + Math.random() * 5, r(3), 40, 0.8, 0.15);
                c.s(0, "entity.firework_rocket.blast", 1.5f, 0.8f + i * 0.05f);
                c.s(0, "entity.firework_rocket.launch", 1f, 1f);
            });
        });

        // =====================================================================
        // SCARY
        // =====================================================================
        a("wither_curse", SCARY, "Wither Curse", c -> {
            c.say(0, "Something wicked this way comes.");
            c.s(0, "entity.wither.spawn", 1f, 0.7f);
            c.each(0, 50, t -> {
                c.ring(0, "large_smoke", lerp(0.5, 3, t), 20, lerp(0, 3, t));
                c.pa(0, "soul_fire_flame", r(1), Math.random() * 3, r(1), 2, 0.2, 0.01);
            });
        });

        a("jumpscare", SCARY, "Jumpscare", c -> {
            c.say(0, "BOO!");
            c.s(0, "entity.enderman.scream", 2.5f, 0.6f);
            c.s(0, "entity.elder_guardian.curse", 2f, 0.5f);
            c.pa(0, "sonic_boom", 0, 1, 0, 1, 0, 0);
            c.p(0, "large_smoke", 100, 1, 0.05);
            c.chat(4, "\u00a74\u00a7lBOO!");
        });

        a("warden_heartbeat", SCARY, "Heartbeat", c -> {
            c.say(0, "Don't breathe.");
            for (int tk : new int[]{0, 30, 54, 72, 84, 92}) c.s(tk, "entity.warden.heartbeat", 1.6f, 1f);
            c.s(100, "entity.warden.sonic_boom", 2f, 1f);
            c.pa(100, "sonic_boom", 0, 1, 0, 1, 0, 0);
            c.ring(100, "sculk_soul", 2, 20, 1);
        });

        a("whispers", SCARY, "Whispers", c -> {
            c.chat(0, "\u00a7k#########\u00a7r \u00a77behind you");
            c.say(0, "...");
            c.s(0, "ambient.cave", 1.5f, 0.6f);
            c.rep(10, 15, 4, i -> c.s(0, "entity.vex.ambient", 0.7f, 0.5f));
            c.each(0, 60, t -> c.pa(0, "soul", r(2), Math.random() * 2.5, r(2), 1, 0.1, 0));
        });

        a("graveyard_hands", SCARY, "Graveyard", c -> {
            c.say(0, "They're digging you up.");
            c.s(0, "entity.zombie.ambient", 1f, 0.5f);
            c.rep(0, 8, 5, i -> c.s(0, "block.gravel.break", 1f, 0.7f));
            c.each(0, 40, t -> {
                c.bl(0, Blocks.DIRT, r(1), 0.1, r(1), 2, 0.2, 0.05);
                c.pa(0, "soul", r(0.8), lerp(0, 3, t), r(0.8), 1, 0.1, 0);
            });
        });

        a("witch_cackle", SCARY, "Cackle", c -> {
            c.say(0, "Hehehehe!");
            c.rep(0, 8, 4, i -> c.s(0, "entity.witch.ambient", 1.5f, 0.8f + i * 0.1f));
            c.each(0, 40, t -> c.p(0, "witch", 3, 0.5, 0.1));
        });

        a("bell_toll", SCARY, "The Bell Tolls", c -> {
            c.say(0, "The bell tolls.");
            c.rep(0, 24, 4, i -> {
                c.s(0, "block.bell.use", 2f, 0.5f);
                c.ring(0, "smoke", 1 + i, 24, 0.2);
            });
        });

        a("ghost_train", SCARY, "Ghost Train", c -> {
            c.say(0, "All aboard the ghost train.");
            c.s(0, "entity.ghast.warn", 1.2f, 0.6f);
            c.rep(0, 6, 6, i -> c.s(0, "entity.vex.ambient", 1f, 0.6f));
            c.each(0, 40, t -> {
                c.pa(0, "soul_fire_flame", lerp(12, -12, t), 1, Math.sin(t * 20) * 0.5, 4, 0.2, 0.02);
                c.pa(0, "soul", lerp(12, -12, t) + 1, 1.5, 0, 2, 0.3, 0);
            });
        });

        a("vex_swarm", SCARY, "Swarm", c -> {
            c.say(0, "They're coming.");
            c.rep(0, 8, 6, i -> c.s(0, "entity.vex.charge", 1.2f, 0.8f + i * 0.05f));
            c.each(0, 60, t -> {
                for (int k = 0; k < 3; k++) {
                    double ang = t * 40 + k * 2.1, rad = lerp(3, 0.5, t);
                    c.pa(0, "soul_fire_flame", Math.cos(ang) * rad, 1 + Math.sin(ang * 2) * 0.7, Math.sin(ang) * rad, 1, 0.05, 0);
                }
            });
        });
    }
}
