package com.technion.studybuddy.network;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.technion.studybuddy.utils.Constants;

public class NetworkDBAdapter
{
	private final Context context;
	private final NetworkSqlHelper helper;
	private SQLiteDatabase sqLiteDatabase;

	/**
	 * @param context
	 */
	public NetworkDBAdapter(Context context)
	{
		super();
		helper = new NetworkSqlHelper(context, Constants.DB_Name, null,
				Constants.DB_ver);
		this.context = context;

	}

	public SyncTask addTask(String json, String type,
			NetworkTaskImportence priority)
	{
		sqLiteDatabase = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.object],
						json);
		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.type],
						type);
		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.priority],
						priority.ordinal());
		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.progress],
						SyncProgress.Queued.ordinal());
		long lastId = sqLiteDatabase.insert(Constants.Table_Name, null,
				contentValues);
		sqLiteDatabase.close();

		return new SyncTask(lastId, json, context, priority, type);

	}

	public SyncTask addTask(String json, String type)
	{
		return addTask(json, type, NetworkTaskImportence.Low);
	}

	public void updateProgress(SyncTask task, SyncProgress progress)
	{
		sqLiteDatabase = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.progress],
						task.getProgress().ordinal());
		sqLiteDatabase.update(Constants.Table_Name, contentValues,
				Constants.Network_fields[0] + " = " + task.getId(), null);
		sqLiteDatabase.close();

	}

	public List<SyncTask> getTaskNotDone()
	{
		List<SyncTask> tasks = new ArrayList<SyncTask>();
		sqLiteDatabase = helper.getReadableDatabase();
		Cursor result = sqLiteDatabase
				.query(Constants.Table_Name,
						Constants.Network_fields,
						Constants.Network_fields[Constants.Network_fields_constants.progress]
								+ " <> " + SyncProgress.Completed.ordinal(),
						null, null, null, null);
		result.moveToFirst();
		while (!result.isAfterLast())
		{
			tasks.add(cursorToTask(result));
			result.moveToNext();
		}
		sqLiteDatabase.close();
		return tasks;
	}

	private SyncTask cursorToTask(Cursor result)
	{
		return new SyncTask(result.getLong(0), result.getString(1), context,
				NetworkTaskImportence.values()[result.getInt(3)],
				result.getString(4));
	}
}
