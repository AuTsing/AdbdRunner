package com.at.adbdrunner

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import java.io.DataOutputStream

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runCatching {
            val rt = Runtime.getRuntime()
            val p = rt.exec("su")
            val cmds = listOf(
                "setprop service.adb.tcp.port 5555\n",
                "stop adbd\n",
                "start adbd\n",
                "exit\n",
            )
            DataOutputStream(p.outputStream).use { dos ->
                for (cmd in cmds) {
                    dos.writeBytes(cmd)
                    dos.flush()
                }
            }
            p.waitFor()
        }.fold(
            onSuccess = { it },
            onFailure = { it.message },
        ).also {
            Toast.makeText(this, "Exit with: $it", Toast.LENGTH_LONG).show()
            finishAndRemoveTask()
        }
    }
}
