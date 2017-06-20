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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by yannick on 6/20/2017.
 */
public class BaseUtilsTest {
    @Test
    public void getSafeInteger() throws Exception {
        assertEquals(10, BaseUtils.getSafeInteger(10));
        assertEquals(20, BaseUtils.getSafeInteger(20));
        assertEquals(30, BaseUtils.getSafeInteger(30));
        assertEquals(40, BaseUtils.getSafeInteger(40));
        assertEquals(0, BaseUtils.getSafeInteger(null));
    }
}