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

package ca.watier.echechess.components;

import ca.watier.echechess.models.UserDetailsImpl;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.UUID;

public class EcheChessSecurityExpressionRootImpl extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    public EcheChessSecurityExpressionRootImpl(Authentication authentication) {
        super(authentication);
    }

    public boolean isPlayerInGame(UUID uuid) {
        if (Objects.isNull(uuid)) {
            return false;
        }

        UserDetailsImpl user = (UserDetailsImpl) this.getPrincipal();
        return user.isInGame(uuid);
    }

    @Override
    public Object getFilterObject() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFilterObject(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getReturnObject() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReturnObject(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getThis() {
        throw new UnsupportedOperationException();
    }
}
