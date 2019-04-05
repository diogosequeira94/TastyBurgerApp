package com.example.ifood.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.ifood.R;
import com.example.ifood.adapter.AdapterEmpresa;
import com.example.ifood.adapter.AdapterProduto;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.listener.RecyclerItemClickListener;
import com.example.ifood.model.Empresa;
import com.example.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Toolbar Configs
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("TastyBurger");
        setSupportActionBar(toolbar);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //RecyclerView Configs

        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresa.setAdapter( adapterEmpresa );

        //Method to recover the companies
        recuperarEmpresas();

        //Configuration of SearchView
        searchView.setHint("Search Restaurants (Uppercase)");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });

        //Click Event
        recyclerEmpresa.addOnItemTouchListener(
                new RecyclerItemClickListener(this,
                        recyclerEmpresa, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Empresa empresaSelecionada = empresas.get(position);
                        Intent i = new Intent(HomeActivity.this, MenuActivity.class);

                        i.putExtra("empresa", empresaSelecionada);

                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));



    }

    private void pesquisarEmpresas(String pesquisa){

        DatabaseReference empresasRef = firebaseRef
                .child("empresas");
        Query query = empresasRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                empresas.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    empresas.add( ds.getValue(Empresa.class));
                }

                adapterEmpresa.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarEmpresas(){

        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                empresas.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    empresas.add( ds.getValue(Empresa.class));
                }

                adapterEmpresa.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);
        searchView = findViewById(R.id.materialSearchView);

    }
}
