package com.example.shaadi.network

import android.util.Log
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseRealtimeManager(
    private val supabaseUrl: String,
    private val supabaseAnonKey: String
) {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    data class Handle(val webSocket: WebSocket)

    private fun optStringOrNull(obj: JSONObject, key: String): String? {
        val v = obj.optString(key, "")
        return if (v.isNullOrBlank()) null else v
    }

    /**
     * Subscribes to Postgres INSERT changes for `public.messages` filtered by conversation_id.
     * Invokes [onMessage] when a new row arrives.
     */
    fun subscribeToMessages(
        userJwt: String,
        conversationId: String,
        onMessage: (MessageDto) -> Unit,
        onError: (Throwable) -> Unit = { }
    ): Handle {
        // Use the correct Supabase Realtime websocket endpoint and include apikey + vsn as query params
        val httpUrl = (supabaseUrl.trimEnd('/') + "/realtime/v1/websocket").toHttpUrl().newBuilder()
            .addQueryParameter("apikey", supabaseAnonKey)
            .addQueryParameter("vsn", "1.0.0")
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .header("Authorization", "Bearer $userJwt")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("SupabaseRealtime", "WebSocket opened: url=${httpUrl}")
                val joinPayload = JSONObject().apply {
                    put("topic", "realtime:public:messages")
                    put("event", "phx_join")
                    put("ref", "1")
                    put("payload", JSONObject().apply {
                        // pass jwt for RLS
                        put("user_token", userJwt)
                        put("config", JSONObject().apply {
                            put("postgres_changes", org.json.JSONArray().apply {
                                put(
                                    JSONObject().apply {
                                        put("event", "INSERT")
                                        put("schema", "public")
                                        put("table", "messages")
                                        // No server-side filter; we filter by conversationId client-side
                                    }
                                )
                            })
                        })
                    })
                }
                val sent = webSocket.send(joinPayload.toString())
                Log.d("SupabaseRealtime", "Sent phx_join (ok=$sent) topic=realtime:public:messages filter=conversation_id=eq.$conversationId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val root = JSONObject(text)
                    val event = root.optString("event")
                    if (event == "phx_reply") {
                        val status = root.optJSONObject("payload")?.optString("status")
                        Log.d("SupabaseRealtime", "phx_reply status=${status} text=${text}")
                        return
                    }
                    // Accept both supabase 'postgres_changes' wrapper and direct INSERT event
                    if (event != "postgres_changes" && event != "INSERT" && event != "UPDATE") return
                    val payload = root.optJSONObject("payload") ?: return
                    // Supabase Realtime payloads vary:
                    // - event=INSERT: payload.record
                    // - event=UPDATE: payload.new
                    // - event=postgres_changes: payload.data.{record|new}
                    val row = payload.optJSONObject("record")
                        ?: payload.optJSONObject("new")
                        ?: payload.optJSONObject("data")?.optJSONObject("record")
                        ?: payload.optJSONObject("data")?.optJSONObject("new")
                        ?: run {
                            Log.w("SupabaseRealtime", "Unexpected payload shape: $text")
                            null
                        }
                    if (row == null) return
                    val rowConversationId = optStringOrNull(row, "conversation_id") ?: ""
                    val rowSenderId = optStringOrNull(row, "sender_id") ?: ""
                    val rowReceiverId = optStringOrNull(row, "receiver_id") ?: ""
                    val rowContent = row.optString("content", "")
                    Log.d(
                        "SupabaseRealtime",
                        "Received event=${event} row with keys=${row.names()} convo=${rowConversationId} sender=${rowSenderId} receiver=${rowReceiverId} content=\"${rowContent}\""
                    )

                    val msg = MessageDto(
                        id = optStringOrNull(row, "id"),
                        conversationId = optStringOrNull(row, "conversation_id") ?: "",
                        senderId = optStringOrNull(row, "sender_id") ?: "",
                        receiverId = optStringOrNull(row, "receiver_id") ?: "",
                        content = row.optString("content", ""),
                        createdAt = optStringOrNull(row, "created_at")
                    )
                    if (msg.conversationId == conversationId) {
                        Log.d("SupabaseRealtime", "Dispatching message id=${msg.id ?: ""} to UI for convo=${conversationId}")
                        onMessage(msg)
                    } else {
                        Log.d(
                            "SupabaseRealtime",
                            "Ignored event for convo=${msg.conversationId} (subscribed=${conversationId})"
                        )
                    }
                } catch (e: Exception) {
                    Log.w("SupabaseRealtime", "Parse error: ${e.message}\ntext=$text")
                    onError(e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onError(t)
            }
        }

        val ws = client.newWebSocket(request, listener)
        return Handle(ws)
    }
}
