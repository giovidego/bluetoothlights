package com.example.myapplication;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;



public class Home extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT
    };
    private static final String DEVICE_NAME = "HC-06"; // Nombre del dispositivo HC-06
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID para la comunicación Bluetooth serial

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    Button btnabrir;
    Button btncerrar;
    Button btn_cerrarsesion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnabrir = findViewById(R.id.btn_abrir);
        btncerrar = findViewById(R.id.btn_cerrar);
        btn_cerrarsesion=findViewById(R.id.btn_cerrarsesion);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (bluetoothAdapter == null) {
            Toast.makeText(this, "El dispositivo no admite Bluetooth", Toast.LENGTH_SHORT).show();

        }
        // Comprobar si los permisos ya están concedidos
        if (!checkPermissions()) {
            // Los permisos no están concedidos, solicitarlos
            requestPermissions();
        }else { }

        btnabrir.setOnClickListener(view -> abrir());
        btncerrar.setOnClickListener(view -> cerrar());

        btn_cerrarsesion.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Home.this, "Sesion cerrada", Toast.LENGTH_SHORT).show();
            gologin();
        });
    }

    public void abrir() {
        enviarMensaje("1");
    }
    public void cerrar() {
        enviarMensaje("2");
    }

    private void gologin(){
        Intent i = new Intent(this, LoginPageActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private boolean checkPermissions() {
        // Verificar si se han concedido todos los permisos requeridos
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_BLUETOOTH_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (checkPermissions()) {
                // Los permisos fueron concedidos, continuar con la lógica de la aplicación
                // ...
            } else {
                // Los permisos fueron rechazados, mostrar un mensaje o tomar alguna acción adecuada
                Toast.makeText(this, "Los permisos de Bluetooth fueron rechazados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "El dispositivo no admite Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        conectarBluetooth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        desconectarBluetooth();
    }


    @SuppressLint("MissingPermission")
    private void conectarBluetooth() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(DEVICE_NAME)) {
                    bluetoothDevice = device;
                    break;
                }
            }
        }

        if (bluetoothDevice == null) {
            Toast.makeText(this, "No se encontró el dispositivo Bluetooth HC-06", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            Toast.makeText(this, "Conexión Bluetooth establecida", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error al conectar con el dispositivo Bluetooth HC-06", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void desconectarBluetooth() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enviarMensaje(String mensaje) {
        if(outputStream != null){
            try {
                outputStream.write(mensaje.getBytes());
                Toast.makeText(this, "Mensaje enviado: " + mensaje, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Error: no hay dispositivos conectados no está inicializado", Toast.LENGTH_SHORT).show();
        }
    }
}