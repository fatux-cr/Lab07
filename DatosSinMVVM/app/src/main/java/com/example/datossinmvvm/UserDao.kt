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

    @Query("DELETE FROM User WHERE uid = (SELECT uid FROM User ORDER BY uid DESC LIMIT 1)")
    suspend fun deleteLast(): Int

}
