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
package net.formio.choice;

import java.io.Serializable;

/**
 * Item of a choice with an identifier and title.
 * @author Radek Beran
 */
public final class ChoiceItem implements Serializable, Identified<String>, Titled {
	private static final long serialVersionUID = -6192114289298402598L;

	private final String id;
	
	private final String title;
	
	public static ChoiceItem valueOf(String id, String title) {
		return new ChoiceItem(id, title);
	}
	
	private ChoiceItem(String id, String title) {
		this.id = id;
		this.title = title;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ChoiceItem))
			return false;
		ChoiceItem other = (ChoiceItem)obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return id + "=" + title;
	}
}
