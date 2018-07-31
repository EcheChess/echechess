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

package ca.watier.echechess.services;

import ca.watier.echechess.redis.interfaces.GameRepository;
import ca.watier.echechess.redis.model.GenericGameHandlerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class GameHandlerService implements MessageListener {
    private final GameRepository gameRepository;

    @Autowired(required = false)
    public GameHandlerService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        String uuid = new String(message.getBody()).substring(7);
        fetchNewGames();
    }

    public void fetchNewGames() {
        List<GenericGameHandlerWrapper> games = gameRepository.getAll();
        System.out.println(games);
    }
}
