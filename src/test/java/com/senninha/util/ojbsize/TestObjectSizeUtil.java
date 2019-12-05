package com.senninha.util.ojbsize;

import com.senninha.util.objsize.ObjectSizeUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.junit.Test;

import java.util.HashSet;

/**
 * Coded by senninha on 2019/12/3
 */
public class TestObjectSizeUtil {
    @Test
    public void testSizeOfUtil() {
        Object object = new TestObjectSize();
        System.out.println(ObjectSizeUtil.sizeOfObj(
                object, new HashSet<>(256)));
        System.out.println(RamUsageEstimator.sizeOf(object));
    }
}
