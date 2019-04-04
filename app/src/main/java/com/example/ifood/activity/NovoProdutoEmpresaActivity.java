package com.example.ifood.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ifood.R;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.model.Empresa;
import com.example.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private FirebaseAuth autenticacao;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Configs for Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);

        //Back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Initial Configs
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        inicializarComponentes();



    }

    public void validarDadosProduto(View view){

        //Verify if all fields were completed and get the inputs

        String nome = editProdutoNome.getText().toString();
        String descicao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();


        //validate

        if (!nome.isEmpty()){
            if (!descicao.isEmpty()){
                if (!preco.isEmpty()){

                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descicao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.salvar();

                    finish();
                    exibirMensagem("Product Saved");


                    }else{
                        exibirMensagem("Insert Price");
                    }
                }else{
                    exibirMensagem("Insert Product Description");
                }
            }else{
                exibirMensagem("Insert Product Name");
            }
        }


    private void exibirMensagem (String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes(){

        editProdutoDescricao= findViewById(R.id.editProdutoDescricao);
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);




    }
}
