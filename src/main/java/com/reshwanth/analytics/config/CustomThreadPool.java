package com.reshwanth.analytics.config;

import com.reshwanth.analytics.util.EngineConstants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomThreadPool {
    public static final ExecutorService executor =
            Executors.newFixedThreadPool(EngineConstants.Async.FIXED_THREAD_POOL_SIZE);

}
