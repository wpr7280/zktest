package com.wpr.zk.zktest;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String a ="192.168.0.1:2181/wpr;192.168.0.2:2181/wpr";
        int off = a.indexOf(47);
        if(off >= 0) {
            System.out.println("aaa");
        }
    }
}
