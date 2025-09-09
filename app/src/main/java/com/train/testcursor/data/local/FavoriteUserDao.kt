package com.train.testcursor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteUserDao {
	@Query("SELECT * FROM favorites ORDER BY login ASC")
	fun observeFavorites(): Flow<List<FavoriteUserEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(entity: FavoriteUserEntity)

	@Delete
	suspend fun delete(entity: FavoriteUserEntity)

	@Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
	fun isFavorite(id: Int): Flow<Boolean>

	@Query("SELECT * FROM favorites WHERE id = :id LIMIT 1")
	suspend fun getById(id: Int): FavoriteUserEntity?
}



