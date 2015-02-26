/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */

package org.forgerock.bloomfilter.monitoring;

import static junit.framework.Assert.assertTrue;

import org.forgerock.bloomfilter.BloomFilter;
import org.forgerock.bloomfilter.BloomFilters;
import org.forgerock.bloomfilter.ConcurrencyStrategy;
import org.forgerock.guava.common.hash.Funnels;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BloomFilterMonitorTest {

    private BloomFilterMonitor<Integer> testMonitor;

    private ExecutorService threadPool;

    @BeforeMethod
    public void createTestObject() {
        final BloomFilter<Integer> bf = BloomFilters.<Integer>create(Funnels.integerFunnel())
                .withConcurrencyStrategy(ConcurrencyStrategy.COPY_ON_WRITE)
                .withCapacityGrowthFactor(2.0D)
                .withWriteBatchSize(100)
                .build();

        testMonitor = new BloomFilterMonitor<Integer>(bf);
        threadPool = Executors.newFixedThreadPool(16);
    }

    @AfterMethod
    public void printStats() {
        threadPool.shutdownNow();
        System.out.println("Add Stats:");
        System.out.println(testMonitor.getAddStatistics());
        System.out.println("MightContain Stats:");
        System.out.println(testMonitor.getMightContainStatistics());
        System.out.println("Bucket stats:");
        System.out.println(testMonitor.getBloomFilterStatistics());
        System.out.flush();
    }

    @Test
    public void testConcurrency() throws Exception {
        final CountDownLatch latch = new CountDownLatch(16);
        for (int i = 0; i < 16; ++i) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    final Random random = new Random();
                    for (int i = 0; i < 500000; ++i) {
                        final int r = random.nextInt();
                        if (i % 10 == 0) {
                            testMonitor.add(r);
                            assertTrue(testMonitor.mightContain(r));
                        }
                        testMonitor.mightContain(r);
                    }
                    latch.countDown();
                }
            });
        }
        latch.await();
    }
}