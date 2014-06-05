package com.technion.studybuddy.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.technion.studybuddy.data.DataStore;
import com.technion.studybuddy.exceptions.ItemNotDoneError;
import com.technion.studybuddy.persisters.AbstractPersistable;
import com.technion.studybuddy.persisters.Persistable;
import com.technion.studybuddy.utils.Action;
import com.technion.studybuddy.utils.Constants;
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

	@DatabaseField
	private String links;

	final OnEvent onDone = new OnEventListener();
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

	@Override
	public List<String> getLinks() {
		if (links != null)
			return Arrays.asList(links.split(" "));
		return new ArrayList<String>(); // TODO change to Lists.newLinkedList
	}

	@Override
	public void setLinks(List<String> _links) {
		StringBuilder builder = new StringBuilder();
		for (String link : _links)
			builder.append(link).append(" ");
		links = builder.toString().isEmpty() ? "" : builder.toString()
				.substring(0, builder.length() - 1);
	}

	@Override
	public void addLink(String link) {
		links = links + " " + link;
	}

	@Override
	public void removeLink(String link) {
		links.replace(" " + link, "");
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
	public void setDone(Date date) {
		done = true;
		dateDone = date;
		update();
	}

	@Override
	public void setUnDone() {
		done = false;
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
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		new JSONArray();
		try {
			object.put("id", getParent().getParent().getId());
			object.put("label", label);
			object.put(
					"date",
					dateDone != null ? dateDone.getTime() : System
							.currentTimeMillis());
			object.put("type", getParent().getName());
			JSONArray linksArray = new JSONArray();
			for (String link : getLinks())
				linksArray.put(link);
			object.put(Constants.LINKS_JSON, linksArray);
			System.out.println(object);
			return object;
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public void fromJson(JSONObject json) {
		try {
			JSONArray links = json.getJSONArray(Constants.LINKS_JSON);
			List<String> linksList = new ArrayList<>();
			for (int i = 0; i < links.length(); i++)
				linksList.add(links.getJSONObject(i).toString());
			int _num = json.getInt("num");
			String _label = json.getString("label");
			num = _num;
			label = _label;
			new StudyItemImpl(_num, _label);
			setLinks(linksList);
			if (json.getBoolean("done"))
				toggleDone();
			return;
		} catch (JSONException e) {
		}
	}

	@Override
	public String getItemType() {
		return getParent().getName();
	}

	@Override
	public void update() {
		super.update();
		DataStore.getInstance().updateWidgetData();
	}

}
