package org.twinstone.formio.binding.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builds various common collections and arrays from given specification 
 * (collection class and ordering of items) and given items.
 * Different subclasses with different registered collection builders can be
 * created: Method {@link #registerBuilders()} can be overridden.
 * 
 * @author Radek Beran
 */
public class BasicCollectionBuilders implements CollectionBuilders {

	/**
	 * Intentionally left default constructor, that can be used with dependency
	 * injection. Subclasses can define and inject their own mechanisms to
	 * build collections.
	 */
	public BasicCollectionBuilders() {
		this.builders = registerBuilders();
	}
	
	@Override
	public <C, I> C buildCollection(CollectionSpec<C> collSpec, Class<I> itemClass, List<I> items) {
		CollectionSpec<C> cSpec = null;
		if (collSpec.getCollClass().isArray()) {
			cSpec = (CollectionSpec<C>)CollectionSpec.getInstance(Array.class, collSpec.getPreferedItemsOrder());
		} else {
			cSpec = collSpec;
		}
		CollectionBuilder<C> collBuilder = (CollectionBuilder<C>)builders.get(cSpec);
		if (collBuilder == null)
			throw new CollectionBuilderNotFoundException(cSpec);
		return collBuilder.build(itemClass, items);
	}
	
	@Override
	public boolean canHandle(CollectionSpec<?> collSpec) {
		return collSpec.getCollClass().isArray() || builders.get(collSpec) != null;
	}
	
	protected Map<CollectionSpec<?>, CollectionBuilder<?>> registerBuilders() {
		if (buildersCache.isEmpty()) {
			buildersCache.put(CollectionSpec.getInstance(List.class, ItemsOrder.LINEAR), ListBuilder.LINEAR);
			buildersCache.put(CollectionSpec.getInstance(ArrayList.class, ItemsOrder.LINEAR), ListBuilder.LINEAR);
			buildersCache.put(CollectionSpec.getInstance(Set.class, ItemsOrder.LINEAR), SetBuilder.LINEAR);
			buildersCache.put(CollectionSpec.getInstance(Set.class, ItemsOrder.HASH), SetBuilder.HASH);
			buildersCache.put(CollectionSpec.getInstance(Set.class, ItemsOrder.SORTED), SetBuilder.SORTED);
			buildersCache.put(CollectionSpec.getInstance(LinkedHashSet.class, ItemsOrder.LINEAR), SetBuilder.LINEAR);
			buildersCache.put(CollectionSpec.getInstance(HashSet.class, ItemsOrder.HASH), SetBuilder.HASH);
			buildersCache.put(CollectionSpec.getInstance(TreeSet.class, ItemsOrder.SORTED), SetBuilder.SORTED);
			buildersCache.put(CollectionSpec.getInstance(Collection.class, ItemsOrder.LINEAR), ListBuilder.LINEAR);
			// Note: Array is auxiliary class for arrays, expected by buildCollection
			buildersCache.put(CollectionSpec.getInstance(Array.class, ItemsOrder.LINEAR), ArrayBuilder.LINEAR);
			buildersCache.put(CollectionSpec.getInstance(Array.class, ItemsOrder.SORTED), ArrayBuilder.SORTED);
		}
		return buildersCache;
	}
	
	private final Map<CollectionSpec<?>, CollectionBuilder<?>> builders;
	private static final Map<CollectionSpec<?>, CollectionBuilder<?>> buildersCache = new ConcurrentHashMap<CollectionSpec<?>, CollectionBuilder<?>>();
	
}
