package uz.filegenerationsystemv1

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @CreatedBy var createdBy: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
)


@Entity
@Table(name = "organizations")
class Organization(
    @Column(nullable = false,unique = true)var name: String,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var status: Status = Status.ACTIVE,
):BaseEntity()

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false,unique = true) var username : String,
    @Column(nullable = false) var fullname : String,
    @Column(nullable = false) var password : String,
    @Enumerated(EnumType.STRING) var role : UserRole,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "organization_id", nullable = false) var organization: Organization,

): BaseEntity()