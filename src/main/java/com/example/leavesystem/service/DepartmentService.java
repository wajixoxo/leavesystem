package com.example.leavesystem.service;

import com.example.leavesystem.dto.DepartmentRequest;
import com.example.leavesystem.entity.Department;
import com.example.leavesystem.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Cacheable(value = "departmentList")
    public List<DepartmentRequest> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DepartmentRequest getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return mapToDto(department);
    }

    @CacheEvict(value = "departmentList", allEntries = true)
    public DepartmentRequest createDepartment(DepartmentRequest departmentDto) {
        if (departmentRepository.existsByName(departmentDto.getName())) {
            throw new RuntimeException("Department already exists with name: " + departmentDto.getName());
        }

        Department department = new Department();
        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());

        Department savedDepartment = departmentRepository.save(department);
        return mapToDto(savedDepartment);
    }

    @CacheEvict(value = "departmentList", allEntries = true)
    public DepartmentRequest updateDepartment(Long id, DepartmentRequest departmentDto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());

        Department updatedDepartment = departmentRepository.save(department);
        return mapToDto(updatedDepartment);
    }

    @CacheEvict(value = "departmentList", allEntries = true)
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentRequest mapToDto(Department department) {
        DepartmentRequest dto = new DepartmentRequest();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        return dto;
    }
}
