/*
 * Copyright (c) 2013, 2023 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.angus.mail.iap;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Test ResponseInputStream.
 */
public class ResponseInputStreamTest {

    /**
     * Test that an EOF while reading a literal throws an IOException.
     */
    @Test
    public void testEofWhileReadingLiteral() throws Exception {
	ByteArrayInputStream bis = new ByteArrayInputStream(
	    "test{1}\r\n".getBytes("ISO-8859-1"));
	ResponseInputStream ris = new ResponseInputStream(bis);
	try {
	    ris.readResponse();
	} catch (IOException ex) {
	    // success!
	    return;
	}
	fail("no exception");
    }
}
