package com.iguerra94.weathernow.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.iguerra94.weathernow.db.daos.UserDao;
import com.iguerra94.weathernow.db.entities.User;

@Database(entities = { User.class }, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}