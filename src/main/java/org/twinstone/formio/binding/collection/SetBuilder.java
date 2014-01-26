package org.twinstone.formio.binding.collection;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Builder of {@link Set}.
 * @author Radek Beran
 */
public class SetBuilder implements CollectionBuilder<Set<?>> {

	public static final SetBuilder HASH = new SetBuilder(ItemsOrder.HASH);
	public static final SetBuilder LINEAR = new SetBuilder(ItemsOrder.LINEAR);
	public static final SetBuilder SORTED = new SetBuilder(ItemsOrder.SORTED);
	
	protected SetBuilder(ItemsOrder itemsOrder) { this.itemsOrder = itemsOrder; }
	
	@Override
	public <I> Set<I> build(Class<I> itemClass, List<I> items) {
		Set<I> set = null;
		switch (itemsOrder) {
			case HASH:
				set = new HashSet<I>(items);
				break;
			case LINEAR:
				set = new LinkedHashSet<I>(items);
				break;
			case SORTED:
				set = new TreeSet<I>(items);
				break;
			default:
				throw new IllegalStateException("Unknown items order '" + itemsOrder + "'");
		}
		return set;
	}
	
	private ItemsOrder itemsOrder;
	
}
