package com.example.shaadi.data.profile;

import android.content.Context;
import com.example.shaadi.data.model.Profile;
import com.example.shaadi.network.ProfileDto;
import com.example.shaadi.network.ProfilesService;
import com.example.shaadi.network.SupabaseRestApiClient;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J.\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\nH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000b\u0010\fJ\f\u0010\r\u001a\u00020\b*\u00020\u000eH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0010"}, d2 = {"Lcom/example/shaadi/data/profile/ProfilesRepository;", "", "()V", "service", "Lcom/example/shaadi/network/ProfilesService;", "fetchProfiles", "Lkotlin/Result;", "", "Lcom/example/shaadi/data/model/Profile;", "limit", "", "fetchProfiles-gIAlu-s", "(Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toDomain", "Lcom/example/shaadi/network/ProfileDto;", "Companion", "app_debug"})
public final class ProfilesRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.shaadi.network.ProfilesService service = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.example.shaadi.data.profile.ProfilesRepository instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.shaadi.data.profile.ProfilesRepository.Companion Companion = null;
    
    private ProfilesRepository() {
        super();
    }
    
    private final com.example.shaadi.data.model.Profile toDomain(com.example.shaadi.network.ProfileDto $this$toDomain) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/shaadi/data/profile/ProfilesRepository$Companion;", "", "()V", "instance", "Lcom/example/shaadi/data/profile/ProfilesRepository;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.shaadi.data.profile.ProfilesRepository getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}