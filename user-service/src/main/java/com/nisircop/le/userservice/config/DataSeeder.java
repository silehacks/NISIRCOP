package com.nisircop.le.userservice.config;

import com.nisircop.le.userservice.model.User;
import com.nisircop.le.userservice.model.UserRole;
import com.nisircop.le.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            createUsers();
        }
    }

    private void createUsers() {
        // Create SUPER_USER
        User superUser = new User();
        superUser.setUsername("admin");
        superUser.setPassword(passwordEncoder.encode("admin123"));
        superUser.setEmail("admin@nisircop.le");
        superUser.setRole(UserRole.SUPER_USER);
        superUser.setActive(true);
        User savedSuperUser = userRepository.save(superUser);

        // Create POLICE_STATION
        User policeStation = new User();
        policeStation.setUsername("station_commander");
        policeStation.setPassword(passwordEncoder.encode("admin123"));
        policeStation.setEmail("station@nisircop.le");
        policeStation.setRole(UserRole.POLICE_STATION);
        policeStation.setActive(true);
        policeStation.setCreatedBy(savedSuperUser.getId());
        User savedPoliceStation = userRepository.save(policeStation);

        // Create OFFICER
        User officer = new User();
        officer.setUsername("officer_001");
        officer.setPassword(passwordEncoder.encode("admin123"));
        officer.setEmail("officer001@nisircop.le");
        officer.setRole(UserRole.OFFICER);
        officer.setActive(true);
        officer.setCreatedBy(savedPoliceStation.getId());
        userRepository.save(officer);
    }
}