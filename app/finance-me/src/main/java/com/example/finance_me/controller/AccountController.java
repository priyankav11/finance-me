package com.example.finance_me.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class AccountController {
    private final Map<String, Map<String, Object>> db = new HashMap<>();

    @PostMapping("/createAccount")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> req){
        String acc = String.valueOf(req.getOrDefault("accountNo", UUID.randomUUID().toString()));
        db.put(acc, req);
        return ResponseEntity.ok(Map.of("status","created","accountNo",acc));
    }

    @PutMapping("/updateAccount/{accountNo}")
    public ResponseEntity<?> update(@PathVariable String accountNo, @RequestBody Map<String, Object> req){
        db.put(accountNo, req);
        return ResponseEntity.ok(Map.of("status","updated","accountNo",accountNo));
    }

    @GetMapping("/viewPolicy/{accountNo}")
    public ResponseEntity<?> view(@PathVariable String accountNo){
        return ResponseEntity.ok(db.getOrDefault(accountNo, Map.of("error","not found")));
    }

    @DeleteMapping("/deletePolicy/{accountNo}")
    public ResponseEntity<?> delete(@PathVariable String accountNo){
        db.remove(accountNo);
        return ResponseEntity.ok(Map.of("status","deleted","accountNo",accountNo));
    }
}
