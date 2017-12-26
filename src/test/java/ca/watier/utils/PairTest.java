/*
 *    Copyright 2014 - 2017 Yannick Watier
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

import org.junit.Before;
import org.junit.Test;

/**
 * Created by yannick on 6/20/2017.
 */
public class PairTest {

    public static final String TEST_FIRST_VALUE = "TEST_FIRST_VALUE";
    public static final String TEST_SECOND_VALUE = "TEST_SECOND_VALUE";
    private Pair<String, String> pair;

    @Before
    public void setUp() {
        pair = new Pair<>();
    }

    @Test
    public void getFirstValue() {
        pair.setFirstValue(TEST_FIRST_VALUE);
        org.junit.Assert.assertEquals(TEST_FIRST_VALUE, pair.getFirstValue());
    }

    @Test
    public void setSecondValue() {
        pair.setSecondValue(TEST_SECOND_VALUE);
        org.junit.Assert.assertEquals(TEST_SECOND_VALUE, pair.getSecondValue());
    }

}