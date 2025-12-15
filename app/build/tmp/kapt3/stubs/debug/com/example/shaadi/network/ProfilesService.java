package com.example.shaadi.network;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J*\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u00062\n\b\u0003\u0010\u0007\u001a\u0004\u0018\u00010\bH\u00a7@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\n"}, d2 = {"Lcom/example/shaadi/network/ProfilesService;", "", "getProfiles", "", "Lcom/example/shaadi/network/ProfileDto;", "select", "", "limit", "", "(Ljava/lang/String;Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface ProfilesService {
    
    @retrofit2.http.Headers(value = {"Accept: application/json"})
    @retrofit2.http.GET(value = "profiles")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getProfiles(@retrofit2.http.Query(value = "select")
    @org.jetbrains.annotations.NotNull()
    java.lang.String select, @retrofit2.http.Query(value = "limit")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.shaadi.network.ProfileDto>> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}