package com.example.leavesystem.repository;

import com.example.leavesystem.dto.DepartmentEmployeeCountRequest;
import com.example.leavesystem.entity.Role;
import com.example.leavesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
    User findByUsername(String username);

    @Query("SELECT new com.example.leavesystem.dto.DepartmentEmployeeCountRequest(d.id, d.name, COUNT(u)) " +
            "FROM User u JOIN u.department d GROUP BY d.id, d.name")
    List<DepartmentEmployeeCountRequest> countEmployeesByDepartment();

    @Query("SELECT u FROM User u WHERE u.department.name = :departmentName")
    List<User> findByDepartmentName(@Param("departmentName") String departmentName);



    List<User> findByRole(Role role);
}
