/*
 * Created on 25.5.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package org.twinstone.formio.text;

import java.io.Serializable;

/** Serializacni zastupce {@link ImmutableDate}.
 * 
 * @author Vlastimil Mencik
 */
public class ImmutableDateHandle implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long time;
	
	public ImmutableDateHandle(long time) {
		this.time = time;
	}
	
	private Object readResolve() {
		return new ImmutableDate(time);
	}
}
