package org.coollib.leaf.service

import org.coollib.leaf.data.entity.AppLogEntity
import org.coollib.leaf.data.repository.AppLogRepository
import org.coollib.leaf.web.model.LogResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.criteria.Predicate

@Service
class TelemetryService(private val appLogRepository: AppLogRepository) {

    @Transactional(readOnly = true)
    fun searchLogs(
        level: String?,
        traceId: String?,
        searchTerm: String?,
        pageable: Pageable
    ): Page<LogResponse> {

        val spec = Specification<AppLogEntity> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            if (!level.isNullOrBlank()) {
                predicates.add(cb.equal(root.get<String>("level"), level.uppercase()))
            }

            if (!traceId.isNullOrBlank()) {
                predicates.add(cb.equal(root.get<String>("traceId"), traceId))
            }

            if (!searchTerm.isNullOrBlank()) {
                val likeTerm = "%${searchTerm.lowercase()}%"
                val messagePredicate = cb.like(cb.lower(root.get("message")), likeTerm)
                // 🎯 对齐新表的 tag 字段进行模糊搜索
                val tagPredicate = cb.like(cb.lower(root.get("tag")), likeTerm)
                predicates.add(cb.or(messagePredicate, tagPredicate))
            }

            cb.and(*predicates.toTypedArray())
        }

        return appLogRepository.findAll(spec, pageable).map { entity ->
            LogResponse(
                id = entity.id!!,
                timestamp = entity.timestamp,
                environment = entity.environment,
                platform = entity.platform,
                level = entity.level,
                traceId = entity.traceId,
                tag = entity.tag,
                message = entity.message,
                stackTrace = entity.stackTrace
            )
        }
    }
}