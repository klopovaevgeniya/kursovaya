package com.example.AutoDetail.controller.api;

import com.example.AutoDetail.dto.SupplierDto;
import com.example.AutoDetail.entity.Supplier;
import com.example.AutoDetail.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API для управления поставщиками
 * Базовый URL: /api/v1/suppliers
 */
// @RestController
// @RequestMapping("/api/v1/suppliers")
public class SupplierApiController {

    private final AdminService adminService;

    public SupplierApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    // @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        try {
            List<SupplierDto> suppliers = adminService.getAllSuppliers().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable Long id) {
        try {
            return adminService.getSupplierById(id)
                    .map(this::convertToDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @PostMapping
    public ResponseEntity<SupplierDto> createSupplier(@RequestBody SupplierDto supplierDto) {
        try {
            Supplier supplier = convertToEntity(supplierDto);
            Supplier savedSupplier = adminService.saveSupplier(supplier);
            return ResponseEntity.ok(convertToDto(savedSupplier));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // @PutMapping("/{id}")
    public ResponseEntity<SupplierDto> updateSupplier(@PathVariable Long id, @RequestBody SupplierDto supplierDto) {
        try {
            supplierDto.setId(id);
            Supplier supplier = convertToEntity(supplierDto);
            Supplier updatedSupplier = adminService.saveSupplier(supplier);
            return ResponseEntity.ok(convertToDto(updatedSupplier));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        try {
            adminService.deleteSupplier(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private SupplierDto convertToDto(Supplier supplier) {
        return new SupplierDto(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactPhone(),
                supplier.getContactEmail()
        );
    }

    private Supplier convertToEntity(SupplierDto supplierDto) {
        Supplier supplier = new Supplier();
        supplier.setId(supplierDto.getId());
        supplier.setName(supplierDto.getName());
        supplier.setContactPhone(supplierDto.getContactPhone());
        supplier.setContactEmail(supplierDto.getContactEmail());
        return supplier;
    }
}