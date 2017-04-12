package com.wpr.zk.hostprovider;

import org.junit.Test;

/**
 * Created by peirong.wpr on 2017/4/12.
 */
public class DynamicHostProviderTest {
    @Test
    public void test_domainUrl_get(){
        String domainUrl = "http://127.0.0.1:8080/zkconfig";
        DynamicHTTPHostProvider provider = new DynamicHTTPHostProvider(domainUrl);
        System.out.println(provider.next(0));
    }
}
