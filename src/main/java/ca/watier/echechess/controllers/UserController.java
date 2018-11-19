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

package ca.watier.echechess.controllers;

import ca.watier.echechess.exceptions.UserException;
import ca.watier.echechess.models.User;
import ca.watier.echechess.models.UserDetailsImpl;
import ca.watier.echechess.services.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private static final String USER_DESC = "The user information (Username, email or password";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "The user already exist"),
            @ApiResponse(code = 200, message = "The user is created")
    })
    @ApiOperation("Create a new user")
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void addUser(@ApiParam(value = USER_DESC, required = true) @RequestBody @Valid @NotNull User user) throws UserException {
        userService.addNewUser(user);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 501, message = "This feature is not implemented yet"),
            @ApiResponse(code = 409, message = "The user already exist"),
            @ApiResponse(code = 200, message = "The user is updated")
    })
    @ApiOperation("Update the user settings")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@ApiParam(value = USER_DESC, required = true) @RequestBody @Valid @NotNull User user) throws UserException {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateUser(user, principal);
    }
}
