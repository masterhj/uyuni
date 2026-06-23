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

import com.redhat.rhn.frontend.xmlrpc.auth.AuthHandler;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.Map;

public class AuthHandlerContractTest extends BaseOpenApiTest {

    @Override protected String getApiNamespace() { return "auth"; }
    @Override protected Class<AuthHandler> getHandlerClass() { return AuthHandler.class; }

    private AuthHandler handler() {
        return (AuthHandler) handlerMock;
    }

    @Test
    public void testLogin() throws Exception {
        var username = "admin";
        var password = "secret";

        context.checking(new Expectations() {{
            oneOf(handler()).login(with(username), with(password));
            will(returnValue("fake-session"));
        }});

        validateApiContract("/auth/login", "POST")
                .withBody(Map.of("username", username, "password", password))
                .onHandlerMethod("login", String.class, String.class);
    }

    @Test
    public void testLoginWithDuration() throws Exception {
        var username = "admin";
        var password = "secret";
        var duration = 3600;

        context.checking(new Expectations() {{
            oneOf(handler()).login(with(username), with(password), with(duration));
            will(returnValue("fake-session"));
        }});

        validateApiContract("/auth/login", "POST")
                .withBody(Map.of("username", username, "password", password, "duration", duration))
                .onHandlerMethod("login", String.class, String.class, Integer.class);
    }

    @Test
    public void testLogout() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).logout(with("fake-session"));
            will(returnValue(1));
        }});

        validateApiContract("/auth/logout", "POST")
                .onHandlerMethod("logout", String.class);
    }
}