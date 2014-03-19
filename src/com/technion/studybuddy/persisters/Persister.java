package com.technion.studybuddy.persisters;

import java.util.List;

public interface Persister<Interface, Implementation, P> {
	public void persist(Interface t);

	public List<Interface> getAll();

	public List<Interface> getByParent(P p);

	public void persist(List<Interface> list);
}
