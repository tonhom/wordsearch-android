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
import android.widget.TextView;

import com.dahl.brendan.wordsearch.model.ColorState;
import com.dahl.brendan.wordsearch.model.Theme;

/**
 * 
 * @author Brendan Dahl
 *
 * this class stores the action of updating a textview's colorList on the ui thread
 *
 */
public class ChangeTextViewColor implements Runnable {
	final private ColorState color;
	final private TextView view;
	final private ColorStateList picked;
	final private ColorStateList found;
	final private ColorStateList normal;


	public ChangeTextViewColor(Theme theme, ColorState color, TextView view) {
		this.picked = theme.picked;
		this.normal = theme.normal;
		this.found = theme.getCurrentFound();
		if (color == null) {
			this.color = ColorState.NORMAL;
		} else {
			this.color = color;
		}
		this.view = view;
	}


	public void run() {
		switch (color) {
		case SELECTED:
			if (view.getTag() == null) {
				view.setTag(view.getTextColors());
				view.setTextColor(picked);
			}
			break;
		case FOUND:
			view.setTag(null);
			view.setTextColor(found);
			break;
		case NORMAL:
		default:
			Object tag = view.getTag();
			if (tag instanceof ColorStateList) {
				view.setTextColor((ColorStateList) tag);
			} else {
				view.setTextColor(normal);
			}
			view.setTag(null);
			break;
		}
	}

}
