package com.train.github.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
	entities = [FavoriteUserEntity::class],
	version = 2,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun favoriteUserDao(): FavoriteUserDao
}



