package com.technion.studybuddy.persisters;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.technion.studybuddy.utils.Action;

public class PersisterImpl<Interface extends Persistable<Parent>, Implementation extends Interface, Parent>
				implements Persister<Interface, Implementation, Parent>
{

	private final RuntimeExceptionDao<Implementation, ?> dao;

	public PersisterImpl(RuntimeExceptionDao<Implementation, ?> dao) {
		super();
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void persist(Interface t) {
		dao.create((Implementation) t);
		listenOn(t);
	}

	/**
	 * This provides the ability to automatically persist changes in state
	 * 
	 * @param t
	 */
	private void listenOn(final Interface t) {
		t.onUpdate(new Action() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				dao.update((Implementation) t);
			}
		});
		t.onDelete(new Action() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				dao.delete((Implementation) t);
			}
		});
	}

	private void listenOn(List<Interface> list) {
		for (Interface t : list) {
			listenOn(t);
		}
	}

	@Override
	public void persist(List<Interface> list) {
		for (Interface t : list) {
			persist(t);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Interface> getAll() {
		List<Interface> $ = (List<Interface>) dao.queryForAll();
		listenOn($);
		return $;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Interface> getByParent(Parent p) {
		List<Interface> $;
		try {
			$ = (List<Interface>) dao.queryBuilder().where()
							.eq(Interface.PARENT_COLUMN_ID, p).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			$ = Collections.<Interface> emptyList();
		}

		listenOn($);
		return $;
	}

}