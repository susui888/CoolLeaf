package org.coollib.leaf.data.repository

import org.coollib.leaf.data.entity.AppLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface AppLogRepository : JpaRepository<AppLogEntity, Long>, JpaSpecificationExecutor<AppLogEntity> {

}