package com.medhir.rest.service.employee;

import com.medhir.rest.dto.*;
import com.medhir.rest.exception.BadRequestException;
import com.medhir.rest.service.auth.EmployeeAuthService;
import com.medhir.rest.model.employee.EmployeeModel;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.company.CompanyModel;
import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.repository.employee.EmployeeRepository;
import com.medhir.rest.repository.ModuleRepository;
import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.service.company.CompanyService;
import com.medhir.rest.service.settings.DepartmentService;
import com.medhir.rest.model.settings.DesignationModel;
import com.medhir.rest.service.settings.DesignationService;
import com.medhir.rest.model.settings.LeaveTypeModel;
import com.medhir.rest.service.settings.LeaveTypeService;
import com.medhir.rest.model.settings.LeavePolicyModel;
import com.medhir.rest.service.settings.LeavePolicyService;
import com.medhir.rest.utils.GeneratedId;
import com.medhir.rest.utils.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import com.medhir.rest.dto.EmployeeDTO;
import com.medhir.rest.mapper.employee.EmployeeMapper;

@Service
public class EmployeeService {

//    @Value("${auth.service.url}")
//    String authServiceUrl;
//    @Value("${attendance.service.url}")
//    String attendanceServiceUrl;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MinioService minioService;
    @Autowired
    private GeneratedId generatedId;
    @Autowired
    private CompanyService companyService;
    @Autowired
    ModuleRepository moduleRepository;
    @Autowired
    private DesignationService designationService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private EmployeeAuthService employeeAuthService;
    @Autowired
    private LeavePolicyService leavePolicyService;
    @Autowired
    private LeaveTypeService leaveTypeService;

    // Create Employee
    public EmployeeWithLeaveDetailsDTO createEmployee(EmployeeDTO employeeDTO,
                                                      MultipartFile profileImage,
                                                      MultipartFile aadharImage,
                                                      MultipartFile panImage,
                                                      MultipartFile passportImage,
                                                      MultipartFile drivingLicenseImage,
                                                      MultipartFile voterIdImage,
                                                      MultipartFile passbookImage) {

        EmployeeModel employee = EmployeeMapper.toEmployeeModel(employeeDTO);

        employee.setEmployeeId(generateEmployeeId(employee.getCompanyId()));

        if (employeeRepository.findByEmployeeId(employee.getEmployeeId()).isPresent()) {
            throw new DuplicateResourceException("Employee ID already exists: " + employee.getEmployeeId());
        }
        if (employee.getEmailPersonal() != null) {
            if (employeeRepository.findByEmailPersonal(employee.getEmailPersonal()).isPresent()) {
                throw new DuplicateResourceException("Email already exists: " + employee.getEmailPersonal());
            }
        }

        if (employeeRepository.findByPhone(employee.getPhone()).isPresent()) {
            throw new DuplicateResourceException("Phone number already exists : " + employee.getPhone());
        }

        // Set role as EMPLOYEE
        employee.setRoles(Set.of("EMPLOYEE"));

        if ((employee.getDepartment() == null || employee.getDepartment().isEmpty()) &&
                (employee.getDesignation() != null && !employee.getDesignation().isEmpty())) {
            throw new BadRequestException("Department must be provided when a designation is specified.");
        }

        if (employee.getDepartment() != null && !employee.getDepartment().isEmpty()) {

            DepartmentModel department = departmentService.getDepartmentById(employee.getDepartment());

            if (department != null) {

                if (department.getLeavePolicy() != null && !department.getLeavePolicy().isEmpty()) {
                    employee.setLeavePolicyId(department.getLeavePolicy());
                }

                if (employee.getDesignation() != null && !employee.getDesignation().isEmpty()) {
                    DesignationModel designation = designationService.getDesignationById(employee.getDesignation());

                    if (designation == null) {
                        throw new BadRequestException("Designation not found: " + employee.getDesignation());
                    }

                    if (!designation.getDepartment().equals(department.getDepartmentId())) {
                        throw new BadRequestException("The given designation does not belong to the selected department.");
                    }
                }
            }
        }


        List<String> nameParts = new ArrayList<>();
        if (employee.getFirstName() != null && !employee.getFirstName().trim().isEmpty()) {
            nameParts.add(employee.getFirstName().trim());
        }
        if (employee.getMiddleName() != null && !employee.getMiddleName().trim().isEmpty()) {
            nameParts.add(employee.getMiddleName().trim());
        }
        if (employee.getLastName() != null && !employee.getLastName().trim().isEmpty()) {
            nameParts.add(employee.getLastName().trim());
        }

        employee.setName(String.join(" ", nameParts));

        // Generate image URLs only after validation passes
        if (profileImage != null) {
            employee.setEmployeeImgUrl(minioService.uploadProfileImage(profileImage, employee.getEmployeeId()));
        }
        if (aadharImage != null) {
            employee.getIdProofs()
                    .setAadharImgUrl(minioService.uploadDocumentsImg(aadharImage, employee.getEmployeeId()));
        }
        if (panImage != null) {
            employee.getIdProofs()
                    .setPancardImgUrl(minioService.uploadDocumentsImg(panImage, employee.getEmployeeId()));
        }
        if (passportImage != null) {
            employee.getIdProofs()
                    .setPassportImgUrl(minioService.uploadDocumentsImg(passportImage, employee.getEmployeeId()));
        }
        if (drivingLicenseImage != null) {
            employee.getIdProofs().setDrivingLicenseImgUrl(
                    minioService.uploadDocumentsImg(drivingLicenseImage, employee.getEmployeeId()));
        }
        if (voterIdImage != null) {
            employee.getIdProofs()
                    .setVoterIdImgUrl(minioService.uploadDocumentsImg(voterIdImage, employee.getEmployeeId()));
        }
        if (passbookImage != null) {
            employee.getBankDetails()
                    .setPassbookImgUrl(minioService.uploadDocumentsImg(passbookImage, employee.getEmployeeId()));
        }

        EmployeeModel savedEmployee = employeeRepository.save(employee);

        // Update reporting manager's assignTo list if a reporting manager is set
        if (savedEmployee.getReportingManager() != null && !savedEmployee.getReportingManager().isEmpty()) {
            updateManagerAssignTo(savedEmployee.getReportingManager());
        }

        // Create response DTO with leave details
        EmployeeWithLeaveDetailsDTO response = EmployeeMapper.toEmployeeWithLeaveDetailsDTO(savedEmployee);

        // Populate leave policy and type names
        if (savedEmployee.getLeavePolicyId() != null) {
            try {
                LeavePolicyModel leavePolicy = leavePolicyService.getLeavePolicyById(savedEmployee.getLeavePolicyId());
                response.setLeavePolicyName(leavePolicy.getName());

                // Get all leave type names and IDs from the policy
                List<String> leaveTypeNames = new ArrayList<>();
                List<String> leaveTypeIds = new ArrayList<>();

                leavePolicy.getLeaveAllocations().forEach(allocation -> {
                    try {
                        LeaveTypeModel leaveType = leaveTypeService.getLeaveTypeById(allocation.getLeaveTypeId());
                        leaveTypeNames.add(leaveType.getLeaveTypeName());
                        leaveTypeIds.add(leaveType.getLeaveTypeId());
                    } catch (Exception e) {
                        // Skip if leave type not found
                    }
                });

                response.setLeaveTypeNames(leaveTypeNames);
                response.setLeaveTypeIds(leaveTypeIds);
            } catch (Exception e) {
                // If leave policy not found, leave names and IDs as null
            }
        }

        try {
            // Register employee for login with email and phone number as password
            if (savedEmployee.getPhone() != null && !savedEmployee.getPhone().isEmpty() &&
                    savedEmployee.getEmailPersonal() != null && !savedEmployee.getEmailPersonal().isEmpty()) {
                employeeAuthService.registerEmployee(
                        savedEmployee.getEmployeeId(),
                        savedEmployee.getEmailPersonal(),
                        savedEmployee.getPhone());
            }
//
//            // call Attendance Service to register user for face verification
//            registerUserInAttendanceService(savedEmployee);
        } catch (Exception e) {
            // Log the error but don't fail the employee creation
            System.err.println("Failed to register employee in auth/attendance service: " + e.getMessage());
        }

        return response;
    }

    public EmployeeModel createCompanyHeadEmployee(
            String companyId,
            String departmentId,
            String designationId,
            String firstName,
            String middleName,
            String lastName,
            String email,
            String phone
    ) {
        EmployeeModel employee = new EmployeeModel();
        employee.setCompanyId(companyId);
        employee.setDepartment(departmentId);
        employee.setDesignation(designationId);
        employee.setFirstName(firstName);
        employee.setMiddleName(middleName);
        employee.setLastName(lastName);
        employee.setEmailPersonal(email);
        employee.setPhone(phone);

        employee.setEmployeeId(generateAdminsId(companyId));

        if (employeeRepository.findByEmployeeId(employee.getEmployeeId()).isPresent()) {
            throw new DuplicateResourceException("Employee ID already exists: " + employee.getEmployeeId());
        }

        if (employeeRepository.findByEmailPersonal(email).isPresent()) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        if (employeeRepository.findByPhone(phone).isPresent()) {
            throw new DuplicateResourceException("Phone number already exists: " + phone);
        }

        // Full name
        List<String> nameParts = new ArrayList<>();
        if (firstName != null && !firstName.trim().isEmpty()) {
            nameParts.add(firstName.trim());
        }
        if (middleName != null && !middleName.trim().isEmpty()) {
            nameParts.add(middleName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            nameParts.add(lastName.trim());
        }
        employee.setName(String.join(" ", nameParts));

        // Mark role as COMPANY_HEAD or ADMIN or your standard role
        employee.setRoles(Set.of("COMPANY_HEAD","EMPLOYEE"));

        // Save
        EmployeeModel saved = employeeRepository.save(employee);

        // Optionally register login too
        if (phone != null && email != null) {
            employeeAuthService.registerEmployee(saved.getEmployeeId(), email, phone);
        }

        return saved;
    }


    // Get All Employees
    public List<EmployeeWithLeaveDetailsDTO> getAllEmployees() {
        List<EmployeeModel> employees = employeeRepository.findAll();

        Set<String> departmentIds = employees.stream()
                .map(EmployeeModel::getDepartment)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> designationIds = employees.stream()
                .map(EmployeeModel::getDesignation)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> leavePolicyIds = employees.stream()
                .map(EmployeeModel::getLeavePolicyId)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toSet());

        Map<String, String> departmentNameMap = departmentService.getDepartmentsByIds(departmentIds).stream()
                .collect(Collectors.toMap(DepartmentModel::getDepartmentId, DepartmentModel::getName));

        Map<String, String> designationNameMap = designationService.getDesignationsByIds(designationIds).stream()
                .collect(Collectors.toMap(DesignationModel::getDesignationId, DesignationModel::getName));

        Map<String, LeavePolicyModel> leavePolicyMap = leavePolicyService.getLeavePoliciesByIds(leavePolicyIds).stream()
                .collect(Collectors.toMap(LeavePolicyModel::getLeavePolicyId, lp -> lp));

        Set<String> leaveTypeIds = leavePolicyMap.values().stream()
                .flatMap(lp -> lp.getLeaveAllocations().stream())
                .map(LeavePolicyModel.LeaveAllocation::getLeaveTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, String> leaveTypeNameMap = leaveTypeService.getLeaveTypesByIds(leaveTypeIds).stream()
                .collect(Collectors.toMap(LeaveTypeModel::getLeaveTypeId, LeaveTypeModel::getLeaveTypeName));

        return employees.stream().map(employee -> {
            EmployeeWithLeaveDetailsDTO dto = new EmployeeWithLeaveDetailsDTO(employee);

            // Department name
            dto.setDepartmentName(
                    departmentNameMap.getOrDefault(employee.getDepartment(), employee.getDepartment())
            );

            // Designation name
            dto.setDesignationName(
                    designationNameMap.getOrDefault(employee.getDesignation(), employee.getDesignation())
            );

            // Leave policy & leave types
            if (employee.getLeavePolicyId() != null && !employee.getLeavePolicyId().isEmpty()) {
                LeavePolicyModel leavePolicy = leavePolicyMap.get(employee.getLeavePolicyId());
                if (leavePolicy != null) {
                    dto.setLeavePolicyName(leavePolicy.getName());

                    List<String> leaveTypeNames = new ArrayList<>();
                    List<String> leaveTypeIdList = new ArrayList<>();
                    for (LeavePolicyModel.LeaveAllocation allocation : leavePolicy.getLeaveAllocations()) {
                        String leaveTypeId = allocation.getLeaveTypeId();
                        String leaveTypeName = leaveTypeNameMap.get(leaveTypeId);
                        if (leaveTypeName != null) {
                            leaveTypeNames.add(leaveTypeName);
                            leaveTypeIdList.add(leaveTypeId);
                        }
                    }
                    dto.setLeaveTypeNames(leaveTypeNames);
                    dto.setLeaveTypeIds(leaveTypeIdList);
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }

    // Get All Employees with minimal fields (name and employeeId)
    public List<Map<String, String>> getAllEmployeesMinimal() {
        List<EmployeeModel> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> Map.of(
                        "name", employee.getName(),
                        "employeeId", employee.getEmployeeId()))
                .collect(Collectors.toList());
    }

    // Get All Employees by Company ID
    public List<EmployeeWithLeaveDetailsDTO> getEmployeesByCompanyId(String companyId) {
        List<EmployeeModel> employees = employeeRepository.findByCompanyId(companyId);
        return employees.stream().map(employee -> {
            EmployeeWithLeaveDetailsDTO dto = EmployeeMapper.toEmployeeWithLeaveDetailsDTO(employee);

            // Get department name
            try {
                if (employee.getDepartment() != null && !employee.getDepartment().isEmpty()) {
                    dto.setDepartmentName(departmentService.getDepartmentById(employee.getDepartment()).getName());
                }
            } catch (Exception e) {
                dto.setDepartmentName(employee.getDepartment());
            }

            // Get designation name
            try {
                Optional<DesignationModel> designation = Optional
                        .ofNullable(designationService.getDesignationById(employee.getDesignation()));
                designation.ifPresent(d -> dto.setDesignationName(d.getName()));
                if (designation.isEmpty()) {
                    dto.setDesignationName(employee.getDesignation());
                }
            } catch (Exception e) {
                dto.setDesignationName(employee.getDesignation());
            }

            // Get leave policy name if available
            if (employee.getLeavePolicyId() != null) {
                try {
                    LeavePolicyModel leavePolicy = leavePolicyService.getLeavePolicyById(employee.getLeavePolicyId());
                    dto.setLeavePolicyName(leavePolicy.getName());

                    // Get all leave type names and IDs from the policy
                    List<String> leaveTypeNames = new ArrayList<>();
                    List<String> leaveTypeIds = new ArrayList<>();

                    leavePolicy.getLeaveAllocations().forEach(allocation -> {
                        try {
                            LeaveTypeModel leaveType = leaveTypeService.getLeaveTypeById(allocation.getLeaveTypeId());
                            leaveTypeNames.add(leaveType.getLeaveTypeName());
                            leaveTypeIds.add(leaveType.getLeaveTypeId());
                        } catch (Exception e) {
                            // Skip if leave type not found
                        }
                    });

                    dto.setLeaveTypeNames(leaveTypeNames);
                    dto.setLeaveTypeIds(leaveTypeIds);
                } catch (Exception e) {
                    // If leave policy not found, leave names and IDs as null
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }

    // Get Employee By EmployeeId
    public Optional<EmployeeWithLeaveDetailsDTO> getEmployeeById(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId).map(employee -> {
            EmployeeWithLeaveDetailsDTO dto = EmployeeMapper.toEmployeeWithLeaveDetailsDTO(employee);

            // Get department name
            try {
                if (employee.getDepartment() != null && !employee.getDepartment().isEmpty()) {
                    dto.setDepartmentName(departmentService.getDepartmentById(employee.getDepartment()).getName());
                }
            } catch (Exception e) {
                dto.setDepartmentName(employee.getDepartment());
            }

            // Get designation name
            try {
                Optional<DesignationModel> designation = Optional
                        .ofNullable(designationService.getDesignationById(employee.getDesignation()));
                designation.ifPresent(d -> dto.setDesignationName(d.getName()));
                if (designation.isEmpty()) {
                    dto.setDesignationName(employee.getDesignation());
                }
            } catch (Exception e) {
                dto.setDesignationName(employee.getDesignation());
            }

            // Get leave policy name if available
            if (employee.getLeavePolicyId() != null) {
                try {
                    LeavePolicyModel leavePolicy = leavePolicyService.getLeavePolicyById(employee.getLeavePolicyId());
                    dto.setLeavePolicyName(leavePolicy.getName());

                    // Get all leave type names and IDs from the policy
                    List<String> leaveTypeNames = new ArrayList<>();
                    List<String> leaveTypeIds = new ArrayList<>();

                    leavePolicy.getLeaveAllocations().forEach(allocation -> {
                        try {
                            LeaveTypeModel leaveType = leaveTypeService.getLeaveTypeById(allocation.getLeaveTypeId());
                            leaveTypeNames.add(leaveType.getLeaveTypeName());
                            leaveTypeIds.add(leaveType.getLeaveTypeId());
                        } catch (Exception e) {
                            // Skip if leave type not found
                        }
                    });

                    dto.setLeaveTypeNames(leaveTypeNames);
                    dto.setLeaveTypeIds(leaveTypeIds);
                } catch (Exception e) {
                    // If leave policy not found, leave names and IDs as null
                }
            }

            return dto;
        });
    }

    // Get Employees by Manager
    public List<ManagerEmployeeDTO> getEmployeesByManager(String managerId) {
        List<EmployeeModel> employees = employeeRepository.findByReportingManager(managerId);

        return employees.stream()
                .map(employee -> {
                    ManagerEmployeeDTO dto = new ManagerEmployeeDTO();
                    dto.setEmployeeId(employee.getEmployeeId());
                    dto.setName(employee.getName());
                    dto.setFathersName(employee.getFathersName());
                    dto.setPhone(employee.getPhone());
                    dto.setEmailOfficial(employee.getEmailOfficial());
                    dto.setJoiningDate(employee.getJoiningDate());
                    dto.setCurrentAddress(employee.getCurrentAddress());
                    dto.setRoles(employee.getRoles());

                    // Get designation name from designation service
                    try {
                        Optional<DesignationModel> designation = Optional
                                .ofNullable(designationService.getDesignationById(employee.getDesignation()));
                        designation.ifPresent(d -> dto.setDesignationName(d.getName()));
                        if (designation.isEmpty()) {
                            dto.setDesignationName(employee.getDesignation());
                        }
                    } catch (Exception e) {
                        dto.setDesignationName(employee.getDesignation());
                    }

                    // Get department name from department service
                    try {
                        if (employee.getDepartment() != null && !employee.getDepartment().isEmpty()) {
                            dto.setDepartmentName(
                                    departmentService.getDepartmentById(employee.getDepartment()).getName());
                        }
                    } catch (Exception e) {
                        dto.setDepartmentName(employee.getDepartment());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Delete Employee by Employee ID
    public void deleteEmployee(String employeeId) {
        Optional<EmployeeModel> employeeOpt = employeeRepository.findByEmployeeId(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        // Delete the employee
        employeeRepository.delete(employeeOpt.get());
    }

    // Update Employee
    public EmployeeWithLeaveDetailsDTO updateEmployee(String employeeId, EmployeeDTO updatedEmployeeDTO,
                                                      MultipartFile profileImage,
                                                      MultipartFile aadharImage,
                                                      MultipartFile panImage,
                                                      MultipartFile passportImage,
                                                      MultipartFile drivingLicenseImage,
                                                      MultipartFile voterIdImage,
                                                      MultipartFile passbookImage) {
        return employeeRepository.findByEmployeeId(employeeId).map(existingEmployee -> {

            Optional<EmployeeModel> employeeIDExists = employeeRepository
                    .findByEmployeeId(updatedEmployeeDTO.getEmployeeId());
            if (employeeIDExists.isPresent() && !employeeIDExists.get().getEmployeeId().equals(employeeId)) {
                throw new DuplicateResourceException("Employee ID already exists: " + updatedEmployeeDTO.getEmployeeId());
            }

            if (updatedEmployeeDTO.getEmailPersonal() != null) {
                Optional<EmployeeModel> emailExists = employeeRepository
                        .findByEmailPersonal(updatedEmployeeDTO.getEmailPersonal());
                if (emailExists.isPresent() && !emailExists.get().getEmployeeId().equals(employeeId)) {
                    throw new DuplicateResourceException(
                            emailExists.get().getEmailPersonal() + " : Email is already in use by another Employee");
                }
            }

            Optional<EmployeeModel> phoneExists = employeeRepository.findByPhone(updatedEmployeeDTO.getPhone());
            if (phoneExists.isPresent() && !phoneExists.get().getEmployeeId().equals(employeeId)) {
                throw new DuplicateResourceException(
                        phoneExists.get().getPhone() + " : Phone number is already in use by another Employee");
            }

            // Store old reporting manager for comparison
            String oldReportingManager = existingEmployee.getReportingManager();

            StringBuilder fullName = new StringBuilder();

            if (updatedEmployeeDTO.getFirstName() != null && !updatedEmployeeDTO.getFirstName().trim().isEmpty()) {
                fullName.append(updatedEmployeeDTO.getFirstName().trim());
            }

            if (updatedEmployeeDTO.getMiddleName() != null && !updatedEmployeeDTO.getMiddleName().trim().isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(updatedEmployeeDTO.getMiddleName().trim());
            }

            if (updatedEmployeeDTO.getLastName() != null && !updatedEmployeeDTO.getLastName().trim().isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(updatedEmployeeDTO.getLastName().trim());
            }

            updatedEmployeeDTO.setName(fullName.toString());


            // Update basic details
            existingEmployee.setName(updatedEmployeeDTO.getName());
            existingEmployee.setDesignation(updatedEmployeeDTO.getDesignation());
            existingEmployee.setFathersName(updatedEmployeeDTO.getFathersName());
            existingEmployee.setOvertimeEligibile(updatedEmployeeDTO.isOvertimeEligibile());
            existingEmployee.setPfEnrolled(updatedEmployeeDTO.isPfEnrolled());
            existingEmployee.setUanNumber(updatedEmployeeDTO.getUanNumber());
            existingEmployee.setEsicEnrolled(updatedEmployeeDTO.isEsicEnrolled());
            existingEmployee.setEsicNumber(updatedEmployeeDTO.getEsicNumber());
            existingEmployee.setWeeklyOffs(updatedEmployeeDTO.getWeeklyOffs());
            existingEmployee.setEmailPersonal(updatedEmployeeDTO.getEmailPersonal());
            existingEmployee.setEmailOfficial(updatedEmployeeDTO.getEmailOfficial());
            existingEmployee.setPhone(updatedEmployeeDTO.getPhone());
            existingEmployee.setAlternatePhone(updatedEmployeeDTO.getAlternatePhone());
            existingEmployee.setDepartment(updatedEmployeeDTO.getDepartment());
            existingEmployee.setGender(updatedEmployeeDTO.getGender());
            existingEmployee.setReportingManager(updatedEmployeeDTO.getReportingManager());
            existingEmployee.setPermanentAddress(updatedEmployeeDTO.getPermanentAddress());
            existingEmployee.setCurrentAddress(updatedEmployeeDTO.getCurrentAddress());
            existingEmployee.setSalaryDetails(EmployeeMapper.toModel(updatedEmployeeDTO.getSalaryDetails()));
            existingEmployee.setJoiningDate(updatedEmployeeDTO.getJoiningDate());
            existingEmployee.setFirstName(updatedEmployeeDTO.getFirstName());
            existingEmployee.setMiddleName(updatedEmployeeDTO.getMiddleName());
            existingEmployee.setLastName(updatedEmployeeDTO.getLastName());

            // Update leave policy based on department
            // Update leave policy based on department
            if (updatedEmployeeDTO.getDepartment() != null && !updatedEmployeeDTO.getDepartment().isEmpty()) {
                DepartmentModel department = departmentService.getDepartmentById(updatedEmployeeDTO.getDepartment());

                // Only update leave policy if department has one assigned
                if (department.getLeavePolicy() != null) {
                    LeavePolicyModel leavePolicy = leavePolicyService.getLeavePolicyById(department.getLeavePolicy());

                    // Set the leave policy ID
                    existingEmployee.setLeavePolicyId(department.getLeavePolicy());
                } else {
                    // Optionally clear or skip setting the leavePolicyId
                    System.out.println("Department does not have a leave policy assigned.");
                    existingEmployee.setLeavePolicyId(null); // or leave as-is
                }
            }

            // Update Bank Details
            if (updatedEmployeeDTO.getBankDetails() != null) {
                if (existingEmployee.getBankDetails() == null) {
                    existingEmployee.setBankDetails(new EmployeeModel.BankDetails());
                }
                existingEmployee.setBankDetails(EmployeeMapper.toModel(updatedEmployeeDTO.getBankDetails()));
            }

            // Update ID Proofs
            if (updatedEmployeeDTO.getIdProofs() != null) {
                if (existingEmployee.getIdProofs() == null) {
                    existingEmployee.setIdProofs(new EmployeeModel.IdProofs());
                }
                existingEmployee.setIdProofs(EmployeeMapper.toModel(updatedEmployeeDTO.getIdProofs()));
            }

            // Update Salary Details
            if (updatedEmployeeDTO.getSalaryDetails() != null) {
                existingEmployee.setSalaryDetails(EmployeeMapper.toModel(updatedEmployeeDTO.getSalaryDetails()));
            }

            // Preserve existing images or update if a new image is uploaded
            if (profileImage != null) {
                existingEmployee.setEmployeeImgUrl(
                        minioService.uploadProfileImage(profileImage, existingEmployee.getEmployeeId()));
            }

            if (existingEmployee.getIdProofs() == null) {
                existingEmployee.setIdProofs(new EmployeeModel.IdProofs());
            }

            if (aadharImage != null) {
                existingEmployee.getIdProofs().setAadharImgUrl(
                        minioService.uploadDocumentsImg(aadharImage, existingEmployee.getEmployeeId()));
            }

            if (panImage != null) {
                existingEmployee.getIdProofs()
                        .setPancardImgUrl(minioService.uploadDocumentsImg(panImage, existingEmployee.getEmployeeId()));
            }

            if (passportImage != null) {
                existingEmployee.getIdProofs().setPassportImgUrl(
                        minioService.uploadDocumentsImg(passportImage, existingEmployee.getEmployeeId()));
            }

            if (drivingLicenseImage != null) {
                existingEmployee.getIdProofs().setDrivingLicenseImgUrl(
                        minioService.uploadDocumentsImg(drivingLicenseImage, existingEmployee.getEmployeeId()));
            }

            if (voterIdImage != null) {
                existingEmployee.getIdProofs().setVoterIdImgUrl(
                        minioService.uploadDocumentsImg(voterIdImage, existingEmployee.getEmployeeId()));
            }

            if (existingEmployee.getBankDetails() == null) {
                existingEmployee.setBankDetails(new EmployeeModel.BankDetails());
            }

            if (passbookImage != null) {
                existingEmployee.getBankDetails().setPassbookImgUrl(
                        minioService.uploadDocumentsImg(passbookImage, existingEmployee.getEmployeeId()));
            }

            // existingEmployee = setDefaultValues(existingEmployee);

//            // call Attendance Service to update user for face verification
//            updateEmployeeInAttendanceService(existingEmployee);

            EmployeeModel savedEmployee = employeeRepository.save(existingEmployee);

            // Create response DTO with leave details
            EmployeeWithLeaveDetailsDTO response = EmployeeMapper.toEmployeeWithLeaveDetailsDTO(savedEmployee);

            // Populate leave policy and type names
            if (savedEmployee.getLeavePolicyId() != null) {
                try {
                    LeavePolicyModel leavePolicy = leavePolicyService
                            .getLeavePolicyById(savedEmployee.getLeavePolicyId());
                    response.setLeavePolicyName(leavePolicy.getName());

                    // Get all leave type names and IDs from the policy
                    List<String> leaveTypeNames = new ArrayList<>();
                    List<String> leaveTypeIds = new ArrayList<>();

                    leavePolicy.getLeaveAllocations().forEach(allocation -> {
                        try {
                            LeaveTypeModel leaveType = leaveTypeService.getLeaveTypeById(allocation.getLeaveTypeId());
                            leaveTypeNames.add(leaveType.getLeaveTypeName());
                            leaveTypeIds.add(leaveType.getLeaveTypeId());
                        } catch (Exception e) {
                            // Skip if leave type not found
                        }
                    });

                    response.setLeaveTypeNames(leaveTypeNames);
                    response.setLeaveTypeIds(leaveTypeIds);
                } catch (Exception e) {
                    // If leave policy not found, leave names and IDs as null
                }
            }

            // If reporting manager changed, update both old and new manager's assignTo lists
            String newReportingManager = updatedEmployeeDTO.getReportingManager() != null ? updatedEmployeeDTO.getReportingManager() : "";
            if (!newReportingManager.equals(oldReportingManager)) {
                // Update old manager's assignTo list (remove this employee)
                updateManagerAssignTo(oldReportingManager);
                // Update new manager's assignTo list (add this employee)
                updateManagerAssignTo(newReportingManager);
            }

            return response;
        }).orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));
    }

    public String generateEmployeeId(String companyId) {
        CompanyModel company = companyService.getCompanyById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        String prefix = company.getPrefixForEmpID();

        return generatedId.generateId(prefix, EmployeeModel.class, "employeeId");
    }
    public String generateAdminsId(String companyId) {
        CompanyModel company = companyService.getCompanyById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        String prefix = company.getPrefixForEmpID();

        return generatedId.generateAdminId(prefix, EmployeeModel.class, "employeeId");
    }

    public List<UserCompanyDTO> getEmployeeCompanies(String employeeId) {
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));

        if (employee.getModuleIds() == null || employee.getModuleIds().isEmpty()) {
            return List.of();
        }

        // Get all modules for the employee
        List<ModuleModel> modules = employee.getModuleIds().stream()
                .map(moduleId -> moduleRepository.findByModuleId(moduleId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        // Get unique company IDs from modules
        Set<String> companyIds = modules.stream()
                .filter(module -> module.getCompanyId() != null)
                .map(ModuleModel::getCompanyId)
                .collect(Collectors.toSet());

        // Get company details for each company ID
        return companyIds.stream()
                .map(companyId -> {
                    try {
                        Optional<CompanyModel> company = companyService.getCompanyById(companyId);
                        return new UserCompanyDTO(
                                company.get().getCompanyId(),
                                company.get().getName(),
                                company.get().getColorCode());
                    } catch (ResourceNotFoundException e) {
                        return new UserCompanyDTO(companyId, "Unknown Company", "Unknown Color");
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getManagersByDepartment(String departmentId) {
        // Get all designations in the department that have isManager=true
        List<DesignationModel> managerDesignations = designationService.getDesignationsByDepartment(departmentId)
                .stream()
                .filter(DesignationModel::isManager)
                .collect(Collectors.toList());

        // Get all designation IDs
        List<String> managerDesignationIds = managerDesignations.stream()
                .map(DesignationModel::getDesignationId)
                .collect(Collectors.toList());

        // Get all employees with these designations
        List<EmployeeModel> managers = employeeRepository.findByDepartmentAndDesignationIn(departmentId,
                managerDesignationIds);

        // Map to required format (name and employeeId)
        return managers.stream()
                .map(manager -> Map.of(
                        "name", manager.getName(),
                        "employeeId", manager.getEmployeeId()))
                .collect(Collectors.toList());
    }

    // Get All Employees by Company ID with additional details
    public List<CompanyEmployeeDTO> getAllEmployeesByCompanyIdWithDetails(String companyId) {
        List<EmployeeModel> employees = employeeRepository.findByCompanyId(companyId);

        // Batch fetch: Departments, Designations, Leave Policies, Reporting Managers, Leave Types
        Set<String> departmentIds = employees.stream()
                .map(EmployeeModel::getDepartment)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> designationIds = employees.stream()
                .map(EmployeeModel::getDesignation)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> reportingManagerIds = employees.stream()
                .map(EmployeeModel::getReportingManager)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> leavePolicyIds = employees.stream()
                .map(EmployeeModel::getLeavePolicyId)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toSet());

        // Get data maps
        Map<String, String> departmentNameMap = departmentService.getDepartmentsByIds(departmentIds).stream()
                .collect(Collectors.toMap(DepartmentModel::getDepartmentId, DepartmentModel::getName));

        Map<String, String> designationNameMap = designationService.getDesignationsByIds(designationIds).stream()
                .collect(Collectors.toMap(DesignationModel::getDesignationId, DesignationModel::getName));

        Map<String, EmployeeModel> managerMap = employeeRepository.findByEmployeeIdIn(reportingManagerIds).stream()
                .collect(Collectors.toMap(EmployeeModel::getEmployeeId, e -> e));

        Map<String, LeavePolicyModel> leavePolicyMap = leavePolicyService.getLeavePoliciesByIds(leavePolicyIds).stream()
                .collect(Collectors.toMap(LeavePolicyModel::getLeavePolicyId, lp -> lp));

        // Collect ALL unique leave type IDs
        Set<String> leaveTypeIds = leavePolicyMap.values().stream()
                .flatMap(lp -> lp.getLeaveAllocations().stream())
                .map(LeavePolicyModel.LeaveAllocation::getLeaveTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, String> leaveTypeNameMap = leaveTypeService.getLeaveTypesByIds(leaveTypeIds).stream()
                .collect(Collectors.toMap(LeaveTypeModel::getLeaveTypeId, LeaveTypeModel::getLeaveTypeName));

        // Final DTO mapping
        return employees.stream().map(employee -> {
            CompanyEmployeeDTO dto = new CompanyEmployeeDTO(employee);

            // Department name
            dto.setDepartmentName(
                    departmentNameMap.getOrDefault(employee.getDepartment(), employee.getDepartment())
            );

            // Designation name
            dto.setDesignationName(
                    designationNameMap.getOrDefault(employee.getDesignation(), employee.getDesignation())
            );

            // Reporting manager name
            if (employee.getReportingManager() != null && !employee.getReportingManager().isEmpty()) {
                dto.setReportingManagerName(
                        managerMap.getOrDefault(employee.getReportingManager(), new EmployeeModel())
                                .getName()
                );
            }

            // Leave policy & types
            if (employee.getLeavePolicyId() != null && !employee.getLeavePolicyId().isEmpty()) {
                LeavePolicyModel policy = leavePolicyMap.get(employee.getLeavePolicyId());
                if (policy != null) {
                    dto.setLeavePolicyName(policy.getName());

                    List<String> leaveTypeNames = new ArrayList<>();
                    List<String> leaveTypeIdList = new ArrayList<>();

                    for (LeavePolicyModel.LeaveAllocation alloc : policy.getLeaveAllocations()) {
                        String ltId = alloc.getLeaveTypeId();
                        String ltName = leaveTypeNameMap.get(ltId);
                        if (ltName != null) {
                            leaveTypeIdList.add(ltId);
                            leaveTypeNames.add(ltName);
                        }
                    }
                    dto.setLeaveTypeNames(leaveTypeNames);
                    dto.setLeaveTypeIds(leaveTypeIdList);
                }
            }

            // Keep assignTo list
            dto.setAssignTo(employee.getAssignTo());

            return dto;
        }).collect(Collectors.toList());
    }

    public EmployeeModel updateEmployeeRole(String employeeId, List<String> roles, String operation, String companyId) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles list cannot be null or empty");
        }

        // Get the employee
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // Get current roles or initialize
        Set<String> currentRoles = Optional.ofNullable(employee.getRoles())
                .map(HashSet::new)
                .orElseGet(HashSet::new);

        // Normalize operation
        String op = operation == null ? "" : operation.trim().toUpperCase();

        // Find all HR modules for the provided companyId
        List<ModuleModel> hrModules = moduleRepository.findAll().stream()
                .filter(m -> m.getCompanyId() != null && m.getCompanyId().equals(companyId)
                        && m.getModuleName() != null && m.getModuleName().toUpperCase().contains("HR"))
                .collect(Collectors.toList());

        switch (op) {
            case "ADD":
                currentRoles.addAll(roles);
                // If HRADMIN is being added, attach HR modules
                if (roles.contains("HRADMIN")) {
                    if (employee.getModuleIds() == null) {
                        employee.setModuleIds(new ArrayList<>());
                    }
                    for (ModuleModel hrModule : hrModules) {
                        if (!employee.getModuleIds().contains(hrModule.getModuleId())) {
                            employee.getModuleIds().add(hrModule.getModuleId());
                        }
                        // Also add this employee to the module's employeeIds if not present
                        if (hrModule.getEmployeeIds() == null) {
                            hrModule.setEmployeeIds(new ArrayList<>());
                        }
                        if (!hrModule.getEmployeeIds().contains(employeeId)) {
                            hrModule.getEmployeeIds().add(employeeId);
                            moduleRepository.save(hrModule);
                        }
                    }
                }
                break;
            case "REMOVE":
                currentRoles.removeAll(roles);
                // If HRADMIN is being removed, detach HR modules
                if (roles.contains("HRADMIN")) {
                    if (employee.getModuleIds() != null) {
                        for (ModuleModel hrModule : hrModules) {
                            employee.getModuleIds().remove(hrModule.getModuleId());
                            // Remove this employee from the module's employeeIds
                            if (hrModule.getEmployeeIds() != null) {
                                hrModule.getEmployeeIds().remove(employeeId);
                                // Save the module even if employeeIds is now empty
                                moduleRepository.save(hrModule);
                            }
                            // No error or exception if no admin remains
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid operation. Must be 'ADD' or 'REMOVE'");
        }

        employee.setRoles(currentRoles);
        return employeeRepository.save(employee);
    }

    public EmployeeAttendanceDetailsDTO getEmployeeAttendanceDetails(String employeeId) {
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        EmployeeAttendanceDetailsDTO dto = new EmployeeAttendanceDetailsDTO();
        dto.setName(employee.getName());
        dto.setEmployeeImgUrl(employee.getEmployeeImgUrl());
        dto.setJoiningDate(employee.getJoiningDate());
        dto.setWeeklyOffs(employee.getWeeklyOffs());

        return dto;
    }

    // Add this new method
    private void updateAllManagersAssignTo() {
        // Get all employees
        List<EmployeeModel> allEmployees = employeeRepository.findAll();

        // Create a map of reporting manager to their team members
        Map<String, List<String>> managerToTeamMap = new HashMap<>();

        // First, collect all team members for each manager based on reportingManager
        // field
        for (EmployeeModel employee : allEmployees) {
            if (employee.getReportingManager() != null && !employee.getReportingManager().isEmpty()) {
                managerToTeamMap.computeIfAbsent(employee.getReportingManager(), k -> new ArrayList<>())
                        .add(employee.getEmployeeId());
            }
        }

        // Then update each manager's assignTo list and add MANAGER role
        for (Map.Entry<String, List<String>> entry : managerToTeamMap.entrySet()) {
            String managerId = entry.getKey();
            List<String> teamMembers = entry.getValue();

            employeeRepository.findByEmployeeId(managerId).ifPresent(manager -> {
                boolean needsUpdate = false;

                // Update assignTo list
                if (!teamMembers.equals(manager.getAssignTo())) {
                    manager.setAssignTo(teamMembers);
                    needsUpdate = true;
                }

                // Add MANAGER role if not already present
                Set<String> roles = manager.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                if (!roles.contains("MANAGER")) {
                    roles.add("MANAGER");
                    manager.setRoles(roles);
                    needsUpdate = true;
                }

                // Save if any changes were made
                if (needsUpdate) {
                    employeeRepository.save(manager);
                }
            });
        }

        // Also handle direct assignTo relationships
        for (EmployeeModel employee : allEmployees) {
            if (employee.getAssignTo() != null && !employee.getAssignTo().isEmpty()) {
                boolean needsUpdate = false;

                // Add MANAGER role if not already present
                Set<String> roles = employee.getRoles();
                if (roles == null) {
                    roles = new HashSet<>();
                }
                if (!roles.contains("MANAGER")) {
                    roles.add("MANAGER");
                    employee.setRoles(roles);
                    needsUpdate = true;
                }

                // Save if any changes were made
                if (needsUpdate) {
                    employeeRepository.save(employee);
                }
            }
        }
    }

    // Add this new method
    private void updateManagerAssignTo(String managerId) {
        if (managerId == null || managerId.isEmpty()) {
            return;
        }

        // Get all employees who report to this manager
        List<EmployeeModel> teamMembers = employeeRepository.findByReportingManager(managerId);

        // Get the manager
        employeeRepository.findByEmployeeId(managerId).ifPresent(manager -> {
            boolean needsUpdate = false;

            // Create list of team member IDs
            List<String> teamMemberIds = teamMembers.stream()
                    .map(EmployeeModel::getEmployeeId)
                    .collect(Collectors.toList());

            // Update assignTo list if different
            if (!teamMemberIds.equals(manager.getAssignTo())) {
                manager.setAssignTo(teamMemberIds);
                needsUpdate = true;
            }

            // Handle MANAGER role based on assignTo list
            Set<String> roles = manager.getRoles();
            if (roles == null) {
                roles = new HashSet<>();
            }

            if (teamMemberIds.isEmpty()) {
                // Remove MANAGER role if assignTo is empty
                if (roles.contains("MANAGER")) {
                    roles.remove("MANAGER");
                    manager.setRoles(roles);
                    needsUpdate = true;
                }
            } else {
                // Add MANAGER role if not already present and assignTo is not empty
                if (!roles.contains("MANAGER")) {
                    roles.add("MANAGER");
                    manager.setRoles(roles);
                    needsUpdate = true;
                }
            }

            // Save if any changes were made
            if (needsUpdate) {
                employeeRepository.save(manager);
            }
        });
    }

    public EmployeeLeavePolicyWeeklyOffsDTO getEmployeeLeavePolicy(String employeeId) {
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        EmployeeLeavePolicyWeeklyOffsDTO dto = new EmployeeLeavePolicyWeeklyOffsDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeName(employee.getName());
        dto.setWeeklyOffs(employee.getWeeklyOffs());

        if (employee.getLeavePolicyId() != null) {
            try {
                LeavePolicyModel leavePolicy = leavePolicyService.getLeavePolicyById(employee.getLeavePolicyId());
                dto.setLeavePolicyId(leavePolicy.getLeavePolicyId());
                dto.setLeavePolicyName(leavePolicy.getName());
                dto.setLeaveAllocations(leavePolicy.getLeaveAllocations());
            } catch (Exception e) {
                // If leave policy not found, leave policy details will be null
            }
        }

        return dto;
    }
    public void addRoleToEmployee(String empId, String role) {
        EmployeeModel employee = employeeRepository.findByEmployeeId(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        employee.getRoles().add(role);
        employeeRepository.save(employee);
    }

    public void removeRoleFromEmployee(String empId, String role) {
        EmployeeModel employee = employeeRepository.findByEmployeeId(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        employee.getRoles().remove(role);
        employeeRepository.save(employee);
    }


}