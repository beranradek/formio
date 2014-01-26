package org.twinstone.formio.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.twinstone.formio.text.BasicFormatters;

public class BindingTest {

	@Test
	public void testBindToNewEmptyPerson() {
		Map<String, BoundValuesInfo> values = new HashMap<String, BoundValuesInfo>();
		values.put("personId", BoundValuesInfo.getInstance(new Object[] { Long.valueOf(1) }));
		values.put("firstName", BoundValuesInfo.getInstance(new Object[] { "Michael" }));
		values.put("lastName", BoundValuesInfo.getInstance(new Object[] { "Cane" }));
		values.put("salary", BoundValuesInfo.getInstance(new Object[] { Integer.valueOf(40000) }));
		values.put("male", BoundValuesInfo.getInstance(new Object[] { Boolean.TRUE }));
		values.put("nation", BoundValuesInfo.getInstance(new Object[] { Nation.JEDI_KNIGHT }));
		
		FilledData<Person> filledPerson = createBinding().bindToNewInstance(Person.class, values);
		assertNotNull(filledPerson);
		assertNotNull(filledPerson.getData());
	}
	
	@Test
	public void testBindToNewPerson() throws ParseException {
		Map<String, BoundValuesInfo> values = new HashMap<String, BoundValuesInfo>();
		String firstName = "Michael";
		String lastName = "Cane";
		String birthDate = "28.1.2013 23:51:30";
		String birthDatePattern = "dd.MM.yyyy HH:mm:ss";
		String male = "true";
		String salary = "40000";
		String personId = "123";
		String nation = "CZECH";
		
		values.put("firstName", BoundValuesInfo.getInstance(new Object[] {firstName}));
		values.put("lastName", BoundValuesInfo.getInstance(new Object[] {lastName}));
		values.put("birthDate", BoundValuesInfo.getInstance(new Object[] {birthDate}, birthDatePattern));
		values.put("male", BoundValuesInfo.getInstance(new Object[] {male}));
		values.put("salary", BoundValuesInfo.getInstance(new Object[] {salary}));
		values.put("personId", BoundValuesInfo.getInstance(new Object[] {personId}));
		values.put("nation", BoundValuesInfo.getInstance(new Object[] {nation}));
		
		FilledData<Person> filledPerson = createBinding().bindToNewInstance(Person.class, values);
		assertTrue(filledPerson.isSuccessfullyBound());
		Person person = filledPerson.getData();
		assertNotNull(person);
		assertEquals(firstName, person.getFirstName());
		assertEquals(lastName, person.getLastName());
		assertEquals(new SimpleDateFormat(birthDatePattern).parse(birthDate), person.getBirthDate());
		assertEquals(Boolean.TRUE, Boolean.valueOf(person.isMale()));
		assertEquals(Integer.valueOf(salary), Integer.valueOf(person.getSalary()));
		assertEquals(Long.valueOf(personId), Long.valueOf(person.getPersonId()));
		assertEquals(Nation.valueOf(nation), person.getNation());
	}
	
	private DefaultBinder createBinding() { return new DefaultBinder(new BasicFormatters()); }

}
