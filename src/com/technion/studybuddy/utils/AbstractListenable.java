package com.technion.studybuddy.utils;


public abstract class AbstractListenable implements Listenable {

	private final OnEvent onUpdate = new OnEventListener();

	public AbstractListenable() {
		super();
	}

	@Override
	public void onUpdate(Action a) {
		onUpdate.register(a);
	}

	@Override
	public void update() {
		onUpdate.notifyListeners();
	}

}