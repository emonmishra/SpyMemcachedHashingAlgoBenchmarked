package org.sample;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MyBenchmarkV2 {


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testAsyncGetByUsingNativeHashing(MyState state) {

        state.nativeClient.asyncGet("123");

    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testAyncGetUsingConsistentHashing(MyState state) {


        state.consistentClient.asyncGet("123");

    }

    @State(Scope.Benchmark)
    public static class MyState {

        public MemcachedClient consistentClient;
        public MemcachedClient nativeClient;
        public String key;
        public String[] keys = new String[1000];
        private List<InetSocketAddress> memcachedAddresses = AddrUtil.getAddresses("localhost:11211");
        private ConnectionFactoryBuilder consistentBuilder = new ConnectionFactoryBuilder().setHashAlg(DefaultHashAlgorithm.KETAMA_HASH).setOpTimeout(1000L).setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT).setFailureMode(FailureMode.Redistribute).setMaxReconnectDelay(1);
        private ConnectionFactoryBuilder nativeBuilder = new ConnectionFactoryBuilder().setOpTimeout(1000L).setLocatorType(ConnectionFactoryBuilder.Locator.ARRAY_MOD).setFailureMode(FailureMode.Redistribute).setMaxReconnectDelay(1);

        public MyState() {


        }

        @Setup(Level.Trial)
        public void doSetup() {
            try {
                consistentClient = new MemcachedClient(consistentBuilder.build(), memcachedAddresses);
                nativeClient = new MemcachedClient(nativeBuilder.build(), memcachedAddresses);

                for (int i = 0; i < 1000; i++) {
                    keys[i] = UUID.randomUUID().toString();
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }

        @TearDown(Level.Trial)
        public void doTeardown() {

            consistentClient.shutdown();
            nativeClient.shutdown();

        }

    }

}
