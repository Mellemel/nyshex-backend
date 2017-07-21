package com.nyshex.challenge.ratelimit;

import java.time.Clock;

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
        // TODO
    }

    /**
     * TODO Runtime & Memory Complexity
     * <p>
     * @param key
     *            identifying the resource that is being rate limited, e.g. IP number
     * @return {@code true} if token was consumed
     */
    public boolean tryConsume(final String key) {
        throw new UnsupportedOperationException("TODO implement this");
    }

}
