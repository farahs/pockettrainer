package com.pockettrainer.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MONSTER")
public class MONSTER implements Parcelable {

	@DatabaseField(generatedId = true, canBeNull = false)
	private int ID;
	
	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String CODE;	
	
	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String NAME;	
	
	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String IMAGE;
	
	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false)
	private int BASE_EXPERIENCE;
	
	public MONSTER() {
		
	}
	
	public MONSTER(int iD, String cODE, String nAME,String iMAGE, int bASEEXP, int bASERANDOMRANGE, int tOPRANDOMRANGE) {
		super();
		ID = iD;
		CODE = cODE;
		NAME = nAME;
		IMAGE = iMAGE;
		BASE_EXPERIENCE = bASEEXP;
		
	}

	public MONSTER(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(ID);
		dest.writeString(CODE);
		dest.writeString(NAME);
		dest.writeString(IMAGE);
		dest.writeInt(BASE_EXPERIENCE);
	}
	
	private void readFromParcel(Parcel in) {
		ID = in.readInt();
		CODE = in.readString();
		NAME = in.readString();
		IMAGE = in.readString();
		BASE_EXPERIENCE = in.readInt();
	}
	
	public static final Parcelable.Creator	CREATOR	= new Parcelable.Creator() {

		@Override
		public MONSTER createFromParcel(Parcel in)
		{
			return new MONSTER(in);
		}

		@Override
		public MONSTER[] newArray(int size)
		{
			return new MONSTER[size];
		}
	};

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getCODE() {
		return CODE;
	}

	public void setCODE(String cODE) {
		CODE = cODE;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public String getIMAGE() {
		return IMAGE;
	}

	public void setIMAGE(String iMAGE) {
		IMAGE = iMAGE;
	}

	public int getBASE_EXPERIENCE() {
		return BASE_EXPERIENCE;
	}

	public void setBASE_EXPERIENCE(int bASE_EXPERIENCE) {
		BASE_EXPERIENCE = bASE_EXPERIENCE;
	}
	
}