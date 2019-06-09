package com.example.locationtracker.Interface;

import java.util.List;

public interface IFirebaseLoadDone {

    void onFirebaseLoadUserNameDone(List<String> lastEmail);
    void onFirebaseLoadFailed(String message);
}
