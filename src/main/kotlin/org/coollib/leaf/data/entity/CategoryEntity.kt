package org.coollib.leaf.data.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "categories")
class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('categories_categoryid_seq')")
    @Column(name = "categoryid", nullable = false)
    open var id: Int = 0,

    @Column(name = "categoryname", nullable = false)
    open var name: String = "Fiction",

    @Column(name = "description", length = Integer.MAX_VALUE)
    open var description: String? = null,

    @OneToMany(mappedBy = "categoryid")
    open var books: MutableSet<BookEntity> = mutableSetOf()
)