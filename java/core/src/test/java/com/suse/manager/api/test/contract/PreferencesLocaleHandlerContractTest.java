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
import com.redhat.rhn.frontend.xmlrpc.preferences.locale.PreferencesLocaleHandler;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.Map;

public class PreferencesLocaleHandlerContractTest extends BaseOpenApiTest {

    @Override protected String getApiNamespace() { return "preferences.locale"; }
    @Override protected Class<PreferencesLocaleHandler> getHandlerClass() { return PreferencesLocaleHandler.class; }

    private PreferencesLocaleHandler handler() {
        return (PreferencesLocaleHandler) handlerMock;
    }

    @Test
    public void testSetTimeZone() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).setTimeZone(with(mockUser), with("alice"), with(5));
            will(returnValue(1));
        }});

        validateApiContract("/preferences.locale/setTimeZone", "POST")
                .withBody(Map.of("login", "alice", "tzid", 5))
                .onHandlerMethod("setTimeZone", User.class, String.class, Integer.class);
    }

    @Test
    public void testSetLocale() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).setLocale(with(mockUser), with("alice"), with("en_US"));
            will(returnValue(1));
        }});

        validateApiContract("/preferences.locale/setLocale", "POST")
                .withBody(Map.of("login", "alice", "locale", "en_US"))
                .onHandlerMethod("setLocale", User.class, String.class, String.class);
    }

    @Test
    public void testListTimeZones() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).listTimeZones();
            will(returnValue(new Object[]{}));
        }});

        validateApiContract("/preferences.locale/listTimeZones", "GET").onHandlerMethod("listTimeZones");
    }

    @Test
    public void testListLocales() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).listLocales();
            will(returnValue(new Object[]{}));
        }});

        validateApiContract("/preferences.locale/listLocales", "GET").onHandlerMethod("listLocales");
    }
}
