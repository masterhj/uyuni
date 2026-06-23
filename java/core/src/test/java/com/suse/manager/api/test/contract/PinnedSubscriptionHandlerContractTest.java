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

import com.redhat.rhn.domain.server.PinnedSubscription;
import com.redhat.rhn.domain.user.User;
import com.redhat.rhn.frontend.xmlrpc.subscriptionmatching.PinnedSubscriptionHandler;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class PinnedSubscriptionHandlerContractTest extends BaseOpenApiTest {

    @Override protected String getApiNamespace() { return "subscriptionmatching.pinnedsubscription"; }
    @Override protected Class<PinnedSubscriptionHandler> getHandlerClass() { return PinnedSubscriptionHandler.class; }

    private PinnedSubscriptionHandler handler() {
        return (PinnedSubscriptionHandler) handlerMock;
    }

    @Test
    public void testList() throws Exception {
        context.checking(new Expectations() {{
            oneOf(handler()).list(with(mockUser));
            will(returnValue(List.of()));
        }});

        validateApiContract("/subscriptionmatching.pinnedsubscription/list", "POST")
                .onHandlerMethod("list", User.class);
    }

    @Test
    public void testCreate() throws Exception {
        var subscriptionId = 100;
        var sid = 1000;

        PinnedSubscription fake = new PinnedSubscription() {
            {
                setId(7L);
                setSubscriptionId((long) subscriptionId);
                setSystemId((long) sid);
            }
        };

        context.checking(new Expectations() {{
            oneOf(handler()).create(with(mockUser), with(subscriptionId), with(sid));
            will(returnValue(fake));
        }});

        validateApiContract("/subscriptionmatching.pinnedsubscription/create", "POST")
                .withBody(Map.of("subscriptionId", subscriptionId, "sid", sid))
                .onHandlerMethod("create", User.class, Integer.class, Integer.class);
    }

    @Test
    public void testDelete() throws Exception {
        var subscriptionId = 100;

        context.checking(new Expectations() {{
            oneOf(handler()).delete(with(mockUser), with(subscriptionId));
            will(returnValue(1));
        }});

        validateApiContract("/subscriptionmatching.pinnedsubscription/delete", "POST")
                .withBody(Map.of("subscriptionId", subscriptionId))
                .onHandlerMethod("delete", User.class, Integer.class);
    }
}
