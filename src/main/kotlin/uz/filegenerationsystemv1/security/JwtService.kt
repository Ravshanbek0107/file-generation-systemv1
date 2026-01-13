package uz.filegenerationsystemv1.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import uz.filegenerationsystemv1.CustomUserDetailsService
import java.util.Date
import kotlin.text.startsWith
import kotlin.text.substring


@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long,) {
    private fun secretKey() = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey())
            .compact()
    }

    fun extractUsername(token: String): String? {
        return Jwts.parserBuilder().setSigningKey(secretKey()).build()
            .parseClaimsJws(token).body.subject
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder().setSigningKey(secretKey()).build().parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}




@Component
class JwtAuthentication(private val jwtService: JwtService, private val userDetailsService: CustomUserDetailsService) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            val username = jwtService.extractUsername(token)

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(username)
                if (jwtService.isTokenValid(token)) {
                    val authToken = UsernamePasswordAuthenticationToken(username, null, userDetails.authorities)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}