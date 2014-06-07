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
import net.formio.MapParams;
import net.formio.domain.AttendanceReason;

/**
 * Data "filled" into the forms, for test purposes.
 * @author Radek Beran
 */
public final class TestParams {

	public static MapParams newRegistrationParams() {
		// Preparing data (filled "by the user" into the form)
		final String sep = Forms.PATH_SEP;
		final MapParams reqParams = new MapParams();
		reqParams.put("registration" + sep + "email", "invalidemail.com");
		reqParams.put("registration" + sep + "attendanceReasons", 
			new String[] { AttendanceReason.COMPANY_INTEREST.name(), AttendanceReason.CERTIFICATION.name() });
		reqParams.put("registration" + sep + "collegues[0]" + sep + "name", "Michael");
		reqParams.put("registration" + sep + "collegues[1]" + sep + "name", "Natalie");
		reqParams.put("registration" + sep + "newCollegue" + sep + "regDate" + sep + "year", "2014");
		reqParams.put("registration" + sep + "newCollegue" + sep + "regDate" + sep + "month", "11");
		reqParams.put("registration" + sep + "newCollegue" + sep + "name", "Joshua");
		return reqParams;
	}
	
	public static MapParams newRegistrationCollegueParams() {
		// Preparing data (filled "by the user" into the form)
		final String sep = Forms.PATH_SEP;
		final MapParams reqParams = new MapParams();
		reqParams.put("registration" + sep + "newCollegue" + sep + "regDate" + sep + "year", "2014");
		reqParams.put("registration" + sep + "newCollegue" + sep + "regDate" + sep + "month", "11");
		reqParams.put("registration" + sep + "newCollegue" + sep + "name", "Joshua");
		return reqParams;
	}
	
	private TestParams() {
	}
}
