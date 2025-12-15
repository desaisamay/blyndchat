package com.example.shaadi.data.auth;

import android.content.Context;
import com.example.shaadi.BuildConfig;
import com.example.shaadi.network.SupabaseApiClient;
import com.example.shaadi.network.SupabaseAuthService;
import com.example.shaadi.network.SupabaseSignupRequest;
import com.example.shaadi.network.SupabaseTokenRequest;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\t\b\u0007\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\n\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016J\b\u0010\t\u001a\u00020\nH\u0016J+\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\bH\u0016\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\b\u0010\u0012\u001a\u00020\rH\u0016J+\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\bH\u0016\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0011R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0016"}, d2 = {"Lcom/example/shaadi/data/auth/SupabaseAuthRepository;", "Lcom/example/shaadi/data/auth/AuthRepository;", "store", "Lcom/example/shaadi/data/auth/CredentialStore;", "service", "Lcom/example/shaadi/network/SupabaseAuthService;", "(Lcom/example/shaadi/data/auth/CredentialStore;Lcom/example/shaadi/network/SupabaseAuthService;)V", "currentUserEmail", "", "isRegistered", "", "login", "Lkotlin/Result;", "", "email", "password", "login-gIAlu-s", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;", "logout", "register", "register-gIAlu-s", "Companion", "app_debug"})
public final class SupabaseAuthRepository implements com.example.shaadi.data.auth.AuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.shaadi.data.auth.CredentialStore store = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.shaadi.network.SupabaseAuthService service = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.example.shaadi.data.auth.SupabaseAuthRepository INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.shaadi.data.auth.SupabaseAuthRepository.Companion Companion = null;
    
    private SupabaseAuthRepository(com.example.shaadi.data.auth.CredentialStore store, com.example.shaadi.network.SupabaseAuthService service) {
        super();
    }
    
    @java.lang.Override()
    public boolean isRegistered() {
        return false;
    }
    
    @java.lang.Override()
    public void logout() {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.String currentUserEmail() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/shaadi/data/auth/SupabaseAuthRepository$Companion;", "", "()V", "INSTANCE", "Lcom/example/shaadi/data/auth/SupabaseAuthRepository;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.shaadi.data.auth.SupabaseAuthRepository getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}