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
package com.redhat.rhn.frontend.xmlrpc.packages.provider;

import com.redhat.rhn.domain.user.User;

import com.suse.manager.api.ApiResponseWrapper;
import com.suse.manager.api.docs.ApiEndpointDoc;

import java.util.List;
import java.util.Set;

import io.swagger.models.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Contract for PackagesProviderHandler.
 */
@Tag(name = "packages.provider", description = "Methods to retrieve information about Package Providers associated with packages.")
public interface PackagesProviderHandlerApi {

    @ApiEndpointDoc(
        summary = "List all Package Providers. User executing the request must be a Uyuni administrator.",
        responseClass = PackageProviderListResponse.class
    )
    List<com.redhat.rhn.domain.rhnpackage.PackageProvider> list(User loggedInUser);

    @ApiEndpointDoc(
        summary = "List all security keys associated with a package provider. " +
                  "User executing the request must be a Uyuni administrator.",
        method = HttpMethod.GET,
        responseClass = PackageKeyListResponse.class
    )
    Set<com.redhat.rhn.domain.rhnpackage.PackageKey> listKeys(User loggedInUser, String providerName);

    @ApiEndpointDoc(
        summary = "Associate a package security key and with the package provider. " +
                  "If the provider or key doesn't exist, it is created. " +
                  "User executing the request must be a Uyuni administrator.",
        requestClass = AssociateKeyRequest.class,
        isIntegerResponse = true
    )
    int associateKey(User loggedInUser, String providerName, String key, String type);

    @Schema(name = "AssociateKeyRequest")
    interface AssociateKeyRequest {
        @Schema(description = "The provider name", requiredMode = Schema.RequiredMode.REQUIRED)
        String getProviderName();

        @Schema(description = "The actual key", requiredMode = Schema.RequiredMode.REQUIRED)
        String getKey();

        @Schema(description = "The type of the key. Currently, only 'gpg' is supported",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String getType();
    }

    @Schema(name = "PackageProvider", description = "package provider")
    interface PackageProviderDoc {
        @Schema(description = "provider name", requiredMode = Schema.RequiredMode.REQUIRED)
        String getName();

        @Schema(description = "associated package keys", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<PackageKeyDoc> getKeys();
    }

    @Schema(name = "PackageKey", description = "package security key")
    interface PackageKeyDoc {
        @Schema(description = "the key string", requiredMode = Schema.RequiredMode.REQUIRED)
        String getKey();

        @Schema(description = "the type of the key", requiredMode = Schema.RequiredMode.REQUIRED)
        String getType();
    }

    @Schema(name = "ApiResponsePackageProviderList")
    interface PackageProviderListResponse extends ApiResponseWrapper<List<PackageProviderDoc>> {}

    @Schema(name = "ApiResponsePackageKeyList")
    interface PackageKeyListResponse extends ApiResponseWrapper<List<PackageKeyDoc>> {}
}
