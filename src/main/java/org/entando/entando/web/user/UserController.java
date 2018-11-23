/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.web.user;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.services.user.IUserService;
import org.entando.entando.aps.system.services.user.model.*;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.*;
import org.entando.entando.web.user.model.*;
import org.entando.entando.web.user.validator.UserValidator;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static org.entando.entando.web.user.validator.UserValidator.createDeleteAdminError;
import static org.entando.entando.web.user.validator.UserValidator.isValidDeleteUser;

/**
 *
 * @author paddeo
 */
@Validated
@RestController
@RequestMapping(value = "/users")
@SessionAttributes("user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private UserValidator userValidator;

    public IUserService getUserService() {
        return userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public UserValidator getUserValidator() {
        return userValidator;
    }

    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedRestResponse<UserDto>> getUsers(RestListRequest requestList, @RequestParam(value = "withProfile", required = false) String withProfile) {
        logger.debug("getting users details with request {}", requestList);
        this.getUserValidator().validateRestListRequest(requestList, UserDto.class);
        PagedMetadata<UserDto> result = this.getUserService().getUsers(requestList, withProfile);
        if (withProfile != null) {
            result.addAdditionalParams("withProfile", withProfile);
        }
        return new ResponseEntity<>(new PagedRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> getUser(@PathVariable String username) {
        logger.debug("getting user {} details", username);
        UserDto user = this.getUserService().getUser(username);
        return new ResponseEntity<>(new SimpleRestResponse<>(user), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> addUser(@Valid @RequestBody UserRequest userRequest, BindingResult bindingResult) throws ApsSystemException {
        logger.debug("adding user with request {}", userRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validateUserPost(userRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        UserDto dto = this.getUserService().addUser(userRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(dto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> updateUser(@ModelAttribute("user") UserDetails user, @PathVariable String target, @Valid @RequestBody UserRequest userRequest, BindingResult bindingResult) {
        logger.debug("updating user {} with request {}", target, userRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validatePutBody(target, userRequest, bindingResult);
        this.getUserValidator().validateUpdateSelf(target, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        UserDto userDto = this.getUserService().updateUser(userRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(userDto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{username:.+}/password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<UserDto>> updateUserPassword(@PathVariable String username, @Valid @RequestBody UserPasswordRequest passwordRequest, BindingResult bindingResult) {
        logger.debug("changing pasword for user {} with request {}", username, passwordRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validateChangePasswords(username, passwordRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        UserDto userDto = this.getUserService().updateUserPassword(passwordRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(userDto), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<Map>> deleteUser(@PathVariable String username) throws ApsSystemException {
        logger.debug("deleting {}", username);

        if (!isValidDeleteUser(username)) {
            throw new ValidationGenericException(createDeleteAdminError());
        }

        this.getUserService().removeUser(username);
        Map<String, String> result = new HashMap<>();
        result.put("code", username);
        return new ResponseEntity<>(new SimpleRestResponse<>(result), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}/authorities", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<List<UserAuthorityDto>>> updateUserAuthorities(@ModelAttribute("user") UserDetails user, @PathVariable String target, @Valid @RequestBody UserAuthoritiesRequest authRequest, BindingResult bindingResult) {
        logger.debug("user {} requesting update authorities for username {} with req {}", user.getUsername(), target, authRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        this.getUserValidator().validate(authRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validateUpdateSelf(target, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        List<UserAuthorityDto> authorities = this.getUserService().updateUserAuthorities(target, authRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(authorities), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}/authorities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<List<UserAuthorityDto>>> getUserAuthorities(@ModelAttribute("user") UserDetails user, @PathVariable String target) {
        logger.debug("requesting authorities for username {}", target);
        List<UserAuthorityDto> authorities = this.getUserService().getUserAuthorities(target);
        return new ResponseEntity<>(new SimpleRestResponse<>(authorities), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}/authorities", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<List<UserAuthorityDto>>> addUserAuthorities(@ModelAttribute("user") UserDetails user, @PathVariable String target, @Valid @RequestBody UserAuthoritiesRequest authRequest, BindingResult bindingResult) throws ApsSystemException {
        logger.debug("user {} requesting add authorities for username {} with req {}", user.getUsername(), target, authRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        getUserValidator().validate(authRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserValidator().validateUpdateSelf(target, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        List<UserAuthorityDto> authorities = this.getUserService().addUserAuthorities(target, authRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(authorities), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.MANAGE_USERS)
    @RequestMapping(value = "/{target:.+}/authorities", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse> deleteUserAuthorities(@ModelAttribute("user") UserDetails user, @PathVariable String target) throws ApsSystemException {
        logger.debug("user {} requesting delete authorities for username {}", user.getUsername(), target);
        DataBinder binder = new DataBinder(target);
        BindingResult bindingResult = binder.getBindingResult();
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        //business validations
        getUserValidator().validateUpdateSelf(target, user.getUsername(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getUserService().deleteUserAuthorities(target);
        return new ResponseEntity<>(new SimpleRestResponse<>(new ArrayList<>()), HttpStatus.OK);
    }

}
