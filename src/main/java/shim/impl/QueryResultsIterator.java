/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package shim.impl;

import org.bcia.julongchain.protos.node.SmartContractShim;
import shim.ledger.IQueryResultsIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

class QueryResultsIterator<T> implements IQueryResultsIterator<T> {

	private final Handler handler;
	private final String channelId;
	private final String txId;
	private Iterator<SmartContractShim.QueryResultBytes> currentIterator;
	private SmartContractShim.QueryResponse currentQueryResponse;
	private Function<SmartContractShim.QueryResultBytes, T> mapper;

	public QueryResultsIterator(final Handler handler, final String channelId, final String txId, final SmartContractShim.QueryResponse queryResponse, Function<SmartContractShim.QueryResultBytes, T> mapper) {
		this.handler = handler;
		this.channelId = channelId;
		this.txId = txId;
		this.currentQueryResponse = queryResponse;
		this.currentIterator = currentQueryResponse.getResultsList().iterator();
		this.mapper = mapper;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return currentIterator.hasNext() || currentQueryResponse.getHasMore();
			}

			@Override
			public T next() {

				// return next fetched result, if any
				if(currentIterator.hasNext()) return mapper.apply(currentIterator.next());

				// throw exception if there are no more expected results
				if(!currentQueryResponse.getHasMore()) throw new NoSuchElementException();

				// get more results from peer
				currentQueryResponse = handler.queryStateNext(channelId, txId, currentQueryResponse.getId());
				currentIterator = currentQueryResponse.getResultsList().iterator();

				// return next fetched result
				return mapper.apply(currentIterator.next());

			}

		};
	}

	@Override
	public void close() throws Exception {
		this.handler.queryStateClose(channelId, txId, currentQueryResponse.getId());
		this.currentIterator = Collections.emptyIterator();
		this.currentQueryResponse = SmartContractShim.QueryResponse.newBuilder().setHasMore(false).build();
	}

}
