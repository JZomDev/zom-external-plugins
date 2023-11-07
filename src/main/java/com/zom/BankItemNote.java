package com.zom;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankItemNote
{
	@SerializedName("itemid")
	private int itemid;

	@SerializedName("note")
	private String note;

}
