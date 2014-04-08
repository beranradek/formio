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
import java.util.Calendar;

import net.formio.validation.constraints.Email;
import net.formio.validation.constraints.NotEmpty;

public class NewCollegue implements Serializable {
	private static final long serialVersionUID = 7718460344527824543L;

	public NewCollegue() {
		Calendar cal = Calendar.getInstance();
		this.regDate = new RegDate(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
	}

	public interface New {
		/** marker interface */
	}

	@NotEmpty(groups = New.class)
	private String name;

	@NotEmpty(groups = New.class)
	@Email(groups = New.class)
	private String email;

	private RegDate regDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public RegDate getRegDate() {
		return regDate;
	}

	public void setRegDate(RegDate regDate) {
		this.regDate = regDate;
	}

	public Collegue toCollegue() {
		Collegue c = new Collegue();
		c.setName(this.name);
		c.setEmail(this.email);
		c.setRegDate(this.regDate != null ? new RegDate(this.regDate.getMonth(), this.regDate.getYear()) : null);
		return c;
	}

}
