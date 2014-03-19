package com.technion.studybuddy.data;

import android.graphics.Color;

public class ColorTable
{

	public static int getColor(int delay) {
		if(delay <1) 
			return Color.parseColor("#ff90c000");
		else if (delay == 1)
			return Color.parseColor("#fff0b030");
		else if (delay == 2)
			return Color.parseColor("#fff08000");
		else
			return Color.parseColor("#ffc00000");
	}
}
