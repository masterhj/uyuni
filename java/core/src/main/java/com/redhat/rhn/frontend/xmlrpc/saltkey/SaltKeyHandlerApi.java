/*
 * Copyright (c) 2026 SUSE LLC
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 */
package com.redhat.rhn.frontend.xmlrpc.saltkey;

import com.redhat.rhn.domain.user.User;

import com.suse.manager.api.ApiResponseWrapper;
import com.suse.manager.api.docs.ApiEndpointDoc;

import java.util.List;

import io.swagger.models.HttpMethod;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Contract for SaltKeyHandler.
 */
@Tag(name = "saltkey", description = "Provides methods to manage salt keys")
public interface SaltKeyHandlerApi {

    @ApiEndpointDoc(
        summary = "List accepted salt keys",
        method = HttpMethod.GET,
        responseClass = SaltKeyListResponse.class
    )
    List<String> acceptedList(User loggedInUser);

    @ApiEndpointDoc(
        summary = "List pending salt keys",
        method = HttpMethod.GET,
        responseClass = SaltKeyListResponse.class
    )
    List<String> pendingList(User loggedInUser);

    @ApiEndpointDoc(
        summary = "List of rejected salt keys",
        method = HttpMethod.GET,
        responseClass = SaltKeyListResponse.class
    )
    List<String> rejectedList(User loggedInUser);

    @ApiEndpointDoc(
        summary = "List of denied salt keys",
        method = HttpMethod.GET,
        responseClass = SaltKeyListResponse.class
    )
    List<String> deniedList(User loggedInUser);

    @ApiEndpointDoc(
        summary = "Accept a minion key",
        requestClass = SaltKeyActionRequest.class,
        isIntegerResponse = true
    )
    int accept(User loggedInUser, String minionId);

    @ApiEndpointDoc(
        summary = "Reject a minion key",
        requestClass = SaltKeyActionRequest.class,
        isIntegerResponse = true
    )
    int reject(User loggedInUser, String minionId);

    @ApiEndpointDoc(
        summary = "Delete a minion key",
        requestClass = SaltKeyActionRequest.class,
        isIntegerResponse = true
    )
    int delete(User loggedInUser, String minionId);

    @Schema(name = "SaltKeyActionRequest")
    interface SaltKeyActionRequest {
        @Schema(description = "the key identifier (minionId)", requiredMode = Schema.RequiredMode.REQUIRED)
        String getMinionId();
    }

    @Schema(name = "ApiResponseSaltKeyList")
    interface SaltKeyListResponse extends ApiResponseWrapper<List<String>> {}
}
