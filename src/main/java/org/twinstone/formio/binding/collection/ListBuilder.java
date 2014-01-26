package org.twinstone.formio.binding.collection;

import java.util.ArrayList;
import java.util.List;


/**
 * Builder of {@link List}.
 * @author Radek Beran
 */
public class ListBuilder implements CollectionBuilder<List<?>> {

	public static final ListBuilder LINEAR = new ListBuilder();
	
	protected ListBuilder() {}
	
	@Override
	public <I> List<I> build(Class<I> itemClass, List<I> items) {
		return new ArrayList<I>(items);
	}
	
}
