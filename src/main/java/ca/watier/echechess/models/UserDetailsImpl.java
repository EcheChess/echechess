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

import ca.watier.echechess.common.sessions.Player;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Yannick on 25/9/2015.
 */
public class UserDetailsImpl extends Player implements UserDetails {

    private static final long serialVersionUID = -124649164513355612L;
    private int userId;
    private String usr;
    private String pwd;
    private String email;

    private boolean isExpired;
    private boolean isLocked;
    private boolean isPwdExpired;
    private boolean isEnabled;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(int userId, String usr, String pwd, String email, boolean isExpired, boolean isLocked, boolean isPwdExpired, boolean isEnabled, Collection<? extends GrantedAuthority> authorities) {
        super(UUID.randomUUID().toString());
        this.userId = userId;
        this.usr = usr;
        this.pwd = pwd;
        this.email = email;
        this.isExpired = isExpired;
        this.isLocked = isLocked;
        this.isPwdExpired = isPwdExpired;
        this.isEnabled = isEnabled;
        this.authorities = authorities;
    }


    public boolean isInGame(UUID game) {
        return getCreatedGameList().contains(game);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return pwd;
    }

    public String getUsername() {
        return usr;
    }

    public boolean isAccountNonExpired() {
        return isExpired;
    }

    public boolean isAccountNonLocked() {
        return isLocked;
    }

    public boolean isCredentialsNonExpired() {
        return isPwdExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return userId == that.userId &&
                isExpired == that.isExpired &&
                isLocked == that.isLocked &&
                isPwdExpired == that.isPwdExpired &&
                isEnabled == that.isEnabled &&
                Objects.equals(usr, that.usr) &&
                Objects.equals(pwd, that.pwd) &&
                Objects.equals(email, that.email) &&
                Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, usr, pwd, email, isExpired, isLocked, isPwdExpired, isEnabled, authorities);
    }
}
