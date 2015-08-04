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

import net.formio.Forms;
import net.formio.domain.AttendanceReason;
import net.formio.inmemory.MapParams;

/**
 * Data "filled" into the forms, for test purposes.
 * @author Radek Beran
 */
public final class TestParams {

	public static MapParams newRegistrationParams(String pathSep) {
		// Preparing data (filled "by the user" into the form)
		final MapParams reqParams = new MapParams();
		reqParams.put("registration" + pathSep + "email", "invalidemail.com");
		reqParams.put("registration" + pathSep + "attendanceReasons", 
			new String[] { AttendanceReason.COMPANY_INTEREST.name(), AttendanceReason.CERTIFICATION.name() });
		reqParams.put("registration" + pathSep + "collegues[0]" + pathSep + "name", "Michael");
		reqParams.put("registration" + pathSep + "collegues[1]" + pathSep + "name", "Natalie");
		reqParams.put("registration" + pathSep + "newCollegue" + pathSep + "regDate" + pathSep + "year", "2014");
		reqParams.put("registration" + pathSep + "newCollegue" + pathSep + "regDate" + pathSep + "month", "11");
		reqParams.put("registration" + pathSep + "newCollegue" + pathSep + "name", "Joshua");
		return reqParams;
	}
	
	public static MapParams newRegistrationCollegueParams(String pathSep) {
		// Preparing data (filled "by the user" into the form)
		final MapParams reqParams = new MapParams();
		reqParams.put("registration" + pathSep + "newCollegue" + pathSep + "regDate" + pathSep + "year", "2014");
		reqParams.put("registration" + pathSep + "newCollegue" + pathSep + "regDate" + pathSep + "month", "11");
		reqParams.put("registration" + pathSep + "newCollegue" + pathSep + "name", "Joshua");
		return reqParams;
	}
	
	private TestParams() {
	}
}
