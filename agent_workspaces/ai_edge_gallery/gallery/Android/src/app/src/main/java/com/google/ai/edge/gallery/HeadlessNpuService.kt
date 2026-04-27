package com.google.ai.edge.gallery

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HeadlessNpuService : Service() {
    private var llmInference: LlmInference? = null

    override fun onCreate() {
        super.onCreate()
        Log.i("HEAVYNET_DAEMON", "Service Created. Initializing MediaPipe LlmInference Engine...")
        
        // Hardcoded physical path to the 4B parameter weights discovered by Omni-Hunter
        val modelPath = "/sdcard/Android/data/com.google.aiedge.gallery/files/Gemma_4_E4B_it/9695417f248178c63a9f318c6e0c56cb917cb837/gemma-4-E4B-it.litertlm"
        
        if (!File(modelPath).exists()) {
            Log.e("HEAVYNET_DAEMON", "FATAL: Model weights not found at $modelPath")
            return
        }

        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(1024)
                .setTemperature(0.1f)
                .build()

            llmInference = LlmInference.createFromOptions(applicationContext, options)
            Log.i("HEAVYNET_DAEMON", "NPU Ignition Successful. Engine Ready.")
        } catch (e: Exception) {
            Log.e("HEAVYNET_DAEMON", "Failed to initialize LlmInference: ${e.message}")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prompt = intent?.getStringExtra("HEAVYNET_PROMPT") ?: return START_NOT_STICKY

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (llmInference == null) {
                    Log.e("HEAVYNET_OUTPUT", """{"status":"error", "message":"Engine not initialized."}""")
                    return@launch
                }
                
                // Execute hardware generation
                val output = llmInference?.generateResponse(prompt)
                
                // Escape quotes for clean JSON output to logcat
                val safeOutput = output?.replace("\"", "\\\"")?.replace("\n", "\\n")
                Log.i("HEAVYNET_OUTPUT", """{"status":"success", "response":"$safeOutput"}""")
                
            } catch (e: Exception) {
                Log.e("HEAVYNET_OUTPUT", """{"status":"error", "message":"${e.message}"}""")
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        llmInference?.close()
        Log.i("HEAVYNET_DAEMON", "Service Destroyed. Hardware memory released.")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
// Cloud APK trigger: Mon Apr 27 14:37:30 IST 2026
// Cache bypass trigger: Mon Apr 27 14:47:07 IST 2026
// Dynamic build trigger: Mon Apr 27 14:54:42 IST 2026
