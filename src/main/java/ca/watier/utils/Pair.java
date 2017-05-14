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

/**
 * Created by yannick on 5/10/2017.
 */
public class Pair<X, Y> {

    private X firstValue;
    private Y secondValue;

    public Pair(X firstValue, Y secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    public Pair() {
    }

    public X getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(X firstValue) {
        this.firstValue = firstValue;
    }

    public Y getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(Y secondValue) {
        this.secondValue = secondValue;
    }
}
