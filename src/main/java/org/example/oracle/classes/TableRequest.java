package org.example.oracle.classes;


import lombok.Data;

import java.util.List;

@Data
public class TableRequest {
    // Getters and Setters
    private String tableName;
    private List<String>columns;


}
