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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by yannick on 2/24/2017.
 */
public class Assert {

    public static final String ERROR_OBJECTS_ARE_EQUALS = "The %s are equals !";
    private static final String ERROR_SUPERIOR = "%s must be superior THAN %s !";
    private static final String ERROR_SUPERIOR_EQUALS = "%s must be superior or equals THAN %s !";
    private static final String ERROR_INFERIOR = "%s must be inferior THAN %s !";
    private static final String ERROR_INFERIOR_EQUALS = "%s must be inferior or equals THAN %s !";
    private static final String ERROR_CANNOT_BE_NULL = "The object cannot be null !";
    private static final String ERROR_MUST_BE_NULL = "The object must be null !";
    private static final String ERROR_NUMBER_MUST_BE_EQUALS = "The number must be equals !";
    private static final String ERROR_NUMBER_MUST_NOT_BE_EQUALS = "The number must not be equals !";
    private static final String ERROR_OBJECT_IS_NOT_OF_THE_REQUESTED_TYPE = "The object is not of the requested type !";
    private static final String ERROR_OBJECT_NEED_TO_BE_EMPTY = "The object need to be empty !";
    private static final String ERRROR_OBJECT_CANNOT_BE_EMPTY = "The object cannot be empty !";
    private static final String ERROR_VALUES_MUST_BE_THE_SAME_TYPE = "The values must be the same type !";
    private static final String ERROR_VALUE_MUST_BE_BETWEEN_OR_EQUALS_TO = "%s MUST be between or equals to %s and %s";
    private static final String ERROR_VALUE_MUST_BE_BETWEEN = "%s MUST be between %s and %s";
    private static final String ERROR_OBJECTS_ARE_NOT_EQUALS = "The %s are not equals !";

    /**
     * Check if the string are equals
     *
     * @param first
     * @param second
     * @throws IllegalArgumentException
     */
    public static void assertEquals(String first, String second) throws AssertionError {
        if (first == null && second == null) {
            return;
        }
        assertNotNull(first, second);

        if (!first.equals(second)) {
            throw new AssertionError(String.format(ERROR_OBJECTS_ARE_NOT_EQUALS, "strings"));
        }
    }

    /**
     * Check if the object is null, throw an exception if it is the case.
     *
     * @param obj
     * @throws IllegalArgumentException
     */
    public static void assertNotNull(Object... obj) throws AssertionError {

        if (obj == null) {
            throw new AssertionError(ERROR_CANNOT_BE_NULL);
        }

        for (Object o : obj) {
            if (o == null) {
                throw new AssertionError(ERROR_CANNOT_BE_NULL);
            }
        }
    }

    /**
     * Check if the string are not equals
     *
     * @param first
     * @param second
     * @throws IllegalArgumentException
     */
    public static void assertNotEquals(String first, String second) throws AssertionError {

        if (first == null && second == null || (first != null && first.equals(second)) || (second != null && second.equals(first))) {
            throw new AssertionError(String.format(ERROR_OBJECTS_ARE_EQUALS, "strings"));
        }
    }

    /**
     * Check if the number is superior than the other number, throw an exception if it is not the case.
     *
     * @param value
     * @param lowestValue
     * @throws IllegalArgumentException
     */
    public static void assertNumberSuperiorTo(Number value, Number lowestValue) throws AssertionError {
        assertNotNull(value, lowestValue);
        assertNumberNotEquals(value, lowestValue);
        assertSuperiorInferiorNumber(value, lowestValue, true, false);
    }

    /**
     * Check if the number is superior than the other number, throw an exception if it is not the case.
     *
     * @param value
     * @param lowestValue
     * @throws IllegalArgumentException
     */
    public static void assertNumberInferiorTo(Number value, Number lowestValue) throws AssertionError {
        assertNotNull(value, lowestValue);
        assertNumberNotEquals(value, lowestValue);
        assertSuperiorInferiorNumber(value, lowestValue, false, false);
    }

    /**
     * Check if the number is superior or equals than the other number, throw an exception if it is not the case.
     *
     * @param value
     * @param lowestValue
     * @throws IllegalArgumentException
     */
    public static void assertNumberSuperiorOrEqualsTo(Number value, Number lowestValue) throws AssertionError {
        assertNotNull(value, lowestValue);
        assertSuperiorInferiorNumber(value, lowestValue, true, true);
    }

    /**
     * Check if the number is superior or equals than the other number, throw an exception if it is not the case.
     *
     * @param value
     * @param lowestValue
     * @throws IllegalArgumentException
     */
    public static void assertNumberInferiorOrEqualsTo(Number value, Number lowestValue) throws AssertionError {
        assertNotNull(value, lowestValue);
        assertSuperiorInferiorNumber(value, lowestValue, false, true);
    }

    /**
     * Check if the number is between the two other numbers, throw an exception if it is not the case.
     *
     * @param value
     * @param lowestValue
     * @param highestNumber
     * @throws IllegalArgumentException
     */
    public static void assertNumberBetweenTo(Number value, Number lowestValue, Number highestNumber) throws AssertionError {
        assertNotNull(value, lowestValue, highestNumber);
        assertNumbersSameType(value, lowestValue, highestNumber);
        assertBetweenNumber(value, lowestValue, highestNumber, false);
    }

    /**
     * Check if the number is between or equals the two other numbers, throw an exception if it is not the case.
     *
     * @param value
     * @param lowestValue
     * @param highestNumber
     * @throws IllegalArgumentException
     */
    public static void assertNumberBetweenOrEqualsTo(Number value, Number lowestValue, Number highestNumber) throws AssertionError {
        assertNotNull(value, lowestValue);
        assertNumbersSameType(value, lowestValue, highestNumber);
        assertBetweenNumber(value, lowestValue, highestNumber, true);
    }

    /**
     * Check is the numbers are equals
     *
     * @param firstNumber
     * @param secondNumber
     * @throws AssertionError
     */
    public static void assertNumberEquals(Number firstNumber, Number secondNumber) throws AssertionError {
        assertNotNull(firstNumber, secondNumber);
        assertNumbersSameType(firstNumber, secondNumber);

        if (!firstNumber.equals(secondNumber)) {
            throw new AssertionError(ERROR_NUMBER_MUST_BE_EQUALS);
        }
    }

    /**
     * Check if the numbers are the same type (floating vs integer)
     *
     * @param numbers
     */
    public static void assertNumbersSameType(Number... numbers) {
        assertNotEmpty(numbers);

        for (Number number : numbers) {
            for (Number innerNumber : numbers) {
                if (!number.getClass().equals(innerNumber.getClass())) {
                    throw new AssertionError(ERROR_VALUES_MUST_BE_THE_SAME_TYPE);
                }
            }
        }
    }

    /**
     * This assert try to find a "isEmpty" method. (Uses reflection when the type is not known)
     *
     * @param obj
     * @throws IllegalArgumentException
     */
    public static void assertNotEmpty(Object obj) throws AssertionError {
        assertNotNull(obj);

        if (isEmpty(obj)) {
            throw new AssertionError(ERRROR_OBJECT_CANNOT_BE_EMPTY);
        }
    }

    /**
     * A utility function to check if the value is empty or not, null if undefined
     *
     * @param obj
     * @throws IllegalArgumentException
     */
    public static Boolean isEmpty(Object obj) throws AssertionError {
        assertNotNull(obj);

        boolean isString = obj instanceof String;
        boolean isArray = obj.getClass().isArray();
        boolean isCollection = obj instanceof Collection;
        boolean isMap = obj instanceof Map;
        Boolean value = false;

        if (isString && "".equals(obj)) {
            value = true;
        } else if (isArray && Array.getLength(obj) == 0) {
            value = true;
        } else if (isCollection && ((Collection) obj).isEmpty()) {
            value = true;
        } else if (isMap && ((Map) obj).isEmpty()) {
            value = true;
        } else if (!isString && !isArray && !isMap && !isCollection) {
            try {
                Class<?> clazz = obj.getClass();
                Method isEmptyMethod = clazz.getMethod("isEmpty");
                Boolean isEmpty = (Boolean) isEmptyMethod.invoke(obj);

                value = (isEmpty == null || isEmpty);
            } catch (Exception e) {
                value = null;
            }
        }
        return value;
    }

    /**
     * Check is the numbers are not equals
     *
     * @param firstNumber
     * @param secondNumber
     * @throws AssertionError
     */
    public static void assertNumberNotEquals(Number firstNumber, Number secondNumber) throws AssertionError {
        assertNotNull(firstNumber, secondNumber);
        assertNumbersSameType(firstNumber, secondNumber);

        if (firstNumber.equals(secondNumber)) {
            throw new AssertionError(ERROR_NUMBER_MUST_NOT_BE_EQUALS);
        }
    }

    /**
     * Check if the object is null, throw an exception if it is not the case.
     *
     * @param obj
     * @throws IllegalArgumentException
     */
    public static void assertNull(Object... obj) throws AssertionError {
        if (obj == null) {
            return;
        }

        for (Object o : obj) {
            if (o != null) {
                throw new AssertionError(ERROR_MUST_BE_NULL);
            }
        }
    }

    /**
     * Check if the object is of the type (class), throw an exception if not the case.
     *
     * @param obj
     * @param type
     * @throws IllegalArgumentException
     */
    public static void assertType(Object obj, Class<?>... type) throws AssertionError {
        assertNotNull(obj);
        assertNotEmpty(type);

        if (!Arrays.asList(type).contains(obj.getClass())) {
            throw new AssertionError(ERROR_OBJECT_IS_NOT_OF_THE_REQUESTED_TYPE);
        }
    }

    /**
     * This assert try to find a "isEmpty" method. (Uses reflection when the type is not known)
     *
     * @param obj
     * @throws AssertionError
     */
    public static void assertEmpty(Object obj) throws AssertionError {
        assertNotNull(obj);

        if (!isEmpty(obj)) {
            throw new AssertionError(ERROR_OBJECT_NEED_TO_BE_EMPTY);
        }
    }

    /**
     * Check if the number are not the same type (floating vs integer)
     *
     * @param firstNumber
     * @param secondNumber
     */
    public static void assertNumbersNotSameType(Number firstNumber, Number secondNumber) {
        assertNotNull(firstNumber, secondNumber);

        boolean isValueFloatingPoint = firstNumber instanceof Double || firstNumber instanceof Float;
        boolean isLowestValueFloatingPoint = secondNumber instanceof Double || secondNumber instanceof Float;

        if (isValueFloatingPoint == isLowestValueFloatingPoint) {
            throw new AssertionError(ERROR_VALUES_MUST_BE_THE_SAME_TYPE);
        }
    }

    /**
     * A utility function that check if the number is inferior / superior than the other number
     *
     * @param value
     * @param highestNumber
     * @param isSuperior
     * @param canBeEquals
     * @throws AssertionError
     */
    private static void assertSuperiorInferiorNumber(Number value, Number highestNumber, boolean isSuperior, boolean canBeEquals) throws AssertionError {
        assertNotNull(value, highestNumber);
        assertNumbersSameType(value, highestNumber);

        if (canBeEquals) {
            if (isSuperior) {
                if (!isNumberSuperiorOrEqualsTo(value, highestNumber)) {
                    throw new AssertionError(String.format(ERROR_SUPERIOR_EQUALS, value, highestNumber));
                }
            } else {
                if (!isNumberInferiorOrEqualsTo(value, highestNumber)) {
                    throw new AssertionError(String.format(ERROR_INFERIOR_EQUALS, value, highestNumber));
                }
            }
        } else {
            if (isSuperior) {
                if (!isNumberSuperiorTo(value, highestNumber)) {
                    throw new AssertionError(String.format(ERROR_SUPERIOR, value, highestNumber));
                }
            } else {
                if (!isNumberInferiorTo(value, highestNumber)) {
                    throw new AssertionError(String.format(ERROR_INFERIOR, value, highestNumber));
                }
            }
        }
    }

    /**
     * A utility function that check if the number is between the other numbers
     *
     * @param value
     * @param lowestValue
     * @param highestNumber
     * @param canBeEquals
     */
    private static void assertBetweenNumber(Number value, Number lowestValue, Number highestNumber, boolean canBeEquals) {
        assertNotNull(value, lowestValue, highestNumber);
        assertNumbersSameType(value, lowestValue, highestNumber);

        if (canBeEquals) {
            if (!(isNumberInferiorOrEqualsTo(lowestValue, value) && isNumberSuperiorOrEqualsTo(highestNumber, value))) {
                throw new AssertionError(String.format(ERROR_VALUE_MUST_BE_BETWEEN_OR_EQUALS_TO, value, lowestValue, highestNumber));
            }
        } else {
            if (!(isNumberInferiorTo(lowestValue, value) && isNumberSuperiorTo(highestNumber, value))) {
                throw new AssertionError(String.format(ERROR_VALUE_MUST_BE_BETWEEN, value, lowestValue, highestNumber));
            }
        }
    }

    /**
     * A utility function that check if the number is inferior to the other number
     *
     * @param value
     * @param mark
     * @return
     */
    public static boolean isNumberInferiorTo(Number value, Number mark) {
        assertNotNull(value, mark);
        assertNumbersSameType(value, mark);

        boolean isNumberInferiorTo = true;

        if (value instanceof Double || value instanceof Float) {
            isNumberInferiorTo = value.doubleValue() < mark.doubleValue();
        } else {
            isNumberInferiorTo = value.longValue() < mark.longValue();
        }

        return isNumberInferiorTo;
    }


    /**
     * A utility function that check if the number is superior to the other number
     *
     * @param value
     * @param mark
     * @return
     */
    public static boolean isNumberSuperiorTo(Number value, Number mark) {
        assertNotNull(value, mark);
        assertNumbersSameType(value, mark);

        boolean isNumberInferiorTo = true;

        if (value instanceof Double || value instanceof Float) {
            isNumberInferiorTo = value.doubleValue() > mark.doubleValue();
        } else {
            isNumberInferiorTo = value.longValue() > mark.longValue();
        }

        return isNumberInferiorTo;
    }

    /**
     * A utility function that check if the number is inferior or equals to the other number
     *
     * @param value
     * @param mark
     * @return
     */
    public static boolean isNumberInferiorOrEqualsTo(Number value, Number mark) {
        assertNotNull(value, mark);
        assertNumbersSameType(value, mark);

        boolean isNumberInferiorTo = true;

        if (value instanceof Double || value instanceof Float) {
            isNumberInferiorTo = value.doubleValue() <= mark.doubleValue();
        } else {
            isNumberInferiorTo = value.longValue() <= mark.longValue();
        }

        return isNumberInferiorTo;
    }

    /**
     * A utility function that check if the number is superior or equals to the other number
     *
     * @param value
     * @param mark
     * @return
     */
    public static boolean isNumberSuperiorOrEqualsTo(Number value, Number mark) {
        assertNotNull(value, mark);
        assertNumbersSameType(value, mark);

        boolean isNumberInferiorTo = true;

        if (value instanceof Double || value instanceof Float) {
            isNumberInferiorTo = value.doubleValue() >= mark.doubleValue();
        } else {
            isNumberInferiorTo = value.longValue() >= mark.longValue();
        }

        return isNumberInferiorTo;
    }
}