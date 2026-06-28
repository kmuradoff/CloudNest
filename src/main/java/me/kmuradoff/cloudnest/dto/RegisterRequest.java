package me.kmuradoff.cloudnest.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterRequest {
    @NonNull
    String username;
    @NonNull
    String password;
    @NonNull
    String fullName;

}
