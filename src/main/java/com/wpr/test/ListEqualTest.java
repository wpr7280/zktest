package com.wpr.test;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by peirong.wpr on 2017/4/11.
 */
public class ListEqualTest {

    @Test
    public void test_list_equal(){
        List<String> listA = Arrays.asList("a","b","c");
        List<String> listB = Arrays.asList("a","b","c");
        if(listA.equals(listB)){
            System.out.println("true");
        }
    }
}
