package org.sample;


import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class ConnectionFactory {

    public static MemcachedClient consistentClient;
    public static MemcachedClient nativeClient;

    static {

        List<InetSocketAddress> memcachedAddresses = AddrUtil.getAddresses("localhost:11211, localhost:11212, localhost:11213");
        ConnectionFactoryBuilder consistentBuilder = new ConnectionFactoryBuilder().setHashAlg(DefaultHashAlgorithm.KETAMA_HASH).setOpTimeout(1000L).setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT).setFailureMode(FailureMode.Redistribute).setMaxReconnectDelay(1);
        ConnectionFactoryBuilder nativeBuilder = new ConnectionFactoryBuilder().setOpTimeout(1000L).setLocatorType(ConnectionFactoryBuilder.Locator.ARRAY_MOD).setFailureMode(FailureMode.Redistribute).setMaxReconnectDelay(1);
        try {
            nativeClient = new MemcachedClient(nativeBuilder.build(), memcachedAddresses);
            consistentClient = new MemcachedClient(consistentBuilder.build(), memcachedAddresses);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
