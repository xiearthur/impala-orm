package com.example.impala.example.mapper;

import com.example.impala.example.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM category WHERE category_id = #{categoryId}")
    Category getById(String categoryId);

    @Select("SELECT * FROM category")
    List<Category> findAll();

    @Select("SELECT * FROM category WHERE parent_id is null")
    List<Category> findTopCategories();

    @Select("SELECT * FROM category WHERE parent_id = #{parentId}")
    List<Category> findByParentId(String parentId);
}
