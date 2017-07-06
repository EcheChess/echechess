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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by yannick on 4/23/2017.
 */
public class BooleanResponse {
    @JsonIgnore
    public final static BooleanResponse NO = new BooleanResponse(false);
    @JsonIgnore
    public final static BooleanResponse YES = new BooleanResponse(true);

    private boolean response;
    private String message;

    public BooleanResponse(boolean response, String message) {
        this.response = response;
        this.message = message;
    }

    public BooleanResponse(boolean response) {
        this.response = response;
    }

    public static BooleanResponse getResponse(boolean is) {
        return is ? YES : NO;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        int result = (response ? 1 : 0);
        result = 31 * result + (message != null ? message.hashCode() : "".hashCode());
        return result;
    }

    /**
     * Compare only the boolean value, the message is irrelevant
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanResponse that = (BooleanResponse) o;

        return response == that.response;
    }
}
