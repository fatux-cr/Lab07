package com.example.datossinmvvm

import androidx.room.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM Product ORDER BY id DESC")
    suspend fun getAll(): List<Product>

    @Insert
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("DELETE FROM Product WHERE id = :id")
    suspend fun deleteById(id: Int): Int
}
