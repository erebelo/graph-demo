package com.erebelo.graphdemo.persistence.mongodb;

import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphConfig {

    @Bean
    public Graph<Node, Edge> graph() {
        return new DefaultDirectedGraph<>(null, null, false);
    }
}
