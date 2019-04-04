package com.example.ifood.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ifood.R;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticationActivity extends AppCompatActivity {

    private Button botaoAcesso;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentication);


        inicalizaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //To Log user out
        autenticacao.signOut();

        //Verifies if User is Logged in
        verificarUsuarioLogado();

        //Verifying if Switch is on so we display the other Linear Layout
        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(tipoAcesso.isChecked()){//empresa
                        linearTipoUsuario.setVisibility(View.VISIBLE);
                }else {//usuario
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        botaoAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //We will get the data from the user here

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                //We should test if they aren't empty

                if(!email.isEmpty() ){
                    if(!senha.isEmpty() ){

                        //If these 2 conditions are ok, we can proceed to check switch status

                        if( tipoAcesso.isChecked()){//User wants to register

                            autenticacao.createUserWithEmailAndPassword(
                              email,senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){ //If register is sucessfull we will point the user to another screen

                                        Toast.makeText(AutenticationActivity.this,
                                                "Cadastro realizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();

                                        String tipoUsuario = getTipoUsuario();
                                        UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);

                                            abrirTelaPrincipal(tipoUsuario);


                                    }else{ //If not we will check the exceptions

                                        String erroExcecao = "";

                                        try{
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e) {
                                            erroExcecao = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            erroExcecao = "Por favor, digite um e-mail válido";

                                        }catch (FirebaseAuthUserCollisionException e){
                                            erroExcecao = "Está conta já foi cadastrada";


                                        }catch (Exception e){
                                            erroExcecao = "ao cadastrar usuário: " +e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(AutenticationActivity.this,
                                                "Erro: " + erroExcecao,
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });




                        }else{//User is pointing to login

                            autenticacao.signInWithEmailAndPassword(
                                    email,senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    //Test if the user sucessfully logged in
                                    if (task.isSuccessful()){

                                        Toast.makeText(AutenticationActivity.this,
                                                "Logado com sucesso!",Toast.LENGTH_SHORT).show();

                                        //Recovers data from User

                                        String tipoUsuario = task.getResult().getUser().getDisplayName();

                                        abrirTelaPrincipal(tipoUsuario);



                                    }else{
                                        Toast.makeText(AutenticationActivity.this,
                                                "Erro ao fazer login : " +task.getException(),
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });



                        }


                    }else {
                        Toast.makeText(AutenticationActivity.this,
                                "Preencha a Password",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AutenticationActivity.this,
                            "Preencha o E-mail!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void verificarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if( usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    //Method that will return the type of the user
    private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "E" : "U";

    }

    private void abrirTelaPrincipal(String tipoUsuario) {
        if (tipoUsuario.equals("E")) {//Empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));

        } else {//Usuario
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

        }

    }

    //1st we Initialize all the Componentes

    private void inicalizaComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editcadastroSenha);
        botaoAcesso = findViewById(R.id.buttonAcess);
        tipoAcesso = findViewById(R.id.switchAcess);

        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);

    }
}
