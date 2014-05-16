package com.technion.studybuddy.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.technion.studybuddy.data.CompositeVisitor;
import com.technion.studybuddy.exceptions.ItemNotDoneError;
import com.technion.studybuddy.exceptions.NoItemsLeftException;
import com.technion.studybuddy.persisters.AbstractPersistable;
import com.technion.studybuddy.persisters.Persistable;
import com.technion.studybuddy.utils.Action;
import com.technion.studybuddy.utils.Utils;

@DatabaseTable
public class StudyResourceImpl extends AbstractPersistable<Course> implements
		StudyResource
{

	@DatabaseField(generatedId = true)
	private UUID id;

	@DatabaseField
	private String name;

	private final List<StudyItem> itemList = new ArrayList<StudyItem>();

	@DatabaseField(foreign = true, canBeNull = false, index = true, columnName = Persistable.PARENT_COLUMN_ID)
	private CourseImpl parent;

	public StudyResourceImpl()
	{

	}

	public StudyResourceImpl(String name, List<StudyItem> list)
	{
		this.name = name;
		setItems(list);
	}

	@Override
	public void accept(CompositeVisitor cv)
	{
		for (StudyItem item : itemList)
			cv.visit(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.StudyResource#addItem(com.technion
	 * .coolie.studybuddy.models.StudyItem)
	 */
	@Override
	public void addItem(final StudyItem item)
	{
		itemList.add(item);
		item.onDelete(new Action()
		{
			@Override
			public void run()
			{
				itemList.remove(item);
			};
		});
		item.setParent(this);
		update();
	}

	@Override
	public Course getParent()
	{
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.StudyResource#getDoneItemsCount()
	 */
	@Override
	public int getDoneItemsCount()
	{
		return getDoneItems().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.StudyResource#getDoneItems()
	 */
	@Override
	public List<StudyItem> getDoneItems()
	{
		// ARIK check this
		return (Utils.filter(itemList, StudyItems.isDoneMatcher));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.StudyResource#getRemainingItems()
	 */
	@Override
	public List<StudyItem> getRemainingItems()
	{
		// ARIK check this
		return (Utils.filter(itemList, StudyItems.isNotDoneMatcher));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.StudyResource#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.StudyResource#getRemainingItemsCount
	 * ()
	 */
	@Override
	public int getRemainingItemsCount()
	{
		return getRemainingItems().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.StudyResource#getTotalItemCount()
	 */
	@Override
	public int getTotalItemCount()
	{
		return itemList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.technion.coolie.studybuddy.models.StudyResource#setItems(java.util
	 * .List)
	 */
	@Override
	public void setItems(List<StudyItem> list)
	{
		itemList.clear();
		for (StudyItem si : list)
			addItem(si);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.studybuddy.models.StudyResource#getItems()
	 */
	@Override
	public List<StudyItem> getItems()
	{
		return itemList;
	}

	@Override
	public void setParent(Course p)
	{
		parent = (CourseImpl) p;
	}

	@Override
	public int getNumOfItemsBehind(int semesterWeek, int totalWeeks)
	{

		if (semesterWeek == 0)
			return 0;
		int behind = getNumOfItemsDue(semesterWeek, totalWeeks)
				- getDoneItemsCount();

		behind = (behind < 0) ? 0 : behind; // prevent underflow
		behind = (behind > getTotalItemCount()) ? getTotalItemCount() : behind; // prevent
		// overflow

		return behind;
	}

	@Override
	public void toggleDone(int i)
	{
		itemList.get(i - 1).toggleDone();
	}

	@Override
	public StudyItem getNextItem() throws NoItemsLeftException
	{

		if (getRemainingItems().isEmpty())
			throw new NoItemsLeftException();

		return getRemainingItems().get(0);

	}

	@Override
	public Collection<Date> getDoneDates()
	{

		Collection<Date> $ = new ArrayList<Date>();

		for (StudyItem i : getDoneItems())
			try
			{
				$.add(i.getDoneDate());
			} catch (ItemNotDoneError e)
			{

				e.printStackTrace();
			}

		return $;
	}

	@Override
	public int getNumOfItemsDue(int semesterWeek, int totalWeeks)
	{

		if (semesterWeek == 0)
			return 0;

		double unit = ((double) totalWeeks) / getTotalItemCount();

		int due = (int) Math.floor((semesterWeek - 1.0) / unit);

		if (due > getTotalItemCount())
			due = getTotalItemCount();
		return due;
	}

	@Override
	public JSONObject toJson()
	{
		JSONObject object = new JSONObject();
		JSONArray arrayItems = new JSONArray();
		try
		{
			object.put("name", name);
			for (StudyItem item : itemList)
				arrayItems.put(item.toJson());
			object.put("itemList", arrayItems);
			object.put("parent", parent);
			System.out.println(object);
			return object;
		} catch (JSONException e)
		{
			return null;
		}
	}

	@Override
	public void fromJson(JSONObject json)
	{
		try
		{
			name = json.getString("name");
			if (json.has("itemList"))
			{
				JSONArray arrayItems = json.getJSONArray("itemList");
				new ArrayList<StudyItem>();

				for (int i = 0; i < arrayItems.length(); i++)
				{
					StudyItem item = new StudyItemImpl();
					item.fromJson(arrayItems.getJSONObject(i));
					itemList.add(item);
				}
			}
		} catch (JSONException e)
		{
		}
	}
}
