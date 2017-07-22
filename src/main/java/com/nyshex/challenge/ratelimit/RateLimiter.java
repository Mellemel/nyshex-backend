package com.nyshex.challenge.ratelimit;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;;

/**
 * A rate limiter using the <a href="https://en.wikipedia.org/wiki/Token_bucket">Token Bucket algorithm</a>.
 * <p>
 * A key is meant to represent some unique identifier for which each key has its own bucket.
 * Tokens are added to the bucket every millisecondsPerToken. When the bucket is full,
 * extra tokens are lost. The rate limiter is used to check a key for conformance by calling tryConsume.
 * If there are tokens in a key's bucket, then one token will be consumed and the method will return true,
 * but if the bucket is empty, the method will return false.
 * <p>
 * TODO Concurrent Access
 * <p>
 * TODO Runtime & Memory Complexity
 * <p>
 * TODO Security Implications
 */
public class RateLimiter {

    private ConcurrentHashMap<String, HashMap<String, Long>> bucket = new ConcurrentHashMap<String, HashMap<String, Long>>();
    private final long maxTokens = 10;
    private Clock clock;
    private final long burst;
    private final long millisecondsPerToken;
    private Timer timer;

    /**
     * TODO Runtime & Memory Complexity
     * <p>
     * @param clock
     *            Clock used for getting the time using {@link Clock#instant()}.
     * @param burst
     *            number of tokens allowed in a burst
     * @param millisecondsPerToken
     *            token renew rate
     */
    public RateLimiter(final Clock clock, final long burst, final long millisecondsPerToken) {
        this.clock = clock;
        this.burst = burst;
        this.millisecondsPerToken = millisecondsPerToken;
        this.timer = new Timer(true);
        RateLimiterTimer rlt = new RateLimiterTimer();
        timer.scheduleAtFixedRate(rlt, 0, millisecondsPerToken);
    }

    private class RateLimiterTimer extends TimerTask {
        @Override
        public void run() {
            addTokens(1);
        }
    }

    private void addTokens(long tokens) {
        for (Map.Entry<String, HashMap<String, Long>> e : bucket.entrySet()) {
            String key = e.getKey();
            HashMap<String, Long> map = bucket.get(key);
            long totaltokens = map.get("totalTokens");
            if (totaltokens >= this.maxTokens) {
                continue;
            }
            map.put("totalTokens", totaltokens + tokens);
            bucket.put(key, map);
        }
    }

    public void addSeconds(int seconds) {
        timer.cancel();
        this.clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        addTokens((long) seconds * 1000 / millisecondsPerToken);
        startTimer();
    }

    private void startTimer() {
        timer = new Timer(true);
        RateLimiterTimer rlt = new RateLimiterTimer();
        timer.scheduleAtFixedRate(rlt, 0, millisecondsPerToken);
    }

    /**
     * TODO Runtime & Memory Complexity
     *  
     * <p>
     * @param key
     *            identifying the resource that is being rate limited, e.g. IP number
     * @return {@code true} if token was consumed
     */
    public boolean tryConsume(final String key) {
        boolean isPresent = bucket.containsKey(key);
        Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        HashMap<String, Long> map = new HashMap<String, Long>();
        if (isPresent) {
            map = bucket.get(key);
            long totalTokens = map.get("totalTokens");
            if (totalTokens == 0) {
                return false;
            }
            long lastAccess = map.get("lastAccess");
            long burstTokens = map.get("burstTokens");
            if (lastAccess <= this.clock.millis() + 250) {
                if(burstTokens == 0) {
                    return false;
                }
                map.put("burstTokens", burstTokens - 1);
            } else {
                map.put("burstTokens", this.burst - 1);
            }
            map.put("totalTokens", totalTokens - 1);
            map.put("lastAccess", clock.millis());
            bucket.put(key, map);
        } else {
            map.put("totalTokens", this.maxTokens - 1);
            map.put("burstTokens", (long) this.burst - 1);
            map.put("lastAccess", clock.millis());
            bucket.put(key, map);
        }
        return true;
    }
}