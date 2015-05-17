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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.formio.domain.Address;
import net.formio.domain.AttendanceReason;
import net.formio.domain.Car;
import net.formio.domain.CarDimensions;
import net.formio.domain.Collegue;
import net.formio.domain.Engine;
import net.formio.domain.Nation;
import net.formio.domain.NewCollegue;
import net.formio.domain.Person;
import net.formio.domain.Registration;
import net.formio.domain.inputs.Country;
import net.formio.domain.inputs.Employer;
import net.formio.domain.inputs.Function;
import net.formio.domain.inputs.Profile;
import net.formio.domain.inputs.Salutation;
import net.formio.domain.inputs.Skill;

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
	
	public static Car newCar() {
		Car car = new Car();
		car.setCarId(Long.valueOf(200));
		car.setBrand("Porsche 911 Turbo");
		car.setColor(25);
		car.setDescription("Great model");
		car.setMaxSpeed(315);
		car.setProductionYear(2013);
		Engine engine = new Engine();
		engine.setCylinderCount(6);
		engine.setVolume(3800);
		car.setEngine(engine);
		CarDimensions dimensions = new CarDimensions();
		dimensions.setLength(4506);
		dimensions.setWidth(1880);
		dimensions.setHeight(1295);
		car.setDimensions(dimensions);
		return car;
	}
	
	public static Profile newAllFields() {
		Profile profile = new Profile();
		profile.setAgreement(true);
		profile.setOtherInfoUrl("http://other-info/how-to/?param1=1&param2=info&param3=sd289dsfssf#anchor");
		Calendar birthCal = Calendar.getInstance();
		birthCal.set(1980, 11, 6);
		profile.setBirthDate(birthCal.getTime());
		profile.setCertificate(null);
		profile.setCountry(Country.GB);
		profile.setFirstName("Marry " + getScriptInjectionAttempt());
		List<Function> functions = new ArrayList<Function>();
		functions.add(new Function(Long.valueOf(200), "Student"));
		functions.add(new Function(Long.valueOf(400), "Manager"));
		profile.setFunctions(functions);
		profile.setHeader("Research");
		profile.setNote("These are the most important moments of my life... " + getScriptInjectionAttempt());
		profile.setPassword("");
		profile.setProfileId("ab565");
		profile.setSalutation(Salutation.MS);
		Set<Skill> skills = new LinkedHashSet<Skill>();
		skills.add(new Skill(Long.valueOf(17), "CRM"));
		profile.setSkills(skills);
		
		List<Employer> employers = new ArrayList<Employer>();
		Employer e1 = new Employer();
		e1.setName("IBM");
		e1.setFromYear(19999); // invalid year
		e1.setToYear(1999);
		employers.add(e1);
		
		Employer e2 = new Employer();
		e2.setName("Microsoft");
		e2.setFromYear(1999);
		e2.setToYear(2014);
		employers.add(e2);
		
		profile.setEmployers(employers);
		
		profile.setRegistrationDate(new Date());
		profile.setSubmitValue("submitted");
		profile.setEmail("invalid-email.com");
		profile.setPhone("invalid-/%@phone123");
		profile.setFavoriteColor("#0000ff");
		profile.setFavoriteNumber(Integer.valueOf(4));
		profile.setSecondFavoriteNumber(Integer.valueOf(7));
		profile.setSearch("Something to search for...");
		profile.setHomepage("invalid-url");
		profile.setYearWeek("2014-W15");
		profile.setYearMonth("2014-04");
		return profile;
	}	
	
	private static String getScriptInjectionAttempt() {
		return "\" onFocus=\"window.alert('Alert attempt');\" style=\"";
	}
	
	private TestData() {
	}
}
