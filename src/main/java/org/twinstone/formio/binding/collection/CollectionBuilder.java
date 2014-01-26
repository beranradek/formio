package org.twinstone.formio.binding.collection;

import java.util.List;

/**
 * Collection builder.
 * @author Radek Beran
 *
 * @param <C> type of collection
 */
public interface CollectionBuilder<C> {

	public <I> C build(Class<I> itemClass, List<I> items);
}
