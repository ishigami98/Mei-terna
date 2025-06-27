package com.example.meica_linterna

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.meica_linterna.ui.theme.MeicaLinternaTheme

import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily

class MainActivity : ComponentActivity() {
    private lateinit var cameraManager: CameraManager
    private var cameraId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicita permiso de cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager

        // Obtiene el ID de la cámara con flash
        try {
            cameraId = cameraManager.cameraIdList.first {
                cameraManager.getCameraCharacteristics(it)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) {
            Toast.makeText(this, "No se encontró flash en el dispositivo", Toast.LENGTH_LONG).show()
        }

        setContent {
            MeicaLinternaTheme {
                FlashlightUI(
                    onToggleFlash = { isOn ->
                        try {
                            cameraManager.setTorchMode(cameraId, isOn)
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error al activar el flash", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FlashlightUI(onToggleFlash: (Boolean) -> Unit) {
    var isFlashOn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                isFlashOn = !isFlashOn
                onToggleFlash(isFlashOn)
            }
    ) {
        // Fondo adaptado a la pantalla
        Image(
            painter = painterResource(id = if (isFlashOn) R.drawable.fondo_on2 else R.drawable.fondo_off2 ),
            contentDescription = "Fondo linterna",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // o FillBounds si quieres estirar la imagen
        )

        // Título
        Text(
            text = " ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .align(Alignment.TopCenter),
            fontFamily = FontFamily.Monospace
        )

        // Botón
        Image(
            painter = painterResource(id = if (isFlashOn) R.drawable.foco_on else R.drawable.foco_off),
            contentDescription = "Botón linterna",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        )
    }
}
