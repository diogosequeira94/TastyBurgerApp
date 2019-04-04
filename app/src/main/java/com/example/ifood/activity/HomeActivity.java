package com.example.ifood.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ifood.R;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Toolbar Configs
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("TastyBurger");
        setSupportActionBar(toolbar);

        inicializarComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //We have to create and inflate this menu

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        //Search Button Configs
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Creating all the method for onOptionsItemSelected here:

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
            finish();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(HomeActivity.this, ConfiguracoesUsuarioActivity.class));

    }

    private void inicializarComponentes(){

        searchView = findViewById(R.id.materialSearchView);

    }
}
