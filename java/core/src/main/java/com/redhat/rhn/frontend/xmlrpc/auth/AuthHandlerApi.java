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
package com.redhat.rhn.frontend.xmlrpc.auth;

import com.suse.manager.api.ApiResponseWrapper;
import com.suse.manager.api.docs.ApiEndpointDoc;
import com.suse.manager.api.docs.PublicApiEndpoint;

import javax.security.auth.login.LoginException;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Contract for AuthHandler.
 */
@Tag(name = "auth", description = "This namespace provides methods to authenticate with the system's management server.")
public interface AuthHandlerApi {

    @PublicApiEndpoint
    @ApiEndpointDoc(
        summary = "Login using a username and password. Returns the session key used by most other API methods.",
        requestClass = LoginRequest.class,
        responseClass = StringResponse.class
    )
    String login(
        @Parameter(
            name = "username",
            description = "Username",
            in = ParameterIn.DEFAULT,
            required = true
        ) String username,
        @Parameter(
            name = "password",
            description = "Password",
            in = ParameterIn.DEFAULT,
            required = true
        ) String password
    ) throws LoginException;

    @PublicApiEndpoint
    @ApiEndpointDoc(
        summary = "Login using a username and password. Returns the session key used by other methods.",
        requestClass = LoginWithDurationRequest.class,
        responseClass = StringResponse.class
    )
    String login(
        @Parameter(
            name = "username",
            description = "Username",
            in = ParameterIn.DEFAULT,
            required = true
        ) String username,
        @Parameter(
            name = "password",
            description = "Password",
            in = ParameterIn.DEFAULT,
            required = true
        ) String password,
        @Parameter(
            name = "duration",
            description = "Length of session.",
            in = ParameterIn.DEFAULT,
            required = true
        ) Integer duration
    );

    @ApiEndpointDoc(
        summary = "Logout the user with the given session key.",
        isIntegerResponse = true
    )
    int logout(
        @Parameter(hidden = true) String sessionKey
    );

    @Schema(name = "LoginRequest")
    interface LoginRequest {
        @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED)
        String getUsername();

        @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED)
        String getPassword();
    }

    @Schema(name = "LoginWithDurationRequest")
    interface LoginWithDurationRequest {
        @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED)
        String getUsername();

        @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED)
        String getPassword();

        @Schema(description = "Length of session.", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer getDuration();
    }

    @Schema(name = "ApiResponseString")
    interface StringResponse extends ApiResponseWrapper<String> {}
}