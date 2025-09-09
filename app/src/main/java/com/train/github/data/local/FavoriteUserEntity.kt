package com.train.github.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteUserEntity(
	@PrimaryKey val id: Int,
	val login: String,
	val avatarUrl: String
)


