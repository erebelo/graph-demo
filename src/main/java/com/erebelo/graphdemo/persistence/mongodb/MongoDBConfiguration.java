package com.erebelo.graphdemo.persistence.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDBConfiguration {

    private static final String CONNECTION_STRING_TEMPLATE = "mongodb://%s:%s/%s?ssl=false&replicaSet=rs0&authSource=admin";

    @Value("${database.host:localhost}")
    protected String dbHost;

    @Value("${database.port:27017}")
    protected String dbPort;

    @Value("${database.name:demo_db}")
    private String dbName;

    private ConnectionString getConnectionString() {
        return new ConnectionString(String.format(CONNECTION_STRING_TEMPLATE, dbHost, dbPort, dbName));
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(getConnectionString());
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(dbName);
    }
}
