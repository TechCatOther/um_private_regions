package org.ultramine.mods.privreg;

public enum Action
{
	ADD, REMOVE, UPDATE;

	private static final Action[] ACTIONS = {ADD, REMOVE, UPDATE};

	public static Action getAction(int origin)
	{
		return ACTIONS[origin];
	}
}
