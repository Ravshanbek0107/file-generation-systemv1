package uz.filegenerationsystemv1

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.Locale

@ControllerAdvice
class ExceptionHandler(
    private val errorMessageSource: ResourceBundleMessageSource,
) {
    @ExceptionHandler(Throwable::class)
    fun handleOtherExceptions(exception: Throwable): ResponseEntity<Any> {
        when (exception) {
            is ShopAppException-> {

                return ResponseEntity
                    .badRequest()
                    .body(exception.getErrorMessage(errorMessageSource))
            }

            is AccessDeniedException, is AuthorizationDeniedException -> {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403 status
                    .body(BaseMessage(403, "Sizda ushbu amalni bajarish uchun ruxsat yo'q!"))
            }

            else -> {
                exception.printStackTrace()
                return ResponseEntity
                    .badRequest().body(
                        BaseMessage(100,
                            "Iltimos support bilan bog'laning")
                    )
            }
        }
    }

}



sealed class ShopAppException(message: String? = null) : RuntimeException(message) {
    abstract fun errorType(): ErrorCode
    protected open fun getErrorMessageArguments(): Array<Any?>? = null
    fun getErrorMessage(errorMessageSource: ResourceBundleMessageSource): BaseMessage {
        return BaseMessage(
            errorType().code,
            errorMessageSource.getMessage(
                errorType().toString(),
                getErrorMessageArguments(),
                Locale(LocaleContextHolder.getLocale().language)
            )
        )
    }
}

class OrganizationNotFoundException() : ShopAppException() {
    override fun errorType() = ErrorCode.ORGANIZATION_NOT_FOUND
}

class OrganizationAlreadyExistsException() : ShopAppException() {
    override fun errorType() = ErrorCode.ORGANIZATION_ALREADY_EXISTS
}

class OrganizationHasEmployeesException() : ShopAppException() {
    override fun errorType() = ErrorCode.ORGANIZATION_HAS_EMPLOYEES
}

class UserNotFoundException : ShopAppException() {
    override fun errorType() = ErrorCode.USER_NOT_FOUND
}

class UserAlreadyExistsException : ShopAppException() {
    override fun errorType() = ErrorCode.USER_ALREADY_EXISTS
}

class UsernameAlreadyTakenException : ShopAppException() {
    override fun errorType() = ErrorCode.USERNAME_ALREADY_TAKEN
}

