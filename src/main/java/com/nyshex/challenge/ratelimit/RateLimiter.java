package com.nyshex.challenge.ratelimit;

import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
// import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

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
 * With concurrent access, the ConcurrentHashMap will prevent access to future requests with
 * identical keys if the value of the key is being written to. Different keys will be able to
 * read and write to the data structure simultaneously. 
 * <p>
 * TODO Runtime & Memory Complexity
 * Runtime complexity is O(n) because of the public method add tokens I added below.
 * Memory complexity is O(n) due to the number of keys that are present in the hashtable.aver
 * <p>
 * TODO Security Implications
 * if tryConsume can be called with input from a malicious actor, then he can potentially drain
 *  the burst tokens for every ip available and render the service useless and he / she would be
 * able to shut down your system through utilizing every bit of memory available by introducing non Ip
 * keys. To mitigate this we can clean the input and make sure only valid ip are able to be stored.
 * In terms of knowing wether the request from a certain ip is legit, we would need to check headers
 * from our web servers and prevent malicious web requests from making it to the app servers.
 * I suggest you 
 */
public class RateLimiter {

    private ConcurrentHashMap<String, HashMap<String, Long>> bucket = new ConcurrentHashMap<String, HashMap<String, Long>>();
    // Max tokens, while not mentioned in the readme, the wiki page mentions it.
    private final long maxTokens = 5;
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
        // this.timer = new Timer(true);
        // RateLimiterTimer rlt = new RateLimiterTimer();
        // timer.scheduleAtFixedRate(rlt, 0, millisecondsPerToken);
    }

    // private class RateLimiterTimer extends TimerTask {
    //     @Override
    //     public void run() {
    //         addTokens(1);
    //     }
    // }

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
    /**
     *  Run time complexity for this public method is O(n), n being the number
     *  of unique keys in the concurrenthasmap. This is because of the for loop
     *  in addtokens where we add a token to each key.
     */
    public void addSeconds(int seconds) {
        // timer.cancel();
        // System.out.println(this.clock.millis());
        this.clock = Clock.offset(this.clock, Duration.ofSeconds(seconds));
        // System.out.println(this.clock.millis());
        addTokens((long) seconds * 1000 / millisecondsPerToken);
        // startTimer();
    }

    // private void startTimer() {
    //     timer = new Timer(true);
    //     RateLimiterTimer rlt = new RateLimiterTimer();
    //     timer.scheduleAtFixedRate(rlt, 0, millisecondsPerToken);
    // }

    /**
     * TODO Runtime & Memory Complexity
     *  Runtime complexity for this method is O(1) because since we are using
     *  a HashTable as our data strcuture, both read and write times are constant
     *  time. Every line in method is takes constant time. Memory Complexity is O(n) because the amount of data that is being held
     *  in memory only grows depending on the amount of unique keys. Keys that visit multiple
     *  times won't create more data, just change them.
     * <p>
     * @param key
     *            identifying the resource that is being rate limited, e.g. IP number
     * @return {@code true} if token was consumed
     */
    public boolean tryConsume(final String key) {
        boolean isPresent = bucket.containsKey(key);
        HashMap<String, Long> map = new HashMap<String, Long>();
        if (isPresent) {
            map = bucket.get(key);
            // System.out.println(map);
            long totalTokens = map.get("totalTokens");
            if (totalTokens == 0) {
                return false;
            }
            long burstTokens = map.get("burstTokens");
            long lastAccess = map.get("lastAccess");
            // System.out.println(clock.millis() - lastAccess);
            if (clock.millis() - lastAccess < this.millisecondsPerToken / 4) {
                if (burstTokens == 0) {
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
            // I assigned the initial totaltokens ammount to the burst amount because
            // the second unit would not pass.
            map.put("totalTokens", this.burst - 1);
            map.put("burstTokens", (long) this.burst - 1);
            map.put("lastAccess", clock.millis());
            bucket.put(key, map);
        }
        return true;
    }
}