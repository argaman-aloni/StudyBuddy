package com.technion.studybuddy.network;

import android.content.Context;

public class SyncTask
{
	private final long id;
	private final String json;
	private SyncProgress progress;
	private final Context context;
	private final String type;
	private final NetworkTaskImportence priority;

	/**
	 * @param object
	 * @param context
	 */
	public SyncTask(long id, String json, Context context,
			NetworkTaskImportence priority, String type)
	{
		super();
		this.id = id;
		this.priority = priority;
		progress = SyncProgress.Queued;
		this.json = json;
		this.context = context;
		this.type = type;
	}

	public void start()
	{
		SendAsyncTask sendTask = new SendAsyncTask(this, context);
		sendTask.execute();
	}

	void markCompleted(NetworkDBAdapter adapter)
	{
		adapter.updateProgress(this, SyncProgress.Completed);
		progress = SyncProgress.Completed;
	}

	void markQueued(NetworkDBAdapter adapter)
	{
		adapter.updateProgress(this, SyncProgress.Queued);
		progress = SyncProgress.Queued;
	}

	void markOnProgress(NetworkDBAdapter adapter)
	{
		adapter.updateProgress(this, SyncProgress.In_Progress);
		progress = SyncProgress.In_Progress;
	}

	/**
	 * @return the type
	 */
	public synchronized String getType()
	{
		return type;
	}

	/**
	 * @return the priority
	 */
	public synchronized NetworkTaskImportence getPriority()
	{
		return priority;
	}

	/**
	 * @return the id
	 */
	public synchronized long getId()
	{
		return id;
	}

	/**
	 * @return the progress
	 */
	public synchronized SyncProgress getProgress()
	{
		return progress;
	}

	/**
	 * @return the json
	 */
	public synchronized String getJson()
	{
		return json;
	}
}
