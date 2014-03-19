package com.technion.studybuddy.utils;

public interface OnEvent {
	void register(Action f);

	void unregister(Action f);

	void clear();

	void notifyListeners();
}
