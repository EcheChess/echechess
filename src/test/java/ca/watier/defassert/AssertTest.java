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

package ca.watier.defassert;

import ca.watier.defassert.utils.EmptyObj;
import ca.watier.utils.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by yannick on 2/24/2017.
 */
public class AssertTest {


    @Test
    public void assertNotEqualsString() throws Exception {

        try {
            Assert.assertNotEquals("", "a");
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNotEquals("a", null);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNotEquals(null, "a");
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNotEquals("a", "a");
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNotEquals(null, null);
            fail();
        } catch (AssertionError ignored) {
        }

    }

    /**
     * Not the best way, but had to find a way to throw an exception in a try without interfering with the "AssertionError"
     * (Cannot use "org.junit.Assert.fail() into a try with a catch on an "AssertionError")
     *
     * @throws Exception
     */
    private void fail() throws Exception {
        throw new Exception("The test has failed !");
    }

    @Test
    public void assertEqualsString() throws Exception {

        try {
            Assert.assertEquals("a", "");
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertEquals(null, "");
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertEquals("", null);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertEquals("a", "a");
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertEquals(null, null);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

    }

    @Test
    public void assertNumberBetweenTo() throws Exception {
        try {
            Assert.assertNumberBetweenTo(10, 0, 50);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberBetweenTo(10d, 0d, 50d);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberBetweenTo(1000, 0, 50);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenTo(1000d, 0d, 50d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenTo(-1000, 0, 50);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenTo(-1000d, 0d, 50d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenTo(0, 0, 50);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenTo(0d, 0d, 50d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenTo(50, 0, 50);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenTo(50d, 0d, 50d);
            fail();
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void assertNumberBetweenOrEqualsTo() throws Exception {
        try {
            Assert.assertNumberBetweenOrEqualsTo(10, 0, 50);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(10d, 0d, 50d);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(0, 0, 50);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(0d, 0d, 50d);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(50, 0, 50);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(50d, 0d, 50d);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(1000, 0, 50);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(1000d, 0d, 50d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(-1000, 0, 50);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberBetweenOrEqualsTo(-1000d, 0d, 50d);
            fail();
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void assertNumberSuperiorTo() throws Exception {
        try {
            Assert.assertNumberSuperiorTo(1000000d, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorTo(10, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorTo(5, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorTo(5d, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorTo(10, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorTo(10d, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorTo(11, 10);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberSuperiorTo(11d, 10d);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberSuperiorTo(1000000000, 10);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberSuperiorTo(1000000000d, 10d);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }
    }


    @Test
    public void assertNumberSuperiorOrEqualsTo() throws Exception {
        try {
            Assert.assertNumberSuperiorOrEqualsTo(1000000d, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(10, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(5, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(5d, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(10, 10);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(10d, 10d);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(11, 10);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(11d, 10d);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(1000000000, 10);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberSuperiorOrEqualsTo(1000000000d, 10d);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }
    }

    @Test
    public void assertNumbersSameType() throws Exception {

        try {
            Assert.assertNumbersSameType(10, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumbersSameType(10d, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumbersSameType(10, 10);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumbersSameType(10d, 10d);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }
    }

    @Test
    public void assertNumbersNotSameType() throws Exception {
        try {
            Assert.assertNumbersNotSameType(10, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumbersNotSameType(10d, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumbersNotSameType(10, 10d);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumbersNotSameType(10d, 10);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }
    }

    @Test
    public void assertNumberInferiorTo() throws Exception {

        try {
            Assert.assertNumberInferiorTo(10, 1000000d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorTo(10d, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorTo(10, 5);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorTo(10d, 5d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorTo(10, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorTo(10d, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorTo(10, 11);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberInferiorTo(10d, 11d);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberInferiorTo(10, 1000000000);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberInferiorTo(10d, 1000000000d);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }
    }


    @Test
    public void assertNumberInferiorOrEqualsTo() throws Exception {

        try {
            Assert.assertNumberInferiorOrEqualsTo(10, 1000000d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10d, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10, 5);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10d, 5d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10, 10);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10d, 10d);
        } catch (AssertionError ignored) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10, 11);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10d, 11d);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10, 1000000000);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberInferiorOrEqualsTo(10d, 1000000000d);
        } catch (AssertionError ae) {
            org.junit.Assert.fail();
        }
    }

    @Test
    public void assertIsNull() throws Exception {
        try {
            Assert.assertNull();
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNull(new Object());
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNull(null, new Object(), null);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNull(null, null, null);
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }
    }


    @Test
    public void assertNumberNotEquals() throws Exception {

        try {
            Assert.assertNumberNotEquals(10, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberNotEquals(10d, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberNotEquals(11, 10);
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberNotEquals(11d, 10d);
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }
    }

    @Test
    public void assertNumberEquals() throws Exception {

        try {
            Assert.assertNumberEquals(11, 10);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberEquals(11d, 10d);
            fail();
        } catch (AssertionError ignored) {
        }

        try {
            Assert.assertNumberEquals(10, 10);
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNumberEquals(10d, 10d);
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }
    }

    @Test
    public void assertNotNull() throws Exception {
        try {
            Assert.assertNotNull(10);
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertNotNull();
            org.junit.Assert.fail();
        } catch (AssertionError ignored) {
        }


        try {
            Assert.assertNotNull(new Object(), null, new Object());
            fail();
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void assertOfType() throws Exception {

        try {
            Assert.assertType(5);
            fail();
        } catch (AssertionError iae) {
        }

        try {
            Assert.assertType(10, Integer.class);
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }

        try {
            Assert.assertType(20, Double.class);
            fail();
        } catch (AssertionError iae) {
        }

        try {
            Assert.assertType(new Object(), Object.class);
        } catch (AssertionError iae) {
            org.junit.Assert.fail();
        }
    }


    @Test
    public void assertNotEmpty() throws Exception {
        String string = "";
        String[] array = {};
        int[] primitiveArray = {};
        ArrayList<Object> list = new ArrayList<Object>();
        TreeSet<Object> set = new TreeSet<Object>();
        Vector<Object> vector = new Vector<Object>();
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        EmptyObj emptyObj = new EmptyObj(true);

        //Must be empty
        for (Object obj : Arrays.asList(string, array, primitiveArray, list, set, vector, map, emptyObj)) {
            try {
                Assert.assertNotEmpty(obj);
                fail();
            } catch (AssertionError ignored) {
            }
        }

        string = "\0";
        array = new String[]{string};
        primitiveArray = new int[]{10, 20, 30};
        list.add(string);
        set.add(string);
        vector.add(string);
        map.put(string, string);
        emptyObj = new EmptyObj(false);

        //Must be not empty
        for (Object obj : Arrays.asList(string, array, primitiveArray, list, set, vector, map, emptyObj)) {
            try {
                Assert.assertNotEmpty(obj);
            } catch (AssertionError iae) {
                org.junit.Assert.fail();
            }
        }
    }


    @Test
    public void assertEmpty() throws Exception {
        String string = "";
        String[] array = {};
        int[] primitiveArray = {};
        ArrayList<Object> list = new ArrayList<Object>();
        TreeSet<Object> set = new TreeSet<Object>();
        Vector<Object> vector = new Vector<Object>();
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        EmptyObj emptyObj = new EmptyObj(true);

        //Must be assertEmpty
        for (Object obj : Arrays.asList(string, array, primitiveArray, list, set, vector, map, emptyObj)) {
            try {
                Assert.assertEmpty(obj);
            } catch (AssertionError iae) {
                org.junit.Assert.fail();
            }
        }

        string = "\0";
        array = new String[]{string};
        primitiveArray = new int[]{10, 20, 30};
        list.add(string);
        set.add(string);
        vector.add(string);
        map.put(string, string);
        emptyObj = new EmptyObj(false);

        //Must be not assertEmpty
        for (Object obj : Arrays.asList(string, array, primitiveArray, list, set, vector, map, emptyObj)) {
            try {
                Assert.assertEmpty(obj);
                fail();
            } catch (AssertionError ignored) {
            }
        }
    }


    @Test
    public void isEmpty() {
        org.junit.Assert.assertTrue(Assert.isEmpty(""));
        org.junit.Assert.assertFalse(Assert.isEmpty("\0x00"));
        org.junit.Assert.assertTrue(Assert.isEmpty(new EmptyObj(true)));
        org.junit.Assert.assertFalse(Assert.isEmpty(new EmptyObj(false)));
        org.junit.Assert.assertNull(Assert.isEmpty(new Object()));
    }
}