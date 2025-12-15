-if class com.example.shaadi.network.LoginRequest
-keepnames class com.example.shaadi.network.LoginRequest
-if class com.example.shaadi.network.LoginRequest
-keep class com.example.shaadi.network.LoginRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
