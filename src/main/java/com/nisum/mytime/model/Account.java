package com.nisum.mytime.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "Accounts")
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	private String accountId;
	private String accountName;
	private int accountProjectSequence;
    private String status;
	
}
