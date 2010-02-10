package com.aptana.scope;

import junit.framework.TestCase;

public class NegativeLookaheadTests extends TestCase
{
	/**
	 * testLookaheadMatches
	 */
	public void testLookaheadMatches()
	{
		ScopeSelector selector = new ScopeSelector("A B - C");

		assertFalse(selector.matches("A B C"));
	}

	/**
	 * testLookaheadDoesNotMatch
	 */
	public void testLookaheadDoesNotMatch()
	{
		ScopeSelector selector = new ScopeSelector("A B - C");

		assertTrue(selector.matches("A B D"));
	}

	/**
	 * testLookaheadAgainstNothing
	 */
	public void testLookaheadAgainstNothing()
	{
		ScopeSelector selector = new ScopeSelector("A B - C");

		assertTrue(selector.matches("A B"));
	}
}
