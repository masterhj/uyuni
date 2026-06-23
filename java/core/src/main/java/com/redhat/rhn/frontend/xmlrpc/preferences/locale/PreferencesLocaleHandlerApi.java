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
package com.redhat.rhn.frontend.xmlrpc.preferences.locale;

import com.redhat.rhn.domain.user.User;

import com.suse.manager.api.ApiResponseWrapper;
import com.suse.manager.api.docs.ApiEndpointDoc;

import io.swagger.models.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * API Contract for PreferencesLocaleHandler.
 */
@Tag(name = "preferences.locale", description = "Provides methods to access and modify user locale information")
public interface PreferencesLocaleHandlerApi {

    @ApiEndpointDoc(
        summary = "Set a user's timezone.",
        requestClass = SetTimeZoneRequest.class,
        isIntegerResponse = true
    )
    int setTimeZone(User loggedInUser, String login, Integer tzid);

    @ApiEndpointDoc(
        summary = "Set a user's locale.",
        requestClass = SetLocaleRequest.class,
        isIntegerResponse = true
    )
    int setLocale(User loggedInUser, String login, String locale);

    @ApiEndpointDoc(
        summary = "Returns a list of all understood timezones. Results can be used as input to setTimeZone.",
        method = HttpMethod.GET,
        responseClass = TimeZoneListResponse.class
    )
    Object[] listTimeZones();

    @ApiEndpointDoc(
        summary = "Returns a list of all understood locales. Can be used as input to setLocale.",
        method = HttpMethod.GET,
        responseClass = LocaleListResponse.class
    )
    Object[] listLocales();

    @Schema(name = "SetTimeZoneRequest")
    interface SetTimeZoneRequest {
        @Schema(description = "User's login name.", requiredMode = Schema.RequiredMode.REQUIRED)
        String getLogin();

        @Schema(description = "Timezone ID. (from listTimeZones)", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer getTzid();
    }

    @Schema(name = "SetLocaleRequest")
    interface SetLocaleRequest {
        @Schema(description = "User's login name.", requiredMode = Schema.RequiredMode.REQUIRED)
        String getLogin();

        @Schema(description = "Locale to set. (from listLocales)", requiredMode = Schema.RequiredMode.REQUIRED)
        String getLocale();
    }

    @Schema(name = "RhnTimeZone", description = "timezone")
    interface RhnTimeZoneDoc {
        @Schema(name = "time_zone_id", description = "unique identifier for timezone",
                requiredMode = Schema.RequiredMode.REQUIRED)
        int getTimeZoneId();

        @Schema(name = "olson_name", description = "name as identified by the Olson database",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String getOlsonName();
    }

    @Schema(name = "ApiResponseTimeZoneList")
    interface TimeZoneListResponse extends ApiResponseWrapper<List<RhnTimeZoneDoc>> {}

    @Schema(name = "ApiResponseLocaleList")
    interface LocaleListResponse extends ApiResponseWrapper<List<String>> {}
}
