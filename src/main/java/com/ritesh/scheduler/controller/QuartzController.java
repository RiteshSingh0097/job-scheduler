package com.ritesh.scheduler.controller;

import com.ritesh.scheduler.dto.QuartzJob;
import com.ritesh.scheduler.service.QuartzJobScheduler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuartzController {

    QuartzJobScheduler scheduler;

/*
curl --location 'http://localhost:8080/schedule-job' \
--header 'Content-Type: application/json' \
--data '{
    "key": "ritesh",
    "description" : "Test Job",
    "cronExpression" : "* * * * *"
}'
 */
    @PostMapping("/schedule-job")
    public ResponseEntity<String> scheduleJob(@RequestBody QuartzJob dto) {
        scheduler.replaceJob(dto);
        return new ResponseEntity<>("Job Created Successfully", HttpStatus.CREATED);
    }

    /*
    curl --location --request DELETE 'http://localhost:8080/stop-job' \
--header 'Content-Type: application/json' \
--data '{
    "key": "ritesh"
}'
     */
    @DeleteMapping("/stop-job")
    public ResponseEntity<String> stopJob(@RequestBody QuartzJob dto) {
        scheduler.deleteJob(dto);
        return new ResponseEntity<>("Job removed Successfully", HttpStatus.OK);
    }
}
