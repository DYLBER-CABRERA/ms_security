package com.ddcf.security.Controllers;

import com.ddcf.security.Models.Permission;
import com.ddcf.security.Repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {
    @Autowired
    private PermissionRepository thePermissionRepository;
    @GetMapping("")
    public List<Permission> findAll(){
        return this.thePermissionRepository.findAll();
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Permission create(@RequestBody Permission theNewPermission){
        return this.thePermissionRepository.save(theNewPermission);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        Permission thePermission = this.thePermissionRepository
                .findById(id)
                .orElse(null);
        if (thePermission != null) {
            this.thePermissionRepository.delete(thePermission);
        }
    }
}
