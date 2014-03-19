package com.technion.studybuddy.persisters;

import com.technion.studybuddy.utils.AbstractListenable;
import com.technion.studybuddy.utils.Action;
import com.technion.studybuddy.utils.Listenable;
import com.technion.studybuddy.utils.OnEvent;
import com.technion.studybuddy.utils.OnEventListener;

public abstract class AbstractPersistable<P> extends AbstractListenable
				implements Persistable<P>, Listenable
{

	private final OnEvent onDelete = new OnEventListener();

	public AbstractPersistable() {
		super();
	}

	@Override
	public void onDelete(Action a) {
		onDelete.register(a);

	}

	@Override
	public void delete() {
		onDelete.notifyListeners();
	}

}