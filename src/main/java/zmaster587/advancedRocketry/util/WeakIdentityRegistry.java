package zmaster587.advancedRocketry.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Ordered, identity-based membership without owning the registered objects. */
public final class WeakIdentityRegistry<T> {
    private final ReferenceQueue<T> collected = new ReferenceQueue<>();
    private final List<WeakReference<T>> entries = new ArrayList<>();

    public synchronized void add(T target) {
        if (target == null) throw new NullPointerException("target");
        prune();
        for (WeakReference<T> entry : entries) {
            if (entry.get() == target) return;
        }
        entries.add(new WeakReference<>(target, collected));
    }

    public synchronized void remove(T target) {
        removeIf(value -> value == target);
    }

    public synchronized void removeIf(Predicate<? super T> predicate) {
        prune();
        entries.removeIf(entry -> {
            T target = entry.get();
            if (target == null || predicate.test(target)) {
                // Also detach this registration from any in-progress dispatch snapshot.
                entry.clear();
                return true;
            }
            return false;
        });
    }

    /** A transient snapshot: additions wait until the next dispatch, removals take effect immediately. */
    public synchronized List<WeakReference<T>> snapshot() {
        prune();
        return new ArrayList<>(entries);
    }

    private void prune() {
        java.lang.ref.Reference<? extends T> entry;
        while ((entry = collected.poll()) != null) entries.remove(entry);
    }
}
