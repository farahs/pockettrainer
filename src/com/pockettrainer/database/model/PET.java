package com.pockettrainer.database.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.pockettrainer.helper.FormatHelper;

@DatabaseTable(tableName = "PET")
public class PET implements Parcelable {

	@DatabaseField(generatedId = true, canBeNull = false)
	private int ID;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false)
	private int USER_ID;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String NAME;

	@DatabaseField(dataType = DataType.DATE, canBeNull = true)
	private Date BIRTH_DATE;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String TYPE;
	
	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String LEVEL;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String ENVIRONMENT;
	
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int CURRENT_EXPERIENCE;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String MOOD;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int HUNGER_INDICATOR;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int SLEEP_INDICATOR;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int HYGIENE_INDICATOR;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = true)
	private int RELATIONSHIP_INDICATOR;

	public PET() {

	}

	public PET(int iD, int uSER_ID, String nAME, Date bIRTH_DATE, String tYPE,
			String lEVEL, String eNVIRONMENT, int cURRENT_EXPERIENCE,
			String mOOD, int hUNGER_INDICATOR, int sLEEP_INDICATOR,
			int hYGIENE_INDICATOR, int rELATIONSHIP_INDICATOR) {
		super();
		ID = iD;
		USER_ID = uSER_ID;
		NAME = nAME;
		BIRTH_DATE = bIRTH_DATE;
		TYPE = tYPE;
		LEVEL = lEVEL;
		ENVIRONMENT = eNVIRONMENT;
		CURRENT_EXPERIENCE = cURRENT_EXPERIENCE;
		MOOD = mOOD;
		HUNGER_INDICATOR = hUNGER_INDICATOR;
		SLEEP_INDICATOR = sLEEP_INDICATOR;
		HYGIENE_INDICATOR = hYGIENE_INDICATOR;
		RELATIONSHIP_INDICATOR = rELATIONSHIP_INDICATOR;
	}

	public PET(Parcel in) {
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
		dest.writeString(NAME);
		dest.writeString(TYPE);
		
		if (BIRTH_DATE != null) {
			dest.writeString(FormatHelper.getFormattedStringDate(BIRTH_DATE,
					FormatHelper.SYSTEM_DATE_FORMAT));
		} else {
			dest.writeString("");
		}

		dest.writeString(LEVEL);
		dest.writeString(ENVIRONMENT);
		dest.writeInt(CURRENT_EXPERIENCE);
		dest.writeString(MOOD);
		dest.writeInt(HUNGER_INDICATOR);
		dest.writeInt(SLEEP_INDICATOR);
		dest.writeInt(HYGIENE_INDICATOR);
		dest.writeInt(RELATIONSHIP_INDICATOR);

	}

	private void readFromParcel(Parcel in) {
		ID = in.readInt();
		USER_ID = in.readInt();
		NAME = in.readString();
		TYPE = in.readString();
		
		String birthDate = in.readString();
		BIRTH_DATE = ((birthDate != null) && !birthDate.isEmpty()) ? FormatHelper
				.getFormattedDate(birthDate, FormatHelper.SYSTEM_DATE_FORMAT)
				: null;

		LEVEL = in.readString();
		ENVIRONMENT = in.readString();
		CURRENT_EXPERIENCE = in.readInt();
		MOOD = in.readString();
		HUNGER_INDICATOR = in.readInt();
		SLEEP_INDICATOR = in.readInt();
		HYGIENE_INDICATOR = in.readInt();
		RELATIONSHIP_INDICATOR = in.readInt();

	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public PET createFromParcel(Parcel in) {
			return new PET(in);
		}

		@Override
		public PET[] newArray(int size) {
			return new PET[size];
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

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public Date getBIRTH_DATE() {
		return BIRTH_DATE;
	}

	public void setBIRTH_DATE(Date bIRTH_DATE) {
		BIRTH_DATE = bIRTH_DATE;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getLEVEL() {
		return LEVEL;
	}

	public void setLEVEL(String lEVEL) {
		LEVEL = lEVEL;
	}

	public String getENVIRONMENT() {
		return ENVIRONMENT;
	}

	public void setENVIRONMENT(String eNVIRONMENT) {
		ENVIRONMENT = eNVIRONMENT;
	}

	public int getCURRENT_EXPERIENCE() {
		return CURRENT_EXPERIENCE;
	}

	public void setCURRENT_EXPERIENCE(int cURRENT_EXPERIENCE) {
		CURRENT_EXPERIENCE = cURRENT_EXPERIENCE;
	}

	public String getMOOD() {
		return MOOD;
	}

	public void setMOOD(String mOOD) {
		MOOD = mOOD;
	}

	public int getHUNGER_INDICATOR() {
		return HUNGER_INDICATOR;
	}

	public void setHUNGER_INDICATOR(int hUNGER_INDICATOR) {
		HUNGER_INDICATOR = hUNGER_INDICATOR;
	}

	public int getSLEEP_INDICATOR() {
		return SLEEP_INDICATOR;
	}

	public void setSLEEP_INDICATOR(int sLEEP_INDICATOR) {
		SLEEP_INDICATOR = sLEEP_INDICATOR;
	}

	public int getHYGIENE_INDICATOR() {
		return HYGIENE_INDICATOR;
	}

	public void setHYGIENE_INDICATOR(int hYGIENE_INDICATOR) {
		HYGIENE_INDICATOR = hYGIENE_INDICATOR;
	}

	public int getRELATIONSHIP_INDICATOR() {
		return RELATIONSHIP_INDICATOR;
	}

	public void setRELATIONSHIP_INDICATOR(int rELATIONSHIP_INDICATOR) {
		RELATIONSHIP_INDICATOR = rELATIONSHIP_INDICATOR;
	}

}
