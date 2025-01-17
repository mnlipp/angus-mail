/*
 * Copyright (c) 2009, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.eclipse.angus.mail.pop3;

import java.io.*;
import java.util.Properties;

import jakarta.mail.Session;
import jakarta.mail.Store;

import org.eclipse.angus.mail.test.TestServer;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Test that authentication information is only included in
 * the debug output when explicitly requested by setting the
 * property "mail.debug.auth" to "true".
 *
 * XXX - should test all authentication types, but that requires
 *	 more work in the dummy test server.
 */
public final class POP3AuthDebugTest {

    // timeout the test in case of deadlock
    @Rule
    public Timeout deadlockTimeout = Timeout.seconds(20);

    /**
     * Test that authentication information isn't included in the debug output.
     */
    @Test
    public void testNoAuthDefault() {
	final Properties properties = new Properties();
	assertFalse("PASS in debug output", test(properties, "PASS"));
    }

    @Test
    public void testNoAuth() {
	final Properties properties = new Properties();
	properties.setProperty("mail.debug.auth", "false");
	assertFalse("PASS in debug output", test(properties, "PASS"));
    }

    /**
     * Test that authentication information *is* included in the debug output.
     */
    @Test
    public void testAuth() {
	final Properties properties = new Properties();
	properties.setProperty("mail.debug.auth", "true");
	assertTrue("PASS in debug output", test(properties, "PASS"));
    }

    /**
     * Create a test server, connect to it, and collect the debug output.
     * Scan the debug output looking for "expect", return true if found.
     */
    public boolean test(Properties properties, String expect) {
	TestServer server = null;
	try {
	    final POP3Handler handler = new POP3Handler();
	    server = new TestServer(handler);
	    server.start();
	    Thread.sleep(1000);

	    properties.setProperty("mail.pop3.host", "localhost");
	    properties.setProperty("mail.pop3.port", "" + server.getPort());
	    final Session session = Session.getInstance(properties);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(bos);
	    session.setDebugOut(ps);
	    session.setDebug(true);

	    final Store store = session.getStore("pop3");
	    try {
		store.connect("test", "test");
	    } catch (Exception ex) {
		System.out.println(ex);
		//ex.printStackTrace();
		fail(ex.toString());
	    } finally {
		store.close();
	    }

	    ps.close();
	    bos.close();
	    ByteArrayInputStream bis =
		new ByteArrayInputStream(bos.toByteArray());
	    BufferedReader r = new BufferedReader(
					new InputStreamReader(bis, "us-ascii"));
	    String line;
	    boolean found = false;
	    while ((line = r.readLine()) != null) {
		if (line.startsWith("DEBUG"))
		    continue;
		if (line.startsWith("*"))
		    continue;
		if (line.indexOf(expect) >= 0)
		    found = true;
	    }
	    r.close();
	    return found;
	} catch (final Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	    return false;	// XXX - doesn't matter
	} finally {
	    if (server != null) {
		server.quit();
	    }
	}
    }
}
