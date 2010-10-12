/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.util.List;

import com.aptana.editor.js.JSTypeConstants;

public class FunctionInferencingTests extends InferencingTestsBase
{
	/**
	 * testReturnsBoolean
	 */
	public void testReturnsBoolean()
	{
		String source = this.getContent("inferencing/function-returns-boolean.js");

		this.lastStatementTypeTests(source, "Boolean");
	}

	/**
	 * testReturnsFunction
	 */
	public void testReturnsFunction()
	{
		String source = this.getContent("inferencing/function-returns-function.js");

		this.lastStatementTypeTests(source, "Function");
	}

	/**
	 * testReturnsNumber
	 */
	public void testReturnsNumber()
	{
		String source = this.getContent("inferencing/function-returns-number.js");

		this.lastStatementTypeTests(source, "Number");
	}

	/**
	 * testReturnsRegExp
	 */
	public void testReturnsRegExp()
	{
		String source = this.getContent("inferencing/function-returns-regexp.js");

		this.lastStatementTypeTests(source, "RegExp");
	}

	/**
	 * testReturnsString
	 */
	public void testReturnsString()
	{
		String source = this.getContent("inferencing/function-returns-string.js");

		this.lastStatementTypeTests(source, "String");
	}

	/**
	 * testReturnsArray
	 */
	public void testReturnsArray()
	{
		String source = this.getContent("inferencing/function-returns-array.js");

		this.lastStatementTypeTests(source, "Array");
	}

	/**
	 * testReturnsArrayOfNumbers
	 */
	public void testReturnsArrayOfNumbers()
	{
		String source = this.getContent("inferencing/function-returns-array-of-numbers.js");

		this.lastStatementTypeTests(source, "Array<Number>");
	}

	/**
	 * testReturnsObject
	 */
	public void testReturnsObject()
	{
		String source = this.getContent("inferencing/function-returns-object.js");

		this.lastStatementTypeTests(source, "Object");
	}

	/**
	 * testReturnsUserObject
	 */
	public void testReturnsUserObject()
	{
		String source = this.getContent("inferencing/function-returns-user-object.js");
		List<String> types = this.getLastStatementTypes(source);

		assertNotNull(types);
		assertEquals(1, types.size());

		String type = types.get(0);
		assertTrue(type + " is not a user type", type.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX));

		this.lastStatementTypeTests(source, JSTypeConstants.DYNAMIC_CLASS_PREFIX);
	}
}
