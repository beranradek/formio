/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import net.formio.binding.ArgumentName;
import net.formio.validation.SeverityPayload;

public class Person implements Serializable {
	private static final long serialVersionUID = -1568960763694971728L;
	private long personId;
	
	@NotNull
	private final String firstName;

	@NotNull
	@Size(min = 2)
	private final String lastName;

	@Min(value=8000, payload=SeverityPayload.Warning.class)
	private int salary;

	@Pattern(regexp="\\d{9}")
	private String phone;
	private boolean male;
	private Date birthDate;
	private Nation nation;

	public Person(
		@ArgumentName("firstName") String firstName,
		@ArgumentName("lastName") String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getSalary() {
		return salary;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public Date getBirthDate() {
		return birthDate != null ? new Date(birthDate.getTime()) : null;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate != null ? new Date(birthDate.getTime()) : null;
	}

	public Nation getNation() {
		return nation;
	}

	public void setNation(Nation nation) {
		this.nation = nation;
	}

}
