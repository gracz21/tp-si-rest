package com.example.rest.utils;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by inf109714 on 2016-04-29.
 */
public class DatastoreHandlerUtil {
    private static DatastoreHandlerUtil Instance = new DatastoreHandlerUtil();
    private Datastore datastore;

    public static DatastoreHandlerUtil getInstance() {
        return Instance;
    }

    private DatastoreHandlerUtil() {
        final Morphia morphia = new Morphia();
        datastore = morphia.createDatastore(new MongoClient("localhost", 8004), "rest");
        morphia.mapPackage("com.example.rest.models");
        datastore.ensureIndexes();
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
