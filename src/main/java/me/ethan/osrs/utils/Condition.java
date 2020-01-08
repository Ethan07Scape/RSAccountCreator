package me.ethan.osrs.utils;

import java.nio.file.DirectoryStream.Filter;
import java.util.concurrent.Callable;

/**
 * Condition An event-driven blocking utility. Frequencies are randomly adjusted
 * to provide a basic antipattern.
 */
public class Condition {
    /**
     * The random adjustment variance, 0.85-1.50.
     */
    public static final double[] VARIANCE = {.85d, 1.5d};

    /**
     * Blocks until the specified condition is satisfied (returns {@code true}).
     * This uses a frequency of 600ms for up to 10 tries, i.e. attempting a
     * maximum of 6 seconds.
     *
     * @param cond the condition
     * @return {@code true} if the condition was satisfied, otherwise
     * {@code false}
     */
    public static boolean wait(final Callable<Boolean> cond) {
        return wait(cond, 600, 10);
    }

    /**
     * Blocks until the specified condition is satisfied (returns {@code true}).
     * This uses the specified frequency interval and retries for up to 6
     * seconds.
     *
     * @param cond the condition
     * @param freq the polling frequency in milliseconds
     * @return {@code true} if the condition was satisfied, otherwise
     * {@code false}
     */
    public static boolean wait(final Callable<Boolean> cond, final int freq) {
        return wait(cond, freq, Math.max(2, 6000 / freq));
    }

    /**
     * Blocks until the specified condition is satisfied (returns {@code true}).
     *
     * @param cond  the condition
     * @param freq  the polling frequency in milliseconds
     * @param tries the maximum number of attempts before this method returns
     *              {@code false}
     * @return if the condition was satisfied, otherwise {@code false}
     */
    public static boolean wait(final Callable<Boolean> cond, final int freq, int tries) {
        tries = Math.max(1, tries + Random.nextInt(-1, 2));

        for (int i = 0; i < tries; i++) {
            try {
                final double f = freq * Random.nextDouble(VARIANCE[0], VARIANCE[1]);
                Thread.sleep(Math.max(5, (int) f));
            } catch (final InterruptedException ignored) {
                return false;
            }

            final boolean r;
            try {
                r = cond.call();
            } catch (final Exception ignored) {
                return false;
            }
            if (r) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sleeps the current thread.
     *
     * @param ms the length of time to sleep in milliseconds, which is adjusted
     *           by a random variance
     * @return the actual amount of time slept in milliseconds, which is subject
     * to system clock accuracy
     */
    public static int sleep(final long ms) {
        if (ms <= 0) {
            Thread.yield();
            return 0;
        }
        final long s = System.nanoTime();
        try {
            Thread.sleep((long) (ms * Random.nextDouble(VARIANCE[0], VARIANCE[1])));
        } catch (final InterruptedException ignored) {
        }
        return (int) ((System.nanoTime() - s) / 1000000L);
    }

    /**
     * Sleeps the current thread for a duration that is 10 times the value of
     * {@link Random#getDelay()}.
     */
    public static void sleep() {
        sleep(Random.getDelay() * 10);
    }

    /**
     * Check A simplified conditional checking task.
     */
    public static abstract class Check implements Callable<Boolean>, Filter<Void> {
        /**
         * {@inheritDoc}
         */
        @Override
        public Boolean call() {
            return poll();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(final Void v) {
            return poll();
        }

        /**
         * Checks if a condition has been met.
         *
         * @return {@code true} if the condition has been met, otherwise
         * {@code false} to try again later.
         */
        public abstract boolean poll();
    }
}