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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.formio.binding.ArgumentName;
import net.formio.domain.validation.RegistrationValid;
import net.formio.upload.UploadedFile;
import net.formio.validation.constraints.Email;


@RegistrationValid
public class Registration implements Serializable {
	private static final long serialVersionUID = -1568960763694971728L;

	private final Set<AttendanceReason> attendanceReasons;

	private UploadedFile cv;

	private List<UploadedFile> certificates;

	private int[] interests;

	@Email
	private String email;

	private Address contactAddress;

	private List<Collegue> collegues;

	private NewCollegue newCollegue;

	public Registration(
			@ArgumentName("attendanceReasons") Set<AttendanceReason> attendanceReasons) {
		if (attendanceReasons == null)
			throw new IllegalArgumentException(
					"attendanceReasons cannot be null, only empty");
		this.attendanceReasons = attendanceReasons;
	}

	public UploadedFile getCv() {
		return cv;
	}

	public void setCv(UploadedFile cv) {
		this.cv = cv;
	}

	public List<UploadedFile> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<UploadedFile> certificates) {
		this.certificates = certificates;
	}

	public int[] getInterests() {
		return interests != null ? interests.clone() : null;
	}

	public void setInterests(int[] interests) {
		this.interests = interests != null ? interests.clone() : null;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Address getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(Address contactAddress) {
		if (contactAddress == null) {
			this.contactAddress = Address.getInstance(null, null, null);
		} else {
			this.contactAddress = contactAddress;
		}
	}

	public static List<Interest> allInterests() {
		List<Interest> list = new ArrayList<Interest>();
		list.add(PARALLEL);
		list.add(WEB_FRAMEWORKS);
		list.add(AI);
		list.add(DATA_STRUCTURES);
		return list;
	}

	public Set<AttendanceReason> getAttendanceReasons() {
		return attendanceReasons;
	}

	public List<Collegue> getCollegues() {
		return collegues;
	}

	public void setCollegues(List<Collegue> collegues) {
		this.collegues = collegues;
	}

	public NewCollegue getNewCollegue() {
		return newCollegue;
	}

	public void setNewCollegue(NewCollegue newCollegue) {
		this.newCollegue = newCollegue;
	}

	public static final Interest PARALLEL = new Interest(1,
			"Parallel Computation");
	public static final Interest WEB_FRAMEWORKS = new Interest(2,
			"Web frameworks");
	public static final Interest AI = new Interest(3, "AI");
	public static final Interest DATA_STRUCTURES = new Interest(4, "Data Structures");

}
