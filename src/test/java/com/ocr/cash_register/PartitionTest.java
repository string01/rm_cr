package com.ocr.cash_register;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PartitionTest {

    @Test
    public void partitionEleven(){
        Partition p = new Partition(11);
        List<Map<Double, Integer>> output = p.partition();
        assertEquals(6, output.size());
    }
}
