package org.sample;


import java.io.IOException;

public class LinuxCommands {

    public static void main(String[] args) {
        try {
            Process p = Runtime.getRuntime().exec("memcached -d -p 11215");

            System.out.println(p.isAlive());

            Thread.sleep(5000);

            p.destroyForcibly();
            Runtime.getRuntime().exec("kill -9 $(lsof -t -i:11215)");

            Thread.sleep(5000);

            System.out.println(p.isAlive());

            System.out.println(p.getErrorStream().read());
        } catch (IOException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
