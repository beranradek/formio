/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.domain.inputs;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import net.formio.domain.Address;
import net.formio.upload.UploadedFile;
import net.formio.validation.constraints.Email;
import net.formio.validation.constraints.NotEmpty;
import net.formio.validation.constraints.Phone;
import net.formio.validation.constraints.URL;

/**
 * Domain object of an user profile with fields for testing various types of form inputs.
 * @author Radek Beran
 */
public class Profile implements Serializable {
	private static final long serialVersionUID = -7676052816696638107L;

	// hidden
	private String profileId;
	
	// label
	private String header;
	
	// radio
	@NotNull
	private Salutation salutation;
	
	// text
	@NotEmpty
	private String firstName;
	
	@NotEmpty
	private String password;
	
	// select
	@NotNull
	private Country country;
	
	// Cascade validation to employers
	@Valid
	private List<Employer> employers;
	
	// date
	private Date birthDate;
	
	// multiple checkboxes
	private Set<Skill> skills;
	
	// multiple select
	private List<Function> functions;
	
	// file
	private UploadedFile certificate;
	
	// text area
	private String note;
	
	@Valid
	private Address contactAddress;
	
	private String favoriteColor;
	
	private Date registrationDate;
	
	@Email
	private String email;
	
	private String yearMonth;
	
	private String yearWeek;
	
	private Integer favoriteNumber;
	
	private Integer secondFavoriteNumber;
	
	private String search;
	
	@Phone
	private String phone;
	
	@URL
	private String homepage;
	
	// checkbox
	private boolean agreement;
	
	private String otherInfoUrl;
	
	private String submitValue;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	
	protected Address getContactAddress() {
		return contactAddress;
	}

	protected void setContactAddress(Address contactAddress) {
		this.contactAddress = contactAddress;
	}

	public boolean isAgreement() {
		return agreement;
	}

	public void setAgreement(boolean agreement) {
		this.agreement = agreement;
	}

	public Set<Skill> getSkills() {
		return skills;
	}

	public void setSkills(Set<Skill> skills) {
		this.skills = skills;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public List<Employer> getEmployers() {
		return employers;
	}

	public void setEmployers(List<Employer> employers) {
		this.employers = employers;
	}

	public List<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public UploadedFile getCertificate() {
		return certificate;
	}

	public void setCertificate(UploadedFile certificate) {
		this.certificate = certificate;
	}

	public String getFavoriteColor() {
		return favoriteColor;
	}

	public void setFavoriteColor(String favoriteColor) {
		this.favoriteColor = favoriteColor;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public String getYearWeek() {
		return yearWeek;
	}

	public void setYearWeek(String yearWeek) {
		this.yearWeek = yearWeek;
	}

	public Integer getFavoriteNumber() {
		return favoriteNumber;
	}

	public void setFavoriteNumber(Integer favoriteNumber) {
		this.favoriteNumber = favoriteNumber;
	}

	public Integer getSecondFavoriteNumber() {
		return secondFavoriteNumber;
	}

	public void setSecondFavoriteNumber(Integer secondFavoriteNumber) {
		this.secondFavoriteNumber = secondFavoriteNumber;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getOtherInfoUrl() {
		return otherInfoUrl;
	}

	public void setOtherInfoUrl(String otherInfoUrl) {
		this.otherInfoUrl = otherInfoUrl;
	}

	public String getSubmitValue() {
		return submitValue;
	}

	public void setSubmitValue(String submitValue) {
		this.submitValue = submitValue;
	}
}
