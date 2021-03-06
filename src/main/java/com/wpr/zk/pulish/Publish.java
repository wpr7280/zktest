package com.wpr.zk.pulish;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 利用zk来模拟发布订阅模式
 * Created by peirong.wpr on 2017/4/5.
 */
public class Publish implements Watcher{
    private static CountDownLatch latch =  new CountDownLatch(1);
    private static Stat stat = new Stat();
    private static ZooKeeper zk =null;
    private final static Integer  SESSION_TIMEOUT = 5000;

    public static void main(String[] args) {
        try {
            String path  ="/publish";
             zk =  new ZooKeeper("192.168.109.130:2181",SESSION_TIMEOUT,new Publish());
            latch.await();
            System.out.println("zk connection");
            byte[]  temp = zk.getData(path,true,stat);
            System.out.println("init data :pulish node data"+new String(temp));
            int i=0;
            while(true){
                System.out.println( "publish new Data:"+i);
                zk.setData(path,String.valueOf(i).getBytes(),-1);
                Thread.sleep(5000L);
                i++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void process(WatchedEvent event) {
        if(Event.KeeperState.SyncConnected == event.getState()){
            System.out.println("receive watched event:"+event);
            System.out.println(event.getState());
            latch.countDown();
        }
    }
}
