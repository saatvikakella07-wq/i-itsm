package com.itsm.config;

import com.itsm.model.*;
import com.itsm.model.enums.Priority;
import com.itsm.model.enums.Role;
import com.itsm.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Automatically seeds the database with essential SLA policies, IT services,
 * and test accounts on application startup if they are not already present.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;
    private final ItServiceRepository serviceRepository;
    private final SlaPolicyRepository slaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           TechnicianRepository technicianRepository,
                           ItServiceRepository serviceRepository,
                           SlaPolicyRepository slaRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.serviceRepository = serviceRepository;
        this.slaRepository = slaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedSlaPolicies();
        seedItServices();
        seedUsers();
    }

    private void seedSlaPolicies() {
        if (slaRepository.count() == 0) {
            log.info("Seeding default SLA policies...");
            slaRepository.save(new SlaPolicy(Priority.P1, 5, 30));
            slaRepository.save(new SlaPolicy(Priority.P2, 15, 120));
            slaRepository.save(new SlaPolicy(Priority.P3, 60, 480));
            slaRepository.save(new SlaPolicy(Priority.P4, 240, 1440));
        }
    }

    private void seedItServices() {
        if (serviceRepository.count() == 0) {
            log.info("Seeding IT services catalog...");
            serviceRepository.save(new ItService("Payment Application", "Core customer transaction and payment gateway platform", 3));
            serviceRepository.save(new ItService("Corporate Email & Communication", "Enterprise mail server and instant messaging services", 2));
            serviceRepository.save(new ItService("Corporate Network & VPN", "Primary LAN/WAN routing, firewall, and remote VPN access", 3));
            serviceRepository.save(new ItService("Employee Portal / Intranet", "Internal HR, leave tracking, and document intranet system", 1));
            serviceRepository.save(new ItService("Cloud Database Cluster", "Production PostgreSQL & Redis database cluster infrastructure", 3));
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            log.info("Seeding initial user accounts...");

            // 1. System Administrator (admin@org.com / admin123)
            User admin = new User("System Administrator", "admin@org.com", passwordEncoder.encode("admin123"), Role.ADMIN);
            userRepository.save(admin);

            // 2. Bob NetworkTech (tech@org.com / tech123)
            User techBob = new User("Bob NetworkTech", "tech@org.com", passwordEncoder.encode("tech123"), Role.TECHNICIAN);
            User savedBob = userRepository.save(techBob);
            Technician techRecordBob = new Technician(savedBob, "NETWORK,HARDWARE,SECURITY");
            technicianRepository.save(techRecordBob);

            // 3. Sarah CloudTech (tech2@org.com / tech123)
            User techSarah = new User("Sarah CloudTech", "tech2@org.com", passwordEncoder.encode("tech123"), Role.TECHNICIAN);
            User savedSarah = userRepository.save(techSarah);
            Technician techRecordSarah = new Technician(savedSarah, "APPLICATION,DATABASE,CLOUD,SOFTWARE,AUTH,STORAGE");
            technicianRepository.save(techRecordSarah);

            // 4. Alice Employee (emp@org.com / emp123)
            User empAlice = new User("Alice Employee", "emp@org.com", passwordEncoder.encode("emp123"), Role.EMPLOYEE);
            userRepository.save(empAlice);

            log.info("Seed data successfully loaded.");
        }
    }
}
