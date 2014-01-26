package org.twinstone.formio.binding.collection;

import java.io.Serializable;

/**
 * Collection type specification.
 * 
 * @param <C> type of collection
 * @author Radek Beran
 */
public final class CollectionSpec<C> implements Serializable {
	private static final long serialVersionUID = 1703421631268127880L;
	private final Class<C> collClass;
	private final ItemsOrder preferedItemsOrder;

	public static <C> CollectionSpec<C> getInstance(Class<C> collClass,
			ItemsOrder preferedItemsOrder) {
		return new CollectionSpec<C>(collClass, preferedItemsOrder);
	}

	private CollectionSpec(Class<C> collClass, ItemsOrder preferedItemsOrder) {
		if (collClass == null) throw new IllegalArgumentException("collClass cannot be null");
		if (preferedItemsOrder == null) throw new IllegalArgumentException("preferedItemsOrder cannot be null");
		this.collClass = collClass;
		this.preferedItemsOrder = preferedItemsOrder;
	}

	public Class<C> getCollClass() {
		return collClass;
	}

	public ItemsOrder getPreferedItemsOrder() {
		return preferedItemsOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collClass == null) ? 0 : collClass.hashCode());
		result = prime
				* result
				+ ((preferedItemsOrder == null) ? 0 : preferedItemsOrder
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CollectionSpec))
			return false;
		CollectionSpec<?> other = (CollectionSpec<?>) obj;
		if (collClass == null) {
			if (other.collClass != null)
				return false;
		} else if (!collClass.equals(other.collClass))
			return false;
		if (preferedItemsOrder != other.preferedItemsOrder)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return collClass.getName() + " " + preferedItemsOrder;
	}

}
