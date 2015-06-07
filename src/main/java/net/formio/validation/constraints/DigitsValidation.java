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
package net.formio.validation.constraints;

import java.math.BigDecimal;

/**
 * Validates the number of integer and fraction digits of given <code>Number</code>.
 * @author Radek Beran
 */
public class DigitsValidation {

	public static boolean isValid(Number num, int maxIntegerLength, int maxFractionLength) {
		if ( num == null ) {
			return false;
		}

		BigDecimal bigNum = null;
		if (num instanceof BigDecimal) {
			bigNum = (BigDecimal)num;
		} else {
			bigNum = new BigDecimal(num.toString()).stripTrailingZeros();
		}

		int integerPartLength = bigNum.precision() - bigNum.scale();
		int fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();

		return integerPartLength <= maxIntegerLength && fractionPartLength <= maxFractionLength;
	}
	
	private DigitsValidation() {
		throw new AssertionError("Not instantiable, use static members");
	}
}
