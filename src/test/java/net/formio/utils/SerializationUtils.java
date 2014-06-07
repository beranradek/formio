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
package net.formio.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Utility methods for Java serialization and deserialization.
 * @author Radek Beran
 */
public final class SerializationUtils {

	/**
	 * Serializes the object into its serialized form.
	 * 
	 * @param obj
	 *            serializable object
	 * @return serialized form of object
	 */
	public static byte[] serialize(Serializable obj) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(obj);
			return os.toByteArray();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Returns the object with the specified serialized form.
	 * 
	 * @param sf
	 *            serialized form of object
	 * @return deserialized serializable object
	 */
	public static Serializable deserialize(byte[] sf) {
		try {
			InputStream is = new ByteArrayInputStream(sf);
			ObjectInputStream ois = new ObjectInputStream(is);
			return (Serializable) ois.readObject();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private SerializationUtils() {
		throw new AssertionError("Not instantiable");
	}

}
