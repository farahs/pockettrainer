package com.pockettrainer.database.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;

@DatabaseTable(tableName = "USER")
public class USER implements Parcelable {

	@DatabaseField(generatedId = true, canBeNull = false)
	private int ID;
	
	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String TRAINING_HISTORY;	
	
	
	public USER() {

	}
	
	public USER(int iD, String tRAINING_HISTORY) {
		super();
		ID = iD;
		TRAINING_HISTORY = tRAINING_HISTORY;
	}
	
	public USER(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(ID);
		dest.writeString(TRAINING_HISTORY);
	}
	
	private void readFromParcel(Parcel in) {
		ID = in.readInt();
		TRAINING_HISTORY = in.readString();
	}
	
	public static final Parcelable.Creator	CREATOR	= new Parcelable.Creator() {

		@Override
		public USER createFromParcel(Parcel in)
		{
			return new USER(in);
		}

		@Override
		public USER[] newArray(int size)
		{
			return new USER[size];
		}
	};

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getTRAINING_HISTORY() {
		return TRAINING_HISTORY;
	}

	public void setTRAINING_HISTORY(String tRAINING_HISTORY) {
		TRAINING_HISTORY = tRAINING_HISTORY;
	}
	
}
