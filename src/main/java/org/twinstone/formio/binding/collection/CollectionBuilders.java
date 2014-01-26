package org.twinstone.formio.binding.collection;

import java.util.List;

/**
 * Factory of collection builders.
 * @author Radek Beran
 */
public interface CollectionBuilders {

	<C, I> C buildCollection(CollectionSpec<C> collSpec, Class<I> itemClass, List<I> items);
	
	boolean canHandle(CollectionSpec<?> collSpec);
}
