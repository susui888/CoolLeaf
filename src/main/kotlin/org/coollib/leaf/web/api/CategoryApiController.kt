package org.coollib.leaf.web.api

import org.coollib.leaf.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/category")
class CategoryApiController(private val categoryService: CategoryService) {

    @GetMapping
    fun getCategory() =
        categoryService.getAllCategory()


    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Int) =
        categoryService.getCategoryById(id)

}