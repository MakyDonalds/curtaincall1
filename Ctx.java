package com.curtaincall;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

/**
 * Toolbox handed to every animation. Positions are RELATIVE to the victim:
 *   f = forward (the way they were facing), u = up from feet, s = sideways.
 * Every method takes a delay d in ticks (20 ticks = 1 second). d = 0 means "right now".
 */
public final class Ctx {
    public final ServerWorld w;
    public final LivingEntity e;
    final Config cfg;
    final double x, y, z, fX, fZ, sX, sZ;

    public Ctx(LivingEntity e, ServerWorld w, Config cfg) {
        this.e = e; this.w = w; this.cfg = cfg;
        x = e.getX(); y = e.getY(); z = e.getZ();
        double yaw = Math.toRadians(e.getYaw());
        fX = -Math.sin(yaw); fZ = Math.cos(yaw);
        sX = -fZ; sZ = fX;
    }

    public static double lerp(double a, double b, double t) { return a + (b - a) * t; }

    // ---------- lookups (by id string, so unknown ids are just skipped instead of crashing) ----------
    private static final Map<String, SoundEvent> SOUNDS = new HashMap<>();
    private static final Map<String, ParticleEffect> PARTS = new HashMap<>();

    private static SoundEvent sound(String id) {
        return SOUNDS.computeIfAbsent(id, k -> Registries.SOUND_EVENT.get(Identifier.of(k)));
    }

    private static ParticleEffect part(String id) {
        return PARTS.computeIfAbsent(id, k -> {
            var t = Registries.PARTICLE_TYPE.get(Identifier.of(k));
            return t instanceof ParticleEffect pe ? pe : null;
        });
    }

    // ---------- timing ----------
    public void at(int d, Runnable r) {
        if (d <= 0) r.run(); else CurtainCall.later(d, r);
    }

    /** run fn at d, d+gap, d+2*gap ... (n times) */
    public void rep(int d, int gap, int n, IntConsumer fn) {
        for (int i = 0; i < n; i++) { final int k = i; at(d + i * gap, () -> fn.accept(k)); }
    }

    /** run fn every tick for dur ticks, with progress t going 0 -> 1 */
    public void each(int d, int dur, DoubleConsumer fn) {
        for (int i = 0; i <= dur; i++) {
            final double t = dur == 0 ? 1 : (double) i / dur;
            at(d + i, () -> fn.accept(t));
        }
    }

    // ---------- sound ----------
    public void s(int d, String id, float vol, float pitch) {
        at(d, () -> {
            SoundEvent ev = sound(id);
            if (ev != null)
                w.playSound(null, x, y, z, ev, SoundCategory.MASTER, vol * cfg.volume,
                        MathHelper.clamp(pitch * cfg.pitch, 0.5f, 2.0f));
        });
    }

    /** play the same sound with a list of pitches, one every `gap` ticks (a tiny tune) */
    public void tune(int d, int gap, String snd, float vol, float... pitches) {
        for (int i = 0; i < pitches.length; i++) s(d + i * gap, snd, vol, pitches[i]);
    }

    // ---------- particles ----------
    private void spawn(ParticleEffect pe, double f, double u, double s, int n, double sp, double speed) {
        double px = x + fX * f + sX * s, pz = z + fZ * f + sZ * s;
        w.spawnParticles(pe, px, y + u, pz, Math.max(1, Math.round(n * cfg.particles)), sp, sp, sp, speed);
    }

    /** particle burst at the victim's body */
    public void p(int d, String id, int n, double spread, double speed) { pa(d, id, 0, 1, 0, n, spread, speed); }

    /** particle burst at a relative position */
    public void pa(int d, String id, double f, double u, double s, int n, double sp, double speed) {
        at(d, () -> { ParticleEffect pe = part(id); if (pe != null) spawn(pe, f, u, s, n, sp, speed); });
    }

    /** item-shaped particles (fish, cake, feathers...) */
    public void it(int d, Item item, double f, double u, double s, int n, double sp, double speed) {
        at(d, () -> spawn(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(item)), f, u, s, n, sp, speed));
    }

    /** block-shaped particles (anvils, concrete chunks...) */
    public void bl(int d, Block b, double f, double u, double s, int n, double sp, double speed) {
        at(d, () -> spawn(new BlockStateParticleEffect(ParticleTypes.BLOCK, b.getDefaultState()), f, u, s, n, sp, speed));
    }

    public void ring(int d, String id, double r, int pts, double u) {
        at(d, () -> {
            ParticleEffect pe = part(id);
            if (pe == null) return;
            for (int i = 0; i < pts; i++) {
                double a = i * 2 * Math.PI / pts;
                spawn(pe, Math.cos(a) * r, u, Math.sin(a) * r, 1, 0, 0);
            }
        });
    }

    // ---------- big stuff ----------
    public void bolt(int d, double f, double s) {
        at(d, () -> {
            LightningEntity b = EntityType.LIGHTNING_BOLT.create(w, SpawnReason.TRIGGERED);
            if (b != null) {
                b.refreshPositionAfterTeleport(x + fX * f + sX * s, y, z + fZ * f + sZ * s);
                b.setCosmetic(true); // no fire, no damage
                w.spawnEntity(b);
            }
        });
    }

    /** harmless explosion (visual + sound only, never breaks blocks) */
    public void boom(int d) {
        p(d, "explosion_emitter", 1, 0, 0);
        s(d, "entity.generic.explode", 1.6f, 0.9f);
    }

    // ---------- text ----------
    private void near(Consumer<ServerPlayerEntity> fn) {
        double r2 = (double) cfg.radius * cfg.radius;
        for (ServerPlayerEntity o : w.getPlayers())
            if (o.squaredDistanceTo(x, y, z) <= r2) fn.accept(o);
    }

    /** caption on the action bar */
    public void say(int d, String t) {
        if (!cfg.captions) return;
        at(d, () -> near(o -> o.sendMessage(Text.literal(t).formatted(Formatting.GOLD, Formatting.BOLD), true)));
    }

    /** line in chat */
    public void chat(int d, String t) {
        if (!cfg.chat) return;
        at(d, () -> near(o -> o.sendMessage(Text.literal(t), false)));
    }
}
