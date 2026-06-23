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
package com.suse.manager.api.test.contract;

import com.redhat.rhn.domain.user.User;
import com.redhat.rhn.frontend.xmlrpc.packages.provider.PackagesProviderHandler;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackagesProviderHandlerContractTest extends BaseOpenApiTest {

    @Override protected String getApiNamespace() { return "packages.provider"; }
    @Override protected Class<PackagesProviderHandler> getHandlerClass() { return PackagesProviderHandler.class; }

    private PackagesProviderHandler handler() {
        return (PackagesProviderHandler) handlerMock;
    }

    @Test
    public void testList() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).list(with(mockUser));
            will(returnValue(List.of()));
        }});

        validateApiContract("/packages.provider/list", "POST").onHandlerMethod("list");
    }

    @Test
    public void testListKeys() throws Exception {
        var providerName = "ProviderA";

        context.checking(new Expectations() {{
            oneOf(handler()).listKeys(with(mockUser), with(providerName));
            will(returnValue(Set.of()));
        }});

        validateApiContract("/packages.provider/listKeys", "GET").onHandlerMethod("listKeys");
    }

    @Test
    public void testAssociateKey() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).associateKey(with(mockUser), with("ProviderA"),
                    with("KEY-CONTENT"), with("gpg"));
            will(returnValue(1));
        }});

        validateApiContract("/packages.provider/associateKey", "POST")
                .withBody(Map.of(
                    "providerName", "ProviderA",
                    "key", "KEY-CONTENT",
                    "type", "gpg"
                ))
                .onHandlerMethod("associateKey", User.class, String.class, String.class, String.class);
    }
}
