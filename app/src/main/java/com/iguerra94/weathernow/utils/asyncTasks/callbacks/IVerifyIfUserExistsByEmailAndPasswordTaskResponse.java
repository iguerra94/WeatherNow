package com.iguerra94.weathernow.utils.asyncTasks.callbacks;

import com.iguerra94.weathernow.db.entities.User;

public interface IVerifyIfUserExistsByEmailAndPasswordTaskResponse {
    void onVerifyIfUserExistsByEmailAndPasswordTaskDone(User userFound);
}