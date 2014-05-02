package com.technion.studybuddy.network;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.technion.studybuddy.utils.Constants;

public class NetworkSqlHelper extends SQLiteOpenHelper
{

	public NetworkSqlHelper(Context context, String name,
			CursorFactory factory, int version)
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		StringBuilder Command = new StringBuilder();
		Command.append("CREATE TABLE " + Constants.Table_Name + " (");
		for (int i = 0; i < Constants.Network_fields.length - 1; i++)
		{
			Command.append(Constants.Network_fields[i] + " "
					+ Constants.Network_types[i] + ",");
		}
		Command.append(Constants.Network_fields[Constants.Network_fields.length - 1]
				+ " "
				+ Constants.Network_types[Constants.Network_types.length - 1]
				+ ");");
		db.execSQL(Command.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("drop table if exist " + Constants.Table_Name);
		onCreate(db);

	}

}
