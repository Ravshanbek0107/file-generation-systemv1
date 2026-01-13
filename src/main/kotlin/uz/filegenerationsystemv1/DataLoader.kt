package uz.filegenerationsystemv1

import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataLoader(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        if (organizationRepository.count() == 0L) {


            val org = Organization(
                name = "IT Academy",
                status = Status.ACTIVE
            )
            val savedOrg = organizationRepository.save(org)


            val admin = User(
                username = "admin",
                fullname = "Asosiy Admin",
                password = passwordEncoder.encode("admin123"),
                role = UserRole.ADMIN,
                organization = savedOrg
            )

            val user = User(
                username = "user1",
                fullname = "Oddiy Foydalanuvchi",
                password = passwordEncoder.encode("user123"),
                role = UserRole.EMPLOYEE,
                organization = savedOrg
            )

            userRepository.saveAll(listOf(admin, user))

            println("--- Test ma'lumotlari muvaffaqiyatli yuklandi ---")
            println("Admin: username: admin, password: admin123")
            println("User: username: user1, password: user123")
        }
    }
}