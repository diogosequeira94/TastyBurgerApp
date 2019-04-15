package com.example.ifood.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ifood.R;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.model.Empresa;
import com.example.ifood.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioMorada;
    private String idUsuario;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        //Initial Configs
        inicializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Configs for Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações Usuário");
        setSupportActionBar(toolbar);

        //Back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recovers data from User
        recuperarDadosUsuario();
    }

    private void recuperarDadosUsuario(){
        DatabaseReference usuarioRef = firebaseRef
                            .child("usuarios")
                            .child(idUsuario);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioMorada.setText(usuario.getEndereco());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes(){

        editUsuarioMorada = findViewById(R.id.editUsuarioMorada);
        editUsuarioNome = findViewById(R.id.editUsuarioNome);

    }

    public void validarDadosUsuario(View view){

        //Retrieves Data Typed into Fields

        String nome = editUsuarioNome.getText().toString();
        String morada = editUsuarioMorada.getText().toString();

        if (!nome.isEmpty()){
            if (!morada.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUsuario);
                usuario.setNome(nome);
                usuario.setEndereco( morada);
                usuario.salvar();

                exibirMensagem("Data saved with sucess");
                finish();

            }else{
                exibirMensagem("Insert Address");
            }

        }else{
            exibirMensagem("Insert Name");
        }


    }

    private void exibirMensagem (String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }


}
