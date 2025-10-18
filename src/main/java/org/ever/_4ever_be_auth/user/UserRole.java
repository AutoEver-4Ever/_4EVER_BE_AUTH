package org.ever._4ever_be_auth.user;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    // <MODULE_NAME>_<ROLE_NAME>으로 구성되어 있는 사용자의 역할임
    // 사용자에게는 각 역할이 부여되며 역할마다 권한이 부여됨
    // 내부 사용자(USER, ADMIN)
    MM_USER(UserType.INTERNAL),
    MM_ADMIN(UserType.INTERNAL),
    SD_USER(UserType.INTERNAL),
    SD_ADMIN(UserType.INTERNAL),
    IM_USER(UserType.INTERNAL),
    IM_ADMIN(UserType.INTERNAL),
    FCM_USER(UserType.INTERNAL),
    FCM_ADMIN(UserType.INTERNAL),
    HRM_USER(UserType.INTERNAL),
    HRM_ADMIN(UserType.INTERNAL),
    PP_USER(UserType.INTERNAL),
    PP_ADMIN(UserType.INTERNAL),

    // 내부 관리자
    ALL_ADMIN(UserType.INTERNAL),       // 전사 관리자
    CEO_ADMIN(UserType.INTERNAL),       // 최고 경영자
    COO_ADMIN(UserType.INTERNAL),       // 생산(PP) 총괄자
    CPO_ADMIN(UserType.INTERNAL),       // 구매(MM) 총괄자
    CSCO_ADMIN(UserType.INTERNAL),      // 공급망(IM, MM, SD) 총괄자
    CHRO_ADMIN(UserType.INTERNAL),      // 인사(HRM) 총괄자
    CFO_ADMIN(UserType.INTERNAL),       // 재무(FCM) 총괄자
    CMO_ADMIN(UserType.INTERNAL),       // 영업(SD) 총괄자

    // 고객사
    CUSTOMER_USER(UserType.CUSTOMER),
    CUSTOMER_ADMIN(UserType.CUSTOMER),

    // 공급사
    SUPPLIER_USER(UserType.SUPPLIER),
    SUPPLIER_ADMIN(UserType.SUPPLIER);

    private final UserType type;
}
