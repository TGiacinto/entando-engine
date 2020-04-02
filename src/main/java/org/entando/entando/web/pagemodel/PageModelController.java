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

package org.entando.entando.web.pagemodel;

import com.agiletec.aps.system.services.role.Permission;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Map;
import javax.validation.Valid;
import org.entando.entando.aps.system.services.pagemodel.IPageModelService;
import org.entando.entando.aps.system.services.pagemodel.model.PageModelDto;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.PagedRestResponse;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.pagemodel.model.PageModelRequest;
import org.entando.entando.web.pagemodel.validator.PageModelValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"page-models"})
@RestController
@RequestMapping(value = "/pageModels", produces = MediaType.APPLICATION_JSON_VALUE)
public class PageModelController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final IPageModelService pageModelService;
    private final PageModelValidator pageModelValidator;

    @Autowired
    public PageModelController(IPageModelService pageModelService, PageModelValidator pageModelValidator) {
        this.pageModelService = pageModelService;
        this.pageModelValidator = pageModelValidator;
    }

    @ApiOperation("Retrieve multiple page models")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RestAccessControl(permission = Permission.SUPERUSER)
    @GetMapping
    public ResponseEntity<PagedRestResponse<PageModelDto>> getPageModels(
            RestListRequest requestList, @RequestParam Map<String, String> requestParams) {
        logger.trace("loading page models");

        pageModelValidator.validateRestListRequest(requestList, PageModelDto.class);
        PagedMetadata<PageModelDto> result = pageModelService.getPageModels(requestList, requestParams);

        pageModelValidator.validateRestListResult(requestList, result);

        return ResponseEntity.ok(new PagedRestResponse<>(result));
    }

    @ApiOperation("Retrieve page model by code")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "NotFound")
    })
    @RestAccessControl(permission = Permission.SUPERUSER)
    @GetMapping(value = "/{code}")
    public ResponseEntity<SimpleRestResponse<PageModelDto>> getPageModel(@PathVariable String code) {
        PageModelDto pageModelDto = pageModelService.getPageModel(code);
        return ResponseEntity.ok(new SimpleRestResponse<>(pageModelDto));
    }

    @ApiOperation("Retrieve page model references")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK")
    })
    @RestAccessControl(permission = Permission.SUPERUSER)
    @GetMapping(value = "/{code}/references/{manager}")
    public ResponseEntity<PagedRestResponse<?>> getPageModelReferences(
            @PathVariable String code, @PathVariable String manager, RestListRequest requestList) {

        PagedMetadata<?> result = pageModelService.getPageModelReferences(code, manager, requestList);
        return ResponseEntity.ok(new PagedRestResponse<>(result));
    }

    @ApiOperation("Update page model")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @RestAccessControl(permission = Permission.SUPERUSER)
    @PutMapping(value = "/{code}", name = "roleGroup")
    public ResponseEntity<SimpleRestResponse<PageModelDto>> updatePageModel(@PathVariable String code,
            @Valid @RequestBody PageModelRequest pageModelRequest, BindingResult bindingResult) {

        validateWithBodyName(code, pageModelRequest, bindingResult);

        PageModelDto pageModel = pageModelService.updatePageModel(pageModelRequest);
        return ResponseEntity.ok(new SimpleRestResponse<>(pageModel));
    }

    private void validateWithBodyName(String code, PageModelRequest pageModelRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        pageModelValidator.validateBodyName(code, pageModelRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
    }

    @ApiOperation("Add page model")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 409, message = "Conflict")
    })
    @RestAccessControl(permission = Permission.SUPERUSER)
    @PostMapping
    public ResponseEntity<SimpleRestResponse<PageModelDto>> addPageModel(
            @Valid @RequestBody PageModelRequest pagemodelRequest, BindingResult bindingResult) {

        validatePageModelRequest(pagemodelRequest, bindingResult);

        PageModelDto dto = pageModelService.addPageModel(pagemodelRequest);
        return ResponseEntity.ok(new SimpleRestResponse<>(dto));
    }

    private void validatePageModelRequest(@RequestBody @Valid PageModelRequest pagemodelRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        pageModelValidator.validate(pagemodelRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
    }

    @ApiOperation("Delete page model")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK")
    })
    @RestAccessControl(permission = Permission.SUPERUSER)
    @DeleteMapping(value = "/{code}")
    public ResponseEntity<SimpleRestResponse<Map>> deletePageModel(@PathVariable String code) {
        logger.debug("deleting {}", code);
        pageModelService.removePageModel(code);
        Map<String, String> result = ImmutableMap.of("code", code);
        return ResponseEntity.ok(new SimpleRestResponse<>(result));
    }
}
