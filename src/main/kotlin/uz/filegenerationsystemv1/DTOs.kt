package uz.filegenerationsystemv1

data class BaseMessage(val code: Int? = null, val message: String? = null) {
    companion object {
        var OK = BaseMessage(0, "OK")
    }
}

data class OrganizationCreateRequest(
    val name: String,
)

data class OrganizationUpdateRequest(
    val name: String?,
    val status: Status?
)

data class OrganizationResponse(
    val id: Long,
    val name: String,
    val status: Status,
){

    companion object{
        fun toResponse(organization: Organization) = OrganizationResponse(
            organization.id!!,
            organization.name,
            organization.status
        )
    }

}



data class UserCreateRequest(
    val username: String,
    val fullname: String,
    val password: String,
    val organizationId: Long,
)

data class UserUpdateRequest(
    val username: String?,
    val fullname: String?,
    val password: String?
)


data class UserResponse(
    val id: Long,
    val username: String,
    val fullname: String,
    val role: UserRole,
    val organizationId: Long,
) {
    companion object {
        fun toResponse(user: User) = UserResponse(
            id = user.id!!,
            username = user.username,
            fullname = user.fullname,
            role = user.role,
            organizationId = user.organization.id!!
        )
    }
}



data class LoginRequest(
    val username: String,
    val password: String
)