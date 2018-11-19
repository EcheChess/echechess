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

import ca.watier.echechess.exceptions.UserAlreadyExistException;
import ca.watier.echechess.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    private static final ResponseEntity<Object> NOT_FOUND = ResponseEntity.notFound().build();
    private static final ResponseEntity<Object> CONFLICT = ResponseEntity.status(HttpStatus.CONFLICT).build();

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity handleUserAlreadyExistException() {
        return CONFLICT;
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity handleUserNotFoundException() {
        return NOT_FOUND;
    }
}
