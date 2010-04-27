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

package com.dahl.brendan.wordsearch.view.controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.dahl.brendan.wordsearch.model.Grid;
import com.dahl.brendan.wordsearch.model.Selection;
import com.dahl.brendan.wordsearch.util.ConversionUtil;
import com.dahl.brendan.wordsearch.view.WordSearchActivity;
import com.dahl.brendan.wordsearch.view.runnables.ChangeTextViewColor;

/**
 * 
 * @author Brendan Dahl
 * 
 * contains the game logic of the text view grid and interactions
 * most complex stuff happens here
 *
 */
public class TextViewGridController implements OnTouchListener, OnKeyListener, Runnable {
	final private static String LOG_TAG = "TextViewGridController";
	/**
	 * populated by the setupgridview in {@link WordSearchActivity.java}
	 */
	private TextView[][] gridView;
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
	/**
	 * overall word search game controller
	 */
	final private WordSearchActivityController control;
	/**
	 * holds the user's current selection progress
	 */
	final private Selection selection = new Selection();

	/**
	 * color to set the next word found as
	 */
	private int colorCurrent = Color.rgb(255, 0, 0);
	/**
	 * delta used to change colorCurrent between word findings
	 */
	private int colorChangeDelta = 250 / 20;

	/**
	 * width and height of one grid TextView
	 */
	private Point pointDemension;
	
	final private Thread eventThread = new Thread(this);

	final private BlockingQueue<MotionEvent> eventsQue = new LinkedBlockingQueue<MotionEvent>();

	protected TextViewGridController(WordSearchActivityController control,
			TextView[][] gridView,
			ColorStateList colorFound,
			ColorStateList colorPicked, ColorStateList colorNormal) {
		this.gridView = gridView;
		this.control = control;
		this.colorFound = colorFound;
		this.colorPicked = colorPicked;
		this.colorNormal = colorNormal;
		this.eventThread.start();
	}

	public void setGridView(TextView[][] gridViewNew) {
		this.gridView = gridViewNew;
	}

	public TextView[][] getGridView() {
		return gridView;
	}

	/**
	 * allow normal operation for arrows
	 * 
	 * enter key and space causes selection
	 */
	public boolean onKey(View view, int keyCode, KeyEvent event) {
//		Log.v(LOG_TAG, "onKey," + view.getId() + ":" + keyCode + ", event:"	+ event.toString());
		switch (keyCode) {
		case 19:
		case 20:
		case 21:
		case 22:
		case 82:
			return false;
		case 23:
		case 62:
		case 66:
			if (event.getAction() == KeyEvent.ACTION_UP
					&& view instanceof TextView) {
				this.selectionStartEnd((TextView) view);
			}
			return true;
		default:
			return true;
		}
	}

	public boolean onTouch(View view, MotionEvent event) {
		eventsQue.add(event);
		return true;
	}
	
	private boolean touchMode = true;

	protected void setTouchMode(boolean touchMode2) {
		if (touchMode2 != touchMode) {
			this.touchMode = touchMode2;
			this.selectionEnd();
		}
	}
	
	public void run() {
		MotionEvent event = null;
		boolean running = true;
		int maxEvents = 0;
		try {
			while (running) {
				event = eventsQue.take();
				if (event.getAction() != MotionEvent.ACTION_MOVE
						|| eventsQue.size() == 0) {
					// defines grid TextView's height and width for later calculations if it
					// isn't already saved
					if (pointDemension == null) {
						TextView t = gridView[0][0];
						Point p = new Point();
						p.x = t.getWidth();
						p.y = t.getHeight();
						pointDemension = p;
					}
					Point point = new Point();// row and column of the grid that was touched
					Point pointPadding = new Point();// the x and y offset within the
														// touched grid TextView where the
														// touch occurred
					point.y = Math.round(event.getY()) / pointDemension.y;
					pointPadding.y = Math.round(event.getY()) % pointDemension.y;
					if (pointPadding.y == 0 && point.y != 0) {
						point.y--;
					}
					point.x = Math.round(event.getX()) / pointDemension.x;
					pointPadding.x = Math.round(event.getX()) % pointDemension.x;
					if (pointPadding.x == 0 && point.x != 0) {
						point.x--;
					}
					if (touchMode) {
						handleTextViewEventTouch(point, pointPadding, event.getAction());
					} else {
						handleTextViewEventClick(point, pointPadding, event.getAction());
					}
				}
				if (eventsQue.size() > maxEvents) {
					maxEvents = eventsQue.size();
					Log.v(LOG_TAG, "maxEvents="+maxEvents);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void handleTextViewEventClick(Point point, Point pointPadding, int action) {
		if (Selection.isValidPoint(point, gridView.length)) {
			TextView views2 = null;
			views2 = gridView[point.y][point.x];
			switch (action) {
			case MotionEvent.ACTION_UP:
				selectionStartEnd(views2);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
				control.setLetter(null);
				break;
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				control.setLetter(views2.getText());
				break;
			default:
			}
		} else {
			Log.e(LOG_TAG, "INVALID ONTOUCH POINT, " + point);
			control.setLetter(null);
		}
	}

	private void handleTextViewEventTouch(Point point, Point pointPadding, int action) {
		// if touch happened outside the middle 1/2 of the width of the TextView
		// or outside the middle 3/5 of the height of the TextView
		// ignore touch unless it is a down or up event in which case force an
		// end to the selection
		if (pointPadding.x < pointDemension.x / 4
				|| pointPadding.x > pointDemension.x * 3 / 4
				|| pointPadding.y < pointDemension.y / 5
				|| pointPadding.y > pointDemension.y * 4 / 5) {
			switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
				selectionEnd();
				break;
			}
			return;
		}

		// if touch was within the gridView assign gridView else if up or down
		// event force a selection end
		TextView views2 = null;
		if (Selection.isValidPoint(point, gridView.length)) {
			views2 = gridView[point.y][point.x];
		} else {
			switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
				selectionEnd();
				break;
			}
			Log.e(LOG_TAG, "INVALID ONTOUCH POINT, " + point);
			return;
		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			selectionStartEnd(views2);
			break;
		case MotionEvent.ACTION_UP:
			if (selection.hasBegun()) {
				selectionAdd(views2);
				selectionEnd();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			selectionEnd();
			break;
		case MotionEvent.ACTION_OUTSIDE:
			selectionEnd();
			break;
		case MotionEvent.ACTION_MOVE:
			if (selection.hasBegun()) {
				if (selection.getEnd().getId() != views2.getId()) {
					selectionAdd(views2);
				}
			} else {
				selectionStart(views2);
			}
			break;
		default:
		}
	}

	/**
	 * adds a TextView to the current selection is it fits in the current selection
	 * 
	 * @param view TextView from grid to add to the current selection list
	 * @return false if selection failed
	 */
	private boolean selectionAdd(TextView view) {
		if (view == null) {
			return false;
		}
		if (!selection.hasBegun()) {// starting
			selection.setStart(view);
			setTextViewColor(view, colorPicked);
		} else if (!view.equals(selection.getStart())
				&& !view.equals(selection.getEnd())) {
			Point pointStart = ConversionUtil.convertIDToPoint(selection.getStart().getId(), control.getGridSize());
			Point pointEnd = ConversionUtil.convertIDToPoint(selection.getEnd().getId(), control.getGridSize());
			Point pointNew = ConversionUtil.convertIDToPoint(view.getId(), control.getGridSize());
			Point delta = Selection.getDeltas(pointStart, pointNew);
			if (delta == null) {
				return false;
			}
			if (!Selection.getDeltas(pointStart, pointEnd).equals(delta)) {
				this.selectionPaint(pointStart, pointEnd, null);
				pointEnd = pointStart;
			}
			int length = Selection.getLength(pointStart, pointNew);
			int lengthOld = Selection.getLength(pointStart, pointEnd);
			int lengthDiff = length - lengthOld;
			if (lengthDiff > 0) {// growing
				this.selectionPaint(pointEnd, pointNew, colorPicked);
			} else {// shrinking
				this.selectionPaint(pointNew, pointEnd, null);
				this.setTextViewColor(view, colorPicked);
			}
			selection.setEnd(view);
		}
		control.setLetter(selection.getEnd().getText());
		return true;
	}
	
	/**
	 * takes the current selection and checks to see if it is a real word in the grid
	 * also resets the selection
	 */
	private void selectionEnd() {
		if (this.selection.hasBegun()) {// if selection has been started
			Point pointStart = ConversionUtil.convertIDToPoint(this.selection.getStart().getId(), control.getGridSize());
			Point pointEnd = ConversionUtil.convertIDToPoint(this.selection.getEnd().getId(), control.getGridSize());
			String word = control.guessWord(pointStart, pointEnd);
			if (word == null) {// selection was not a word in the grid so revert the colors of all leters in the selection
				this.selectionPaint(pointStart, pointEnd, null);
			} else {// highlight found word in grid and pass found back to control main
				this.selectionPaint(pointStart, pointEnd, colorFound);
				colorCurrent = Color.rgb(
						Color.red(colorCurrent) - colorChangeDelta, Color
								.green(colorCurrent), Color.blue(colorCurrent)
								+ colorChangeDelta);
				control.foundWord(word);
			}
		}
		this.selection.reset();
		control.setLetter(null);
	}

	/**
	 * will iterate from pointStart to pointEnd and change each TextView's color to supplied color
	 * 
	 * @param pointStart first point in selection
	 * @param pointEnd last point in selection
	 * @param color null to revert a previouscolorPicked
	 * 				colorPicked to set the color to colorPicked
	 * 				colorFound to set the color to currentColor
	 */
	private void selectionPaint(Point pointStart, Point pointEnd, ColorStateList color) {
		Point delta = Selection.getDeltas(pointStart, pointEnd);
		if (delta == null) {
			return;
		}
		Point point = new Point();
		point.x = pointStart.x;
		point.y = pointStart.y;
		if (!Selection.isValidPoint(point, gridView.length)) {
			throw new NullPointerException("point: "+point.x+","+point.y+"; delta: "+delta.x+","+delta.y + "; length: "+gridView.length);
		}
		this.setTextViewColor(this.gridView[point.y][point.x], color);
		do {
			point.x += delta.x;
			point.y += delta.y;
			if (!point.equals(pointStart) && Selection.isValidPoint(point, gridView.length)) {
				this.setTextViewColor(this.gridView[point.y][point.x], color);
			}
		} while (!point.equals(pointEnd) && Selection.isValidPoint(point, gridView.length));
	}

	/**
	 * reset a selection if needed, then start a new one with selectionAdd
	 * 
	 * @param view TextView to pass into selectionAdd
	 */
	private void selectionStart(TextView view) {
		if (this.selection.hasBegun()) {
			Point pointStart = ConversionUtil.convertIDToPoint(this.selection.getStart().getId(), control.getGridSize());
			Point pointEnd = ConversionUtil.convertIDToPoint(this.selection.getEnd().getId(), control.getGridSize());
			this.selectionPaint(pointStart, pointEnd, null);
			this.selection.reset();
		}
		this.selectionAdd(view);
	}

	/**
	 * Will either start or end a selection based on whether the selection has already begun
	 * 
	 * @param view TextView to act upon
	 */
	private void selectionStartEnd(TextView view) {
		if (!this.selection.hasBegun()) {// start selection
			selectionStart(view);
		} else {// end selection
			selectionAdd(view);
			selectionEnd();
		}
	}

	/**
	 * Changes a single TextView's color; and saves the old color within the TextView for later reverting if passing colorPicked
	 * 
	 * @param view TextView in the grid to change the color of
	 * @param color change the view to this color; or pass in null to revert the color to its old color
	 */
	private void setTextViewColor(TextView view, ColorStateList color) {
		view.post(new ChangeTextViewColor(colorFound, colorPicked, colorNormal, colorCurrent, view, color));
	}

	/**
	 * 
	 * @param grid word search model of all the letters in a grid
	 */
	public void reset(Grid grid) {
		if (grid.getWordListLength() != 0) {
			colorChangeDelta = 250 / grid.getWordListLength();
		}
		colorCurrent = Color.rgb(255, 0, 0);
		eventsQue.clear();
		Point point = new Point();
		for (point.y = 0; point.y < gridView.length; point.y++) {
			for (point.x = 0; point.x < gridView[point.y].length; point.x++) {
				gridView[point.y][point.x].setText(grid.getLetterAt(point)
						.toString());
				gridView[point.y][point.x].setTag(null);
				gridView[point.y][point.x].setTextColor(colorNormal);
			}
		}
	}

	/**
	 * handles bundle serializing
	 */
	final private static String BUNDLE_COLOR_CURRENT = "ws_color_current";
	final private static String BUNDLE_COLOR_DELTA = "ws_color_delta";
	final private static String BUNDLE_COLOR_STATE_PREFIX = "ws_color_state_";
	final private static String BUNDLE_COLOR_STATE_SEP = ":";

	protected void fromBundle(Bundle bundle) {
		colorCurrent = bundle.getInt(BUNDLE_COLOR_CURRENT);
		colorChangeDelta = bundle.getInt(BUNDLE_COLOR_DELTA);
		for (int y = 0; y <gridView.length; y++) {
			for (int x = 0; x < gridView[y].length; x++) {
				ColorStateList csl = bundle.getParcelable(BUNDLE_COLOR_STATE_PREFIX+Integer.valueOf(y)+BUNDLE_COLOR_STATE_SEP+Integer.valueOf(x));
				gridView[y][x].setTextColor(csl);
			}
		}
	}
	
	protected Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putInt(BUNDLE_COLOR_CURRENT, colorCurrent);
		bundle.putInt(BUNDLE_COLOR_DELTA, colorChangeDelta);
		for (int y = 0; y <gridView.length; y++) {
			for (int x = 0; x < gridView[y].length; x++) {
				TextView view = gridView[y][x];
				if (view.getTextColors().equals(colorPicked)) {
					this.setTextViewColor(view, null);
				}
				bundle.putParcelable(BUNDLE_COLOR_STATE_PREFIX+Integer.valueOf(y)+BUNDLE_COLOR_STATE_SEP+Integer.valueOf(x), view.getTextColors());
			}
		}
		return bundle;
	}

	public void setPointDemension(Point pointDemension2) {
		this.pointDemension = pointDemension2;
	}
}
