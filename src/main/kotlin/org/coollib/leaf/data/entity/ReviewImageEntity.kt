package org.coollib.leaf.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.Instant

@Entity
@Table(name = "review_images")
open class ReviewImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_images_id_gen")
    @SequenceGenerator(name = "review_images_id_gen", sequenceName = "review_images_imageid_seq", allocationSize = 1)
    @Column(name = "imageid", nullable = false)
    open var id: Int? = null

    @Column(name = "reviewid", nullable = false)
    open var reviewid: Int? = null

    @Column(name = "image_url", nullable = false, length = Integer.MAX_VALUE)
    open var imageUrl: String? = null

    @Column(name = "width", nullable = false)
    open var width: Int? = null

    @Column(name = "height", nullable = false)
    open var height: Int? = null

    @ColumnDefault("0")
    @Column(name = "sort_order")
    open var sortOrder: Short? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdat", nullable = false)
    open var createdat: Instant? = null

}