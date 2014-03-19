package com.technion.studybuddy.graphs;

import java.util.List;

import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;

enum Progress
{
	; // namespace

	static XYMultipleSeriesDataset buildBarDataset(String[] titles,
			List<double[]> values)
	{
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = titles.length;

		for (int i = 0; i < length; i++)
		{
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++)
			{
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}
}
