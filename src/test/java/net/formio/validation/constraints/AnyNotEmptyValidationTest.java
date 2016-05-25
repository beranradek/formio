package net.formio.validation.constraints;

import org.junit.Test;

import junit.framework.Assert;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.data.TestForms;
import net.formio.domain.Contact;
import net.formio.inmemory.MapParams;

/**
 * Tests for {@link AnyNotEmpty}
 * @author Petr Kalivoda
 *
 */
public class AnyNotEmptyValidationTest {
	@Test
	public void testBothFilled() {
		FormMapping<Contact> contactForm = TestForms.CONTACT_FORM;
		FormData<Contact> contactData = newFormData(contactForm, "proper.email@example", "+420000000");
		Assert.assertTrue("validation with both filled should yield true", contactData.isValid());
		
	}
	
	@Test
	public void testEmailFilled() {
		FormMapping<Contact> contactForm = TestForms.CONTACT_FORM;
		FormData<Contact> contactData = newFormData(contactForm, "proper.email@example", null);
		Assert.assertTrue("validation with only first filled should yield true", contactData.isValid());
	}
	
	@Test
	public void testPhoneFilled() {
		FormMapping<Contact> contactForm = TestForms.CONTACT_FORM;
		FormData<Contact> contactData = newFormData(contactForm, null, "+420000000");
		Assert.assertTrue("validation with only second filled should yield true", contactData.isValid());
	}
	
	@Test
	public void testNoneFilled() {
		FormMapping<Contact> contactForm = TestForms.CONTACT_FORM;
		FormData<Contact> contactData = newFormData(contactForm, null, null);
		Assert.assertFalse("validation with none filled should yield false", contactData.isValid());
	}
	
	public static FormData<Contact> newFormData(FormMapping<Contact> mapping, String email, String phone) {
		return TestForms.CONTACT_FORM.bind(newContactFormParams(mapping.getConfig().getPathSeparator(), email, phone));
	}
	
	public static MapParams newContactFormParams(String pathSep, String email, String phone) {
		final MapParams reqParams = new MapParams();
		reqParams.put("contact" + pathSep + "email", email);
		reqParams.put("contact" + pathSep + "phone", phone);
		return reqParams;
	}
}
