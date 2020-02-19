package com.iguerra94.weathernow.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    public int id;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "profile_image_uri")
    public String profileImageUri;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    @NonNull
    @Override
    public String toString() {
        return "User: { id: " + getId() +
                ", firstName: " + getFirstName() +
                ", lastName: " + getLastName() +
                ", email: " + getEmail() +
                ", Password: " + getPassword() +
                ", ProfileImageUri: " + getProfileImageUri() +
                " }";
    }
}

