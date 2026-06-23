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
package com.redhat.rhn.frontend.xmlrpc.admin.monitoring;

import com.redhat.rhn.domain.user.User;

import com.suse.manager.api.ApiResponseWrapper;
import com.suse.manager.api.docs.ApiEndpointDoc;

import java.util.Map;

import io.swagger.models.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Contract for AdminMonitoringHandler.
 */
@Tag(name = "admin.monitoring", description = "Provides methods to manage the monitoring of the Uyuni server.")
public interface AdminMonitoringHandlerApi {

    @ApiEndpointDoc(
        summary = "Enable monitoring.",
        responseClass = ExportersResponse.class
    )
    Map<String, String> enable(User loggedInUser);

    @ApiEndpointDoc(
        summary = "Disable monitoring.",
        responseClass = ExportersResponse.class
    )
    Map<String, String> disable(User loggedInUser);

    @ApiEndpointDoc(
        summary = "Get the status of each Prometheus exporter.",
        method = HttpMethod.GET,
        responseClass = ExportersResponse.class
    )
    Map<String, String> getStatus(User loggedInUser);

    @Schema(name = "ApiResponseExporters")
    interface ExportersResponse extends ApiResponseWrapper<Map<String, String>> {}
}