package com.pockettrainer.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "TRAINING")
public class TRAINING implements Parcelable {

	@DatabaseField(generatedId = true, canBeNull = false)
	private int ID;
	
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false)
	private int USER_ID;
	
	@DatabaseField(dataType = DataType.FLOAT, canBeNull = true)
	private float DURATION;	
	
	@DatabaseField(dataType = DataType.FLOAT, canBeNull = true)
	private float DISTANCE;
	
	@DatabaseField(dataType = DataType.FLOAT, canBeNull = true)
	private float SPEED;
	
	@DatabaseField(dataType = DataType.FLOAT, canBeNull = true)
	private float BURNED_CALORIES;
	
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int STEPS;
	
	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String MONSTER_DEFEATED;	
	
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int EXPERIENCE;
	
	public TRAINING() {

	}

	public TRAINING(int iD, int uSER_ID, float dURATION, float dISTANCE,
			float sPEED, float bURNED_CALORIES, int sTEPS,
			String mONSTER_DEFEATED, int eXPERIENCE) {
		super();
		ID = iD;
		USER_ID = uSER_ID;
		DURATION = dURATION;
		DISTANCE = dISTANCE;
		SPEED = sPEED;
		BURNED_CALORIES = bURNED_CALORIES;
		STEPS = sTEPS;
		MONSTER_DEFEATED = mONSTER_DEFEATED;
		EXPERIENCE = eXPERIENCE;
	}

	public TRAINING(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(ID);
		dest.writeInt(USER_ID);
		dest.writeFloat(DURATION);
		dest.writeFloat(DISTANCE);
		dest.writeFloat(SPEED);
		dest.writeFloat(BURNED_CALORIES);
		dest.writeInt(STEPS);
		dest.writeString(MONSTER_DEFEATED);
		dest.writeInt(EXPERIENCE);
	}
	
	private void readFromParcel(Parcel in) {
		ID = in.readInt();
		USER_ID = in.readInt();
		DURATION = in.readFloat();
		DISTANCE = in.readFloat();
		SPEED = in.readFloat();
		BURNED_CALORIES = in.readFloat();
		STEPS = in.readInt();
		MONSTER_DEFEATED = in.readString();
		EXPERIENCE = in.readInt();
	}
	
	public static final Parcelable.Creator	CREATOR	= new Parcelable.Creator() {

		@Override
		public TRAINING createFromParcel(Parcel in)
		{
			return new TRAINING(in);
		}

		@Override
		public TRAINING[] newArray(int size)
		{
			return new TRAINING[size];
		}
	};

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(int uSER_ID) {
		USER_ID = uSER_ID;
	}

	public float getDURATION() {
		return DURATION;
	}

	public void setDURATION(float dURATION) {
		DURATION = dURATION;
	}

	public float getDISTANCE() {
		return DISTANCE;
	}

	public void setDISTANCE(float dISTANCE) {
		DISTANCE = dISTANCE;
	}

	public float getSPEED() {
		return SPEED;
	}

	public void setSPEED(float sPEED) {
		SPEED = sPEED;
	}

	public float getBURNED_CALORIES() {
		return BURNED_CALORIES;
	}

	public void setBURNED_CALORIES(float bURNED_CALORIES) {
		BURNED_CALORIES = bURNED_CALORIES;
	}

	public int getSTEPS() {
		return STEPS;
	}

	public void setSTEPS(int sTEPS) {
		STEPS = sTEPS;
	}

	public String getMONSTER_DEFEATED() {
		return MONSTER_DEFEATED;
	}

	public void setMONSTER_DEFEATED(String mONSTER_DEFEATED) {
		MONSTER_DEFEATED = mONSTER_DEFEATED;
	}

	public int getEXPERIENCE() {
		return EXPERIENCE;
	}

	public void setEXPERIENCE(int eXPERIENCE) {
		EXPERIENCE = eXPERIENCE;
	}
	
}