/*
 * Created on 25. 4. 2016
 *
 * Copyright (c) 2016 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package net.formio;

import static org.junit.Assert.*;

import org.junit.Test;

import net.formio.data.TestForms;
import net.formio.domain.animal.Mammal;
import net.formio.inmemory.MapParams;

/**
 * @author Radek Beran
 */
public class ClassHierarchyFormMappingTest {

	@Test
	public void testFormMappingClassWithParent() {
		FormData<Mammal> formData = TestForms.ANIMAL_FORM.bind(getAnimalParams(Config.DEFAULT_PATH_SEP));
		assertEquals("Fox", formData.getData().getName());
		assertEquals(4, formData.getData().getLegCount());
		assertEquals("FOXAB", formData.getData().getId());
	}
	
	private MapParams getAnimalParams(String pathSep) {
		MapParams reqParams = new MapParams();
		reqParams.put("animal" + pathSep + "name", "Fox");
		reqParams.put("animal" + pathSep + "legCount", "4");
		reqParams.put("animal" + pathSep + "id", "FOXAB");
		return reqParams;
	}
}
