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

package ca.watier.responses;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yannick on 6/20/2017.
 */
public class BooleanResponseTest {

    public static final String MESSAGE = "Message";
    private BooleanResponse booleanResponse;

    @Before
    public void setUp() {
        booleanResponse = new BooleanResponse(true, MESSAGE);
    }

    @Test
    public void isResponse() {
        Assert.assertTrue(booleanResponse.isResponse());
    }

    @Test
    public void setResponse() {
        booleanResponse.setResponse(false);
        Assert.assertFalse(booleanResponse.isResponse());
    }

    @Test
    public void getMessage() {
        String message = "A newer message";
        booleanResponse.setMessage(message);
        Assert.assertEquals(message, booleanResponse.getMessage());
    }
}