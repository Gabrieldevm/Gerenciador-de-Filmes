package com.example.gerenciadorfilmes;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editNome, editAno, editNota, editBuscaNome;
    private Spinner spinnerTipo, spinnerGenero, spinnerFiltroTipo;
    private CheckBox checkAssistido;
    private Button btnSalvar, btnLimpar;
    private RecyclerView recyclerFilmes;

    private FirebaseFirestore db;
    private CollectionReference filmesRef;
    private FilmeAdapter adapter;
    private List<Filme> listaFilmes;
    
    private String currentFilmeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        filmesRef = db.collection("filmes");

        initViews();
        setupSpinners();
        setupRecyclerView();
        loadFilmes();

        btnSalvar.setOnClickListener(v -> salvarFilme());
        btnLimpar.setOnClickListener(v -> limparCampos());

        // Extra 1: Busca por nome
        editBuscaNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarLista();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filtro por tipo
        spinnerFiltroTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarLista();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initViews() {
        editNome = findViewById(R.id.editNome);
        editAno = findViewById(R.id.editAno);
        editNota = findViewById(R.id.editNota);
        editBuscaNome = findViewById(R.id.editBuscaNome);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        spinnerFiltroTipo = findViewById(R.id.spinnerFiltroTipo);
        checkAssistido = findViewById(R.id.checkAssistido);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnLimpar = findViewById(R.id.btnLimpar);
        recyclerFilmes = findViewById(R.id.recyclerFilmes);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(this,
                R.array.array_tipos, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterGenero = ArrayAdapter.createFromResource(this,
                R.array.array_generos, android.R.layout.simple_spinner_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterGenero);

        ArrayAdapter<CharSequence> adapterFiltro = ArrayAdapter.createFromResource(this,
                R.array.array_tipos_filtro, android.R.layout.simple_spinner_item);
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroTipo.setAdapter(adapterFiltro);
    }

    private void setupRecyclerView() {
        listaFilmes = new ArrayList<>();
        adapter = new FilmeAdapter(listaFilmes, new FilmeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Filme filme) {
                carregarParaEdicao(filme);
            }

            @Override
            public void onItemLongClick(Filme filme) {
                // Extra 2: Diálogo de confirmação antes de excluir
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Excluir Registro")
                        .setMessage("Deseja realmente excluir '" + filme.getNome() + "'?")
                        .setPositiveButton("Sim", (dialog, which) -> excluirFilme(filme))
                        .setNegativeButton("Não", null)
                        .show();
            }
        });
        recyclerFilmes.setLayoutManager(new LinearLayoutManager(this));
        recyclerFilmes.setAdapter(adapter);
    }

    private void salvarFilme() {
        String nome = editNome.getText().toString().trim();
        String ano = editAno.getText().toString().trim();
        String nota = editNota.getText().toString().trim();
        boolean assistido = checkAssistido.isChecked();
        
        int tipoPos = spinnerTipo.getSelectedItemPosition();
        int generoPos = spinnerGenero.getSelectedItemPosition();

        // Validações
        if (nome.isEmpty()) {
            editNome.setError("O nome não pode estar vazio");
            return;
        }
        if (tipoPos == 0) {
            Toast.makeText(this, "Selecione um tipo válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (generoPos == 0) {
            Toast.makeText(this, "Selecione um gênero válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ano.isEmpty()) {
            editAno.setError("O ano não pode estar vazio");
            return;
        }
        if (nota.isEmpty()) {
            editNota.setError("A nota não pode estar vazia");
            return;
        }

        String tipo = spinnerTipo.getSelectedItem().toString();
        String genero = spinnerGenero.getSelectedItem().toString();

        Filme filme = new Filme(nome, tipo, genero, ano, nota, assistido);

        if (currentFilmeId == null) {
            // Criar novo
            filmesRef.add(filme).addOnSuccessListener(doc -> {
                Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                limparCampos();
            }).addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show());
        } else {
            // Atualizar
            filmesRef.document(currentFilmeId).set(filme).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                limparCampos();
            });
        }
    }

    private void carregarParaEdicao(Filme filme) {
        currentFilmeId = filme.getId();
        editNome.setText(filme.getNome());
        editAno.setText(filme.getAno());
        editNota.setText(filme.getNota());
        checkAssistido.setChecked(filme.isAssistido());

        setSpinnerSelection(spinnerTipo, filme.getTipo());
        setSpinnerSelection(spinnerGenero, filme.getGenero());
        
        btnSalvar.setText("Atualizar");
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void excluirFilme(Filme filme) {
        filmesRef.document(filme.getId()).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Excluído com sucesso", Toast.LENGTH_SHORT).show();
        });
    }

    private void limparCampos() {
        currentFilmeId = null;
        editNome.setText("");
        editAno.setText("");
        editNota.setText("");
        checkAssistido.setChecked(false);
        spinnerTipo.setSelection(0);
        spinnerGenero.setSelection(0);
        btnSalvar.setText("Salvar");
    }

    private void loadFilmes() {
        filmesRef.addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            
            listaFilmes.clear();
            for (DocumentSnapshot doc : value.getDocuments()) {
                Filme f = doc.toObject(Filme.class);
                if (f != null) {
                    f.setId(doc.getId());
                    listaFilmes.add(f);
                }
            }
            filtrarLista(); // Atualiza a recycler view com ou sem filtro
        });
    }

    private void filtrarLista() {
        String busca = editBuscaNome.getText().toString().toLowerCase().trim();
        String tipoFiltro = spinnerFiltroTipo.getSelectedItem().toString();
        
        List<Filme> listaFiltrada = new ArrayList<>();
        
        for (Filme f : listaFilmes) {
            boolean matchNome = f.getNome().toLowerCase().contains(busca);
            boolean matchTipo = tipoFiltro.equals("Todos") || f.getTipo().equals(tipoFiltro);
            
            if (matchNome && matchTipo) {
                listaFiltrada.add(f);
            }
        }
        
        adapter.setFilmesList(listaFiltrada);
    }
}