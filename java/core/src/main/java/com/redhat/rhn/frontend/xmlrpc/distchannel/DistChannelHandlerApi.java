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
package com.redhat.rhn.frontend.xmlrpc.distchannel;

import com.redhat.rhn.domain.user.User;

import com.suse.manager.api.ApiResponseWrapper;
import com.suse.manager.api.docs.ApiEndpointDoc;

import io.swagger.models.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * API Contract for DistChannelHandler.
 */
@Tag(name = "distchannel", description = "Provides methods to access and modify distribution channel information")
public interface DistChannelHandlerApi {

    @ApiEndpointDoc(
        summary = "Lists the default distribution channel maps",
        method = HttpMethod.GET,
        responseClass = DistChannelMapListResponse.class
    )
    Object[] listDefaultMaps(User loggedInUser);

    @ApiEndpointDoc(
        summary = "Lists distribution channel maps valid for the user's organization",
        method = HttpMethod.GET,
        responseClass = DistChannelMapListResponse.class
    )
    Object[] listMapsForOrg(User loggedInUser);

    @ApiEndpointDoc(
        summary = "Lists distribution channel maps valid for an organization, Uyuni admin rights needed.",
        method = HttpMethod.GET,
        responseClass = DistChannelMapListResponse.class
    )
    Object[] listMapsForOrg(User loggedInUser, Integer orgId);

    @ApiEndpointDoc(
        summary = "Sets, overrides (/removes if channelLabel empty) a distribution channel map within an organization",
        requestClass = SetMapForOrgRequest.class,
        isIntegerResponse = true
    )
    int setMapForOrg(User loggedInUser, String os, String release, String archName, String channelLabel);

    @Schema(name = "SetMapForOrgRequest")
    interface SetMapForOrgRequest {
        @Schema(description = "OS", requiredMode = Schema.RequiredMode.REQUIRED)
        String getOs();

        @Schema(description = "Release", requiredMode = Schema.RequiredMode.REQUIRED)
        String getRelease();

        @Schema(description = "architecture label", requiredMode = Schema.RequiredMode.REQUIRED)
        String getArchName();

        @Schema(description = "channel label", requiredMode = Schema.RequiredMode.REQUIRED)
        String getChannelLabel();
    }

    @Schema(name = "DistChannelMap", description = "distribution channel map")
    interface DistChannelMapDoc {
        @Schema(description = "operating system", requiredMode = Schema.RequiredMode.REQUIRED)
        String getOs();

        @Schema(description = "OS Release", requiredMode = Schema.RequiredMode.REQUIRED)
        String getRelease();

        @Schema(name = "arch_name", description = "channel architecture", requiredMode = Schema.RequiredMode.REQUIRED)
        String getArchName();

        @Schema(name = "channel_label", description = "channel label", requiredMode = Schema.RequiredMode.REQUIRED)
        String getChannelLabel();

        @Schema(name = "org_specific", description = "'Y' organization specific, 'N' default",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String getOrgSpecific();
    }

    @Schema(name = "ApiResponseDistChannelMapList")
    interface DistChannelMapListResponse extends ApiResponseWrapper<List<DistChannelMapDoc>> {}
}
