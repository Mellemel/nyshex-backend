package com.nyshex.challenge.ratelimit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

/**
 * Unit tests
 */
public class TestRateLimiter {

    /**
     * Test RateLimiter with fixed time and a burst of 2
     */
    @Test
    public void testBurst2() {
        final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        final RateLimiter rateLimiter = new RateLimiter(clock, 2, 1000);
        assertTrue(rateLimiter.tryConsume("127.0.0.1"));
        assertTrue(rateLimiter.tryConsume("127.0.0.1"));
        assertFalse(rateLimiter.tryConsume("127.0.0.1"));
        assertFalse(rateLimiter.tryConsume("127.0.0.1"));
    }

    /**
     * Test RateLimiter with fixed time two keys
     */
    @Test
    public void testTwoKeys() {
        final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        final RateLimiter rateLimiter = new RateLimiter(clock, 1, 1000);
        assertTrue(rateLimiter.tryConsume("127.0.0.1"));
        assertFalse(rateLimiter.tryConsume("127.0.0.1"));
        assertTrue(rateLimiter.tryConsume("10.0.0.1"));
        assertFalse(rateLimiter.tryConsume("10.0.0.1"));
    }

    /**
     * General test of RateLimiter
     */
    @Test
    public void testRateLimiter() {
        final Clock clock = null; // TODO create test clock
        final RateLimiter rateLimiter = new RateLimiter(clock, 2, 1000);
        assertTrue(rateLimiter.tryConsume("127.0.0.1"));
        assertTrue(rateLimiter.tryConsume("127.0.0.1"));
        assertFalse(rateLimiter.tryConsume("127.0.0.1"));
        // TODO add 1 second to the clock
        assertTrue(rateLimiter.tryConsume("127.0.0.1"));
        assertFalse(rateLimiter.tryConsume("127.0.0.1"));
        // TODO add 2 seconds to the clock
        assertTrue(rateLimiter.tryConsume("127.0.0.1"));
        assertTrue(rateLimiter.tryConsume("127.0.0.1"));
        assertFalse(rateLimiter.tryConsume("127.0.0.1"));
    }

    // TODO add more tests

}
