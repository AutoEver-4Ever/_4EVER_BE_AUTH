package org.ever._4ever_be_auth.bootstrap;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    private static final String INITIAL_PASSWORD_RAW = "password";

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("[Seed] Skipped (app.seed.enabled=false)");
            return;
        }

        log.info("[Seed] Start seeding modules/roles/permissions/users...");

        // 1) Modules (MM, IM, PP, SD, FCM, HRM)
        Map<String, UUID> moduleIds = ensureModules();

        // 2) Permissions (HRM only for now)
        Map<String, UUID> permissionIds = ensureHrmPermissions(moduleIds.get("HRM"));

        // 3) Roles
        Map<String, UUID> roleIds = ensureRoles();

        // 4) Role-Permissions mapping (HRM only + ALL_ADMIN minimal)
        ensureRolePermissions(roleIds, permissionIds);

        // 5) Users (17 accounts) & User-Roles mapping
        ensureUsers(roleIds);

        log.info("[Seed] Completed.");
    }

    private Map<String, UUID> ensureModules() {
        Map<String, String> modules = new HashMap<>();
        modules.put("MM", "Materials Management");
        modules.put("IM", "Inventory Management");
        modules.put("PP", "Production Planning");
        modules.put("SD", "Sales & Distribution");
        modules.put("FCM", "Financial & Cost Management");
        modules.put("HRM", "Human Resource Management");

        Map<String, UUID> ids = new HashMap<>();
        for (Map.Entry<String, String> e : modules.entrySet()) {
            String code = e.getKey();
            String name = e.getValue();
            UUID id = UuidCreator.getTimeOrdered();
            jdbcTemplate.update(
                "INSERT INTO modules(module_id, code, name, description) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT (code) DO NOTHING",
                id, code, name, name
            );
        }
        jdbcTemplate.query("SELECT module_id, code FROM modules", rs -> {
            ids.put(rs.getString("code"), UUID.fromString(rs.getString("module_id")));
        });
        log.info("[Seed] Modules ensured: {}", ids.keySet());
        return ids;
    }

    private Map<String, UUID> ensureHrmPermissions(UUID hrmModuleId) {
        Map<String, UUID> ids = new HashMap<>();
        if (hrmModuleId == null) return ids;

        // permissions: erp.hrm.read | write | admin
        insertPermission(hrmModuleId, "erp.hrm.read", "READ", null);
        insertPermission(hrmModuleId, "erp.hrm.write", "WRITE", null);
        insertPermission(hrmModuleId, "erp.hrm.admin", "ADMIN", null);

        jdbcTemplate.query(
            "SELECT permission_id, code FROM permissions WHERE module_id = ?",
            rs -> ids.put(rs.getString("code"), UUID.fromString(rs.getString("permission_id"))),
            hrmModuleId
        );
        log.info("[Seed] HRM permissions ensured: {}", ids.keySet());
        return ids;
    }

    private void insertPermission(UUID moduleId, String code, String action, String resource) {
        jdbcTemplate.update(
            "INSERT INTO permissions(permission_id, module_id, code, action, resource) " +
                "VALUES (?, ?, ?, ?, ?) ON CONFLICT (module_id, code) DO NOTHING",
            UuidCreator.getTimeOrdered(), moduleId, code, action, resource
        );
    }

    private Map<String, UUID> ensureRoles() {
        // Module roles (USER/ADMIN) for MM, IM, PP, SD, FCM, HRM
        List<String> modules = List.of("MM","IM","PP","SD","FCM","HRM");
        for (String m : modules) {
            insertRole(m + "_USER", m + " User");
            insertRole(m + "_ADMIN", m + " Admin");
        }
        // Global + External
        insertRole("ALL_ADMIN", "Global Admin");
        insertRole("CUSTOMER_USER", "Customer User");
        insertRole("CUSTOMER_ADMIN", "Customer Admin");
        insertRole("SUPPLIER_USER", "Supplier User");
        insertRole("SUPPLIER_ADMIN", "Supplier Admin");

        Map<String, UUID> ids = new HashMap<>();
        jdbcTemplate.query("SELECT role_id, code FROM roles", rs -> {
            ids.put(rs.getString("code"), UUID.fromString(rs.getString("role_id")));
        });
        log.info("[Seed] Roles ensured: {}", ids.keySet());
        return ids;
    }

    private void insertRole(String code, String name) {
        jdbcTemplate.update(
            "INSERT INTO roles(role_id, code, name, description) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (code) DO NOTHING",
            UuidCreator.getTimeOrdered(), code, name, name
        );
    }

    private void ensureRolePermissions(Map<String, UUID> roleIds, Map<String, UUID> permIds) {
        // HRM mappings only
        UUID hrmRead = permIds.get("erp.hrm.read");
        UUID hrmWrite = permIds.get("erp.hrm.write");
        UUID hrmAdmin = permIds.get("erp.hrm.admin");
        if (hrmRead == null || hrmWrite == null || hrmAdmin == null) return;

        // HRM_USER → read
        linkRolePerm(roleIds.get("HRM_USER"), hrmRead);
        // HRM_ADMIN → read/write/admin
        linkRolePerm(roleIds.get("HRM_ADMIN"), hrmRead);
        linkRolePerm(roleIds.get("HRM_ADMIN"), hrmWrite);
        linkRolePerm(roleIds.get("HRM_ADMIN"), hrmAdmin);
        // ALL_ADMIN → at least HRM admin for now
        linkRolePerm(roleIds.get("ALL_ADMIN"), hrmAdmin);
    }

    private void linkRolePerm(UUID roleId, UUID permId) {
        if (roleId == null || permId == null) return;
        jdbcTemplate.update(
            "INSERT INTO role_permissions(role_id, permission_id) VALUES (?, ?) " +
                "ON CONFLICT DO NOTHING",
            roleId, permId
        );
    }

    private void ensureUsers(Map<String, UUID> roleIds) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode(INITIAL_PASSWORD_RAW);
        OffsetDateTime now = OffsetDateTime.now();

        // 17 users
        createUserWithRole("mmUser@everp.com", "mmUser", "INTERNAL", roleIds.get("MM_USER"), hashed, now);
        createUserWithRole("mmAdmin@everp.com", "mmAdmin", "INTERNAL", roleIds.get("MM_ADMIN"), hashed, now);
        createUserWithRole("imUser@everp.com", "imUser", "INTERNAL", roleIds.get("IM_USER"), hashed, now);
        createUserWithRole("imAdmin@everp.com", "imAdmin", "INTERNAL", roleIds.get("IM_ADMIN"), hashed, now);
        createUserWithRole("ppUser@everp.com", "ppUser", "INTERNAL", roleIds.get("PP_USER"), hashed, now);
        createUserWithRole("ppAdmin@everp.com", "ppAdmin", "INTERNAL", roleIds.get("PP_ADMIN"), hashed, now);
        createUserWithRole("sdUser@everp.com", "sdUser", "INTERNAL", roleIds.get("SD_USER"), hashed, now);
        createUserWithRole("sdAdmin@everp.com", "sdAdmin", "INTERNAL", roleIds.get("SD_ADMIN"), hashed, now);
        createUserWithRole("fcmUser@everp.com", "fcmUser", "INTERNAL", roleIds.get("FCM_USER"), hashed, now);
        createUserWithRole("fcmAdmin@everp.com", "fcmAdmin", "INTERNAL", roleIds.get("FCM_ADMIN"), hashed, now);
        createUserWithRole("hrmUser@everp.com", "hrmUser", "INTERNAL", roleIds.get("HRM_USER"), hashed, now);
        createUserWithRole("hrmAdmin@everp.com", "hrmAdmin", "INTERNAL", roleIds.get("HRM_ADMIN"), hashed, now);
        createUserWithRole("allAdmin@everp.com", "allAdmin", "INTERNAL", roleIds.get("ALL_ADMIN"), hashed, now);
        createUserWithRole("customerUser@everp.com", "customerUser", "CUSTOMER", roleIds.get("CUSTOMER_USER"), hashed, now);
        createUserWithRole("customerAdmin@everp.com", "customerAdmin", "CUSTOMER", roleIds.get("CUSTOMER_ADMIN"), hashed, now);
        createUserWithRole("supplierUser@everp.com", "supplierUser", "SUPPLIER", roleIds.get("SUPPLIER_USER"), hashed, now);
        createUserWithRole("supplierAdmin@everp.com", "supplierAdmin", "SUPPLIER", roleIds.get("SUPPLIER_ADMIN"), hashed, now);
    }

    private void createUserWithRole(
        String email, String username, String userType, UUID roleId,
        String hashedPassword, OffsetDateTime now
    ) {
        if (roleId == null) {
            log.warn("[Seed] Role not found for user {}. Skipping.", email);
            return;
        }
        // Insert user if not exists
        jdbcTemplate.update(
            "INSERT INTO users(user_id, login_email, contact_email, username, password, user_type, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE', ?, ?) " +
                "ON CONFLICT (login_email) DO NOTHING",
            UuidCreator.getTimeOrdered(), email, email, username, hashedPassword, userType, now, now
        );
        // Fetch user_id
        UUID userId = jdbcTemplate.query(
            "SELECT user_id FROM users WHERE login_email = ?",
            rs -> rs.next() ? UUID.fromString(rs.getString(1)) : null,
            email
        );
        if (userId == null) return;

        // Link user-role
        jdbcTemplate.update(
            "INSERT INTO user_roles(user_id, role_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
            userId, roleId
        );
    }
}

