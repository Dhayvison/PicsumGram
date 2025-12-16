package com.example.picsumgram;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ErrorActivity extends AppCompatActivity {

    // Constante para a chave do Intent Extra
    public static final String EXTRA_ERROR_MESSAGE = "extra_error_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error); // Certifique-se de que este layout existe

        TextView errorMessageTextView = findViewById(R.id.errorMessage);
        Button homeButton = findViewById(R.id.homeButton);

        // 1. Receber e exibir a mensagem de erro
        String errorMessage = getIntent().getStringExtra(EXTRA_ERROR_MESSAGE);
        if (errorMessage != null) {
            errorMessageTextView.setText(errorMessage);
        } else {
            // Mensagem padrão se nenhuma for fornecida
            errorMessageTextView.setText(getString(R.string.error_message_default));
        }

        // 2. Configurar o botão "Voltar ao Início"
        homeButton.setOnClickListener(v -> {
            // Cria um Intent para retornar à MainActivity
            Intent intent = new Intent(this, MainActivity.class);

            // Flags para limpar a pilha e trazer a MainActivity para o topo
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish(); // Fecha a ErrorActivity
        });
    }
}