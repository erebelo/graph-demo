package com.erebelo.graphdemo.persistence.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017/demo-db?ssl=false&replicaSet=rs0&authSource=admin");
    }

    @Bean
    public MongoSession mongoSession(MongoClient mongoClient) {
        return new MongoSession(mongoClient, "demo-db");
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoSession mongoSession) {
        return mongoSession.database();
    }
}
