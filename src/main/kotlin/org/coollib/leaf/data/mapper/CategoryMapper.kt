package org.coollib.leaf.data.mapper

import org.coollib.leaf.data.entity.CategoryEntity
import org.coollib.leaf.web.model.Category

fun CategoryEntity.toCategory() = Category(
    this.id,
    this.name,
    this.description
)
