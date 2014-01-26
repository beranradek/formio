package org.twinstone.formio.binding.collection;

public class CollectionBuilderNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2323409180532486743L;
	private final CollectionSpec<?> collectionSpec;

	public CollectionBuilderNotFoundException(CollectionSpec<?> collectionSpec) {
		super("Cannot find collection builder for '" + collectionSpec + "'");
		this.collectionSpec = collectionSpec;
	}

	public CollectionSpec<?> getCollectionSpec() {
		return collectionSpec;
	}

}
