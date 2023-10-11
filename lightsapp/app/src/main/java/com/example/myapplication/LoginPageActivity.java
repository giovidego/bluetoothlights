package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginPageActivity extends AppCompatActivity {
    Button btnlogin,btnregistro;
    EditText inpemail,inppassword;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        inpemail = findViewById(R.id.inpemail);
        inppassword = findViewById(R.id.inppassword);

        btnlogin = findViewById(R.id.btnlogin);
        btnregistro = findViewById(R.id.btnregistro);

        firebaseAuth = FirebaseAuth.getInstance();

        btnregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginPageActivity.this,Registro.class);
                startActivity(i);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String email = inpemail.getText().toString();
                String pass = inppassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    inpemail.setError("Ingrese su correo");
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    inpemail.setError("Ingrese un correo válido");
                }
                if(TextUtils.isEmpty(pass)){
                    inppassword.setError("Ingrese su contraseña");
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            home();
                        }else{
                            Toast.makeText(LoginPageActivity.this,"Error al iniciar sesion. Verifique sus credenciales", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
    private void home(){
        Intent i = new Intent(this,Home.class);
        i.putExtra("email",inpemail.getText().toString());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().signOut();
    }

}