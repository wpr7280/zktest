package com.wpr.zk.zktest;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperFirstBlood implements Watcher {
	private static  CountDownLatch latch = new CountDownLatch(1);
	private final static Integer  SESSION_TIMEOUT = 5000;
	public static void main(String[] args) {
		try {
			ZooKeeper zooKeeper = new ZooKeeper("192.168.109.130:2181",SESSION_TIMEOUT,new ZooKeeperFirstBlood());
			latch.await();
			System.out.println("zookeeper connection established");
			System.out.println("-------------------------------------");
			System.out.println("sessionId:"+zooKeeper.getSessionId()+"|sessionKey:"+zooKeeper.getSessionPasswd());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void process(WatchedEvent event) {
		System.out.println("receive watched event:"+event);
		System.out.println(event.getState());
		latch.countDown();
	}
}
