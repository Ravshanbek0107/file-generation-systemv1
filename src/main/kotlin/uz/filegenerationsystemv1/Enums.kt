package uz.filegenerationsystemv1

enum class UserRole{
    EMPLOYEE,ADMIN
}

enum class Status{
    ACTIVE,INACTIVE
}

enum class ErrorCode(val code: Int) {
    USER_NOT_FOUND(100),
    USER_ALREADY_EXISTS(101),


    ORGANIZATION_NOT_FOUND(200),
    ORGANIZATION_ALREADY_EXISTS(201),
    ORGANIZATION_HAS_EMPLOYEES(202),

    USERNAME_ALREADY_TAKEN(300),


}