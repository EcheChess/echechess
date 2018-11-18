/*
 *    Copyright 2014 - 2018 Yannick Watier
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

package ca.watier.echechess.models;

import java.io.Serializable;
import java.util.List;

public class AvailableMove implements Serializable {
    private static final long serialVersionUID = 8612686569088965026L;

    private final String from;
    private final List<String> positions;

    public AvailableMove(String from, List<String> positions) {
        this.from = from;
        this.positions = positions;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getPositions() {
        return positions;
    }
}
