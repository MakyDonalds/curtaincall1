package com.curtaincall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CurtainCall implements ModInitializer {
    public static final Logger LOG = LoggerFactory.getLogger("curtaincall");
    public static Config cfg;

    private static final Random RNG = new Random();
    private static String last = "";

    // ---- tiny tick scheduler ----
    private static final class Task {
        int left; final Runnable r;
        Task(int left, Runnable r) { this.left = left; this.r = r; }
    }
    private static final List<Task> TASKS = new ArrayList<>();

    public static void later(int ticks, Runnable r) { TASKS.add(new Task(ticks, r)); }

    @Override
    public void onInitialize() {
        cfg = Config.load();
        Animations.init();
        LOG.info("Curtain Call loaded with {} death animations", Animations.ALL.size());

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (TASKS.isEmpty()) return;
            for (Task t : new ArrayList<>(TASKS)) {
                if (--t.left <= 0) {
                    TASKS.remove(t);
                    try { t.r.run(); } catch (Throwable ex) { LOG.error("Animation step failed", ex); }
                }
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> TASKS.clear());

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!cfg.enabled) return;
            if (!(entity instanceof ServerPlayerEntity) && !cfg.includeMobs) return;
            Animations.Anim a = pick(entity);
            if (a != null) play(a, entity);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> Commands.register(dispatcher));
    }

    /** All animations that are currently allowed by the config. */
    public static List<Animations.Anim> pool() {
        List<Animations.Anim> out = new ArrayList<>();
        for (Animations.Anim a : Animations.ALL.values())
            if (!cfg.disabled.contains(a.id()) && !cfg.disabledCategories.contains(a.cat().name())) out.add(a);
        return out;
    }

    public static Animations.Anim pick(LivingEntity e) {
        List<Animations.Anim> pool = pool();
        if (pool.isEmpty()) return null;

        // 1) personal choice (/deathfx mine <id>) always wins
        if (e instanceof ServerPlayerEntity p) {
            String mine = cfg.playerChoices.get(p.getUuidAsString());
            if (mine != null && !mine.equals("random")) {
                Animations.Anim a = Animations.ALL.get(mine);
                if (a != null) return a;
            }
        }
        // 2) server-wide mode
        switch (cfg.mode) {
            case SELECTED -> {
                Animations.Anim a = Animations.ALL.get(cfg.selected);
                if (a != null) return a;
            }
            case CYCLE -> {
                cfg.cycleIndex = (cfg.cycleIndex + 1) % pool.size();
                cfg.save();
                return pool.get(cfg.cycleIndex);
            }
            default -> { }
        }
        Animations.Anim a;
        do {
            a = pool.get(RNG.nextInt(pool.size()));
        } while (cfg.avoidRepeat && pool.size() > 1 && a.id().equals(last));
        last = a.id();
        return a;
    }

    public static void play(Animations.Anim a, LivingEntity e) {
        if (!(e.getEntityWorld() instanceof ServerWorld w)) return;
        try {
            a.run().accept(new Ctx(e, w, cfg));
        } catch (Throwable t) {
            LOG.error("Animation " + a.id() + " failed", t);
        }
    }
}
