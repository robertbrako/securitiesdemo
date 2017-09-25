package com.rmbcorp.securitiesdemo;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class DemoPrinter {

    private static final Logger logger = Logger.getLogger(DemoPrinter.class.getName());

    static void print(List<ObjectNode> objectNodes) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        objectNodes.forEach(item -> {
            try {
                System.out.println(writer.writeValueAsString(item));
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        });
    }
}
