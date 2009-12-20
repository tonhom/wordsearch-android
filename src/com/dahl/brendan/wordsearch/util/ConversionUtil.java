//    This file is part of WordSearch FREE.
//
//    WordSearch FREE is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    WordSearch FREE is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with WordSearch FREE.  If not, see <http://www.gnu.org/licenses/>.
//
//	  Copyright 2009 Brendan Dahl

package com.dahl.brendan.wordsearch.util;

import java.text.SimpleDateFormat;

import android.graphics.Point;

import com.dahl.brendan.wordsearch.view.controller.WordSearchActivityController;

/**
 * 
 * @author Brendan Dahl
 *
 * class used to convert stuff
 * 
 */
public class ConversionUtil {
	/**
	 * used to format the time a game took to play
	 */
	final public static SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
	/**
	 * convert a gridId to a x y point
	 * 
	 * @param id from grid TextView indicating which position in the grid it is
	 * @return point equivalent of id parameter
	 */
	public static Point convertIDToPoint(int id) {
		Point point = new Point();
		point.x = id % WordSearchActivityController.getGridSize();
		point.y = id / WordSearchActivityController.getGridSize();
		return point;
	}
	/**
 	 * convert a x y point to a gridId
 	 * 
	 * @param point with x and y set to convert into an Id
	 * @return Id equivalent of point parameter
	 */
	public static int convertPointToID(Point point) {
		return (point.x + point.y * WordSearchActivityController.getGridSize());
	}
}
