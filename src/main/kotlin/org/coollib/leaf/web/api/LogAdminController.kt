package org.coollib.leaf.web.api

import org.coollib.leaf.service.TelemetryService
import org.coollib.leaf.web.model.LogResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/telemetry")
class LogAdminController(private val telemetryService: TelemetryService) {

    /**
     * 获取系统日志列表（支持分页、级别过滤、TraceId追踪以及关键字模糊搜索）
     * 适合简历网站前端 Dashboard 异步刷新和条件筛选
     */
    @GetMapping("/logs")
    fun getSystemLogs(
        @RequestParam(required = false) level: String?,
        @RequestParam(required = false) traceId: String?,
        @RequestParam(required = false) searchTerm: String?,
        // 默认一页返回 50 条，并且严格按照时间戳倒序排列（最新日志在最前）
        @PageableDefault(size = 50, sort = ["timestamp"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<LogResponse>> {

        val logs = telemetryService.searchLogs(level, traceId, searchTerm, pageable)
        return ResponseEntity.ok(logs)
    }
}