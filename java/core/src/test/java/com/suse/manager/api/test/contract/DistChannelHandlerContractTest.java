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
import com.redhat.rhn.frontend.xmlrpc.distchannel.DistChannelHandler;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.Map;

public class DistChannelHandlerContractTest extends BaseOpenApiTest {

    @Override protected String getApiNamespace() { return "distchannel"; }
    @Override protected Class<DistChannelHandler> getHandlerClass() { return DistChannelHandler.class; }

    private DistChannelHandler handler() {
        return (DistChannelHandler) handlerMock;
    }

    @Test
    public void testListDefaultMaps() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).listDefaultMaps(with(mockUser));
            will(returnValue(new Object[]{}));
        }});

        validateApiContract("/distchannel/listDefaultMaps", "GET").onHandlerMethod("listDefaultMaps");
    }

    @Test
    public void testListMapsForOrgNoArg() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).listMapsForOrg(with(mockUser));
            will(returnValue(new Object[]{}));
        }});

        validateApiContract("/distchannel/listMapsForOrg", "GET")
                .onHandlerMethod("listMapsForOrg", User.class);
    }

    @Test
    public void testListMapsForOrgWithOrgId() throws Exception {
        var orgId = 42;

        context.checking(new Expectations() {{
            oneOf(handler()).listMapsForOrg(with(mockUser), with(orgId));
            will(returnValue(new Object[]{}));
        }});

        validateApiContract("/distchannel/listMapsForOrg", "GET")
                .onHandlerMethod("listMapsForOrg", User.class, Integer.class);
    }

    @Test
    public void testSetMapForOrg() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).setMapForOrg(with(mockUser), with("SLES"), with("15.4"), with("x86_64"), with("sles15-x86_64"));
            will(returnValue(1));
        }});

        validateApiContract("/distchannel/setMapForOrg", "POST")
                .withBody(Map.of(
                    "os", "SLES",
                    "release", "15.4",
                    "archName", "x86_64",
                    "channelLabel", "sles15-x86_64"
                ))
                .onHandlerMethod("setMapForOrg", User.class, String.class, String.class, String.class, String.class);
    }
}
