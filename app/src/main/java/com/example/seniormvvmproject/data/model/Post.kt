package com.example.seniormvvmproject.data.model

data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

//// Post.java (em com.example.seniormvvmproject.data.model)
//
//package com.example.seniormvvmproject.data.model;
//
//// As bibliotecas Gson e Retrofit usarão reflexão para preencher esses campos.
//public class Post {
//
//    private int userId;
//    private int id;
//    private String title;
//    private String body;
//
//    // Construtor vazio necessário para que o Gson/Retrofit consiga criar o objeto
//    public Post() {
//    }
//
//    // Construtor completo (opcional, mas boa prática)
//    public Post(int userId, int id, String title, String body) {
//        this.userId = userId;
//        this.id = id;
//        this.title = title;
//        this.body = body;
//    }
//
//    // Getters essenciais para acessar os dados da sua Activity/ViewModel
//
//    public int getUserId() {
//        return userId;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getBody() {
//        return body;
//    }
//}