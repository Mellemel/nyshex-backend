Rate limiter challenge
======================

General description
-------------------

Implement a rate limiter using the [Token Bucket algoritm](https://en.wikipedia.org/wiki/Token_bucket).

The rate limiter is meant to be used in a micro service to defend against DoS
attacks from individual IPs.


Tasks
-----

 1. Implement the rate limiter (60 minutes)
     * Implement the TODOs in the code.
     * Write test cases that prove your solution and call your functions to
       demonstrate that it works.
     * Document all your assumptions. Explain why you made that assumption. You
       may make the wrong assumption, but if it's well-documented, you'll help us
       see your thought process. Undocumented assumptions can’t help us help you.
 2. Describe concurrent access (10 minutes)
     * Describe how RateLimiter will behave under concurrent access, both with
       identical keys and different keys.
 3. Analyze complexities (10 minutes)
      The analysis should include the average and worst-case complexity along
      with a brief explanation of your reasoning.
     * Describe runtime complexity for all public methods. The descriptions
       should be written in the comments for each method.
     * Describe memory complexity for RateLimiter class. The description should
       be written in the comments on the class.
 4. Consider security implications (10 minutes)
     * If we assume that the method RateLimiter.tryConsume() can be called with
       input from a malicious actor describe possible security issues and propose
       mitigations.
