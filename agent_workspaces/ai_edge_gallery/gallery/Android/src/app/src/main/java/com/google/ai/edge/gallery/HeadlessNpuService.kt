package com.google.ai.edge.gallery

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HeadlessNpuService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prompt = intent?.getStringExtra("HEAVYNET_PROMPT") ?: return START_NOT_STICKY
        val modelId = intent.getStringExtra("HEAVYNET_MODEL") ?: "Gemma-4-E4B-it"

        Log.i("HEAVYNET_DAEMON", "Igniting NPU for model: $modelId")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Placeholder: Bridge to actual LlmInference instance here
                // val output = llmInferenceEngine.generate(prompt)
                
                // Emitting the telemetry back to the OS log stream
                Log.i("HEAVYNET_OUTPUT", """{"status":"success", "response":"[NPU_INFERENCE_MOCKED_SUCCESS]"}""")
            } catch (e: Exception) {
                Log.e("HEAVYNET_OUTPUT", """{"status":"error", "message":"${e.message}"}""")
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
