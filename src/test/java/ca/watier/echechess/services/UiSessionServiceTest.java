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

import ca.watier.echechess.common.sessions.Player;
import ca.watier.echechess.common.impl.WebSocketServiceTestImpl;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by yannick on 6/11/2017.
 */
public class UiSessionServiceTest {

    private UiSessionService service;

    @Before
    public void setup() {
        service = new UiSessionService(new WebSocketServiceTestImpl(), CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, Player.class, ResourcePoolsBuilder.heap(100))
                .withExpiry(Expirations.timeToIdleExpiration(new Duration(5, TimeUnit.SECONDS))));
    }

    @Test
    public void createNewSessionTest() throws Exception {
        Player player = new Player();

        UUID newSessionUuid = UUID.fromString(service.createNewSession(player));
        assertThat(newSessionUuid).isNotNull();
        assertThat(service.getItemFromCache(newSessionUuid)).isNotNull();

        Thread.sleep(5 * 1000); //Wait for the cache to delete the item

        assertThat(service.getItemFromCache(newSessionUuid)).isNull(); //Supposed to be deleted
    }

    @Test
    public void refreshTest() throws Exception {
        Player player = new Player();

        UUID newSessionUuid = UUID.fromString(service.createNewSession(player));
        assertThat(newSessionUuid).isNotNull();
        assertThat(service.getItemFromCache(newSessionUuid)).isNotNull();

        Thread.sleep(3 * 1000);
        service.refresh(newSessionUuid.toString());
        Thread.sleep(3 * 1000);

        assertThat(service.getItemFromCache(newSessionUuid)).isNotNull(); // Not supposed to be deleted

        Thread.sleep(5 * 1000);

        assertThat(service.getItemFromCache(newSessionUuid)).isNull(); // Supposed to be deleted

    }
}