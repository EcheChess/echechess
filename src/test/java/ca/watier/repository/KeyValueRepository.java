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

package ca.watier.repository;

import ca.watier.echechess.engine.engines.GenericGameHandler;
import ca.watier.echechess.redis.interfaces.GameRepository;
import ca.watier.echechess.redis.model.GenericGameHandlerWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyValueRepository implements GameRepository<GenericGameHandler> {

    private Map<String, GenericGameHandlerWrapper<GenericGameHandler>> repo = new HashMap<>();

    @Override
    public void add(GenericGameHandlerWrapper<GenericGameHandler> genericGameHandlerWrapper) {
        if (genericGameHandlerWrapper == null) {
            throw new IllegalArgumentException();
        }

        repo.put(genericGameHandlerWrapper.getId(), genericGameHandlerWrapper);
    }

    @Override
    public void add(String key, GenericGameHandler genericGameHandler) {
        if (key == null || key.isEmpty() || genericGameHandler == null) {
            throw new IllegalArgumentException();
        }

        add(new GenericGameHandlerWrapper<>(key, genericGameHandler));
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException();
        }

        repo.remove(key);
    }

    @Override
    public GenericGameHandlerWrapper<GenericGameHandler> get(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return repo.get(key);
    }

    @Override
    public List<GenericGameHandlerWrapper<GenericGameHandler>> getAll() {
        return List.copyOf(repo.values());
    }
}
