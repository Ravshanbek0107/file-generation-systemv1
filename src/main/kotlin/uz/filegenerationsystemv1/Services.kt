package uz.filegenerationsystemv1

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface OrganizationService {
    fun create(request: OrganizationCreateRequest): OrganizationResponse
    fun update(id:Long, request: OrganizationUpdateRequest): OrganizationResponse
    fun delete(id:Long)
    fun getOne(id: Long): OrganizationResponse
    fun getAll(): List<OrganizationResponse>
}

@Service
class OrganizationServiceImpl(
    val organizationRepository: OrganizationRepository,
    val userRepository: UserRepository
):OrganizationService {

    @Transactional
    override fun create(request: OrganizationCreateRequest): OrganizationResponse {
        val org = organizationRepository.findByNameAndDeletedFalse(request.name)

        if(org != null) throw OrganizationAlreadyExistsException()

        val newOrg = Organization(
            name = request.name,
        )

        organizationRepository.save(newOrg)

        return OrganizationResponse.toResponse(newOrg)
    }

    @Transactional
    override fun update(id: Long, request: OrganizationUpdateRequest): OrganizationResponse {
        val org = organizationRepository.findByIdAndDeletedFalse(id) ?: throw OrganizationNotFoundException()

        request.name?.let { org.name = it }
        request.status?.let { org.status = it }

        organizationRepository.save(org)

        return OrganizationResponse.toResponse(org)
    }

    @Transactional
    override fun delete(id: Long) {
        val org = organizationRepository.findByIdAndDeletedFalse(id) ?: throw OrganizationNotFoundException()

        val hasEmployees = userRepository.findAllNotDeleted().any{
            it.organization.id == org.id
        }

        if (hasEmployees) throw OrganizationHasEmployeesException()

        organizationRepository.trash(id)
    }

    override fun getOne(id: Long): OrganizationResponse {
        val org = organizationRepository.findByIdAndDeletedFalse(id) ?: throw OrganizationNotFoundException()

        return OrganizationResponse.toResponse(org)
    }

    override fun getAll(): List<OrganizationResponse> {
        return organizationRepository.findAllNotDeleted().map{
            OrganizationResponse.toResponse(it)
        }
    }


}

interface UserService {
    fun create(request: UserCreateRequest): UserResponse
    fun update(id: Long, request: UserUpdateRequest): UserResponse
    fun delete(id: Long)
    fun getOne(id: Long): UserResponse
    fun getAll(): List<UserResponse>
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val encoder: PasswordEncoder // security uchun
) : UserService {

    @Transactional
    override fun create(request: UserCreateRequest): UserResponse {

        val user = userRepository.findAllNotDeleted().any{
            it.username == request.username
        }

        if (user) throw UserAlreadyExistsException()

        val organization = organizationRepository.findByIdAndDeletedFalse(request.organizationId) ?: throw OrganizationNotFoundException()

        if (organization.status == Status.INACTIVE) throw OrganizationNotFoundException()

        val newUser = User(
            username = request.username,
            fullname = request.fullname,
            password = encoder.encode(request.password),//security uchun
            role = UserRole.EMPLOYEE,
            organization = organization
        )

        userRepository.save(newUser)
        return UserResponse.toResponse(newUser)
    }

    @Transactional
    override fun update(id: Long, request: UserUpdateRequest): UserResponse {

        val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()

        request.username?.let { newUsername ->
            val exists = userRepository.findAllNotDeleted().any {
                it.username == newUsername && it.id != user.id
            }
            if (exists) throw UsernameAlreadyTakenException()

            user.username = newUsername
        }

        request.fullname?.let { user.fullname = it }

        request.password?.let {
            user.password = encoder.encode(it)
        }

        userRepository.save(user)
        return UserResponse.toResponse(user)
    }

    @Transactional
    override fun delete(id: Long) {
        val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()

        userRepository.trash(user.id!!)
    }

    override fun getOne(id: Long): UserResponse {
        val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()

        return UserResponse.toResponse(user)
    }

    override fun getAll(): List<UserResponse> {
        return userRepository.findAllNotDeleted().map{
            UserResponse.toResponse(it)
        }
    }

}
