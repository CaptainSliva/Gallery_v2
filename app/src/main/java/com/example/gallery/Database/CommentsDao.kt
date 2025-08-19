package com.example.gallery.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CommentsDao {

    @Query("SELECT * FROM comments")
    fun getAllComments(): List<Comment>

    @Query("SELECT * FROM comments WHERE LOWER(image_comment) LIKE LOWER('%' || :comment || '%')")
    fun findImageByNoRegisterComment(comment: String): List<Comment>

    @Query("SELECT * FROM comments WHERE image_comment LIKE '%' || :comment || '%' COLLATE BINARY")
    fun findImageByRegisterComment(comment: String): List<Comment>

    @Query("SELECT * FROM comments WHERE image_hash LIKE :hash")
    fun findImageByHash(hash: String): Comment

    @Query("UPDATE comments SET image_comment = :comment WHERE image_hash = :imageHash")
    fun replaceCommentByHash(imageHash: String, comment: String): Int

    @Query("DELETE FROM comments WHERE image_hash = :imageHash")
    fun deleteCommentByHash(vararg imageHash: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addComment(comment: Comment)

    @Delete
    fun delete(vararg comments: Comment)

    @Query("DELETE FROM comments")
    suspend fun clearComments()

}