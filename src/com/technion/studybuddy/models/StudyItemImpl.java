package com.technion.studybuddy.models;

import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.technion.studybuddy.exceptions.ItemNotDoneError;
import com.technion.studybuddy.persisters.AbstractPersistable;
import com.technion.studybuddy.persisters.Persistable;
import com.technion.studybuddy.utils.Action;
import com.technion.studybuddy.utils.OnEvent;
import com.technion.studybuddy.utils.OnEventListener;

@DatabaseTable(tableName = "study_items")
public class StudyItemImpl extends AbstractPersistable<StudyResource> implements
		StudyItem {
	@DatabaseField(generatedId = true)
	private UUID id;

	@DatabaseField
	private int num;

	@DatabaseField
	private String label;

	@DatabaseField
	private boolean done;

	@DatabaseField(foreign = true, canBeNull = false, index = true, columnName = Persistable.PARENT_COLUMN_ID)
	private StudyResourceImpl parent;

	@DatabaseField
	private Date dateDone;

	private final OnEvent onDone = new OnEventListener();
	private final OnEvent onUnDone = new OnEventListener();

	public StudyItemImpl() {

	}

	public StudyItemImpl(int num, String label) {
		this.num = num;
		this.label = label;
		done = false;
		dateDone = null;
	}

	@Override
	public int compareTo(StudyItem another) {
		return num - another.getNum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.StudyItem#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.StudyItem#getNum()
	 */
	@Override
	public int getNum() {
		return num;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.StudyItem#isDone()
	 */
	@Override
	public boolean isDone() {
		return (done == true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.StudyItem#toggleDone()
	 */
	@Override
	public void toggleDone() {
		boolean wasDone = done;
		done = !done;

		if (wasDone)
			onUnDone.notifyListeners();
		else
			onDone.notifyListeners();

		if (done)
			dateDone = new Date();
		else
			dateDone = null;

		update();
	}

	@Override
	public StudyResourceImpl getParent() {
		return parent;
	}

	@Override
	public void setParent(StudyResource sr) {
		parent = (StudyResourceImpl) sr;
	}

	@Override
	public void setLabel(String newName) {
		label = newName;
		update();
	}

	@Override
	public Date getDoneDate() throws ItemNotDoneError {
		if (dateDone == null)
			throw new ItemNotDoneError();

		return dateDone;
	}

	@Override
	public void onDone(Action a) {
		onDone.register(a);

	}

	@Override
	public void onUnDone(Action a) {
		onUnDone.register(a);
	}

	@Override
	public String toJson() {
		JSONObject object = new JSONObject();
		new JSONArray();
		try {
			// object.put("id", id.toString()); //TODO: check this.
			object.put("num", num);
			object.put("label", label);
			object.put("done", done);
			// TODO: add list of links.
			String jsonStr = object.toString();
			System.out.println(jsonStr);
			return jsonStr;
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public Object fromJson(String jsonStr) {
		JSONObject json;
		try {
			json = new JSONObject(jsonStr);
			// UUID id = (UUID) json.get("id"); //TODO: check this.
			// TODO: add list of links.
			int _num = json.getInt("num");
			String _label = json.getString("label");
			StudyItem res = new StudyItemImpl(_num, _label);
			if (json.getBoolean("done"))
				res.toggleDone();
			return res;
		} catch (JSONException e) {
			return null;
		}
	}

}
