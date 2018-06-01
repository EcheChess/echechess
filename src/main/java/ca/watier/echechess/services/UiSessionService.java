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

package ca.watier.echechess.services;

import ca.watier.echechess.common.enums.ChessEventMessage;
import ca.watier.echechess.common.interfaces.WebSocketService;
import ca.watier.echechess.common.sessions.Player;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static ca.watier.echechess.common.utils.CacheConstants.CACHE_UI_SESSION_NAME;
import static ca.watier.echechess.common.utils.Constants.REQUESTED_SESSION_ALREADY_DEFINED;
import static ca.watier.echechess.common.utils.Constants.THE_CLIENT_LOST_THE_CONNECTION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yannick on 6/11/2017.
 */

@Service
public class UiSessionService {

    private final Cache<UUID, Player> CACHE_UI;
    private final WebSocketService webSocketService;

    @Autowired
    public UiSessionService(WebSocketService webSocketService, CacheConfigurationBuilder<UUID, Player> uuidPlayerCacheConfiguration) {
        CacheManager CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_UI_SESSION_NAME, uuidPlayerCacheConfiguration)
                .build();

        CACHE_MANAGER.init();
        CACHE_UI = CACHE_MANAGER.getCache(CACHE_UI_SESSION_NAME, UUID.class, Player.class);
        this.webSocketService = webSocketService;
    }

    public String createNewSession(Player player) {
        String uuidAsString = null;
        UUID uuid = UUID.randomUUID();

        if (!isUiSessionActive(uuid)) {
            uuidAsString = uuid.toString();
            player.addUiSession(uuid);
            CACHE_UI.put(uuid, player);
        } else {
            webSocketService.fireUiEvent(uuid.toString(), ChessEventMessage.UI_SESSION_ALREADY_INITIALIZED, REQUESTED_SESSION_ALREADY_DEFINED);
        }

        return uuidAsString;
    }

    public boolean isUiSessionActive(UUID uuid) {
        return CACHE_UI.containsKey(uuid);
    }

    public void refresh(String uuid) {
        assertThat(uuid).isNotEmpty();

        Player player = CACHE_UI.get(UUID.fromString(uuid));

        if (player == null) {
            webSocketService.fireUiEvent(uuid, ChessEventMessage.UI_SESSION_EXPIRED, THE_CLIENT_LOST_THE_CONNECTION);
        }
    }

    public Player getItemFromCache(UUID uuid) {
        return CACHE_UI.get(uuid);
    }
}
