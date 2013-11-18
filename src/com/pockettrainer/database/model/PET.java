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
	private String LEVEL;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String ENVIRONMENT;

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

	@DatabaseField(dataType = DataType.DATE, canBeNull = true)
	private Date LAST_UPDATE_HUNGER_INDICATOR;

	@DatabaseField(dataType = DataType.DATE, canBeNull = true)
	private Date LAST_UPDATE_SLEEP_INDICATOR;

	@DatabaseField(dataType = DataType.DATE, canBeNull = true)
	private Date LAST_UPDATE_HYGIENE_INDICATOR;

	@DatabaseField(dataType = DataType.DATE, canBeNull = true)
	private Date LAST_UPDATE_RELATIONSHIP_INDICATOR;

	public PET() {

	}

	public PET(int iD, int uSER_ID, String nAME, Date bIRTH_DATE, String lEVEL,
			String eNVIRONMENT, String mOOD, int hUNGER_INDICATOR,
			int sLEEP_INDICATOR, int hYGIENE_INDICATOR,
			int rELATIONSHIP_INDICATOR, Date lAST_UPDATE_HUNGER_INDICATOR,
			Date lAST_UPDATE_SLEEP_INDICATOR,
			Date lAST_UPDATE_HYGIENE_INDICATOR,
			Date lAST_UPDATE_RELATIONSHIP_INDICATOR) {
		super();
		ID = iD;
		USER_ID = uSER_ID;
		NAME = nAME;
		BIRTH_DATE = bIRTH_DATE;
		LEVEL = lEVEL;
		ENVIRONMENT = eNVIRONMENT;
		MOOD = mOOD;
		HUNGER_INDICATOR = hUNGER_INDICATOR;
		SLEEP_INDICATOR = sLEEP_INDICATOR;
		HYGIENE_INDICATOR = hYGIENE_INDICATOR;
		RELATIONSHIP_INDICATOR = rELATIONSHIP_INDICATOR;
		LAST_UPDATE_HUNGER_INDICATOR = lAST_UPDATE_HUNGER_INDICATOR;
		LAST_UPDATE_SLEEP_INDICATOR = lAST_UPDATE_SLEEP_INDICATOR;
		LAST_UPDATE_HYGIENE_INDICATOR = lAST_UPDATE_HYGIENE_INDICATOR;
		LAST_UPDATE_RELATIONSHIP_INDICATOR = lAST_UPDATE_RELATIONSHIP_INDICATOR;
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

		if (BIRTH_DATE != null) {
			dest.writeString(FormatHelper.getFormattedStringDate(BIRTH_DATE,
					FormatHelper.SYSTEM_DATE_FORMAT));
		} else {
			dest.writeString("");
		}

		dest.writeString(LEVEL);
		dest.writeString(ENVIRONMENT);
		dest.writeString(MOOD);
		dest.writeInt(HUNGER_INDICATOR);
		dest.writeInt(SLEEP_INDICATOR);
		dest.writeInt(HYGIENE_INDICATOR);
		dest.writeInt(RELATIONSHIP_INDICATOR);

		if (LAST_UPDATE_HUNGER_INDICATOR != null) {
			dest.writeString(FormatHelper.getFormattedStringDate(
					LAST_UPDATE_HUNGER_INDICATOR,
					FormatHelper.SYSTEM_DATE_FORMAT));
		} else {
			dest.writeString("");
		}

		if (LAST_UPDATE_SLEEP_INDICATOR != null) {
			dest.writeString(FormatHelper.getFormattedStringDate(
					LAST_UPDATE_SLEEP_INDICATOR,
					FormatHelper.SYSTEM_DATE_FORMAT));
		} else {
			dest.writeString("");
		}

		if (LAST_UPDATE_HYGIENE_INDICATOR != null) {
			dest.writeString(FormatHelper.getFormattedStringDate(
					LAST_UPDATE_HYGIENE_INDICATOR,
					FormatHelper.SYSTEM_DATE_FORMAT));
		} else {
			dest.writeString("");
		}

		if (LAST_UPDATE_RELATIONSHIP_INDICATOR != null) {
			dest.writeString(FormatHelper.getFormattedStringDate(
					LAST_UPDATE_RELATIONSHIP_INDICATOR,
					FormatHelper.SYSTEM_DATE_FORMAT));
		} else {
			dest.writeString("");
		}
	}

	private void readFromParcel(Parcel in) {
		ID = in.readInt();
		USER_ID = in.readInt();
		NAME = in.readString();
		String birthDate = in.readString();
		BIRTH_DATE = ((birthDate != null) && !birthDate.isEmpty()) ? FormatHelper
				.getFormattedDate(birthDate, FormatHelper.SYSTEM_DATE_FORMAT)
				: null;

		LEVEL = in.readString();
		ENVIRONMENT = in.readString();
		MOOD = in.readString();
		HUNGER_INDICATOR = in.readInt();
		SLEEP_INDICATOR = in.readInt();
		HYGIENE_INDICATOR = in.readInt();
		RELATIONSHIP_INDICATOR = in.readInt();

		String hungInd = in.readString();
		LAST_UPDATE_HUNGER_INDICATOR = ((hungInd != null) && !hungInd.isEmpty()) ? FormatHelper
				.getFormattedDate(hungInd, FormatHelper.SYSTEM_DATE_FORMAT)
				: null;

		String sleepInd = in.readString();
		LAST_UPDATE_SLEEP_INDICATOR = ((sleepInd != null) && !sleepInd
				.isEmpty()) ? FormatHelper.getFormattedDate(sleepInd,
				FormatHelper.SYSTEM_DATE_FORMAT) : null;

		String hygInd = in.readString();
		LAST_UPDATE_HYGIENE_INDICATOR = ((hygInd != null) && !hygInd.isEmpty()) ? FormatHelper
				.getFormattedDate(hygInd, FormatHelper.SYSTEM_DATE_FORMAT)
				: null;

		String relInd = in.readString();
		LAST_UPDATE_RELATIONSHIP_INDICATOR = ((relInd != null) && !relInd
				.isEmpty()) ? FormatHelper.getFormattedDate(relInd,
				FormatHelper.SYSTEM_DATE_FORMAT) : null;

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

	public Date getLAST_UPDATE_HUNGER_INDICATOR() {
		return LAST_UPDATE_HUNGER_INDICATOR;
	}

	public void setLAST_UPDATE_HUNGER_INDICATOR(
			Date lAST_UPDATE_HUNGER_INDICATOR) {
		LAST_UPDATE_HUNGER_INDICATOR = lAST_UPDATE_HUNGER_INDICATOR;
	}

	public Date getLAST_UPDATE_SLEEP_INDICATOR() {
		return LAST_UPDATE_SLEEP_INDICATOR;
	}

	public void setLAST_UPDATE_SLEEP_INDICATOR(Date lAST_UPDATE_SLEEP_INDICATOR) {
		LAST_UPDATE_SLEEP_INDICATOR = lAST_UPDATE_SLEEP_INDICATOR;
	}

	public Date getLAST_UPDATE_HYGIENE_INDICATOR() {
		return LAST_UPDATE_HYGIENE_INDICATOR;
	}

	public void setLAST_UPDATE_HYGIENE_INDICATOR(
			Date lAST_UPDATE_HYGIENE_INDICATOR) {
		LAST_UPDATE_HYGIENE_INDICATOR = lAST_UPDATE_HYGIENE_INDICATOR;
	}

	public Date getLAST_UPDATE_RELATIONSHIP_INDICATOR() {
		return LAST_UPDATE_RELATIONSHIP_INDICATOR;
	}

	public void setLAST_UPDATE_RELATIONSHIP_INDICATOR(
			Date lAST_UPDATE_RELATIONSHIP_INDICATOR) {
		LAST_UPDATE_RELATIONSHIP_INDICATOR = lAST_UPDATE_RELATIONSHIP_INDICATOR;
	}

}
