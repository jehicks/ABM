package org.sandag.abm.active;

import java.util.Collection;

public interface ShortestPathResults<N extends Node> extends Iterable<NodePair<N>> {
	int size();
	ShortestPathResult<N> getShortestPathResult(NodePair<N> od);
	Collection<ShortestPathResult<N>> getResults();
}