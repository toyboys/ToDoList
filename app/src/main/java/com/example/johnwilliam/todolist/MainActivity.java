package com.example.johnwilliam.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLData;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText textoTarefa;
    private Button btnAdicionar;
    private ListView listView;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            //Recuperar componentes
            textoTarefa = (EditText) findViewById(R.id.edtTarefa);
            btnAdicionar = (Button) findViewById(R.id.btnAdicionar);

            //Lista Tarefa
            listView = (ListView) findViewById(R.id.listView);


            //Banco de Dados Configuraçao
            bancoDados = openOrCreateDatabase("appBD", MODE_PRIVATE, null);

            //Criar Tabela Tarefa
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            //Ação Botao Adicionar
            btnAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa(textoDigitado);
                }
            });

            listView.setLongClickable(true);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removerTarefa(ids.get(position));
                    return true;
                }
            });

            //Listar Tarefas
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void salvarTarefa(String texto){
        try {
            if(texto.equals("")){
                Toast.makeText(MainActivity.this,"Digite uma tarefa",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this,"Tarefa Salva com Sucesso",Toast.LENGTH_SHORT).show();
                bancoDados.execSQL("INSERT INTO tarefas(tarefa) VALUES('" + texto + "')");
                recuperarTarefas();
                textoTarefa.setText("");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void recuperarTarefas(){
        try{
            //Recupera as Tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //Recupera os id das Colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //Criar Adaptador
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, android.R.id.text1,itens);

            listView.setAdapter(itensAdaptador);
            //Listar as Tarefa
            cursor.moveToFirst();
            while(cursor != null){

                Log.i("Resultado - ","ID Tarefa: "+ cursor.getString( indiceColunaId)+ " - Tarefa: " + cursor.getString( indiceColunaTarefa));
                itens.add(cursor.getString( indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString( indiceColunaId)));

                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removerTarefa(Integer id){
        try{
            bancoDados.execSQL("DELETE FROM tarefas WHERE id = " + id);
            recuperarTarefas();
            Toast.makeText(MainActivity.this,"Tarefa Removida com Sucesso",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"Erro ao remover tarefa",Toast.LENGTH_SHORT).show();
        }
    }
}
