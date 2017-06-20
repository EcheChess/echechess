/*
 *    Copyright 2014 - 2016 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ca.watier.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Yannick on 5/4/2016.
 */
public class MultiArrayMapTest {
    @Test
    public void clear() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");
        map.put(20, "20");
        map.put(30, "30");
        map.put(40, "40");
        map.clear();

        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void keySet() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");
        map.put(20, "20");
        map.put(30, "30");
        map.put(40, "40");

        assertThat(map.keySet()).containsOnly(10, 20, 30, 40);
    }

    @Test
    public void containsKey() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");
        map.put(20, "20");
        map.put(30, "30");
        map.put(40, "40");

        assertThat(map.containsKey(10)).isTrue();
        assertThat(map.containsKey(50)).isFalse();
    }

    @Test
    public void containsValue() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");
        map.put(20, "20");
        map.put(30, "30");
        map.put(40, "40");

        assertThat(map.containsValue("10")).isNotEmpty().containsOnly(10);
        assertThat(map.containsValue("50")).isEmpty();
    }

    @Test
    public void addItemTest() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");
        map.put(20, "20");
        map.put(30, "30");
        map.put(40, "40");

        Assert.assertTrue("The map doesn't contains all the value !", map.size() == 4);
        System.out.println("addItemTest() = " + map);
    }

    @Test
    public void addItemWithSameKeyTest() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");
        map.put(10, "10.1");
        map.put(10, "10.2");
        map.put(10, "10.3");
        map.put(10, "10.4");
        map.put(20, "20");
        map.put(30, "30");
        map.put(40, "40");
        map.put(40, "40.1");
        map.put(40, "40.2");

        List<String> valuesTen = map.get(10);
        List<String> valuesFourty = map.get(40);

        Assert.assertTrue("The map doesn't contains all the value !", map.size() == 4);
        Assert.assertTrue("The map doesn't contains all the value for the key (10) !", valuesTen.size() == 5);
        Assert.assertTrue("The map doesn't contains all the value for the key (40) !", valuesFourty.size() == 3);
        System.out.println("addItemWithSameKeyTest() = " + map);
    }


    @Test
    public void mergeTest() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");
        map.put(20, "20");
        map.put(30, "30");
        map.put(40, "40");

        MultiArrayMap<Integer, String> map2 = new MultiArrayMap<Integer, String>();
        map2.put(50, "50");
        map2.put(60, "60");
        map2.put(70, "70");
        map2.put(80, "80");

        map.putAll(map2);

        Assert.assertTrue("The map doesn't contains the newer values !", map.size() == 8);

        assertThat(map.values()).containsOnly("10", "20", "30", "40", "50", "60", "70", "80");
        assertThat(map2.values()).containsOnly("50", "60", "70", "80");
        System.out.println("mergeTest() = " + map);
    }

    @Test
    public void removeItemTest() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");
        map.put(10, "10.1");
        map.put(10, "10.2");
        map.put(10, "10.3");
        map.put(10, "10.4");
        map.put(20, "20");
        map.put(30, "30");
        map.put(40, "40");
        map.put(40, "40.1");
        map.put(40, "40.2");

        map.remove(10);
        map.remove(40);

        Assert.assertTrue("The map doesn't contains all the value !", map.size() == 2);
        System.out.println("removeItemTest() = " + map);
    }

    @Test
    public void valueNotPresentTest() throws Exception {
        MultiArrayMap<Integer, String> map = new MultiArrayMap<Integer, String>();
        map.put(10, "10");

        Assert.assertTrue("The map doesn't contains the newer values !", map.get(20) == null);
    }
}