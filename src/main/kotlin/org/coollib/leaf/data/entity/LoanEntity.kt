package org.coollib.leaf.data.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDate

@Entity
@Table(name = "loans")
open class LoanEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('loans_loanid_seq')")
    @Column(name = "loanid", nullable = false)
    open var id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bookid", nullable = false)
    open var book: BookEntity,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userid", nullable = false)
    open var user: UserEntity,

    @ColumnDefault("CURRENT_DATE")
    @Column(name = "borrowdate", nullable = false)
    open var borrowdate: LocalDate,

    @Column(name = "duedate", nullable = false)
    open var duedate: LocalDate,

    @Column(name = "returndate")
    open var returndate: LocalDate? = null,
)