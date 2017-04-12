package com.wpr.zk.hostprovider;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.client.HostProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 利用HTTP请求来获取zk服务器地址的功能
 * <p>同机房地址优先返回<p/>
 * <p>这个解析过程其实是可以分离的 TODO </p>
 * Created by peirong.wpr on 2017/4/11.
 */
public class DynamicHTTPHostProvider implements HostProvider {
    private static final Logger log = LoggerFactory.getLogger(DynamicHTTPHostProvider.class);

    /**
     * zk服务器列表
     */
    private final List<InetSocketAddress> domainAddresses = new ArrayList(5);
    /***
     * 默认的查找域名的URL
     */
    private final String DEFAULT_ADDRESS_URL = "http://127.0.0.1:8080";
    /**
     * 默认的服务器端口
     */
    private final int DEFAULT_ADDRESS_PORT = 2181;
    /**
     * 请求的服务器URL
     */
    private static  String ADDRESS_SERVER_URL ;
    /**
     * 从服务器端获取的zk地址里列表  ip:port的形式
     */
    private static List<String> serverUrls = new ArrayList<String>();

    private boolean isStart = false;

    public DynamicHTTPHostProvider(String domainURL)  {
        ADDRESS_SERVER_URL = domainURL;
        start();
    }

    public DynamicHTTPHostProvider() {
        ADDRESS_SERVER_URL = DEFAULT_ADDRESS_URL;
        start();
    }

    public synchronized void start(){
        //这个其实可以重新写到一个类里面
        if(isStart){
            log.warn("DynamicHTTPHostProvider already run");
            return ;
        }
        GetServerListTask getServersTask = new GetServerListTask(ADDRESS_SERVER_URL);
        for (int i = 0; i < 3 && domainAddresses.isEmpty(); ++i) {
            getServersTask.run();
            try {
                Thread.sleep(100L);
            } catch (Exception e) {
            }
        }
        if (domainAddresses.isEmpty()) {
            log.error("DynamicHTTPHostProvider-0001|cannnot get zookeeper address");
            throw new RuntimeException("fail to get zk-server serverlist! env:" + ADDRESS_SERVER_URL);
        }
        TimerService.scheduleWithFixedDelay(getServersTask, 0L, 30L, TimeUnit.SECONDS);
        isStart = true;
    }
    public int size() {
        return domainAddresses.size();
    }

    public InetSocketAddress next(long l) {
        //这个地方可以加入控制策略 本次不加了 TODO
        if(CollectionUtils.isEmpty(domainAddresses)){
            log.error("getzkServerList error-no server address defined");
            return null;
        }
        Random random = new Random();
        return this.domainAddresses.get(random.nextInt(domainAddresses.size()));
    }

    public void onConnected() {
        log.warn("connected zk server");
    }

    class GetServerListTask implements Runnable {
        final String url;

        GetServerListTask(String url) {
            this.url = url;
        }
        public void run() {
            //获取服务器地址
            List<String> result = getZkServerList();
            updateIfChanged(result);
        }

        /**
         * 判断zk服务器地址是否发生变化
         * <p>如果没有变化，直接返回<p/>
         * <p>如果发生了变化，做相应的处理</p>
         * @param result
         */
        private synchronized void  updateIfChanged(List<String> result) {
            if(CollectionUtils.isEmpty(result)){
                log.error(" getzkServerList- [check-serverlist] error");
                return;
            }
            if(CollectionUtils.isEqualCollection(result,serverUrls)){
                //zk服务器地址没有发生变化
                return ;
            }
            serverUrls = new ArrayList<String>(result);
            //更新domainAddresses列表
            domainAddresses.clear();
            for(String domainUrl : serverUrls){
                int port = DEFAULT_ADDRESS_PORT;
                String ip = null;
                String[] $domainUrl = domainUrl.split(":");
                if($domainUrl.length <= 1){
                    //只有ip的形式，默认端口是8080
                    ip = $domainUrl[0];
                }else{
                    ip = $domainUrl[0];
                    port = Integer.valueOf($domainUrl[1]);
                }
                try{
                    domainAddresses.add(InetSocketAddress.createUnresolved(ip, port));
                    //打散
                    Collections.shuffle(domainAddresses);
                }catch (Exception e){
                    log.error("getzkServerList - cannot resolve address -please check -domainUrl:{}",domainUrl,e);
                    continue;
                }
            }
            //TODO 应该通知对应的zk连接 暂时不做
        }

        /***
         *  从地址服务器拿地址列表，返回NULL表示遇到服务器故障。
          */
       List<String> getZkServerList() {
            try {
                HttpSimpleClient.HttpResult httpResult = HttpSimpleClient.httpGet(url, null, null, null, 3000);

                if (200 == httpResult.code) {
                    log.warn("httpResult content:{}",httpResult.content);
                    List<String> lines = IOUtils.readLines(new StringReader(httpResult.content));
                    List<String> result = new ArrayList<String>(lines.size());
                    for (String line : lines) {
                        if (StringUtils.isEmpty(line)) {
                            continue;
                        } else {
                            result.add(line);
                        }
                    }
                    return result;
                } else {
                    log.error(url, "getzkServerList- [check-serverlist] error. code={}", httpResult.code);
                    return null;
                }
            } catch (IOException e) {
                log.error("getzkServerList-[check-serverlist] - url:{}", url, e);
                return null;
            }
        }
    }
}
