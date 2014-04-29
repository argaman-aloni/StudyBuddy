/**
 * 
 */
package com.technion.studybuddy.Views;

import org.achartengine.GraphicalView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.technion.studybuddy.R;
import com.technion.studybuddy.charts.ProgressPieChart;

/**
 * @author Argaman
 * 
 */
public class ChartFullFragment extends DialogFragment {

	ProgressPieChart chartGen = new ProgressPieChart();
	public int chartIndex = -1;
	private GraphicalView graphicalView = null;
	private LinearLayout layout;

	private void generateChart() {
		switch (chartIndex) {
		case 0:
			graphicalView = chartGen.getOverviewPieChart(getActivity());
			break;
		case 1:
			graphicalView = chartGen.getLecturesPieChart(getActivity());
			break;
		default:
			graphicalView = chartGen.getTutorialsPieChart(getActivity());
			break;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		layout = (LinearLayout) getActivity().getLayoutInflater().inflate(
				R.layout.piechart_full_layout, null);
		builder.setView(layout);
		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (chartIndex != -1) {
			generateChart();
			layout.addView(graphicalView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
	}

}
