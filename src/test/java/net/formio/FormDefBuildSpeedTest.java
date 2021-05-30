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
package net.formio;

import net.formio.domain.Address;
import net.formio.domain.Registration;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Testing speed of building form definition.
 * @author Radek Beran
 */
public class FormDefBuildSpeedTest {
	private static final Logger LOG = Logger.getLogger(FormDefBuildSpeedTest.class.getName());
	
	@Test
	public void testFormDefWithDefaultConfigBuildSpeed() {
		final int repeatCnt = 10;
		final long startTime = System.nanoTime();
		for (int i = 0; i < repeatCnt; i++) {
			// In first cycle, init of Hibernate Validator and compilation of patterns take time
			final long startTimeInCycle = System.nanoTime();
			buildFormDefinitionWithDefaultConfig();
			final long millisInCycle = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeInCycle);
			//System.out.println("For definition built within " + millisInCycle + " ms");
		}
		final long millisAvg = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) / repeatCnt;
		// System.out.println("For definition built within avg " + millisAvg + " ms");
		assertTrue("Build of form definition with default config is too slow", millisAvg < 50);
	}

	private void buildFormDefinitionWithDefaultConfig() {
		Forms.automatic(Registration.class, "registration")
			.nested(Forms.automatic(Address.class, "contactAddress", Forms.factoryMethod(Address.class, "getInstance")).build())
			.build();
	}
}
