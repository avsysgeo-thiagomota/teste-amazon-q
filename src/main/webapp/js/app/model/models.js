// models.js

Ext.ns('App.model');

// Modelo para os ingredientes (usado na janela de edição)
App.model.Ingrediente = Ext.data.Record.create([
    {name: 'nome', type: 'string'},
    {name: 'quantidade', type: 'float'},
    {name: 'unidade', type: 'string'}
]);

// Modelo para os passos (usado na janela de edição)
App.model.Passo = Ext.data.Record.create([
    {name: 'ordem', type: 'int'},
    {name: 'descricao', type: 'string'}
]);

// Modelo principal da Receita (usado pela Store e pela Grid)
App.model.Receita = Ext.data.Record.create([
    {name: 'id', type: 'int'},
    'nome',
    'descricao',
    {name: 'tempoDePreparo', type: 'int'},
    {name: 'porcoes', type: 'int'},
    'dificuldade',
    'categorias',   // Armazena o array de categorias
    'ingredientes', // Armazena o array de ingredientes
    'passos'        // Armazena o array de passos
]);