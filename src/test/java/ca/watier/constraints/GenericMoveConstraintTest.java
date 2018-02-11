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

package ca.watier.constraints;

import ca.watier.echechessengine.constraints.GenericMoveConstraint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yannick on 6/20/2017.
 */
public class GenericMoveConstraintTest {


    private GenericMoveConstraint genericMoveConstraint;

    @Before
    public void setUp() {
        genericMoveConstraint = new GenericMoveConstraint(null);
    }

    /**
     * Only to assert that the pattern cannot be null (return false if so)
     *
     * @throws Exception
     */
    @Test
    public void isMoveValid() {
        Assert.assertFalse(genericMoveConstraint.isMoveValid(null, null, null, null));
    }
}