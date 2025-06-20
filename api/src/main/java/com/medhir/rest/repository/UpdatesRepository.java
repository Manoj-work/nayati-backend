package com.medhir.rest.repository;

import com.medhir.rest.model.updates.UpdatesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UpdatesRepository extends MongoRepository<UpdatesModel,String> {
    List<UpdatesModel> findByEmployeeIdOrderByTimestampDesc(String employeeId);
}
