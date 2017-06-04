package org.sample;


import net.spy.memcached.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestKetmaHasingAndNodeLocater {

    public static void main(String[] args) throws IOException, InterruptedException {
        ConnectionFactoryBuilder consistentBuilder = new ConnectionFactoryBuilder().setHashAlg(DefaultHashAlgorithm.KETAMA_HASH).setOpTimeout(1000L).setDaemon(true).setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT).setFailureMode(FailureMode.Redistribute).setMaxReconnectDelay(30);

        List<String> list = new ArrayList<>();
        MemcachedClient consistentClient = new MemcachedClient(consistentBuilder.build(), AddrUtil.getAddresses("localhost:11211, localhost:11213, localhost:11212"));

        for (int i = 0; i < 20; i++) {
            Thread.sleep(2000);
            String key = UUID.randomUUID().toString();
            list.add(key);
            System.out.println(i + ":will add to: " + consistentClient.getNodeLocator().getPrimary(key));
            consistentClient.add(key, 10000, key);
        }

        Thread.sleep(2000);

        assert 20 == list.size() : "Some items got missed out, try again!";

        Runtime.getRuntime().exec("kill -9 $(lsof -t -i:11211)");

        for (String key : list) {
            assert null != consistentClient.get(key) : "Key not found in memcached";
        }
        consistentClient.shutdown();
    }
}
