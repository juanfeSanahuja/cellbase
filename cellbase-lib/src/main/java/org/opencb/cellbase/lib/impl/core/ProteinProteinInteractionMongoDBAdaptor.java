/*
 * Copyright 2015-2020 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.cellbase.lib.impl.core;

import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.opencb.biodata.models.protein.Interaction;
import org.opencb.cellbase.core.api.core.ProteinProteinInteractionDBAdaptor;
import org.opencb.cellbase.core.api.queries.AbstractQuery;
import org.opencb.cellbase.core.result.CellBaseDataResult;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.commons.datastore.mongodb.MongoDataStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by imedina on 13/12/15.
 */
public class ProteinProteinInteractionMongoDBAdaptor extends MongoDBAdaptor implements ProteinProteinInteractionDBAdaptor<Interaction> {


    public ProteinProteinInteractionMongoDBAdaptor(String species, String assembly, MongoDataStore mongoDataStore) {
        super(species, assembly, mongoDataStore);
        mongoDBCollection = mongoDataStore.getCollection("variation");

        logger.debug("ProteinProteinInteractionMongoDBAdaptor: in 'constructor'");
    }

//    @Override
//    public CellBaseDataResult rank(Query query, String field, int numResults, boolean asc) {
//        return null;
//    }

    @Override
    public CellBaseDataResult groupBy(Query query, String field, QueryOptions options) {
        return groupBy(parseQuery(query), field, "name", options);
    }

    @Override
    public CellBaseDataResult groupBy(Query query, List<String> fields, QueryOptions options) {
        return groupBy(parseQuery(query), fields, "name", options);
    }

    @Override
    public CellBaseDataResult<Long> update(List objectList, String field, String[] innerFields) {
        return null;
    }

    @Override
    public CellBaseDataResult<Long> count(Query query) {
        return new CellBaseDataResult<>(mongoDBCollection.count(parseQuery(query)));
    }

    @Override
    public CellBaseDataResult distinct(Query query, String field) {
        return new CellBaseDataResult<>(mongoDBCollection.distinct(field, parseQuery(query)));
    }

//    @Override
//    public CellBaseDataResult stats(Query query) {
//        return null;
//    }

    @Override
    public CellBaseDataResult<Interaction> get(Query query, QueryOptions options) {
        return null;
    }

    @Override
    public CellBaseDataResult nativeGet(AbstractQuery query) {
        return new CellBaseDataResult<>(mongoDBCollection.find(new BsonDocument(), null));
    }

    @Override
    public CellBaseDataResult nativeGet(Query query, QueryOptions options) {
        return new CellBaseDataResult<>(mongoDBCollection.find(parseQuery(query), options));
    }

    @Override
    public Iterator<Interaction> iterator(Query query, QueryOptions options) {
        return null;
    }

    @Override
    public Iterator nativeIterator(Query query, QueryOptions options) {
        return mongoDBCollection.nativeQuery().find(parseQuery(query), options);
    }

    @Override
    public void forEach(Query query, Consumer<? super Object> action, QueryOptions options) {

    }

    private Bson parseQuery(Query query) {
        List<Bson> andBsonList = new ArrayList<>();

        createOrQuery(query, ProteinProteinInteractionDBAdaptor.QueryParams.INTERACTOR_A_ID.key(), "interactorA.id", andBsonList);
        createOrQuery(query, ProteinProteinInteractionDBAdaptor.QueryParams.INTERACTOR_B_ID.key(), "interactorB.id", andBsonList);
        createOrQuery(query, ProteinProteinInteractionDBAdaptor.QueryParams.INTERACTOR_A_XREFS.key(), "interactorA.xrefs", andBsonList);
        createOrQuery(query, ProteinProteinInteractionDBAdaptor.QueryParams.INTERACTOR_B_XREFS.key(), "interactorB.xrefs", andBsonList);
        createOrQuery(query, ProteinProteinInteractionDBAdaptor.QueryParams.XREFs.key(), "xrefs.id", andBsonList);
        createOrQuery(query, ProteinProteinInteractionDBAdaptor.QueryParams.TYPE_PSIMI.key(), "type.name", andBsonList);
        createOrQuery(query, ProteinProteinInteractionDBAdaptor.QueryParams.TYPE_NAME.key(), "type.name", andBsonList);
        createOrQuery(query,
                ProteinProteinInteractionDBAdaptor.QueryParams.DETECTION_METHOD_NAME.key(), "detectionMethod.name", andBsonList);

        if (andBsonList.size() > 0) {
            return Filters.and(andBsonList);
        } else {
            return new Document();
        }
    }

}
