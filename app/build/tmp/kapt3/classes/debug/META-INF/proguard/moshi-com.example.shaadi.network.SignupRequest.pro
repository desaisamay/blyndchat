-if class com.example.shaadi.network.SignupRequest
-keepnames class com.example.shaadi.network.SignupRequest
-if class com.example.shaadi.network.SignupRequest
-keep class com.example.shaadi.network.SignupRequestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.shaadi.network.SignupRequest
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.shaadi.network.SignupRequest
-keepclassmembers class com.example.shaadi.network.SignupRequest {
    public synthetic <init>(java.lang.String,java.lang.String,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
