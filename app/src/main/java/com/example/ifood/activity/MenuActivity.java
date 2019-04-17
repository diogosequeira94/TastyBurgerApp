package com.example.ifood.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.DialogPreference;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ifood.R;
import com.example.ifood.adapter.AdapterProduto;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.listener.RecyclerItemClickListener;
import com.example.ifood.model.Empresa;
import com.example.ifood.model.ItemPedido;
import com.example.ifood.model.Pedido;
import com.example.ifood.model.Produto;
import com.example.ifood.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;
    private Empresa empresaSelecionada;
    private AlertDialog dialog;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private String idUsuarioLogado;
    private Usuario usuario;
    private Pedido pedidoRecuperado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Lets Initialize Components again
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Recovers selected company
        Bundle bundle = getIntent().getExtras();
        if( bundle != null){
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");

            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());

            idEmpresa = empresaSelecionada.getIdusuario(); //its Id Company

            String url = empresaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCardapio);

        }

        //Configs for Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //RecyclerView Config to Display Items
        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter( adapterProduto );

        //OnClick
        recyclerProdutosCardapio.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerProdutosCardapio,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                confirmarQuantidade(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        //Method to recover the products
        recuperarProdutos();
        recuperarDadosUsuario();


    }

    private void confirmarQuantidade(final int posicao){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantity");
        builder.setMessage("Insert Quantity");

        //Creating EditText inside

        final EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView( editQuantidade );



        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String quantidade = editQuantidade.getText().toString();

                //Recovering Selected Product

                Produto produtoSelecionado = produtos.get(posicao);

                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getPreco());
                itemPedido.setQuantidade( Integer.parseInt(quantidade) );


                itensCarrinho.add(itemPedido);

                //Saving
                if (pedidoRecuperado == null){
                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                }

                pedidoRecuperado.setNome( usuario.getNome() );
                pedidoRecuperado.setEndereco( usuario.getEndereco() );
                pedidoRecuperado.setItens( itensCarrinho );
                pedidoRecuperado.salvar();




            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void recuperarDadosUsuario(){


        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Loading")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child( idUsuarioLogado );

        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    usuario = dataSnapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarPedido() {

        dialog.dismiss();

    }

    private void recuperarProdutos(){

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idEmpresa);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add( ds.getValue(Produto.class));
                }

                adapterProduto.notifyDataSetChanged();
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
        inflater.inflate(R.menu.menu_cardapio, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuPedido :

              break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){

        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutoCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

    }
}
