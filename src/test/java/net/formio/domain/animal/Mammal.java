/*
 * Created on 25. 4. 2016
 *
 * Copyright (c) 2016 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package net.formio.domain.animal;

/**
 * @author Radek Beran
 */
public class Mammal extends Animal {

	private String name;
	
	private int legCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLegCount() {
		return legCount;
	}

	public void setLegCount(int legCount) {
		this.legCount = legCount;
	}
	
	
}
