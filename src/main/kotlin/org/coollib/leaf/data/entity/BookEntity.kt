package org.coollib.leaf.data.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "books")
open class BookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('books_bookid_seq')")
    @Column(name = "bookid", nullable = false)
    open var id: Int = 0,

    @Column(name = "isbn", nullable = false, length = 20)
    open var isbn: String,

    @Column(name = "title", nullable = false)
    open var title: String,

    @Column(name = "author", nullable = false)
    open var author: String,

    @Column(name = "publicationyear")
    open var year: Int? = null,

    @Column(name = "publisher")
    open var publisher: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryid")
    open var categoryid: CategoryEntity? = null,

    @ColumnDefault("1")
    @Column(name = "totalcopies", nullable = false)
    open var totalcopies: Int = 1,

    @ColumnDefault("1")
    @Column(name = "availablecopies", nullable = false)
    open var availablecopies: Int = 1,

    // @OneToMany(mappedBy = "book")
    //open var loans: MutableSet<LoanEntity> = mutableSetOf(),

    @ColumnDefault("description")
    open var description: String? = null
)