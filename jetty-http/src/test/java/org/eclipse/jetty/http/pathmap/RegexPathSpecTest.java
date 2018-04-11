//
//  ========================================================================
//  Copyright (c) 1995-2018 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.http.pathmap;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class RegexPathSpecTest
{
    public static void assertMatches(PathSpec spec, String path)
    {
        String msg = String.format("Spec(\"%s\").matches(\"%s\")",spec.getDeclaration(),path);
        assertThat(msg,spec.matches(path),is(true));
    }

    public static void assertNotMatches(PathSpec spec, String path)
    {
        String msg = String.format("!Spec(\"%s\").matches(\"%s\")",spec.getDeclaration(),path);
        assertThat(msg,spec.matches(path),is(false));
    }

    @Test
    public void testExactSpec()
    {
        RegexPathSpec spec = new RegexPathSpec("^/a$");
        assertEquals("^/a$", spec.getDeclaration(), "Spec.pathSpec");
        assertEquals("^/a$", spec.getPattern().pattern(), "Spec.pattern");
        assertEquals(1, spec.getPathDepth(), "Spec.pathDepth");
        assertEquals(PathSpecGroup.EXACT, spec.group, "Spec.group");

        assertMatches(spec,"/a");

        assertNotMatches(spec,"/aa");
        assertNotMatches(spec,"/a/");
    }

    @Test
    public void testMiddleSpec()
    {
        RegexPathSpec spec = new RegexPathSpec("^/rest/([^/]*)/list$");
        assertEquals("^/rest/([^/]*)/list$", spec.getDeclaration(), "Spec.pathSpec");
        assertEquals("^/rest/([^/]*)/list$", spec.getPattern().pattern(), "Spec.pattern");
        assertEquals(3, spec.getPathDepth(), "Spec.pathDepth");
        assertEquals(PathSpecGroup.MIDDLE_GLOB, spec.group, "Spec.group");

        assertMatches(spec,"/rest/api/list");
        assertMatches(spec,"/rest/1.0/list");
        assertMatches(spec,"/rest/2.0/list");
        assertMatches(spec,"/rest/accounts/list");

        assertNotMatches(spec,"/a");
        assertNotMatches(spec,"/aa");
        assertNotMatches(spec,"/aa/bb");
        assertNotMatches(spec,"/rest/admin/delete");
        assertNotMatches(spec,"/rest/list");
    }

    @Test
    public void testMiddleSpecNoGrouping()
    {
        RegexPathSpec spec = new RegexPathSpec("^/rest/[^/]+/list$");
        assertEquals("^/rest/[^/]+/list$", spec.getDeclaration(), "Spec.pathSpec");
        assertEquals("^/rest/[^/]+/list$", spec.getPattern().pattern(), "Spec.pattern");
        assertEquals(3, spec.getPathDepth(), "Spec.pathDepth");
        assertEquals(PathSpecGroup.MIDDLE_GLOB, spec.group, "Spec.group");

        assertMatches(spec,"/rest/api/list");
        assertMatches(spec,"/rest/1.0/list");
        assertMatches(spec,"/rest/2.0/list");
        assertMatches(spec,"/rest/accounts/list");

        assertNotMatches(spec,"/a");
        assertNotMatches(spec,"/aa");
        assertNotMatches(spec,"/aa/bb");
        assertNotMatches(spec,"/rest/admin/delete");
        assertNotMatches(spec,"/rest/list");
    }

    @Test
    public void testPrefixSpec()
    {
        RegexPathSpec spec = new RegexPathSpec("^/a/(.*)$");
        assertEquals("^/a/(.*)$", spec.getDeclaration(), "Spec.pathSpec");
        assertEquals("^/a/(.*)$", spec.getPattern().pattern(), "Spec.pattern");
        assertEquals(2, spec.getPathDepth(), "Spec.pathDepth");
        assertEquals(PathSpecGroup.PREFIX_GLOB, spec.group, "Spec.group");

        assertMatches(spec,"/a/");
        assertMatches(spec,"/a/b");
        assertMatches(spec,"/a/b/c/d/e");

        assertNotMatches(spec,"/a");
        assertNotMatches(spec,"/aa");
        assertNotMatches(spec,"/aa/bb");
    }

    @Test
    public void testSuffixSpec()
    {
        RegexPathSpec spec = new RegexPathSpec("^(.*).do$");
        assertEquals("^(.*).do$", spec.getDeclaration(), "Spec.pathSpec");
        assertEquals("^(.*).do$", spec.getPattern().pattern(), "Spec.pattern");
        assertEquals(0, spec.getPathDepth(), "Spec.pathDepth");
        assertEquals(PathSpecGroup.SUFFIX_GLOB, spec.group, "Spec.group");

        assertMatches(spec,"/a.do");
        assertMatches(spec,"/a/b/c.do");
        assertMatches(spec,"/abcde.do");
        assertMatches(spec,"/abc/efg.do");

        assertNotMatches(spec,"/a");
        assertNotMatches(spec,"/aa");
        assertNotMatches(spec,"/aa/bb");
        assertNotMatches(spec,"/aa/bb.do/more");
    }
}
