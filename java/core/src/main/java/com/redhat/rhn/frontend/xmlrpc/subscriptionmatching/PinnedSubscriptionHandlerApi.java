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
package com.redhat.rhn.frontend.xmlrpc.subscriptionmatching;

import com.redhat.rhn.domain.server.PinnedSubscription;
import com.redhat.rhn.domain.user.User;

import com.suse.manager.api.ApiResponseWrapper;
import com.suse.manager.api.docs.ApiEndpointDoc;

import java.util.List;

import io.swagger.models.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Contract for PinnedSubscriptionHandler.
 */
@Tag(name = "subscriptionmatching.pinnedsubscription",
     description = "Provides the namespace for operations on Pinned Subscriptions")
public interface PinnedSubscriptionHandlerApi {

    @ApiEndpointDoc(
        summary = "Lists all PinnedSubscriptions",
        responseClass = PinnedSubscriptionListResponse.class
    )
    List<PinnedSubscription> list(User loggedInUser);

    @ApiEndpointDoc(
        summary = "Creates a Pinned Subscription based on given subscription and system",
        requestClass = CreatePinnedSubscriptionRequest.class,
        responseClass = PinnedSubscriptionResponse.class
    )
    PinnedSubscription create(User loggedInUser, Integer subscriptionId, Integer sid);

    @ApiEndpointDoc(
        summary = "Deletes Pinned Subscription with given id",
        requestClass = DeletePinnedSubscriptionRequest.class,
        isIntegerResponse = true
    )
    int delete(User loggedInUser, Integer subscriptionId);

    @Schema(name = "CreatePinnedSubscriptionRequest")
    interface CreatePinnedSubscriptionRequest {
        @Schema(description = "Subscription ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer getSubscriptionId();

        @Schema(description = "System ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer getSid();
    }

    @Schema(name = "DeletePinnedSubscriptionRequest")
    interface DeletePinnedSubscriptionRequest {
        @Schema(description = "Pinned Subscription ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer getSubscriptionId();
    }

    @Schema(name = "PinnedSubscription", description = "pinned subscription")
    interface PinnedSubscriptionDoc {
        @Schema(description = "pinned subscription id", requiredMode = Schema.RequiredMode.REQUIRED)
        Long getId();

        @Schema(name = "subscription_id", description = "subscription id",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long getSubscriptionId();

        @Schema(name = "system_id", description = "system id",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long getSystemId();
    }

    @Schema(name = "ApiResponsePinnedSubscription")
    interface PinnedSubscriptionResponse extends ApiResponseWrapper<PinnedSubscriptionDoc> {}

    @Schema(name = "ApiResponsePinnedSubscriptionList")
    interface PinnedSubscriptionListResponse extends ApiResponseWrapper<List<PinnedSubscriptionDoc>> {}
}