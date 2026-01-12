package uz.filegenerationsystemv1

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
    private val organizationService: OrganizationService
) {

    @PostMapping
    fun create(@RequestBody request: OrganizationCreateRequest): OrganizationResponse {
        return organizationService.create(request)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: OrganizationUpdateRequest): OrganizationResponse {
        return organizationService.update(id, request)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): BaseMessage {
        organizationService.delete(id)
        return BaseMessage.OK
    }

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): OrganizationResponse {
        return organizationService.getOne(id)
    }

    @GetMapping
    fun getAll(): List<OrganizationResponse> {
        return organizationService.getAll()
    }
}

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun create(@RequestBody request: UserCreateRequest): UserResponse {
        return userService.create(request)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UserUpdateRequest): UserResponse {
        return userService.update(id, request)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): BaseMessage {
        userService.delete(id)
        return BaseMessage.OK
    }

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): UserResponse {
        return userService.getOne(id)
    }

    @GetMapping
    fun getAll(): List<UserResponse> {
        return userService.getAll()
    }
}