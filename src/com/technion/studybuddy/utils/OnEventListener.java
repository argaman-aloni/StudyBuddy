package com.technion.studybuddy.utils;

import java.util.ArrayList;
import java.util.List;

public class OnEventListener implements OnEvent {
	private final List<Action> actions = new ArrayList<Action>();

	@Override
	public void register(Action f) {
		if (null == f)
			throw new IllegalArgumentException("f is null");
		actions.add(f);
	}

	@Override
	public void unregister(Action f) {
		if (f == null)
			throw new IllegalArgumentException("f is null");
		actions.remove(f);

	}

	@Override
	public void clear() {
		actions.clear();
	}

	@Override
	public void notifyListeners() {
		for (Action a : actions) {
			a.run();
		}
	}
}
