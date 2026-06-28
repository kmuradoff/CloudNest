package me.kmuradoff.cloudnest.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {
    @NonNull
    String username;
    @NonNull
    String password;
}
