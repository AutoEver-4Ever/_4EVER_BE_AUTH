package org.ever._4ever_be_auth.user.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    // <MODULE_NAME>_<ROLE_NAME>으로 구성되어 있는 사용자의 역할임
    // 사용자에게는 각 역할이 부여되며 역할마다 권한이 부여됨
    // 내부 사용자(USER, ADMIN)
    MM_USER(UserType.INTERNAL, Collections.emptySet()),
    MM_ADMIN(UserType.INTERNAL, Collections.emptySet()),
    SD_USER(UserType.INTERNAL, Collections.emptySet()),
    SD_ADMIN(UserType.INTERNAL, Collections.emptySet()),
    IM_USER(UserType.INTERNAL, Collections.emptySet()),
    IM_ADMIN(UserType.INTERNAL, Collections.emptySet()),
    FCM_USER(UserType.INTERNAL, Collections.emptySet()),
    FCM_ADMIN(UserType.INTERNAL, Collections.emptySet()),
    HRM_USER(UserType.INTERNAL, Set.of(Permission.CREATE_USER)),
    HRM_ADMIN(UserType.INTERNAL, Set.of(Permission.CREATE_USER)),
    PP_USER(UserType.INTERNAL, Collections.emptySet()),
    PP_ADMIN(UserType.INTERNAL, Collections.emptySet()),

    // 내부 관리자
    ALL_ADMIN(UserType.INTERNAL, Set.of(Permission.CREATE_USER)),       // 전사 관리자
    CEO_ADMIN(UserType.INTERNAL, Set.of(Permission.CREATE_USER)),       // 최고 경영자
    COO_ADMIN(UserType.INTERNAL, Collections.emptySet()),       // 생산(PP) 총괄자
    CPO_ADMIN(UserType.INTERNAL, Collections.emptySet()),       // 구매(MM) 총괄자
    CSCO_ADMIN(UserType.INTERNAL, Collections.emptySet()),      // 공급망(IM, MM, SD) 총괄자
    CHRO_ADMIN(UserType.INTERNAL, Set.of(Permission.CREATE_USER)),      // 인사(HRM) 총괄자
    CFO_ADMIN(UserType.INTERNAL, Collections.emptySet()),       // 재무(FCM) 총괄자
    CMO_ADMIN(UserType.INTERNAL, Collections.emptySet()),       // 영업(SD) 총괄자

    // 고객사
    CUSTOMER_USER(UserType.CUSTOMER, Collections.emptySet()),
    CUSTOMER_ADMIN(UserType.CUSTOMER, Collections.emptySet()),

    // 공급사
    SUPPLIER_USER(UserType.SUPPLIER, Collections.emptySet()),
    SUPPLIER_ADMIN(UserType.SUPPLIER, Collections.emptySet());

    private final UserType type;
    private final Set<Permission> permissions;
}
