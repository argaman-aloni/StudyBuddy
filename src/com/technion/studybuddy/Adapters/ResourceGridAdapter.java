package com.technion.studybuddy.adapters;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.technion.studybuddy.R;
import com.technion.studybuddy.Views.PopupMenu;
import com.technion.studybuddy.Views.StrikeThroughTextView;
import com.technion.studybuddy.models.StudyItem;


public class ResourceGridAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private List<StudyItem> items;
	private Activity activity;

	// private AnimationSet inAnimation;
	//
	// private Animation outAnimation;

	/**
	 * @param context
	 */
	public ResourceGridAdapter(Activity activity, List<StudyItem> list) {
		super();
		this.activity = activity;
		mInflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		items = list;

	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		StrikeThroughTextView textView = null;
		ImageView menuView = null;
		if (convertView == null) {
			ViewHolder holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.stb_view_single_resource,
							null);
			textView = (StrikeThroughTextView) convertView
							.findViewById(R.id.stb_strike_text);
			menuView = (ImageView) convertView.findViewById(R.id.stb_overflow);
			menuView.setVisibility(View.GONE);
			showMenu(menuView);
			holder.textView = textView;
			holder.imageView = menuView;
			convertView.setTag(holder);
			textView.setOnClickListener(new StudyItemClicked(position));

		} else {
			textView = ((ViewHolder) convertView.getTag()).textView;
			textView.setText(getItem(position).toString());
			menuView = ((ViewHolder) convertView.getTag()).imageView;
		}
		final StrikeThroughTextView finalTextView = textView;
		menuView.setOnClickListener(new OnMenuClick(finalTextView, position));
		OnLongClickListenerImplementation overflowhandler = new OnLongClickListenerImplementation(
						menuView);
		convertView.setOnLongClickListener(overflowhandler);
		textView.setOnLongClickListener(overflowhandler);
		textView.setStriked(items.get(position).isDone());
		textView.setText(items.get(position).getLabel());

		return convertView;
	}

	public void removeItem(View view) {
		String val = ((StrikeThroughTextView) view).getText().toString();
		items.remove(val);
		notifyDataSetChanged();
	}

	private void updateResourceName(String newName, View convertView, int which)
	{
		items.get(which).setLabel(newName);

		notifyDataSetChanged();
	}

	private void showMenu(final ImageView menuView) {
		Animation inAnimation = AnimationUtils.loadAnimation(activity,
						R.anim.stb_in);
		menuView.setAnimation(inAnimation);
		menuView.setVisibility(View.VISIBLE);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				AnimationSet outAnimation = (AnimationSet) AnimationUtils
								.loadAnimation(activity, R.anim.stb_out);
				// Hide your View after 3 seconds
				menuView.setVisibility(View.GONE);
				menuView.setAnimation(outAnimation);
			}
		}, 3000);
	}

	private final class StudyItemClicked implements OnClickListener {
		private final int position;

		private StudyItemClicked(int position) {
			this.position = position;
		}

		@Override
		public void onClick(final View v) {

			StudyItem item = items.get(position);

			item.toggleDone();
			((StrikeThroughTextView) v).setStriked(item.isDone());

			activity.setResult(Activity.RESULT_OK);

		}
	}

	private class ViewHolder {
		public StrikeThroughTextView textView;
		public ImageView imageView;
	}

	private final class OnLongClickListenerImplementation implements
					OnLongClickListener
	{
		// private final StrikeThroughTextView finalTextView;
		// private int position;
		private ImageView menuView;

		private OnLongClickListenerImplementation(ImageView menuView) {
			// this.finalTextView = finalTextView;
			// this.position = position;
			this.menuView = menuView;
		}

		@Override
		public boolean onLongClick(final View v) {
			showMenu(menuView);
			return true;
		}
	}

	private final class OnMenuClick implements OnClickListener {
		private final StrikeThroughTextView finalTextView;
		private final int position;

		private OnMenuClick(StrikeThroughTextView finalTextView, int position) {
			this.finalTextView = finalTextView;
			this.position = position;
		}

		@Override
		public void onClick(final View v) {
			PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
			popupMenu.setOnMenuItemClickListener(new com.technion.studybuddy.Views.PopupMenu.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if (item.getItemId() == R.id.stb_rename) {
						final View view = LayoutInflater
										.from(v.getContext())
										.inflate(R.layout.stb_view_resourse_rename,
														null);
						AlertDialog.Builder builder = new Builder(v
										.getContext());
						builder.setTitle("Edit Item").setView(view)
										.setCancelable(true);
						builder.setPositiveButton("Save",
										new AlertDialog.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,
																int which)
											{

												updateResourceName(
																((EditText) view.findViewById(R.id.stb_resourse_rename_name))
																				.getText()
																				.toString(),
																v, position);
												dialog.dismiss();
											}
										});
						builder.setNegativeButton("Cancel",
										new AlertDialog.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,
																int which)
											{
												dialog.dismiss();
											}
										});
						((EditText) view.findViewById(R.id.stb_resourse_rename_name))
										.setText(finalTextView.getText());
						builder.create().show();
						return true;
					}
					return false;
				}
			});
			popupMenu.inflate(R.menu.stb_resource_menu);
			popupMenu.show();

		}
	}

	public interface CrossGesture {
		public void remove(View view);
	}

}
