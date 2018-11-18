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

package ca.watier.echechess.repositories;

import ca.watier.echechess.communication.redis.interfaces.GameRepository;
import ca.watier.echechess.communication.redis.model.GenericGameHandlerWrapper;
import ca.watier.echechess.engine.engines.GenericGameHandler;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandaloneGameRepositoryImpl implements GameRepository<GenericGameHandler> {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(StandaloneGameRepositoryImpl.class);
    private final Map<String, GenericGameHandlerWrapper<GenericGameHandler>> games = new HashMap<>();

    @Override
    public void add(GenericGameHandlerWrapper<GenericGameHandler> genericGameHandlerWrapper) {
        addGame(genericGameHandlerWrapper.getId(), genericGameHandlerWrapper);
    }

    private void addGame(String id, GenericGameHandlerWrapper<GenericGameHandler> genericGameHandlerWrapper) {
        LOGGER.info("Added new game with id {}", genericGameHandlerWrapper.getId());
        games.put(id, genericGameHandlerWrapper);
    }

    @Override
    public void add(String id, GenericGameHandler genericGameHandler) {
        addGame(id, new GenericGameHandlerWrapper<>(genericGameHandler));
    }

    @Override
    public void delete(String id) {
        games.remove(id);
    }

    @Override
    public GenericGameHandlerWrapper<GenericGameHandler> get(String id) {
        return games.get(id);
    }

    @Override
    public List<GenericGameHandlerWrapper<GenericGameHandler>> getAll() {
        return new ArrayList<>(games.values());
    }
}
