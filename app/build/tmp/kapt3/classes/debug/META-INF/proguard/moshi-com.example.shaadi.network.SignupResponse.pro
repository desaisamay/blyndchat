-if class com.example.shaadi.network.SignupResponse
-keepnames class com.example.shaadi.network.SignupResponse
-if class com.example.shaadi.network.SignupResponse
-keep class com.example.shaadi.network.SignupResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.shaadi.network.SignupResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.shaadi.network.SignupResponse
-keepclassmembers class com.example.shaadi.network.SignupResponse {
    public synthetic <init>(boolean,java.lang.String,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
