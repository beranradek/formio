package org.twinstone.formio.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.twinstone.formio.binding.PrimitiveType;

public class PrimitiveTypeTest {

	@Test
	public void testIsPrimitiveType() {
		assertEquals(Boolean.TRUE, Boolean.valueOf(PrimitiveType.isPrimitiveType(int.class)));
		assertEquals(Boolean.FALSE, Boolean.valueOf(PrimitiveType.isPrimitiveType(Integer.class)));
	}

	@Test
	public void testByPrimitiveClass() {
		PrimitiveType ptype = PrimitiveType.byPrimitiveClass(int.class);
		assertNotNull("Primitive type null", ptype);
		assertEquals(int.class, ptype.getPrimitiveClass());
		assertEquals(Integer.class, ptype.getWrapperClass());
		assertEquals(Integer.valueOf(0), ptype.getInitialValue());
	}
	
	@Test
	public void testByClasses() {
		assertEquals(null, PrimitiveType.byClasses(int.class, Boolean.class));
		assertNotNull("Returned type is null", PrimitiveType.byClasses(int.class, Integer.class));
	}

}
