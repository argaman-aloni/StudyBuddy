package com.technion.studybuddy.network;

import org.json.JSONObject;

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

	public SyncTask addTask(JSONObject jsonObject, String type,
			NetworkTaskImportence priority)
	{
		sqLiteDatabase = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.object],
						jsonObject.toString());
		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.type],
						type);
		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.priority],
						priority.ordinal());
		contentValues
				.put(Constants.Network_types[Constants.Network_fields_constants.progress],
						SyncProgress.Queued.ordinal());
		sqLiteDatabase.insert(Constants.Table_Name, null, contentValues);
		sqLiteDatabase.close();
		sqLiteDatabase = helper.getReadableDatabase();
		String query = "SELECT ROWID from MYTABLE order by ROWID DESC limit 1";
		sqLiteDatabase.close();
		Cursor c = sqLiteDatabase.rawQuery(query, null);
		if (c != null && c.moveToFirst())
		{
			int lastId = c.getInt(0);
			return new SyncTask(lastId, jsonObject, context, priority, type);

		}

		return null;
	}

	public SyncTask addTask(JSONObject jsonObject, String type)
	{
		return addTask(jsonObject, type, NetworkTaskImportence.Low);
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
}
