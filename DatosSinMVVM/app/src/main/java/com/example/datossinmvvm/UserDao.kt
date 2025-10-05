package com.example.datossinmvvm
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>


    @Insert
    suspend fun insert(user: User)
}
