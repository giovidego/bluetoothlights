package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

public class Registro extends AppCompatActivity {
    Button btn_reguser;
    EditText inp_regusuario, inp_regemail, inp_regpassword;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        inp_regusuario = findViewById(R.id.inp_regusuario);
        inp_regemail = findViewById(R.id.inp_regemail);
        inp_regpassword = findViewById(R.id.inp_regpassword);
        btn_reguser = findViewById(R.id.btn_reguser);

        btn_reguser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String name = inp_regusuario.getText().toString();
                String email = inp_regemail.getText().toString();
                String pass = inp_regpassword.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    inp_regusuario.setError("Ingrese su nombre");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    inp_regemail.setError("Ingrese su correo");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inp_regemail.setError("Ingrese un correo válido");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    inp_regpassword.setError("Ingrese su contraseña");
                    return;
                }
                if (pass.length() < 6) {
                    inp_regpassword.setError("La contraseña debe tener al menos 6 caracteres");
                    return;
                }


                DatabaseReference usuariosRef = mDatabase.child("usuarios");
                usuariosRef.orderByChild("nombre").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(Registro.this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            sendEmailVerification(user);
                                            saveUsername(user.getUid(), name);
                                        }
                                        Toast.makeText(Registro.this, "Usuario creado con éxito. Se ha enviado un correo de verificación.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(Registro.this, "Error al registrar. Inténtelo nuevamente.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Registro.this, "Error de conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Registro.this, "Por favor, verifique su dirección de correo electrónico y verifique su email.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Registro.this, "Error al enviar el correo de verificación.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUsername(String userId, String name) {
        DatabaseReference usuariosRef = mDatabase.child("usuarios");
        usuariosRef.child(userId).child("nombre").setValue(name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Registro.this, "Nombre de usuario guardado exitosamente", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registro.this, "Error al guardar el nombre de usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}