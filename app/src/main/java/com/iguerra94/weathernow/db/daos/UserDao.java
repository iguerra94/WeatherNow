package com.iguerra94.weathernow.db.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.iguerra94.weathernow.db.entities.User;

@Dao
public interface UserDao {
    @Insert
    Long insert(User user);

    @Update
    Integer update(User user);

    @Query("SELECT * FROM users WHERE email = :email")
    User findByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User findByEmailAndPassword(String email, String password);
}