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

package ca.watier.utils;

import org.springframework.data.redis.listener.ChannelTopic;

public class TestTopic extends ChannelTopic {
    private final String name;

    /**
     * Constructs a new {@link ChannelTopic} instance.
     *
     * @param name must not be {@literal null}.
     */
    public TestTopic(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public String getTopic() {
        return name;
    }
}
