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

package com.dahl.brendan.wordsearch.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.dahl.brendan.wordsearch.view.WordDictionaryProvider.Word;

public class WordSearchBackupAgent extends BackupAgent {
    static final int AGENT_VERSION = 1;
    static final String KEY_WORDS = "words";

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
            ParcelFileDescriptor newState) throws IOException {
    	Log.e("FUCK", "FUCK");
    	List<String> words = new LinkedList<String>();
		Cursor cursor = this.getContentResolver().query(Word.CONTENT_URI, new String[] { Word.WORD }, null, null, null);
		if (cursor.getCount() != 0) {
			do {
				words.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		cursor.close();
    	JSONArray jsonWords = new JSONArray(words);
    	String wordsStr = jsonWords.toString();
    	
        boolean doBackup = (oldState == null);
        if (!doBackup) {
            doBackup = compareStateFile(oldState, wordsStr.hashCode());
        }

        if (doBackup) {
            ByteArrayOutputStream bufStream = new ByteArrayOutputStream();

            // We use a DataOutputStream to write structured data into
            // the buffering stream
            DataOutputStream outWriter = new DataOutputStream(bufStream);
            outWriter.writeUTF(wordsStr);

            // Okay, we've flattened the data for transmission.  Pull it
            // out of the buffering stream object and send it off.
            byte[] buffer = bufStream.toByteArray();
            int len = buffer.length;
            data.writeEntityHeader(KEY_WORDS, len);
            data.writeEntityData(buffer, len);
        }

        // Finally, in all cases, we need to write the new state blob
        writeStateFile(newState, wordsStr.hashCode());
    }

    private boolean compareStateFile(ParcelFileDescriptor oldState, int hash) {
        try {
            FileInputStream instream = new FileInputStream(oldState.getFileDescriptor());
            DataInputStream in = new DataInputStream(instream);
            int stateVersion = in.readInt();
            if (stateVersion > AGENT_VERSION) {
                return true;
            }
            int hashOld = in.readInt();
            return hash != hashOld;
        } catch (Exception e) {
            return true;
        }
    }

    private void writeStateFile(ParcelFileDescriptor stateFile, int hash) throws IOException {
        FileOutputStream outstream = new FileOutputStream(stateFile.getFileDescriptor());
        DataOutputStream out = new DataOutputStream(outstream);

        out.writeInt(AGENT_VERSION);
        out.writeInt(hash);
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
            ParcelFileDescriptor newState) throws IOException {
    	try {
        	int hash = 0;
            while (data.readNextHeader()) {
                String key = data.getKey();
                int dataSize = data.getDataSize();

                if (KEY_WORDS.equals(key)) {
                    byte[] dataBuf = new byte[dataSize];
                    data.readEntityData(dataBuf, 0, dataSize);
                    ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
                    DataInputStream in = new DataInputStream(baStream);

                    String wordsStr = in.readUTF();
                    JSONArray jsonWords = new JSONArray(wordsStr);
                    hash = wordsStr.hashCode();

                    // wipe old data
            		Cursor cursor = this.getContentResolver().query(Word.CONTENT_URI, new String[] { Word._ID }, null, null, null);
            		if (cursor.getCount() != 0) {
            			do {
                			Uri wordUri = ContentUris.withAppendedId(Word.CONTENT_URI, cursor.getInt(0));
                			getContentResolver().delete(wordUri, null, null);
            			} while (cursor.moveToNext());
            		}
            		cursor.close();
                    // insert new data
                    for (int i = 0; i < jsonWords.length(); i++) {
           				ContentValues values = new ContentValues();
        				values.put(Word.WORD, jsonWords.getString(i));
        				getContentResolver().insert(Word.CONTENT_URI, values);
                    }
                } else {
                    data.skipEntityData();
                }
                writeStateFile(newState, hash);
            }
    	} catch (Exception e) {
//    		e.printStackTrace();
    		throw new IOException(e.getMessage());
    	}
    }
}
