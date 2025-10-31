package com.misere.tictactoe.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misere.tictactoe.viewmodel.P2PViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P2PSetupScreen(
    onNavigateBack: () -> Unit,
    vm: P2PViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val status by vm.status.collectAsState()
    val connected by vm.connected.collectAsState()

    // Get Bluetooth manager + adapter
    val btManager = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val btAdapter: BluetoothAdapter? = btManager.adapter

    // Runtime permissions (Android 12+)
    var hasConnect by remember { mutableStateOf(false) }
    var hasScan by remember { mutableStateOf(false) }
    var hasAdvertise by remember { mutableStateOf(false) }

    val connectLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasConnect = granted }

    val scanLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasScan = granted }

    val advertiseLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasAdvertise = granted }

    // Check + request permissions
    LaunchedEffect(Unit) {
        hasConnect = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        hasScan = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        hasAdvertise = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.BLUETOOTH_ADVERTISE
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasConnect) connectLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        if (!hasScan) scanLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
        if (!hasAdvertise) advertiseLauncher.launch(Manifest.permission.BLUETOOTH_ADVERTISE)
    }

    val canUseBt = btAdapter != null && hasConnect && hasScan && hasAdvertise

    // Remember selected device for the Join flow
    var selectedDevice by remember { mutableStateOf<String?>(null) }

    // Get paired device names dynamically
    val pairedDevices = remember(btAdapter, hasConnect) {
        if (btAdapter != null && hasConnect) {
            btAdapter.bondedDevices?.map { it.name }?.sorted().orEmpty()
        } else emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bluetooth P2P Setup") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .padding(paddingVals)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Status: $status")

            if (!canUseBt) {
                Text(
                    "Enable Bluetooth and accept permission popups on BOTH phones.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                // HOST button
                Button(onClick = { vm.startBluetoothHost(btAdapter!!) }) {
                    Text("Host (Phone A)")
                }

                // JOIN dropdown
                var expanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        enabled = pairedDevices.isNotEmpty()
                    ) {
                        Text(selectedDevice ?: "Join (Select Paired Host)")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        pairedDevices.forEach { deviceName ->
                            DropdownMenuItem(
                                text = { Text(deviceName) },
                                onClick = {
                                    selectedDevice = deviceName
                                    expanded = false
                                    btAdapter?.let { vm.connectBluetooth(it, deviceName) }
                                }
                            )
                        }
                    }
                }

                if (pairedDevices.isEmpty()) {
                    Text(
                        "No paired devices found. Pair the phones in Android Bluetooth settings first!",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (connected) {
                    Text("Connected! You can now start playing.")
                    Button(onClick = onNavigateBack) {
                        Text("Continue to Game")
                    }
                }

                OutlinedButton(onClick = { vm.stop() }) {
                    Text("Disconnect")
                }
            }
        }
    }
}
