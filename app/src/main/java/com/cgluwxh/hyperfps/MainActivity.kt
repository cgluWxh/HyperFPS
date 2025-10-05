package com.cgluwxh.hyperfps

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cgluwxh.hyperfps.ui.theme.HyperFPSTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HyperFPSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Module 1 states
    var directRefreshRate by remember { mutableStateOf("") }
    var isApplyingDirect by remember { mutableStateOf(false) }
    var currentRefreshRate by remember { mutableStateOf<Int?>(null) }
    
    // Module 2 states
    var tileOffRefreshRate by remember { mutableStateOf("") }
    var tileOnRefreshRate by remember { mutableStateOf("") }
    var isApplyingTile by remember { mutableStateOf(false) }
    
    // Load saved tile settings and current refresh rate
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val offRate = PreferenceManager.getTileOffRefreshRate(context)
            val onRate = PreferenceManager.getTileOnRefreshRate(context)
            val current = RefreshRateManager.getCurrentRefreshRate(context)
            tileOffRefreshRate = offRate.toString()
            tileOnRefreshRate = onRate.toString()
            currentRefreshRate = current
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = "HyperFPS",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // Current refresh rate display
        currentRefreshRate?.let { rate ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "当前刷新率",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$rate Hz",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Module 1: Direct Refresh Rate Setting
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "立即设置刷新率",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "输入目标刷新率并立即应用",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = directRefreshRate,
                    onValueChange = { directRefreshRate = it },
                    label = { Text("刷新率 (Hz)") },
                    placeholder = { Text("例如: 60, 120, 144, 165") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Button(
                    onClick = {
                        val rate = directRefreshRate.toIntOrNull()
                        if (rate != null && rate > 0) {
                            isApplyingDirect = true
                            scope.launch(Dispatchers.IO) {
                                val success = RefreshRateManager.setRefreshRate(context, rate)
                                withContext(Dispatchers.Main) {
                                    isApplyingDirect = false
                                    if (success) {
                                        currentRefreshRate = rate
                                        Toast.makeText(
                                            context,
                                            "已设置刷新率为 $rate Hz",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "设置失败，请检查权限",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "请输入有效的刷新率", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isApplyingDirect
                ) {
                    if (isApplyingDirect) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isApplyingDirect) "应用中..." else "应用")
                }
            }
        }
        
        // Module 2: Quick Tile Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "快速切换磁贴设置",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "设置快速设置磁贴的开关刷新率",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = tileOffRefreshRate,
                    onValueChange = { tileOffRefreshRate = it },
                    label = { Text("OFF 状态刷新率 (Hz)") },
                    placeholder = { Text("例如: 60") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = tileOnRefreshRate,
                    onValueChange = { tileOnRefreshRate = it },
                    label = { Text("ON 状态刷新率 (Hz)") },
                    placeholder = { Text("例如: 120") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Button(
                    onClick = {
                        val offRate = tileOffRefreshRate.toIntOrNull()
                        val onRate = tileOnRefreshRate.toIntOrNull()
                        
                        if (offRate != null && offRate > 0 && onRate != null && onRate > 0) {
                            isApplyingTile = true
                            scope.launch(Dispatchers.IO) {
                                PreferenceManager.saveTileOffRefreshRate(context, offRate)
                                PreferenceManager.saveTileOnRefreshRate(context, onRate)
                                withContext(Dispatchers.Main) {
                                    isApplyingTile = false
                                    Toast.makeText(
                                        context,
                                        "快速切换设置已保存\nOFF: $offRate Hz, ON: $onRate Hz",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "请输入有效的刷新率", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isApplyingTile
                ) {
                    if (isApplyingTile) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isApplyingTile) "保存中..." else "保存设置")
                }
                
//                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text(
                    text = "提示：保存设置后，从快速设置面板添加 HyperFPS 磁贴即可使用",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
        
        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚠️ 使用说明",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "本软件需要您通过 ADB 授予 WRITE_SECURE_SETTINGS 权限，命令: \nadb shell pm grant com.cgluwxh.hyperfps android.permission.WRITE_SECURE_SETTINGS\n" +
                            "\nBy cgluWxh with ❤️",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    lineHeight = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
