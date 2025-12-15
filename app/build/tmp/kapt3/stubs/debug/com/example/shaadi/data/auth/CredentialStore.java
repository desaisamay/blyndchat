package com.example.shaadi.data.auth;

import android.content.Context;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u0007\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\fJ\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fJ\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fJ\b\u0010\u0011\u001a\u0004\u0018\u00010\u000fJ\u0016\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u000fJ\u000e\u0010\u0015\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u000fR \u0010\u0005\u001a\u0014 \b*\t\u0018\u00010\u0006\u00a2\u0006\u0002\b\u00070\u0006\u00a2\u0006\u0002\b\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\t\u001a\u0014 \b*\t\u0018\u00010\n\u00a2\u0006\u0002\b\u00070\n\u00a2\u0006\u0002\b\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/example/shaadi/data/auth/CredentialStore;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "masterKey", "Landroidx/security/crypto/MasterKey;", "Lorg/jspecify/annotations/NonNull;", "kotlin.jvm.PlatformType", "prefs", "Landroid/content/SharedPreferences;", "clear", "", "clearToken", "getEmail", "", "getPasswordHash", "getToken", "saveCredentials", "email", "passwordHash", "saveToken", "token", "Companion", "app_debug"})
public final class CredentialStore {
    private final androidx.security.crypto.MasterKey masterKey = null;
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREF_NAME = "secure_credentials";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_EMAIL = "email";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PASSWORD_HASH = "password_hash";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_TOKEN = "auth_token";
    @org.jetbrains.annotations.NotNull()
    public static final com.example.shaadi.data.auth.CredentialStore.Companion Companion = null;
    
    public CredentialStore(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final void saveCredentials(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String passwordHash) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getEmail() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPasswordHash() {
        return null;
    }
    
    public final void saveToken(@org.jetbrains.annotations.NotNull()
    java.lang.String token) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getToken() {
        return null;
    }
    
    public final void clearToken() {
    }
    
    public final void clear() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/shaadi/data/auth/CredentialStore$Companion;", "", "()V", "KEY_EMAIL", "", "KEY_PASSWORD_HASH", "KEY_TOKEN", "PREF_NAME", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}