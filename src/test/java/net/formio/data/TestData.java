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
package net.formio.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.formio.domain.Address;
import net.formio.domain.AttendanceReason;
import net.formio.domain.Collegue;
import net.formio.domain.Nation;
import net.formio.domain.NewCollegue;
import net.formio.domain.Person;
import net.formio.domain.Registration;

/**
 * Common data for tests.
 * @author Radek Beran
 */
public final class TestData {

	public static Person newPerson() {
		Person person = new Person("Jan", "Novak");
		try {
			person.setBirthDate(new SimpleDateFormat("d.M.yyyy HH:mm").parse("20.2.1982 11:20"));
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		person.setMale(true);
		person.setNation(Nation.CZECH);
		person.setPersonId(1L);
		return person;
	}
	
	public static Registration newRegistration() {
		final Set<AttendanceReason> attendanceReasons = new HashSet<AttendanceReason>();
		attendanceReasons.add(AttendanceReason.COMPANY_INTEREST);
		Registration reg = new Registration(attendanceReasons);
		reg.setInterests(new int[] {Registration.DATA_STRUCTURES.getInterestId(), Registration.WEB_FRAMEWORKS.getInterestId()});
		reg.setContactAddress(Address.getInstance("Milady Horakove 22", "Praha", "16000"));
		
		List<Collegue> collegues = new ArrayList<Collegue>();
		final Collegue michael = new Collegue();
		michael.setName("Michael");
		michael.setEmail("michael@email.com");
		collegues.add(michael);
		
		Collegue jane = new Collegue();
		jane.setName("Jane");
		jane.setEmail("jane@email.com");
		collegues.add(jane);
		
		reg.setCollegues(collegues);
		
		reg.setNewCollegue(new NewCollegue());
		return reg;
	}
	
	private TestData() {
	}
}
