package com.example.shaadi.network;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001:\u0003\u0015\u0016\u0017J\"\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u0006\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\r\u001a\u00020\u000e2\b\b\u0001\u0010\u0006\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0018\u0010\u0011\u001a\u00020\u00122\b\b\u0001\u0010\u0006\u001a\u00020\u0013H\u00a7@\u00a2\u0006\u0002\u0010\u0014\u00a8\u0006\u0018"}, d2 = {"Lcom/example/shaadi/network/SupabaseAuthService;", "", "passwordGrant", "Lcom/example/shaadi/network/SupabaseTokenResponse;", "grantType", "", "body", "Lcom/example/shaadi/network/SupabaseTokenRequest;", "(Ljava/lang/String;Lcom/example/shaadi/network/SupabaseTokenRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestOtp", "", "Lcom/example/shaadi/network/SupabaseAuthService$OtpRequest;", "(Lcom/example/shaadi/network/SupabaseAuthService$OtpRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signup", "Lcom/example/shaadi/network/SupabaseSignupResponse;", "Lcom/example/shaadi/network/SupabaseSignupRequest;", "(Lcom/example/shaadi/network/SupabaseSignupRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyOtp", "Lcom/example/shaadi/network/SupabaseAuthService$VerifyResponse;", "Lcom/example/shaadi/network/SupabaseAuthService$VerifyRequest;", "(Lcom/example/shaadi/network/SupabaseAuthService$VerifyRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "OtpRequest", "VerifyRequest", "VerifyResponse", "app_debug"})
public abstract interface SupabaseAuthService {
    
    @retrofit2.http.POST(value = "signup")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object signup(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.example.shaadi.network.SupabaseSignupRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.shaadi.network.SupabaseSignupResponse> $completion);
    
    @retrofit2.http.POST(value = "token")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object passwordGrant(@retrofit2.http.Query(value = "grant_type")
    @org.jetbrains.annotations.NotNull()
    java.lang.String grantType, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.example.shaadi.network.SupabaseTokenRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.shaadi.network.SupabaseTokenResponse> $completion);
    
    @retrofit2.http.POST(value = "otp")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object requestOtp(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.example.shaadi.network.SupabaseAuthService.OtpRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @retrofit2.http.POST(value = "verify")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyOtp(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.example.shaadi.network.SupabaseAuthService.VerifyRequest body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.shaadi.network.SupabaseAuthService.VerifyResponse> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
    
    @com.squareup.moshi.JsonClass(generateAdapter = true)
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0003\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J\'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00062\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0016"}, d2 = {"Lcom/example/shaadi/network/SupabaseAuthService$OtpRequest;", "", "phone", "", "channel", "createUser", "", "(Ljava/lang/String;Ljava/lang/String;Z)V", "getChannel", "()Ljava/lang/String;", "getCreateUser", "()Z", "getPhone", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
    public static final class OtpRequest {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String phone = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String channel = null;
        private final boolean createUser = false;
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        public final boolean component3() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.shaadi.network.SupabaseAuthService.OtpRequest copy(@org.jetbrains.annotations.NotNull()
        java.lang.String phone, @org.jetbrains.annotations.NotNull()
        java.lang.String channel, @com.squareup.moshi.Json(name = "create_user")
        boolean createUser) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
        
        public OtpRequest(@org.jetbrains.annotations.NotNull()
        java.lang.String phone, @org.jetbrains.annotations.NotNull()
        java.lang.String channel, @com.squareup.moshi.Json(name = "create_user")
        boolean createUser) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhone() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getChannel() {
            return null;
        }
        
        public final boolean getCreateUser() {
            return false;
        }
    }
    
    @com.squareup.moshi.JsonClass(generateAdapter = true)
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0015"}, d2 = {"Lcom/example/shaadi/network/SupabaseAuthService$VerifyRequest;", "", "phone", "", "token", "type", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getPhone", "()Ljava/lang/String;", "getToken", "getType", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class VerifyRequest {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String phone = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String token = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String type = null;
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.shaadi.network.SupabaseAuthService.VerifyRequest copy(@org.jetbrains.annotations.NotNull()
        java.lang.String phone, @org.jetbrains.annotations.NotNull()
        java.lang.String token, @org.jetbrains.annotations.NotNull()
        java.lang.String type) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
        
        public VerifyRequest(@org.jetbrains.annotations.NotNull()
        java.lang.String phone, @org.jetbrains.annotations.NotNull()
        java.lang.String token, @org.jetbrains.annotations.NotNull()
        java.lang.String type) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhone() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getToken() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getType() {
            return null;
        }
    }
    
    @com.squareup.moshi.JsonClass(generateAdapter = true)
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\n\b\u0001\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0001\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0005J\u000b\u0010\t\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\n\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J!\u0010\u000b\u001a\u00020\u00002\n\b\u0003\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0003\u0010\u0004\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/example/shaadi/network/SupabaseAuthService$VerifyResponse;", "", "accessToken", "", "tokenType", "(Ljava/lang/String;Ljava/lang/String;)V", "getAccessToken", "()Ljava/lang/String;", "getTokenType", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class VerifyResponse {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String accessToken = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String tokenType = null;
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.shaadi.network.SupabaseAuthService.VerifyResponse copy(@com.squareup.moshi.Json(name = "access_token")
        @org.jetbrains.annotations.Nullable()
        java.lang.String accessToken, @com.squareup.moshi.Json(name = "token_type")
        @org.jetbrains.annotations.Nullable()
        java.lang.String tokenType) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
        
        public VerifyResponse(@com.squareup.moshi.Json(name = "access_token")
        @org.jetbrains.annotations.Nullable()
        java.lang.String accessToken, @com.squareup.moshi.Json(name = "token_type")
        @org.jetbrains.annotations.Nullable()
        java.lang.String tokenType) {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getAccessToken() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getTokenType() {
            return null;
        }
    }
}