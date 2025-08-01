package com.erebelo.graphdemo.persistence.mongodb;

import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Reference;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphConfig {

    @Bean
    public Graph<Reference<Node>, Reference<Edge>> graph() {
        return new DefaultDirectedGraph<>(null, null, false);
    }
}
