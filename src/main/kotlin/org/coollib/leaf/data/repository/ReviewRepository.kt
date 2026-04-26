package org.coollib.leaf.data.repository

import org.coollib.leaf.data.entity.ReviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<ReviewEntity, Long> {

    fun findByBookId(bookId: Int): List<ReviewEntity>

    fun findByUserIdAndBookId(userId: Int, bookId: Int): ReviewEntity?

    // 2. 获取特定用户的评价，并按创建时间降序排列
    //fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Review>

    // 3. 计算产品的平均评分（返回 Double? 以处理无评分的情况）
    //@Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId")
    //fun getAverageRating(@Param("productId") productId: Long): Double?

    // 4. 检查用户是否已对某产品发表过评价
    //fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    // 5. 查找评分大于等于指定分数的评价（例如：好评过滤）
    //fun findByRatingGreaterThanEqual(minRating: Int): List<Review>
}