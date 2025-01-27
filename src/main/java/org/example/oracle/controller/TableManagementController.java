package org.example.oracle.controller;

import org.example.oracle.classes.TableRequest;
import org.example.oracle.classes.model.TableInfo;
import org.example.oracle.service.TableManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableManagementController {

    @Autowired
    private TableManagementService tableManagementService;

    @PostMapping("/create")
    public ResponseEntity<?> createTable(@RequestBody TableRequest request) {
        try {
            System.out.println("Received request: " + request);
            tableManagementService.createTable(request.getTableName(), request.getColumns());
            return ResponseEntity.ok("Table created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating table: " + e.getMessage());
        }
    }
    @PostMapping("/{tableName}/insert")
    public ResponseEntity<?> insertIntoTable(@PathVariable String tableName, @RequestBody List<String> values) {
        try {
            tableManagementService.insertIntoTable(tableName, values);
            return ResponseEntity.ok("Data inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error inserting data: " + e.getMessage());
        }
    }


    @GetMapping("/list")
    public ResponseEntity<List<TableInfo>> listTables() {
        try {
            List<TableInfo> tables = tableManagementService.listTables();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{tableName}")
    public ResponseEntity<?> getTableInfo(@PathVariable String tableName) {
        try {
            TableInfo tableInfo = tableManagementService.getTableInfo(tableName);
            if (tableInfo != null) {
                return ResponseEntity.ok(tableInfo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error fetching table info: " + e.getMessage());
        }
    }

    @PutMapping("/{tableName}")
    public ResponseEntity<?> editTable(@PathVariable String tableName, @RequestBody List<String> newColumns) {
        try {
            tableManagementService.editTable(tableName, newColumns);
            return ResponseEntity.ok("Table updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error editing table: " + e.getMessage());
        }
    }

    @DeleteMapping("/{tableName}")
    public ResponseEntity<?> deleteTable(@PathVariable String tableName) {
        try {
            tableManagementService.deleteTable(tableName);
            return ResponseEntity.ok("Table deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error deleting table: " + e.getMessage());
        }
    }

    @GetMapping("/columns/{tableName}")
    public ResponseEntity<?> listColumns(@PathVariable String tableName) {
        try {
            List<String> columns = tableManagementService.listColumns(tableName);
            return columns != null ? ResponseEntity.ok(columns) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error fetching columns: " + e.getMessage());
        }
    }
}
