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
package net.formio.binding;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Pojmenovani argumentu u builder konstruktoru
 * (nutne u viceparametroveho konstruktoru pro prirazeni fieldu k argumentum, 
 * u jednoparametroveho konstruktoru lze vynechat).
 */
@Target({PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface ArgumentName {
	/** deklarovane jmeno argumentu (musi byt unikatni a musi odpovidat jednomu z fieldu) */
	String value();
}
