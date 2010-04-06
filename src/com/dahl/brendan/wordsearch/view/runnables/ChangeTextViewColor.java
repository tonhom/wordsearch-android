//    This file is part of Open WordSearch.
//
//    Open WordSearch is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Open WordSearch is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Open WordSearch.  If not, see <http://www.gnu.org/licenses/>.
//
//	  Copyright 2009, 2010 Brendan Dahl <dahl.brendan@brendandahl.com>
//	  	http://www.brendandahl.com

package com.dahl.brendan.wordsearch.view.runnables;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.TextView;

/**
 * 
 * @author Brendan Dahl
 *
 * this class stores the action of updating a textview's colorList on the ui thread
 *
 */
public class ChangeTextViewColor implements Runnable {
	/**
	 * color to set the TextView to when it is part of a found word
	 */
	final private ColorStateList colorFound;
	/**
	 * color to set the TextView to when it has been picked
	 */
	final private ColorStateList colorPicked;
	/**
	 * color to set the TextView to when it is reverted to normal
	 */
	final private ColorStateList colorNormal;

	final private int colorCurrent;

	final private TextView view;
	final private ColorStateList color;


	public ChangeTextViewColor(ColorStateList colorFound,
			ColorStateList colorPicked, ColorStateList colorNormal,
			int colorCurrent, TextView view, ColorStateList color) {
		this.colorFound = colorFound;
		this.colorPicked = colorPicked;
		this.colorNormal = colorNormal;
		this.colorCurrent = colorCurrent;
		this.view = view;
		this.color = color;
	}


	public void run() {
		if (colorPicked.equals(color)) {
			if (view.getTag() == null) {
				view.setTag(view.getTextColors());
				view.setTextColor(colorPicked);
			}
		} else if (colorFound.equals(color)) {
			view.setTag(null);
			view.setTextColor(new ColorStateList(new int[][] {
					new int[] { android.R.attr.state_focused,
							android.R.attr.state_enabled }, new int[1] },
					new int[] {
							colorFound.getColorForState(new int[] {
									android.R.attr.state_focused,
									android.R.attr.state_enabled }, Color.rgb(
									255, 255, 255)), colorCurrent,

					}));
		} else {
			Object tag = view.getTag();
			if (tag instanceof ColorStateList) {
				view.setTextColor((ColorStateList) tag);
			} else {
				view.setTextColor(colorNormal);
			}
			view.setTag(null);
		}
	}

}
