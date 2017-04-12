package com.wpr.concurrent;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 生产者、消费者模式的弊端
 * Created by peirong.wpr on 2017/3/21.
 */
public class ProductConsumerModel {
    static class Producer implements Runnable{
        private BlockingQueue<String> queue;

        public Producer(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        public BlockingQueue<String> getQueue() {
            return queue;
        }

        public void setQueue(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        public void run() {
                try {
                    queue.put("aaa");
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }
    static class Consumer implements Runnable{
        private BlockingQueue<String> queue;

        public BlockingQueue<String> getQueue() {
            return queue;
        }

        public void setQueue(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        public void run() {
                try {
                    System.out.println(Thread.currentThread().getName()+queue.take());
                    System.out.println(System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void main(String[] args) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>() ;
        Producer p = new Producer(queue);
        Thread thread = new Thread(p);
        System.out.println("start"+System.currentTimeMillis());
        for(int i=0;i<10000;i++){
            Consumer consumer = new Consumer();
            consumer.setQueue(queue);
            Thread t = new Thread(consumer);
            t.setName(""+i);
            t.start();
        }
        thread.start();
    }
}
