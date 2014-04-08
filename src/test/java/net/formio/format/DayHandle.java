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
/*
 * Created on 25.5.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package net.formio.format;

import java.io.Serializable;

/** Serializacni zastupce {@link Day}.
 * 
 * @author Vlastimil Mencik
 */
public class DayHandle implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long time;
	
	public DayHandle(long time) {
		this.time = time;
	}
	
	private Object readResolve() {
		return new Day(time);
	}
}
