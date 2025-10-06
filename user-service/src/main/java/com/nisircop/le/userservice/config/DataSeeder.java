package com.nisircop.le.userservice.config;

import com.nisircop.le.userservice.model.User;
import com.nisircop.le.userservice.model.UserProfile;
import com.nisircop.le.userservice.model.UserRole;
import com.nisircop.le.userservice.repository.UserRepository;
import com.nisircop.le.userservice.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final WKTReader wktReader = new WKTReader();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            createUsers();
        }
    }

    private void createUsers() {
        try {
            // Create SUPER_USER
            User superUser = new User();
            superUser.setUsername("admin");
            superUser.setPassword(passwordEncoder.encode("admin123"));
            superUser.setEmail("admin@nisircop.le");
            superUser.setRole(UserRole.SUPER_USER);
            superUser.setActive(true);
            User savedSuperUser = userRepository.save(superUser);

            // Create SUPER_USER profile with national boundary
            UserProfile superUserProfile = new UserProfile();
            superUserProfile.setFirstName("System");
            superUserProfile.setLastName("Administrator");
            superUserProfile.setPhone("+251-11-123-4567");
            superUserProfile.setBadgeNumber("ADMIN-001");
            // National boundary for Ethiopia (simplified)
            String nationalBoundary = "POLYGON((32.9975 3.3974, 47.9784 3.3974, 47.9784 18.0022, 32.9975 18.0022, 32.9975 3.3974))";
            superUserProfile.setBoundary(parseWKT(nationalBoundary));
            superUserProfile.setUser(savedSuperUser);
            userProfileRepository.save(superUserProfile);

            // Create POLICE_STATION
            User policeStation = new User();
            policeStation.setUsername("station_commander");
            policeStation.setPassword(passwordEncoder.encode("admin123"));
            policeStation.setEmail("station@nisircop.le");
            policeStation.setRole(UserRole.POLICE_STATION);
            policeStation.setActive(true);
            policeStation.setCreatedBy(savedSuperUser.getId());
            User savedPoliceStation = userRepository.save(policeStation);

            // Create POLICE_STATION profile with Addis Ababa boundary
            UserProfile stationProfile = new UserProfile();
            stationProfile.setFirstName("Station");
            stationProfile.setLastName("Commander");
            stationProfile.setPhone("+251-11-234-5678");
            stationProfile.setBadgeNumber("STATION-001");
            // Addis Ababa boundary (simplified)
            String addisBoundary = "POLYGON((38.7 8.9, 38.8 8.9, 38.8 9.1, 38.7 9.1, 38.7 8.9))";
            stationProfile.setBoundary(parseWKT(addisBoundary));
            stationProfile.setUser(savedPoliceStation);
            userProfileRepository.save(stationProfile);

            // Create OFFICER
            User officer = new User();
            officer.setUsername("officer_001");
            officer.setPassword(passwordEncoder.encode("admin123"));
            officer.setEmail("officer001@nisircop.le");
            officer.setRole(UserRole.OFFICER);
            officer.setActive(true);
            officer.setCreatedBy(savedPoliceStation.getId());
            User savedOfficer = userRepository.save(officer);

            // Create OFFICER profile (inherits station boundary)
            UserProfile officerProfile = new UserProfile();
            officerProfile.setFirstName("John");
            officerProfile.setLastName("Doe");
            officerProfile.setPhone("+251-11-345-6789");
            officerProfile.setBadgeNumber("OFF-001");
            // Officer inherits the same boundary as their station
            officerProfile.setBoundary(parseWKT(addisBoundary));
            officerProfile.setUser(savedOfficer);
            userProfileRepository.save(officerProfile);

        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse WKT boundary", e);
        }
    }

    private Geometry parseWKT(String wkt) throws ParseException {
        return wktReader.read(wkt);
    }
}