////package com.medhir.rest.sales;
////
////import jakarta.validation.Valid;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.List;
////
////@RestController
////@RequestMapping("/lead/")
////@CrossOrigin(origins = "*")
////public class LeadModlController {
////
////    @Autowired
////    private LeadModlService service;
////
////    @PostMapping("/create")
////    public ResponseEntity<LeadModl> createLead(@Valid @RequestBody LeadModl lead) {
////        return ResponseEntity.ok(service.addLead(lead));
////    }
////
////    @GetMapping("/get")
////    public ResponseEntity<List<LeadModl>> getAllLeads() {
////        return ResponseEntity.ok(service.getAllLeads());
////    }
////
////    @GetMapping("/getby/{id}")
////    public ResponseEntity<LeadModl> getLeadById(@PathVariable String id) {
////        return service.getLeadById(id)
////                .map(ResponseEntity::ok)
////                .orElse(ResponseEntity.notFound().build());
////    }
////
////    @PutMapping("/update/{id}")
////    public ResponseEntity<LeadModl> updateLead(@PathVariable String id, @Valid @RequestBody LeadModl lead) {
////        return service.updateLead(id, lead)
////                .map(ResponseEntity::ok)
////                .orElse(ResponseEntity.notFound().build());
////    }
////
////    @DeleteMapping("/deleteby/{id}")
////    public ResponseEntity<Void> deleteLead(@PathVariable String id) {
////        if (service.deleteLead(id)) {
////            return ResponseEntity.noContent().build();
////        } else {
////            return ResponseEntity.notFound().build();
////        }
////    }
////}
//package com.medhir.rest.sales;
//
////import jakarta.validation.Valid;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/lead")
//@CrossOrigin(origins = "*")
//public class LeadModlController {
//
//    @Autowired
//    private LeadModlService service;
//
//    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'MANAGER', 'HRADMIN', 'SUPERADMIN')")
//    @PostMapping("/create")
//    public ResponseEntity<LeadModl> createLead( @Valid @RequestBody LeadModl lead) {
//        return ResponseEntity.ok(service.addLead(lead));
//    }
//
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/get")
//    public ResponseEntity<List<LeadModl>> getAllLeads() {
//        return ResponseEntity.ok(service.getAllLeads());
//    }
//
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/getby/{id}")
//    public ResponseEntity<LeadModl> getLeadById(@PathVariable String id) {
//        return service.getLeadById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PreAuthorize("hasAnyAuthority('HRADMIN', 'SUPERADMIN')")
//    @PutMapping("/update/{id}")
//    public ResponseEntity<LeadModl> updateLead( @PathVariable String id,  @Valid @RequestBody LeadModl lead) {
//        return service.updateLead(id, lead)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PreAuthorize("hasAnyAuthority('HRADMIN', 'SUPERADMIN')")
//    @DeleteMapping("/deleteby/{id}")
//    public ResponseEntity<Void> deleteLead(@PathVariable String id) {
//        if (service.deleteLead(id)) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}
package com.medhir.rest.sales;

import com.medhir.rest.sales.dto.LeadDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lead")
@CrossOrigin(origins = "*")
public class LeadModlController {

    @Autowired
    private LeadModlService service;

    @PostMapping("/create")
    public ResponseEntity<LeadDto> createLead(@Valid @RequestBody LeadDto dto) {
        LeadDto saved = service.addLead(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/get")
    public ResponseEntity<List<LeadDto>> getAllLeads() {
        List<LeadDto> leads = service.getAllLeads();
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/getby/{id}")
    public ResponseEntity<LeadDto> getLeadById(@PathVariable String id) {
        return service.getLeadById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LeadDto> updateLead(@PathVariable String id, @Valid @RequestBody LeadDto dto) {
        return service.updateLead(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteby/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable String id) {
        if (service.deleteLead(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
